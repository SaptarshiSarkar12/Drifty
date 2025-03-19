package utils;

import properties.FileState;
import properties.Program;
import support.Job;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

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
        String createSessionTableQuery = "CREATE TABLE IF NOT EXISTS SESSION (Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, StartDate TEXT NOT NULL, EndDate TEXT);";
        String createFileTableQuery = "CREATE TABLE IF NOT EXISTS FILE (Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, FileName TEXT NOT NULL, Url TEXT NOT NULL, SaveTargetPath TEXT NOT NULL, Size INTEGER, DownloadStartTime TEXT, DownloadEndTime TEXT, State INTEGER NOT NULL, SessionId INTEGER NOT NULL, FOREIGN KEY(SessionId) REFERENCES Session(Id));";
        try (PreparedStatement createSessionTableStatement = connection.prepareStatement(createSessionTableQuery);
             PreparedStatement createFileTableStatement = connection.prepareStatement(createFileTableQuery))
        {
            createSessionTableStatement.executeUpdate();
            createFileTableStatement.executeUpdate();
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
            } else {
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

    public int addFileRecordToQueue(String fileName, String url, String saveTargetPath, int sessionId) throws SQLException {
        String insertFileQuery = "INSERT INTO FILE (FileName, Url, SaveTargetPath, State, SessionId) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertFileQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, fileName);
            preparedStatement.setString(2, url);
            preparedStatement.setString(3, saveTargetPath);
            preparedStatement.setInt(4, FileState.QUEUED.ordinal());
            preparedStatement.setInt(5, sessionId);
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Failed to insert record into FILE table, no ID generated.");
            }
        }
    }

    public int addFileRecord(String fileName, String url, String saveTargetPath, String startDownloadingTime, int sessionId) throws SQLException {
        String insertFileQuery = "INSERT INTO FILE (FileName, Url, SaveTargetPath, DownloadStartTime, State, SessionId) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertFileQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, fileName);
            preparedStatement.setString(2, url);
            preparedStatement.setString(3, saveTargetPath);
            preparedStatement.setString(4, startDownloadingTime);
            preparedStatement.setInt(5, FileState.QUEUED.ordinal());
            preparedStatement.setInt(6, sessionId);
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Failed to insert record into FILE table, no ID generated.");
            }
        }
    }

    public void updateFileInfo(int fileId, FileState state, String endDownloadingTime, int size) throws SQLException {
        String updateFileQuery = "UPDATE FILE SET State = ?, DownloadEndTime = ?, Size = ? WHERE Id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateFileQuery)) {
            preparedStatement.setInt(1, state.ordinal());
            preparedStatement.setString(2, endDownloadingTime);
            preparedStatement.setInt(3, size);
            preparedStatement.setInt(4, fileId);
            preparedStatement.executeUpdate();
        }
    }

    public void updateFileState(String url, String saveTargetPath, String fileName, FileState state, String startDownloadingTime, String endDownloadingTime, int size) throws SQLException {
        String updateFileQuery = "UPDATE FILE SET State = ?, DownloadStartTime = ?, DownloadEndTime = ?, Size = ? WHERE Url = ? AND SaveTargetPath = ? AND FileName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateFileQuery)) {
            preparedStatement.setInt(1, state.ordinal());
            preparedStatement.setString(2, startDownloadingTime);
            preparedStatement.setString(3, endDownloadingTime);
            preparedStatement.setInt(4, size);
            preparedStatement.setString(5, url);
            preparedStatement.setString(6, saveTargetPath);
            preparedStatement.setString(7, fileName);
            preparedStatement.executeUpdate();
        }
    }

    public void updateFile(String fileName, String url, String saveTargetPath) throws SQLException {
        String updateFileQuery = "UPDATE FILE SET FileName = ?, SaveTargetPath = ? WHERE Url = ? AND State = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateFileQuery)) {
            preparedStatement.setString(1, fileName);
            preparedStatement.setString(2, saveTargetPath);
            preparedStatement.setString(3, url);
            preparedStatement.setInt(4, FileState.QUEUED.ordinal());
            preparedStatement.executeUpdate();
        }
    }

    public Collection<Job> getCompletedJobs() throws SQLException {
        String query = "SELECT Url, SaveTargetPath, FileName FROM FILE WHERE State = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, FileState.COMPLETED.ordinal());

            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<Job> jobs = new ArrayList<>();

            while (resultSet.next()) {
                String url = resultSet.getString("Url");
                String saveTargetPath = resultSet.getString("SaveTargetPath");
                String fileName = resultSet.getString("FileName");

                jobs.add(new Job(url, saveTargetPath, fileName, url));
            }
            return jobs;
        }
    }

    public Collection<Job> getQueuedJobs() throws SQLException {
        String query = "SELECT Url, SaveTargetPath, FileName FROM FILE WHERE State = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, FileState.QUEUED.ordinal());

            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<Job> jobs = new ArrayList<>();

            while (resultSet.next()) {
                String url = resultSet.getString("Url");
                String saveTargetPath = resultSet.getString("SaveTargetPath");
                String fileName = resultSet.getString("FileName");

                jobs.add(new Job(url, saveTargetPath, fileName, null));
            }
            return jobs;
        }
    }

    public void deleteQueuedFile(String url, String saveTargetPath, String fileName) throws SQLException {
        String deleteFileQuery = "DELETE FROM FILE WHERE Url = ? AND SaveTargetPath = ? AND FileName = ? AND State = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteFileQuery)) {
            preparedStatement.setString(1, url);
            preparedStatement.setString(2, saveTargetPath);
            preparedStatement.setString(3, fileName);
            preparedStatement.setInt(4, FileState.QUEUED.ordinal());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("File successfully deleted from the database.");
            }
        }
    }

    public void deleteFilesHistory() throws SQLException {
        String deleteFileQuery = "DELETE FROM FILE WHERE State IN (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteFileQuery)) {
            preparedStatement.setInt(1, FileState.COMPLETED.ordinal());
            preparedStatement.setInt(2, FileState.FAILED.ordinal());
            preparedStatement.setInt(3, FileState.PAUSED.ordinal());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Files successfully deleted from the database.");
            }
        }
    }
}
