#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "blowfish.h"
#include "salsa20.h"
#include "rsa.h"
#include "utils.h"

static void usage(void) {
    fprintf(stderr,
        "Usage:\n"
        "  Blowfish (CTR):\n"
        "    ./crypto.exe blowfish -e -i <plaintext> -k <keyfile> -o <output>\n"
        "    ./crypto.exe blowfish -d -i <ciphertext> -k <keyfile> -o <output>\n"
        "\n"
        "  Salsa20:\n"
        "    ./crypto.exe salsa20 -e -i <plaintext> -k <keyfile> -o <output>\n"
        "    ./crypto.exe salsa20 -d -i <ciphertext> -k <keyfile> -o <output>\n"
        "\n"
        "  RSA\n"
        "    ./crypto.exe rsa keygen -p <pubkey_out> -s <privkey_out>\n"
        "    ./crypto.exe rsa -e -i <plaintext> -k <pubkey>  -o <output>\n"
        "    ./crypto.exe rsa -d -i <ciphertext> -k <privkey> -o <output>\n"
        "\n"
        "  Key generation (symmetric):\n"
        "    ./crypto.exe keygen blowfish -o <keyfile>   (generates 32-byte key)\n"
        "    ./crypto.exe keygen salsa20  -o <keyfile>   (generates 32-byte key)\n"
        "\n"
        "Options:\n"
        "  -e          encrypt\n"
        "  -d          decrypt\n"
        "  -i <file>   input file\n"
        "  -k <file>   key file\n"
        "  -o <file>   output file\n"
        "  -p <file>   public key file  (RSA keygen)\n"
        "  -s <file>   private key file (RSA keygen)\n"
        "\n"
        "Examples:\n"
        "  ./crypto.exe keygen blowfish -o my.key\n"
        "  ./crypto.exe blowfish -e -i secret.txt -k my.key -o secret.enc\n"
        "  ./crypto.exe blowfish -d -i secret.enc -k my.key -o recovered.txt\n"
        "  ./crypto.exe rsa keygen -p pub.key -s priv.key\n"
        "  ./crypto.exe rsa -e -i secret.txt -k pub.key  -o secret.enc\n"
        "  ./crypto.exe rsa -d -i secret.enc -k priv.key -o recovered.txt\n"
    );
    exit(1);
}

static void cmd_keygen_sym(const char *algo, const char *out) {
    if (!out) { fprintf(stderr, "keygen: missing -o\n"); usage(); }
    uint8_t key[32];
    rand_bytes(key, 32);
    if (write_file(out, key, 32) != 0) exit(1);
    printf("Generated 32-byte %s key -> %s\n", algo, out);
}

static void cmd_rsa_keygen(const char *pub_path, const char *priv_path) {
    if (!pub_path || !priv_path) {
        fprintf(stderr, "rsa keygen: missing -p or -s\n"); usage();
    }
    RSAPublicKey  pub;
    RSAPrivateKey priv;
    rsa_gen_keypair(&pub, &priv);
    if (rsa_save_public(&pub,  pub_path)  != 0) exit(1);
    if (rsa_save_private(&priv, priv_path) != 0) exit(1);
    printf("Public key  -> %s\n", pub_path);
    printf("Private key -> %s\n", priv_path);
}

static void cmd_blowfish(int encrypt, const char *in, const char *key_f, const char *out) {
    if (!in || !key_f || !out) { fprintf(stderr, "blowfish: missing argument\n"); usage(); }
    uint8_t key[32];
    if (read_key_file(key_f, key, 32) != 0) exit(1);
    BlowfishCtx ctx;
    bf_init(&ctx, key, 32);
    int ret = encrypt ? bf_ctr_encrypt_file(&ctx, in, out)
                      : bf_ctr_decrypt_file(&ctx, in, out);
    if (ret != 0) { fprintf(stderr, "Blowfish %s failed\n", encrypt?"encrypt":"decrypt"); exit(1); }
    printf("Blowfish %s: %s -> %s\n", encrypt?"encrypt":"decrypt", in, out);
}

static void cmd_salsa20(int encrypt, const char *in, const char *key_f, const char *out) {
    if (!in || !key_f || !out) { fprintf(stderr, "salsa20: missing argument\n"); usage(); }
    uint8_t key[SALSA20_KEY_SIZE];
    if (read_key_file(key_f, key, SALSA20_KEY_SIZE) != 0) exit(1);
    int ret = encrypt ? salsa20_encrypt_file(key, in, out)
                      : salsa20_decrypt_file(key, in, out);
    if (ret != 0) { fprintf(stderr, "Salsa20 %s failed\n", encrypt?"encrypt":"decrypt"); exit(1); }
    printf("Salsa20 %s: %s -> %s\n", encrypt?"encrypt":"decrypt", in, out);
}

static void cmd_rsa(int encrypt, const char *in, const char *key_f, const char *out) {
    if (!in || !key_f || !out) { fprintf(stderr, "rsa: missing argument\n"); usage(); }
    int ret;
    if (encrypt) {
        RSAPublicKey pub;
        if (rsa_load_public(&pub, key_f) != 0) exit(1);
        ret = rsa_encrypt_file(&pub, in, out);
    } else {
        RSAPrivateKey priv;
        if (rsa_load_private(&priv, key_f) != 0) exit(1);
        ret = rsa_decrypt_file(&priv, in, out);
    }
    if (ret != 0) { fprintf(stderr, "RSA %s failed\n", encrypt?"encrypt":"decrypt"); exit(1); }
    printf("RSA %s: %s -> %s\n", encrypt?"encrypt":"decrypt", in, out);
}

int main(int argc, char *argv[]) {
    if (argc < 2) usage();

    const char *algo = argv[1];

    if (strcmp(algo, "keygen") == 0) {
        if (argc < 3) usage();
        const char *sub = argv[2];
        const char *out = NULL;
        for (int i = 3; i < argc - 1; i++)
            if (strcmp(argv[i], "-o") == 0) out = argv[i+1];
        cmd_keygen_sym(sub, out);
        return 0;
    }

    if (strcmp(algo, "rsa") == 0) {
        if (argc < 3) usage();
        if (strcmp(argv[2], "keygen") == 0) {
            const char *pub = NULL, *priv = NULL;
            for (int i = 3; i < argc - 1; i++) {
                if (strcmp(argv[i], "-p") == 0) pub  = argv[i+1];
                if (strcmp(argv[i], "-s") == 0) priv = argv[i+1];
            }
            cmd_rsa_keygen(pub, priv);
            return 0;
        }
        int encrypt = -1;
        const char *in = NULL, *key_f = NULL, *out = NULL;
        for (int i = 2; i < argc; i++) {
            if (strcmp(argv[i], "-e") == 0) encrypt = 1;
            else if (strcmp(argv[i], "-d") == 0) encrypt = 0;
            else if (strcmp(argv[i], "-i") == 0 && i+1 < argc) in    = argv[++i];
            else if (strcmp(argv[i], "-k") == 0 && i+1 < argc) key_f = argv[++i];
            else if (strcmp(argv[i], "-o") == 0 && i+1 < argc) out   = argv[++i];
        }
        if (encrypt < 0) { fprintf(stderr, "rsa: specify -e or -d\n"); usage(); }
        cmd_rsa(encrypt, in, key_f, out);
        return 0;
    }

    if (strcmp(algo, "blowfish") == 0 || strcmp(algo, "salsa20") == 0) {
        int encrypt = -1;
        const char *in = NULL, *key_f = NULL, *out = NULL;
        for (int i = 2; i < argc; i++) {
            if (strcmp(argv[i], "-e") == 0) encrypt = 1;
            else if (strcmp(argv[i], "-d") == 0) encrypt = 0;
            else if (strcmp(argv[i], "-i") == 0 && i+1 < argc) in    = argv[++i];
            else if (strcmp(argv[i], "-k") == 0 && i+1 < argc) key_f = argv[++i];
            else if (strcmp(argv[i], "-o") == 0 && i+1 < argc) out   = argv[++i];
        }
        if (encrypt < 0) { fprintf(stderr, "%s: specify -e or -d\n", algo); usage(); }
        if (strcmp(algo, "blowfish") == 0)
            cmd_blowfish(encrypt, in, key_f, out);
        else
            cmd_salsa20(encrypt, in, key_f, out);
        return 0;
    }

    fprintf(stderr, "Unknown algorithm: %s\n", algo);
    usage();
    return 1;
}