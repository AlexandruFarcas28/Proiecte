#ifndef SALSA20_H
#define SALSA20_H

#include <stdint.h>
#include <stddef.h>

#define SALSA20_BLOCK_SIZE  64
#define SALSA20_KEY_SIZE    32  
#define SALSA20_NONCE_SIZE   8  

typedef struct {
    uint32_t state[16];  
} Salsa20Ctx;

void salsa20_init(Salsa20Ctx *ctx, const uint8_t key[SALSA20_KEY_SIZE],const uint8_t nonce[SALSA20_NONCE_SIZE]);
void salsa20_xor(Salsa20Ctx *ctx, const uint8_t *in, uint8_t *out, size_t len);
int salsa20_encrypt_file(const uint8_t key[SALSA20_KEY_SIZE], const char *in_path, const char *out_path);
int salsa20_decrypt_file(const uint8_t key[SALSA20_KEY_SIZE], const char *in_path, const char *out_path);

#endif 