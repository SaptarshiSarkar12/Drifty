package preferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import init.Environment;
import org.apache.commons.io.FileUtils;
import org.hildan.fxgson.FxGson;
import properties.Program;
import support.JobHistory;

import javax.crypto.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.prefs.Preferences;

import static preferences.Labels.*;
import static properties.Program.JOB_HISTORY_FILE;

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

    public void jobHistory(JobHistory jobHistory) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = FxGson.addFxSupport(gsonBuilder).setPrettyPrinting().create();
        String value = gson.toJson(jobHistory);
        Path jobHistoryFile = Paths.get(Program.get(JOB_HISTORY_FILE));
        try {
            FileUtils.writeStringToFile(jobHistoryFile.toFile(), value, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
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
}
