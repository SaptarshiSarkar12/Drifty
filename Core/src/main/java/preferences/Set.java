package preferences;

import init.Environment;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.prefs.Preferences;

import static preferences.Labels.*;

public class Set {
    private static final Set INSTANCE = new Set();
    private final Preferences preferences = Labels.PREFERENCES;
    SecretKey secretKey;

    protected Set() {
    }

    protected static Set getInstance() {
        return INSTANCE;
    }

    public void lastYtDlpUpdateTime(long value) {
        AppSettings.CLEAR.lastYtDlpUpdateTime();
        preferences.putLong(LAST_YT_DLP_UPDATE_TIME, value);
    }

    public void lastFolder(String lastFolderPath) {
        AppSettings.CLEAR.lastFolder();
        preferences.put(LAST_FOLDER, lastFolderPath);
    }

    public void ytDlpVersion(String version) {
        AppSettings.CLEAR.ytDlpVersion();
        preferences.put(YT_DLP_VERSION, version);
    }

    public void ffmpegVersion(String version) {
        AppSettings.CLEAR.ffmpegVersion();
        preferences.put(FFMPEG_VERSION, version);
    }

    public void spotifyAccessToken(String token) {
        try {
            // Generate a secret key for encryption and decryption of the access token
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            INSTANCE.secretKey = keyGenerator.generateKey();
            // Encrypt the access token
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, INSTANCE.secretKey);
            token = Base64.getEncoder().encodeToString(cipher.doFinal(token.getBytes()));
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
        AppSettings.CLEAR.spotifyAccessToken();
        preferences.put(SPOTIFY_ACCESS_TOKEN, token);
    }

    public void ytDlpUpdating(boolean isInitializing) {
        AppSettings.CLEAR.ytDlpUpdating();
        preferences.putBoolean(YT_DLP_UPDATING, isInitializing);
    }

    public void isFfmpegWorking(boolean isWorking) {
        AppSettings.CLEAR.isFfmpegWorking();
        preferences.putBoolean(IS_FFMPEG_WORKING, isWorking);
    }

    public void earlyAccess(boolean isEarlyAccess) {
        AppSettings.CLEAR.earlyAccess();
        preferences.putBoolean(EARLY_ACCESS, isEarlyAccess);
    }

    public void newDriftyVersionName(String versionName) {
        AppSettings.CLEAR.newDriftyVersionName();
        preferences.put(NEW_DRIFTY_VERSION_NAME, versionName);
    }

    public void lastDriftyUpdateTime(long value) {
        AppSettings.CLEAR.lastDriftyUpdateTime();
        preferences.putLong(LAST_DRIFTY_UPDATE_TIME, value);
    }

    public void latestDriftyVersionTag(String tag) {
        AppSettings.CLEAR.latestDriftyVersionTag();
        preferences.put(LATEST_DRIFTY_VERSION_TAG, tag);
    }

    public void driftyUpdateAvailable(boolean isUpdateAvailable) {
        AppSettings.CLEAR.driftyUpdateAvailable();
        preferences.putBoolean(DRIFTY_UPDATE_AVAILABLE, isUpdateAvailable);
    }
}
