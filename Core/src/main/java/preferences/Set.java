package preferences;

import init.Environment;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.prefs.Preferences;

import static preferences.Labels.*;

public class Set implements SettingsServiceSetters {
    private static final Set INSTANCE = new Set();
    private final Preferences preferences = Labels.PREFERENCES;
    SecretKey secretKey;

    protected Set() {
    }

    protected static Set getInstance() {
        return INSTANCE;
    }

    @Override
    public void setLastYtDlpUpdateTime(long value) {
        preferences.putLong(LAST_YT_DLP_UPDATE_TIME, value);
    }

    @Override
    public void setLastFolder(String lastFolderPath) {
        preferences.put(LAST_FOLDER, lastFolderPath);
    }

    @Override
    public void setYtDlpVersion(String version) {
        preferences.put(YT_DLP_VERSION, version);
    }

    @Override
    public void setFfmpegVersion(String version) {
        preferences.put(FFMPEG_VERSION, version);
    }

    @Override
    public void setSpotifyAccessToken(String token) {
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
        preferences.put(SPOTIFY_ACCESS_TOKEN, token);
    }

    @Override
    public void setYtDlpUpdating(boolean isInitializing) {
        preferences.putBoolean(YT_DLP_UPDATING, isInitializing);
    }

    @Override
    public void setFfmpegWorking(boolean isWorking) {
        preferences.putBoolean(IS_FFMPEG_WORKING, isWorking);
    }

    @Override
    public void setEarlyAccess(boolean isEarlyAccess) {
        preferences.putBoolean(EARLY_ACCESS, isEarlyAccess);
    }

    @Override
    public void setNewDriftyVersionName(String versionName) {
        preferences.put(NEW_DRIFTY_VERSION_NAME, versionName);
    }

    @Override
    public void setLastDriftyUpdateTime(long value) {
        preferences.putLong(LAST_DRIFTY_UPDATE_TIME, value);
    }

    @Override
    public void setLatestDriftyVersionTag(String tag) {
        preferences.put(LATEST_DRIFTY_VERSION_TAG, tag);
    }

    @Override
    public void setDriftyUpdateAvailable(boolean isUpdateAvailable) {
        preferences.putBoolean(DRIFTY_UPDATE_AVAILABLE, isUpdateAvailable);
    }
}
