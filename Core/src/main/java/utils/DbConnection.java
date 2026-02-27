package utils;

import properties.FileState;
import properties.Program;
import support.Job;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public final class DbConnection {
    private static Connection connection;
    private static volatile DbConnection dbConnection; // volatile keyword ensures that multiple threads handle the uniqueInstance variable correctly when it is being initialized to the DbConnection instance. This way we can avoid race conditions and ensure consistency in our database operations.

    private DbConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return;
        }
        connection = getConnection();
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:" + Program.get(Program.DATABASE_PATH);
        return DriverManager.getConnection(url);
    }

    public static DbConnection getInstance() throws SQLException {
        if (dbConnection == null) {
            // Synchronizing the block inside the if statement to make sure only one thread can enter at a time when the instance is null.
            synchronized (DbConnection.class) {
                if (dbConnection == null) {
                    dbConnection = new DbConnection();
                }
            }
        }
        return dbConnection;
    }

    public void createTables() throws SQLException {
        final String createSessionTableQuery = """
                CREATE TABLE IF NOT EXISTS SESSION (
                    Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    StartDate TEXT NOT NULL,
                    EndDate TEXT
                );
            """;
        final String createFileTableQuery = """
                CREATE TABLE IF NOT EXISTS FILE (
                    Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    FileName TEXT NOT NULL,
                    FileUrl TEXT NOT NULL,
                    DownloadUrl TEXT,
                    SaveTargetPath TEXT NOT NULL,
                    Size INTEGER,
                    DownloadStartTime TEXT,
                    DownloadEndTime TEXT,
                    State TEXT NOT NULL,
                    SessionId INTEGER NOT NULL,
                    FOREIGN KEY (SessionId) REFERENCES SESSION(Id)
                );
            """;
        try (PreparedStatement createSessionTableStatement = connection.prepareStatement(createSessionTableQuery);
             PreparedStatement createFileTableStatement = connection.prepareStatement(createFileTableQuery))
        {
            createSessionTableStatement.executeUpdate();
            createFileTableStatement.executeUpdate();
        }
catch (SQLException e) {
            throw new SQLException("Failed to create tables: " + e.getMessage());
        }
    }

    public int addSessionRecord(String startDate) throws SQLException {
        String insertSessionQuery = "INSERT INTO SESSION (StartDate) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSessionQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, startDate);
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
else {
                throw new SQLException("Failed to retrieve session ID.");
            }
        }
    }

    public void updateSessionEndDate(int sessionId, String endDate) throws SQLException {
        String updateSessionQuery = "UPDATE SESSION SET EndDate = ? WHERE Id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSessionQuery)) {
            preparedStatement.setString(1, endDate);
            preparedStatement.setInt(2, sessionId);
            preparedStatement.executeUpdate();
        }
    }

    public void addFileRecordToQueue(String fileName, String fileUrl, String downloadUrl, String saveTargetPath, int sessionId) throws SQLException {
        String insertFileQuery = "INSERT INTO FILE (FileName, FileUrl, DownloadUrl, SaveTargetPath, State, SessionId) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertFileQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, fileName);
            preparedStatement.setString(2, fileUrl);
            preparedStatement.setString(3, downloadUrl);
            preparedStatement.setString(4, saveTargetPath);
            preparedStatement.setString(5, FileState.QUEUED.name());
            preparedStatement.setInt(6, sessionId);
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                generatedKeys.getInt(1);
            }
else {
                throw new SQLException("Failed to insert record into FILE table, no ID generated.");
            }
        }
    }

    public int addFileRecord(String fileName, String fileUrl, String downloadUrl, String saveTargetPath, String startDownloadingTime, int sessionId) throws SQLException {
        String insertFileQuery = "INSERT INTO FILE (FileName, FileUrl, DownloadUrl, SaveTargetPath, DownloadStartTime, State, SessionId) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertFileQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, fileName);
            preparedStatement.setString(2, fileUrl);
            preparedStatement.setString(3, downloadUrl);
            preparedStatement.setString(4, saveTargetPath);
            preparedStatement.setString(5, startDownloadingTime);
            preparedStatement.setString(6, FileState.QUEUED.name());
            preparedStatement.setInt(7, sessionId);
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
else {
                throw new SQLException("Failed to insert record into FILE table, no ID generated.");
            }
        }
    }

    public void updateFileName(int fileId, String fileName) throws SQLException {
        String updateFileQuery = "UPDATE FILE SET FileName = ? WHERE Id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateFileQuery)) {
            preparedStatement.setString(1, fileName);
            preparedStatement.setInt(2, fileId);
            preparedStatement.executeUpdate();
        }
    }

    public void updateFileInfo(int fileId, FileState state, String endDownloadingTime, int size) throws SQLException {
        String updateFileQuery = "UPDATE FILE SET State = ?, DownloadEndTime = ?, Size = ? WHERE Id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateFileQuery)) {
            preparedStatement.setString(1, state.name());
            preparedStatement.setString(2, endDownloadingTime);
            preparedStatement.setInt(3, size);
            preparedStatement.setInt(4, fileId);
            preparedStatement.executeUpdate();
        }
    }

    public void updateFileState(String fileUrl, String saveTargetPath, String fileName, FileState state, String startDownloadingTime, String endDownloadingTime, int size) throws SQLException {
        String updateFileQuery = "UPDATE FILE SET State = ?, DownloadStartTime = ?, DownloadEndTime = ?, Size = ? WHERE FileUrl = ? AND SaveTargetPath = ? AND FileName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateFileQuery)) {
            preparedStatement.setString(1, state.name());
            preparedStatement.setString(2, startDownloadingTime);
            preparedStatement.setString(3, endDownloadingTime);
            preparedStatement.setInt(4, size);
            preparedStatement.setString(5, fileUrl);
            preparedStatement.setString(6, saveTargetPath);
            preparedStatement.setString(7, fileName);
            preparedStatement.executeUpdate();
        }
    }

    public void updateFile(String fileName, String fileUrl, String saveTargetPath) throws SQLException {
        String updateFileQuery = "UPDATE FILE SET FileName = ?, SaveTargetPath = ? WHERE FileUrl = ? AND State = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateFileQuery)) {
            preparedStatement.setString(1, fileName);
            preparedStatement.setString(2, saveTargetPath);
            preparedStatement.setString(3, fileUrl);
            preparedStatement.setString(4, FileState.QUEUED.name());
            preparedStatement.executeUpdate();
        }
    }

    public Collection<Job> getCompletedJobs() throws SQLException {
        String query = "SELECT FileUrl, DownloadUrl, SaveTargetPath, FileName FROM FILE WHERE State = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, FileState.COMPLETED.name());

            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<Job> jobs = new ArrayList<>();

            while (resultSet.next()) {
                String fileUrl = resultSet.getString("FileUrl");
                String downloadUrl = resultSet.getString("DownloadUrl");
                String saveTargetPath = resultSet.getString("SaveTargetPath");
                String fileName = resultSet.getString("FileName");

                jobs.add(new Job(fileUrl, saveTargetPath, fileName, downloadUrl));
            }
            return jobs;
        }
    }

    public Collection<Job> getQueuedJobs() throws SQLException {
        String query = "SELECT FileUrl, DownloadUrl, SaveTargetPath, FileName FROM FILE WHERE State = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, FileState.QUEUED.name());

            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<Job> jobs = new ArrayList<>();

            while (resultSet.next()) {
                String fileUrl = resultSet.getString("FileUrl");
                String downloadUrl = resultSet.getString("DownloadUrl");
                String saveTargetPath = resultSet.getString("SaveTargetPath");
                String fileName = resultSet.getString("FileName");

                if (!Objects.equals(downloadUrl, fileUrl)) {
                    jobs.add(new Job(fileUrl, saveTargetPath, fileName, downloadUrl));
                }
else {
                    jobs.add(new Job(fileUrl, saveTargetPath, fileName, null));
                }
            }
            return jobs;
        }
    }

    public void deleteQueuedFile(String fileUrl, String saveTargetPath, String fileName) throws SQLException {
        String deleteFileQuery = "DELETE FROM FILE WHERE FileUrl = ? AND SaveTargetPath = ? AND FileName = ? AND State = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteFileQuery)) {
            preparedStatement.setString(1, fileUrl);
            preparedStatement.setString(2, saveTargetPath);
            preparedStatement.setString(3, fileName);
            preparedStatement.setString(4, FileState.QUEUED.name());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("File successfully deleted from the database.");
            }
        }
    }

    public void deleteFilesHistory() throws SQLException {
        String deleteFileQuery = "DELETE FROM FILE WHERE State IN (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteFileQuery)) {
            preparedStatement.setString(1, FileState.COMPLETED.name());
            preparedStatement.setString(2, FileState.FAILED.name());
            preparedStatement.setString(3, FileState.PAUSED.name());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Files successfully deleted from the database.");
            }
        }
    }
}
