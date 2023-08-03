package Enums;

import java.nio.file.Paths;

/**
 * This class contains all the configurations of Drifty required throughout its life.
 */
public enum DriftyConfig {
    /**
     * The name of the yt-dlp program
     */
    NAME,
    /**
     * The path to the system's default temporary directory
     */
    PATH,
    /**
     * The command to call the yt-dlp program. It combines the name of yt-dlp program along with its path
     */
    YT_DLP_COMMAND,
    /**
     * The path to the Operating System's default configuration folder.
     * <table>
     *     <tr>
     *         <th>Operating System</th>
     *         <th>App Configuration Directory</th>
     *     </tr>
     *     <tr>
     *         <td>Windows</td>
     *         <td>AppData</td>
     *     </tr>
     *     <tr>
     *         <td>Linux/macOS</td>
     *         <td>~/.config</td>
     *     </tr>
     * </table>
     */
    BATCH_PATH;

    /**
     * This stores the name of the yt-dlp program, according to the Operating System on which Drifty is run
     */
    private static String yt_dlpProgramName;
    /**
     * This stores the path of the yt-dlp program
     */
    private static String yt_dlpProgramPath;
    /**
     * This
     */
    private static String batchPath;

    /**
     * This method is used to set the name of the yt-dlp program
     * @param name The name of the yt-dlp program according to the operating system
     *      <table>
     *          <tr>
     *              <th>Operating System</th>
     *              <th>yt-dlp program Name</th>
     *          </tr>
     *          <tr>
     *              <td>Windows</td>
     *              <td>yt-dlp.exe</td>
     *          </tr>
     *          <tr>
     *              <td>MacOS</td>
     *              <td>yt-dlp_macos</td>
     *          </tr>
     *          <tr>
     *              <td>Linux / Other OS</td>
     *              <td>yt-dlp</td>
     *          </tr>
     *      </table>
     */
    public static void setYt_dlpProgramName(String name) {
        DriftyConfig.yt_dlpProgramName = name;
    }

    /**
     * This method is used to set the yt-dlp program path
     * @param path The path to the yt-dlp program
     */
    public static void setYt_dlpProgramPath(String path) {
        DriftyConfig.yt_dlpProgramPath = path;
    }

    /**
     * This method is used to set the Operating System default configuration directory where the json file containing the required link, directory and filename for batch downloading, is stored.
     * @param path The path to the Operating System's default configuration directory
     */
    public static void setBatchPath(String path) {
        DriftyConfig.batchPath = path;
    }

    /**
     * This method is used to get the configuration for Drifty
     * @param driftyConfig The configuration name that is required to fetch
     * @return the configuration value for the particular configuration asked
     */
    public static String getConfig(DriftyConfig driftyConfig) {
        return switch (driftyConfig) {
            case NAME -> yt_dlpProgramName;
            case PATH -> yt_dlpProgramPath;
            case YT_DLP_COMMAND -> Paths.get(yt_dlpProgramPath, yt_dlpProgramName).toAbsolutePath().toString();
            case BATCH_PATH -> batchPath;
        };
    }
}
