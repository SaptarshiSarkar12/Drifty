module CLI {
    requires Core;
    requires org.yaml.snakeyaml;
    requires org.apache.commons.io;
    requires com.google.gson;
    requires java.sql;

    exports cli.init; // Exporting the "cli.init" package (in the "test" directory) for CLI Tests
}