#ifndef UTILS_H
#define UTILS_H

#include <stdint.h>
#include <stddef.h>

void rand_bytes(uint8_t *buf, size_t len);
void sha256(const uint8_t *msg, size_t len, uint8_t out[32]);
void xor_bytes(uint8_t *dst, const uint8_t *a, const uint8_t *b, size_t len);
int  read_key_file(const char *path, uint8_t *buf, size_t expected_len);
int  write_file(const char *path, const uint8_t *buf, size_t len);
void print_hex(const char *label, const uint8_t *buf, size_t len);
void die(const char *msg);

#endif