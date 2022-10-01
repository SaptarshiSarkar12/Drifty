package src;

import java.io.*;

class DefaultDownloadFolderLocationFinder {
    private static final String REG_TOKEN = "REG_EXPAND_SZ";
    private static final String REG_NAME = "reg query \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\User Shell Folders\" /v {374DE290-123F-4565-9164-39C4925E467B}";
    public static String findPath() {
        try {
            Process process = Runtime.getRuntime().exec(REG_NAME);
            StreamReader reader = new StreamReader(process.getInputStream());
            reader.start();
            process.waitFor();
            reader.join();
            String result = reader.getResult();
            int p = result.indexOf(REG_TOKEN);
            if (p == -1) {
                return null;
            }
            return result.substring(p + REG_TOKEN.length()).trim();
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
            catch (IOException ignored) {

            }
        }
        String getResult() {
            return sw.toString();
        }
    }
}