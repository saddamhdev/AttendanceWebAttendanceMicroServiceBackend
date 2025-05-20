package com.example.Attendence.service;
import com.example.Attendence.model.*;
import com.example.Attendence.repository.AttendanceDataRepository;
import com.example.Attendence.repository.GlobalSettingRepository;
import com.example.Attendence.repository.LocalSettingRepository;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
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
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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

    public void exportAtAGlance(UserAtAGlance userAtAGlance, HttpServletResponse response){

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

    public UserAtAGlance getUserAtAGlanceData(String employeeId, String employeeName, String startDate1, String endDate1, String header) {
        UserAtAGlance userAtAGlance = new UserAtAGlance();
        userAtAGlance.setEmployeeId(employeeId);
        userAtAGlance.setEmployeeName(employeeName);
        userAtAGlance.setStartDate(startDate1);
        userAtAGlance.setEndDate(endDate1);

        String selectedPerson = employeeName;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ChronoLocalDate startDate = LocalDate.parse(startDate1, formatter);
        ChronoLocalDate endDate = LocalDate.parse(endDate1, formatter);

        resetCounters();

        List<AttendanceData> dataList = attendanceDataRepository.findByEmployeeIdAndUpdateStatusAndEntryDateInclusive(employeeId, "1", startDate1, endDate1);
        employeeList = userService.employeeList(header);

        if (!dataList.isEmpty()) {
            for (AttendanceData e : dataList) {
                LocalDate entryDate;
                try {
                    entryDate = LocalDate.parse(e.getEntryDate(), formatter);
                } catch (DateTimeParseException ex) {
                    continue;
                }

                if (!isDateInRange(entryDate, startDate, endDate) || !selectedPerson.equals(e.getName())) continue;

                for (Employee g : employeeList) {
                    LocalDate joinDate = LocalDate.parse(g.getJoinDate(), formatter);
                    if (!g.getName().equals(e.getName()) || joinDate.isAfter(entryDate)) continue;

                    String empId = e.getEmployeeId();
                    String empName = e.getName();
                    String entryDay = e.getEntryDate();
                    LocalTime entryTime = e.getEntryTime().toLocalTime();
                    LocalTime exitTime = e.getExitTime().toLocalTime();

                    String status = e.getStatus();

                    if (!"Holiday".equals(status)) officedayc++;
                    if ("Present".equals(status)) {
                        Duration entryExitDuration = Duration.between(e.getEntryTime(), e.getExitTime());
                        timeInSecond += entryExitDuration.toHoursPart() * 3600L + entryExitDuration.toMinutesPart() * 60L;
                        durationc = durationc.plus(entryExitDuration);
                        presentdayc++;

                        int settingHours = returnSettingTotalHour(empId, empName, entryDay);
                        if (entryExitDuration.toHours() < settingHours) shorttimec++;
                        else if (entryExitDuration.toHours() > settingHours || entryExitDuration.toMinutesPart() > 0) extratimec++;
                        else regulartimec++;

                        LocalTime startThreshold = LocalTime.of(returnSettingStartHour(empId, empName, entryDay), returnGlobalSettingLateMinute(entryDay) + 1);
                        if (entryTime.isBefore(startThreshold)) intimec++;

                        LocalTime lateLimit = LocalTime.of(returnSettingStartHour(empId, empName, entryDay), returnGlobalSettingLateMinute(entryDay));
                        if (entryTime.isAfter(lateLimit)) {
                            latetimec++;
                            LocalTime actualStart = LocalTime.of(returnSettingStartHour(empId, empName, entryDay), returnSettingStartMinute(empId, empName, entryDay));
                            Duration lateDuration = Duration.between(actualStart, entryTime);
                            totallatedurationc = totallatedurationc.plus(lateDuration);
                        }

                        String[] endTimeData = subtractHourMinute(returnSettingEndHour(empId, empName, entryDay), returnGlobalSettingEarlyMinute(entryDay));
                        LocalTime earlyExitLimit = LocalTime.of(Integer.parseInt(endTimeData[0]), Integer.parseInt(endTimeData[1]));
                        if (exitTime.isBefore(earlyExitLimit)) earlytimec++;

                        LocalTime actualEnd = LocalTime.of(returnSettingEndHour(empId, empName, entryDay), returnSettingEndMinute(empId, empName, entryDay));
                        if (exitTime.isAfter(actualEnd)) {
                            LocalTime extendedEnd = actualEnd.plusMinutes(15);
                            if (exitTime.isAfter(extendedEnd)) {
                                Duration extraDuration = Duration.between(actualEnd, exitTime);
                                totalExtraTime += extraDuration.toHoursPart() * 3600L + extraDuration.toMinutesPart() * 60L;
                            }
                        }

                        Matcher matcher = Pattern.compile("(\\d+)\\.(\\d+)").matcher(e.getOuttime());
                        if (matcher.matches()) {
                            timeInSecondOfOutTime += Long.parseLong(matcher.group(1)) * 3600L + Long.parseLong(matcher.group(2)) * 60L;
                        }
                    }

                    if ("Leave".equals(status)) leavedayc++;
                    else if ("Absent".equals(status)) absentdayc++;
                    else if ("Holiday".equals(status)) holydayc++;
                }
            }

            userAtAGlance.setOfficeDay(officedayc);
            userAtAGlance.setTotalPresent(presentdayc);

            intotaltimec = Duration.ofSeconds(timeInSecond);
            Duration outDuration = Duration.ofSeconds(timeInSecondOfOutTime);
            userAtAGlance.setOfficeOutTime(outDuration.toHours() + ":" + outDuration.toMinutesPart());
            totaltimecc = Duration.ofSeconds(timeInSecond + timeInSecondOfOutTime);
            userAtAGlance.setOfficeInTime(intotaltimec.toHours() + ":" + intotaltimec.toMinutesPart());
            userAtAGlance.setTotalTime(totaltimecc.toHours() + ":" + totaltimecc.toMinutesPart());

            if (presentdayc != 0) {
                long averageSeconds = timeInSecond / presentdayc;
                durationc = Duration.ofSeconds(averageSeconds);
                totalextradurationc = Duration.ofSeconds(totalExtraTime);
            }

            userAtAGlance.setAvgTime(durationc.toHoursPart() + ":" + durationc.toMinutesPart());
            userAtAGlance.setLeave(leavedayc);
            userAtAGlance.setAbsent(absentdayc);
            userAtAGlance.setHoliday(holydayc);
            userAtAGlance.setShortTime(shorttimec);
            userAtAGlance.setRegularTime(regulartimec);
            userAtAGlance.setExtraTime(extratimec);
            userAtAGlance.setEntryInTime(intimec);
            userAtAGlance.setEntryLate(latetimec);
        }

        return userAtAGlance;
    }
    public UserAtAGlance getUserAtAGlanceData(String userEmail, String startDate1, String endDate1, String header) {
        // i have email.. i wll extract rest part
        employeeList = userService.employeeList(header);
        Employee employee = employeeList.stream().filter(e -> e.getEmail().equals(userEmail)).findFirst().orElse(null);

        UserAtAGlance userAtAGlance = new UserAtAGlance();
        userAtAGlance.setEmployeeId(employee.getIdNumber());
        userAtAGlance.setEmployeeName(employee.getName());
        userAtAGlance.setStartDate(startDate1);
        userAtAGlance.setEndDate(endDate1);

        String selectedPerson = employee.getName();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ChronoLocalDate startDate = LocalDate.parse(startDate1, formatter);
        ChronoLocalDate endDate = LocalDate.parse(endDate1, formatter);

        resetCounters();

        List<AttendanceData> dataList = attendanceDataRepository.findByEmployeeIdAndUpdateStatusAndEntryDateInclusive(employee.getIdNumber(), "1", startDate1, endDate1);

        if (!dataList.isEmpty()) {
            for (AttendanceData e : dataList) {
                LocalDate entryDate;
                try {
                    entryDate = LocalDate.parse(e.getEntryDate(), formatter);
                } catch (DateTimeParseException ex) {
                    continue;
                }

                if (!isDateInRange(entryDate, startDate, endDate) || !selectedPerson.equals(e.getName())) continue;

                for (Employee g : employeeList) {
                    LocalDate joinDate = LocalDate.parse(g.getJoinDate(), formatter);
                    if (!g.getName().equals(e.getName()) || joinDate.isAfter(entryDate)) continue;

                    String empId = e.getEmployeeId();
                    String empName = e.getName();
                    String entryDay = e.getEntryDate();
                    LocalTime entryTime = e.getEntryTime().toLocalTime();
                    LocalTime exitTime = e.getExitTime().toLocalTime();

                    String status = e.getStatus();

                    if (!"Holiday".equals(status)) officedayc++;
                    if ("Present".equals(status)) {
                        Duration entryExitDuration = Duration.between(e.getEntryTime(), e.getExitTime());
                        timeInSecond += entryExitDuration.toHoursPart() * 3600L + entryExitDuration.toMinutesPart() * 60L;
                        durationc = durationc.plus(entryExitDuration);
                        presentdayc++;

                        int settingHours = returnSettingTotalHour(empId, empName, entryDay);
                        if (entryExitDuration.toHours() < settingHours) shorttimec++;
                        else if (entryExitDuration.toHours() > settingHours || entryExitDuration.toMinutesPart() > 0) extratimec++;
                        else regulartimec++;

                        LocalTime startThreshold = LocalTime.of(returnSettingStartHour(empId, empName, entryDay), returnGlobalSettingLateMinute(entryDay) + 1);
                        if (entryTime.isBefore(startThreshold)) intimec++;

                        LocalTime lateLimit = LocalTime.of(returnSettingStartHour(empId, empName, entryDay), returnGlobalSettingLateMinute(entryDay));
                        if (entryTime.isAfter(lateLimit)) {
                            latetimec++;
                            LocalTime actualStart = LocalTime.of(returnSettingStartHour(empId, empName, entryDay), returnSettingStartMinute(empId, empName, entryDay));
                            Duration lateDuration = Duration.between(actualStart, entryTime);
                            totallatedurationc = totallatedurationc.plus(lateDuration);
                        }

                        String[] endTimeData = subtractHourMinute(returnSettingEndHour(empId, empName, entryDay), returnGlobalSettingEarlyMinute(entryDay));
                        LocalTime earlyExitLimit = LocalTime.of(Integer.parseInt(endTimeData[0]), Integer.parseInt(endTimeData[1]));
                        if (exitTime.isBefore(earlyExitLimit)) earlytimec++;

                        LocalTime actualEnd = LocalTime.of(returnSettingEndHour(empId, empName, entryDay), returnSettingEndMinute(empId, empName, entryDay));
                        if (exitTime.isAfter(actualEnd)) {
                            LocalTime extendedEnd = actualEnd.plusMinutes(15);
                            if (exitTime.isAfter(extendedEnd)) {
                                Duration extraDuration = Duration.between(actualEnd, exitTime);
                                totalExtraTime += extraDuration.toHoursPart() * 3600L + extraDuration.toMinutesPart() * 60L;
                            }
                        }

                        Matcher matcher = Pattern.compile("(\\d+)\\.(\\d+)").matcher(e.getOuttime());
                        if (matcher.matches()) {
                            timeInSecondOfOutTime += Long.parseLong(matcher.group(1)) * 3600L + Long.parseLong(matcher.group(2)) * 60L;
                        }
                    }

                    if ("Leave".equals(status)) leavedayc++;
                    else if ("Absent".equals(status)) absentdayc++;
                    else if ("Holiday".equals(status)) holydayc++;
                }
            }

            userAtAGlance.setOfficeDay(officedayc);
            userAtAGlance.setTotalPresent(presentdayc);

            intotaltimec = Duration.ofSeconds(timeInSecond);
            Duration outDuration = Duration.ofSeconds(timeInSecondOfOutTime);
            userAtAGlance.setOfficeOutTime(outDuration.toHours() + ":" + outDuration.toMinutesPart());
            totaltimecc = Duration.ofSeconds(timeInSecond + timeInSecondOfOutTime);
            userAtAGlance.setOfficeInTime(intotaltimec.toHours() + ":" + intotaltimec.toMinutesPart());
            userAtAGlance.setTotalTime(totaltimecc.toHours() + ":" + totaltimecc.toMinutesPart());

            if (presentdayc != 0) {
                long averageSeconds = timeInSecond / presentdayc;
                durationc = Duration.ofSeconds(averageSeconds);
                totalextradurationc = Duration.ofSeconds(totalExtraTime);
            }

            userAtAGlance.setAvgTime(durationc.toHoursPart() + ":" + durationc.toMinutesPart());
            userAtAGlance.setLeave(leavedayc);
            userAtAGlance.setAbsent(absentdayc);
            userAtAGlance.setHoliday(holydayc);
            userAtAGlance.setShortTime(shorttimec);
            userAtAGlance.setRegularTime(regulartimec);
            userAtAGlance.setExtraTime(extratimec);
            userAtAGlance.setEntryInTime(intimec);
            userAtAGlance.setEntryLate(latetimec);
        }

        return userAtAGlance;
    }

    // Utility: reset all counters at the start
    private void resetCounters() {
        officedayc = presentdayc = avgtimec = leavedayc = absentdayc = holydayc = shorttimec =
                regulartimec = extratimec = intimec = latetimec = totallatetimec = okc = earlytimec =
                        totalextratimec  = 0;
        outtimec=0.0;
        totalExtraTime = 0;
        durationc = totallatedurationc = totalextradurationc = totaltimecc = intotaltimec = Duration.ZERO;
        timeInSecond = timeInSecondOfOutTime = 0;
    }

    private boolean isDateInRange(LocalDate date, ChronoLocalDate start, ChronoLocalDate end) {
        return (date.equals(start) || date.equals(end)) || (date.isAfter(start) && date.isBefore(end));
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

}
