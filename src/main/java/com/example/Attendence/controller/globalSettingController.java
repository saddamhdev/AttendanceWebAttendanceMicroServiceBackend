package com.example.Attendence.controller;
import com.example.Attendence.model.GlobalSetting;
import com.example.Attendence.repository.GlobalSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/globalSetting") // Base URL for all endpoints in this controller
public class globalSettingController {
    @Autowired
    private  GlobalSettingRepository globalSettingRepository;
    @PostMapping("/insert")
    public ResponseEntity<String> insertEmployee(@RequestBody GlobalSetting employeeData) {
        try {
            if (employeeData == null) {
                return ResponseEntity.badRequest().body("Error: Received null data.");
            }

            globalSettingRepository.save(employeeData);
            return ResponseEntity.ok("Successfully inserted");

        } catch (Exception e) {
            e.printStackTrace(); // Optional: Log error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to insert data. Please try again.");
        }
    }

    @PostMapping("/update")
    public ResponseEntity<String> update(@RequestBody Map<String,String> employeeData) {
       //  readCSVForGlobalSetting("C:\\Users\\01957\\Downloads/global.csv");
       // System.out.println("Received Data: " + employeeData); // Debugging
      //  System.out.println("Received Data: " + employeeData.get("currentTime")); // Debugging
        // Save the employee data to the database
       // GlobalSetting globalSetting = globalSettingRepository.save(employeeData);
        // Return the saved employee data as a response
       Optional<GlobalSetting> gg= globalSettingRepository.findById(employeeData.get("rowId"));
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
    @PostMapping("/updateDel")
    public ResponseEntity<String> updateDel(@RequestBody Map<String,String> employeeData) {
        //  readCSVForGlobalSetting("C:\\Users\\01957\\Downloads/global.csv");
        // System.out.println("Received Data: " + employeeData); // Debugging
        //  System.out.println("Received Data: " + employeeData.get("currentTime")); // Debugging
        // Save the employee data to the database
        // GlobalSetting globalSetting = globalSettingRepository.save(employeeData);
        // Return the saved employee data as a response
        Optional<GlobalSetting> gg= globalSettingRepository.findById(employeeData.get("rowId"));
        if(gg.isPresent()){

            GlobalSetting data=gg.get();
            data.setStatus("2");
            globalSettingRepository.save(data);

            return ResponseEntity.ok("Successfully deleted");
        }
        return ResponseEntity.ok("Sorry Not updated");



    }

    @GetMapping("/getAll")
    public List<GlobalSetting> retrieveData(){

        //return repositoryManager.getUserGlobalSettingRepository().findAllByStatus("1");
        return  listData(globalSettingRepository.findAllByStatus("1"), Comparator.comparing(GlobalSetting::getFormattedBirthDate));
    }
    public  void readCSVForGlobalSetting(String filePath) {
        String line;
        String regex = "\"([^\"]*)\"|([^,]+)"; // Regex to capture quoted and unquoted values
        Pattern pattern = Pattern.compile(regex);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
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

              //  System.out.println(values.size()+"  "+values); // Print as a list
                GlobalSetting ee=new GlobalSetting();
                ee.setCurrentTime(convertUtcToDhaka(values.get(1)));
                ee.setEarlyMinute(values.get(2));
                ee.setFormattedBirthDate(values.get(3));
                ee.setFormattedDeathDate(values.get(4));
                ee.setLateMinute(values.get(5));
                ee.setStatus(values.get(6));
                globalSettingRepository.save(ee);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<GlobalSetting> listData(Iterable<GlobalSetting> data, Comparator<GlobalSetting> comparator)
    {
        List<GlobalSetting> tr= new ArrayList<>();
        data.forEach(tr::add);
        Collections.sort(tr, comparator.reversed()); // Sorting in descending order
        return tr;
    }


    @DeleteMapping("del/{id}")
    public ResponseEntity<String> deleteGlobalData(@PathVariable String id) {
        System.out.println("Received for deletion: " + id); // ✅ Log input

        try {

            Optional<GlobalSetting> data=globalSettingRepository.findByIdAndStatus(id,"1");
           if(data.isPresent()){
              GlobalSetting gog=data.get();
              gog.setStatus("2");
              globalSettingRepository.save(gog);

           }

            return ResponseEntity.ok("Successfully deleted");
        } catch (Exception e) {
            e.printStackTrace(); // ✅ Print the full error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting entry: " + e.getMessage());
        }
    }
    public static String convertUtcToDhaka(String inputTime) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime utcDateTime = LocalDateTime.parse(inputTime, inputFormatter);

        ZonedDateTime utcZoned = utcDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime dhakaZoned = utcZoned.withZoneSameInstant(ZoneId.of("Asia/Dhaka"));

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dhakaZoned.format(outputFormatter);
    }
}
