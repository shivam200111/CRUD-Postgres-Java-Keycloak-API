package com.shivam.keycloak;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Setter
@Service
public class Dbfunctions {
    private final DataSource dataSource;
    private final EmployeeRepository employeeRepository;
    @Autowired
    public Dbfunctions(DataSource dataSource, EmployeeRepository employeeRepository) {
        this.dataSource = dataSource;
        this.employeeRepository = employeeRepository;
    }
    public void createTable(Connection conn) {
        String query = "CREATE TABLE IF NOT EXISTS Employee3 (" +
                "id BIGSERIAL PRIMARY KEY, " +
                "keycloak_user_id VARCHAR(255) UNIQUE, " +
                "name VARCHAR(255) NOT NULL, " +
                "email VARCHAR(255) UNIQUE NOT NULL, " +
                "address VARCHAR(255) NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        try (Statement statement = conn.createStatement()) {
            statement.execute(query);
            System.out.println("Table created successfully");
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    public Connection connect_to_db(String dbname, String user, String pass) {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + dbname, user, pass);
            System.out.println("Connected to database");
        } catch (Exception e) {
            System.out.println(e);
        }
        return conn;
    }

    public void insertData(Connection conn, String keycloakUserId, String name, String email, String address, String password) {
        String query = "INSERT INTO Employee3 (keycloak_user_id, name, email, address, password) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, keycloakUserId);
            pstmt.setString(2, name);
            pstmt.setString(3, email);
            pstmt.setString(4, address);
            pstmt.setString(5, password);  // Include password field

            pstmt.executeUpdate();
            System.out.println("Data inserted successfully");
        } catch (SQLException e) {
            System.out.println("Error inserting data: " + e.getMessage());
        }
    }

    public List<Employee> readData(Connection conn, String tableName) {
        List<Employee> employees = new ArrayList<>();
        if (tableName == null) {
            throw new NullPointerException("Table name cannot be null");
        }
        String query = "SELECT * FROM " + tableName;
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getLong("id"));
                employee.setKeycloakUserId(rs.getString("keycloak_user_id"));
                employee.setName(rs.getString("name"));
                employee.setEmail(rs.getString("email"));
                employee.setAddress(rs.getString("address"));
                employee.setPassword(rs.getString("password"));
                employee.setCreatedAt(rs.getTimestamp("created_at"));
                employee.setUpdatedAt(rs.getTimestamp("updated_at"));
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.out.println("Error reading data: " + e.getMessage());
        }
        return employees;
    }

    public List<Employee> readDatafromid(Connection conn,long id, String tableName) {
        List<Employee> employees = new ArrayList<>();

        if (tableName == null) {
            throw new NullPointerException("Table name cannot be null");
        }
        String query = "SELECT * FROM " + tableName + " WHERE id = " + id;
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getLong("id"));
                employee.setKeycloakUserId(rs.getString("keycloak_user_id"));
                employee.setName(rs.getString("name"));
                employee.setEmail(rs.getString("email"));
                employee.setAddress(rs.getString("address"));
                employee.setPassword(rs.getString("password"));
                employee.setCreatedAt(rs.getTimestamp("created_at"));
                employee.setUpdatedAt(rs.getTimestamp("updated_at"));
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.out.println("Error reading data: " + e.getMessage());
        }
        return employees;
    }
    public String deleteData(Connection conn, long id) {
        String query = "DELETE FROM Employee3 WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return "Data deleted successfully";
            } else {
                return "No data found with the given ID";
            }
        } catch (SQLException e) {
            return "Error deleting data: " + e.getMessage();
        }
    }
    public String updateData(Connection conn, long id, String name, String email, String address) {
        String query = "UPDATE Employee3 SET name = ?, email = ?, address = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);  // Set email field
            pstmt.setString(3, address);
            pstmt.setLong(4, id);  // Correct column for id

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return "Data updated successfully";
            } else {
                return "No data found with the given ID";
            }
        } catch (SQLException e) {
            return "Error updating data: " + e.getMessage();
        }
    }
}




