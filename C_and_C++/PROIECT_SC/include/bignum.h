#ifndef BIGNUM_H
#define BIGNUM_H

#include <stdint.h>
#include <stddef.h>

#define BN_MAX_WORDS 64 

typedef struct {
    uint32_t d[BN_MAX_WORDS];
    int      top;  
    int      neg;
} BigNum;

void bn_zero(BigNum *a);
void bn_one(BigNum *a);
void bn_copy(BigNum *dst, const BigNum *src);
int  bn_cmp(const BigNum *a, const BigNum *b);
int  bn_is_zero(const BigNum *a);
int  bn_is_one(const BigNum *a);
int  bn_get_bit(const BigNum *a, int n);
void bn_add(BigNum *r, const BigNum *a, const BigNum *b);
void bn_sub(BigNum *r, const BigNum *a, const BigNum *b);
void bn_mul(BigNum *r, const BigNum *a, const BigNum *b);
void bn_mod(BigNum *r, const BigNum *a, const BigNum *m);
void bn_divmod(BigNum *q, BigNum *r, const BigNum *a, const BigNum *b);
void bn_modexp(BigNum *r, const BigNum *base, const BigNum *exp, const BigNum *mod);
void bn_modinv(BigNum *r, const BigNum *a, const BigNum *m);
void bn_gcd(BigNum *r, const BigNum *a, const BigNum *b);
void bn_from_bytes(BigNum *a, const uint8_t *buf, size_t len);
void bn_to_bytes(const BigNum *a, uint8_t *buf, size_t len);
void bn_from_u32(BigNum *a, uint32_t v);
void bn_rand_bits(BigNum *a, int bits);
void bn_rand_range(BigNum *r, const BigNum *max);
int  bn_is_prime_miller_rabin(const BigNum *n, int rounds);
void bn_gen_prime(BigNum *p, int bits);

#endif