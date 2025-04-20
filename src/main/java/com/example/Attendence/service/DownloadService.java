package com.example.Attendence.service;

import com.example.Attendence.model.*;
import com.example.Attendence.repository.AttendanceDataRepository;
import com.example.Attendence.repository.GlobalSettingRepository;
import com.example.Attendence.repository.LocalSettingRepository;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DownloadService {

    private ThreadLocal<LocalDate> databaseDate = ThreadLocal.withInitial(() -> null);
    private ThreadLocal<Duration> durationc = ThreadLocal.withInitial(() -> Duration.ZERO);
    private ThreadLocal<Duration> totallatedurationc = ThreadLocal.withInitial(() -> Duration.ZERO);
    private ThreadLocal<Duration> totalextradurationc = ThreadLocal.withInitial(() -> Duration.ZERO);
    private ThreadLocal<Duration> totaltimecc = ThreadLocal.withInitial(() -> Duration.ZERO);
    private ThreadLocal<Duration> intotaltimec = ThreadLocal.withInitial(() -> Duration.ZERO);
    private ThreadLocal<Integer> officedayc = ThreadLocal.withInitial(() -> 0);
    private ThreadLocal<Integer> presentdayc = ThreadLocal.withInitial(() -> 0);
    private ThreadLocal<Integer> avgtimec = ThreadLocal.withInitial(() -> 0);
    private ThreadLocal<Integer> totaltimec = ThreadLocal.withInitial(() -> 0);
    private ThreadLocal<Integer> leavedayc = ThreadLocal.withInitial(() -> 0);
    private ThreadLocal<Integer> absentdayc = ThreadLocal.withInitial(() -> 0);
    private ThreadLocal<Integer> holydayc = ThreadLocal.withInitial(() -> 0);
    private ThreadLocal<Integer> shorttimec = ThreadLocal.withInitial(() -> 0);
    private ThreadLocal<Integer> regulartimec = ThreadLocal.withInitial(() -> 0);
    private ThreadLocal<Integer> extratimec = ThreadLocal.withInitial(() -> 0);
    private ThreadLocal<Integer> intimec = ThreadLocal.withInitial(() -> 0);
    private ThreadLocal<Integer> latetimec = ThreadLocal.withInitial(() -> 0);
    private ThreadLocal<Integer> totallatetimec = ThreadLocal.withInitial(() -> 0);
    private ThreadLocal<Integer> okc = ThreadLocal.withInitial(() -> 0);
    private ThreadLocal<Integer> earlytimec = ThreadLocal.withInitial(() -> 0);
    private ThreadLocal<Integer> totalextratimec = ThreadLocal.withInitial(() -> 0);
    private ThreadLocal<Long> timeInSecond = ThreadLocal.withInitial(() -> 0L);
    private ThreadLocal<Long> totalExtraTime = ThreadLocal.withInitial(() -> 0L);
    private ThreadLocal<Long> timeInSecondOfOutTime = ThreadLocal.withInitial(() -> 0L);
    private ThreadLocal<Double> outtimec = ThreadLocal.withInitial(() -> 0.0);
    private ThreadLocal<Boolean> result = ThreadLocal.withInitial(() -> false);



    List<Employee> employeeList;

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

    public void exportAllAttendanceData(List<AllEmployeeAttendanceData> dataList, HttpServletResponse response) {
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
        // Create a filename
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "Employee_Report_" + timestamp + ".xlsx";

        // Set download response headers
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        try (ServletOutputStream out = response.getOutputStream()) {
            workbook.write(out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<AllEmployeeAttendanceData> getAllEmployeeAttendanceData(String startDate1, String endDate1,String header){

        List<AllEmployeeAttendanceData> resultList=new ArrayList<>();
       // List<AttendanceData> dataList=attendanceDataRepository.findByUpdateStatusAndEntryDateBetween("1",startDate1,endDate1);
        employeeList=userService.employeeList(header);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Convert to ChronoLocalDate
        ChronoLocalDate startDate = LocalDate.parse(startDate1, formatter);
        ChronoLocalDate endDate=LocalDate.parse(endDate1, formatter);


            employeeList.parallelStream().forEach(f->{
                List<AttendanceData>   dataList=attendanceDataRepository.findByEmployeeIdAndUpdateStatusAndEntryDateInclusive(f.getIdNumber(),"1",startDate1,endDate1);
               // List<AttendanceData>  dataList=attendanceDataMap.get(f.getIdNumber());
                // If dataList is null or empty, handle the case
                if (dataList == null || dataList.isEmpty()) {
                    // Log the case where no data is found for this employee
                  //  System.out.println("No attendance data found for employee: " + f.getIdNumber());
                    return; // Skip processing for this employee
                }

                totalExtraTime.set(0L);
                officedayc.set(0);presentdayc.set(0);avgtimec.set(0);leavedayc.set(0);absentdayc.set(0);holydayc.set(0);shorttimec.set(0);regulartimec.set(0);extratimec.set(0);intimec.set(0);latetimec.set(0);totallatetimec.set(0);okc.set(0);earlytimec.set(0);totalextratimec.set(0);
                durationc.set(Duration.ZERO);totallatedurationc.set(Duration.ZERO);totalextradurationc.set(Duration.ZERO);outtimec.set(0.0);totaltimecc.set(Duration.ZERO);
                intotaltimec.set(Duration.ZERO);outtimec.set(0.0);totaltimecc.set(Duration.ZERO);
                timeInSecond.set(0L);
                result.set(false);
                timeInSecondOfOutTime.set(0L);

                dataList.forEach(e->{

                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    databaseDate.set(LocalDate.now()); // Initialize with a default value
                    // Convert the string to LocalDate
                    try {
                        databaseDate.set(LocalDate.parse(e.getEntryDate(), dateFormatter));
                        //System.out.println("Converted LocalDate: " + databaseDate);
                    } catch (DateTimeParseException g) {
                        g.printStackTrace(); // Handle parsing exception
                    }
                    if (databaseDate != null) {
                       // System.out.println(databaseDate+" "+startDate+" "+endDate);
                         LocalDate date = databaseDate.get();
                        if (!date.isBefore(startDate) && !date.isAfter(endDate) && f.getName().equals(e.getName())) {
                           // check join date
                            LocalDate databaseDate1 = LocalDate.parse(f.getJoinDate(), dateFormatter);
                            if ((e.getName().equals(f.getName()) && databaseDate1.isBefore(databaseDate.get())) || (e.getName().equals(f.getName()) && databaseDate1.isEqual(databaseDate.get()))) {
                                result.set(true);


                                if(!"Holiday".equals(e.getStatus()))
                                {

                                    officedayc.set(officedayc.get()+1);


                                }

                                if("Present".equals(e.getStatus()))
                                {
                                    Duration durationBetweenEntryExit = Duration.between(e.getEntryTime(), e.getExitTime());
                                    timeInSecond.set(timeInSecond.get()+durationBetweenEntryExit.toHoursPart()*60L*60L+durationBetweenEntryExit.toMinutesPart()*60L);
                                    durationc.set(durationc.get().plus(durationBetweenEntryExit));
                                  //  System.out.println("Current total duration (seconds): " + durationc.getSeconds());
                                    presentdayc.set(presentdayc.get()+1);
                                    long hours = durationBetweenEntryExit.toHoursPart();
                                    long minutes = durationBetweenEntryExit.toMinutesPart();

                                    // total hours calculate
                                    // need info:
                                    int settingHours= returnSettingTotalHour(e.getEmployeeId(),e.getName(),e.getEntryDate());
                                  //  System.out.println(settingHours);

                                    if (hours < settingHours ) {


                                        shorttimec.set(shorttimec.get()+1);
                                    } else if (hours > settingHours || (hours == settingHours && minutes > 0)) {


                                        extratimec.set(extratimec.get()+1);
                                    } else {

                                        regulartimec.set(regulartimec.get()+1);

                                    }


                                    LocalTime lateThreshold = LocalTime.of(returnSettingStartHour(e.getEmployeeId(),e.getName(),e.getEntryDate()), (returnSettingStartMinute(e.getEmployeeId(),e.getName(),e.getEntryDate())+16));

                                    if ( e.getEntryTime().toLocalTime().isBefore(lateThreshold)) {

                                        intimec.set(intimec.get()+1);
                                    }

                                    lateThreshold = LocalTime.of(returnSettingStartHour(e.getEmployeeId(),e.getName(),e.getEntryDate()), returnGlobalSettingLateMinute(e.getEntryDate()));
                                    // late count
                                    if ( e.getEntryTime().toLocalTime().isAfter(lateThreshold)) {

                                        latetimec.set(latetimec.get()+1);

                                        //late duration count
                                        Duration duration = Duration.between(lateThreshold, e.getEntryTime().toLocalTime());
                                        duration= addMinutesToDuration(duration , 15);
                                        totallatedurationc.set(totallatedurationc.get().plus(duration));


                                    }


                                    LocalTime exitThreshold = LocalTime.of(returnSettingEndHour(e.getEmployeeId(),e.getName(),e.getEntryDate()), returnSettingEndMinute(e.getEmployeeId(),e.getName(),e.getEntryDate()));
                                    if ( e.getExitTime().toLocalTime().isBefore(exitThreshold)) {

                                        earlytimec.set(earlytimec.get()+1);
                                    }



                                    if ( e.getExitTime().toLocalTime().isAfter(exitThreshold)) {
                                        // 15 minute extra time count
                                        LocalTime exitThreshold1 = LocalTime.of(returnSettingEndHour(e.getEmployeeId(),e.getName(),e.getEntryDate()), 15+returnSettingEndMinute(e.getEmployeeId(),e.getName(),e.getEntryDate()));

                                        if(e.getExitTime().toLocalTime().isAfter(exitThreshold1)){
                                            Duration duration = Duration.between(exitThreshold, e.getExitTime().toLocalTime());
                                            totalExtraTime.set(totalExtraTime.get()+duration.toHoursPart()*60L*60L+duration.toMinutesPart()*60L);
                                            // totalextradurationc=totalextradurationc.plus(duration);
                                        }


                                    }

                                    String regex = "(\\d+)\\.(\\d+)";

                                    Pattern pattern = Pattern.compile(regex);
                                    Matcher matcher = pattern.matcher(e.getOuttime());
                                    if (matcher.matches()) {
                                        String integerPart = matcher.group(1);
                                        String fractionalPart = matcher.group(2);
                                        timeInSecondOfOutTime.set(timeInSecondOfOutTime.get()+Long.parseLong(integerPart)*60L*60L+Long.parseLong(fractionalPart)*60L);

                                       // System.out.println("Integer Part: " + integerPart);
                                       // System.out.println("Fractional Part: " + fractionalPart);
                                    } else {
                                     //   System.out.println("The input is not a valid decimal number.");
                                    }


                                }

                                if("Leave".equals(e.getStatus()))
                                {

                                    leavedayc.set(leavedayc.get()+1);
                                }

                                if("Absent".equals(e.getStatus()))
                                {

                                    absentdayc.set(absentdayc.get()+1);
                                }

                                if("Holiday".equals(e.getStatus()))
                                {

                                    holydayc.set(holydayc.get()+1);
                                }


                            }
                        }
                    }




                });

                if(result.get())
                {

                    intotaltimec.set(Duration.ofSeconds(timeInSecond.get()));


                    // Create a Duration object
                    Duration outtimeduration = Duration.ofSeconds(timeInSecondOfOutTime.get());


                    totaltimecc.set(intotaltimec.get().plus(outtimeduration));



                    if(presentdayc.get()!=0)
                    {
                        long totalSeconds =  timeInSecond.get();
                        long averageSeconds = totalSeconds / presentdayc.get();
                       // System.out.println(f.getName()+ " Avg second " + totalSeconds);
                        durationc.set(Duration.ofSeconds(averageSeconds));
                        /// total extra time
                        totalextradurationc.set(Duration.ofSeconds(totalExtraTime.get()));
                    }



                    resultList.add(new AllEmployeeAttendanceData(
                            startDate1
                            ,
                            endDate1,f.getIdNumber(),f.getName(),Integer.toString(officedayc.get()),Integer.toString(presentdayc.get()),
                            durationc.get().toHoursPart()+":"+ durationc.get().toMinutesPart(),
                            Integer.toString(leavedayc.get()),
                            Integer.toString(absentdayc.get()),
                            Integer.toString(holydayc.get()),
                            Integer.toString(shorttimec.get()),
                            Integer.toString(regulartimec.get()),
                            Integer.toString(extratimec.get()),
                            Integer.toString(intimec.get()),
                            Integer.toString(latetimec.get()),
                            totallatedurationc.get().toHoursPart()+":"+ totallatedurationc.get().toMinutesPart(),
                            Integer.toString(presentdayc.get()),
                            Integer.toString(earlytimec.get()),
                            totalextradurationc.get().toHoursPart()+":"+ totalextradurationc.get().toMinutesPart(),
                            outtimeduration.toHours() + ":" + outtimeduration.toMinutesPart(),
                            intotaltimec.get().toHours()+":"+ intotaltimec.get().toMinutesPart(),
                            totaltimecc.get().toHours()+":"+ totaltimecc.get().toMinutesPart()));
                }

            });


        // i want to rearrange resultList by IdNumber of attendanceData . compare with employeelist employeeId
// Rearrange the resultList based on employeeList order
        resultList.sort(new Comparator<AllEmployeeAttendanceData>() {
            @Override
            public int compare(AllEmployeeAttendanceData data1, AllEmployeeAttendanceData data2) {
                String idNumber1 = data1.getSerial();  // get EmployeeId from the AllEmployeeAttendanceData
                String idNumber2 = data2.getSerial();  // get EmployeeId from the AllEmployeeAttendanceData

                // Create a map to store employeeId and their index
                Map<String, Integer> employeeIndexMap = new HashMap<>();

                // Fill the map with employeeId -> index from employeeList
                for (int i = 0; i < employeeList.size(); i++) {
                    employeeIndexMap.put(employeeList.get(i).getIdNumber(), i);
                }

                // Get indices from the map
                Integer index1 = employeeIndexMap.get(idNumber1);
                Integer index2 = employeeIndexMap.get(idNumber2);

                // If either ID is not found, treat them as the end of the list or handle accordingly
                if (index1 == null) {
                    return 1; // Put elements with unknown employeeId at the end
                }
                if (index2 == null) {
                    return -1; // Put elements with unknown employeeId at the end
                }

                // Compare based on the employeeList index order
                return Integer.compare(index1, index2);
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
    public int returnGlobalSettingLateMinute(String insertDataDateStr) {
        int defaultMinute = 9;

        LocalDate insertDate;
        try {
            insertDate = LocalDate.parse(insertDataDateStr); // expects format: yyyy-MM-dd
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return defaultMinute;
        }

        return globalSettingRepository.findAllByStatus("1").stream()
                .sorted(Comparator.comparing(GlobalSetting::getFormattedBirthDate).reversed()) // reverse for latest first
                .map(setting -> {
                    try {
                        LocalDate start = LocalDate.parse(setting.getFormattedBirthDate());
                        LocalDate end = LocalDate.parse(setting.getFormattedDeathDate());
                        if (!insertDate.isBefore(start) && !insertDate.isAfter(end)) {
                            return Integer.parseInt(setting.getLateMinute());
                        }
                    } catch (DateTimeParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(defaultMinute);
    }

    public int returnGlobalSettingEarlyMinute(String insertDataDateStr) {
        int defaultMinute = 9;

        LocalDate insertDate;
        try {
            insertDate = LocalDate.parse(insertDataDateStr); // expects format "yyyy-MM-dd"
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return defaultMinute;
        }

        return globalSettingRepository.findAllByStatus("1").stream()
                .sorted(Comparator.comparing(GlobalSetting::getFormattedBirthDate).reversed())
                .map(setting -> {
                    try {
                        LocalDate start = LocalDate.parse(setting.getFormattedBirthDate());
                        LocalDate end = LocalDate.parse(setting.getFormattedDeathDate());

                        if (!insertDate.isBefore(start) && !insertDate.isAfter(end)) {
                            return Integer.parseInt(setting.getEarlyMinute());
                        }
                    } catch (DateTimeParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(defaultMinute);
    }

    public int returnSettingStartMinute(String id, String name, String insertDataDateStr) {
        int defaultMinute = 9;

        LocalDate insertDate;
        try {
            insertDate = LocalDate.parse(insertDataDateStr); // expects "yyyy-MM-dd"
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return defaultMinute;
        }

        return localSettingRepository.findAllByStatus("1").stream()
                .filter(s -> id.equals(s.getEmployeeId()) && name.equals(s.getName()))
                .sorted(Comparator.comparing(LocalSetting::getFormattedBirthDate).reversed())
                .map(setting -> {
                    try {
                        LocalDate start = LocalDate.parse(setting.getFormattedBirthDate());
                        LocalDate end = LocalDate.parse(setting.getFormattedDeathDate());

                        if (!insertDate.isBefore(start) && !insertDate.isAfter(end)) {
                            return Integer.parseInt(setting.getStartMinute());
                        }
                    } catch (DateTimeParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(defaultMinute);
    }

    public int returnSettingEndHour(String id, String name, String insertDataDateStr) {
        int defaultHour = 17;

        LocalDate insertDate;
        try {
            insertDate = LocalDate.parse(insertDataDateStr);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return defaultHour;
        }

        return localSettingRepository.findAllByStatus("1").stream()
                .filter(s -> id.equals(s.getEmployeeId()) && name.equals(s.getName()))
                .sorted(Comparator.comparing(LocalSetting::getFormattedBirthDate).reversed()) // assume dates are in "yyyy-MM-dd"
                .map(setting -> {
                    try {
                        LocalDate startDate = LocalDate.parse(setting.getFormattedBirthDate());
                        LocalDate endDate = LocalDate.parse(setting.getFormattedDeathDate());

                        if (!insertDate.isBefore(startDate) && !insertDate.isAfter(endDate)) {
                            return Integer.parseInt(setting.getEndHours());
                        }
                    } catch (DateTimeParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(defaultHour);
    }


    public int returnSettingEndMinute(String id, String name, String insertDataDateStr) {
        int defaultMinute = 9;

        LocalDate insertDate;
        try {
            insertDate = LocalDate.parse(insertDataDateStr); // Expects format "yyyy-MM-dd"
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return defaultMinute;
        }

        return localSettingRepository.findAllByStatus("1").stream()
                .filter(s -> id.equals(s.getEmployeeId()) && name.equals(s.getName()))
                .sorted(Comparator.comparing(LocalSetting::getFormattedBirthDate).reversed())
                .map(setting -> {
                    try {
                        LocalDate start = LocalDate.parse(setting.getFormattedBirthDate());
                        LocalDate end = LocalDate.parse(setting.getFormattedDeathDate());

                        if (!insertDate.isBefore(start) && !insertDate.isAfter(end)) {
                            return Integer.parseInt(setting.getEndMinute());
                        }
                    } catch (DateTimeParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(defaultMinute);
    }

    public int returnSettingStartHour(String id, String name, String insertDataDateStr) {
        int defaultHour = 9;

        LocalDate insertDate;
        try {
            insertDate = LocalDate.parse(insertDataDateStr); // expects format "yyyy-MM-dd"
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return defaultHour;
        }

        return localSettingRepository.findAllByStatus("1").stream()
                .filter(s -> id.equals(s.getEmployeeId()) && name.equals(s.getName()))
                .sorted(Comparator.comparing(LocalSetting::getFormattedBirthDate).reversed())
                .map(setting -> {
                    try {
                        LocalDate start = LocalDate.parse(setting.getFormattedBirthDate());
                        LocalDate end = LocalDate.parse(setting.getFormattedDeathDate());

                        if (!insertDate.isBefore(start) && !insertDate.isAfter(end)) {
                            return Integer.parseInt(setting.getStartHours());
                        }
                    } catch (DateTimeParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(defaultHour);
    }

    public int returnSettingTotalHour(String id, String name, String insertDataDateStr) {
        int defaultHour = 8;

        LocalDate insertDate;
        try {
            insertDate = LocalDate.parse(insertDataDateStr); // expects format "yyyy-MM-dd"
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return defaultHour;
        }

        return localSettingRepository.findAllByStatus("1").stream()
                .filter(s -> id.equals(s.getEmployeeId()) && name.equals(s.getName()))
                .sorted(Comparator.comparing(LocalSetting::getFormattedBirthDate).reversed())
                .map(setting -> {
                    try {
                        LocalDate start = LocalDate.parse(setting.getFormattedBirthDate());
                        LocalDate end = LocalDate.parse(setting.getFormattedDeathDate());

                        if (!insertDate.isBefore(start) && !insertDate.isAfter(end)) {
                            return Integer.parseInt(setting.getTotalHours());
                        }
                    } catch (DateTimeParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(defaultHour);
    }

    public static Duration addMinutesToDuration(Duration originalDuration, long minutesToAdd) {
        return originalDuration.plusMinutes(minutesToAdd);
    }
}
