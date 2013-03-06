CRYPTO_RSA
==========

Cryptography Assignment - DCU - 2012 - RSA

==========================================
ASSIGNMENT DESCRIPTION
==========================================

CA547 Cryptography and Security Protocols

Assignment 2

Digital Signature Using RSA


The aim of this assignment is to implement a digital signature using RSA. Before the digital signature can be implemented, you will need to set up an appropriate public/private RSA key pair. This should be done as follows:

Generate two distinct 512-bit probable primes p and q
Calculate the product of these two primes N = pq
Calculate the Euler totient function phi(N)
You will be using an encryption exponent e = 65537, so you will need to ensure that this is relatively prime to phi(N). If it is not, go back to step 1 and generate new values for p and q
Compute the value for the decryption exponent d, which is the multiplicative inverse of e (mod phi(N)). This should use your own implementation of the extended Euclidean GCD algorithm to calculate the inverse rather than using a library method for this purpose.
You should then write code to implement a decryption method which calculates cd (mod N). You should use your own implementation of the Chinese Remainder Theorem to calculate this more efficiently; this can also make use of your multiplicative inverse implementation.

Once your implementation is complete, you should digitally sign your code as follows:

Generate a 256-bit digest of your code using SHA-256.
Apply your decryption method to this digest. Note that for the purpose of this assignment no padding should be added to the digest.
You should send me the following by email:

Your 1024-bit modulus N in hexadecimal.
Your code as plaintext.
The digitally signed code digest.
A brief description (2-3 pages) of your implementation.
A declaration that this is solely your own work (except elements that are explicitly attributed to another source).
The implementation language must be Java. You can make use of the BigInteger class (java.math.BigInteger) and the crypto libraries (javax.crypto.*). You must not make use of the multiplicative inverse or GCD methods provided by the BigInteger class; you will need to implement these yourself. You can however make use of the crypto libraries to perform the SHA-256 hashing.

