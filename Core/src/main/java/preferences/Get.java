package preferences;

import init.Environment;
import utils.Utility;

import javax.crypto.*;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.prefs.Preferences;

import static preferences.Labels.*;

public class Get implements SettingsService {
    private static final Get INSTANCE = new Get();
    private final Preferences preferences = Labels.PREFERENCES;

    static Get getInstance() {
        return INSTANCE;
    }

    @Override
    public long getLastYtDlpUpdateTime() {
        return preferences.getLong(LAST_YT_DLP_UPDATE_TIME, 1000L);
    }

    @Override
    public String getLastDownloadFolder() {
        String defaultPath = Paths.get(System.getProperty("user.home"), "Downloads").toAbsolutePath().toString();
        return preferences.get(LAST_FOLDER, defaultPath);
    }

    @Override
    public String getYtDlpVersion() {
        return preferences.get(YT_DLP_VERSION, "");
    }

    @Override
    public String getFfmpegVersion() {
        return preferences.get(FFMPEG_VERSION, "");
    }

    @Override
    public String getSpotifyAccessToken() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            Cipher cipher = Cipher.getInstance("AES");
            SecretKey secretKey = Set.getInstance().secretKey;
            while (secretKey == null) { // Sometimes, the encryption of token takes time and the key doesn't get generated in time
                Utility.sleep(10);
                secretKey = Set.getInstance().secretKey;
            }
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(preferences.get(SPOTIFY_ACCESS_TOKEN, ""))));
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
        return preferences.get(SPOTIFY_ACCESS_TOKEN, "");
    }

    @Override
    public boolean isYtDlpUpdating() {
        return preferences.getBoolean(YT_DLP_UPDATING, false);
    }

    @Override
    public boolean isFfmpegWorking() {
        return preferences.getBoolean(IS_FFMPEG_WORKING, false);
    }

    @Override
    public boolean isEarlyAccessEnabled() {
        return preferences.getBoolean(EARLY_ACCESS, false);
    }

    @Override
    public String getNewDriftyVersionName() {
        return preferences.get(NEW_DRIFTY_VERSION_NAME, "");
    }

    @Override
    public long getLastDriftyUpdateTime() {
        return preferences.getLong(LAST_DRIFTY_UPDATE_TIME, 1000L);
    }

    @Override
    public String getLatestDriftyVersionTag() {
        return preferences.get(LATEST_DRIFTY_VERSION_TAG, "");
    }

    @Override
    public boolean isDriftyUpdateAvailable() {
        return preferences.getBoolean(DRIFTY_UPDATE_AVAILABLE, false);
    }

    @Override
    public String getFolders() {
        return preferences.get(FOLDERS, "");
    }

    @Override
    public boolean isGuiAutoPasteEnabled() {
        return preferences.getBoolean(GUI_AUTO_PASTE, false);
    }

    @Override
    public String getGuiTheme() {
        return preferences.get(GUI_THEME, "Light");
    }
}
