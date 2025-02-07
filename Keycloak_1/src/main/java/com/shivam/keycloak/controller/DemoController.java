package com.shivam.keycloak.controller;

import com.shivam.keycloak.model.Employee;
import com.shivam.keycloak.repository.EmployeeRepository;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Data
@RestController
@RequestMapping("/employees")
public class DemoController {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public DemoController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @PostMapping("/insert")
    @PreAuthorize("hasRole('client_admin')")
    public Employee createEmployee(@Valid @RequestBody Employee employee, @AuthenticationPrincipal Jwt jwt) {
        String keycloakUserId = jwt.getClaimAsString("sub");
      //  employee.setKeycloakUserId(keycloakUserId);
        return employeeRepository.save(employee); // Direct repository call
    }

    @GetMapping("/read")
    @PreAuthorize("hasRole('client_user') or hasRole('client_admin')")
    public List<Employee> readEmployeeData() {
        return employeeRepository.findAll(); // Direct repository call
    }

    @GetMapping("/read/{id}")
    @PreAuthorize("hasRole('client_user') or hasRole('client_admin')")
    public ResponseEntity<Employee> readEmployee(@PathVariable long id) {
        Optional<Employee> employee = employeeRepository.findById(id); // Direct repository call
        return employee.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('client_admin')")
    public ResponseEntity<String> deleteEmployee(@PathVariable long id) {
        if (employeeRepository.existsById(id)) { // Direct repository call
            employeeRepository.deleteById(id); // Direct repository call
            return ResponseEntity.ok("Data deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No data found with the given ID");
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('client_admin')")
    public ResponseEntity<String> updateEmployee(@PathVariable long id,
                                                 @RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String email,
                                                 @RequestParam(required = false) String address) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id); // Direct repository call
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            if (name != null) employee.setName(name);
            if (email != null) employee.setEmail(email);
            if (address != null) employee.setAddress(address);
            employeeRepository.save(employee); // Direct repository call
            return ResponseEntity.ok("Data updated successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No data found with the given ID");
    }
}
