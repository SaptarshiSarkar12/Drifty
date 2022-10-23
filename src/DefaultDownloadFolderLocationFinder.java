import java.io.*;

/**
 * This class deals with finding the path of the default downloads folder.
 */
class DefaultDownloadFolderLocationFinder {
    private static final String REG_TOKEN = "REG_EXPAND_SZ";

    /**
     * This function finds the path of the default downloads folder.
     * @return The path of the default downloads folder as a String object.
     */
    public static String findPath() {
        try {
            Process process = new ProcessBuilder("reg", "query", "\"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\User Shell Folders\"", "/v", "{374DE290-123F-4565-9164-39C4925E467B}").start();
            StreamReader reader = new StreamReader(process.getInputStream());
            reader.start();
            process.waitFor();
            reader.join();
            String result = reader.getResult();
            int p = result.indexOf(REG_TOKEN);
            if (p == -1) {
                return null;
            }

            result = result.substring(p + REG_TOKEN.length()).trim();
            result = result.replace("%USERPROFILE%", System.getProperty("user.home"));

            return result;
        }
        catch (Exception e) {
            return null;
        }
    }
    static class StreamReader extends Thread {
        private final InputStream is;
        private final StringWriter sw;

        StreamReader(InputStream is) {
            this.is = is;
            sw = new StringWriter();
        }

        @Override
        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.write(c);
            }
            catch (IOException ignored) {

            }
        }
        public String getResult() {
            return sw.toString();
        }
    }
}