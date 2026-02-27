package cli.utils;

import cli.init.Environment;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.util.Scanner;

import static cli.support.Constants.ENTER_Y_OR_N;

public class Utility extends utils.Utility {
    private static final Scanner SC = ScannerFactory.getInstance();

    public boolean yesNoValidation(String input, String printMessage, boolean isWarning) {
        while (input.isEmpty()) {
            Environment.getMessageBroker().msgInputError(ENTER_Y_OR_N, true);
            msgBroker.msgLogError(ENTER_Y_OR_N);
            if (isWarning) {
                Environment.getMessageBroker().msgHistoryWarning(printMessage, false);
            }
else {
                Environment.getMessageBroker().msgInputInfo(printMessage, false);
            }
            input = SC.nextLine().toLowerCase();
        }
        char choice = input.charAt(0);
        if (choice == 'y') {
            return true;
        }
else if (choice == 'n') {
            return false;
        }
else {
            Environment.getMessageBroker().msgInputError("Invalid input!", true);
            msgBroker.msgLogError("Invalid input!");
            if (isWarning) {
                Environment.getMessageBroker().msgHistoryWarning(printMessage, false);
            }
else {
                Environment.getMessageBroker().msgInputInfo(printMessage, false);
            }
            input = SC.nextLine().toLowerCase();
            return yesNoValidation(input, printMessage, isWarning);
        }
    }

    public static Yaml getYamlParser() {
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setAllowDuplicateKeys(false);
        loaderOptions.setAllowRecursiveKeys(false);
        loaderOptions.setProcessComments(false);
        Yaml yamlParser = new Yaml(new SafeConstructor(loaderOptions));
        msgBroker.msgLogInfo("YAML parser initialized successfully");
        return yamlParser;
    }
}
