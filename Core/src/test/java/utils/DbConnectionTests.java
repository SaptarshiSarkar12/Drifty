package utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import properties.Program;

import java.io.File;

@DisplayName("Database Connection Tests")
public class DbConnectionTests {
    @Test
    @DisplayName("Test if SQLite file exists")
    public void testDatabaseConnection() {
        File dbFile = new File(Program.get(Program.DATABASE_PATH));
        assert dbFile.exists() : "Database file does not exist!";
        assert dbFile.isFile() : "Database path is not a file!";
        assert dbFile.canRead() : "Database file is not readable!";
        assert dbFile.canWrite() : "Database file is not writable!";
    }
}
