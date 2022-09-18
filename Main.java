import java.util.Scanner;
import java.sql.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("\u001B[32m" + "==================================" + "\u001B[0m");
        System.out.println("\u001B[34m\t" + "DRIFTY\t" + "\u001B[0m");
        System.out.println("\u001B[32m" + "==================================" + "\u001B[0m");

        Scanner sc = new Scanner(System.in);
        Connection connection = null;
        try {
            // Connecting to database
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/data", "datauser", "datauser");

            Statement statement;
            statement = connection.createStatement();
            ResultSet resultSet;
            resultSet = statement.executeQuery("select * from data");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
