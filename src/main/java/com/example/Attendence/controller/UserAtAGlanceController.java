package com.example.Attendence.controller;

import com.example.Attendence.model.AttendanceData;
import com.example.Attendence.model.Employee;
import com.example.Attendence.model.Position;
import com.example.Attendence.model.UserAtAGlance;
import com.example.Attendence.repository.AttendanceDataRepository;
import com.example.Attendence.service.UserAtAGlanceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/userAtAGlance") // Base URL for all endpoints in this controller
@CrossOrigin(origins = "http://localhost:3000")
public class UserAtAGlanceController {
    @Autowired
    private UserAtAGlanceService userAtAGlanceService;
    @Autowired
    private AttendanceDataRepository attendanceDataRepository;

    @GetMapping("/getAll")
    public UserAtAGlance getAllAtAGlanceData(
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate, HttpServletRequest request) {

        List<AttendanceData> dataList=attendanceDataRepository.findByUpdateStatus("1");
        if(dataList.size()>0)
            System.out.println("Ok");
        else
            System.out.println("No");

        return userAtAGlanceService.getUserAtAGlanceData(employeeId, employeeName, startDate, endDate,request.getHeader("Authorization"));
    }

    @PostMapping("/exportAtAGlanceData")
    public ResponseEntity<String> updateSorting(@RequestBody UserAtAGlance userAtAGlance) {
        return userAtAGlanceService.exportAtAGlance(userAtAGlance);
    }

}
