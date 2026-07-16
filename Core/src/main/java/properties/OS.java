package properties;

public enum OS {
    WIN, MAC, LINUX, SOLARIS, FREEBSD;

    private static OS osType;
    private static String osName;
    private static String osArch;

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

    private static void setOSArch() {
        osArch = System.getProperty("os.arch");
        if (osArch.contains("arm") || osArch.contains("aarch64")) {
            osArch = "arm";
        } else if (osArch.contains("amd64") || osArch.contains("x86_64")) {
            osArch = "x64";
        } else {
            osArch = "unknown";
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

    public static String getOSArch() {
        if (osArch == null) {
            setOSArch();
        }
        return osArch;
    }

    public static boolean isWindows() {
        return getOSType().equals(OS.WIN);
    }

    public static boolean isMac() {
        return getOSType().equals(OS.MAC);
    }

    public static boolean isArm() {
        return "arm".equals(getOSArch());
    }

    public static boolean isX64() {
        return "x64".equals(getOSArch());
    }
}
