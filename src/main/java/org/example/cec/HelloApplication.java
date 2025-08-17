package org.example.cec;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {

    private File mainSheet;
    private File weekendSheet;
    private File holidaysSheet;
    private int daysInMonth;
    private int firstDayOfWeekend;
    // we need to know how many days are in a month and what is the first day of the first weekend ( like to know how to count and take in consideration the weekends when we process the data)

    @Override
    public void start(Stage stage) {

        Path outputDir = Path.of("arhiva");
        try {
            if (!Files.exists(outputDir)) {
                Files.createDirectory(outputDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Label title = new Label("Excel Sheet Selector");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox fileSelectors = new VBox(20);
        fileSelectors.setPadding(new Insets(20));
        fileSelectors.setAlignment(Pos.CENTER);

        fileSelectors.getChildren().addAll(
                createFileSelector("Main Employee Sheet", file -> mainSheet = file),
                createFileSelector("Weekend Work Sheet", file -> weekendSheet = file),
                createFileSelector("Holidays Sheet", file -> holidaysSheet = file)
        );

        Button processButton = new Button("Process Data");
        processButton.setPrefWidth(200);
        processButton.setStyle("-fx-background-color: rgba(0,123,255,0.85); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20 10 20;");
        processButton.setOnAction(e -> {
            if (mainSheet == null & weekendSheet == null & holidaysSheet == null) {
                showAlert("Te rog selecteaza toate fisierele necesare!");
                return;
            }
            //holidaysSheet = HolidayModify(mainSheet, holidaysSheet);
            File modifiedSheet = HolidayModify(mainSheet, holidaysSheet);
            if (modifiedSheet != null) {
                try {
                    Files.copy(modifiedSheet.toPath(), outputDir.resolve(modifiedSheet.getName()), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    showAlert("Failed to copy the modified file to the output directory.");
                }
            }
            showAlert("Files selected:\n" +
                    "Main: " + mainSheet.getName() + "\n" +
                    "Weekend: " + weekendSheet.getName() + "\n" +
                    "Holidays: " + holidaysSheet.getName());
        });


        VBox root = new VBox(30, title, fileSelectors, processButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(0,100,200,0.85), rgba(0,180,255,0.85));");

        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Excel File Selector");
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.setMinHeight(350);
        stage.show();
    }

    private HBox createFileSelector(String labelText, FileConsumer fileSetter) {
        Label label = new Label(labelText + ":");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        TextField filePath = new TextField();
        filePath.setEditable(false);
        filePath.setStyle("-fx-background-radius: 8; -fx-background-color: white;");

        Button browseButton = new Button("Browse");
        browseButton.setStyle("-fx-background-color: rgba(255,255,255,0.8); -fx-background-radius: 8; -fx-font-weight: bold;");
        browseButton.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));
            File selectedFile = fc.showOpenDialog(null);
            if (selectedFile != null) {
                filePath.setText(selectedFile.getAbsolutePath());
                fileSetter.setFile(selectedFile);
            }
        });

        HBox box = new HBox(10, label, filePath, browseButton);
        box.setAlignment(Pos.CENTER);
        HBox.setHgrow(filePath, Priority.ALWAYS);
        return box;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private File HolidayModify(File mainSheet, File holidaysSheet) {
        List<Holiday> holidays = new ArrayList<>();
        String filePath = holidaysSheet.getAbsolutePath();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 2; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) break;

                String name = row.getCell(0).getStringCellValue();
                System.out.println("Processing row " + (rowIndex + 1) + " for employee: " + name);
                //int firstDay = (int) row.getCell(1).getNumericCellValue();
                String magazin = row.getCell(1).getStringCellValue();
                String period;
                if (row.getCell(2).getCellType() == CellType.STRING) {
                    period = row.getCell(2).getStringCellValue();
                } else if (row.getCell(2).getCellType() == CellType.NUMERIC) {
                    // If it's a date, format as day of month
                    int day = row.getCell(2).getDateCellValue().getDate();
                    period = day + "-" + day;
                } else {
                    period = "";
                } // i should never be a date, we should always have a string like "1-5" or "1-2"

                System.out.println("Vacation Period: " + period);
                String firstDay = period.split("-")[0].trim();
                String lastDay = period.split("-")[1].trim();
                String reason = row.getCell(3).getStringCellValue();
                reason = switch (reason) {
                    case "co" -> "concediu";
                    case "m" -> "maternitate";
                    case "cm" -> "medical";
                    case "absmot" -> "absentaMotivata";
                    case "absmotiv" -> "absentaNemotivata";
                    default -> "concediu"; // in caz ca nu se potriveste niciunul
                };
                Holiday holiday = new Holiday(Integer.parseInt(firstDay), Integer.parseInt(lastDay), reason, name, magazin);
                holidays.add(holiday); // adauga in vectorul de concedii






            }

            try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
                workbook.write(fos);
            }

            System.out.println("Excel file modified successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Holiday holiday : holidays) {
            System.out.println("Holiday: " + holiday.getName() + ", " + holiday.getFirstDay() + "-" + holiday.getLastDay() + ", " + holiday.getReason());
        }

        // now we do have the holiday data, we can modify the mainSheet
        try (FileInputStream fis = new FileInputStream(mainSheet);
             Workbook workbook = new XSSFWorkbook(fis)) {



            Sheet sheet = workbook.getSheetAt(0);
            // Go to row 3 (index 3, since it's 0-based), starting from column F (index 5), and find the last column with an integer (day of month)
            Row headerRow = sheet.getRow(3);
            int lastDayOfMonth = -1;
            int firstDayOfWeekend = 0;
            if (headerRow != null) {
                for (int col = 5; col < headerRow.getLastCellNum(); col++) {
                    if (headerRow.getCell(col) != null && headerRow.getCell(col).getCellType() == CellType.NUMERIC) {

                        int value = (int) headerRow.getCell(col).getNumericCellValue();
                        if (value >= 1 && value <= 31) {
                            lastDayOfMonth = value;
                        }
                    }
                    XSSFColor color = (XSSFColor) headerRow.getCell(col).getCellStyle().getFillForegroundColorColor();
                    String rgbHex ="#FFFFFF"; // default white color
                    if(color != null){
                        String hexColor = color.getARGBHex();
                        rgbHex = hexColor.substring(2, 8); // remove alpha channel
                        rgbHex = "#" + rgbHex.toUpperCase();


                    }
                    if (headerRow.getCell(col) != null && firstDayOfWeekend == 0) {
                        if (rgbHex.equals("#FFFF00")) firstDayOfWeekend = (int) headerRow.getCell(col).getNumericCellValue();
                    }
                    if( col > 35) break;
                }
            }
            System.out.println("Last day of the month detected: " + lastDayOfMonth);
            System.out.println("First day of the weekend detected: " + firstDayOfWeekend);
            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) break;
                String name = row.getCell(2).getStringCellValue();
                String magazin = row.getCell(2).getStringCellValue();
                name = name.toLowerCase();
                //magazin = magazin.toLowerCase();
                for( Holiday holiday : holidays) {
                    if (holiday.getName().toLowerCase().equals(name)) {
                        // we found a match, we can modify the row
                        // now based on the reason of the holiday, we do color the row from the mainSheet ( at that specific employee, from the first day to the last day)
                        int firstDay = holiday.getFirstDay();
                        int lastDay = holiday.getLastDay();
                        String reason = holiday.getReason();

                        for (int i = firstDay; i <= lastDay; i++) {
                            if (i < 1 || i > 31) break;

                            int colIndex = i + 4; // because we start from column F (index 5)
                            if (colIndex >= row.getLastCellNum()) continue; // skip if column index is out of bounds

                            Cell cell = row.getCell(colIndex);
                            if (cell == null) {
                                cell = row.createCell(colIndex, CellType.STRING);
                            }

                            // Skip weekends
                            XSSFColor color = (XSSFColor) headerRow.getCell(colIndex).getCellStyle().getFillForegroundColorColor();
                            String rgbHex = "#FFFFFF"; // default white color
                            if (color != null) {
                                String hexColor = color.getARGBHex();
                                rgbHex = hexColor.substring(2, 8); // remove alpha channel
                                rgbHex = "#" + rgbHex.toUpperCase();
                            }
                            if (headerRow.getCell(colIndex) != null && rgbHex.equals("#FFFF00")) {
                                continue;
                            }

                            // Create a new cell style
                            CellStyle newStyle = row.getSheet().getWorkbook().createCellStyle();
                            newStyle.cloneStyleFrom(cell.getCellStyle());

                            // Set the cell style based on the reason
                            if (reason.equals("concediu")) {
                                newStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
                            } else if (reason.equals("maternitate")) {
                                newStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                            } else if (reason.equals("medical")) {
                                newStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                            } else if (reason.equals("absentaMotivata")) {
                                newStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                            } else if (reason.equals("absentaNemotivata")) {
                                newStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                            }
                            newStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                            cell.setCellStyle(newStyle);
                        }




                        System.out.println("Updated row " + (rowIndex + 1) + " for employee: " + name);
                    }
                }


                // Process each row here
               // System.out.println("Processing row " + (rowIndex + 1));
            } // to commit merge



            try (FileOutputStream fos = new FileOutputStream(mainSheet)) {
                workbook.write(fos);
            }

            System.out.println("Main sheet updated with holidays successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return mainSheet;
    }

    @FunctionalInterface
    interface FileConsumer {
        void setFile(File file);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
