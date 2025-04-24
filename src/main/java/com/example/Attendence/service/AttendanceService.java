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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

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

    public void exportSummaryAttendanceData(List<AttendanceDataForAnyPeriod> dataList, HttpServletResponse response) {

        // Sort dataList by date
        dataList.sort(Comparator.comparing(AttendanceDataForAnyPeriod::getDate));

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employee Data");

        // Style for date row
        CellStyle dateRowStyle = workbook.createCellStyle();
        dateRowStyle.setAlignment(HorizontalAlignment.CENTER);
        dateRowStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // First row: Starting and Ending dates
        Row dateRow = sheet.createRow(0);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 3));

        Cell startDateCell = dateRow.createCell(0);
        startDateCell.setCellValue("Starting Date: " + dataList.getFirst().getStartDate());
        startDateCell.setCellStyle(dateRowStyle);

        Cell endDateCell = dateRow.createCell(2);
        endDateCell.setCellValue("End Date: " + dataList.getFirst().getEndDate());
        endDateCell.setCellStyle(dateRowStyle);

        // Header row
        Row headerRow = sheet.createRow(1);
        String[] headers = {
                "Date", "Entry Time", "Late Duration", "Entry Comment", "Exit Time",
                "Time After Exit", "Exit Comment", "Out Time", "Total Time in Day",
                "Day Comment", "Comment"
        };

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data rows
        int rowIndex = 2;
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


    public List<AttendanceDataForAnyPeriod> getAttendanceDataForAnyPeriod(
            String employeeId, String employeeName, String startDateStr, String endDateStr, String header) {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(startDateStr, dateFormatter);
        LocalDate endDate = LocalDate.parse(endDateStr, dateFormatter);

        List<AttendanceDataForAnyPeriod> resultList = new ArrayList<>();
        List<AttendanceData> dataList = attendanceDataRepository
                .findByEmployeeIdAndUpdateStatusAndEntryDateInclusive(employeeId, "1", startDateStr, endDateStr);

        List<Employee> employeeList = userService.employeeList(header);

        if (!dataList.isEmpty()) {
            for (Employee f : employeeList) {
                for (AttendanceData e : dataList) {
                    LocalDate entryDate;
                    try {
                        entryDate = LocalDate.parse(e.getEntryDate(), dateFormatter);
                    } catch (DateTimeParseException ex) {
                        ex.printStackTrace();
                        continue;
                    }

                    boolean dateMatch = (!entryDate.isBefore(startDate) && !entryDate.isAfter(endDate));
                    boolean nameMatch = f.getName().equals(e.getName());

                    if (dateMatch && nameMatch && employeeName.equals(e.getName())) {
                        String date = e.getEntryDate();

                        if (List.of("Absent", "Leave", "Holiday").contains(e.getStatus())) {
                            resultList.add(new AttendanceDataForAnyPeriod(
                                    employeeId, employeeName, startDateStr, endDateStr, date,
                                    "❌", "❌", "❌", "❌", "❌", "❌", "❌", "❌","❌", e.getStatus()
                            ));
                            continue;
                        }

                        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

                        // Entry time logic
                        String entryTimeStr = e.getEntryTime().format(timeFormatter);
                        LocalTime entryTime = e.getEntryTime().toLocalTime();
                        LocalTime lateThreshold = LocalTime.of(
                                returnSettingStartHour(e.getEmployeeId(), e.getName(), e.getEntryDate()),
                                returnGlobalSettingLateMinute(e.getEntryDate())
                        );
                        LocalTime lateCheckTime = LocalTime.of(
                                returnSettingStartHour(e.getEmployeeId(), e.getName(), e.getEntryDate()),
                                returnSettingStartMinute(e.getEmployeeId(), e.getName(), e.getEntryDate())
                        );

                        String lateDuration;
                        String entryComment;
                        if (entryTime.isAfter(lateThreshold)) {
                            Duration duration = Duration.between(lateCheckTime, entryTime);
                            lateDuration = String.format("%d:%02d", duration.toHoursPart(), duration.toMinutesPart());
                            entryComment = "Late";
                        } else {
                            Duration duration = entryTime.isAfter(lateCheckTime)
                                    ? Duration.between(lateCheckTime, entryTime)
                                    : Duration.ZERO;
                            lateDuration = String.format("%d:%02d", duration.toHoursPart(), duration.toMinutesPart());
                            entryComment = "In Time";
                        }

                        // Exit time logic
                        String exitTimeStr = e.getExitTime().format(timeFormatter);
                        LocalTime exitTime = e.getExitTime().toLocalTime();
                        String[] earlyThresholdParts = subtractHourMinute(
                                returnSettingEndHour(e.getEmployeeId(), e.getName(), e.getEntryDate()),
                                returnGlobalSettingEarlyMinute(e.getEntryDate())
                        );
                        LocalTime overtimeThreshold = LocalTime.of(
                                Integer.parseInt(earlyThresholdParts[0]),
                                Integer.parseInt(earlyThresholdParts[1])
                        );
                        LocalTime standardExitTime = LocalTime.of(
                                returnSettingEndHour(e.getEmployeeId(), e.getName(), e.getEntryDate()),
                                returnSettingEndMinute(e.getEmployeeId(), e.getName(), e.getEntryDate())
                        );

                        String overtimeDuration;
                        String exitComment;
                        if (exitTime.isBefore(overtimeThreshold)) {
                            Duration duration = Duration.between(exitTime, standardExitTime);
                            overtimeDuration = "-" + String.format("%d:%02d", duration.toHours(), duration.toMinutesPart());
                            exitComment = "Early";
                        } else {
                            Duration duration = Duration.between(standardExitTime, exitTime);
                            overtimeDuration = String.format("%d:%02d", duration.toHours(), duration.toMinutesPart());
                            exitComment = "Ok";
                        }

                        // Total day duration
                        Duration dayDuration = Duration.between(e.getEntryTime(), e.getExitTime());
                        String totalDayDuration = String.format("%d:%02d", dayDuration.toHoursPart(), dayDuration.toMinutesPart());

                        int expectedHours = returnSettingTotalHour(e.getEmployeeId(), e.getName(), e.getEntryDate());
                        String dayComment = checkTimeDifference(dayDuration, expectedHours);

                        resultList.add(new AttendanceDataForAnyPeriod(
                                employeeId,
                                employeeName,
                                startDateStr,
                                endDateStr,
                                date,
                                entryTimeStr,
                                lateDuration,
                                entryComment,
                                exitTimeStr,
                                overtimeDuration,
                                exitComment,
                                e.getOuttime(),
                                totalDayDuration,
                                dayComment,
                                e.getStatus()
                        ));
                    }
                }
            }
        }

        return resultList;
    }

    public static LocalTime convertUtcToDhakaLocalTime(LocalTime utcTime) {
        // Combine UTC time with today's date in UTC
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LocalDateTime utcDateTime = LocalDateTime.of(today, utcTime);

        // Attach UTC zone
        ZonedDateTime utcZoned = utcDateTime.atZone(ZoneOffset.UTC);

        // Convert to Asia/Dhaka time zone
        ZonedDateTime dhakaZoned = utcZoned.withZoneSameInstant(ZoneId.of("Asia/Dhaka"));

        // Return only the time part
        return dhakaZoned.toLocalTime();
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
