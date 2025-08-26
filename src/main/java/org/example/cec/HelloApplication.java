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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            if (weekendSheet == null && holidaysSheet == null) {
                showAlert("Te rog selecteaza toate fisierele necesare!");
                return;
            }
            if(mainSheet == null){
                showAlert("Fisierul principal nu a fost selectat!");
                return;
            }
            if(weekendSheet != null && holidaysSheet == null){
                File modifiedSheet = WeekendModify(mainSheet, weekendSheet);
                if (modifiedSheet != null) {
                    try {
                        Files.copy(modifiedSheet.toPath(), outputDir.resolve(modifiedSheet.getName()), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        showAlert("Failed to copy the modified file to the output directory.");
                    }
                }
                showAlert("Fisierul principal a fost modificat cu succes folosind fisierul de weekend!");
                return;

            }
            if(weekendSheet == null && holidaysSheet != null){
                File modifiedSheet = HolidayModify(mainSheet, holidaysSheet);
                if (modifiedSheet != null) {
                    try {
                        Files.copy(modifiedSheet.toPath(), outputDir.resolve(modifiedSheet.getName()), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        showAlert("Failed to copy the modified file to the output directory.");
                    }
                }
                showAlert("Fisierul principal a fost modificat cu succes folosind fisierul de concedii!");
                return;
            }

            File modifiedSheet = WeekendModify(mainSheet, weekendSheet);
            HolidayModify(modifiedSheet, holidaysSheet);
            if (modifiedSheet != null) {
                try {
                    Files.copy(modifiedSheet.toPath(), outputDir.resolve(modifiedSheet.getName()), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    showAlert("Failed to copy the modified file to the output directory.");
                }
            }

            showAlert("Fisierul principal a fost modificat cu succes folosind ambele fisiere!");



        });

        Button instructionsButton = new Button("How to Use");
        instructionsButton.setPrefWidth(200);
        instructionsButton.setStyle("-fx-background-color: rgba(0,123,255,0.85); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20 10 20;");
        VBox root = new VBox(30, title, fileSelectors, processButton, instructionsButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(0,100,200,0.85), rgba(0,180,255,0.85));");

        Scene mainScene = new Scene(root, 600, 400);
        stage.setScene(mainScene);
        stage.setTitle("Excel File Selector");
        stage.setMinWidth(500);
        stage.setMinHeight(350);
        stage.show();

        instructionsButton.setOnAction(e -> stage.setScene(createInstructionsScene(stage, mainScene))); // Redirect to instructions page
    }

    private Scene createInstructionsScene(Stage stage, Scene mainScene) {
        Label title = new Label("How to Use the Application");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label instructions = new Label(
                "1. Select the required Excel files using the 'Browse' buttons.\n" +
                        "2. Ensure the files are correctly formatted.\n" +
                        "3. Click 'Process Data' to modify the main sheet.\n" +
                        "4. The modified file will be saved in the 'arhiva' folder."
        );
        instructions.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-padding: 10;");

        Button backButton = new Button("Back to Main Page");
        backButton.setStyle("-fx-background-color: rgba(0,123,255,0.85); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20 10 20;");
        backButton.setOnAction(e -> stage.setScene(mainScene)); // Go back to the main scene

        VBox layout = new VBox(20, title, instructions, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(0,100,200,0.85), rgba(0,180,255,0.85));");

        return new Scene(layout, 600, 400);
    }

    private HBox createFileSelector(String labelText, FileConsumer fileSetter) {
        Label label = new Label(labelText + ":");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        TextField filePath = new TextField();
        filePath.setEditable(true); // to enable text editing in the filepath
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
        List<Holiday> holidays;
        String filePath = holidaysSheet.getAbsolutePath();

        holidays = InitialiseHolidaysList(holidaysSheet);

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
                String magazin = row.getCell(1).getStringCellValue();
                name = name.toLowerCase();
                //magazin = magazin.toLowerCase();
                for( Holiday holiday : holidays) {
                    if (holiday.getName().toLowerCase().equals(name)) {
                        // we found a match, we can modify the row
                        // now based on the reason of the holiday, we do color the row from the mainSheet ( at that specific employee, from the first day to the last day)
                        int firstDay = holiday.getFirstDay();
                        int lastDay = holiday.getLastDay();
                        String reason = holiday.getReason();

                        XSSFColor colorBefore = (XSSFColor) headerRow.getCell(firstDay + 2).getCellStyle().getFillForegroundColorColor(); // two days before the first day of the holiday
                        if(colorBefore != null){
                            String hexColorBefore = colorBefore.getARGBHex();
                            String rgbHexBefore = hexColorBefore.substring(2, 8); // remove alpha channel
                            rgbHexBefore = "#" + rgbHexBefore.toUpperCase();
                            if (rgbHexBefore.equals("#FFFF00")) { // if is yellow, we delete the shift ( 8 0 0 ) , monday now is free and he cant work no more on that day
                               headerRow.getCell(firstDay + 2).setCellValue("");
                            }
                        }

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
                                cell.setCellValue("");// clear the cell value if it's weekend day bacause he got a holiday and he will not longer work that specific weekend shift if he is on holiday( holiday from 10 to 23 ,
                                // weekend is 12,13, he had a shift on Sunday, but the shift needs to be removed now since he got a holiday cannot work no anymore)
                                continue;
                            }

                            color = (XSSFColor) headerRow.getCell(colIndex + 2).getCellStyle().getFillForegroundColorColor();
                            rgbHex = "#FFFFFF"; // default white color
                            if (color != null) {
                                String hexColor = color.getARGBHex();
                                rgbHex = hexColor.substring(2, 8); // remove alpha channel
                                rgbHex = "#" + rgbHex.toUpperCase();
                            }
                            if(headerRow.getCell(colIndex + 2) != null && rgbHex.equals("#FFFF00")) {
                                // if the cell is a weekend day, we skip it
                                if(headerRow.getCell(colIndex + 2).getNumericCellValue() > 0 ) headerRow.getCell(colIndex + 2).setCellValue("");
                            }

                            // Set the cell value to the reason
                            cell.setCellValue(""); // Clear the cell value by setting it to an empty string
                            // Create a new cell style
                            CellStyle newStyle = row.getSheet().getWorkbook().createCellStyle();
                            newStyle.cloneStyleFrom(cell.getCellStyle());

                            // Set the cell style based on the reason
                            if (reason.equals("concediu")) {
                               newStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(0x00, 0xB0, 0x50), null));
                            } else if (reason.equals("maternitate")) {
                                newStyle.setFillForegroundColor(IndexedColors.PINK.getIndex());
                            } else if (reason.equals("medical")) {
                                newStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());;
                            } else if (reason.equals("absenta")) {
                                newStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
                            } else if (reason.equals("demisie")) {
                                newStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
                            }
                            newStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                            cell.setCellStyle(newStyle);
                        }

                        if(reason.equals("materniate"))
                            row.getCell(daysInMonth + 6).setCellValue(row.getCell(daysInMonth + 6).getNumericCellValue() + (lastDay - firstDay + 1)); // add the number of days of maternity leave to the maternity leave column
                        if(reason.equals("concediu"))
                           row.getCell(daysInMonth + 6).setCellValue(row.getCell(daysInMonth + 6).getNumericCellValue() + (lastDay - firstDay + 1));

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

    private List<Holiday> InitialiseHolidaysList(File holidaysSheet) {
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
                String magazin = row.getCell(1).getStringCellValue();
                String period;
                if (row.getCell(2).getCellType() == CellType.STRING) {
                    period = row.getCell(2).getStringCellValue();
                } else if (row.getCell(2).getCellType() == CellType.NUMERIC) {
                    int day = row.getCell(2).getDateCellValue().getDate();
                    period = day + "-" + day;
                } else {
                    period = "";
                }

                System.out.println("Vacation Period: " + period);
                String firstDay = period.split("-")[0].trim();
                String lastDay = period.split("-")[1].trim();
                String reason = row.getCell(3).getStringCellValue();
                reason = switch (reason) {
                    case "co" -> "concediu";
                    case "m" -> "maternitate";
                    case "cm" -> "medical";
                    case "abs" -> "absenta";
                    case "dem" -> "demisie";
                    default -> "concediu";
                };
                Holiday holiday = new Holiday(Integer.parseInt(firstDay), Integer.parseInt(lastDay), reason, name, magazin);
                holidays.add(holiday);
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

        return holidays;
    }

    private File WeekendModify(File mainSheet, File weekendSheet){

        Map< String , List<Employee> > weekendEmployees;

        WeekendShift test = new WeekendShift();
        test.initialiseSize(weekendSheet); // to set the size of the weekend shift ( static variable)
        System.out.println("da");
        weekendEmployees = InitialiseWeekendList(weekendSheet);
        if( weekendEmployees.isEmpty()){
            System.out.println("Eroare la initializarea listei de angajati pentru weekend!");
        }
        else System.out.println("Lista de angajati pentru weekend a fost initializata cu succes!");

        for( String magazin : weekendEmployees.keySet()){
            System.out.println("Magazin: " + magazin);
            List < Employee > employees = weekendEmployees.get(magazin);
            if( employees.isEmpty()) System.out.println("GOL");
            for( Employee employee : employees){
                System.out.println("Employee: " + employee.name + ", Shifts: " + employee.numberOfShifts + ", Has Worked Saturday: " + employee.hasWorkedSaturday);
                for( int i = 0; i < WeekendShift.size; i++){
                    System.out.println("Shift day " + (i+1) + ": " + WeekendShift.pos[i]);
                    System.out.print( employee.shift.work[i] + " ");
                }
            }
        }



        return mainSheet;
    }

    private Map< String, List<Employee>> InitialiseWeekendList(File weekendSheet) {
        try (FileInputStream fis = new FileInputStream(weekendSheet);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Map<String, List<Employee>> weekendEmployees = new HashMap<>();
            String magazin = "";
            List < Employee > employees = new ArrayList<>();

            for (int rowIndex = 2; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                Row nextRow = sheet.getRow(rowIndex + 1);
                if (row == null) break;

                String name = row.getCell(1).getStringCellValue();
                if(!row.getCell(0).getStringCellValue().isEmpty()) magazin = row.getCell(0).getStringCellValue();

                int numberOfShifts = (int) row.getCell(2).getNumericCellValue();
                int hasWorkedSaturday = (int) row.getCell(3).getNumericCellValue(); // assuming the hasWorkedSaturday is in the fourth cell (index 3) of the row




                WeekendShift shift = new WeekendShift();
                shift.initialiseDays(WeekendShift.size);
                Employee employee = new Employee(name, numberOfShifts, shift);
                if(hasWorkedSaturday != 0) employee.hasWorked(true);
                employees.add(employee);
                if(!nextRow.getCell(0).getStringCellValue().isEmpty()) {
                    weekendEmployees.put(magazin, new ArrayList<>(employees));
                    employees.clear();
                }
                if(nextRow.getCell(1).getStringCellValue().isEmpty()){
                    weekendEmployees.put(magazin, new ArrayList<>(employees));
                    employees.clear();
                    break;
                }

            }

            return weekendEmployees;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>(); // return an empty map if there was an error
    }



    @FunctionalInterface
    interface FileConsumer {
        void setFile(File file);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public int getValueint(Cell cell){
        if(cell.getCellType() == CellType.NUMERIC) return (int) cell.getNumericCellValue();
        if(cell.getCellType() == CellType.STRING) return Integer.parseInt(cell.getStringCellValue());
        return 0;
    }
}
