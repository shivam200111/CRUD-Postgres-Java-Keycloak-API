package com.shivam.keycloak;
import org.springframework.security.access.prepost.PreAuthorize;


import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Data
@RestController
@RequestMapping("/employees")
public class DemoController {

    private final Dbfunctions dbfunctions;

    @Autowired
    public DemoController(Dbfunctions dbfunctions) {
        this.dbfunctions = dbfunctions;
    }

    // Create Employee
    @PostMapping("/insert")
    @PreAuthorize("hasRole('client_admin')")
    public Employee createEmployee(@Valid @RequestBody Employee employee, @AuthenticationPrincipal Jwt jwt) {
        String keycloakUserId = jwt.getClaimAsString("sub");
        employee.setKeycloakUserId(keycloakUserId);

        Connection conn = dbfunctions.connect_to_db("learn1", "postgres", "roots");
        dbfunctions.insertData(conn, employee.getKeycloakUserId(), employee.getName(), employee.getEmail(), employee.getAddress(), employee.getPassword());
        return employee;
    }

    // Read all Employees
    @GetMapping("/read")
    @PreAuthorize("hasRole('client_user') or hasRole('client_admin')")
    public List<Employee> readEmployeeData() {
        Connection conn = dbfunctions.connect_to_db("learn1", "postgres", "roots");
        return dbfunctions.readData(conn, "Employee3");
    }

    // Read Employee by ID
    @GetMapping("/read/{id}")
    @PreAuthorize("hasRole('client_user')or hasRole('client_admin')")
    public Employee readEmployee(@PathVariable long id) {
        Connection conn = dbfunctions.connect_to_db("learn1", "postgres", "roots");
        List<Employee> employees = dbfunctions.readDatafromid(conn, id, "Employee3");
        return employees.isEmpty() ? null : employees.get(0);
    }

    // Delete Employee
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('client_admin')")
    public ResponseEntity<String> deleteEmployee(@PathVariable long id) {
        Connection conn = null;
        try {
            conn = dbfunctions.connect_to_db("learn1", "postgres", "roots");
            String message = dbfunctions.deleteData(conn, id);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting employee: " + e.getMessage());
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.out.println("Error closing connection: " + ex.getMessage());
            }
        }
    }

    // Update Employee
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('client_admin')")
    public ResponseEntity<String> updateEmployee(@PathVariable long id,
                                                 @RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String email,
                                                 @RequestParam(required = false) String address) {
        Connection conn = dbfunctions.connect_to_db("learn1", "postgres", "roots");
        if (conn == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to establish database connection");
        }
        try {
            List<Employee> employees = dbfunctions.readDatafromid(conn, id, "Employee3");
            if (employees.isEmpty()) {
                return ResponseEntity.ok("No data found with the given ID");
            }
            Employee employee = employees.get(0);
            if (name != null) employee.setName(name);
            if (email != null) employee.setEmail(email);
            if (address != null) employee.setAddress(address);

            String message = dbfunctions.updateData(conn, id, employee.getName(), employee.getEmail(), employee.getAddress());
            return ResponseEntity.ok(message);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating employee: " + e.getMessage());
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.out.println("Error closing connection: " + ex.getMessage());
            }
        }
    }
}


