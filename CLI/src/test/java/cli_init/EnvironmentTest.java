package cli_init;

import cli_utils.MessageBroker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test CLI Environment")
class EnvironmentTest {
    private static final MessageBroker M = new MessageBroker(System.out);

    @Test
    @DisplayName("Test MessageBroker instance sent to Environment")
    void testMessageBrokerInstance() {
        Environment.setMessageBroker(M);
        Assertions.assertEquals(M, Environment.getMessageBroker());
        Assertions.assertInstanceOf(MessageBroker.class, Environment.getMessageBroker());
    }
}