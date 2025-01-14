package com.shivam.keycloak.service;

import com.shivam.keycloak.model.Employee;
import com.shivam.keycloak.repository.EmployeeRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Setter
@Service
public class Dbfunctions {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public Dbfunctions(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(long id) {
        return employeeRepository.findById(id);
    }

    public String deleteEmployee(long id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return "Data deleted successfully";
        }
        return "No data found with the given ID";
    }

    public String updateEmployee(long id, String name, String email, String address) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    if (name != null) employee.setName(name);
                    if (email != null) employee.setEmail(email);
                    if (address != null) employee.setAddress(address);
                    employeeRepository.save(employee);
                    return "Data updated successfully";
                })
                .orElse("No data found with the given ID");
    }
}
