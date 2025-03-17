package com.example.Attendence.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Position")
public class Position {

    @Id
    private Long id;

    private String employeeId;
    private String name;
    private String currentTime;
    private String position;
    private String status;

    // Constructor (optional, depending on your needs)
    public Position(){

    }
    public Position(String employeeId, String name, String currentTime, String position, String status) {
        this.employeeId = employeeId;
        this.name = name;
        this.currentTime = currentTime;
        this.position = position;
        this.status = status;
    }

    // Getter methods
    public String getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public String getPosition() {
        return position;
    }

    public String getStatus() {
        return status;
    }

    // Setter methods
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Position{" +
                "id=" + id +
                ", employeeId='" + employeeId + '\'' +
                ", name='" + name + '\'' +
                ", currentTime='" + currentTime + '\'' +
                ", position='" + position + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
    // You can add additional methods or behavior as needed
}
