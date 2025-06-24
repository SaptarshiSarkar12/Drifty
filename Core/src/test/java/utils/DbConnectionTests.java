package utils;

import init.Environment;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import properties.FileState;
import properties.Program;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@DisplayName("Database Connection Tests")
public class DbConnectionTests {
    static DbConnection dbConnection;

    @BeforeAll
    @DisplayName("Initialize Environment")
    public static void setup() throws SQLException {
        Program.setDatabaseName("drifty_test.db");
        MessageBroker messageBroker = new MessageBroker();
        Environment.setMessageBroker(messageBroker);
        Environment.initializeEnvironment();
        dbConnection = DbConnection.getInstance();
    }

    @Test
    @DisplayName("Test if SQLite file exists")
    public void testDatabaseConnection() {
        File dbFile = new File(Program.get(Program.DATABASE_PATH));
        assert dbFile.exists() : "Database file does not exist!";
        assert dbFile.isFile() : "Database path is not a file!";
        assert dbFile.canRead() : "Database file is not readable!";
        assert dbFile.canWrite() : "Database file is not writable!";
    }

    @Test
    @DisplayName("Check if all tables exist")
    public void testDatabaseTablesExist() throws Exception {
        String[] expectedTables = {"SESSION", "FILE"};
        Set<String> foundTables = new HashSet<>();

        QueryResult result = runQuery("SELECT name FROM sqlite_master WHERE type='table'");
        try (ResultSet resultSet = result.resultSet()) {
            while (resultSet.next()) {
                foundTables.add(resultSet.getString("name"));
            }
        }
        for (String expectedTable : expectedTables) {
            assert foundTables.contains(expectedTable) : "Expected table missing: " + expectedTable;
        }
    }

    @Test
    @DisplayName("Check if session table has expected columns")
    public void testSessionTableColumns() throws Exception {
        String[] expectedColumns = {"Id", "StartDate", "EndDate"};
        Set<String> foundColumns = getTableColumns("SESSION");
        for (String expectedColumn : expectedColumns) {
            assert foundColumns.contains(expectedColumn) : "Expected column missing: " + expectedColumn;
        }
    }

    @Test
    @DisplayName("Check if file table has expected columns")
    public void testFileTableColumns() throws Exception {
        String[] expectedColumns = {"Id", "FileName", "FileUrl", "DownloadUrl", "SaveTargetPath", "Size", "DownloadStartTime", "DownloadEndTime", "State", "SessionId"};
        Set<String> foundColumns = getTableColumns("FILE");
        for (String expectedColumn : expectedColumns) {
            assert foundColumns.contains(expectedColumn) : "Expected column missing: " + expectedColumn;
        }
    }

    @Test
    @DisplayName("Check if session is recorded in the database")
    public void testSessionRecorded() throws Exception {
        String startDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        dbConnection.addSessionRecord(startDate);
        QueryResult result = runQuery("SELECT * FROM SESSION WHERE StartDate = ?", startDate);
        try (ResultSet resultSet = result.resultSet()) {
            boolean hasRecord = resultSet.next();
            assert hasRecord : "Session record not found in the database!";
            assert resultSet.getString("StartDate").equals(startDate) : "Session start data does not match!";
        }
        // Clean up the session record after the test
        runUpdate("DELETE FROM SESSION WHERE StartDate = ?", startDate);
    }

    @Test
    @DisplayName("Check if file is recorded in the database")
    public void testFileRecorded() throws Exception {
        String fileName = "testFile.txt";
        String fileUrl = "http://example.com/testFile.txt";
        String downloadUrl = "http://example.com/download/testFile.txt";
        String saveTargetPath = "/downloads/testFile.txt";
        String downloadStartTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        int sessionId = 1;

        dbConnection.addFileRecord(fileName, fileUrl, downloadUrl, saveTargetPath, downloadStartTime, sessionId);

        QueryResult result = runQuery("SELECT * FROM FILE WHERE FileName = ?", fileName);
        try (ResultSet resultSet = result.resultSet()) {
            boolean hasRecord = resultSet.next();
            assert hasRecord : "File record not found in the database!";
            assert resultSet.getString("FileName").equals(fileName) : "File name does not match!";
            assert resultSet.getString("FileUrl").equals(fileUrl) : "File URL does not match!";
            assert resultSet.getString("DownloadUrl").equals(downloadUrl) : "Download URL does not match!";
            assert resultSet.getString("SaveTargetPath").equals(saveTargetPath) : "Save target path does not match!";
            assert resultSet.getString("DownloadStartTime").equals(downloadStartTime) : "Download start time does not match!";
            assert resultSet.getString("State").equals(FileState.QUEUED.name()) : "State does not match!";
            assert resultSet.getInt("SessionId") == sessionId : "Session ID does not match!";
        }

        // Clean up the file record after the test
        runUpdate("DELETE FROM FILE WHERE FileName = ?", fileName);
    }

    private Set<String> getTableColumns(String tableName) throws Exception {
        Set<String> columns = new HashSet<>();
        QueryResult result = runQuery("PRAGMA table_info(" + tableName + ")");
        try (ResultSet resultSet = result.resultSet()) {
            while (resultSet.next()) {
                columns.add(resultSet.getString("name"));
            }
        }
        return columns;
    }

    private QueryResult runQuery(String query, Object... parameters) throws Exception {
        String connectionUrl = "jdbc:sqlite:" + Program.get(Program.DATABASE_PATH);
        Connection connection = DriverManager.getConnection(connectionUrl);
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        for (int i = 0; i < parameters.length; i++) {
            preparedStatement.setObject(i + 1, parameters[i]);
        }
        return new QueryResult(preparedStatement.executeQuery());
    }

    private void runUpdate(String query, Object... parameters) throws Exception {
        String connectionUrl = "jdbc:sqlite:" + Program.get(Program.DATABASE_PATH);
        Connection connection = DriverManager.getConnection(connectionUrl);
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        for (int i = 0; i < parameters.length; i++) {
            preparedStatement.setObject(i + 1, parameters[i]);
        }
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }
}

record QueryResult(ResultSet resultSet) {
}