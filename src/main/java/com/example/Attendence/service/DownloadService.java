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
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DownloadService {

    LocalDate databaseDate=null;
    Duration durationc=Duration.ZERO,totallatedurationc=Duration.ZERO,totalextradurationc=Duration.ZERO;Duration totaltimecc=Duration.ZERO;
    Duration intotaltimec=Duration.ZERO;
    int officedayc=0,presentdayc=0,avgtimec=0,totaltimec=0,leavedayc=0,absentdayc=0,holydayc=0,shorttimec=0,regulartimec=0,extratimec=0,intimec=0,latetimec=0,totallatetimec=0,okc=0,earlytimec=0,totalextratimec=0;
    List<Employee> employeeList;
    long timeInSecond=0,totalExtraTime=0, timeInSecondOfOutTime=0;
    double outtimec=0;
    boolean result;

    @Autowired
    UserService userService;

    @Autowired
    LocalSettingRepository localSettingRepository;

    @Autowired
    GlobalSettingRepository globalSettingRepository;

    @Autowired
    private AttendanceDataRepository attendanceDataRepository;


    public List<AttendanceDataForFixedDay> getAllEmployeeAttendanceDataForFixedDay(String selectedDate,String header){
        List <AttendanceDataForFixedDay> resultlist=new ArrayList<>();
        List<AttendanceData> dataList=attendanceDataRepository.findByEntryDateAndUpdateStatus(selectedDate,"1");
        employeeList=userService.employeeList(header);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        employeeList.forEach(user->{
            dataList.forEach(data->{

                if (user.getName().equals(data.getName()) && user.getIdNumber().equals(data.getEmployeeId())) {
                    // Your logic here
                    AttendanceDataForFixedDay view=new AttendanceDataForFixedDay(
                         selectedDate,
                         user.getIdNumber(),
                         user.getName(),
                         data.getEntryTime().format(formatter).substring(0,2),
                         data.getEntryTime().format(formatter).substring(3,5),
                         data.getLateEntryReason(),
                         data.getEntryTime().format(formatter).substring(6,8),

                        data.getExitTime().format(formatter).substring(0,2),
                        data.getExitTime().format(formatter).substring(3,5),
                        data.getExitTime().format(formatter).substring(6,8),
                        data.getEarlyExitReason(),
                        separateString(data.getOuttime())[0],
                        separateString(data.getOuttime())[1],
                            data.getUpdateStatus(),
                            data.getGlobalDayStatus(),
                            data.getStatus()
                    );

                    resultlist.add(view);

                }

            });
        });

        return resultlist;

    }

    public static String[] separateString(String input) {

        String[] result = new String[2];

        // Split the string into integer and decimal parts using the dot (.) as a separator
        String[] parts = input.split("\\.");

        // If the string has only an integer part, set decimal part as empty
        result[0] = parts[0];
        result[1] = (parts.length > 1) ? parts[1] : "0";

        return result;
    }

    public ResponseEntity<String> exportAllAttendanceData(List<AllEmployeeAttendanceData> dataList) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employee Data");

        // Create a style for the "Starting Date" and "End Date" row
        CellStyle dateRowStyle = workbook.createCellStyle();
        dateRowStyle.setAlignment(HorizontalAlignment.CENTER);
        dateRowStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Create the first row with Starting Date and End Date
        Row dateRow = sheet.createRow(0);

        // Merge cells for "Starting Date" and "End Date"
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));  // Merge first and second columns

        // Add "Starting Date" in the first cell and "End Date" in the second
        Cell startDateCell = dateRow.createCell(0);
        startDateCell.setCellValue("Starting Date: " + dataList.getFirst().getStartDate());
        startDateCell.setCellStyle(dateRowStyle);

        // Merge cells for the "End Date" text
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 3));  // Merge third and fourth columns
        Cell endDateCell = dateRow.createCell(2);
        endDateCell.setCellValue("End Date: " + dataList.getFirst().getEndDate());
        endDateCell.setCellStyle(dateRowStyle);

        // Create header row (second row, index 1)
        Row headerRow = sheet.createRow(1);
        String[] headers = {
                "Employee ID", "Name", "Office Day", "Total Present", "Avg Time",
                "Leave", "Absent", "Holiday", "Short Time", "Maintain Office Duration",
                "Extra Time", "Entry In Time", "Entry Late", "Entry Total Late",
                "Exit Ok", "Exit Early", "Total Extra Time", "Office Out Time",
                "Office In Time", "Total Time"
        };

        // Create header row with vertical text and centered
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setRotation((short) 90);  // Rotate text to 90 degrees (vertical)
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Populate header row with vertical and centered text
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);  // Apply vertical and centered text style
        }

        // Add data rows
        int rowIndex = 2; // Start from row 2, as row 0 and row 1 are taken
        for (AllEmployeeAttendanceData data : dataList) {
            Row row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(data.getSerial());
            row.createCell(1).setCellValue(data.getName());
            row.createCell(2).setCellValue(data.getOfficeDay());
            row.createCell(3).setCellValue(data.getTotalPresent());
            row.createCell(4).setCellValue(data.getAvgTime());
            row.createCell(5).setCellValue(data.getLeave());
            row.createCell(6).setCellValue(data.getAbsent());
            row.createCell(7).setCellValue(data.getHolyday());
            row.createCell(8).setCellValue(data.getShortTime());
            row.createCell(9).setCellValue(data.getRequiredTime());
            row.createCell(10).setCellValue(data.getExtraTime());
            row.createCell(11).setCellValue(data.getEntryInTime());
            row.createCell(12).setCellValue(data.getEntryLate());
            row.createCell(13).setCellValue(data.getEntryTotalLate());
            row.createCell(14).setCellValue(data.getExitOk());
            row.createCell(15).setCellValue(data.getExitEarly());
            row.createCell(16).setCellValue(data.getTotalExtraTime());
            row.createCell(17).setCellValue(data.getOfficeOutTime());
            row.createCell(18).setCellValue(data.getOfficeInTime());
            row.createCell(19).setCellValue(data.getTotalTime());
        }
            // Get the user's Downloads directory
        String downloadsPath = System.getProperty("user.home") + File.separator + "Downloads";

        // Generate a timestamp for the filename
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        // Create the file with the employee name and timestamp
        File file = new File(downloadsPath, "All_Employee_Report_" + timestamp + ".xlsx");

        // Save the workbook to the file
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

        // Return success message
        return ResponseEntity.ok("Successfully Exported to: " + file.getAbsolutePath());
    }

    public List<AllEmployeeAttendanceData> getAllEmployeeAttendanceData(String startDate1, String endDate1,String header){
        List<AllEmployeeAttendanceData> resultList=new ArrayList<>();
       // List<AttendanceData> dataList=attendanceDataRepository.findByUpdateStatusAndEntryDateBetween("1",startDate1,endDate1);
        employeeList=userService.employeeList(header);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Convert to ChronoLocalDate
        ChronoLocalDate startDate = LocalDate.parse(startDate1, formatter);
        ChronoLocalDate endDate=LocalDate.parse(endDate1, formatter);



            employeeList.forEach(f->{
                List<AttendanceData>   dataList=attendanceDataRepository.findByEmployeeIdAndUpdateStatusAndEntryDateBetween(f.getIdNumber(),"1",startDate1,endDate1);
                totalExtraTime=0;
                officedayc=0;presentdayc=0;avgtimec=0;leavedayc=0;absentdayc=0;holydayc=0;shorttimec=0;regulartimec=0;extratimec=0;intimec=0;latetimec=0;totallatetimec=0;okc=0;earlytimec=0;totalextratimec=0;
                durationc=Duration.ZERO;totallatedurationc=Duration.ZERO;totalextradurationc=Duration.ZERO;outtimec=0;totaltimecc=Duration.ZERO;
                intotaltimec=Duration.ZERO;outtimec=0;totaltimecc=Duration.ZERO;
                timeInSecond=0;
                result=false;
                timeInSecondOfOutTime=0;
                dataList.forEach(e->{
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    databaseDate= LocalDate.now(); // Initialize with a default value
                    // Convert the string to LocalDate
                    try {
                        databaseDate = LocalDate.parse(e.getEntryDate(), dateFormatter);
                        //System.out.println("Converted LocalDate: " + databaseDate);
                    } catch (DateTimeParseException g) {
                        g.printStackTrace(); // Handle parsing exception
                    }
                    if (databaseDate != null) {
                       // System.out.println(databaseDate+" "+startDate+" "+endDate);
                        if (((databaseDate.equals(startDate) || databaseDate.equals(endDate))&& f.getName().equals(e.getName()) )||
                                (databaseDate.isAfter(startDate) && databaseDate.isBefore(endDate)&& f.getName().equals(e.getName()))) {

                            LocalDate databaseDate1 = LocalDate.parse(f.getJoinDate(), dateFormatter);
                            if ((e.getName().equals(f.getName()) && databaseDate1.isBefore(databaseDate)) || (e.getName().equals(f.getName()) && databaseDate1.isEqual(databaseDate))) {
                                result=true;
                                if(!"Holiday".equals(e.getStatus()))
                                {
                                    officedayc++;
                                }

                                if("Present".equals(e.getStatus()))
                                {
                                    Duration durationBetweenEntryExit = Duration.between(e.getEntryTime(), e.getExitTime());
                                    timeInSecond=timeInSecond+durationBetweenEntryExit.toHoursPart()*60L*60L+durationBetweenEntryExit.toMinutesPart()*60L;
                                    durationc = durationc.plus(durationBetweenEntryExit);
                                  //  System.out.println("Current total duration (seconds): " + durationc.getSeconds());
                                    presentdayc++;
                                    long hours = durationBetweenEntryExit.toHoursPart();
                                    long minutes = durationBetweenEntryExit.toMinutesPart();

                                    // total hours calculate
                                    // need info:
                                    int settingHours= returnSettingTotalHour(e.getEmployeeId(),e.getName(),e.getEntryDate());
                                  //  System.out.println(settingHours);

                                    if (hours < settingHours ) {

                                        shorttimec++;
                                    } else if (hours > settingHours || (hours == settingHours && minutes > 0)) {

                                        extratimec++;
                                    } else {
                                        regulartimec++;

                                    }


                                    LocalTime lateThreshold = LocalTime.of(returnSettingStartHour(e.getEmployeeId(),e.getName(),e.getEntryDate()), (returnSettingStartMinute(e.getEmployeeId(),e.getName(),e.getEntryDate())+16));

                                    if ( e.getEntryTime().toLocalTime().isBefore(lateThreshold)) {
                                        intimec++;
                                    }

                                    lateThreshold = LocalTime.of(returnSettingStartHour(e.getEmployeeId(),e.getName(),e.getEntryDate()), returnGlobalSettingLateMinute(e.getEntryDate()));
                                    // late count
                                    if ( e.getEntryTime().toLocalTime().isAfter(lateThreshold)) {
                                        latetimec++;

                                        //late duration count
                                        Duration duration = Duration.between(lateThreshold, e.getEntryTime().toLocalTime());
                                        duration= addMinutesToDuration(duration , 15);
                                        totallatedurationc=totallatedurationc.plus(duration);


                                    }


                                    LocalTime exitThreshold = LocalTime.of(returnSettingEndHour(e.getEmployeeId(),e.getName(),e.getEntryDate()), returnSettingEndMinute(e.getEmployeeId(),e.getName(),e.getEntryDate()));
                                    if ( e.getExitTime().toLocalTime().isBefore(exitThreshold)) {
                                        earlytimec++;
                                    }



                                    if ( e.getExitTime().toLocalTime().isAfter(exitThreshold)) {
                                        // 15 minute extra time count
                                        LocalTime exitThreshold1 = LocalTime.of(returnSettingEndHour(e.getEmployeeId(),e.getName(),e.getEntryDate()), 15+returnSettingEndMinute(e.getEmployeeId(),e.getName(),e.getEntryDate()));

                                        if(e.getExitTime().toLocalTime().isAfter(exitThreshold1)){
                                            Duration duration = Duration.between(exitThreshold, e.getExitTime().toLocalTime());
                                            totalExtraTime=totalExtraTime+duration.toHoursPart()*60L*60L+duration.toMinutesPart()*60L;
                                            // totalextradurationc=totalextradurationc.plus(duration);
                                        }


                                    }

                                    String regex = "(\\d+)\\.(\\d+)";

                                    Pattern pattern = Pattern.compile(regex);
                                    Matcher matcher = pattern.matcher(e.getOuttime());
                                    if (matcher.matches()) {
                                        String integerPart = matcher.group(1);
                                        String fractionalPart = matcher.group(2);
                                        timeInSecondOfOutTime=timeInSecondOfOutTime+Long.parseLong(integerPart)*60L*60L+Long.parseLong(fractionalPart)*60L;

                                       // System.out.println("Integer Part: " + integerPart);
                                      //  System.out.println("Fractional Part: " + fractionalPart);
                                    } else {
                                        //System.out.println("The input is not a valid decimal number.");
                                    }


                                }

                                if("Leave".equals(e.getStatus()))
                                {


                                    leavedayc++;
                                }

                                if("Absent".equals(e.getStatus()))
                                {


                                    absentdayc++;
                                }

                                if("Holyday".equals(e.getStatus()))
                                {


                                    holydayc++;
                                }


                            }
                        }
                    }




                });

                if(result)
                {

                    intotaltimec=Duration.ofSeconds(timeInSecond);

                    Duration outtimeduration = Duration.ofSeconds(timeInSecondOfOutTime);


                    totaltimecc= intotaltimec.plus(outtimeduration);


                    if(presentdayc!=0)
                    {
                        long totalSeconds =  timeInSecond;
                        long averageSeconds = totalSeconds / presentdayc;
                     //   System.out.println("Avg second " + totalSeconds);
                        durationc = Duration.ofSeconds(averageSeconds);
                        /// total extra time
                        totalextradurationc=Duration.ofSeconds(totalExtraTime);
                    }

                    resultList.add(new AllEmployeeAttendanceData(
                            startDate1
                            ,
                            endDate1,f.getIdNumber(),f.getName(),Integer.toString(officedayc),Integer.toString(presentdayc),
                            durationc.toHoursPart()+":"+ durationc.toMinutesPart(),
                            Integer.toString(leavedayc),
                            Integer.toString(absentdayc),
                            Integer.toString(holydayc),
                            Integer.toString(shorttimec),
                            Integer.toString(regulartimec),
                            Integer.toString(extratimec),
                            Integer.toString(intimec),
                            Integer.toString(latetimec),
                            totallatedurationc.toHoursPart()+":"+ totallatedurationc.toMinutesPart(),
                            Integer.toString(presentdayc),
                            Integer.toString(earlytimec),
                            totalextradurationc.toHoursPart()+":"+ totalextradurationc.toMinutesPart(),
                            outtimeduration.toHours() + ":" + outtimeduration.toMinutesPart(),
                            intotaltimec.toHours()+":"+ intotaltimec.toMinutesPart(),
                            totaltimecc.toHours()+":"+ totaltimecc.toMinutesPart()));
                }




            });




        return  resultList;
    }
    public static String checkTimeDifference(Duration duration) {

        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();

        String result;
        if (hours < 8 ) {
            result = "Short time";
        } else if (hours > 8 || (hours == 8 && minutes > 0)) {
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
        int hour = 17; // Default hour value

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
        int hour = 0; // Default hour value

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
