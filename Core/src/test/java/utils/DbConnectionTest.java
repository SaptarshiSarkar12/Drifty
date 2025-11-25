package utils;

import init.Environment;
import org.junit.jupiter.api.*;
import properties.FileState;
import properties.Program;
import support.Job;

import java.io.File;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@DisplayName("Database Connection Tests")
public class DbConnectionTest {
    static DbConnection dbConnection;
    static String fileName = "Know Your Java.mp4";
    static String fileUrl = "https://www.youtube.com/watch?v=v5Q7TC5u5Co";
    static String saveTargetPath = Paths.get(System.getProperty("user.home"), "Downloads", fileName).toString();
    static String downloadStartTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

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
        Assertions.assertTrue(dbFile.exists(), "Database file does not exist!");
        Assertions.assertTrue(dbFile.isFile(), "Database path is not a file!");
        Assertions.assertTrue(dbFile.canRead(), "Database file is not readable!");
        Assertions.assertTrue(dbFile.canWrite(), "Database file is not writable!");
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
            Assertions.assertTrue(foundTables.contains(expectedTable), "Expected table missing: " + expectedTable);
        }
    }

    @Test
    @DisplayName("Check if session table has expected columns")
    public void testSessionTableColumns() throws Exception {
        String[] expectedColumns = {"Id", "StartDate", "EndDate"};
        Set<String> foundColumns = getTableColumns("SESSION");
        for (String expectedColumn : expectedColumns) {
            Assertions.assertTrue(foundColumns.contains(expectedColumn), "Expected column missing: " + expectedColumn);
        }
    }

    @Test
    @DisplayName("Check if file table has expected columns")
    public void testFileTableColumns() throws Exception {
        String[] expectedColumns = {"Id", "FileName", "FileUrl", "DownloadUrl", "SaveTargetPath", "Size", "DownloadStartTime", "DownloadEndTime", "State", "SessionId"};
        Set<String> foundColumns = getTableColumns("FILE");
        for (String expectedColumn : expectedColumns) {
            Assertions.assertTrue(foundColumns.contains(expectedColumn), "Expected column missing: " + expectedColumn);
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
            Assertions.assertTrue(hasRecord, "Session record not found in the database!");
            Assertions.assertEquals(startDate, resultSet.getString("StartDate"), "Session start data does not match!");
        }
    }

    @Test
    @DisplayName("Check if file is recorded in the database")
    public void testFileRecorded() throws Exception {
        dbConnection.addFileRecord(fileName, fileUrl, fileUrl, saveTargetPath, downloadStartTime, 1);

        QueryResult result = runQuery("SELECT * FROM FILE WHERE FileName = ?", fileName);
        try (ResultSet resultSet = result.resultSet()) {
            while (resultSet.next()) {
                Assertions.assertEquals(fileName, resultSet.getString("FileName"), "File name does not match!");
                Assertions.assertEquals(fileUrl, resultSet.getString("FileUrl"), "File URL does not match!");
                Assertions.assertEquals(fileUrl, resultSet.getString("DownloadUrl"), "Download URL does not match!");
                Assertions.assertEquals(saveTargetPath, resultSet.getString("SaveTargetPath"), "Save target path does not match!");
                Assertions.assertEquals(downloadStartTime, resultSet.getString("DownloadStartTime"), "Download start time does not match!");
            }
        }
    }

    @Test
    @DisplayName("Check if file state can be updated")
    public void testFileStateUpdated() throws Exception {
        String downloadEndTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        dbConnection.updateFileState(fileUrl, saveTargetPath, fileName, FileState.COMPLETED, downloadStartTime, downloadEndTime, 89192775);
        QueryResult result = runQuery("SELECT * FROM FILE WHERE FileName = ?", fileName);
        try (ResultSet resultSet = result.resultSet()) {
            boolean hasRecord = resultSet.next();
            assert hasRecord : "File record not found in the database!";
            assert resultSet.getString("State").equals(FileState.COMPLETED.name()) : "File state was not updated correctly!";
            assert resultSet.getString("DownloadEndTime").equals(downloadEndTime) : "Download end time does not match!";
            assert resultSet.getInt("Size") == 89192775 : "File size does not match!";
        }
    }

    @Test
    @DisplayName("Check if the list of completed jobs is correct")
    public void testCompletedJobsList() throws Exception {
        // Add a completed job record to test
        dbConnection.addFileRecord(fileName, fileUrl, fileUrl, saveTargetPath, downloadStartTime, 1);
        dbConnection.updateFileState(fileUrl, saveTargetPath, fileName, FileState.COMPLETED, downloadStartTime, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")), 89192775);

        Collection<Job> completedJobs = dbConnection.getCompletedJobs();
        assert !completedJobs.isEmpty() : "Completed jobs collection is empty!";
        QueryResult result = runQuery("SELECT * FROM FILE WHERE State = ?", FileState.COMPLETED.name());
        Set<String> completedFileNames = new HashSet<>();
        try (ResultSet resultSet = result.resultSet()) {
            while (resultSet.next()) {
                completedFileNames.add(resultSet.getString("FileName"));
            }
        }
        for (Job job : completedJobs) {
            assert completedFileNames.contains(job.getFilename()) : "Completed job file name not found in database: " + job.getFilename();
        }
    }

    @Test
    @DisplayName("Check if the list of queued jobs is correct")
    public void testQueuedJobsList() throws Exception {
        // Add a queued job record to test
        boolean isFileQueued = addQueuedFile("queued_file.mp4", "https://example.com/queued_file.mp4", saveTargetPath);
        assert isFileQueued : "Failed to add queued file record!";

        Collection<Job> queuedJobs = dbConnection.getQueuedJobs();
        assert !queuedJobs.isEmpty() : "Queued jobs collection is empty!";
        QueryResult result = runQuery("SELECT * FROM FILE WHERE State = ?", FileState.QUEUED.name());
        Set<String> queuedFileNames = new HashSet<>();
        try (ResultSet resultSet = result.resultSet()) {
            while (resultSet.next()) {
                queuedFileNames.add(resultSet.getString("FileName"));
            }
        }
        for (Job job : queuedJobs) {
            assert queuedFileNames.contains(job.getFilename()) : "Queued job file name not found in database: " + job.getFilename();
        }
    }

    @Test
    @DisplayName("Check if queued files' deletion works")
    public void testQueuedFilesDeletion() throws Exception {
        // Add a queued file record to delete
        boolean isFileQueued = addQueuedFile("file_to_delete.mp4", "https://example.com/file_to_delete.mp4", saveTargetPath);
        assert isFileQueued : "Failed to add queued file record for deletion!";

        // Delete the queued file
        dbConnection.deleteQueuedFile("https://example.com/file_to_delete.mp4", saveTargetPath, "file_to_delete.mp4");

        // Verify the file record is deleted
        QueryResult result = runQuery("SELECT * FROM FILE WHERE FileName = ?", "file_to_delete.mp4");
        try (ResultSet resultSet = result.resultSet()) {
            boolean hasRecord = resultSet.next();
            assert !hasRecord : "Queued file was not deleted!";
        }
    }

    @Test
    @DisplayName("Check if file history deletion works")
    public void testFileHistoryDeletion() throws Exception {
        // Add a file record to delete
        boolean isFileQueued = addQueuedFile("files_to_delete.mp4", "https://example.com/files_to_delete.mp4", saveTargetPath);
        assert isFileQueued : "Failed to add queued file record for deletion!";
        // Mark the file as completed
        dbConnection.updateFileState("https://example.com/files_to_delete.mp4", saveTargetPath, "files_to_delete.mp4", FileState.COMPLETED, downloadStartTime, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")), 89192775);

        // Delete the file history
        dbConnection.deleteFilesHistory();

        // Verify all file records are deleted
        QueryResult result = runQuery("SELECT * FROM FILE WHERE State = ?", FileState.COMPLETED.name());
        try (ResultSet resultSet = result.resultSet()) {
            boolean hasRecord = resultSet.next();
            assert !hasRecord : "File history was not deleted!";
        }
    }

    @AfterAll
    @DisplayName("Clean up Test Environment")
    public static void cleanup() {
        // Delete the test database file
        File dbFile = new File(Program.get(Program.DATABASE_PATH));
        if (dbFile.exists()) {
            boolean deleted = dbFile.delete();
            assert deleted : "Failed to delete test database file!";
        } else {
            System.out.println("Test database file does not exist, nothing to delete.");
        }
    }

    private boolean addQueuedFile(String fileName, String fileUrl, String saveTargetPath) throws Exception {
        String downloadStartTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        dbConnection.addFileRecord(fileName, fileUrl, fileUrl, saveTargetPath, downloadStartTime, 1); // Assuming session ID 1 exists for the test
        QueryResult result = runQuery("SELECT * FROM FILE WHERE FileName = ?", fileName);
        try (ResultSet resultSet = result.resultSet()) {
            return resultSet.next();
        }
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
}

record QueryResult(ResultSet resultSet) {
}