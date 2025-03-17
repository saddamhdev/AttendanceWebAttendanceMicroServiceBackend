package com.example.Attendence.model;

import java.util.Objects;

public class AttendanceDataForFixedDay {
    private String date;
    private String employeeId;
    private String name;
    private String startHour;
    private String startMinute;
    private String lateEntryReason;
    private String startPeriod;
    private String exitHour;
    private String exitMinute;
    private String exitPeriod;
    private String earlyExitReason;
    private String outHour;
    private String outMinute;
    private String updateStatus;
    private String globalDayStatus;
    private String status;

    public AttendanceDataForFixedDay(String date, String employeeId, String name, String startHour, String startMinute, String lateEntryReason, String startPeriod, String exitHour, String exitMinute, String exitPeriod, String earlyExitReason, String outHour, String outMinute, String updateStatus, String globalDayStatus, String status) {
        this.date = date;
        this.employeeId = employeeId;
        this.name = name;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.lateEntryReason = lateEntryReason;
        this.startPeriod = startPeriod;
        this.exitHour = exitHour;
        this.exitMinute = exitMinute;
        this.exitPeriod = exitPeriod;
        this.earlyExitReason = earlyExitReason;
        this.outHour = outHour;
        this.outMinute = outMinute;
        this.updateStatus = updateStatus;
        this.globalDayStatus = globalDayStatus;
        this.status = status;
    }

    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    public String getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(String startMinute) {
        this.startMinute = startMinute;
    }

    public String getLateEntryReason() {
        return lateEntryReason;
    }

    public void setLateEntryReason(String lateEntryReason) {
        this.lateEntryReason = lateEntryReason;
    }

    public String getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(String startPeriod) {
        this.startPeriod = startPeriod;
    }

    public String getExitHour() {
        return exitHour;
    }

    public void setExitHour(String exitHour) {
        this.exitHour = exitHour;
    }

    public String getExitMinute() {
        return exitMinute;
    }

    public void setExitMinute(String exitMinute) {
        this.exitMinute = exitMinute;
    }

    public String getExitPeriod() {
        return exitPeriod;
    }

    public void setExitPeriod(String exitPeriod) {
        this.exitPeriod = exitPeriod;
    }

    public String getEarlyExitReason() {
        return earlyExitReason;
    }

    public void setEarlyExitReason(String earlyExitReason) {
        this.earlyExitReason = earlyExitReason;
    }

    public String getOutHour() {
        return outHour;
    }

    public void setOutHour(String outHour) {
        this.outHour = outHour;
    }

    public String getOutMinute() {
        return outMinute;
    }

    public void setOutMinute(String outMinute) {
        this.outMinute = outMinute;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AttendanceDataForFixedDay{" +
                "date='" + date + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", name='" + name + '\'' +
                ", startHour='" + startHour + '\'' +
                ", startMinute='" + startMinute + '\'' +
                ", lateEntryReason='" + lateEntryReason + '\'' +
                ", startPeriod='" + startPeriod + '\'' +
                ", exitHour='" + exitHour + '\'' +
                ", exitMinute='" + exitMinute + '\'' +
                ", exitPeriod='" + exitPeriod + '\'' +
                ", earlyExitReason='" + earlyExitReason + '\'' +
                ", outHour='" + outHour + '\'' +
                ", outMinute='" + outMinute + '\'' +
                ", updateStatus='" + updateStatus + '\'' +
                ", globalDayStatus='" + globalDayStatus + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        AttendanceDataForFixedDay that = (AttendanceDataForFixedDay) obj;

        return Objects.equals(date, that.date) &&
                Objects.equals(employeeId, that.employeeId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(startHour, that.startHour) &&
                Objects.equals(startMinute, that.startMinute) &&
                Objects.equals(lateEntryReason, that.lateEntryReason) &&
                Objects.equals(startPeriod, that.startPeriod) &&
                Objects.equals(exitHour, that.exitHour) &&
                Objects.equals(exitMinute, that.exitMinute) &&
                Objects.equals(exitPeriod, that.exitPeriod) &&
                Objects.equals(earlyExitReason, that.earlyExitReason) &&
                Objects.equals(outHour, that.outHour) &&
                Objects.equals(outMinute, that.outMinute) &&
                Objects.equals(updateStatus, that.updateStatus) &&
                Objects.equals(globalDayStatus, that.globalDayStatus) &&
                Objects.equals(status, that.status);
    }

}