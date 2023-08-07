package Enums;
/**
 * This enum class is used to set which OS Drifty is run on
 */
public enum OS {
    WIN, MAC, LINUX, SOLARIS, FREEBSD;
    private static OS osType;
    private static String osName;

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

    public static String getOSName() {
        if (osType == null) {
            setOSType();
        }
        return osName;
    }

    public static boolean isWindows() {
        if (osType == null) {
            setOSType();
        }
        return osType.equals(OS.WIN);
    }

    public static boolean isMac() {
        if (osType == null) {
            setOSType();
        }
        return osType.equals(OS.MAC);
    }

    public static boolean isLinux() {
        if (osType == null) {
            setOSType();
        }
        return osType.equals(OS.LINUX);
    }

    public static boolean isWinMac() {
        if (osType == null) {
            setOSType();
        }
        return osType.equals(OS.WIN) || osType.equals(OS.MAC);
    }

    public static boolean isNix() {
        if (osType == null) {
            setOSType();
        }
        return osType.equals(OS.LINUX) || osType.equals(OS.SOLARIS) || osType.equals(OS.FREEBSD);
    }
}
