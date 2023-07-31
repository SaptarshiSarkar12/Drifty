package Preferences;

import Backend.Drifty;
import Backend.CopyYt_dlp;
import Enums.Category;
import Enums.OS;
import Enums.Program;
import Enums.Type;
import Utils.MessageBroker;
import org.buildobjects.process.ProcBuilder;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * This class is used by the Launcher class and the FileDownloader class.
 * Launcher calls the environment method to initialize the environment which
 * includes figuring out which operating system is running and to figure out
 * the name of the yt-dlp command to use for the operating system. It then
 * copies the program out of resources and into a temp folder.
 * <p>
 * The other methods are used by the FileDownloader class. One will tell the
 * code whether or not the yt-dlp program as been updated within the last
 * 24 hours and the other one will actually pull an update to that file.
 * <p>
 * If the file does not exist in the temp folder when the program starts,
 * after it is copied to the temp folder, the timestamp for the last update
 * is set to a time in the past so that the next check for an update will
 * trigger the initial update after the first copy to the temp folder.
 */

public class Init {
    private static final MessageBroker messageBroker = Drifty.getMessageBrokerInstance();
    private static final long oneDay = 1000 * 60 * 60 * 24;

    public static void environment() {
        messageBroker.send("User OS is : " + OS.osName(), Type.INFORMATION, Category.LOG);
        String yt_dlpProgramName;
        if (OS.isWindows()) {
            yt_dlpProgramName = "yt-dlp.exe";
        }
        else if (OS.isMac()) {
            yt_dlpProgramName = "yt-dlp_macos";
        }
        else {
            yt_dlpProgramName = "yt-dlp";
        }
        Program.setName(yt_dlpProgramName);
        Program.setPath(Paths.get(System.getProperty("java.io.tmpdir")).toAbsolutePath().toString());
        messageBroker.send("yt-dlp program name detected is : " + yt_dlpProgramName, Type.INFORMATION, Category.LOG);
        CopyYt_dlp cy = new CopyYt_dlp();
        try {
            if(!cy.copyToTemp()) {
                AppSettings.set.updateTimestamp(System.currentTimeMillis());
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateProgram() {
        messageBroker.send("Checking for component (yt-dlp) update ...", Type.INFORMATION, Category.DOWNLOAD);
        String command = Program.get(Program.COMMAND);
        ProcBuilder proc = new ProcBuilder(command)
                .withArg("-U")
                .withOutputStream(System.out)
                .withErrorStream(System.err)
                .withTimeoutMillis(35000);
        proc.run();
        AppSettings.set.updateTimestamp(System.currentTimeMillis());
    }

    public static boolean isUpdated() {
        return (System.currentTimeMillis() - AppSettings.get.updateTimestamp()) < oneDay;
    }
}
