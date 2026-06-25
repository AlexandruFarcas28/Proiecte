#include "bignum.h"
#include "utils.h"
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

void bn_zero(BigNum *a) { memset(a->d, 0, sizeof(a->d)); a->top = 0; a->neg = 0; }
void bn_one(BigNum *a)  { bn_zero(a); a->d[0] = 1; a->top = 1; }

void bn_copy(BigNum *dst, const BigNum *src) {
    memcpy(dst->d, src->d, sizeof(src->d));
    dst->top = src->top; dst->neg = src->neg;
}

static void bn_fix_top(BigNum *a) {
    while (a->top > 0 && a->d[a->top-1] == 0) a->top--;
}

int bn_is_zero(const BigNum *a) { return a->top == 0; }
int bn_is_one(const BigNum *a)  { return a->top == 1 && a->d[0] == 1; }

int bn_get_bit(const BigNum *a, int n) {
    int w = n/32, b = n%32;
    if (w >= a->top) return 0;
    return (a->d[w] >> b) & 1;
}

int bn_bits(const BigNum *a) {
    if (!a->top) return 0;
    int bits = (a->top-1)*32;
    uint32_t v = a->d[a->top-1];
    while (v) { bits++; v >>= 1; }
    return bits;
}

int bn_cmp(const BigNum *a, const BigNum *b) {
    if (a->top != b->top) return a->top > b->top ? 1 : -1;
    for (int i = a->top-1; i >= 0; i--)
        if (a->d[i] != b->d[i]) return a->d[i] > b->d[i] ? 1 : -1;
    return 0;
}

void bn_add(BigNum *r, const BigNum *a, const BigNum *b) {
    BigNum ta, tb;
    bn_copy(&ta, a); bn_copy(&tb, b);
    uint64_t carry = 0;
    int max = ta.top > tb.top ? ta.top : tb.top;
    r->top = 0;
    for (int i = 0; i < max || carry; i++) {
        uint64_t s = carry;
        if (i < ta.top) s += ta.d[i];
        if (i < tb.top) s += tb.d[i];
        r->d[i] = (uint32_t)(s & 0xFFFFFFFF);
        carry = s >> 32;
        r->top = i+1;
    }
    r->neg = 0;
}

void bn_sub(BigNum *r, const BigNum *a, const BigNum *b) {
    BigNum ta, tb;
    bn_copy(&ta, a); bn_copy(&tb, b);
    int64_t borrow = 0;
    r->top = ta.top;
    for (int i = 0; i < ta.top; i++) {
        int64_t diff = (int64_t)ta.d[i] - (i < tb.top ? tb.d[i] : 0) - borrow;
        if (diff < 0) { diff += (int64_t)1<<32; borrow = 1; } else borrow = 0;
        r->d[i] = (uint32_t)diff;
    }
    r->neg = 0; bn_fix_top(r);
}

void bn_mul(BigNum *r, const BigNum *a, const BigNum *b) {
    BigNum ta, tb, tmp;
    bn_copy(&ta, a); bn_copy(&tb, b);
    bn_zero(&tmp);
    int at = ta.top, bt = tb.top;
    tmp.top = (at + bt < BN_MAX_WORDS) ? at + bt : BN_MAX_WORDS;
    for (int i = 0; i < at; i++) {
        uint64_t carry = 0;
        for (int j = 0; j < bt && i+j < BN_MAX_WORDS; j++) {
            uint64_t uv = (uint64_t)ta.d[i]*tb.d[j] + tmp.d[i+j] + carry;
            tmp.d[i+j] = (uint32_t)(uv & 0xFFFFFFFF);
            carry = uv >> 32;
        }
        if (i+bt < BN_MAX_WORDS) tmp.d[i+bt] += (uint32_t)carry;
    }
    bn_fix_top(&tmp); bn_copy(r, &tmp);
}

void bn_from_u32(BigNum *a, uint32_t v) {
    bn_zero(a); a->d[0] = v; a->top = v ? 1 : 0;
}

void bn_divmod(BigNum *q, BigNum *r, const BigNum *a, const BigNum *b) {
    BigNum ta, tb;
    bn_copy(&ta, a); bn_copy(&tb, b);
    bn_zero(q); bn_zero(r);
    if (bn_is_zero(&tb)) return;
    if (bn_cmp(&ta, &tb) < 0) { bn_copy(r, &ta); return; }

    int shift = bn_bits(&ta) - bn_bits(&tb);
    BigNum bs; bn_copy(&bs, &tb);

    int wshift = shift/32, bshift = shift%32;
    for (int i = BN_MAX_WORDS-1; i >= 0; i--) {
        uint32_t lo = (i-wshift >= 0) ? bs.d[i-wshift] : 0;
        uint32_t hi = (i-wshift-1 >= 0) ? bs.d[i-wshift-1] : 0;
        bs.d[i] = bshift ? (lo<<bshift)|(hi>>(32-bshift)) : lo;
    }
    bs.top = BN_MAX_WORDS; bn_fix_top(&bs);

    bn_copy(r, &ta);
    for (int i = shift; i >= 0; i--) {
        if (bn_cmp(r, &bs) >= 0) {
            bn_sub(r, r, &bs);
            q->d[i/32] |= (1u << (i%32));
            if (i/32+1 > q->top) q->top = i/32+1;
        }
        for (int j = 0; j < BN_MAX_WORDS-1; j++)
            bs.d[j] = (bs.d[j]>>1)|(bs.d[j+1]<<31);
        bs.d[BN_MAX_WORDS-1] >>= 1;
        bs.top = BN_MAX_WORDS; bn_fix_top(&bs);
    }
    bn_fix_top(q); bn_fix_top(r);
}

void bn_mod(BigNum *r, const BigNum *a, const BigNum *m) {
    BigNum q; bn_zero(&q); bn_divmod(&q, r, a, m);
}

void bn_modexp(BigNum *r, const BigNum *base, const BigNum *exp, const BigNum *mod) {
    if (bn_is_zero(mod)) { bn_zero(r); return; }
    BigNum result, b, tmp;
    bn_one(&result);
    bn_mod(&b, base, mod);
    int ebits = bn_bits(exp);
    for (int i = ebits-1; i >= 0; i--) {
        bn_mul(&tmp, &result, &result);
        bn_mod(&result, &tmp, mod);
        if (bn_get_bit(exp, i)) {
            bn_mul(&tmp, &result, &b);
            bn_mod(&result, &tmp, mod);
        }
    }
    bn_copy(r, &result);
}

void bn_gcd(BigNum *r, const BigNum *a, const BigNum *b) {
    BigNum x, y, tmp, q;
    bn_copy(&x, a); bn_copy(&y, b);
    while (!bn_is_zero(&y)) {
        bn_divmod(&q, &tmp, &x, &y);
        bn_copy(&x, &y); bn_copy(&y, &tmp);
    }
    bn_copy(r, &x);
}

void bn_modinv(BigNum *r, const BigNum *a, const BigNum *m) {
    BigNum rr, newr, t, newt, q, tmp, prod;

    bn_copy(&rr,   m);
    bn_copy(&newr, a);
    bn_zero(&t);
    bn_one(&newt);

    while (!bn_is_zero(&newr)) {
        bn_divmod(&q, &tmp, &rr, &newr);

        bn_copy(&rr, &newr);
        bn_copy(&newr, &tmp);

        bn_mul(&prod, &q, &newt);
        bn_mod(&prod, &prod, m);  

        bn_copy(&tmp, &t);

        bn_copy(&t, &newt);

        if (bn_cmp(&tmp, &prod) >= 0) {
            bn_sub(&newt, &tmp, &prod);
        } else {
            bn_sub(&newt, &prod, &tmp);
            bn_sub(&newt, m, &newt);
        }
    }
    bn_copy(r, &t);
}

void bn_from_bytes(BigNum *a, const uint8_t *buf, size_t len) {
    bn_zero(a);
    for (size_t i = 0; i < len; i++) {
        size_t bp = len-1-i;
        int w = (int)(bp/4), sh = (int)((bp%4)*8);
        if (w < BN_MAX_WORDS) a->d[w] |= (uint32_t)buf[i] << sh;
    }
    a->top = BN_MAX_WORDS; bn_fix_top(a);
    if (!a->top) a->top = 1;
}

void bn_to_bytes(const BigNum *a, uint8_t *buf, size_t len) {
    memset(buf, 0, len);
    for (size_t i = 0; i < len; i++) {
        size_t bp = len-1-i;
        int w = (int)(bp/4), sh = (int)((bp%4)*8);
        if (w < BN_MAX_WORDS) buf[i] = (a->d[w] >> sh) & 0xFF;
    }
}

void bn_rand_bits(BigNum *a, int bits) {
    bn_zero(a);
    int bytes = (bits+7)/8;
    uint8_t buf[BN_MAX_WORDS*4];
    rand_bytes(buf, bytes);
    buf[0] |= 0x80;
    buf[bytes-1] |= 0x01;
    bn_from_bytes(a, buf, bytes);
    bn_fix_top(a);
    if (!a->top) a->top = 1;
}

void bn_rand_range(BigNum *r, const BigNum *max) {
    do { bn_rand_bits(r, bn_bits(max)); } while (bn_cmp(r, max) >= 0);
}

static const uint32_t SMALL_PRIMES[] = {
    3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,
    73,79,83,89,97,101,103,107,109,113,127,131,137,139,149,151,
    157,163,167,173,179,181,191,193,197,199,211,223,227,229,233,
    239,241,251,257,263,269,271,277,281,283,293,307,311,313,317,
    331,337,347,349,353,359,367,373,379,383,389,397,401,409,419,
    421,431,433,439,443,449,457,461,463,467,479,487,491,499,503
};

int bn_is_prime_miller_rabin(const BigNum *n, int rounds) {
    if (bn_is_zero(n) || bn_is_one(n)) return 0;
    BigNum two; bn_from_u32(&two, 2);
    if (bn_cmp(n, &two) == 0) return 1;
    if (!(n->d[0] & 1)) return 0;

    for (size_t i = 0; i < sizeof(SMALL_PRIMES)/sizeof(SMALL_PRIMES[0]); i++) {
        BigNum sp, rem; bn_from_u32(&sp, SMALL_PRIMES[i]);
        if (bn_cmp(n, &sp) == 0) return 1;
        bn_mod(&rem, n, &sp);
        if (bn_is_zero(&rem)) return 0;
    }

    BigNum one, nminus1, d;
    bn_one(&one);
    bn_sub(&nminus1, n, &one);
    bn_copy(&d, &nminus1);
    int s = 0;
    while (!(d.d[0] & 1)) {
        for (int j = 0; j < BN_MAX_WORDS-1; j++)
            d.d[j] = (d.d[j]>>1)|(d.d[j+1]<<31);
        d.d[BN_MAX_WORDS-1] >>= 1;
        bn_fix_top(&d); s++;
    }

    static const uint32_t W[] = {2,3,5,7,11,13,17,19,23,29,31,37};
    int use = rounds < 12 ? rounds : 12;
    for (int i = 0; i < use; i++) {
        BigNum a, x; bn_from_u32(&a, W[i]);
        if (bn_cmp(&a, n) >= 0) continue;
        bn_modexp(&x, &a, &d, n);
        if (bn_cmp(&x, &one)==0 || bn_cmp(&x, &nminus1)==0) continue;
        int composite = 1;
        for (int rr = 0; rr < s-1; rr++) {
            BigNum x2; bn_mul(&x2, &x, &x); bn_mod(&x, &x2, n);
            if (bn_cmp(&x, &nminus1)==0) { composite=0; break; }
        }
        if (composite) return 0;
    }
    return 1;
}

void bn_gen_prime(BigNum *p, int bits) {
    do { bn_rand_bits(p, bits); }
    while (!bn_is_prime_miller_rabin(p, 12));
}