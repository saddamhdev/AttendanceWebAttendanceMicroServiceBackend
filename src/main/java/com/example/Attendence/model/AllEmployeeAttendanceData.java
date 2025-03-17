package com.example.Attendence.model;
public class AllEmployeeAttendanceData {
    private  String startDate;
    private  String endDate;
    // Fields
    private String serial; // serial = index + 1
    private String name; // emp.name
    private String officeDay;
    private String totalPresent;
    private String avgTime;
    private String leave;
    private String absent;
    private String holyday;
    private String shortTime;
    private String requiredTime;
    private String extraTime;
    private String entryInTime;
    private String entryLate;
    private String entryTotalLate;
    private String exitOk;
    private String exitEarly;
    private String totalExtraTime;
    private String officeOutTime;
    private String officeInTime;
    private String totalTime;

    public AllEmployeeAttendanceData(String startDate, String endDate, String serial, String name, String officeDay, String totalPresent, String avgTime, String leave, String absent, String holyday, String shortTime, String requiredTime, String extraTime, String entryInTime, String entryLate, String entryTotalLate, String exitOk, String exitEarly, String totalExtraTime, String officeOutTime, String officeInTime, String totalTime) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.serial = serial;
        this.name = name;
        this.officeDay = officeDay;
        this.totalPresent = totalPresent;
        this.avgTime = avgTime;
        this.leave = leave;
        this.absent = absent;
        this.holyday = holyday;
        this.shortTime = shortTime;
        this.requiredTime = requiredTime;
        this.extraTime = extraTime;
        this.entryInTime = entryInTime;
        this.entryLate = entryLate;
        this.entryTotalLate = entryTotalLate;
        this.exitOk = exitOk;
        this.exitEarly = exitEarly;
        this.totalExtraTime = totalExtraTime;
        this.officeOutTime = officeOutTime;
        this.officeInTime = officeInTime;
        this.totalTime = totalTime;
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

    // Getters and Setters
    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOfficeDay() {
        return officeDay;
    }

    public void setOfficeDay(String officeDay) {
        this.officeDay = officeDay;
    }

    public String getTotalPresent() {
        return totalPresent;
    }

    public void setTotalPresent(String totalPresent) {
        this.totalPresent = totalPresent;
    }

    public String getAvgTime() {
        return avgTime;
    }

    public void setAvgTime(String avgTime) {
        this.avgTime = avgTime;
    }

    public String getLeave() {
        return leave;
    }

    public void setLeave(String leave) {
        this.leave = leave;
    }

    public String getAbsent() {
        return absent;
    }

    public void setAbsent(String absent) {
        this.absent = absent;
    }

    public String getHolyday() {
        return holyday;
    }

    public void setHolyday(String holyday) {
        this.holyday = holyday;
    }

    public String getShortTime() {
        return shortTime;
    }

    public void setShortTime(String shortTime) {
        this.shortTime = shortTime;
    }

    public String getRequiredTime() {
        return requiredTime;
    }

    public void setRequiredTime(String requiredTime) {
        this.requiredTime = requiredTime;
    }

    public String getExtraTime() {
        return extraTime;
    }

    public void setExtraTime(String extraTime) {
        this.extraTime = extraTime;
    }

    public String getEntryInTime() {
        return entryInTime;
    }

    public void setEntryInTime(String entryInTime) {
        this.entryInTime = entryInTime;
    }

    public String getEntryLate() {
        return entryLate;
    }

    public void setEntryLate(String entryLate) {
        this.entryLate = entryLate;
    }

    public String getEntryTotalLate() {
        return entryTotalLate;
    }

    public void setEntryTotalLate(String entryTotalLate) {
        this.entryTotalLate = entryTotalLate;
    }

    public String getExitOk() {
        return exitOk;
    }

    public void setExitOk(String exitOk) {
        this.exitOk = exitOk;
    }

    public String getExitEarly() {
        return exitEarly;
    }

    public void setExitEarly(String exitEarly) {
        this.exitEarly = exitEarly;
    }

    public String getTotalExtraTime() {
        return totalExtraTime;
    }

    public void setTotalExtraTime(String totalExtraTime) {
        this.totalExtraTime = totalExtraTime;
    }

    public String getOfficeOutTime() {
        return officeOutTime;
    }

    public void setOfficeOutTime(String officeOutTime) {
        this.officeOutTime = officeOutTime;
    }

    public String getOfficeInTime() {
        return officeInTime;
    }

    public void setOfficeInTime(String officeInTime) {
        this.officeInTime = officeInTime;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public String toString() {
        return "AllEmployeeAttendanceData{" +
                "startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", serial='" + serial + '\'' +
                ", name='" + name + '\'' +
                ", officeDay='" + officeDay + '\'' +
                ", totalPresent='" + totalPresent + '\'' +
                ", avgTime='" + avgTime + '\'' +
                ", leave='" + leave + '\'' +
                ", absent='" + absent + '\'' +
                ", holyday='" + holyday + '\'' +
                ", shortTime='" + shortTime + '\'' +
                ", requiredTime='" + requiredTime + '\'' +
                ", extraTime='" + extraTime + '\'' +
                ", entryInTime='" + entryInTime + '\'' +
                ", entryLate='" + entryLate + '\'' +
                ", entryTotalLate='" + entryTotalLate + '\'' +
                ", exitOk='" + exitOk + '\'' +
                ", exitEarly='" + exitEarly + '\'' +
                ", totalExtraTime='" + totalExtraTime + '\'' +
                ", officeOutTime='" + officeOutTime + '\'' +
                ", officeInTime='" + officeInTime + '\'' +
                ", totalTime='" + totalTime + '\'' +
                '}';
    }
}