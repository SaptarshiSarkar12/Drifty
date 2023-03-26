package Utils;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import java.io.PrintStream;

/**
 * This class allows the Backend to <b>send its outputs to the Command Line Interface (CLI) and Graphical User Interface (GUI) versions of Drifty</b>.
 * @since 2.0.0
 * @see Backend.Drifty
 * @see GUI.Drifty_GUI
 * @see CLI.Drifty_CLI
 * @version 2.0.0
 */
public class MessageBroker {
    CreateLogs logger = CreateLogs.getInstance(); // logger instance created
    String appType;
    Text link;
    Text dir;
    Text fileName;
    Text download;
    PrintStream output;

    /**
     * This is the <b>constructor of message broker</b> that configures it to be able to work with <b>Command Line Interface (CLI) Functionalities</b>.
     * @param applicationType Type of the <b>application it is configured by</b>. Possible values : <i>"CLI"</i> and <i>"GUI"</i>. Here, usually, <b>"CLI"</b> will be passed as parameter.
     * @param consoleOutput The Output Stream where the message broker will give its outputs received from CLI.
     * @since 2.0.0
     */
    public MessageBroker(String applicationType, PrintStream consoleOutput){
        appType = applicationType;
        output = consoleOutput;
    }

    /**
     * This is the <b>constructor of message broker</b> that configures it to be able to work with <b>Graphical User Interface (GUI) Functionalities</b>.
     * @param applicationType Type of the <b>application it is configured by</b>. Possible values : <i>"CLI"</i> and <i>"GUI"</i>. Here, usually, <b>"GUI"</b> will be passed as parameter.
     * @param link The Text area of the GUI where the output regarding the <b>link</b> will be sent.
     * @param dir The Text area of the GUI where the output regarding the <b>link</b> will be sent.
     * @param download The Text area of the GUI where the output regarding the <b>link</b> will be sent.
     * @param fileName The Text area of the GUI where the output regarding the <b>link</b> will be sent.
     * @since 2.0.0
     */
    public MessageBroker(String applicationType, Text link, Text dir, Text download, Text fileName){
        appType = applicationType;
        this.link = link;
        this.dir = dir;
        this.download = download;
        this.fileName = fileName;
    }

    /**
     * This method <b>sends as well as logs (in the log file) the message containing the output</b> to the output areas for <i>both CLI and GUI versions</i>. It also <b>sets the colour code for the output</b> only in case of <i>GUI version of Drifty</i>.
     * @param message The output message that needs to be shown to the user.
     * @param messageType The type of the output message to be shown to the user. Possible values : <b>INFO</b>, <b>WARN</b> and <b>ERROR</b>.
     * @param messageCategory The category of the output message to be shown to the user. Possible values : <b>link</b>, <b>directory</b>, <b>download</b> and <b>Filename</b>.
     * @since 2.0.0
     */
    public void sendMessage(String message, String messageType, String messageCategory){
        if (appType.equals("CLI")){
            output.println(message);
            logger.log(messageType, message);
        } else if (appType.equals("GUI")){
            Color color = Color.BLACK;
            if (messageType.equals(DriftyConstants.LOGGER_INFO)){
                color = Color.GREEN;
            } else if (messageType.equals(DriftyConstants.LOGGER_ERROR)) {
                color = Color.RED;
            } else if (messageType.equals(DriftyConstants.LOGGER_WARN)) {
                color = Color.YELLOW;
            } else {
                logger.log(DriftyConstants.LOGGER_ERROR, "Invalid message type provided to message broker!");
            }
            if (messageCategory.equals("link")){
                link.setText(message);
                link.setFill(color);
                logger.log(messageType, message);
            } else if (messageCategory.equals("directory")) {
                dir.setText(message);
                dir.setFill(color);
                logger.log(messageType, message);
            } else if (messageCategory.equals("download")) {
                download.setText(message);
                download.setFill(color);
                logger.log(messageType, message);
            } else if (messageCategory.equals("Filename")) {
                fileName.setText(message);
                fileName.setFill(color);
                logger.log(messageType, message);
            } else {
                logger.log(DriftyConstants.LOGGER_ERROR, "Invalid message category provided to message broker!");
            }
        } else {
            logger.log(DriftyConstants.LOGGER_ERROR, "Invalid application type provided to message broker!");
        }
    }

    /**
     * This method returns the <b>output stream</b> where the outputs (usually for CLI) will be sent.
     * @return The Output Stream where the outputs (usually for CLI) will be sent.
     * @since 2.0.0
     */
    public PrintStream getOutput() {
        return output;
    }
}
