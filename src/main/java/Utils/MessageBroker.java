package Utils;

import CLI.DriftyCLI;
import Enums.MessageCategory;
import Enums.Mode;
import Enums.MessageType;
import GUI.Forms.Main;

import java.io.PrintStream;

/**
 * This class allows the Backend to <b>send its outputs to the Command Line Interface (CLI) and Graphical User Interface (GUI) versions of Drifty</b>.
 *
 * @version 2.0.0
 * @see Backend.Drifty
 * @see Main
 * @see DriftyCLI
 * @since 2.0.0
 */
public class MessageBroker {
    /**
     * Logger instance of Drifty
     */
    Logger logger;
    /**
     * An output stream where the output messages are sent. It is usually used for Drifty CLI
     */
    PrintStream output;

    /**
     * This is the <b>constructor of message broker</b> that configures it to be able to work with <b>Command Line Interface (CLI) Functionalities</b>.
     *
     * @param consoleOutput The Output Stream where the message broker will give its outputs received from CLI.
     * @since 2.0.0
     */
    public MessageBroker(PrintStream consoleOutput) {
        output = consoleOutput;
        logger = Logger.getInstance();
    }

    /**
     * This is the <b>constructor of message broker</b> that configures it to be able to work with <b>Graphical User Interface (GUI) Functionalities</b>.
     *
     * @since 2.0.0
     */
    public MessageBroker() {
        logger = Logger.getInstance();
    }

    /**
     * This method <b>sends as well as logs (in the log file) the message containing the output</b> to the output areas for <i>both CLI and GUI versions</i>. It also <b>sets the colour code for the output</b> only in case of <i>GUI version of Drifty</i>.
     *
     * @param message The output message that needs to be shown to the user.
     * @param messageType The type of the output message to be shown to the user. Possible values : <b>INFO</b>, <b>WARN</b> and <b>ERROR</b>.
     * @param messageCategory The category of the output message to be shown to the user. Possible values : <b>link</b>, <b>directory</b>, <b>download</b> and <b>Filename</b>.
     * @since 2.0.0
     */
    public void sendMessage(String message, MessageType messageType, MessageCategory messageCategory) {
        if (Mode.isCLI()) {
            if (!messageCategory.equals(MessageCategory.LOG))
                output.println(message);
            logger.log(messageType, message);
        } else if (Mode.getIsGUILoaded()) {
            logger.log(messageType, message);
            Main.setMessage(message, messageType, messageCategory);
        }
    }

    /**
     * This method returns the <b>output stream</b> where the outputs (usually for CLI) will be sent.
     *
     * @return The Output Stream where the outputs (usually for CLI) will be sent.
     * @since 2.0.0
     */
    public PrintStream getOutput() {
        return output;
    }
}
