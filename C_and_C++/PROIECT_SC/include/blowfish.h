#ifndef BLOWFISH_H
#define BLOWFISH_H

#include <stdint.h>
#include <stddef.h>

#define BF_ROUNDS       16
#define BF_BLOCK_SIZE   8

typedef struct {
    uint32_t P[BF_ROUNDS + 2];
    uint32_t S[4][256];
} BlowfishCtx;

void bf_init(BlowfishCtx *ctx, const uint8_t *key, size_t key_len);
void bf_encrypt_block(const BlowfishCtx *ctx, uint32_t *xl, uint32_t *xr);
int bf_ctr_encrypt_file(const BlowfishCtx *ctx, const char *in_path, const char *out_path);
int bf_ctr_decrypt_file(const BlowfishCtx *ctx, const char *in_path, const char *out_path);

#endif 