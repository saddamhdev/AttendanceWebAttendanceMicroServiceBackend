package com.example.Attendence.service;
import com.example.Attendence.model.*;
import com.example.Attendence.repository.AttendanceDataRepository;
import com.example.Attendence.repository.GlobalSettingRepository;
import com.example.Attendence.repository.LocalSettingRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.*;
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
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserAtAGlanceService {
    LocalDate databaseDate=null;
    Duration durationc=Duration.ZERO,totallatedurationc=Duration.ZERO,totalextradurationc=Duration.ZERO;Duration totaltimecc=Duration.ZERO;
    Duration intotaltimec=Duration.ZERO;
    int officedayc=0,presentdayc=0,avgtimec=0,totaltimec=0,leavedayc=0,absentdayc=0,holydayc=0,shorttimec=0,regulartimec=0,extratimec=0,intimec=0,latetimec=0,totallatetimec=0,okc=0,earlytimec=0,totalextratimec=0;
    List<Employee> employeeList;
    long timeInSecond=0,totalExtraTime=0, timeInSecondOfOutTime=0;
    double outtimec=0;

    @Autowired
    private AttendanceDataRepository attendanceDataRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private LocalSettingRepository localSettingRepository;
    @Autowired
    private GlobalSettingRepository globalSettingRepository;

    public ResponseEntity<String> exportAtAGlance(UserAtAGlance userAtAGlance){

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employee Data");
        int x=14;
        int width=6000;
        int minusWidth=5000;

        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());

        Font fontSize = workbook.createFont();
        fontSize.setBold(true);
        fontSize.setFontHeightInPoints((short) 14); // Font size

        // Define the number of empty rows and columns for padding
        int paddingRows = 1;
        int paddingColumns = 1;
        // Add empty rows for top padding
        for (int i = 0; i < paddingRows; i++) {
            sheet.createRow(i);
        }

        // Add header row with padding
        String[] headers = {
                "At A GLANCE"

        };

        Row headerRow = sheet.createRow(paddingRows);
        headerRow.setHeightInPoints(40); // Set header row height

        for (int i = 0; i < 5; i++) {
            if(i==2)
            {
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                //headerCellStyle.setBorderBottom(BorderStyle.THIN);
                headerCellStyle.setBorderTop(BorderStyle.THIN);
                //headerCellStyle.setBorderRight(BorderStyle.THIN);
                //headerCellStyle.setBorderLeft(BorderStyle.THIN);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically
                headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 255, 204), null));

                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) 18); // Font size
                headerFont.setFontName("Arial"); // Font name
                headerCellStyle.setFont(headerFont);





                Cell cell = headerRow.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue("At A GLANCE");

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width); // Set column width
            }
            else {
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                //headerCellStyle.setBorderBottom(BorderStyle.THIN);
                headerCellStyle.setBorderTop(BorderStyle.THIN);
                //headerCellStyle.setBorderRight(BorderStyle.THIN);
                //headerCellStyle.setBorderLeft(BorderStyle.THIN);
                headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 255, 204), null));
                if(i==0){
                    headerCellStyle.setBorderLeft(BorderStyle.THIN);
                }
                if(i==4){
                    headerCellStyle.setBorderRight(BorderStyle.THIN);
                }
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically

                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) 18); // Font size
                headerFont.setFontName("Arial"); // Font name
                headerCellStyle.setFont(headerFont);

                Cell cell = headerRow.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue(" ");

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, 0); // Set column width
            }

        }

        //  month
        paddingRows=paddingRows+1;
        Row monthRow=sheet.createRow(paddingRows);
        monthRow.setHeightInPoints(20); // Set header row height
        for(int i=0;i<5;i++){
            if(i==2)
            {
                // Create a CellStyle with a background color for the header row
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerCellStyle.setBorderBottom(BorderStyle.THIN);
                //headerCellStyle.setBorderTop(BorderStyle.THIN);
                //headerCellStyle.setBorderRight(BorderStyle.THIN);
                //headerCellStyle.setBorderLeft(BorderStyle.THIN);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically
                headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 255, 204), null));

                Cell cell = monthRow.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue(userAtAGlance.getStartDate()+" to "+userAtAGlance.getEndDate());

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width); // Set column width
            }
            else{
                // Create a CellStyle with a background color for the header row
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerCellStyle.setBorderBottom(BorderStyle.THIN);
                //headerCellStyle.setBorderTop(BorderStyle.THIN);
                headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 255, 204), null));
                if(i==4){
                    headerCellStyle.setBorderRight(BorderStyle.THIN);
                }
                if(i==0)
                {
                    headerCellStyle.setBorderLeft(BorderStyle.THIN);
                }

                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = monthRow.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue(" ");

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, 0); // Set column width
            }

        }

        paddingRows=paddingRows+1;
        Row nameRow=sheet.createRow(paddingRows);
        nameRow.setHeightInPoints(20); // Set header row height
        for(int i=0;i<5;i++){
            if(i==2){
                // Create a CellStyle with a background color for the header row
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                //headerCellStyle.setBorderBottom(BorderStyle.THIN);
                //headerCellStyle.setBorderTop(BorderStyle.THIN);
                //headerCellStyle.setBorderRight(BorderStyle.THIN);
                //headerCellStyle.setBorderLeft(BorderStyle.THIN);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = nameRow.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue(userAtAGlance.getEmployeeName());

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width); // Set column width
            }
            else{
                // Create a CellStyle with a background color for the header row
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                //headerCellStyle.setBorderBottom(BorderStyle.THIN);
                //headerCellStyle.setBorderTop(BorderStyle.THIN);
                //headerCellStyle.setBorderRight(BorderStyle.THIN);
                //headerCellStyle.setBorderLeft(BorderStyle.THIN);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = nameRow.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue(" ");

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, 0); // Set column width
            }

        }

        sheet.createRow(paddingRows+1);

        paddingRows=paddingRows+2;
        Row line1Row=sheet.createRow(paddingRows);
        line1Row.setHeightInPoints(20); // Set header row height
        for(int i=0;i<5;i++){
            if(i%2==0){
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerCellStyle.setBorderBottom(BorderStyle.THIN);
                headerCellStyle.setBorderTop(BorderStyle.THIN);
                headerCellStyle.setBorderRight(BorderStyle.THIN);
                headerCellStyle.setBorderLeft(BorderStyle.THIN);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically

                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) x); // Font size
                headerFont.setFontName("Arial"); // Font name
                headerCellStyle.setFont(headerFont);
                headerCellStyle.setFont(fontSize);

                Cell cell = line1Row.createCell(i + paddingColumns); // Add padding columns
                if(i==0){
                    cell.setCellValue("Office Day");
                    headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#CCCCCC"), null));
                    //headerCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                }else if(i==2){
                    cell.setCellValue("Total Present");
                    headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#B6D7A8"), null));
                }
                else{
                    cell.setCellValue("Avg Time");
                    headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#4472C4"), null));
                }


                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width); // Set column width
            }
            else{
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                //headerCellStyle.setBorderBottom(BorderStyle.THIN);
                //headerCellStyle.setBorderTop(BorderStyle.THIN);
                //headerCellStyle.setBorderRight(BorderStyle.THIN);
                //headerCellStyle.setBorderLeft(BorderStyle.THIN);
                ////headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                //headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = line1Row.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue("");

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width-minusWidth); // Set column width
            }

        }

        paddingRows=paddingRows+1;
        Row line12Row=sheet.createRow(paddingRows);
        line12Row.setHeightInPoints(40); // Set header row height
        for(int i=0;i<5;i++){
            if(i%2==0){
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerCellStyle.setBorderBottom(BorderStyle.THIN);
                headerCellStyle.setBorderTop(BorderStyle.THIN);
                headerCellStyle.setBorderRight(BorderStyle.THIN);
                headerCellStyle.setBorderLeft(BorderStyle.THIN);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically



                Cell cell = line12Row.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue(i);
                if(i==0){
                    cell.setCellValue(userAtAGlance.getOfficeDay());
                }
                else if (i==2) {
                    cell.setCellValue(userAtAGlance.getTotalPresent());
                }else{
                    cell.setCellValue(userAtAGlance.getAvgTime());
                }
                headerCellStyle.setFont(fontSize);
                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width); // Set column width
            }
            else{
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                //headerCellStyle.setBorderBottom(BorderStyle.THIN);
                //headerCellStyle.setBorderTop(BorderStyle.THIN);
                //headerCellStyle.setBorderRight(BorderStyle.THIN);
                //headerCellStyle.setBorderLeft(BorderStyle.THIN);
                ////headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                //headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = line12Row.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue("");

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width-minusWidth); // Set column width
            }

        }

        sheet.createRow(paddingRows+1);

        paddingRows=paddingRows+2;
        Row line2Row=sheet.createRow(paddingRows);
        line2Row.setHeightInPoints(20); // Set header row height
        for(int i=0;i<5;i++){
            if(i%2==0){
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerCellStyle.setBorderBottom(BorderStyle.THIN);
                headerCellStyle.setBorderTop(BorderStyle.THIN);
                headerCellStyle.setBorderRight(BorderStyle.THIN);
                headerCellStyle.setBorderLeft(BorderStyle.THIN);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically

                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) x); // Font size
                headerFont.setFontName("Arial"); // Font name
                headerCellStyle.setFont(headerFont);

                Cell cell = line2Row.createCell(i + paddingColumns); // Add padding columns
                if(i==0){
                    cell.setCellValue("Leave");
                    headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#FFE5A0"), null));
                    //headerCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                }else if(i==2){
                    cell.setCellValue("Absent");
                    headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#B10202"), null));
                    headerCellStyle.setFont(font);
                }
                else{
                    cell.setCellValue("Holiday");
                    headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#3D3D3D"), null));
                    headerCellStyle.setFont(font);
                }

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width); // Set column width
            }
            else{
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                //headerCellStyle.setBorderBottom(BorderStyle.THIN);
                //headerCellStyle.setBorderTop(BorderStyle.THIN);
                //headerCellStyle.setBorderRight(BorderStyle.THIN);
                //headerCellStyle.setBorderLeft(BorderStyle.THIN);
                ////headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                //headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = line2Row.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue("");

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width-minusWidth); // Set column width
            }

        }

        paddingRows=paddingRows+1;
        Row line21Row=sheet.createRow(paddingRows);
        line21Row.setHeightInPoints(40); // Set header row height
        for(int i=0;i<5;i++){
            if(i%2==0){
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerCellStyle.setBorderBottom(BorderStyle.THIN);
                headerCellStyle.setBorderTop(BorderStyle.THIN);
                headerCellStyle.setBorderRight(BorderStyle.THIN);
                headerCellStyle.setBorderLeft(BorderStyle.THIN);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = line21Row.createCell(i + paddingColumns); // Add padding columns
                if(i==0){
                    cell.setCellValue(userAtAGlance.getLeave());
                }
                else if (i==2) {
                    cell.setCellValue(userAtAGlance.getAbsent());
                }else{
                    cell.setCellValue(userAtAGlance.getHoliday());
                }
                headerCellStyle.setFont(fontSize);
                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width); // Set column width
            }
            else{
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                //headerCellStyle.setBorderBottom(BorderStyle.THIN);
                //headerCellStyle.setBorderTop(BorderStyle.THIN);
                //headerCellStyle.setBorderRight(BorderStyle.THIN);
                //headerCellStyle.setBorderLeft(BorderStyle.THIN);
                ////headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                //headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = line21Row.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue("");

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width-minusWidth); // Set column width
            }

        }

        sheet.createRow(paddingRows+1);

        paddingRows=paddingRows+2;
        Row line3Row=sheet.createRow(paddingRows);
        line3Row.setHeightInPoints(20); // Set header row height
        for(int i=0;i<5;i++){
            if(i%2==0){
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerCellStyle.setBorderBottom(BorderStyle.THIN);
                headerCellStyle.setBorderTop(BorderStyle.THIN);
                headerCellStyle.setBorderRight(BorderStyle.THIN);
                headerCellStyle.setBorderLeft(BorderStyle.THIN);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically

                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) x); // Font size
                headerFont.setFontName("Arial"); // Font name
                headerCellStyle.setFont(headerFont);

                Cell cell = line3Row.createCell(i + paddingColumns); // Add padding columns
                if(i==0){
                    cell.setCellValue("Short Time");
                    headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#F4CCCC"), null));
                    //headerCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                }else if(i==2){
                    cell.setCellValue("Regular Time");
                    headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#B7E1CD"), null));
                }
                else{
                    cell.setCellValue("Extra Time");
                    headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#38761D"), null));
                }

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width); // Set column width
            }
            else{
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                //headerCellStyle.setBorderBottom(BorderStyle.THIN);
                //headerCellStyle.setBorderTop(BorderStyle.THIN);
                //headerCellStyle.setBorderRight(BorderStyle.THIN);
                //headerCellStyle.setBorderLeft(BorderStyle.THIN);
                ////headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                //headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = line3Row.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue("");

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width-minusWidth); // Set column width
            }

        }

        paddingRows=paddingRows+1;
        Row line31Row=sheet.createRow(paddingRows);
        line31Row.setHeightInPoints(40); // Set header row height
        for(int i=0;i<5;i++){
            if(i%2==0){
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerCellStyle.setBorderBottom(BorderStyle.THIN);
                headerCellStyle.setBorderTop(BorderStyle.THIN);
                headerCellStyle.setBorderRight(BorderStyle.THIN);
                headerCellStyle.setBorderLeft(BorderStyle.THIN);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = line31Row.createCell(i + paddingColumns); // Add padding columns
                if(i==0){
                    cell.setCellValue(userAtAGlance.getShortTime());
                }
                else if (i==2) {
                    cell.setCellValue(userAtAGlance.getRegularTime());
                }else{
                    cell.setCellValue(userAtAGlance.getExtraTime());
                }
                headerCellStyle.setFont(fontSize);
                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width); // Set column width
            }
            else{
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                //headerCellStyle.setBorderBottom(BorderStyle.THIN);
                //headerCellStyle.setBorderTop(BorderStyle.THIN);
                //headerCellStyle.setBorderRight(BorderStyle.THIN);
                //headerCellStyle.setBorderLeft(BorderStyle.THIN);
                ////headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                //headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = line31Row.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue("");

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width-minusWidth); // Set column width
            }

        }

        sheet.createRow(paddingRows+1);

        paddingRows=paddingRows+2;
        Row line4Row=sheet.createRow(paddingRows);
        line4Row.setHeightInPoints(20); // Set header row height
        for(int i=0;i<5;i++){
            if(i==2){
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                //headerCellStyle.setBorderBottom(BorderStyle.THIN);
                headerCellStyle.setBorderTop(BorderStyle.THIN);
                //headerCellStyle.setBorderRight(BorderStyle.THIN);
                //headerCellStyle.setBorderLeft(BorderStyle.THIN);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically

                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) x); // Font size
                headerFont.setFontName("Arial"); // Font name
                headerCellStyle.setFont(headerFont);

                Cell cell = line4Row.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue("Entry");
                headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#9FC5E8"), null));

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width); // Set column width
            }
            else{
                CellStyle headerCellStyle = workbook.createCellStyle();

                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                if(i==1){
                    headerCellStyle.setBorderBottom(BorderStyle.THIN);
                }
                if(i==3){
                    headerCellStyle.setBorderBottom(BorderStyle.THIN);
                }
                //headerCellStyle.setBorderBottom(BorderStyle.THIN);
                headerCellStyle.setBorderTop(BorderStyle.THIN);
                //headerCellStyle.setBorderRight(BorderStyle.THIN);
                //headerCellStyle.setBorderLeft(BorderStyle.THIN);
                if(i==0){
                    headerCellStyle.setBorderLeft(BorderStyle.THIN);
                }
                if(i==4){
                    headerCellStyle.setBorderRight(BorderStyle.THIN);
                }
                ////headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                //headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = line4Row.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue("");
                headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#9FC5E8"), null));
                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width-minusWidth); // Set column width
            }

        }

        paddingRows=paddingRows+1;
        Row line41Row=sheet.createRow(paddingRows);
        line41Row.setHeightInPoints(20); // Set header row height
        for(int i=0;i<5;i++){
            if(i%2==0){
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerCellStyle.setBorderBottom(BorderStyle.THIN);
                headerCellStyle.setBorderTop(BorderStyle.THIN);
                headerCellStyle.setBorderRight(BorderStyle.THIN);
                headerCellStyle.setBorderLeft(BorderStyle.THIN);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically

                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) x); // Font size
                headerFont.setFontName("Arial"); // Font name
                headerCellStyle.setFont(headerFont);

                Cell cell = line41Row.createCell(i + paddingColumns); // Add padding columns
                if(i==0){
                    cell.setCellValue("In Time");
                    headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#38761D"), null));
                    //headerCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                }else if(i==2){
                    cell.setCellValue("Late");
                    headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#F4CCCC"), null));
                }
                else{
                    cell.setCellValue("Total Late");
                    headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#EA9999"), null));
                }

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width); // Set column width
            }
            else{
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                //headerCellStyle.setBorderBottom(BorderStyle.THIN);
                //headerCellStyle.setBorderTop(BorderStyle.THIN);
                //headerCellStyle.setBorderRight(BorderStyle.THIN);
                //headerCellStyle.setBorderLeft(BorderStyle.THIN);
                ////headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                //headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = line41Row.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue("");

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width-minusWidth); // Set column width
            }

        }
        paddingRows=paddingRows+1;
        Row line42Row=sheet.createRow(paddingRows);
        line42Row.setHeightInPoints(40); // Set header row height
        for(int i=0;i<5;i++){
            if(i%2==0){
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerCellStyle.setBorderBottom(BorderStyle.THIN);
                headerCellStyle.setBorderTop(BorderStyle.THIN);
                headerCellStyle.setBorderRight(BorderStyle.THIN);
                headerCellStyle.setBorderLeft(BorderStyle.THIN);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = line42Row.createCell(i + paddingColumns); // Add padding columns
                if(i==0){
                    cell.setCellValue(userAtAGlance.getEntryInTime());
                }
                else if (i==2) {
                    cell.setCellValue(userAtAGlance.getEntryLate());
                }else{
                    cell.setCellValue(userAtAGlance.getTotalLate());
                }
                headerCellStyle.setFont(fontSize);
                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width); // Set column width
            }
            else{
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                //headerCellStyle.setBorderBottom(BorderStyle.THIN);
                //headerCellStyle.setBorderTop(BorderStyle.THIN);
                //headerCellStyle.setBorderRight(BorderStyle.THIN);
                //headerCellStyle.setBorderLeft(BorderStyle.THIN);
                ////headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                //headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = line42Row.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue("");

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width-minusWidth); // Set column width
            }

        }


        sheet.createRow(paddingRows+1);

        paddingRows=paddingRows+2;
        Row line5Row=sheet.createRow(paddingRows);
        line5Row.setHeightInPoints(20); // Set header row height
        for(int i=0;i<5;i++){
            if(i==1|| i==3){
                CellStyle headerCellStyle = workbook.createCellStyle();

                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                if(i==1){
                    headerCellStyle.setBorderBottom(BorderStyle.THIN);
                    headerCellStyle.setBorderTop(BorderStyle.THIN);
                }
                //headerCellStyle.setBorderRight(BorderStyle.THIN);
                //headerCellStyle.setBorderLeft(BorderStyle.THIN);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically
                int mm=x-2;
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) mm); // Font size
                headerFont.setFontName("Arial"); // Font name
                headerCellStyle.setFont(headerFont);

                Cell cell = line5Row.createCell(i + paddingColumns); // Add padding columns

                if(i==1){
                    cell.setCellValue("Exit");
                    headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#CCCCCC"), null));
                }
                else{
                    cell.setCellValue(" ");
                    headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                }


                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width-minusWidth); // Set column width
            }
            else if(i==4){
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                //headerCellStyle.setBorderBottom(BorderStyle.THIN);
                headerCellStyle.setBorderTop(BorderStyle.THIN);
                headerCellStyle.setBorderRight(BorderStyle.THIN);
                headerCellStyle.setBorderLeft(BorderStyle.THIN);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically

                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) x); // Font size
                headerFont.setFontName("Arial"); // Font name
                headerCellStyle.setFont(headerFont);

                Cell cell = line5Row.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue("Total Extra");
                headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#B6D7A8"), null));

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width); // Set column width
            }
            else{
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                //headerCellStyle.setBorderBottom(BorderStyle.THIN);
                headerCellStyle.setBorderTop(BorderStyle.THIN);
                if(i==0){
                    headerCellStyle.setBorderLeft(BorderStyle.THIN);
                }
                if(i==2){
                    headerCellStyle.setBorderRight(BorderStyle.THIN);
                }
                //headerCellStyle.setBorderRight(BorderStyle.THIN);
                //headerCellStyle.setBorderLeft(BorderStyle.THIN);
                ////headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                //headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = line5Row.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue("");
                headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#CCCCCC"), null));
                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width); // Set column width
            }

        }

        paddingRows=paddingRows+1;
        Row line51Row=sheet.createRow(paddingRows);
        line51Row.setHeightInPoints(20); // Set header row height
        for(int i=0;i<5;i++){
            if(i%2==0){
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerCellStyle.setBorderBottom(BorderStyle.THIN);
                if(i!=4){
                    headerCellStyle.setBorderTop(BorderStyle.THIN);
                }
                headerCellStyle.setBorderRight(BorderStyle.THIN);
                headerCellStyle.setBorderLeft(BorderStyle.THIN);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically

                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) x); // Font size
                headerFont.setFontName("Arial"); // Font name
                headerCellStyle.setFont(headerFont);

                Cell cell = line51Row.createCell(i + paddingColumns); // Add padding columns
                if(i==0){
                    cell.setCellValue("OK");
                    headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#38761D"), null));
                    //headerCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                }else if(i==2){
                    cell.setCellValue("Early");
                    headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#F4CCCC"), null));
                }
                else{
                    cell.setCellValue("Time");
                    headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#B6D7A8"), null));
                }

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width); // Set column width
            }
            else{
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                //headerCellStyle.setBorderBottom(BorderStyle.THIN);
                //headerCellStyle.setBorderTop(BorderStyle.THIN);
                //headerCellStyle.setBorderRight(BorderStyle.THIN);
                //headerCellStyle.setBorderLeft(BorderStyle.THIN);
                ////headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                //headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = line51Row.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue("");

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width-minusWidth); // Set column width
            }

        }
        paddingRows=paddingRows+1;
        Row line52Row=sheet.createRow(paddingRows);
        line52Row.setHeightInPoints(40); // Set header row height
        for(int i=0;i<5;i++){
            if(i%2==0){
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerCellStyle.setBorderBottom(BorderStyle.THIN);
                headerCellStyle.setBorderTop(BorderStyle.THIN);
                headerCellStyle.setBorderRight(BorderStyle.THIN);
                headerCellStyle.setBorderLeft(BorderStyle.THIN);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = line52Row.createCell(i + paddingColumns); // Add padding columns
                if(i==0){
                    cell.setCellValue(userAtAGlance.getExitOk());
                }
                else if (i==2) {
                    cell.setCellValue(userAtAGlance.getExitEarly());
                }else{
                    cell.setCellValue(userAtAGlance.getTotalExtraTime());
                }
                headerCellStyle.setFont(fontSize);
                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width); // Set column width
            }
            else{
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                //headerCellStyle.setBorderBottom(BorderStyle.THIN);
                //headerCellStyle.setBorderTop(BorderStyle.THIN);
                //headerCellStyle.setBorderRight(BorderStyle.THIN);
                //headerCellStyle.setBorderLeft(BorderStyle.THIN);
                ////headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                //headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = line52Row.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue("");

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width-minusWidth); // Set column width
            }

        }

        sheet.createRow(paddingRows+1);

        paddingRows=paddingRows+2;
        Row line6Row=sheet.createRow(paddingRows);
        line6Row.setHeightInPoints(20); // Set header row height
        for(int i=0;i<5;i++){
            if(i%2==0){
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerCellStyle.setBorderBottom(BorderStyle.THIN);
                headerCellStyle.setBorderTop(BorderStyle.THIN);
                headerCellStyle.setBorderRight(BorderStyle.THIN);
                headerCellStyle.setBorderLeft(BorderStyle.THIN);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically

                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) x); // Font size
                headerFont.setFontName("Arial"); // Font name
                headerCellStyle.setFont(headerFont);

                Cell cell = line6Row.createCell(i + paddingColumns); // Add padding columns
                if(i==0){
                    cell.setCellValue("Office Out Time");
                    headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#F4CCCC"), null));
                    //headerCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                }else if(i==2){
                    cell.setCellValue("Office in Time");
                    headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#B7E1CD"), null));
                }
                else{
                    cell.setCellValue("Total Time");
                    headerCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#38761D"), null));
                }

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width); // Set column width
            }
            else{
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                //headerCellStyle.setBorderBottom(BorderStyle.THIN);
                //headerCellStyle.setBorderTop(BorderStyle.THIN);
                //headerCellStyle.setBorderRight(BorderStyle.THIN);
                //headerCellStyle.setBorderLeft(BorderStyle.THIN);
                ////headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                //headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = line6Row.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue("");

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width-minusWidth); // Set column width
            }

        }

        paddingRows=paddingRows+1;
        Row line61Row=sheet.createRow(paddingRows);
        line61Row.setHeightInPoints(40); // Set header row height
        for(int i=0;i<5;i++){
            if(i%2==0){
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerCellStyle.setBorderBottom(BorderStyle.THIN);
                headerCellStyle.setBorderTop(BorderStyle.THIN);
                headerCellStyle.setBorderRight(BorderStyle.THIN);
                headerCellStyle.setBorderLeft(BorderStyle.THIN);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = line61Row.createCell(i + paddingColumns); // Add padding columns
                if(i==0){
                    cell.setCellValue(userAtAGlance.getOfficeOutTime());
                }
                else if (i==2) {
                    cell.setCellValue(userAtAGlance.getOfficeInTime());
                }else{
                    cell.setCellValue(userAtAGlance.getTotalTime());
                }
                headerCellStyle.setFont(fontSize);
                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width); // Set column width
            }
            else{
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                //headerCellStyle.setBorderBottom(BorderStyle.THIN);
                //headerCellStyle.setBorderTop(BorderStyle.THIN);
                //headerCellStyle.setBorderRight(BorderStyle.THIN);
                //headerCellStyle.setBorderLeft(BorderStyle.THIN);
                ////headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
                //headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically


                Cell cell = line61Row.createCell(i + paddingColumns); // Add padding columns
                cell.setCellValue("");

                cell.setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i + paddingColumns, width-minusWidth); // Set column width
            }

        }



        // Get the user's Downloads directory
        String downloadsPath = System.getProperty("user.home") + File.separator + "Downloads";

        // Generate a timestamp for the filename
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        // Create the file with the employee name and timestamp
        File file = new File(downloadsPath, "User At A Glance Report_"+userAtAGlance.getEmployeeName()+"_" + timestamp + ".xlsx");

        // Save the workbook to the file
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
            System.out.println("Excel file saved successfully at: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ResponseEntity.ok("Exported successfully");
    }

    public UserAtAGlance getUserAtAGlanceData(String employeeId, String employeeName, String startDate1, String endDate1,String header) {
      UserAtAGlance userAtAGlance=new UserAtAGlance();
      userAtAGlance.setEmployeeId(employeeId);
      userAtAGlance.setEmployeeName(employeeName);
      userAtAGlance.setStartDate(startDate1);
      userAtAGlance.setEndDate(endDate1);
       String selectedparsonglance=employeeName;
        // Define the formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Convert to ChronoLocalDate
        ChronoLocalDate startDate = LocalDate.parse(startDate1, formatter);
        ChronoLocalDate endDate=LocalDate.parse(endDate1, formatter);

        // Initialize counters
        officedayc = 0;
        presentdayc = 0;
        avgtimec = 0;
        leavedayc = 0;
        absentdayc = 0;
        holydayc = 0;
        shorttimec = 0;
        regulartimec = 0;
        extratimec = 0;
        intimec = 0;
        latetimec = 0;
        totallatetimec = 0;
        okc = 0;
        earlytimec = 0;
        totalextratimec = 0;
        durationc = Duration.ZERO;
        totallatedurationc = Duration.ZERO;
        totalextradurationc = Duration.ZERO;
        outtimec = 0;totalExtraTime=0;
        totaltimecc = Duration.ZERO;
        intotaltimec = Duration.ZERO;
        outtimec = 0;
        totaltimecc = Duration.ZERO;
        timeInSecond=0;
        timeInSecondOfOutTime=0;
        List<AttendanceData> dataList=attendanceDataRepository.findByUpdateStatus("1");
        employeeList=userService.employeeList(header);

        if (dataList.size() > 0) {
            System.out.println("UserAtAGlance");
            dataList.forEach(e -> {
                // Define the date format
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                databaseDate=null;
                // Convert the string to LocalDate
                try {
                    databaseDate = LocalDate.parse(e.getEntryDate(), dateFormatter);
                    //System.out.println("Converted LocalDate: " + databaseDate);
                } catch (DateTimeParseException f) {
                    f.printStackTrace(); // Handle parsing exception
                }
                if (databaseDate != null) {
                    System.out.println(databaseDate+" "+startDate+" "+endDate);
                    if (((databaseDate.equals(startDate) || databaseDate.equals(endDate))&& selectedparsonglance.equals(e.getName()) )||
                            (databaseDate.isAfter(startDate) && databaseDate.isBefore(endDate)&& selectedparsonglance.equals(e.getName()))) {

                        employeeList.forEach(g->{
                            LocalDate databaseDate1 = LocalDate.parse(g.getJoinDate(), dateFormatter);
                            if((e.getName().equals(g.getName())&& databaseDate1.isBefore(databaseDate)) || (e.getName().equals(g.getName())&& databaseDate1.isEqual(databaseDate) )){


                                if (!"Holiday".equals(e.getStatus())) {
                                    officedayc++;
                                }

                                if ("Present".equals(e.getStatus())) {
                                    Duration durationBetweenEntryExit = Duration.between(e.getEntryTime(), e.getExitTime());
                                    timeInSecond=timeInSecond+durationBetweenEntryExit.toHoursPart()*60L*60L+durationBetweenEntryExit.toMinutesPart()*60L;
                                    durationc = durationc.plus(durationBetweenEntryExit);
                                    System.out.println("Current total duration (seconds): " + durationc.getSeconds());
                                    presentdayc++;

                                    long hours = durationBetweenEntryExit.toHoursPart();
                                    long minutes = durationBetweenEntryExit.toMinutesPart();
                                    int settingHours = returnSettingTotalHour(e.getEmployeeId(), e.getName(), e.getEntryDate());
                                    System.out.println(settingHours);

                                    if (hours < settingHours) {
                                        shorttimec++;
                                    } else if (hours > settingHours || (hours == settingHours && minutes > 0)) {
                                        extratimec++;
                                    } else {
                                        regulartimec++;
                                    }

                                    LocalTime lateThreshold = LocalTime.of(returnSettingStartHour(e.getEmployeeId(), e.getName(), e.getEntryDate()), returnGlobalSettingLateMinute(e.getEntryDate()) + 1);

                                    if (e.getEntryTime().toLocalTime().isBefore(lateThreshold)) {
                                        intimec++;
                                    }

                                    lateThreshold = LocalTime.of(returnSettingStartHour(e.getEmployeeId(), e.getName(), e.getEntryDate()), returnGlobalSettingLateMinute(e.getEntryDate()));
                                    if (e.getEntryTime().toLocalTime().isAfter(lateThreshold)) {
                                        latetimec++;
                                        LocalTime lateThreshold1 = LocalTime.of(returnSettingStartHour(e.getEmployeeId(), e.getName(), e.getEntryDate()), returnSettingStartMinute(e.getEmployeeId(), e.getName(), e.getEntryDate()));
                                        Duration lateDuration = Duration.between(lateThreshold1, e.getEntryTime().toLocalTime());
                                        totallatedurationc = totallatedurationc.plus(lateDuration);
                                    }

                                    String[] data = subtractHourMinute(returnSettingEndHour(e.getEmployeeId(), e.getName(), e.getEntryDate()), returnGlobalSettingEarlyMinute(e.getEntryDate()));
                                    LocalTime exitThreshold = LocalTime.of(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
                                    if (e.getExitTime().toLocalTime().isBefore(exitThreshold)) {
                                        earlytimec++;
                                    }

                                    exitThreshold = LocalTime.of(returnSettingEndHour(e.getEmployeeId(), e.getName(), e.getEntryDate()), returnSettingEndMinute(e.getEmployeeId(), e.getName(), e.getEntryDate()));
                                    if (e.getExitTime().toLocalTime().isAfter(exitThreshold)) {
                                        // 15 minute calculation after end time
                                        LocalTime	exitThreshold1 = LocalTime.of(returnSettingEndHour(e.getEmployeeId(), e.getName(), e.getEntryDate()), 15+returnSettingEndMinute(e.getEmployeeId(), e.getName(), e.getEntryDate()));
                                        if (e.getExitTime().toLocalTime().isAfter(exitThreshold1)){
                                            Duration extraDuration = Duration.between(exitThreshold, e.getExitTime().toLocalTime());
                                            totalExtraTime=totalExtraTime+extraDuration.toHoursPart()*60L*60L+extraDuration.toMinutesPart()*60L;
                                            //totalextradurationc = totalextradurationc.plus(extraDuration);
                                            Duration ff=	Duration.ofSeconds(totalExtraTime);

                                        }

                                    }


                                    String regex = "(\\d+)\\.(\\d+)";

                                    Pattern pattern = Pattern.compile(regex);
                                    Matcher matcher = pattern.matcher(e.getOuttime());
                                    if (matcher.matches()) {
                                        String integerPart = matcher.group(1);
                                        String fractionalPart = matcher.group(2);
                                        timeInSecondOfOutTime=timeInSecondOfOutTime+Long.parseLong(integerPart)*60L*60L+Long.parseLong(fractionalPart)*60L;

                                        System.out.println("Integer Part: " + integerPart);
                                        System.out.println("Fractional Part: " + fractionalPart);
                                    } else {
                                        System.out.println("The input is not a valid decimal number.");
                                    }
                                }

                                if ("Leave".equals(e.getStatus())) {
                                    leavedayc++;
                                }

                                if ("Absent".equals(e.getStatus())) {
                                    absentdayc++;
                                }

                                if ("Holiday".equals(e.getStatus())) {
                                    holydayc++;
                                }

                            }
                        });

                        // Your code for handling the case when the databaseDate is within the range

                    } else {
                        // Your code for handling the case when the databaseDate is outside the range
                        System.out.println("The date is outside the range.");
                    }
                }

            });

            //officeday.setText(Integer.toString(officedayc));
            userAtAGlance.setOfficeDay(officedayc);
           // presentday.setText(Integer.toString(presentdayc));
            userAtAGlance.setTotalPresent(presentdayc);

            intotaltimec = Duration.ofSeconds(timeInSecond);
            //
            Duration outT=Duration.ofSeconds(timeInSecondOfOutTime);
           // outtime.setText(outT.toHours() + ":" + outT.toMinutesPart());
            userAtAGlance.setOfficeOutTime(outT.toHours() + ":" + outT.toMinutesPart());

            long seconds = (long) (outtimec * 60 * 60);
            //Duration outtimeduration = Duration.ofSeconds(seconds+timeInSecond);
            long gg=timeInSecondOfOutTime+timeInSecond;
            totaltimecc = Duration.ofSeconds(gg);

           // intotaltime.setText(intotaltimec.toHours() + ":" + intotaltimec.toMinutesPart());
            userAtAGlance.setOfficeInTime(intotaltimec.toHours() + ":" + intotaltimec.toMinutesPart());

           // totaltime.setText(totaltimecc.toHours() + ":" + totaltimecc.toMinutesPart());
            userAtAGlance.setTotalTime(totaltimecc.toHours() + ":" + totaltimecc.toMinutesPart());


            System.out.println("TOTAL HOURS " + durationc.toHoursPart() + " TOTAL MINUTES " + durationc.toMinutesPart() + " total days " + presentdayc);

            if (presentdayc != 0) {
                long totalSeconds =  timeInSecond;
                long averageSeconds = totalSeconds / presentdayc;
                System.out.println("Avg second " + totalSeconds);
                durationc = Duration.ofSeconds(averageSeconds);
                totalextradurationc=Duration.ofSeconds(totalExtraTime);
            }

            System.out.println("Avg hours " + durationc.toHoursPart());

            //avgtime.setText(durationc.toHoursPart() + ":" + durationc.toMinutesPart());
            userAtAGlance.setAvgTime(durationc.toHoursPart() + ":" + durationc.toMinutesPart());

            //leaveday.setText(Integer.toString(leavedayc));
            userAtAGlance.setLeave(leavedayc);
            //absentday.setText(Integer.toString(absentdayc));
            userAtAGlance.setAbsent(absentdayc);
            //holyday.setText(Integer.toString(holydayc));
            userAtAGlance.setHoliday(holydayc);
            //shorttime.setText(Integer.toString(shorttimec));
            userAtAGlance.setShortTime(shorttimec);
            //regulartime.setText(Integer.toString(regulartimec));
            userAtAGlance.setRegularTime(regulartimec);
           // extratime.setText(Integer.toString(extratimec));
            userAtAGlance.setExtraTime(extratimec);
            //intime.setText(Integer.toString(intimec));
            userAtAGlance.setEntryInTime(intimec);
            //latetime.setText(Integer.toString(latetimec));
            userAtAGlance.setEntryLate(latetimec);
            //totallatetime.setText(totallatedurationc.toHoursPart() + ":" + totallatedurationc.toMinutesPart());
            userAtAGlance.setTotalLate(totallatedurationc.toHoursPart() + ":" + totallatedurationc.toMinutesPart());
            //ok.setText(Integer.toString(presentdayc - earlytimec));
            userAtAGlance.setExitOk(presentdayc - earlytimec);
            //earlytime.setText(Integer.toString(earlytimec));
            userAtAGlance.setExitEarly(earlytimec);
            //totalextratime.setText(totalextradurationc.toHoursPart() + ":" + totalextradurationc.toMinutesPart());
            userAtAGlance.setTotalExtraTime(totalextradurationc.toHoursPart() + ":" + totalextradurationc.toMinutesPart());
        }
        else{
            System.out.println("Data Not Found");
        }

        return userAtAGlance;
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
}
