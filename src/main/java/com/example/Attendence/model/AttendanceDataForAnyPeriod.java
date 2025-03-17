package com.example.Attendence.model;

public class AttendanceDataForAnyPeriod {
    private String employeeId;
    private String employeeName;
    private String startDate;
    private String endDate;
    private String date;
    private String entryTime;
    private String lateDuration;
    private String entryComment;
    private String exitTime;
    private String timeAfterExit;
    private String exitComment;
    private String outTime;
    private String totalTimeInDay;
    private String dayComment;
    private String comment;

    public AttendanceDataForAnyPeriod(String employeeId, String employeeName, String startDate, String endDate, String date, String entryTime, String lateDuration, String entryComment, String exitTime, String timeAfterExit, String exitComment, String outTime, String totalTimeInDay, String dayComment, String comment) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.date = date;
        this.entryTime = entryTime;
        this.lateDuration = lateDuration;
        this.entryComment = entryComment;
        this.exitTime = exitTime;
        this.timeAfterExit = timeAfterExit;
        this.exitComment = exitComment;
        this.outTime = outTime;
        this.totalTimeInDay = totalTimeInDay;
        this.dayComment = dayComment;
        this.comment = comment;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public String getLateDuration() {
        return lateDuration;
    }

    public void setLateDuration(String lateDuration) {
        this.lateDuration = lateDuration;
    }

    public String getEntryComment() {
        return entryComment;
    }

    public void setEntryComment(String entryComment) {
        this.entryComment = entryComment;
    }

    public String getExitTime() {
        return exitTime;
    }

    public void setExitTime(String exitTime) {
        this.exitTime = exitTime;
    }

    public String getTimeAfterExit() {
        return timeAfterExit;
    }

    public void setTimeAfterExit(String timeAfterExit) {
        this.timeAfterExit = timeAfterExit;
    }

    public String getExitComment() {
        return exitComment;
    }

    public void setExitComment(String exitComment) {
        this.exitComment = exitComment;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getTotalTimeInDay() {
        return totalTimeInDay;
    }

    public void setTotalTimeInDay(String totalTimeInDay) {
        this.totalTimeInDay = totalTimeInDay;
    }

    public String getDayComment() {
        return dayComment;
    }

    public void setDayComment(String dayComment) {
        this.dayComment = dayComment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
