package com.example.Attendence.repository;

import com.example.Attendence.model.AttendanceData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceDataRepository extends MongoRepository<AttendanceData, String> {


    List<AttendanceData> findByUpdateStatus(String updateStatus);

  //  @Query("{ 'entryDate': { $gte: ?0, $lt: ?1 }, 'updateStatus': ?2 }")
    List<AttendanceData> findByEntryDateAndUpdateStatus(LocalDate entryDate, String updateStatus);


    Optional<AttendanceData> findByEmployeeIdAndEntryDateAndUpdateStatus(String employeeId, LocalDate entryDate, String updateStatus);

    // Custom query to find attendance records by status
    List<AttendanceData> findByStatus(String status);
    // i want a list according to status and (first entryDate and second entryDate . in this period data)
// Find attendance records within a date range and a specific status
    @Query("{ 'employeeId': ?0, 'updateStatus': ?1, 'entryDate': { $gte: ?2, $lte: ?3 } }")
    List<AttendanceData> findByEmployeeIdAndUpdateStatusAndEntryDateInclusive(
            String employeeId, String updateStatus, LocalDate startDate, LocalDate endDate);

    List<AttendanceData> findByUpdateStatusAndEntryDateBetween(
             String status, LocalDate startDate, LocalDate endDate);

  @Query("{ 'entryDate': { $gte: ?0, $lt: ?1 }, 'updateStatus': ?2 }")
  List<AttendanceData> findByEntryDateFixedDay(
          LocalDateTime startOfDay,
          LocalDateTime startOfNextDay,
          String updateStatus
  );


}