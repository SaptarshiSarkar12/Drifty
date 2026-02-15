package utils;

import init.Environment;
import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class SpotifyTokenEncryptor {

    private static SecretKey secretKey;

    private SpotifyTokenEncryptor() {
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
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(token.getBytes()));
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
        }
        return token; // fallback in case of failure
    }

    public static String decrypt(String encoded) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encoded)));
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
        }
        return encoded;  // fallback in case of failure
    }
}
