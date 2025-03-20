package com.example.Attendence.repository;

import com.example.Attendence.model.AttendanceData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceDataRepository extends MongoRepository<AttendanceData, String> {


    List<AttendanceData> findByUpdateStatus(String updateStatus);
    List<AttendanceData> findByEntryDateAndUpdateStatus(String entryDate,String updateStatus);
    Optional<AttendanceData> findByEmployeeIdAndEntryDateAndUpdateStatus(String employeeId, String entryDate, String updateStatus);

    // Custom query to find attendance records by status
    List<AttendanceData> findByStatus(String status);
    // i want a list according to status and (first entryDate and second entryDate . in this period data)
// Find attendance records within a date range and a specific status
    List<AttendanceData> findByEmployeeIdAndUpdateStatusAndEntryDateBetween(
            String employeeId, String status, String startDate, String endDate);
    List<AttendanceData> findByUpdateStatusAndEntryDateBetween(
             String status, String startDate, String endDate);

}