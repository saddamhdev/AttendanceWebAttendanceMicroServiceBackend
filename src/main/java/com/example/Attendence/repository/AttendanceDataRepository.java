package com.example.Attendence.repository;

import com.example.Attendence.model.AttendanceData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceDataRepository extends MongoRepository<AttendanceData, String> {

    // Custom query to find attendance records by employeeId
    List<AttendanceData> findByEmployeeId(String employeeId);
    List<AttendanceData> findByUpdateStatus(String updateStatus);
    List<AttendanceData> findByEntryDateAndUpdateStatus(String entryDate,String updateStatus);
    Optional<AttendanceData> findByEmployeeIdAndEntryDateAndUpdateStatus(String employeeId, String entryDate, String updateStatus);
    // Custom query to find attendance records by employeeId and month
    List<AttendanceData> findByEmployeeIdAndMonth(String employeeId, String month);

    // Custom query to find attendance records by employeeId and year
    List<AttendanceData> findByEmployeeIdAndYear(String employeeId, String year);

    // Custom query to find attendance records by employeeId, month, and year
    List<AttendanceData> findByEmployeeIdAndMonthAndYear(String employeeId, String month, String year);

    // Custom query to find attendance records by status
    List<AttendanceData> findByStatus(String status);

    // Custom query to find attendance records by entryDate
    List<AttendanceData> findByEntryDate(String entryDate);

    // Custom query to find attendance records by globalDayStatus
    List<AttendanceData> findByGlobalDayStatus(String globalDayStatus);

    // Custom query to find attendance records by presentTime between two dates
    List<AttendanceData> findByPresentTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Custom query to delete attendance records by employeeId
    void deleteByEmployeeId(String employeeId);
}