package com.example.Attendence.controller;
import com.example.Attendence.model.Employee;
import com.example.Attendence.model.LocalSetting;
import com.example.Attendence.model.Position;
import com.example.Attendence.repository.LocalSettingRepository;
import com.example.Attendence.repository.PositionRepository;
import com.example.Attendence.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/positionSetting") // Base URL for all endpoints in this controller
public class PositionController {
    // Inject the URL from application.properties or environment variable
    @Value("${user.service.base.url}")
    private String userServiceBaseUrl;
    @Autowired
    UserService userService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PositionRepository positionRepository;
    @PostMapping("/insert")
    public Position insertEmployee(@RequestBody Position employeeData) {
      //  System.out.println("Received Data: " + employeeData); // Debugging
        // Save the employee data to the database
        Position localSetting = positionRepository.save(employeeData);
        // Return the saved employee data as a response
        return localSetting;
    }
    @GetMapping("/getAll")
    public List<Position> retrieveData(){

        //return repositoryManager.getUserGlobalSettingRepository().findAllByStatus("1");
        return  (positionRepository.findAllByStatus("1"));
    }

    @PostMapping("/updateSortingPosition")
    public ResponseEntity<String> updateSorting(@RequestBody Employee singleRowData, HttpServletRequest request) {

        List<Employee> employeeList =userService.employeeList(request.getHeader("Authorization"));
        List<Employee> outresult=new ArrayList<>();

        // Process the list (e.g., update position)
        List<Employee> updatedEmployees = new ArrayList<>();
        for (Employee emp : employeeList) {
          // System.out.println(emp.toString());
        }
        for(int i=0;i<employeeList.size();i++)
        {
            if(i==(Integer.parseInt(singleRowData.getPosition())-1))
            {
               // System.out.println(Integer.parseInt(singleRowData.getPosition())-1);
                outresult.add(new Employee(singleRowData.getIdNumber(),singleRowData.getName(),null,null,null,null,singleRowData.getPosition(),null,null,null,null));
                // update according to singleRowData.getEmployeeId(),singleRowData.getName().
                // Correct API call to get employee by ID & Name
               // repositoryManager.getRegistrationRepository().updatePosition(singleRowData.getFirstId(),singleRowData.getFirstName(),"1",singleRowData.getPosition());
               // System.out.println("number"+singleRowData.getIdNumber() +" "+singleRowData.getName());
                String idNumber = URLEncoder.encode(singleRowData.getIdNumber(), StandardCharsets.UTF_8);
              //  System.out.println("number"+idNumber);
                String name = URLEncoder.encode(singleRowData.getName(), StandardCharsets.UTF_8);
                String url = userServiceBaseUrl+"/updatePosition?idNumber="
                        + idNumber
                        + "&name=" + name
                        + "&status=1"  // Removed extra space before &newPosition
                        + "&newPosition=" + singleRowData.getPosition();

                // Use PUT request
                ResponseEntity<String> response1 = restTemplate.exchange(url, HttpMethod.PUT, null, String.class);

              //  System.out.println(response1.getBody());

                break;
            }
            else {
                //repositoryManager.getRegistrationRepository().updatePosition(employeeList.get(i).getIdNumber(),employeeList.get(i).getName(),"1",employeeList.get(i).getPosition());
                outresult.add(employeeList.get(i));

                String idNumber = URLEncoder.encode(employeeList.get(i).getIdNumber(), StandardCharsets.UTF_8);
                String name = URLEncoder.encode(employeeList.get(i).getName(), StandardCharsets.UTF_8);
                String url = userServiceBaseUrl+"/updatePosition?idNumber="
                        + idNumber
                        + "&name=" + name
                        + "&status=1"  // Removed extra space before &newPosition
                        + "&newPosition=" + employeeList.get(i).getPosition();
               // System.out.println("Request URL: " + url);
                // Use PUT request
                ResponseEntity<String> response1 = restTemplate.exchange(url, HttpMethod.PUT, null, String.class);
            }
        }

        int cc=Integer.parseInt(singleRowData.getPosition())+1;
        for(int i=Integer.parseInt(singleRowData.getPosition())-1;i<employeeList.size();i++)
        {
            if(singleRowData.getIdNumber().equals(employeeList.get(i).getIdNumber())&& singleRowData.getName().equals(employeeList.get(i).getName()) ){

            }
            else {

                outresult.add(new Employee(employeeList.get(i).getIdNumber(),employeeList.get(i).getName(),null,null,null,null,Integer.toString(cc),null,null,null,null));
               // repositoryManager.getRegistrationRepository().updatePosition(employeeList.get(i).getIdNumber(),employeeList.get(i).getName(),"1",Integer.toString(cc));
                String idNumber = URLEncoder.encode(employeeList.get(i).getIdNumber(), StandardCharsets.UTF_8);
                String name = URLEncoder.encode(employeeList.get(i).getName(), StandardCharsets.UTF_8);
                String url = userServiceBaseUrl+"/updatePosition?idNumber="
                        + idNumber
                        + "&name=" + name
                        + "&status=1"  // Removed extra space before &newPosition
                        + "&newPosition=" + Integer.toString(cc);

                // Use PUT request
                ResponseEntity<String> response1 = restTemplate.exchange(url, HttpMethod.PUT, null, String.class);

                cc++;
            }
        }

       // return outresult;
        // You can now save the updated list back to the database (if needed)
      //  employeeService.saveEmployees(updatedEmployees);

        return ResponseEntity.ok("Sorting updated successfully");
    }


}
