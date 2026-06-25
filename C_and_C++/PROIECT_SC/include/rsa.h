#ifndef RSA_H
#define RSA_H

#include "bignum.h"

#define RSA_KEY_BITS  1024
#define RSA_BYTE_SIZE 128   

typedef struct {
    BigNum n;
    BigNum e;
} RSAPublicKey;

typedef struct {
    BigNum n;
    BigNum e;
    BigNum d;
    BigNum p;
    BigNum q;
    BigNum dp;
    BigNum dq;
    BigNum qinv;
} RSAPrivateKey;

void rsa_gen_keypair(RSAPublicKey *pub, RSAPrivateKey *priv);

int rsa_encrypt(const RSAPublicKey *pub,
                const uint8_t *msg, size_t msg_len,
                uint8_t *out, size_t *out_len);

int rsa_decrypt(const RSAPrivateKey *priv,
                const uint8_t *in, size_t in_len,
                uint8_t *out, size_t *out_len);

int rsa_save_public(const RSAPublicKey *pub, const char *path);
int rsa_save_private(const RSAPrivateKey *priv, const char *path);
int rsa_load_public(RSAPublicKey *pub, const char *path);
int rsa_load_private(RSAPrivateKey *priv, const char *path);

int rsa_encrypt_file(const RSAPublicKey *pub,
                     const char *in_path, const char *out_path);

int rsa_decrypt_file(const RSAPrivateKey *priv,
                     const char *in_path, const char *out_path);

#endif