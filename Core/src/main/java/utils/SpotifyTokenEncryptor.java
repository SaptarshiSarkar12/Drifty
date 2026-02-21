package utils;

import init.Environment;
import javax.crypto.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class SpotifyTokenEncryptor {

    private static SecretKey secretKey;

    static {
        // Generate a secret key for encryption and decryption of the access token
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            secretKey = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            Environment.getMessageBroker().msgInitError("Failed to generate encryption key! No such algorithm exists! " + e.getMessage());
        }
    }

    public static String encryptToken(String token) {
        if (token.isEmpty()) {
            return token;
        }
        try {
            byte[] iv = new byte[12];
            new java.security.SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new javax.crypto.spec.GCMParameterSpec(128, iv));
            byte[] ciphertext = cipher.doFinal(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            byte[] combined = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException e) {
            Environment.getMessageBroker().msgInitError("Failed to encrypt Spotify access token! No such algorithm exists! " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            Environment.getMessageBroker().msgInitError("Failed to encrypt Spotify access token! Block size of the data is incorrect! " + e.getMessage());
        } catch (BadPaddingException e) {
            Environment.getMessageBroker().msgInitError("Failed to encrypt Spotify access token! Data is not padded correctly! " + e.getMessage());
        } catch (InvalidKeyException e) {
            Environment.getMessageBroker().msgInitError("Failed to encrypt Spotify access token! Failed to generate secret key! " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            Environment.getMessageBroker().msgInitError("Failed to encrypt Spotify access token! No such padding exists! " + e.getMessage());
        } catch (InvalidAlgorithmParameterException e) {
            Environment.getMessageBroker().msgInitError("Failed to encrypt Spotify access token! Invalid algorithm parameter! " + e.getMessage());
        }
        return token; // fallback in case of failure
    }

    public static String decrypt(String encoded) {
        if (encoded.isEmpty()) {
            return encoded;
        }
        try {
            byte[] combined = Base64.getDecoder().decode(encoded);
            byte[] iv = java.util.Arrays.copyOfRange(combined, 0, 12);
            byte[] ciphertext = java.util.Arrays.copyOfRange(combined, 12, combined.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new javax.crypto.spec.GCMParameterSpec(128, iv));
            return new String(cipher.doFinal(ciphertext), java.nio.charset.StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException e) {
            Environment.getMessageBroker().msgInitError("Failed to decrypt Spotify access token! No such algorithm exists! " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            Environment.getMessageBroker().msgInitError("Failed to decrypt Spotify access token! Block size of the data is incorrect! " + e.getMessage());
        } catch (BadPaddingException e) {
            Environment.getMessageBroker().msgInitError("Failed to decrypt Spotify access token! Data is not padded correctly! " + e.getMessage());
        } catch (InvalidKeyException e) {
            Environment.getMessageBroker().msgInitError("Failed to decrypt Spotify access token! Failed to generate secret key! " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            Environment.getMessageBroker().msgInitError("Failed to decrypt Spotify access token! No such padding exists! " + e.getMessage());
        } catch (InvalidAlgorithmParameterException e) {
            Environment.getMessageBroker().msgInitError("Failed to decrypt Spotify access token! Invalid algorithm parameter! " + e.getMessage());
        }
        return encoded;  // fallback in case of failure
    }
}
