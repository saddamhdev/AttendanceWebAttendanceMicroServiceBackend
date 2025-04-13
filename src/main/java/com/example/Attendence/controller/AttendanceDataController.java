package com.example.Attendence.controller;
import com.example.Attendence.model.*;
import com.example.Attendence.repository.AttendanceDataRepository;
import com.example.Attendence.service.AttendanceService;
import com.example.Attendence.service.DownloadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attendance") // Base URL for all endpoints in this controller
public class AttendanceDataController {
    @Autowired
    private AttendanceDataRepository attendanceDataRepository;

    @Autowired
    DownloadService downloadService;

    @Autowired
    AttendanceService attendanceService;

    @PostMapping("/insert")
    public ResponseEntity<?> insertAttendance(@RequestBody List<Map<String, Object>> attendanceList) {
       // readCSVForAttendanceData("C:\\Users\\Saddam\\Downloads/attendanceData2.csv");
        try {
            List <AttendanceData> listData=new ArrayList<>();
            for (Map<String, Object> data : attendanceList) {
               // saveAttendance(data);
                LocalDate entryDate = LocalDate.parse( data.get("date").toString().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate exitDate =  entryDate;

                Optional<AttendanceData> view=attendanceDataRepository.findByEmployeeIdAndEntryDateAndUpdateStatus(data.get("employeeId").toString().trim(),data.get("date").toString().trim(),"1");

                if(view.isEmpty()){
                    AttendanceData attendanceData=new AttendanceData(
                            data.get("employeeId").toString().trim()
                            ,
                            data.get("name").toString().trim()
                            ,
                            Integer.toString(entryDate.getMonthValue())
                            ,
                            Integer.toString(entryDate.getYear())
                            ,
                            createLocalDateTime(entryDate.getYear(),entryDate.getMonthValue(),entryDate.getDayOfMonth(),Integer.parseInt(data.get("startHour").toString().trim()),Integer.parseInt(data.get("startMinute").toString().trim()),data.get("startPeriod").toString().trim() )
                            ,
                            data.get("lateEntryReason").toString().trim()
                            ,
                            createLocalDateTime(exitDate.getYear(),exitDate.getMonthValue(),exitDate.getDayOfMonth(),Integer.parseInt(data.get("exitHour").toString().trim()),Integer.parseInt(data.get("exitMinute").toString().trim()),data.get("exitPeriod").toString().trim() )
                            ,
                            data.get("earlyExitReason").toString().trim()
                            ,
                            data.get("status").toString().trim()
                            ,
                            data.get("outHour").toString().trim()+"."+ data.get("outMinute").toString().trim()
                            ,
                            entryDate.toString()
                            ,
                            LocalDateTime.now()
                            ,
                            "1"
                            ,
                            data.get("globalDayStatus").toString().trim()
                    );

                    listData.add(attendanceData);
                }
                else{
                    System.out.println("Already inserted bro");
                }


            }
           attendanceDataRepository.saveAll(listData);
            return ResponseEntity.ok(Collections.singletonMap("message", "Attendance saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Failed to save attendance"));
        }
    }
    // Endpoint to retrieve all employees based on status
    @GetMapping("/getAll")
    public List<AttendanceData> getAllEmployees(@RequestParam String status) {

        return attendanceDataRepository.findByUpdateStatus(status);
    }
    public  void readCSVForAttendanceData(String filePath) {
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

                System.out.println(values.size()+"  "+values); // Print as a list
                AttendanceData ee=new AttendanceData();
                ee.setEarlyExitReason(values.get(1));
                ee.setEmployeeId(values.get(2));
                ee.setEntryDate(convertDate(values.get(3)));// date 3
                // ee.setEntryTime(parseDateTime(values.get(3),values.get(4)));
                ee.setEntryTime(parseDateTime(values.get(4)));
               // ee.setExitTime(parseDateTime(values.get(3),values.get(5)));
                ee.setExitTime(parseDateTime(values.get(5)));
                ee.setGlobalDayStatus(values.get(6));
                ee.setLateEntryReason(values.get(7));
                ee.setMonth(values.get(8));
                ee.setName(values.get(9));
                ee.setOuttime(values.get(10));
               // ee.setPresentTime(parseDateTime(values.get(3),values.get(11)));
                ee.setPresentTime(parseDateTime(values.get(11)));
                ee.setStatus(values.get(12));
                ee.setUpdateStatus(values.get(13));
                ee.setYear(values.get(14));
               // attendanceDataRepository.save(ee);
                System.out.println(ee.toString());
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy H:mm", Locale.ENGLISH);
        return LocalDateTime.parse(dateTimeStr, formatter);
    }
    public static String convertDate(String inputDate) {
        // Define the input formatter
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Parse the input date
        LocalDate date = LocalDate.parse(inputDate, inputFormatter);

        // Convert it to the fixed date (February 5 of the same year)
        LocalDate transformedDate = LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth());

        // Format and return as String
        return transformedDate.format(outputFormatter);
    }
    public static LocalDateTime parseDateTime(String dateStr, String timeStr) {
       // System.out.println(dateStr+" "+timeStr);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        LocalDate date = LocalDate.parse(dateStr, dateFormatter);

        String[] timeParts = timeStr.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1].split("\\.")[0]); // Ignore milliseconds

        // Adjust time overflow (e.g., 53 hours → 2 days + 5 hours)
        int extraDays = hours / 24;
        int adjustedHours = hours % 24;

        return LocalDateTime.of(date.plusDays(extraDays), LocalTime.of(adjustedHours, minutes));
    }
    @PostMapping("/getAllAttendanceData")
    public List<AllEmployeeAttendanceData> getAttendanceEmployee(@RequestBody Map<String, String> requestData,HttpServletRequest request) {
        String  startDate = requestData.get("startDate");
        String endDate = requestData.get("endDate");

        return downloadService.getAllEmployeeAttendanceData(startDate, endDate,request.getHeader("Authorization"));
    }

    @PostMapping("/getAttendanceDataForAnyPeriod")
    public List<AttendanceDataForAnyPeriod> getAttendanceDataForAnyPeriod(@RequestBody Map<String, String> requestData, HttpServletRequest request) {
        String employeeId=requestData.get("employeeId");
        String employeeName=requestData.get("employeeName");
        String  startDate = requestData.get("startDate");
        String endDate = requestData.get("endDate");


        return attendanceService.getAttendanceDataForAnyPeriod(employeeId,employeeName,startDate, endDate,request.getHeader("Authorization"));
    }

    @PostMapping("/getAllAttendanceDataForFixedDay")
    public List<AttendanceDataForFixedDay> getAllAttendanceDataForFixedDay(@RequestBody Map<String, String> requestData,HttpServletRequest request) {
        String  selectedDate = requestData.get("selectedDate");

        return downloadService.getAllEmployeeAttendanceDataForFixedDay(selectedDate,request.getHeader("Authorization"));
    }

    @PostMapping("/exportAllAttendanceData")
    public ResponseEntity<String> exportAllAttendanceData(@RequestBody List<AllEmployeeAttendanceData> attendanceData) {

        return downloadService.exportAllAttendanceData(attendanceData);
    }
    @PostMapping("/exportSummaryAttendanceData")
    public ResponseEntity<String> exportSummaryAttendanceData(@RequestBody List<AttendanceDataForAnyPeriod> attendanceData, HttpServletResponse response) {

        return attendanceService.exportSummaryAttendanceData(attendanceData,response);
    }
    @PostMapping("/updateAttendanceData")
    public ResponseEntity<String> updateAttendanceData(@RequestBody Map<String, List<AttendanceDataForFixedDay>> requestData) {
        List<AttendanceDataForFixedDay> newData = requestData.get("newData"); // Take first element
        List<AttendanceDataForFixedDay> oldData = requestData.get("oldData"); // Take first element
        System.out.println(newData);
        System.out.println(oldData);
        return attendanceService.updateAttendanceData(newData, oldData);
    }




    public LocalDateTime createLocalDateTime(int year, int month, int dayOfMonth, int hour, int minute, String amPm) {
        int adjustedHour = hour % 12;
        if (amPm.equalsIgnoreCase("PM")) {
            adjustedHour += 12;
        }
        return LocalDateTime.of(year, month, dayOfMonth, adjustedHour, minute);
    }
}
