package com.shivam.keycloak.controller;

import com.shivam.keycloak.model.Employee;
import com.shivam.keycloak.service.Dbfunctions;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Data
@RestController
@RequestMapping("/employees")
public class DemoController {

    private final Dbfunctions dbfunctions;

    @Autowired
    public DemoController(Dbfunctions dbfunctions) {
        this.dbfunctions = dbfunctions;
    }

    @PostMapping("/insert")
    @PreAuthorize("hasRole('client_admin')")
    public Employee createEmployee(@Valid @RequestBody Employee employee, @AuthenticationPrincipal Jwt jwt) {
        String keycloakUserId = jwt.getClaimAsString("sub");
        employee.setKeycloakUserId(keycloakUserId);
        return dbfunctions.saveEmployee(employee);
    }

    @GetMapping("/read")
    @PreAuthorize("hasRole('client_user') or hasRole('client_admin')")
    public List<Employee> readEmployeeData() {
        return dbfunctions.getAllEmployees();
    }

    @GetMapping("/read/{id}")
    @PreAuthorize("hasRole('client_user') or hasRole('client_admin')")
    public ResponseEntity<Employee> readEmployee(@PathVariable long id) {
        Optional<Employee> employee = dbfunctions.getEmployeeById(id);
        return employee.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('client_admin')")
    public ResponseEntity<String> deleteEmployee(@PathVariable long id) {
        return ResponseEntity.ok(dbfunctions.deleteEmployee(id));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('client_admin')")
    public ResponseEntity<String> updateEmployee(@PathVariable long id,
                                                 @RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String email,
                                                 @RequestParam(required = false) String address) {
        return ResponseEntity.ok(dbfunctions.updateEmployee(id, name, email, address));
    }
}
