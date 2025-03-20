package com.example.Attendence.service;

import com.example.Attendence.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {
    @Autowired
    private RestTemplate restTemplate;
    public List<Employee> employeeList(){
        String userServiceUrl = "http://localhost:8080/api/user/getAll?status=1";  // Provide a valid status// Replace with actual URL

        // Call the external API
        ResponseEntity<Employee[]> response =
                restTemplate.getForEntity(userServiceUrl, Employee[].class);

        // Convert array to List
        System.out.println(" hh "+Arrays.asList(Objects.requireNonNull(response.getBody())));

        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }

    public List<Employee> employeeList(String header) {
        String userServiceUrl = "http://localhost:8080/api/user/getAll?status=1";

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", header); // Use the provided header
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create HttpEntity with headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Call the external API with headers
        ResponseEntity<Employee[]> response =
                restTemplate.exchange(userServiceUrl, HttpMethod.GET, entity, Employee[].class);

        // Convert array to List
        List<Employee> employeeList = Arrays.asList(Objects.requireNonNull(response.getBody()));

        System.out.println(" hh " + employeeList);
        return employeeList;
    }

}
