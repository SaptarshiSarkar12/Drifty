package cli.init;

import cli.utils.MessageBroker;

public class Environment extends init.Environment {
    private static MessageBroker msgBroker;

    public static void setMessageBroker(MessageBroker messageBroker) {
        msgBroker = messageBroker;
        init.Environment.setMessageBroker(messageBroker);
    }

    public static MessageBroker getMessageBroker() {
        return msgBroker;
    }
}
