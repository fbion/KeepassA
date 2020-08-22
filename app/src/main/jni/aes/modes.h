#ifndef HEADER_MODES_H
# define HEADER_MODES_H

//# include  "/Users/aria/dev/android/sdk/ndk/21/toolchains/llvm/prebuilt/darwin-x86_64/lib64/clang/9.0.8/include/stddef.h"

#include <stddef.h>

typedef void (*block128_f)(const unsigned char in[16],
                           unsigned char out[16], const void *key);

typedef void (*cbc128_f)(const unsigned char *in, unsigned char *out,
                         size_t len, const void *key,
                         unsigned char ivec[16], int enc);

void CRYPTO_cbc128_encrypt(const unsigned char *in, unsigned char *out,
                           size_t len, const void *key,
                           unsigned char ivec[16], block128_f block);

void CRYPTO_cbc128_decrypt(const unsigned char *in, unsigned char *out,
                           size_t len, const void *key,
                           unsigned char ivec[16], block128_f block);


#endif
