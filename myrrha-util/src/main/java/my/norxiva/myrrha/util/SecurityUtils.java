package my.norxiva.myrrha.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.*;
import java.util.Enumeration;

/**
 * Utility methods for common cryptography usages including digesting, signing & verifying,
 * encrypting & decrypting, etc.
 */
public class SecurityUtils {
  private static final String X509 = "X.509";
  private static final String DEFAULT_PROVIDER = BouncyCastleProvider.PROVIDER_NAME;

  // For 1024bit encrypt/decrypt block key size
  private static final int ENCRYPT_BLOCK_KEY_SIZE = 117;
  private static final int DECRYPT_BLOCK_KEY_SIZE = 128;

  public static final String MD5 = "MD5";
  public static final String MD2 = "MD2";
  public static final String SHA1 = "SHA1";
  public static final String SM3 = "SM3";

  public static final String RSA = "RSA";
  public static final String DSA = "DSA";
  public static final String SM2 = "SM2";

  private SecurityUtils() {
    // private constructor for util class...
  }

  /**
   * Creates a {@code PrivateKey} from the base64 encoded {@code KeyStore} content.
   *
   * @param type     the type of {@code PrivateKey}
   * @param content  the base64 encoded {@code PrivateKey}
   * @param pwd      the password of {@code PrivateKey}
   * @param provider optional parameters, the name for {@link java.security.Provider}
   *                 If it is empty, the method will use system default provider.
   * @return the {@code PrivateKey}
   * @throws IllegalArgumentException if the content of key is not base64 encoded
   * @throws SecurityException        if the key is failed to generate
   */
  public static PrivateKey from(String type, String content,
                                String pwd, String provider) throws SecurityException {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(content);

    if (!Base64.isBase64(content)) {
      throw new IllegalArgumentException("The content of private key must be encoded as base64");
    }

    try {
      if (Strings.isNullOrEmpty(pwd)) {
        KeyFactory keyFactory = Strings.isNullOrEmpty(provider)
            ? KeyFactory.getInstance(type) : KeyFactory.getInstance(type, provider);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decodeBase64(content)));
      } else {
        char[] chars = pwd.toCharArray();
        KeyStore keyStore = getKeyStore(type, content, pwd, provider);
        Enumeration<String> keyAliases = keyStore.aliases();
        String keyAlias;
        PrivateKey privateKey = null;

        while (keyAliases.hasMoreElements()) {
          keyAlias = keyAliases.nextElement();
          if (keyStore.isKeyEntry(keyAlias)) {
            privateKey = (PrivateKey) keyStore.getKey(keyAlias, chars);
            break;
          }
        }

        if (privateKey == null) {
          throw new InvalidKeyException("No available private key was found");
        } else {
          return privateKey;
        }
      }
    } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException
        | InvalidKeySpecException | UnrecoverableKeyException | KeyStoreException err) {
      throw new SecurityException("Error generating the private key", err);
    }
  }

  /**
   * Use BC Provider as default provider for getting private key.
   */
  public static PrivateKey from(String type, String content, String pwd) throws SecurityException {
    return from(type, content, pwd, DEFAULT_PROVIDER);
  }

  /**
   * Creates a {@code PublicKey} from the base64 encoded {@code Certificate} content.
   *
   * @param type    the type of {@code PublicKey}
   * @param content the base64 encoded {@code PublicKey}
   * @return the {@code PublicKey}
   * @throws IllegalArgumentException if the content of certificate is not base64 encoded
   * @throws SecurityException        if the key is failed to generate
   */
  public static PublicKey from(String type, String content) throws SecurityException {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(content);

    if (!Base64.isBase64(content)) {
      throw new IllegalArgumentException("The content of cert must be encoded as base64");
    }

    try {
      if (X509.equals(type)) {
        return CertificateFactory
            .getInstance(type, DEFAULT_PROVIDER)
            .generateCertificate(new ByteArrayInputStream(Base64.decodeBase64(content)))
            .getPublicKey();
      } else {
        return KeyFactory
            .getInstance(type, DEFAULT_PROVIDER)
            .generatePublic(new X509EncodedKeySpec(Base64.decodeBase64(content)));
      }
    } catch (NoSuchProviderException | NoSuchAlgorithmException
        | CertificateException | InvalidKeySpecException err) {
      throw new SecurityException("Error generating the public key", err);
    }
  }

  /**
   * Retrieves the serial number from the base64 encoded {@code PrivateKey}.
   *
   * @param type     the type of {@code PrivateKey}
   * @param key      the base64 encoded {@code PrivateKey}
   * @param password the password of {@code PrivateKey}
   * @return the serial no of {@code PrivateKey}
   * @throws IllegalArgumentException if the content of key is not base64 encoded
   * @throws SecurityException        if the serial no is failed to retrieve
   */
  public static String serialNo(String type, String key, String password) throws SecurityException {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(password);

    if (!Base64.isBase64(key)) {
      throw new IllegalArgumentException("The content of private key must be encoded as base64");
    }

    X509Certificate cert = getCertificate(type, key, password, DEFAULT_PROVIDER);
    return cert.getSerialNumber().toString();
  }

  /**
   * Retrieves the serial number from the base64 encoded {@code PublicKey}.
   *
   * @param type the type of {@code PublicKey}
   * @param key  the base64 encoded {@code PublicKey}
   * @return the serial no of {@code PublicKey}
   * @throws IllegalArgumentException if the content of key is not base64 encoded
   * @throws SecurityException        if the serial no is failed to retrieve
   */
  public static String serialNo(String type, String key) throws SecurityException {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(key);

    if (!Base64.isBase64(key)) {
      throw new IllegalArgumentException("The content of certificate must be encoded as base64");
    }

    try {
      CertificateFactory factory = CertificateFactory.getInstance(type);
      return ((X509Certificate) factory.generateCertificate(
          new ByteArrayInputStream(Base64.decodeBase64(key)))).getSerialNumber().toString();
    } catch (CertificateException err) {
      throw new SecurityException("Error retrieving the serial no from key", err);
    }
  }

  /**
   * Generates a salt bytes randomly with the specified algorithm.
   *
   * @param algorithm the algorithm
   * @return the generated salt bytes
   * @throws SecurityException if the salt is failed to generate
   */
  public static byte[] generateSalt(String algorithm) throws SecurityException {
    Preconditions.checkNotNull(algorithm);
    try {
      // 8 bytes is good enough for salt
      // see more details on: http://stackoverflow.com/a/5197921/339286
      byte[] salt = new byte[8];
      SecureRandom.getInstance(algorithm).nextBytes(salt);
      return salt;
    } catch (NoSuchAlgorithmException err) {
      throw new SecurityException("Error generating the salt", err);
    }
  }

  /**
   * Generates a {@code SecretKey} with the specified algorithm.
   *
   * @param algorithm the algorithm
   * @return the {@code SecretKey}
   * @throws SecurityException if the secret key is failed to generate
   */
  public static SecretKey generateSecretKey(String algorithm) throws SecurityException {
    Preconditions.checkNotNull(algorithm);
    try {
      return KeyGenerator.getInstance(algorithm, DEFAULT_PROVIDER).generateKey();
    } catch (NoSuchProviderException | NoSuchAlgorithmException err) {
      throw new SecurityException("Error generating the secret key", err);
    }
  }

  /**
   * Generates a {@code SecretKey} with the specified algorithm and key specification.
   *
   * @param algorithm the algorithm
   * @param spec      the {@code KeySpec}
   * @return the {@code SecretKey}
   * @throws SecurityException if the secret key is failed to generate
   */
  public static SecretKey generateSecretKey(String algorithm, KeySpec spec)
      throws SecurityException {
    Preconditions.checkNotNull(algorithm);
    Preconditions.checkNotNull(spec);
    try {
      return SecretKeyFactory.getInstance(algorithm, DEFAULT_PROVIDER).generateSecret(spec);
    } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException err) {
      throw new SecurityException("Error generating the secret key", err);
    }
  }

  /**
   * Completes the hash computation by performing final operations such as padding.
   *
   * @param algorithm the algorithm
   * @param data      the data bytes to be computed with
   * @return the array of bytes for the resulting hash value
   * @throws SecurityException if failed to digest data
   */
  public static byte[] digest(String algorithm, byte[] data) throws SecurityException {
    Preconditions.checkNotNull(algorithm);
    Preconditions.checkNotNull(data);

    try {
      MessageDigest digest = MessageDigest.getInstance(algorithm, DEFAULT_PROVIDER);
      digest.update(data);
      return digest.digest();
    } catch (NoSuchProviderException | NoSuchAlgorithmException err) {
      throw new SecurityException("Error digesting the data", err);
    }
  }

  /**
   * Signs data bytes with the specified algorithm and {@code PrivateKey} instance.
   *
   * @param algorithm the algorithm
   * @param key       the {@code PrivateKey}
   * @param data      the data bytes to be signed
   * @return the bytes of signature
   * @throws SecurityException if failed to sign data
   */
  public static byte[] sign(String algorithm, PrivateKey key, byte[] data)
      throws SecurityException {
    Preconditions.checkNotNull(algorithm);
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(data);

    try {
      Signature signer = Signature.getInstance(algorithm, DEFAULT_PROVIDER);
      signer.initSign(key);
      signer.update(data);
      return signer.sign();
    } catch (NoSuchAlgorithmException | InvalidKeyException
        | SignatureException | NoSuchProviderException err) {
      throw new SecurityException("Error signing the data with private key", err);
    }
  }

  /**
   * Verifies data bytes with the corresponding signature, algorithm and {@code PublicKey}
   * instance.
   *
   * @param algorithm the algorithm
   * @param key       the {@code PublicKey}
   * @param data      the data bytes that already signed
   * @param signature the signature string to be verified
   * @return true if signature is valid, false otherwise
   * @throws SecurityException if failed to verify data
   */
  public static boolean verify(String algorithm, PublicKey key, byte[] data, byte[] signature)
      throws SecurityException {
    Preconditions.checkNotNull(algorithm);
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(data);
    Preconditions.checkNotNull(signature);

    try {
      Signature verifier = Signature.getInstance(algorithm, DEFAULT_PROVIDER);
      verifier.initVerify(key);
      verifier.update(data);
      return verifier.verify(signature);
    } catch (NoSuchProviderException | NoSuchAlgorithmException
        | InvalidKeyException | SignatureException err) {
      throw new SecurityException("Error verifying the data with public key", err);
    }
  }

  /**
   * Encrypts data bytes with the specified algorithm and {@code Key} instance.
   *
   * @param algorithm the algorithm
   * @param key       the {@code key}
   * @param data      the raw data bytes
   * @return the encrypted data bytes
   * @throws SecurityException if failed to encrypt data
   */
  public static byte[] encrypt(String algorithm, Key key, byte[] data) throws SecurityException {
    Preconditions.checkNotNull(algorithm);
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(data);

    try {
      Cipher encryptor = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
      encryptor.init(Cipher.ENCRYPT_MODE, key);
      return encryptor.doFinal(data);
    } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException
        | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException err) {
      throw new SecurityException("Error encrypt the data with key", err);
    }
  }

  /**
   * Encrypts data bytes with the specified algorithm, parameter specification and {@code Key}
   * instance.
   *
   * @param algorithm the algorithm
   * @param spec      the algorithm parameter specification
   * @param key       the {@code key} instance*
   * @param data      the raw data bytes
   * @return the encrypted data bytes
   * @throws SecurityException if failed to encrypt data
   */
  public static byte[] encrypt(String algorithm, AlgorithmParameterSpec spec, Key key, byte[] data)
      throws SecurityException {
    Preconditions.checkNotNull(algorithm);
    Preconditions.checkNotNull(spec);
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(data);

    try {
      Cipher encryptor = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
      encryptor.init(Cipher.ENCRYPT_MODE, key, spec);
      return encryptor.doFinal(data);
    } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException
        | NoSuchPaddingException | BadPaddingException
        | IllegalBlockSizeException | InvalidAlgorithmParameterException err) {
      throw new SecurityException("Error encrypt the data with key & spec", err);
    }
  }

  /**
   * Block encrypts data bytes with the specified algorithm and {@code Key} instance by block.
   *
   * @param algorithm the algorithm
   * @param key       the {@code key}
   * @param data      the raw data bytes
   * @return the encrypted data bytes
   * @throws SecurityException if failed to encrypt data
   */
  public static byte[] blockEncrypt(String algorithm,
                                    Key key, byte[] data) throws SecurityException {
    return blockEncrypt(algorithm, key, data, ENCRYPT_BLOCK_KEY_SIZE);
  }

  /**
   * Block encrypts data bytes with the specified algorithm and {@code Key} instance by block.
   *
   * @param algorithm           the algorithm
   * @param key                 the {@code key}
   * @param data                the raw data bytes
   * @param encryptBlockKeySize the encrypt block key size
   * @return the encrypted data bytes
   * @throws SecurityException if failed to encrypt data
   */
  public static byte[] blockEncrypt(String algorithm, Key key, byte[] data,
                                    int encryptBlockKeySize) throws SecurityException {
    Preconditions.checkNotNull(algorithm);
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(data);
    try {
      Cipher encryptor = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
      encryptor.init(Cipher.ENCRYPT_MODE, key);
      byte[] result = null;
      for (int i = 0; i < data.length; i += encryptBlockKeySize) {
        byte[] doFinal = encryptor.doFinal(subArray(data, i, i + encryptBlockKeySize));
        result = addAll(result, doFinal);
      }
      return result;
    } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException
        | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException err) {
      throw new SecurityException("Error encrypt the data with key", err);
    }

  }

  /**
   * Decrypts data bytes asymmetrically with the specified algorithm and {@code Key} instance.
   *
   * @param algorithm     the algorithm
   * @param key           the {@code key} instance
   * @param encryptedData the encrypted data bytes
   * @return the decrypted data bytes
   * @throws SecurityException if failed to decrypt data
   */
  public static byte[] decrypt(String algorithm, Key key, byte[] encryptedData)
      throws SecurityException {
    Preconditions.checkNotNull(algorithm);
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(encryptedData);

    try {
      Cipher decryptor = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
      decryptor.init(Cipher.DECRYPT_MODE, key);
      return decryptor.doFinal(encryptedData);
    } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException
        | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException err) {
      throw new SecurityException("Error decrypt the data with key", err);
    }
  }

  /**
   * Decrypts data bytes asymmetrically with the specified algorithm and {@code Key} instance.
   *
   * @param algorithm     the algorithm
   * @param spec          the algorithm parameter specification
   * @param key           the {@code key} instance
   * @param encryptedData the encrypted data bytes
   * @return the decrypted data bytes
   * @throws SecurityException if failed to decrypt data
   */
  public static byte[] decrypt(String algorithm,
                               AlgorithmParameterSpec spec,
                               Key key,
                               byte[] encryptedData) throws SecurityException {
    Preconditions.checkNotNull(algorithm);
    Preconditions.checkNotNull(spec);
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(encryptedData);

    try {
      Cipher decryptor = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
      decryptor.init(Cipher.DECRYPT_MODE, key, spec);
      return decryptor.doFinal(encryptedData);
    } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException
        | NoSuchPaddingException | BadPaddingException
        | IllegalBlockSizeException | InvalidAlgorithmParameterException err) {
      throw new SecurityException("Error decrypt the data with key & spec", err);
    }
  }

  /**
   * Block decrypts data bytes asymmetrically with the specified algorithm and {@code Key} instance.
   */
  public static byte[] blockDecrypt(String algorithm,
                                    Key key, byte[] encryptedData) throws SecurityException {
    return blockDecrypt(algorithm, key, encryptedData, DECRYPT_BLOCK_KEY_SIZE);
  }

  /**
   * Block decrypts data bytes asymmetrically with the specified algorithm and {@code Key} instance.
   */
  public static byte[] blockDecrypt(String algorithm, Key key, byte[] encryptedData,
                                    int decryptBlockKeySize) throws SecurityException {
    Preconditions.checkNotNull(algorithm);
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(encryptedData);
    byte[] result = null;

    try {
      Cipher decryptor = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
      decryptor.init(Cipher.DECRYPT_MODE, key);
      for (int i = 0; i < encryptedData.length; i += decryptBlockKeySize) {
        byte[] doFinal = decryptor.doFinal(subArray(encryptedData, i, i + decryptBlockKeySize));
        result = addAll(result, doFinal);
      }
      return result;
    } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException
        | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException err) {
      throw new SecurityException("Error decrypt the data with key", err);
    }
  }

  private static byte[] clone(byte[] array) {
    if (array == null) {
      return new byte[0];
    }
    return array.clone();
  }

  private static byte[] subArray(byte[] array, int startIndexInclusive, int endIndexExclusive) {
    if (array == null) {
      return new byte[0];
    }
    if (startIndexInclusive < 0) {
      startIndexInclusive = 0;
    }
    if (endIndexExclusive > array.length) {
      endIndexExclusive = array.length;
    }
    int newSize = endIndexExclusive - startIndexInclusive;

    if (newSize <= 0) {
      return new byte[0];
    }

    byte[] subArray = new byte[newSize];

    System.arraycopy(array, startIndexInclusive, subArray, 0, newSize);

    return subArray;
  }

  private static byte[] addAll(byte[] array1, byte[] array2) {
    if (array1 == null) {
      return clone(array2);
    } else if (array2 == null) {
      return clone(array1);
    }
    byte[] joinedArray = new byte[array1.length + array2.length];
    System.arraycopy(array1, 0, joinedArray, 0, array1.length);
    System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
    return joinedArray;
  }

  private static KeyStore getKeyStore(String type, String key, String pwd, String provider)
      throws SecurityException {
    try {
      char[] chars = pwd.toCharArray();
      KeyStore keyStore = Strings.isNullOrEmpty(provider)
          ? KeyStore.getInstance(type) : KeyStore.getInstance(type, provider);
      keyStore.load(new ByteArrayInputStream(Base64.decodeBase64(key)), chars);
      return keyStore;
    } catch (NoSuchProviderException | NoSuchAlgorithmException | KeyStoreException
        | CertificateException | IOException err) {
      throw new SecurityException("Error generating the private key", err);
    }
  }

  private static X509Certificate getCertificate(String type,
                                                String key,
                                                String pwd,
                                                String provider)
      throws SecurityException {
    try {
      KeyStore keyStore = getKeyStore(type, key, pwd, provider);
      Enumeration<String> keyAliases = keyStore.aliases();
      if (keyAliases.hasMoreElements()) {
        String keyAlias = keyAliases.nextElement();
        return (X509Certificate) keyStore.getCertificate(keyAlias);
      } else {
        throw new SecurityException("Error generating X509Certificate from the private key!");
      }
    } catch (KeyStoreException err) {
      throw new SecurityException("Error generating X509Certificate from the private key", err);
    }
  }
}
