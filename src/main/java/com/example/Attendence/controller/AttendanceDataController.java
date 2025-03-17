package com.example.Attendence.controller;
import com.example.Attendence.model.AllEmployeeAttendanceData;
import com.example.Attendence.model.AttendanceData;
import com.example.Attendence.model.AttendanceDataForAnyPeriod;
import com.example.Attendence.model.AttendanceDataForFixedDay;
import com.example.Attendence.repository.AttendanceDataRepository;
import com.example.Attendence.service.AttendanceService;
import com.example.Attendence.service.DownloadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attendance") // Base URL for all endpoints in this controller
@CrossOrigin(origins = "http://localhost:3000")
public class AttendanceDataController {
    @Autowired
    private AttendanceDataRepository attendanceDataRepository;

    @Autowired
    DownloadService downloadService;

    @Autowired
    AttendanceService attendanceService;

    @PostMapping("/insert")
    public ResponseEntity<?> insertAttendance(@RequestBody List<Map<String, Object>> attendanceList) {
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
        return attendanceDataRepository.findByStatus(status);
    }
    @PostMapping("/getAllAttendanceData")
    public List<AllEmployeeAttendanceData> getAttendanceEmployee(@RequestBody Map<String, String> requestData) {
        String  startDate = requestData.get("startDate");
        String endDate = requestData.get("endDate");

        return downloadService.getAllEmployeeAttendanceData(startDate, endDate);
    }

    @PostMapping("/getAttendanceDataForAnyPeriod")
    public List<AttendanceDataForAnyPeriod> getAttendanceDataForAnyPeriod(@RequestBody Map<String, String> requestData) {
        String employeeId=requestData.get("employeeId");
        String employeeName=requestData.get("employeeName");
        String  startDate = requestData.get("startDate");
        String endDate = requestData.get("endDate");

        //System.out.println("Okkkkk");

        return attendanceService.getAttendanceDataForAnyPeriod(employeeId,employeeName,startDate, endDate);
    }

    @PostMapping("/getAllAttendanceDataForFixedDay")
    public List<AttendanceDataForFixedDay> getAllAttendanceDataForFixedDay(@RequestBody Map<String, String> requestData) {
        String  selectedDate = requestData.get("selectedDate");

        return downloadService.getAllEmployeeAttendanceDataForFixedDay(selectedDate);
    }

    @PostMapping("/exportAllAttendanceData")
    public ResponseEntity<String> exportAllAttendanceData(@RequestBody List<AllEmployeeAttendanceData> attendanceData) {

        return downloadService.exportAllAttendanceData(attendanceData);
    }
    @PostMapping("/exportSummaryAttendanceData")
    public ResponseEntity<String> exportSummaryAttendanceData(@RequestBody List<AttendanceDataForAnyPeriod> attendanceData) {



        return attendanceService.exportSummaryAttendanceData(attendanceData);
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
