package com.example.Attendence.controller;
import com.example.Attendence.model.GlobalSetting;
import com.example.Attendence.model.LocalSetting;
import com.example.Attendence.repository.GlobalSettingRepository;
import com.example.Attendence.repository.LocalSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/localSetting") // Base URL for all endpoints in this controller
@CrossOrigin(origins = "http://localhost:3000")
public class localSettingController {
    @Autowired
    private LocalSettingRepository localSettingRepository;
    @PostMapping("/insert")
    public LocalSetting insertEmployee(@RequestBody LocalSetting employeeData) {
        System.out.println("Received Data: " + employeeData); // Debugging
        // Save the employee data to the database
        LocalSetting localSetting = localSettingRepository.save(employeeData);
        // Return the saved employee data as a response
        return localSetting;
    }
    @GetMapping("/getAll")
    public List<LocalSetting> retrieveData(){

        //return repositoryManager.getUserGlobalSettingRepository().findAllByStatus("1");
        return  listData(localSettingRepository.findAllByStatus("1"), Comparator.comparing(LocalSetting::getFormattedBirthDate));
    }

    public static List<LocalSetting> listData(Iterable<LocalSetting> data, Comparator<LocalSetting> comparator)
    {
        List<LocalSetting> tr= new ArrayList<>();
        data.forEach(tr::add);
        Collections.sort(tr, comparator.reversed()); // Sorting in descending order
        return tr;
    }


    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteLocalSetting(@RequestBody LocalSetting localSetting) {
        System.out.println("Received for deletion: " + localSetting); // ✅ Log input

        try {
            if (localSetting == null) {
                return ResponseEntity.badRequest().body("Error: Request body is missing!");
            }
            if (localSetting.getCurrentTime() == null) {
                return ResponseEntity.badRequest().body("Error: currentTimee is missing!");
            }

            Optional<LocalSetting> data=localSettingRepository.findByIdAndStatus(localSetting.getId(),"1");
           if(data.isPresent()){
              LocalSetting gog=data.get();
              gog.setStatus("0");
              localSettingRepository.save(gog);

           }

            return ResponseEntity.ok("Deleted successfully");
        } catch (Exception e) {
            e.printStackTrace(); // ✅ Print the full error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting entry: " + e.getMessage());
        }
    }

}
