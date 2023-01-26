package Backend;

import Utils.DriftyConstants;
import Utils.MessageBroker;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class sendYt_dlpOutput extends Thread{
    File yt_dlpOutputFile = FileDownloader.getYt_dlpOutputFile();
    MessageBroker messageBroker = Drifty.messageBroker;
    private boolean exit = false;
    @Override
    public void run() {
        Scanner sc = null;
        try {
            sc = new Scanner(yt_dlpOutputFile);
        } catch (FileNotFoundException ignored) {}
        while (!exit) {
            try {
                String out = sc.nextLine();
                if (out.length() != 0) {
                    messageBroker.sendMessage(out, DriftyConstants.LOGGER_INFO, "download");
                }
            } catch (NoSuchElementException ignored){}
        }
    }

    protected void setExit(boolean exitValue){
        this.exit = exitValue;
    }
}
