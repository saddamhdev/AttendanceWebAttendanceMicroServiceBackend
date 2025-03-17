package com.example.Attendence.service;

import com.example.Attendence.model.*;
import com.example.Attendence.repository.AttendanceDataRepository;
import com.example.Attendence.repository.GlobalSettingRepository;
import com.example.Attendence.repository.LocalSettingRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class AttendanceService {
    LocalDate databaseDate=null;
    List<Employee> employeeList;
    @Autowired
    UserService userService;

    @Autowired
    LocalSettingRepository localSettingRepository;

    @Autowired
    GlobalSettingRepository globalSettingRepository;

    @Autowired
    private AttendanceDataRepository attendanceDataRepository;

    public ResponseEntity<String> exportSummaryAttendanceData(List<AttendanceDataForAnyPeriod> dataList) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employee Data");

        // Create a style for the "Starting Date" and "End Date" row
        CellStyle dateRowStyle = workbook.createCellStyle();
        dateRowStyle.setAlignment(HorizontalAlignment.CENTER);
        dateRowStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Create the first row with Starting Date and End Date
        Row dateRow = sheet.createRow(0);

        // Merge cells for "Starting Date" and "End Date"
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1)); // Merge first and second columns
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 3)); // Merge third and fourth columns

        // Add "Starting Date" and "End Date"
        Cell startDateCell = dateRow.createCell(0);
        startDateCell.setCellValue("Starting Date: " + dataList.getFirst().getStartDate());
        startDateCell.setCellStyle(dateRowStyle);

        Cell endDateCell = dateRow.createCell(2);
        endDateCell.setCellValue("End Date: " + dataList.getFirst().getEndDate());
        endDateCell.setCellStyle(dateRowStyle);

        // Create header row (second row, index 1)
        Row headerRow = sheet.createRow(1);
        String[] headers = {
                "Date", "Entry Time", "Late Duration", "Entry Comment", "Exit Time",
                "Time After Exit", "Exit Comment", "Out Time", "Total Time in Day",
                "Day Comment", "Comment"
        };

        // Create header row style
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Populate header row
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Add data rows
        int rowIndex = 2; // Start from row 2
        for (AttendanceDataForAnyPeriod data : dataList) {
            Row row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(data.getDate());
            row.createCell(1).setCellValue(data.getEntryTime());
            row.createCell(2).setCellValue(data.getLateDuration());
            row.createCell(3).setCellValue(data.getEntryComment());
            row.createCell(4).setCellValue(data.getExitTime());
            row.createCell(5).setCellValue(data.getTimeAfterExit());
            row.createCell(6).setCellValue(data.getExitComment());
            row.createCell(7).setCellValue(data.getOutTime());
            row.createCell(8).setCellValue(data.getTotalTimeInDay());
            row.createCell(9).setCellValue(data.getDayComment());
            row.createCell(10).setCellValue(data.getComment());
        }

        // Save the file to the Downloads folder
        String downloadsPath = System.getProperty("user.home") + File.separator + "Downloads";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File file = new File(downloadsPath, "Any Employee_Report_randomPeriod" + timestamp + ".xlsx");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
            System.out.println("Excel file saved successfully at: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error occurred while exporting data.");
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.ok("Data exported successfully");
    }

    public List<AttendanceDataForAnyPeriod> getAttendanceDataForAnyPeriod(String employeeId,String employeeName,String startDate1,String endDate1){
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Convert to ChronoLocalDate
        ChronoLocalDate startDate = LocalDate.parse(startDate1, formatter1);
        ChronoLocalDate endDate=LocalDate.parse(endDate1, formatter1);
        String  selectedparson=employeeName;
        List<AttendanceDataForAnyPeriod> resultList=new ArrayList<>();

        List<AttendanceData> dataList=attendanceDataRepository.findByUpdateStatus("1");
        employeeList=userService.employeeList();
        if(dataList.size()>0)
        {
            employeeList.forEach(f->{
                dataList.forEach(e->{
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    databaseDate=null;
                    // Convert the string to LocalDate
                    try {
                        databaseDate = LocalDate.parse(e.getEntryDate(), dateFormatter);
                        //System.out.println("Converted LocalDate: " + databaseDate);
                    } catch (DateTimeParseException g) {
                        g.printStackTrace(); // Handle parsing exception
                    }
                    if (databaseDate != null) {
                        if (((databaseDate.equals(startDate) || databaseDate.equals(endDate))&& f.getName().equals(e.getName()) )||
                                (databaseDate.isAfter(startDate) && databaseDate.isBefore(endDate)&& f.getName().equals(e.getName()))) {

                             if(selectedparson.equals(e.getName())){
                                if(e.getStatus().equals("Absent")||e.getStatus().equals("Leave")||e.getStatus().equals("Holiday"))
                                {
                                   // String date=Integer.toString(e.getEntryTime().getDayOfMonth());
                                    String date=e.getEntryDate();
                                    resultList.add(new AttendanceDataForAnyPeriod(
                                        employeeId,
                                        employeeName,
                                        startDate1,
                                        endDate1,
                                        date,
                                        "❌"
                                          ,
                                        "❌ ",
                                        "❌ ",
                                        " ❌",
                                        "❌ ",
                                         "❌ ",
                                         "❌ ",
                                         "❌ ",
                                         " ❌",
                                         e.getStatus()

                                    ));

                                }
                                else {


                                   // String date=Integer.toString(e.getEntryTime().getDayOfMonth());
                                    String date=e.getEntryDate();
                                    // Define the desired format
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
                                    // Format the LocalDateTime using the formatter
                                    String entrytime= e.getEntryTime().format(formatter);




                                    LocalTime currentTime = e.getEntryTime().toLocalTime();

                                    // Define the threshold time (09:15 AM)
                                    LocalTime lateThreshold =LocalTime.of(returnSettingStartHour(e.getEmployeeId(),e.getName(),e.getEntryDate()), returnGlobalSettingLateMinute(e.getEntryDate()));

                                    // Compare with the threshold and print the result
                                    String lateduration;
                                    String entrycomment;

                                    if (currentTime.isAfter(lateThreshold)) {
                                        LocalTime lateThreshold1 =LocalTime.of(returnSettingStartHour(e.getEmployeeId(),e.getName(),e.getEntryDate()),returnSettingStartMinute(e.getEmployeeId(),e.getName(),e.getEntryDate()));
                                        Duration duration = Duration.between(lateThreshold1,currentTime);
                                        long hours = duration.toHoursPart();
                                        long minutes = duration.toMinutesPart();
                                        lateduration=hours+":"+ minutes;
                                        entrycomment="Late";
                                    } else {
                                        // if 9:00 >9:00 then count it.
                                        LocalTime lateThreshold1 =LocalTime.of(returnSettingStartHour(e.getEmployeeId(),e.getName(),e.getEntryDate()),returnSettingStartMinute(e.getEmployeeId(),e.getName(),e.getEntryDate()));
                                        if (currentTime.isAfter(lateThreshold1)) {
                                            Duration duration = Duration.between(lateThreshold1,currentTime);

                                            long hours = duration.toHoursPart();
                                            long minutes = duration.toMinutesPart();


                                            lateduration=hours+":"+ minutes;
                                        }
                                        else {
                                            lateduration="00:00";
                                        }

                                        entrycomment="In Time";
                                    }


                                    String exittime= e.getExitTime().format(formatter);


                                    currentTime = e.getExitTime().toLocalTime();

                                    String[] data=subtractHourMinute(returnSettingEndHour(e.getEmployeeId(),e.getName(),e.getEntryDate()),returnGlobalSettingEarlyMinute(e.getEntryDate()));
                                    LocalTime  overtimeThreshold = LocalTime.of(Integer.parseInt(data[0]),Integer.parseInt(data[1]) );

                                    // Compare with the threshold and print the result
                                    String overtimeduration;
                                    String exitcomment;
                                    System.out.println(overtimeThreshold+" "+currentTime);
                                    if (currentTime.isBefore(overtimeThreshold)) {
                                        LocalTime  overtimeThreshold1 = LocalTime.of(returnSettingEndHour(e.getEmployeeId(),e.getName(),e.getEntryDate()), returnSettingEndMinute(e.getEmployeeId(),e.getName(),e.getEntryDate()));
                                        Duration duration = Duration.between(currentTime,overtimeThreshold1);

                                        long hours = duration.toHours();
                                        long minutes = duration.minusHours(hours).toMinutes();

                                        overtimeduration="-"+hours+":"+ minutes;
                                        exitcomment="Early";

                                    } else {
                                        LocalTime  overtimeThreshold1 = LocalTime.of(returnSettingEndHour(e.getEmployeeId(),e.getName(),e.getEntryDate()), returnSettingEndMinute(e.getEmployeeId(),e.getName(),e.getEntryDate()));
                                        Duration duration = Duration.between(overtimeThreshold1,currentTime);

                                        long hours = duration.toHours();
                                        long minutes = duration.minusHours(hours).toMinutes();

                                        overtimeduration=hours+":"+ minutes;

                                        exitcomment="Ok";
                                    }



                                    Duration duration = Duration.between(e.getEntryTime(),e.getExitTime());

                                    long hours = duration.toHoursPart();
                                    long minutes = duration.toMinutesPart();

                                    String dayduration=hours+":"+minutes;
                                    int totalHours=returnSettingTotalHour(e.getEmployeeId(),e.getName(),e.getEntryDate());
                                    String daycomment= checkTimeDifference(duration,totalHours);
                                    String outtime=e.getOuttime();

                                    String status=e.getStatus();

                                   // tableView.getItems().add(new AttendanceData(date,entrytime,lateduration,entrycomment,exittime,overtimeduration,exitcomment,dayduration,daycomment,status));//
                                    resultList.add(new AttendanceDataForAnyPeriod(
                                            employeeId,
                                            employeeName,
                                            startDate1,
                                            endDate1,
                                            date,
                                            entrytime,
                                            lateduration,
                                            entrycomment,
                                            exittime,
                                            overtimeduration,
                                            exitcomment,
                                            outtime,
                                            dayduration,
                                            daycomment,
                                            status

                                    ));
                                }

                             }


                        }

                    }


                });

            });
        }



        // sorting table data
      //  tableView.getItems().sort(Comparator.comparing(AttendanceData::getDate));

        return resultList;

    }
    public ResponseEntity<String> updateAttendanceData(List<AttendanceDataForFixedDay> newData, List<AttendanceDataForFixedDay> oldData) {
        List<AttendanceDataForFixedDay> changedData = new ArrayList<>();

        // Step 1: Detect changed records
        for (AttendanceDataForFixedDay oldRecord : oldData) {
            Optional<AttendanceDataForFixedDay> matchingNewRecord = newData.stream()
                    .filter(newRecord -> newRecord.getEmployeeId().equals(oldRecord.getEmployeeId())
                            && newRecord.getDate().equals(oldRecord.getDate()))
                    .findFirst();

            if (matchingNewRecord.isPresent()) {
                AttendanceDataForFixedDay newRecord = matchingNewRecord.get();

                // Manually compare fields instead of using equals()
                if (!hasSameData(oldRecord, newRecord)) {
                    changedData.add(newRecord); // ✅ Store newRecord instead of oldRecord
                }
            }
        }

        // Log changed data
        if (!changedData.isEmpty()) {
            changedData.forEach(record -> System.out.println("Changed Data: " + record));
        }

        // Step 2: Update old records' status to "0" (Only for changed records)
        changedData.forEach(e -> {
            Optional<AttendanceData> data = attendanceDataRepository.findByEmployeeIdAndEntryDateAndUpdateStatus(e.getEmployeeId(), e.getDate(), "1");
            data.ifPresent(view -> {
                view.setUpdateStatus("0");
                attendanceDataRepository.save(view);
            });
        });

        // Step 3: Save only changed records
        changedData.forEach(e -> {
            LocalDate entryDate = LocalDate.parse(e.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate exitDate = entryDate;

            AttendanceData attendanceData = new AttendanceData(
                    e.getEmployeeId(),
                    e.getName(),
                    Integer.toString(entryDate.getMonthValue()),
                    Integer.toString(entryDate.getYear()),
                    createLocalDateTime(entryDate.getYear(), entryDate.getMonthValue(), entryDate.getDayOfMonth(), Integer.parseInt(e.getStartHour()), Integer.parseInt(e.getStartMinute()), e.getStartPeriod()),
                    e.getLateEntryReason(),
                    createLocalDateTime(exitDate.getYear(), exitDate.getMonthValue(), exitDate.getDayOfMonth(), Integer.parseInt(e.getExitHour()), Integer.parseInt(e.getExitMinute()), e.getExitPeriod()),
                    e.getEarlyExitReason(),
                    e.getStatus(),
                    e.getOutHour() + "." + e.getOutMinute(),
                    entryDate.toString(),
                    LocalDateTime.now(),
                    "1",
                    e.getGlobalDayStatus()
            );

            attendanceDataRepository.save(attendanceData);
        });

        return ResponseEntity.ok("Successfully Updated");
    }


    private boolean hasSameData(AttendanceDataForFixedDay oldRecord, AttendanceDataForFixedDay newRecord) {
        boolean isSame = oldRecord.getStartHour().equals(newRecord.getStartHour()) &&
                oldRecord.getStartMinute().equals(newRecord.getStartMinute()) &&
                oldRecord.getExitHour().equals(newRecord.getExitHour()) &&
                oldRecord.getExitMinute().equals(newRecord.getExitMinute()) &&
                oldRecord.getEarlyExitReason().equals(newRecord.getEarlyExitReason()) &&
                oldRecord.getLateEntryReason().equals(newRecord.getLateEntryReason()) &&
                oldRecord.getOutHour().equals(newRecord.getOutHour()) &&
                oldRecord.getOutMinute().equals(newRecord.getOutMinute()) &&
                oldRecord.getGlobalDayStatus().equals(newRecord.getGlobalDayStatus()) &&
                oldRecord.getStatus().equals(newRecord.getStatus());

        if (!isSame) {
            System.out.println("Difference detected between:");
            System.out.println("Old: " + oldRecord);
            System.out.println("New: " + newRecord);
        }

        return isSame;
    }

    public LocalDateTime createLocalDateTime(int year, int month, int dayOfMonth, int hour, int minute, String amPm) {
        int adjustedHour = hour % 12;
        if (amPm.equalsIgnoreCase("PM")) {
            adjustedHour += 12;
        }
        return LocalDateTime.of(year, month, dayOfMonth, adjustedHour, minute);
    }

    public static String[] subtractHourMinute(int hour, int minutesToSubtract) {
        // Convert everything to minutes for easier calculation
        int totalMinutes = hour * 60 ;
        totalMinutes -= minutesToSubtract;

        // Handle negative result
        if (totalMinutes < 0) {
            totalMinutes += 24 * 60; // Assuming a 24-hour clock
        }

        // Calculate resulting hour and minute
        int resultingHour = totalMinutes / 60;
        int resultingMinute = totalMinutes % 60;

        // Format the result
        String[] result = new String[2];
        result[0] = String.valueOf(resultingHour);
        result[1] = String.valueOf(resultingMinute);
        return result;
    }
    public static String checkTimeDifference(Duration duration , int totalHours) {

        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();

        String result;
        if (hours < totalHours ) {
            result = "Short time";
        } else if (hours > totalHours|| (hours == totalHours && minutes > 0)) {
            result = "Extra time";
        } else {
            result = "Required time";
        }

        return result;
    }
    public int returnGlobalSettingLateMinute(String insertDataDate1)
    {
        List<GlobalSetting> globalSettingdata=globalSettingRepository.findAllByStatus("1");
        int hour = 9; // Default hour value

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = globalSettingdata.size() - 1; i >= 0; i--) {

            try {
                Date insertDataDate = dateFormat.parse(insertDataDate1);
                Date startDate = dateFormat.parse(globalSettingdata.get(i).getFormattedBirthDate());
                Date endDate = dateFormat.parse(globalSettingdata.get(i).getFormattedDeathDate());

                // Check if insertDataDate is between startDate and endDate (inclusive)
                if ((insertDataDate.equals(startDate) || insertDataDate.equals(endDate)) ||
                        (insertDataDate.after(startDate) && insertDataDate.before(endDate))) {
                    hour = Integer.parseInt(globalSettingdata.get(i).getLateMinute());
                    break;
                }
            } catch (java.text.ParseException e) {
                // Handle parsing exception appropriately, e.g., logging
                e.printStackTrace();
            }
        }



        return hour;
    }
    public int returnGlobalSettingEarlyMinute(String insertDataDate1)
    {
        List<GlobalSetting> globalSettingdata=globalSettingRepository.findAllByStatus("1");
        int hour = 9; // Default hour value

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = globalSettingdata.size() - 1; i >= 0; i--) {

            try {
                Date insertDataDate = dateFormat.parse(insertDataDate1);
                Date startDate = dateFormat.parse(globalSettingdata.get(i).getFormattedBirthDate());
                Date endDate = dateFormat.parse(globalSettingdata.get(i).getFormattedDeathDate());

                // Check if insertDataDate is between startDate and endDate (inclusive)
                if ((insertDataDate.equals(startDate) || insertDataDate.equals(endDate)) ||
                        (insertDataDate.after(startDate) && insertDataDate.before(endDate))) {
                    hour = Integer.parseInt(globalSettingdata.get(i).getEarlyMinute());
                    break;
                }
            } catch (java.text.ParseException e) {
                // Handle parsing exception appropriately, e.g., logging
                e.printStackTrace();
            }
        }



        return hour;
    }
    public int returnSettingStartMinute(String id, String name,String insertDataDate1)
    {
        List<LocalSetting> localSettingdata=localSettingRepository.findAllByStatus("1");
        int hour = 9; // Default hour value

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = localSettingdata.size() - 1; i >= 0; i--) {
            if (id.equals(localSettingdata.get(i).getEmployeeId()) && name.equals(localSettingdata.get(i).getName())) {
                try {
                    Date insertDataDate = dateFormat.parse(insertDataDate1);
                    Date startDate = dateFormat.parse(localSettingdata.get(i).getFormattedBirthDate());
                    Date endDate = dateFormat.parse(localSettingdata.get(i).getFormattedDeathDate());

                    // Check if insertDataDate is between startDate and endDate (inclusive)
                    if ((insertDataDate.equals(startDate) || insertDataDate.equals(endDate)) ||
                            (insertDataDate.after(startDate) && insertDataDate.before(endDate))) {
                        hour = Integer.parseInt(localSettingdata.get(i).getStartMinute());
                        break;
                    }
                } catch (java.text.ParseException e) {
                    // Handle parsing exception appropriately, e.g., logging
                    e.printStackTrace();
                }
            }
        }


        return hour;
    }
    public int returnSettingEndHour(String id, String name,String insertDataDate1)
    {
        List<LocalSetting> localSettingdata=localSettingRepository.findAllByStatus("1");
        int hour = 9; // Default hour value

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = localSettingdata.size() - 1; i >= 0; i--) {
            if (id.equals(localSettingdata.get(i).getEmployeeId()) && name.equals(localSettingdata.get(i).getName())) {
                try {
                    Date insertDataDate = dateFormat.parse(insertDataDate1);
                    Date startDate = dateFormat.parse(localSettingdata.get(i).getFormattedBirthDate());
                    Date endDate = dateFormat.parse(localSettingdata.get(i).getFormattedDeathDate());

                    // Check if insertDataDate is between startDate and endDate (inclusive)
                    if ((insertDataDate.equals(startDate) || insertDataDate.equals(endDate)) ||
                            (insertDataDate.after(startDate) && insertDataDate.before(endDate))) {
                        hour = Integer.parseInt(localSettingdata.get(i).getEndHours());
                        break;
                    }
                } catch (java.text.ParseException e) {
                    // Handle parsing exception appropriately, e.g., logging
                    e.printStackTrace();
                }
            }
        }


        return hour;
    }
    public int returnSettingEndMinute(String id, String name,String insertDataDate1)
    {
        List<LocalSetting> localSettingdata=localSettingRepository.findAllByStatus("1");
        int hour = 9; // Default hour value

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = localSettingdata.size() - 1; i >= 0; i--) {
            if (id.equals(localSettingdata.get(i).getEmployeeId()) && name.equals(localSettingdata.get(i).getName())) {
                try {
                    Date insertDataDate = dateFormat.parse(insertDataDate1);
                    Date startDate = dateFormat.parse(localSettingdata.get(i).getFormattedBirthDate());
                    Date endDate = dateFormat.parse(localSettingdata.get(i).getFormattedDeathDate());

                    // Check if insertDataDate is between startDate and endDate (inclusive)
                    if ((insertDataDate.equals(startDate) || insertDataDate.equals(endDate)) ||
                            (insertDataDate.after(startDate) && insertDataDate.before(endDate))) {
                        hour = Integer.parseInt(localSettingdata.get(i).getEndMinute());
                        break;
                    }
                } catch (java.text.ParseException e) {
                    // Handle parsing exception appropriately, e.g., logging
                    e.printStackTrace();
                }
            }
        }


        return hour;
    }
    public int returnSettingStartHour(String id, String name,String insertDataDate1)
    {
        List<LocalSetting> localSettingdata=localSettingRepository.findAllByStatus("1");
        int hour = 9; // Default hour value

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = localSettingdata.size() - 1; i >= 0; i--) {
            if (id.equals(localSettingdata.get(i).getEmployeeId()) && name.equals(localSettingdata.get(i).getName())) {
                try {
                    Date insertDataDate = dateFormat.parse(insertDataDate1);
                    Date startDate = dateFormat.parse(localSettingdata.get(i).getFormattedBirthDate());
                    Date endDate = dateFormat.parse(localSettingdata.get(i).getFormattedDeathDate());

                    // Check if insertDataDate is between startDate and endDate (inclusive)
                    if ((insertDataDate.equals(startDate) || insertDataDate.equals(endDate)) ||
                            (insertDataDate.after(startDate) && insertDataDate.before(endDate))) {
                        hour = Integer.parseInt(localSettingdata.get(i).getStartHours());
                        break;
                    }
                } catch (java.text.ParseException e) {
                    // Handle parsing exception appropriately, e.g., logging
                    e.printStackTrace();
                }
            }
        }


        return hour;
    }
    public int returnSettingTotalHour(String id, String name,String insertDataDate1)
    {
        List<LocalSetting> localSettingdata=localSettingRepository.findAllByStatus("1");
        int hour = 8; // Default hour value

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = localSettingdata.size() - 1; i >= 0; i--) {
            if (id.equals(localSettingdata.get(i).getEmployeeId()) && name.equals(localSettingdata.get(i).getName())) {
                try {
                    Date insertDataDate = dateFormat.parse(insertDataDate1);
                    Date startDate = dateFormat.parse(localSettingdata.get(i).getFormattedBirthDate());
                    Date endDate = dateFormat.parse(localSettingdata.get(i).getFormattedDeathDate());

                    // Check if insertDataDate is between startDate and endDate (inclusive)
                    if ((insertDataDate.equals(startDate) || insertDataDate.equals(endDate)) ||
                            (insertDataDate.after(startDate) && insertDataDate.before(endDate))) {
                        hour = Integer.parseInt(localSettingdata.get(i).getTotalHours());
                        break;
                    }
                } catch (java.text.ParseException e) {
                    // Handle parsing exception appropriately, e.g., logging
                    e.printStackTrace();
                }
            }
        }


        return hour;
    }
    public static Duration addMinutesToDuration(Duration originalDuration, long minutesToAdd) {
        return originalDuration.plusMinutes(minutesToAdd);
    }
}
