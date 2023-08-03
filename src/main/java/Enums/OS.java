package Enums;

/**
 * This enum class is used to set which OS Drifty is run on
 */
public enum OS {
    WIN, MAC, LINUX, SOLARIS, FREEBSD;
    /**
     * This stores the type of Operating System on which Drifty is run.
     * Possible names of OS and corresponding OS category values are -
     * <table>
     *  <tr>
     *      <th>OS Name</th>
     *      <th>OS Type</th>
     *  </tr>
     *  <tr>
     *      <td>Windows</td>
     *      <td><i>WIN</i></td>
     *  </tr>
     *  <tr>
     *      <td>Mac OS</td>
     *      <td><i>MAC</i></td>
     *  </tr>
     *  <tr>
     *      <td>Linux / Unix</td>
     *      <td><i>LINUX</i></td>
     *  </tr>
     *  <tr>
     *      <td>Solaris</td>
     *      <td><i>SOLARIS</i></td>
     *  </tr>
     *  <tr>
     *      <td>Free BSD</td>
     *      <td><i>FREEBSD</i></td>
     *  </tr>
     * </table>
     */
    private static OS osType;
    /**
     * The full name of the OS along with the version number.
     * For example, <i>Windows 10</i>
     */
    private static String osName;

    /**
     * This method sets the OS type according to the system name where Drifty is run.
     */
    private static void setOSType() {
        osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            osType = OS.WIN;
        }
        else if (osName.contains("mac")) {
            osType = OS.MAC;
        }
        else if (osName.contains("linux")) {
            osType = OS.LINUX;
        }
        else if (osName.contains("sun")) {
            osType = OS.SOLARIS;
        }
        else if (osName.contains("free")) {
            osType = OS.FREEBSD;
        }
        else {
            osType = OS.LINUX;
        }
    }

    /**
     * This method is used to get the complete name of the OS where Drifty is running
     * @return the complete name of the Operating System on which Drifty is run
     */
    public static String getOSName() {
        if (osType == null) {
            setOSType();
        }
        return osName;
    }

    /**
     * This method tells if the OS on which Drifty is run, is Windows
     * @return true if the OS is Windows else false
     */
    public static boolean isWindows() {
        if (osType == null) {
            setOSType();
        }
        return osType.equals(OS.WIN);
    }

    /**
     * This method tells if the OS on which Drifty is run, is macOS
     * @return true if the OS is Mac OS else false
     */
    public static boolean isMac() {
        if (osType == null) {
            setOSType();
        }
        return osType.equals(OS.MAC);
    }

    /**
     * This method tells if the OS on which Drifty is run, is Linux or Unix system
     * @return true if the OS is Linux or Unix system else false
     */
    public static boolean isLinux() {
        if (osType == null) {
            setOSType();
        }
        return osType.equals(OS.LINUX);
    }

    /**
     * This method tells if the OS on which Drifty is run, is either Windows or macOS
     * @return true if the OS is either Windows or macOS else false
     */
    public static boolean isWinMac() {
        if (osType == null) {
            setOSType();
        }
        return osType.equals(OS.WIN) || osType.equals(OS.MAC);
    }

    /**
     * This method tells if the OS on which Drifty is run, is any Unix based system
     * @return true if the OS is any Unix based system else false
     */
    public static boolean isNix() {
        if (osType == null) {
            setOSType();
        }
        return osType.equals(OS.LINUX) || osType.equals(OS.SOLARIS) || osType.equals(OS.FREEBSD);
    }
}