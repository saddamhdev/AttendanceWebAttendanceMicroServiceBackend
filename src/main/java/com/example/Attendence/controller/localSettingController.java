package com.example.Attendence.controller;
import com.example.Attendence.model.GlobalSetting;
import com.example.Attendence.model.LocalSetting;
import com.example.Attendence.repository.GlobalSettingRepository;
import com.example.Attendence.repository.LocalSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/localSetting") // Base URL for all endpoints in this controller
public class localSettingController {
    @Autowired
    private LocalSettingRepository localSettingRepository;
    @PostMapping("/insert")
    public ResponseEntity<String> insertEmployee(@RequestBody LocalSetting employeeData) {
        try {
            if (employeeData == null) {
                return ResponseEntity.badRequest().body("Error: Received null data.");
            }

            System.out.println("Received Data: " + employeeData); // Debugging log

            localSettingRepository.save(employeeData);
            return ResponseEntity.ok("Successfully inserted");
        } catch (Exception e) {
            System.err.println("Error inserting employee data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to insert data. Please try again.");
        }
    }

    @PostMapping("/update")
    public ResponseEntity<String> update(@RequestBody Map<String,String> requestData) {
        System.out.println(requestData);
        Optional<LocalSetting> data=localSettingRepository.findById(requestData.get("id"));
        if(data.isPresent()){
            LocalSetting mm=data.get();
            mm.setStatus("0");
            localSettingRepository.save(mm);
            localSettingRepository.save(new LocalSetting(
                  requestData.get("employeeId"),
                  requestData.get("name"),
                  requestData.get("currentTime") ,
                  requestData.get("formattedBirthDate")  ,
                    requestData.get("formattedDeathDate"),
                    requestData.get("startHours"),
                    requestData.get("startMinute"),
                    requestData.get("endHours"),
                    requestData.get("endMinute"),
                    requestData.get("totalHours"),
                    requestData.get("designation"),
                    requestData.get("status")
            ));
            return ResponseEntity.ok("Successfully updated");
        }
        return ResponseEntity.status(400).body("Failed to update");

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

    public  void readCSVForLocalSetting(String filePath) {
        String line;
        String regex = "\"([^\"]*)\"|([^,]+)"; // Regex to capture quoted and unquoted values
        Pattern pattern = Pattern.compile(regex);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                List<String> values = new ArrayList<>();
                Matcher matcher = pattern.matcher(line);

                while (matcher.find()) {
                    if (matcher.group(1) != null) {
                        values.add(matcher.group(1)); // Quoted value
                    } else {
                        values.add(matcher.group(2)); // Unquoted value
                    }
                }

                System.out.println(values.size()+"  "+values); // Print as a list
          LocalSetting ee=new LocalSetting();
          ee.setCurrentTime(values.get(1));
          ee.setDesignation(values.get(2));
          ee.setEmployeeId(values.get(3));
          ee.setEndHours(values.get(4));
          ee.setEndMinute(values.get(5));
          ee.setFormattedBirthDate(values.get(6));
          ee.setFormattedDeathDate(values.get(7));
          ee.setName(values.get(8));
          ee.setStartHours(values.get(9));
          ee.setStartMinute(values.get(10));
          ee.setStatus(values.get(11));
          ee.setTotalHours(values.get(12));
          localSettingRepository.save(ee);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteLocalSetting(@RequestBody LocalSetting localSetting) {
        System.out.println("Received for deletion: " + localSetting); // ✅ Log input
        //readCSVForLocalSetting("C:\\Users\\Saddam\\Downloads/LocalSetting.csv");
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
