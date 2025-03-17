package com.example.Attendence.model;

public class UserAtAGlance {
    private String employeeId;
    private  String employeeName;
    private String startDate;
    private String endDate;
    private int officeDay;
    private int totalPresent;
    private String avgTime;
    private int leave;
    private int absent;
    private int holiday;
    private int shortTime;
    private int regularTime;
    private int extraTime;
    private int entryInTime;
    private int entryLate;
    private String totalLate;
    private int exitOk;
    private int exitEarly;
    private String totalExtraTime;
    private String officeOutTime;
    private String officeInTime;
    private String totalTime;

    public UserAtAGlance() {
    }

    public UserAtAGlance(String employeeId, String employeeName, String startDate, String endDate, int officeDay, int totalPresent, String avgTime, int leave, int absent, int holiday, int shortTime, int regularTime, int extraTime, int entryInTime, int entryLate, String totalLate, int exitOk, int exitEarly, String totalExtraTime, String officeOutTime, String officeInTime, String totalTime) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.officeDay = officeDay;
        this.totalPresent = totalPresent;
        this.avgTime = avgTime;
        this.leave = leave;
        this.absent = absent;
        this.holiday = holiday;
        this.shortTime = shortTime;
        this.regularTime = regularTime;
        this.extraTime = extraTime;
        this.entryInTime = entryInTime;
        this.entryLate = entryLate;
        this.totalLate = totalLate;
        this.exitOk = exitOk;
        this.exitEarly = exitEarly;
        this.totalExtraTime = totalExtraTime;
        this.officeOutTime = officeOutTime;
        this.officeInTime = officeInTime;
        this.totalTime = totalTime;
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

    public int getOfficeDay() {
        return officeDay;
    }

    public void setOfficeDay(int officeDay) {
        this.officeDay = officeDay;
    }

    public int getTotalPresent() {
        return totalPresent;
    }

    public void setTotalPresent(int totalPresent) {
        this.totalPresent = totalPresent;
    }

    public String getAvgTime() {
        return avgTime;
    }

    public void setAvgTime(String avgTime) {
        this.avgTime = avgTime;
    }

    public int getLeave() {
        return leave;
    }

    public void setLeave(int leave) {
        this.leave = leave;
    }

    public int getAbsent() {
        return absent;
    }

    public void setAbsent(int absent) {
        this.absent = absent;
    }

    public int getHoliday() {
        return holiday;
    }

    public void setHoliday(int holiday) {
        this.holiday = holiday;
    }

    public int getShortTime() {
        return shortTime;
    }

    public void setShortTime(int shortTime) {
        this.shortTime = shortTime;
    }

    public int getRegularTime() {
        return regularTime;
    }

    public void setRegularTime(int regularTime) {
        this.regularTime = regularTime;
    }

    public int getExtraTime() {
        return extraTime;
    }

    public void setExtraTime(int extraTime) {
        this.extraTime = extraTime;
    }

    public int getEntryInTime() {
        return entryInTime;
    }

    public void setEntryInTime(int entryInTime) {
        this.entryInTime = entryInTime;
    }

    public int getEntryLate() {
        return entryLate;
    }

    public void setEntryLate(int entryLate) {
        this.entryLate = entryLate;
    }

    public String getTotalLate() {
        return totalLate;
    }

    public void setTotalLate(String totalLate) {
        this.totalLate = totalLate;
    }

    public int getExitOk() {
        return exitOk;
    }

    public void setExitOk(int exitOk) {
        this.exitOk = exitOk;
    }

    public int getExitEarly() {
        return exitEarly;
    }

    public void setExitEarly(int exitEarly) {
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
        return "UserAtAGlance{" +
                "officeDay=" + officeDay +
                ", totalPresent=" + totalPresent +
                ", avgTime=" + avgTime +
                ", leave=" + leave +
                ", absent=" + absent +
                ", holiday=" + holiday +
                ", shortTime=" + shortTime +
                ", regularTime=" + regularTime +
                ", extraTime=" + extraTime +
                ", entryInTime=" + entryInTime +
                ", entryLate=" + entryLate +
                ", totalLate=" + totalLate +
                ", exitOk=" + exitOk +
                ", exitEarly=" + exitEarly +
                ", totalExtraTime=" + totalExtraTime +
                ", officeOutTime=" + officeOutTime +
                ", officeInTime=" + officeInTime +
                ", totalTime=" + totalTime +
                '}';
    }
}
