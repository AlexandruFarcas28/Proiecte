#include "salsa20.h"
#include "utils.h"
#include <string.h>
#include <stdio.h>

#define ROTL32(v, n) (((v) << (n)) | ((v) >> (32 - (n))))

static void salsa20_quarterround(uint32_t *a, uint32_t *b, uint32_t *c, uint32_t *d) {
    *b ^= ROTL32(*a + *d,  7);
    *c ^= ROTL32(*b + *a,  9);
    *d ^= ROTL32(*c + *b, 13);
    *a ^= ROTL32(*d + *c, 18);
}

static void salsa20_block(const uint32_t in[16], uint32_t out[16]) {
    uint32_t x[16];
    memcpy(x, in, 64);
    for (int i = 0; i < 10; i++) {
        salsa20_quarterround(&x[ 0],&x[ 4],&x[ 8],&x[12]);
        salsa20_quarterround(&x[ 5],&x[ 9],&x[13],&x[ 1]);
        salsa20_quarterround(&x[10],&x[14],&x[ 2],&x[ 6]);
        salsa20_quarterround(&x[15],&x[ 3],&x[ 7],&x[11]);
        salsa20_quarterround(&x[ 0],&x[ 1],&x[ 2],&x[ 3]);
        salsa20_quarterround(&x[ 5],&x[ 6],&x[ 7],&x[ 4]);
        salsa20_quarterround(&x[10],&x[11],&x[ 8],&x[ 9]);
        salsa20_quarterround(&x[15],&x[12],&x[13],&x[14]);
    }
    for (int i = 0; i < 16; i++) out[i] = x[i] + in[i];
}

static uint32_t load32_le(const uint8_t *p) {
    return (uint32_t)p[0] | ((uint32_t)p[1]<<8) |
           ((uint32_t)p[2]<<16) | ((uint32_t)p[3]<<24);
}

static void store32_le(uint8_t *p, uint32_t v) {
    p[0]=v&0xFF; p[1]=(v>>8)&0xFF;
    p[2]=(v>>16)&0xFF; p[3]=(v>>24)&0xFF;
}

static const uint8_t SIGMA[] = "expand 32-byte k";

void salsa20_init(Salsa20Ctx *ctx,
                  const uint8_t key[SALSA20_KEY_SIZE],
                  const uint8_t nonce[SALSA20_NONCE_SIZE]) {
    uint32_t *s = ctx->state;
    s[ 0] = load32_le(SIGMA +  0);
    s[ 1] = load32_le(key   +  0);
    s[ 2] = load32_le(key   +  4);
    s[ 3] = load32_le(key   +  8);
    s[ 4] = load32_le(key   + 12);
    s[ 5] = load32_le(SIGMA +  4);
    s[ 6] = load32_le(nonce +  0);
    s[ 7] = load32_le(nonce +  4);
    s[ 8] = 0; 
    s[ 9] = 0;  
    s[10] = load32_le(SIGMA +  8);
    s[11] = load32_le(key   + 16);
    s[12] = load32_le(key   + 20);
    s[13] = load32_le(key   + 24);
    s[14] = load32_le(key   + 28);
    s[15] = load32_le(SIGMA + 12);
}

void salsa20_xor(Salsa20Ctx *ctx,
                 const uint8_t *in, uint8_t *out, size_t len) {
    uint32_t block[16];
    uint8_t  ks[64];

    while (len > 0) {
        salsa20_block(ctx->state, block);
        for (int i = 0; i < 16; i++) store32_le(ks + i*4, block[i]);

        ctx->state[8]++;
        if (ctx->state[8] == 0) ctx->state[9]++;

        size_t chunk = len < 64 ? len : 64;
        for (size_t i = 0; i < chunk; i++) out[i] = in[i] ^ ks[i];
        in  += chunk;
        out += chunk;
        len -= chunk;
    }
}

int salsa20_encrypt_file(const uint8_t key[SALSA20_KEY_SIZE],
                         const char *in_path, const char *out_path) {
    uint8_t nonce[SALSA20_NONCE_SIZE];
    rand_bytes(nonce, SALSA20_NONCE_SIZE);

    FILE *fin  = fopen(in_path,  "rb");
    FILE *fout = fopen(out_path, "wb");
    if (!fin || !fout) { perror("fopen"); return -1; }

    fwrite(nonce, 1, SALSA20_NONCE_SIZE, fout);

    Salsa20Ctx ctx;
    salsa20_init(&ctx, key, nonce);

    uint8_t buf_in[4096], buf_out[4096];
    size_t n;
    while ((n = fread(buf_in, 1, sizeof(buf_in), fin)) > 0) {
        salsa20_xor(&ctx, buf_in, buf_out, n);
        fwrite(buf_out, 1, n, fout);
    }
    fclose(fin); fclose(fout);
    return 0;
}

int salsa20_decrypt_file(const uint8_t key[SALSA20_KEY_SIZE],
                         const char *in_path, const char *out_path) {
    FILE *fin = fopen(in_path, "rb");
    if (!fin) { perror("fopen"); return -1; }

    uint8_t nonce[SALSA20_NONCE_SIZE];
    if (fread(nonce, 1, SALSA20_NONCE_SIZE, fin) != SALSA20_NONCE_SIZE) {
        fprintf(stderr, "Invalid ciphertext: missing nonce\n");
        fclose(fin); return -1;
    }

    FILE *fout = fopen(out_path, "wb");
    if (!fout) { perror("fopen"); fclose(fin); return -1; }

    Salsa20Ctx ctx;
    salsa20_init(&ctx, key, nonce);

    uint8_t buf_in[4096], buf_out[4096];
    size_t n;
    while ((n = fread(buf_in, 1, sizeof(buf_in), fin)) > 0) {
        salsa20_xor(&ctx, buf_in, buf_out, n);
        fwrite(buf_out, 1, n, fout);
    }
    fclose(fin); fclose(fout);
    return 0;
}