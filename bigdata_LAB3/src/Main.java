import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        /* Load JDBC Driver. */
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        String url = "jdbc:postgresql://localhost/chloetoledano";
        String user = "chloetoledano"; // replace with your database username
        String pass = ""; // replace with your database password
        Connection connexion = null;

        try {
            connexion = DriverManager.getConnection(url, user, pass);
            System.out.println("Database Connected");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connexion != null) {
                try {
                    connexion.close();
                } catch (SQLException ignore) {
                    ignore.printStackTrace();
                }
            }
    }
    }

    public static void displayTable(Connection connection, String tableName) {
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Get metadata about the table for column names
            DatabaseMetaData metaData = connection.getMetaData();
            resultSet = metaData.getColumns(null, null, tableName, null);
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                System.out.print(columnName + " | ");
            }
            System.out.println();

            // Now, get the data from the table
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + tableName);
            while (resultSet.next()) {
                int totalColumns = resultSet.getMetaData().getColumnCount();
                for (int i = 1; i <= totalColumns; i++) {
                    System.out.print(resultSet.getString(i) + " | ");
                }
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources in the finally block to ensure they are always closed
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void displayDepartment (Connection connexion) throws SQLException {
            Statement statement = connexion.createStatement();
            ResultSet resultat = statement.executeQuery("SELECT deptno, dname, loc FROM dept");

            while (resultat.next()) {
                int deptno = resultat.getInt("deptno");
                String dname = resultat.getString("dname");
                String loc = resultat.getString("loc");

                System.out.println("Department " + deptno + " is for " + dname + " and located in " + loc);
            }
            resultat.close();
            statement.close();
        }

    public static void moveDepartment (Connection connexion,int empno, int newDeptno) throws SQLException {
        PreparedStatement statement = connexion.prepareStatement("UPDATE emp SET deptno = ? WHERE empno = ?");
        statement.setInt(1, newDeptno);
        statement.setInt(2, empno);

        int rowsAffected = statement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Employee " + empno + " moved to department " + newDeptno);
        } else {
            System.out.println("No rows affected.");
        }
        statement.close();
    }

    public static void findEmployeeByName (Connection connection){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter first name:");
        String firstName = sc.next();
        System.out.println("Enter last name:");
        String lastName = sc.next();

        PreparedStatement preparedStatement = null;
        ResultSet results = null;

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM emp WHERE efirst = ? AND ename = ?");
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);

            results = preparedStatement.executeQuery();

            ResultSetMetaData metaData = results.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Print column names
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + " | ");
            }
            System.out.println();

            // Print rows
            while (results.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(results.getString(i) + " | ");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (results != null) results.close();
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

