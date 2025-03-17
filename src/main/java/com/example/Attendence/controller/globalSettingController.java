package com.example.Attendence.controller;
import com.example.Attendence.model.GlobalSetting;
import com.example.Attendence.repository.GlobalSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/globalSetting") // Base URL for all endpoints in this controller
@CrossOrigin(origins = "http://localhost:3000")
public class globalSettingController {
    @Autowired
    private  GlobalSettingRepository globalSettingRepository;
    @PostMapping("/insert")
    public GlobalSetting insertEmployee(@RequestBody GlobalSetting employeeData) {
        System.out.println("Received Data: " + employeeData); // Debugging
        // Save the employee data to the database
        GlobalSetting globalSetting = globalSettingRepository.save(employeeData);
        // Return the saved employee data as a response
        return globalSetting;
    }
    @PostMapping("/update")
    public ResponseEntity<String> update(@RequestBody Map<String,String> employeeData) {
        System.out.println("Received Data: " + employeeData); // Debugging
        System.out.println("Received Data: " + employeeData.get("currentTime")); // Debugging
        // Save the employee data to the database
       // GlobalSetting globalSetting = globalSettingRepository.save(employeeData);
        // Return the saved employee data as a response
       Optional<GlobalSetting> gg= globalSettingRepository.findById(employeeData.get("roleId"));
       if(gg.isPresent()){

           GlobalSetting data=gg.get();
           data.setStatus("0");
           globalSettingRepository.save(data);
           globalSettingRepository.save(new GlobalSetting(
                   employeeData.get("currentTime"),
                   employeeData.get("formattedBirthDate"),
                   employeeData.get("formattedDeathDate"),
                   employeeData.get("lateMinute"),
                   employeeData.get("earlyMinute"),
                   employeeData.get("status")
           ));

           return ResponseEntity.ok("Successfully updated");
       }
        return ResponseEntity.ok("Sorry Not updated");



    }
    @GetMapping("/getAll")
    public List<GlobalSetting> retrieveData(){

        //return repositoryManager.getUserGlobalSettingRepository().findAllByStatus("1");
        return  listData(globalSettingRepository.findAllByStatus("1"), Comparator.comparing(GlobalSetting::getFormattedBirthDate));
    }

    public static List<GlobalSetting> listData(Iterable<GlobalSetting> data, Comparator<GlobalSetting> comparator)
    {
        List<GlobalSetting> tr= new ArrayList<>();
        data.forEach(tr::add);
        Collections.sort(tr, comparator.reversed()); // Sorting in descending order
        return tr;
    }


    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteGlobalSetting(@RequestBody GlobalSetting globalSetting) {
        System.out.println("Received for deletion: " + globalSetting); // ✅ Log input

        try {
            if (globalSetting == null) {
                return ResponseEntity.badRequest().body("Error: Request body is missing!");
            }
            if (globalSetting.getCurrentTime() == null) {
                return ResponseEntity.badRequest().body("Error: currentTimee is missing!");
            }

            Optional<GlobalSetting> data=globalSettingRepository.findByIdAndStatus(globalSetting.getId(),"1");
           if(data.isPresent()){
              GlobalSetting gog=data.get();
              gog.setStatus("0");
              globalSettingRepository.save(gog);

           }

            return ResponseEntity.ok("Deleted successfully");
        } catch (Exception e) {
            e.printStackTrace(); // ✅ Print the full error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting entry: " + e.getMessage());
        }
    }

}
