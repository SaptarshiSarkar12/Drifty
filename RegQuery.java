import java.io.*;

public class RegQuery {

    private static final String REGQUERY_UTIL = "reg query ";
    private static final String REGSTR_TOKEN = "REG_SZ";
    private static final String REGSTR_TOKEN2 = "REG_EXPAND_SZ";
    private static final String REGDWORD_TOKEN = "REG_DWORD";

    private static final String PERSONAL_FOLDER_CMD = REGQUERY_UTIL +
            "\"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\"
            + "Explorer\\User Shell Folders\" /v {374DE290-123F-4565-9164-39C4925E467B}";
    private static final String CPU_SPEED_CMD = REGQUERY_UTIL +
            "\"HKLM\\HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0\""
            + " /v ~MHz";
    private static final String CPU_NAME_CMD = REGQUERY_UTIL +
            "\"HKLM\\HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0\""
            + " /v ProcessorNameString";

    public static String getCurrentUserPersonalFolderPath() {
        try {
            Process process = Runtime.getRuntime().exec(PERSONAL_FOLDER_CMD);
            StreamReader reader = new StreamReader(process.getInputStream());

            reader.start();
            process.waitFor();
            reader.join();

            String result = reader.getResult();

            int p = result.indexOf(REGSTR_TOKEN2);

            if (p == -1)
                return null;

            return result.substring(p + REGSTR_TOKEN2.length()).trim(); // prints "D:\Downloads"
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String getCPUSpeed() {
        try {
            Process process = Runtime.getRuntime().exec(CPU_SPEED_CMD);
            System.out.println(CPU_NAME_CMD);
            System.out.println(PERSONAL_FOLDER_CMD);
            StreamReader reader = new StreamReader(process.getInputStream());

            reader.start();
            process.waitFor();
            reader.join();

            String result = reader.getResult();
            System.out.println(result);
            int p = result.indexOf(REGDWORD_TOKEN);

            if (p == -1)
                return null;

            // CPU speed in Mhz (minus 1) in HEX notation, convert it to DEC
            String temp = result.substring(p + REGDWORD_TOKEN.length()).trim();
            return Integer.toString
                    ((Integer.parseInt(temp.substring("0x".length()), 16) + 1));
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String getCPUName() {
        try {
            Process process = Runtime.getRuntime().exec(CPU_NAME_CMD);
            StreamReader reader = new StreamReader(process.getInputStream());

            reader.start();
            process.waitFor();
            reader.join();

            String result = reader.getResult();
            System.out.println(result);
            int p = result.indexOf(REGSTR_TOKEN);

            if (p == -1)
                return null;

            String s2 = result.substring(p + REGSTR_TOKEN.length()).trim();
            System.out.println(s2);
            return s2;
        }
        catch (Exception e) {
            return null;
        }
    }

    static class StreamReader extends Thread {
        private InputStream is;
        private StringWriter sw;

        StreamReader(InputStream is) {
            this.is = is;
            sw = new StringWriter();
        }

        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.write(c);
            }
            catch (IOException e) { ; }
        }

        String getResult() {
            return sw.toString();
        }
    }

    public static void main(String s[]) {
        System.out.println("Personal directory : "
                + getCurrentUserPersonalFolderPath());
        System.out.println("CPU Name : " + getCPUName());
        System.out.println("CPU Speed : " + getCPUSpeed() + " Mhz");
    }
}