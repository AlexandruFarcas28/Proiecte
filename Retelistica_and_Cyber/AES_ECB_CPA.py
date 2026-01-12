#!/usr/bin/env python3
import re
import base64
import requests

BASE = "http://challenge.localhost"
BS = 16

s = requests.Session()

def reset_db():
    s.post(f"{BASE}/reset", data={})

def post_content(raw: bytes):
    s.post(f"{BASE}/", data={"content": raw.decode("latin1")})

def get_ciphertext() -> bytes:
    r = s.get(f"{BASE}/")
    m = re.search(r"<pre>([^<]+)</pre>", r.text)
    if not m:
        raise RuntimeError("ciphertext not found")
    return base64.b64decode(m.group(1))

def oracle(user_bytes: bytes) -> bytes:
    reset_db()
    post_content(user_bytes)
    return get_ciphertext()

def get_block(ct: bytes, idx: int) -> bytes:
    return ct[idx*BS:(idx+1)*BS]

def main():
    known = b""
    for _ in range(4096):
        pad_len = (BS - 1) - (len(known) % BS)
        prefix = b"A" * pad_len
        ct = oracle(prefix)
        block_idx = (len(prefix) + len(known)) // BS
        target = get_block(ct, block_idx)

        table = {}
        base = prefix + known
        for b in range(256):
            ct_guess = oracle(base + bytes([b]))
            table[get_block(ct_guess, block_idx)] = b

        if target not in table:
            break

        known += bytes([table[target]])
        print(known[1:] if known.startswith(b"|") else known)

        if known.endswith(b"}"):
            break

    if known.startswith(b"|"):
        print("FLAG:", known[1:])
    else:
        print("FLAG:", known)

if __name__ == "__main__":
    main()
