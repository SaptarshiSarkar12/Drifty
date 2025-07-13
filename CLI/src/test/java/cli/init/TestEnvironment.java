package cli.init;

import cli.utils.MessageBroker;
import cli.utils.Utility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

public class TestEnvironment {
    public static Utility utility;

    @BeforeAll
    @DisplayName("Set up Environment for Tests")
    public static void setUp() {
        MessageBroker msgBroker = new MessageBroker(System.out);
        Environment.setCLIMessageBroker(msgBroker);
        utility = new Utility();
        Environment.initializeEnvironment();
    }
}
