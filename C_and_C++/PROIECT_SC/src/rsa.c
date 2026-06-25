#include "rsa.h"
#include "blowfish.h"
#include "utils.h"
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

void rsa_gen_keypair(RSAPublicKey *pub, RSAPrivateKey *priv) {
    BigNum p, q, n, phi, e, d, p1, q1, dp, dq, qinv, tmp;

    printf("Generating RSA-%d keypair (this may take a while)...\n", RSA_KEY_BITS);
    bn_gen_prime(&p, RSA_KEY_BITS / 2);
    bn_gen_prime(&q, RSA_KEY_BITS / 2);

    bn_mul(&n, &p, &q);

    BigNum one;
    bn_one(&one);
    bn_sub(&p1, &p, &one);
    bn_sub(&q1, &q, &one);
    bn_mul(&phi, &p1, &q1);

    bn_from_u32(&e, 65537);

    bn_modinv(&d, &e, &phi);

    bn_mod(&dp,   &d, &p1);
    bn_mod(&dq,   &d, &q1);
    bn_modinv(&qinv, &q, &p);

    bn_copy(&pub->n, &n);
    bn_copy(&pub->e, &e);

    bn_copy(&priv->n,    &n);
    bn_copy(&priv->e,    &e);
    bn_copy(&priv->d,    &d);
    bn_copy(&priv->p,    &p);
    bn_copy(&priv->q,    &q);
    bn_copy(&priv->dp,   &dp);
    bn_copy(&priv->dq,   &dq);
    bn_copy(&priv->qinv, &qinv);
    (void)tmp;
    printf("Keypair generated.\n");
}


static int oaep_pad(const uint8_t *msg, size_t msg_len,
                    uint8_t *out, size_t out_len) {
    if (msg_len > out_len - 68) return -1;

    uint8_t seed[32];
    rand_bytes(seed, 32);

    out[0] = 0x00;
    memcpy(out + 1, seed, 32);

    uint8_t db_mask[32];
    sha256(seed, 32, db_mask);
    for (int i = 0; i < 32; i++) out[33 + i] = db_mask[i];

    out[65] = 0x01;

    out[66] = (uint8_t)((msg_len >> 8) & 0xFF);
    out[67] = (uint8_t)(msg_len & 0xFF);

    memcpy(out + 68, msg, msg_len);
    memset(out + 68 + msg_len, 0, out_len - 68 - msg_len);
    return 0;
}

static int oaep_unpad(const uint8_t *in, size_t in_len,
                      uint8_t *msg, size_t *msg_len) {
    if (in_len < 68) return -1;
    if (in[0] != 0x00) return -1;
    if (in[65] != 0x01) return -1;

    size_t len = ((size_t)in[66] << 8) | (size_t)in[67];

    if (68 + len > in_len) return -1;

    memcpy(msg, in + 68, len);
    *msg_len = len;
    return 0;
}

int rsa_encrypt(const RSAPublicKey *pub,
                const uint8_t *msg, size_t msg_len,
                uint8_t *out, size_t *out_len) {
    uint8_t padded[RSA_BYTE_SIZE];
    if (oaep_pad(msg, msg_len, padded, RSA_BYTE_SIZE) != 0) return -1;
    BigNum m, c;
    bn_from_bytes(&m, padded, RSA_BYTE_SIZE);
    bn_modexp(&c, &m, &pub->e, &pub->n);
    bn_to_bytes(&c, out, RSA_BYTE_SIZE);
    *out_len = RSA_BYTE_SIZE;
    return 0;
}

int rsa_decrypt(const RSAPrivateKey *priv,
                const uint8_t *in, size_t in_len,
                uint8_t *out, size_t *out_len) {
    if (in_len != RSA_BYTE_SIZE) return -1;
    BigNum c, m;
    bn_from_bytes(&c, in, RSA_BYTE_SIZE);
    bn_modexp(&m, &c, &priv->d, &priv->n);
    uint8_t padded[RSA_BYTE_SIZE];
    bn_to_bytes(&m, padded, RSA_BYTE_SIZE);
    return oaep_unpad(padded, RSA_BYTE_SIZE, out, out_len);
}

static void write_bn(FILE *f, const BigNum *a) {
    uint8_t buf[BN_MAX_WORDS * 4];
    bn_to_bytes(a, buf, sizeof(buf));
    fwrite(buf, 1, sizeof(buf), f);
}

static void read_bn(FILE *f, BigNum *a) {
    uint8_t buf[BN_MAX_WORDS * 4];
    fread(buf, 1, sizeof(buf), f);
    bn_from_bytes(a, buf, sizeof(buf));
}

int rsa_save_public(const RSAPublicKey *pub, const char *path) {
    FILE *f = fopen(path, "wb");
    if (!f) { perror("fopen"); return -1; }
    write_bn(f, &pub->n);
    write_bn(f, &pub->e);
    fclose(f);
    return 0;
}

int rsa_save_private(const RSAPrivateKey *priv, const char *path) {
    FILE *f = fopen(path, "wb");
    if (!f) { perror("fopen"); return -1; }
    write_bn(f, &priv->n);
    write_bn(f, &priv->e);
    write_bn(f, &priv->d);
    write_bn(f, &priv->p);
    write_bn(f, &priv->q);
    write_bn(f, &priv->dp);
    write_bn(f, &priv->dq);
    write_bn(f, &priv->qinv);
    fclose(f);
    return 0;
}

int rsa_load_public(RSAPublicKey *pub, const char *path) {
    FILE *f = fopen(path, "rb");
    if (!f) { perror("fopen"); return -1; }
    read_bn(f, &pub->n);
    read_bn(f, &pub->e);
    fclose(f);
    return 0;
}

int rsa_load_private(RSAPrivateKey *priv, const char *path) {
    FILE *f = fopen(path, "rb");
    if (!f) { perror("fopen"); return -1; }
    read_bn(f, &priv->n);
    read_bn(f, &priv->e);
    read_bn(f, &priv->d);
    read_bn(f, &priv->p);
    read_bn(f, &priv->q);
    read_bn(f, &priv->dp);
    read_bn(f, &priv->dq);
    read_bn(f, &priv->qinv);
    fclose(f);
    return 0;
}

int rsa_encrypt_file(const RSAPublicKey *pub,
                     const char *in_path, const char *out_path) {
    uint8_t session_key[32];
    rand_bytes(session_key, 32);

    uint8_t enc_key[RSA_BYTE_SIZE];
    size_t  enc_key_len;
    if (rsa_encrypt(pub, session_key, 32, enc_key, &enc_key_len) != 0) {
        fprintf(stderr, "RSA encrypt session key failed\n");
        return -1;
    }

    char tmp_body[4096];
    snprintf(tmp_body, sizeof(tmp_body), "%s.body_tmp", out_path);

    BlowfishCtx ctx;
    bf_init(&ctx, session_key, 32);
    if (bf_ctr_encrypt_file(&ctx, in_path, tmp_body) != 0) {
        fprintf(stderr, "Blowfish encrypt failed\n");
        return -1;
    }

    FILE *fout  = fopen(out_path, "wb");
    FILE *fbody = fopen(tmp_body, "rb");
    if (!fout || !fbody) { perror("fopen"); return -1; }

    fwrite(enc_key, 1, RSA_BYTE_SIZE, fout);

    uint8_t buf[4096];
    size_t n;
    while ((n = fread(buf, 1, sizeof(buf), fbody)) > 0)
        fwrite(buf, 1, n, fout);

    fclose(fout); fclose(fbody);
    remove(tmp_body);
    return 0;
}

int rsa_decrypt_file(const RSAPrivateKey *priv,
                     const char *in_path, const char *out_path) {
    FILE *fin = fopen(in_path, "rb");
    if (!fin) { perror("fopen"); return -1; }

    uint8_t enc_key[RSA_BYTE_SIZE];
    if (fread(enc_key, 1, RSA_BYTE_SIZE, fin) != RSA_BYTE_SIZE) {
        fprintf(stderr, "Invalid RSA ciphertext\n");
        fclose(fin); return -1;
    }

    uint8_t session_key[32];
    size_t  sk_len;
    if (rsa_decrypt(priv, enc_key, RSA_BYTE_SIZE,
                    session_key, &sk_len) != 0 || sk_len != 32) {
        fprintf(stderr, "RSA decrypt failed\n");
        fclose(fin); return -1;
    }

    char tmp_body[4096];
    snprintf(tmp_body, sizeof(tmp_body), "%s.body_tmp", out_path);

    FILE *ftmp = fopen(tmp_body, "wb");
    if (!ftmp) { perror("fopen"); fclose(fin); return -1; }
    uint8_t buf[4096];
    size_t n;
    while ((n = fread(buf, 1, sizeof(buf), fin)) > 0)
        fwrite(buf, 1, n, ftmp);
    fclose(fin); fclose(ftmp);

    BlowfishCtx ctx;
    bf_init(&ctx, session_key, 32);
    int ret = bf_ctr_decrypt_file(&ctx, tmp_body, out_path);
    remove(tmp_body);
    return ret;
}