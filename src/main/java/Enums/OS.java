package Enums;

/**
 * Whenever any of the 'is' methods is called, 'thisOS'
 * gets set to the right OS if it is null. It remains
 * unchanged through the life of the program.
 */

public enum OS {

    WIN, MAC, LINUX, SOLARIS, FREEBSD;

    private static OS thisOS;
    private static String name;

    private static void setThisOS() {
        name = System.getProperty("os.name").toLowerCase();
        if (name.contains("win")) {
            thisOS = OS.WIN;
        }
        else if (name.contains("mac")) {
            thisOS = OS.MAC;
        }
        else if (name.contains("linux")) {
            thisOS = OS.LINUX;
        }
        else if (name.contains("sun")) {
            thisOS = OS.SOLARIS;
        }
        else if (name.contains("free")) {
            thisOS = OS.FREEBSD;
        }
        else {
            thisOS = OS.LINUX;
        }
    }

    public static String osName() {
        if (thisOS == null) {
            setThisOS();
        }
        return name;
    }

    public static boolean isWindows() {
        if (thisOS == null) {
            setThisOS();
        }
        return thisOS.equals(OS.WIN);
    }

    public static boolean isMac() {
        if (thisOS == null) {
            setThisOS();
        }
        return thisOS.equals(OS.MAC);
    }

    public static boolean isLinux() {
        if (thisOS == null) {
            setThisOS();
        }
        return thisOS.equals(OS.LINUX);
    }

    public static boolean isWinMac() {
        if (thisOS == null) {
            setThisOS();
        }
        return thisOS.equals(OS.WIN) || thisOS.equals(OS.MAC);
    }

    public static boolean isSun() {
        if (thisOS == null) {
            setThisOS();
        }
        return thisOS.equals(OS.SOLARIS);
    }

    public static boolean isBSD() {
        if (thisOS == null) {
            setThisOS();
        }
        return thisOS.equals(OS.FREEBSD);
    }

    public static boolean isNix() {
        if (thisOS == null) {
            setThisOS();
        }
        return thisOS.equals(OS.LINUX) || thisOS.equals(OS.SOLARIS) || thisOS.equals(OS.FREEBSD);
    }

}
