package cli_init;

import cli_utils.MessageBroker;

public class Environment extends init.Environment {
    private static MessageBroker M;

    public static void setMessageBroker(MessageBroker messageBroker) {
        M = messageBroker;
        init.Environment.setMessageBroker(messageBroker);
    }

    public static MessageBroker getMessageBroker() {
        return M;
    }
}
