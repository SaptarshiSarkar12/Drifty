package preferences;

import init.Environment;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.prefs.Preferences;

import static preferences.Labels.*;

public class Set {
    private static final Set INSTANCE = new Set();
    private static final Preferences PREFERENCES = Labels.PREFERENCES;
    static final int GCM_IV_LENGTH = 12;
    static final int GCM_TAG_LENGTH = 128;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    SecretKey secretKey;

    protected Set() {
    }

    protected static Set getInstance() {
        return INSTANCE;
    }

    public void lastYtDlpUpdateTime(long value) {
        AppSettings.CLEAR.lastYtDlpUpdateTime();
        PREFERENCES.putLong(LAST_YT_DLP_UPDATE_TIME, value);
    }

    public void lastFolder(String lastFolderPath) {
        AppSettings.CLEAR.lastFolder();
        PREFERENCES.put(LAST_FOLDER, lastFolderPath);
    }

    public void ytDlpVersion(String version) {
        AppSettings.CLEAR.ytDlpVersion();
        PREFERENCES.put(YT_DLP_VERSION, version);
    }

    public void ffmpegVersion(String version) {
        AppSettings.CLEAR.ffmpegVersion();
        PREFERENCES.put(FFMPEG_VERSION, version);
    }

    public void spotifyAccessToken(String token) {
        try {
            // Generate a secret key for encryption and decryption of the access token
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            INSTANCE.secretKey = keyGenerator.generateKey();
            // Encrypt the access token using AES-GCM with a random IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, INSTANCE.secretKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            byte[] ciphertext = cipher.doFinal(token.getBytes(StandardCharsets.UTF_8));
            ByteBuffer buffer = ByteBuffer.allocate(iv.length + ciphertext.length);
            buffer.put(iv);
            buffer.put(ciphertext);
            token = Base64.getEncoder().encodeToString(buffer.array());
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
            Environment.getMessageBroker().msgInitError("Failed to encrypt Spotify access token! Invalid algorithm parameters! " + e.getMessage());
        }
        AppSettings.CLEAR.spotifyAccessToken();
        PREFERENCES.put(SPOTIFY_ACCESS_TOKEN, token);
    }

    public void ytDlpUpdating(boolean isInitializing) {
        AppSettings.CLEAR.ytDlpUpdating();
        PREFERENCES.putBoolean(YT_DLP_UPDATING, isInitializing);
    }

    public void isFfmpegWorking(boolean isWorking) {
        AppSettings.CLEAR.isFfmpegWorking();
        PREFERENCES.putBoolean(IS_FFMPEG_WORKING, isWorking);
    }

    public void earlyAccess(boolean isEarlyAccess) {
        AppSettings.CLEAR.earlyAccess();
        PREFERENCES.putBoolean(EARLY_ACCESS, isEarlyAccess);
    }

    public void newDriftyVersionName(String versionName) {
        AppSettings.CLEAR.newDriftyVersionName();
        PREFERENCES.put(NEW_DRIFTY_VERSION_NAME, versionName);
    }

    public void lastDriftyUpdateTime(long value) {
        AppSettings.CLEAR.lastDriftyUpdateTime();
        PREFERENCES.putLong(LAST_DRIFTY_UPDATE_TIME, value);
    }

    public void latestDriftyVersionTag(String tag) {
        AppSettings.CLEAR.latestDriftyVersionTag();
        PREFERENCES.put(LATEST_DRIFTY_VERSION_TAG, tag);
    }

    public void driftyUpdateAvailable(boolean isUpdateAvailable) {
        AppSettings.CLEAR.driftyUpdateAvailable();
        PREFERENCES.putBoolean(DRIFTY_UPDATE_AVAILABLE, isUpdateAvailable);
    }
}
