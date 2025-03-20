package com.example.Attendence.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "globalSettings") // Updated collection name
public class GlobalSetting { // Renamed class

    @Id
    private String id;
    private String currentTime;
    private String formattedBirthDate;
    private String formattedDeathDate;
    private String lateMinute;
    private String earlyMinute;
    private String status;

    public GlobalSetting() {
    }

    // Parameterized constructor
    public GlobalSetting(String currentTime, String formattedBirthDate, String formattedDeathDate,
                         String lateMinute, String earlyMinute, String status) {
        this.currentTime = currentTime;
        this.formattedBirthDate = formattedBirthDate;
        this.formattedDeathDate = formattedDeathDate;
        this.lateMinute = lateMinute;
        this.earlyMinute = earlyMinute;
        this.status = status;
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getFormattedBirthDate() {
        return formattedBirthDate;
    }

    public void setFormattedBirthDate(String formattedBirthDate) {
        this.formattedBirthDate = formattedBirthDate;
    }

    public String getFormattedDeathDate() {
        return formattedDeathDate;
    }

    public void setFormattedDeathDate(String formattedDeathDate) {
        this.formattedDeathDate = formattedDeathDate;
    }

    public String getLateMinute() {
        return lateMinute;
    }

    public void setLateMinute(String lateMinute) {
        this.lateMinute = lateMinute;
    }

    public String getEarlyMinute() {
        return earlyMinute;
    }

    public void setEarlyMinute(String earlyMinute) {
        this.earlyMinute = earlyMinute;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Other methods

    @Override
    public String toString() {
        return "GlobalSetting{" + // Updated class name in toString
                "id='" + id + '\'' +
                ", currentTime='" + currentTime + '\'' +
                ", formattedBirthDate='" + formattedBirthDate + '\'' +
                ", formattedDeathDate='" + formattedDeathDate + '\'' +
                ", lateMinute='" + lateMinute + '\'' +
                ", earlyMinute='" + earlyMinute + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    // Add other methods as needed
}