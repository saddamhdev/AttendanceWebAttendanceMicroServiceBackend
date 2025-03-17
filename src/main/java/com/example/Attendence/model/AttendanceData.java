package com.example.Attendence.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "attendanceData") // Updated collection name
public class AttendanceData { // Renamed class

    @Id
    private String id; // MongoDB uses String or ObjectId for the ID field

    private String employeeId;
    private String name;
    private String month;
    private String year;
    private LocalDateTime entryTime;
    private String lateEntryReason;
    private LocalDateTime exitTime;
    private String earlyExitReason;
    private String status;
    private String outtime;
    private String entryDate;
    private String updateStatus;
    private String globalDayStatus;
    private LocalDateTime presentTime;

    // Default constructor
    public AttendanceData() { // Updated constructor name
    }

    // Parameterized constructor
    public AttendanceData(String employeeId, String name, String month, String year,
                          LocalDateTime entryTime, String lateEntryReason,
                          LocalDateTime exitTime, String earlyExitReason,
                          String status, String outtime, String entryDate,
                          LocalDateTime presentTime, String updateStatus, String globalDayStatus) { // Updated constructor name
        this.employeeId = employeeId;
        this.name = name;
        this.month = month;
        this.year = year;
        this.entryTime = entryTime;
        this.lateEntryReason = lateEntryReason;
        this.exitTime = exitTime;
        this.earlyExitReason = earlyExitReason;
        this.status = status;
        this.outtime = outtime;
        this.entryDate = entryDate;
        this.presentTime = presentTime;
        this.updateStatus = updateStatus;
        this.globalDayStatus = globalDayStatus;
    }

    // Getters and setters for each field:

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public String getLateEntryReason() {
        return lateEntryReason;
    }

    public void setLateEntryReason(String lateEntryReason) {
        this.lateEntryReason = lateEntryReason;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public String getEarlyExitReason() {
        return earlyExitReason;
    }

    public void setEarlyExitReason(String earlyExitReason) {
        this.earlyExitReason = earlyExitReason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOuttime() {
        return outtime;
    }

    public void setOuttime(String outtime) {
        this.outtime = outtime;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public String getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(String updateStatus) {
        this.updateStatus = updateStatus;
    }

    public String getGlobalDayStatus() {
        return globalDayStatus;
    }

    public void setGlobalDayStatus(String globalDayStatus) {
        this.globalDayStatus = globalDayStatus;
    }

    public LocalDateTime getPresentTime() {
        return presentTime;
    }

    public void setPresentTime(LocalDateTime presentTime) {
        this.presentTime = presentTime;
    }

    @Override
    public String toString() {
        return "AttendanceData{" +
                "id='" + id + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", name='" + name + '\'' +
                ", month='" + month + '\'' +
                ", year='" + year + '\'' +
                ", entryTime=" + entryTime +
                ", lateEntryReason='" + lateEntryReason + '\'' +
                ", exitTime=" + exitTime +
                ", earlyExitReason='" + earlyExitReason + '\'' +
                ", status='" + status + '\'' +
                ", outtime='" + outtime + '\'' +
                ", entryDate='" + entryDate + '\'' +
                ", updateStatus='" + updateStatus + '\'' +
                ", globalDayStatus='" + globalDayStatus + '\'' +
                ", presentTime=" + presentTime +
                '}';
    }
}