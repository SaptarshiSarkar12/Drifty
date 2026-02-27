package properties;

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

    public static OS getOSType() {
        if (osType == null) {
            setOSType();
        }
        return osType;
    }

    public static String getOSName() {
        if (osName == null) {
            setOSType();
        }
        return osName;
    }

    public static boolean isWindows() {
        return getOSType().equals(OS.WIN);
    }

    public static boolean isMac() {
        return getOSType().equals(OS.MAC);
    }
}
