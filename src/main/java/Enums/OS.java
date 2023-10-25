package Enums;

/**
 * This enum class is used to set the OS on which Drifty is running
 */
public enum OS {
    WIN, MAC, LINUX, SOLARIS, FREEBSD;

    private static OS osType;
    private static String osName;

    private static void setOSType() {
        osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            osType = OS.WIN;
        } else if (osName.contains("mac")) {
            osType = OS.MAC;
        } else if (osName.contains("linux")) {
            osType = OS.LINUX;
        } else if (osName.contains("sun")) {
            osType = OS.SOLARIS;
        } else if (osName.contains("free")) {
            osType = OS.FREEBSD;
        } else {
            osType = OS.LINUX;
        }
    }

    public static String getOSName() {
        if (osType == null) {
            setOSType();
        }
        return osName;
    }

    public static OS getOSType() {
        if (osType == null) {
            setOSType();
        }
        return osType;
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
}
