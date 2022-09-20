import java.io.*;
import java.net.*;
import java.util.Scanner;

public class UrlDownload {
    final static int size=1024;
    public static void
    fileUrl(String fAddress, String
            localFileName, String destinationDir) {
        OutputStream outStream = null;
        URLConnection  uCon = null;

        InputStream is = null;
        try {
            URL Url;
            byte[] buf;
            int ByteRead,ByteWritten=0;
            Url= new URL(fAddress);
            outStream = new BufferedOutputStream(new
                    FileOutputStream(destinationDir+"\\"+localFileName));

            uCon = Url.openConnection();
            is = uCon.getInputStream();
            buf = new byte[size];
            while ((ByteRead = is.read(buf)) != -1) {
                outStream.write(buf, 0, ByteRead);
                ByteWritten += ByteRead;
            }
            System.out.println("Downloaded Successfully.");
            System.out.println
                    ("File name:\""+localFileName+ "\"\nNo ofbytes :" + ByteWritten);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                is.close();
                outStream.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }}}
    public static void
    fileDownload(String fAddress, String destinationDir)
    {

        int slashIndex =destinationDir.lastIndexOf('/');
        int periodIndex =destinationDir.lastIndexOf('.');

        String fileName=destinationDir.substring(slashIndex + 1);

        if (periodIndex >=1 &&  slashIndex >= 0
                && slashIndex < destinationDir.length()-1)
        {
            destinationDir = destinationDir.substring(0, slashIndex);
            fileUrl(fAddress,fileName,destinationDir);
        }
        else
        {
            System.err.println("path or file name.");
        }}
    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        String[] s = new String[2];
        for (int i = 0; i < 2; i++) {
            s[i] = sc.nextLine();
        }
        for (int i = 0; i < 1; i++) {
            fileDownload(s[0],s[1]);
        }
    }
}
