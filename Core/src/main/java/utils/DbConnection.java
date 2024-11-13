package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class DbConnection {
    private static Connection connection;
    private static DbConnection dbConnection;

    private DbConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return;
        }
        connection = getConnection();
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:drifty.db";
        return DriverManager.getConnection(url);
    }

    public static DbConnection getInstance() throws SQLException {
        if (dbConnection != null) {
            return dbConnection;
        }
        dbConnection = new DbConnection();
        return dbConnection;
    }

    public void createTables() throws SQLException {
        String createSessionTableQuery = "CREATE TABLE IF NOT EXISTS SESSION (Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, StartDate TEXT NOT NULL, EndDate TEXT);";
        String createFileTableQuery = "CREATE TABLE IF NOT EXISTS FILE (Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, Url TEXT NOT NULL, SaveTargetPath TEXT NOT NULL, Size INTEGER, DownloadStartTime TEXT, DownloadEndTime TEXT, State INTEGER NOT NULL, SessionId INTEGER NOT NULL, FOREIGN KEY(SessionId) REFERENCES Session(Id));";
        PreparedStatement createSessionTableStatement = connection.prepareStatement(createSessionTableQuery);
        createSessionTableStatement.executeUpdate();
        PreparedStatement createFileTableStatement = connection.prepareStatement(createFileTableQuery);
        createFileTableStatement.executeUpdate();
    }

//    public void Create() throws SQLException {
//        //connection.createStatement().executeQuery("");
//    }
//
//    public <T> List<T> Read(String sql) throws SQLException {
//        ResultSet resultSet = connection.createStatement().executeQuery(sql);
//        while (resultSet.next()) {
//
//        }
//    }
//
//    public void Update() throws SQLException {
//
//    }
//
//    public void Delete() throws SQLException {
//
//    }
}
