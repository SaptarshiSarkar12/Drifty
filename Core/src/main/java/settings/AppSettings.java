package settings;

public class AppSettings {
    public static final SettingsReader GET = SettingsAsPreferencesReader.getInstance();
    public static final SettingsWriter SET = SettingsAsPreferencesWriter.getInstance();
}