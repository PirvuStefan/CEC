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
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.swap;

public class HelloApplication extends Application {

    private File mainSheet;
    private File weekendSheet;
    private File holidaysSheet;
    private static int daysInMonth;
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



        // Add input for "How many days are in the month?"
        Label daysLabel = new Label("Cate zile sunt in luna asta?");
        daysLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        TextField daysInput = new TextField();
        daysInput.setPromptText("numarul de zile (e.g. 30)");
        daysInput.setStyle("-fx-background-radius: 8; -fx-background-color: white;");
        daysInput.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                int val = Integer.parseInt(newVal.trim());
                if (val >= 1 && val <= 31) {
                    daysInMonth = val;
                }
            } catch (NumberFormatException ignored) {
                showAlert("Te rog introdu un numar valid intre 1 si 31 pentru zilele din luna!");
                daysInput.clear();
            }
        });
        HBox daysBox = new HBox(10, daysLabel, daysInput);
        daysBox.setAlignment(Pos.CENTER);

        fileSelectors.getChildren().addAll(
            createFileSelector("Fisierul Principal", file -> mainSheet = file),
            createFileSelector("Fisierul Weekend", file -> weekendSheet = file),
            createFileSelector("Fisierul Vacante", file -> holidaysSheet = file)
        );
        fileSelectors.getChildren().add(daysBox);

        Button processButton = new Button("Proceseaza Datele");
        processButton.setPrefWidth(200);
        styleProcessButton(processButton);
        processButton.setOnAction(e -> {
            if( daysInMonth == 0 ){
                showAlert("Te rog introdu numarul de zile din luna!");
                return;
            }
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
                        Files.copy(modifiedSheet.toPath(), outputDir.resolve(modifiedSheet.getName()), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        showAlert("Failed to copy the modified file to the output directory.");
                    }
                }
                showAlert("Fisierul principal a fost modificat cu succes folosind fisierul de weekend!");
                resetStaticVariables();
                clearFileSelections(fileSelectors);
                return;

            }
            if(weekendSheet == null && holidaysSheet != null){
                File modifiedSheet = HolidayModify(mainSheet, holidaysSheet);
                if (modifiedSheet != null) {
                    try {
                        Files.copy(modifiedSheet.toPath(), outputDir.resolve(modifiedSheet.getName()), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        showAlert("Failed to copy the modified file to the output directory.");
                    }
                }
                showAlert("Fisierul principal a fost modificat cu succes folosind fisierul de concedii!");
                resetStaticVariables();
                clearFileSelections(fileSelectors);
                return;
            }

            File modifiedSheet = WeekendModify(mainSheet, weekendSheet);
            HolidayModify(modifiedSheet, holidaysSheet);
            if (modifiedSheet != null) {
                try {
                    Files.copy(modifiedSheet.toPath(), outputDir.resolve(modifiedSheet.getName()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    showAlert("Failed to copy the modified file to the output directory.");
                }
            }

            showAlert("Fisierul principal a fost modificat cu succes folosind ambele fisiere!");
            resetStaticVariables();
            clearFileSelections(fileSelectors);



        });

        Button instructionsButton = new Button("Cum folosim aplicatia?");
        instructionsButton.setPrefWidth(200);
        instructionsButton.setStyle("-fx-background-color: rgba(0,123,255,0.85); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20 10 20;");
        VBox root = new VBox(30, title, fileSelectors, processButton, instructionsButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(0,100,200,0.85), rgba(0,180,255,0.85));");

        Scene mainScene = new Scene(root, 600, 400);
        stage.setScene(mainScene);
        stage.setTitle("Central Excel Controller");
        stage.setMinWidth(500);
        stage.setMinHeight(350);
        stage.show();

        instructionsButton.setOnAction(e -> stage.setScene(createInstructionsScene(stage, mainScene))); // Redirect to instructions page
    }

    private void clearFileSelections(VBox fileSelectors) {
       for (javafx.scene.Node node : fileSelectors.getChildren()) {
           if (node instanceof HBox) {
               HBox hbox = (HBox) node;
               for (javafx.scene.Node child : hbox.getChildren()) {
                   if (child instanceof TextField) {
                       ((TextField) child).clear();
                   }
               }
           }
           if (node instanceof TextField) {
               ((TextField) node).clear();
           }
       }
        mainSheet = null;
        weekendSheet = null;
        holidaysSheet = null;
    }

    private Scene createInstructionsScene(Stage stage, Scene mainScene) {
        Label title = new Label("Ghid De Utilizare  \u2714");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label instructions = new Label(
            """
            1. Selecteaza fisierele necesare apasand pe butonul cauta .
            2. Fii sigur ca fisierele sunt corect formatate .
            3. Apasa 'Proceseaza Datele' .
            4. Fisierul modificat se poate gasi in folderul 'arhiva' .
            5. Nu uita sa faci o copie de siguranta a fisierelor originale inainte de procesare.
            6. Toate sheet-urile trebuie sa fie pe pozitia 0 ( primul sheet din excel ) .
            """
        );
        instructions.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-padding: 10;");

        Button backButton = new Button("Inapoi la Pagina Principala");
        backButton.setStyle("-fx-background-color: rgba(0,123,255,0.85); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20 10 20;");

        Button weekendDetailsButton = new Button("Detalii Weekend");
        weekendDetailsButton.setStyle("-fx-background-color: rgba(0,123,255,0.85); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20 10 20;");

        Button vacanteDetailsButton = new Button("Detalii Vacante");
        vacanteDetailsButton.setStyle("-fx-background-color: rgba(0,123,255,0.85); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20 10 20;");

        VBox layout = new VBox(20, title, instructions, weekendDetailsButton, vacanteDetailsButton, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(0,100,200,0.85), rgba(0,180,255,0.85));");

        Scene instructionsScene = new Scene(layout, 600, 400);

        backButton.setOnAction(e -> stage.setScene(mainScene));
        weekendDetailsButton.setOnAction(e -> stage.setScene(createWeekendDetailsScene(stage, mainScene, instructionsScene)));
        vacanteDetailsButton.setOnAction(e -> stage.setScene(createVacanteDetailsScene(stage, mainScene, instructionsScene)));

        return instructionsScene;
    }



    private Scene createWeekendDetailsScene(Stage stage, Scene mainScene, Scene instructionsScene) {
        Label title = new Label("Detalii Weekend");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label details = new Label(
                """
               Functionalitatea Weekend:
               - Permite modificarea fisierului principal pe baza programului de weekend.
               - Aloca automat turele de weekend angajatilor, tinand cont de istoricul lor si de numarul de ture necesare.
               - Zilele de sambata si duminica sunt evidentiate si gestionate separat.
               - Orice modificare este salvata in fisierul principal si arhivata in folderul 'arhiva'.
               - Pentru o functionare corecta, asigurati-va ca fisierul de weekend este bine formatat si contine toate informatiile necesare.
               - Fiecare angajat trebuie sa aiba un numar specific de ture si sa fie marcat daca a lucrat sambata in luna precedenta.
               - In cazul in care luna incepe cu o duminica, se va tine cont daca angajatul a lucrat sambata in luna precedenta pentru a aloca turele corect.
               - Pentru o gestionare eficienta, este esential ca numarul de ture alocate fiecarui angajat sa nu fie mai mare decat numarul de weekend-uri din luna respectiva.
               """
        );
        details.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-padding: 10;");

        Button backButton = new Button("Înapoi la Ghid");
        backButton.setStyle("-fx-background-color: rgba(0,123,255,0.85); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20 10 20;");
        backButton.setOnAction(e -> stage.setScene(instructionsScene));

        VBox layout = new VBox(20, title, details, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(0,100,200,0.85), rgba(0,180,255,0.85));");

        return new Scene(layout, 600, 400);
    }

    private Scene createVacanteDetailsScene(Stage stage, Scene mainScene, Scene instructionsScene) {
        Label title = new Label("Detalii Vacanțe");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label details = new Label(
                """
               Functionalitatea Vacante:
                - Permite modificarea fisierului principal pe baza fisierului de vacante/concedii.
                - Zilele de concediu, medical, maternitate, absenta sau demisie sunt marcate distinct.
                - Culorile specifice sunt folosite pentru a evidentia diferitele tipuri de vacante:
                  - Concediu: Verde ( co )
                  - Maternitate: Roz ( m )
                  - Medical: Albastru deschis ( cm )
                  - Absenta: Portocaliu ( abs )
                  - Demisie: Rosu ( dem )
                - Zilele de weekend care coincid cu vacanta sunt eliberate automat.
                - In tabelul excel, in coloana "periodata" se introduce perioada vacantei in formatul "zz*zz" (ex: 10*23).
                - In coloana "tip concediu" se introduce tipul vacantei folosind abrevierile specificate mai sus.
                - Daca o persoana are mai multe perioade de vacanta, se adauga cate un rand separat pentru fiecare perioada.
                - Toate modificarile sunt salvate si arhivate in folderul 'arhiva'.
                """
        );
        details.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-padding: 10;");

        Button backButton = new Button("Înapoi la Ghid");
        backButton.setStyle("-fx-background-color: rgba(0,123,255,0.85); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20 10 20;");
        backButton.setOnAction(e -> stage.setScene(instructionsScene));

        VBox layout = new VBox(20, title, details, backButton);
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

        if(testModify(mainSheet)) ModifyMainWithDaily(mainSheet);

        holidays = InitialiseHolidaysList(holidaysSheet);

        // now we do have the holiday data, we can modify the mainSheet
        try (FileInputStream fis = new FileInputStream(mainSheet);
             Workbook workbook = WorkbookFactory.create(fis)) { // Updated to use WorkbookFactory.create



            Sheet sheet = workbook.getSheetAt(0);
            // Go to row 3 (index 3, since it's 0-based), starting from column F (index 5), and find the last column with an integer (day of month)
            Row headerRow = sheet.getRow(3);
            Row first = sheet.getRow(0);

            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) break;
                Cell nameCell = row.getCell(2);
                String name = (nameCell != null) ? nameCell.getStringCellValue() : null;
                if (name == null || name.isEmpty()) break;
                name = name.toLowerCase();
                //magazin = magazin.toLowerCase();
                for( Holiday holiday : holidays) {

                    if (holiday.getName().toLowerCase().equals(name)) {
                        // we found a match, we can modify the row
                        // now based on the reason of the holiday, we do color the row from the mainSheet ( at that specific employee, from the first day to the last day)
                        int firstDay = holiday.getFirstDay();
                        int lastDay = holiday.getLastDay();
                        String reason = holiday.getReason();

                        System.out.println(name.toLowerCase());



                        for (int i = firstDay; i <= lastDay; i++) {
                            if (i < 1 || i > 31) break;


                            if( i == firstDay && firstDay > 2 ) { // if the first day is greater than 2, we need to check if the day before the first day is a weekend day ( yellow color ) , if it is a weekend day we need to delete the shift from that day
                                XSSFColor colorBefore = (XSSFColor) headerRow.getCell(firstDay + 2).getCellStyle().getFillForegroundColorColor(); // two days before the first day of the holiday
                                if(colorBefore != null){
                                    String hexColorBefore = colorBefore.getARGBHex();
                                    String rgbHexBefore = hexColorBefore.substring(2, 8); // remove alpha channel
                                    rgbHexBefore = "#" + rgbHexBefore.toUpperCase();
                                    if (rgbHexBefore.equals("#FFFF00")) { // if is yellow, we delete the shift ( 8 0 0 ) , monday now is free and he cant work no more on that day
                                        headerRow.getCell(firstDay + 2).setCellValue("");
                                    }
                                }
                            }

                            int colIndex = i + 4; // because we start from column F (index 5)
                            if (colIndex >= row.getLastCellNum()) break; // skip if column index is out of bounds

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
                                headerRow.getCell(colIndex + 2).setCellValue("");
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
                                Cell demisieCell = row.getCell(2);
                                CellStyle demisieStyle = row.getSheet().getWorkbook().createCellStyle();
                                demisieStyle.cloneStyleFrom(demisieCell.getCellStyle());
                                demisieStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
                                demisieCell.setCellStyle(demisieStyle);
                            }
                            newStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                            cell.setCellStyle(newStyle);
                        }


                        System.out.println("Days in month: " + daysInMonth);
//                        if( reason.equals("concediu") ){
//                            String awayCellValue = "";
//                            Cell awayCell = row.getCell(daysInMonth + 7);
//                            if (awayCell == null) {
//                                awayCell = row.createCell(daysInMonth + 7, CellType.STRING);
//                            }
//                            if (awayCell.getCellType() == CellType.STRING) {
//                                awayCellValue = awayCell.getStringCellValue().trim();
//                                if (awayCellValue.isEmpty()) {
//                                    awayCellValue = Integer.toString(lastDay - firstDay + 1);
//                                } else {
//                                    try {
//                                        int current = Integer.parseInt(awayCellValue);
//                                        awayCellValue = Integer.toString(current + (lastDay - firstDay + 1));
//                                    } catch (NumberFormatException e) {
//                                        awayCellValue = awayCellValue + " + " + (lastDay - firstDay + 1);
//                                    }
//                                }
//                            } else if (awayCell.getCellType() == CellType.NUMERIC) {
//                                int current = (int) awayCell.getNumericCellValue();
//                                awayCellValue = Integer.toString(current + (lastDay - firstDay + 1));
//                            } else {
//                                awayCellValue = Integer.toString(lastDay - firstDay + 1);
//                            }
//                            System.out.println(" asta e " + row.getCell(daysInMonth + 7).getStringCellValue());
//                            row.getCell(daysInMonth + 7).setCellValue(awayCellValue);
//
//                        }


                    }
                }


                // Process each row here
               // System.out.println("Processing row " + (rowIndex + 1));
            } // to commit merge



            try (FileOutputStream fos = new FileOutputStream(mainSheet)) {
                workbook.write(fos);
            }

            System.out.println("Main sheet updated with holidays successfully!");

        } catch (IOException e) { // Added InvalidFormatException
            e.printStackTrace();
        }

        return mainSheet;
    }

    private List<Holiday> InitialiseHolidaysList(File holidaysSheet) {
        List<Holiday> holidays = new ArrayList<>();
        String filePath = holidaysSheet.getAbsolutePath();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = WorkbookFactory.create(fis)) { // Updated to use WorkbookFactory.create

            Sheet sheet = workbook.getSheetAt(0);
            System.out.println(sheet.getSheetName() );

            for (int rowIndex = 2; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) break;

                String name = row.getCell(0).getStringCellValue();
                if( name == null || name.isEmpty() ) break;
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
                String firstDay = period.split("\\*")[0].trim();
                String lastDay = period.split("\\*")[1].trim();
                String reason = row.getCell(3).getStringCellValue();
                reason = switch (reason) {
                    case "co", "CO" -> "concediu";
                    //case "m", "M" -> "maternitate";
                    case "cm", "m", "CM", "M" -> "medical";
                    case "abs", "ABS" -> "absenta";
                    case "dem", "DEM" -> "demisie";
                    default -> "concediu";
                };
                Holiday holiday = new Holiday(Integer.parseInt(firstDay), Integer.parseInt(lastDay), reason, name, magazin);
                holidays.add(holiday);
            }

            try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
                workbook.write(fos);
            }

            System.out.println("Excel file modified successfully!");

        } catch (IOException e) { // Added InvalidFormatException
            e.printStackTrace();
        }

        for (Holiday holiday : holidays) {
            System.out.println("Holiday: " + holiday.getName() + ", " + holiday.getFirstDay() + "-" + holiday.getLastDay() + ", " + holiday.getReason());
        }

        return holidays;
    }

    private File WeekendModify(File mainSheet, File weekendSheet){

        Map< String , List<Employee> > weekendEmployees;
        System.out.println("Modifying main sheet with weekend shifts...");
        WeekendShift test = new WeekendShift();
        test.initialiseSize(weekendSheet); // to set the size of the weekend shift ( static variable);
        System.out.println("Weekend size: " + WeekendShift.size);
        weekendEmployees = InitialiseWeekendList(weekendSheet);
        if( weekendEmployees.isEmpty()){
            System.out.println("Eroare la initializarea listei de angajati pentru weekend!");
        }
        else System.out.println("Lista de angajati pentru weekend a fost initializata cu succes!");

        if( testModify(mainSheet)) ModifyMainWithDaily(mainSheet);

        for( String magazin : weekendEmployees.keySet()){




            System.out.println("Magazin: " + magazin);
            System.out.print("----------------------\n");
            List < Employee > employees = weekendEmployees.get(magazin);
            int[] numberOfShifts = new int[employees.size()];
            for( int i = 0; i < employees.size(); i++) numberOfShifts[i] = employees.get(i).numberOfShifts;

            

            int[][] y;
            int[][] x = new int[employees.size()][WeekendShift.size];
            x = generateShift1(x, numberOfShifts, WeekendShift.pos); // generate the shifts for the employees
            y = generateShiftEmployeesHolidays( employees, weekendSheet); // generate the holidays for the employees


            for(int i = 0 ; i < x.length; i++){
                System.out.println("Employee: " + employees.get(i).name + " , number of shifts: " + employees.get(i).numberOfShifts );
                for(int j = 0; j < x[i].length; j++){
                    System.out.print(x[i][j] + " ");
                }
                System.out.print("\n");
            }




            for( int i = 0; i < employees.size(); i++){
               mainSheet = WeekendModifyEmployee(mainSheet, employees.get(i).name.toUpperCase(), x[i], WeekendShift.pos, y[i], WeekendShift.sarbatoare);
            }

        }


        System.out.println("Main sheet updated with weekend shifts successfully!");
        return mainSheet;
    }

    private boolean testModify(File mainsheet){

        int count = 0;
        int work = 0;
        boolean ok = false;

        try (FileInputStream fis = new FileInputStream(mainSheet);
             Workbook workbook = WorkbookFactory.create(fis)) { // Updated to use WorkbookFactory.create

            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) break;
                String name = (row.getCell(2) != null) ? row.getCell(2).getStringCellValue() : "";
                if (name == null || name.isEmpty()) break;
                name = name.trim().toUpperCase();
                if (name.isEmpty()) break;

                for(int i = 1; i <= daysInMonth; i++){
                    int colIndex = i + 4; // because we start from column F (index 5)
                    if (colIndex >= row.getLastCellNum()) break; // skip if column index is out of bounds
                    Cell cell = row.getCell(colIndex);
                    boolean skip = false;
                    boolean b = checkColor(cell);
                    if(!b) continue;

                    if (cell != null) {
                        String cellValue = "";
                        if (cell.getCellType() == CellType.STRING) {
                            cellValue = cell.getStringCellValue();
                        } else if (cell.getCellType() == CellType.NUMERIC) {
                            cellValue = String.valueOf((int) cell.getNumericCellValue());
                        }
                        if (cellValue.equals("8")) {
                            ok = true;
                            break;
                        }
                    }
                }
                if(ok) work++;
                count++;
                ok = false;




            }

            try (FileOutputStream fos = new FileOutputStream(mainSheet)) {
                workbook.write(fos);
            }

            System.out.println("Main sheet updated with daily shifts successfully!");

        } catch (IOException e) { // Added InvalidFormatException
            e.printStackTrace();
        }
        if(count == 0 ) return true;
        return count > 0 && ((double) work / count) <= 0.15;

    }

    private File ModifyMainWithDaily(File mainSheet){
        try (FileInputStream fis = new FileInputStream(mainSheet);
             Workbook workbook = WorkbookFactory.create(fis)) { // Updated to use WorkbookFactory.create

            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) break;
                String name = (row.getCell(2) != null) ? row.getCell(2).getStringCellValue() : "";
                if (name == null || name.isEmpty()) break;
                name = name.trim().toUpperCase();
                if (name.isEmpty()) break;

                for(int i = 1; i <= daysInMonth; i++){
                    int colIndex = i + 4; // because we start from column F (index 5)
                    if (colIndex >= row.getLastCellNum()) break; // skip if column index is out of bounds
                    Cell cell = row.getCell(colIndex);
                    if(!checkColor(cell)) continue; // if the cell is not white, we skip it

                    if (cell == null) {
                        cell = row.createCell(colIndex, CellType.STRING);
                    }
                    cell.setCellValue("8"); // set the cell value to 8 (daily shift)
                }




            }

            try (FileOutputStream fos = new FileOutputStream(mainSheet)) {
                workbook.write(fos);
            }

            System.out.println("Main sheet updated with daily shifts successfully!");

        } catch (IOException e) { // Added InvalidFormatException
            e.printStackTrace();
        }
        return mainSheet;
    }


    private File WeekendModifyEmployee(File mainSheet, String employeeName, int shifts[], int[] pos, int shiftsSarbatori[], int[] sarb) {
        try (FileInputStream fis = new FileInputStream(mainSheet);
             Workbook workbook = WorkbookFactory.create(fis)) { // Updated to use WorkbookFactory.create

            Sheet sheet = workbook.getSheetAt(0);
            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) break;
                String name = (row.getCell(2) != null) ? row.getCell(2).getStringCellValue() : "";
                if (name == null || name.isEmpty()) break;
                name = name.trim().toUpperCase();
                if (name.isEmpty()) break;
                employeeName = employeeName.trim().toUpperCase();
                if (name.equals(employeeName)) {
                    // we found the employee, now we can modify the shifts
                    int count = 0;
                    int sarbatoriCount = 0;
                    for (int i = 0; i < shifts.length; i++) {
                        if (shifts[i] == 1) {
                            int day = pos[i];
                            int colIndex = day + 4; // because we start from column F (index 5)

                            boolean skip = false;

                            System.out.println("Days in month: " + daysInMonth);

                            if( whatDay(day, pos).equals("sambata") ){



                                for(int j = 0; j < WeekendShift.sarbatoriSize; j++){
                                    if (WeekendShift.sarbatoare[j] == day + 2 && day + 2 <= daysInMonth) {
                                        // if the saturday is a holiday, we skip it
                                        skip = true;
                                        break;
                                    }
                                }

                                if(!checkColor(row.getCell(colIndex + 2)) && day + 2 <= daysInMonth) skip = true; // if the cell is not white, we skip it


                            }
                            else if( whatDay(day, pos).equals("duminica")){
                                for(int j = 0; j < WeekendShift.sarbatoriSize; j++){
                                    if (WeekendShift.sarbatoare[j] == day - 2 && day - 2 >= 1){

                                        skip = true;
                                        break;
                                    }
                                }
                                if(!checkColor(row.getCell(colIndex - 2)) && day - 2 >= 1) skip = true; // if the cell is not white, we skip it
                            }
                            if(skip) continue;
                            System.out.println("Modifying shift for " + employeeName + " on day " + day + " at column index " + colIndex + " with shift type " + whatDay(day, pos));




                            switch (whatDay(day, pos)) {
                                case "sambataF" -> row.getCell(colIndex).setCellValue(8);
                                case "duminicaF" -> row.getCell(colIndex).setCellValue(8);
                                case "sambata" -> {
                                    row.getCell(colIndex).setCellValue(8);
                                    if (day + 2 <= daysInMonth) row.getCell(colIndex + 2).setCellValue("");
                                    if (day + 1 <= daysInMonth) row.getCell(colIndex + 1).setCellValue("");
                                }
                                case "duminica" -> {
                                    row.getCell(colIndex).setCellValue(8);
                                    if (day > 2) row.getCell(colIndex - 2).setCellValue("");
                                    if (day > 1) row.getCell(colIndex - 1).setCellValue("");
                                }
                            }




                            // Set the cell style to light blue

                        }
                    }

                    for(int i = 0; i < pos.length; i++) {
                        Cell shiftCell = row.getCell(pos[i] + 4);
                        String cellValue = "";
                        if (shiftCell != null) {
                            if (shiftCell.getCellType() == CellType.STRING) {
                                cellValue = shiftCell.getStringCellValue();
                            } else if (shiftCell.getCellType() == CellType.NUMERIC) {
                                cellValue = String.valueOf((int) shiftCell.getNumericCellValue());
                            }
                        }
                        if (cellValue.equals("8")) count++;
                    }

                    for(int i = 0; i < shiftsSarbatori.length; i++){
                        if( shiftsSarbatori[i] == 1 ){
                            int day = sarb[i];
                            int colIndex = day + 4;
                            if( colIndex >= row.getLastCellNum() ) break; // skip if column index is out of bounds
                            sarbatoriCount++;
                            row.getCell(colIndex).setCellValue(8);
                        }
                    }
//                    System.out.println(daysInMonth + " " + count + " " + sarbatoriCount);
                    if(count < 4) row.getCell(daysInMonth + 13).setCellValue( 8 * count );
                    else row.getCell(daysInMonth + 13).setCellValue(32);
                    if(sarbatoriCount < 4) row.getCell(daysInMonth + 14).setCellValue( 8 * sarbatoriCount );
                    else row.getCell(daysInMonth + 14).setCellValue(32);

                    System.out.println("Main sheet updated with weekend shifts successfully! " + employeeName);break; // exit the loop after modifying the employee

                }
            }

            try (FileOutputStream fos = new FileOutputStream(mainSheet)) {
                workbook.write(fos);
            }



        } catch (IOException e) { // Added InvalidFormatException
            e.printStackTrace();
        }
        return mainSheet;
    }

    private boolean checkColor( Cell cell ){
        String s;
        if( cell == null ) s =  "#FFFFFF";
        XSSFColor color = (XSSFColor) cell.getCellStyle().getFillForegroundColorColor();
        String rgbHex = "#FFFFFF"; // default white color
        if(color != null){
            String hexColor = color.getARGBHex();
            rgbHex = hexColor.substring(2, 8); // remove alpha channel
            rgbHex = "#" + rgbHex.toUpperCase();
        }
        return rgbHex.equals("#FFFFFF");
    }



    private Map< String, List<Employee>> InitialiseWeekendList(File weekendSheet) {
        System.out.println("Initialising weekend employees from file: " + weekendSheet.getAbsolutePath());
        try (FileInputStream fis = new FileInputStream(weekendSheet);
             Workbook workbook = WorkbookFactory.create(fis)) { // Updated to use WorkbookFactory.create

            Sheet sheet = workbook.getSheetAt(0);
            Map<String, List<Employee>> weekendEmployees = new HashMap<>();
            String magazin = "";
            List < Employee > employees = new ArrayList<>();



            for (int rowIndex = 2; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row firstRow = sheet.getRow(2);
                Row row = sheet.getRow(rowIndex);
                Row nextRow = sheet.getRow(rowIndex + 1);
                if (row == null) break;

                String name = row.getCell(1).getStringCellValue();
                if( name == null || name.isEmpty() ) break;
                if(!row.getCell(0).getStringCellValue().isEmpty()) magazin = row.getCell(0).getStringCellValue();





                WeekendShift shift = new WeekendShift();
                shift.initialiseDays(WeekendShift.size);
                int numberOfShifts = 0;
                for(int i = 0 ; i < 30; i++){
                    Cell cell = row.getCell(i + 2);
                    if(cell == null) break;
                    if( !checkColor(firstRow.getCell(i)) ) continue;
                    else if(cell.getCellType() == CellType.STRING){
                        if(cell.getStringCellValue().equals("X") || cell.getStringCellValue().equals("x")) numberOfShifts++;
                    }
                }
                System.out.println(name + " has " + numberOfShifts + " shifts.");
                Employee employee = new Employee(name, numberOfShifts, shift);
                employees.add(employee);
                if(!nextRow.getCell(0).getStringCellValue().isEmpty() || nextRow.getCell(0).getStringCellValue() == null || !nextRow.getCell(0).getStringCellValue().equals(magazin)) {
                    weekendEmployees.put(magazin, new ArrayList<>(employees));
                    employees.clear();
                }
                else if(nextRow.getCell(1).getStringCellValue().isEmpty() ) {
                    weekendEmployees.put(magazin, new ArrayList<>(employees));
                    employees.clear();
                    break;
                }// if the next row is empty, we stop the process

            }

            return weekendEmployees;

        } catch (IOException e) { // Added InvalidFormatException
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

    private String whatDay(int x, int[] v){
        // testam ce e tip de zi este y

        // v[i] si v[i+1] si v[i+2]
        if( x == v[0] && v[0] + 1 != v[1]) return "duminicaF";
        if( x == v[WeekendShift.size - 1] && v[WeekendShift.size - 1] - 1 != v[WeekendShift.size - 2]) return "sambataF";

        for(int i = 0; i < WeekendShift.size; i++)
            if( v[i] == x && i + 1 < WeekendShift.size && v[i] + 1 == v[i + 1]) return "sambata";
            else if( v[i] == x && i - 1 >= 0 && v[i] - 1 == v[i - 1]) return "duminica";

        return "none";


    }

    private boolean canWorkInTheFirstDayOfTheMonth(int x, boolean hasWorkedLastSaturday, int[] pos){
        String day = whatDay(x, pos);
        return x != pos[0] || !day.equals("duminica") || !hasWorkedLastSaturday;
    }

    private void styleProcessButton(Button processButton) {
        processButton.setStyle(
                "-fx-background-color: rgba(0,123,255,0.85); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10 20 10 20;"
        );
        processButton.setOnMouseEntered(e -> processButton.setStyle(
                "-fx-background-color: rgba(0,150,255,1); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10 20 10 20;" +
                        "-fx-effect: dropshadow(gaussian, #007bff, 10, 0.5, 0, 2);"
        ));
        processButton.setOnMouseExited(e -> processButton.setStyle(
                "-fx-background-color: rgba(0,123,255,0.85); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10 20 10 20;"
        ));
       processButton.setOnMousePressed(e -> {
           processButton.setStyle(
                   "-fx-background-color: rgba(0,90,200,1); " +
                   "-fx-text-fill: white; " +
                   "-fx-font-weight: bold; " +
                   "-fx-background-radius: 10; " +
                   "-fx-padding: 10 20 10 20;" +
                   "-fx-effect: innershadow(gaussian, #003366, 10, 0.5, 0, 2);"
           );
//           javafx.scene.media.AudioClip clickSound = new javafx.scene.media.AudioClip(getClass().getResource("/sounds/click.mp3").toExternalForm());
//           clickSound.play();
       });//Make sure to place your sound file (e.g., click.mp3) in the resources/sounds directory of your project.
        processButton.setOnMouseReleased(e -> processButton.setStyle(
                "-fx-background-color: rgba(0,150,255,1); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10 20 10 20;" +
                        "-fx-effect: dropshadow(gaussian, #007bff, 10, 0.5, 0, 2);"
        ));
    }

    private int[][] generateShiftEmployeesHolidays(List < Employee > employees, File weekendSheet){
        int[][] y = new int[employees.size()][WeekendShift.sarbatoriSize];
        try {
            FileInputStream fis = new FileInputStream(weekendSheet);
            Workbook workbook = WorkbookFactory.create(fis); // Updated to use WorkbookFactory.create
            Sheet sheet = workbook.getSheetAt(0);

            Row checkRow = sheet.getRow(1);
            for( int i = 2; i <= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                if( row == null ) break;
                String name = row.getCell(1).getStringCellValue();
                if( name == null || name.isEmpty() ) break;
                name = name.toLowerCase();
                for( int j = 0; j < employees.size(); j++){
                    if( employees.get(j).name.toLowerCase().equals(name)){
                        int hol = 0;
                        for (int k = 2; k < 30; k++) {
                            if(row.getCell(k) == null ) break;
                            if(!checkColor(checkRow.getCell(k))){


                               if (row.getCell(k).getCellType() == CellType.BLANK) y[j][hol++] = 0;
                               else y[j][hol++] = 1;
                            }
                            if( hol == WeekendShift.sarbatoriSize ) break;
                        }

                    }


                }



            }

            for(int j = 0;j< employees.size(); j++){
                System.out.print("Employee: " + employees.get(j).name);
                System.out.println();
                for(int k = 0; k < WeekendShift.sarbatoriSize; k++){
                    System.out.print(y[j][k] + " ");
                }
                System.out.println();
            }

            fis.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return y;




    }

    private int[][] generateShift1(int[][] x, int[] numberOfShifts, int[] pos){

       // x[0] = generateFirstOne(workedSaturday[0], numberOfShifts[0], pos); // the first one is associated random to not be repetitive
        // do not generate the first one, might  be redundant
        for(int i = 0; i < x.length ; i++){
            x[i] = generateLine(x, i, numberOfShifts, pos);
        }





        return x;
    }

    private int[] generateFirstOne(boolean workedSaturday, int numberOfShifts, int[] pos){
          int[] x = new int[WeekendShift.size];
          if(numberOfShifts == 0) return x;
          do{
            for(int i = 0; i < x.length; i++){
              if( numberOfShifts == 0) return x;
              if( i == 0 && whatDay(pos[i], pos).equals("duminica") && !workedSaturday){  // if is the first day of the month and is a sunday and he worked last saturday, he cannot work this sunday
                  x[i] = 1;
                  numberOfShifts--;
              }
              else if( i == pos.length - 1  && whatDay(pos[i], pos).equals("sambata") )
              {
                  x[i] = 1;
                  numberOfShifts--;
              }
              else if( i > 0 && whatDay(pos[i], pos).equals("duminica") && x[i - 1] == 0 && pos[i-1] + 1 == pos[i]){ // if is a sunday and the day before was not a shift and is indeed a saturday
                  x[i] = 1;
                  numberOfShifts--;
              }
              else if( i > 0 && whatDay(pos[i], pos).equals("sambata") && x[i + 1] == 0 && pos[i+1] - 1 == pos[i]){ // if is a saturday and the day before was not a shift and is indeed a friday
                  x[i] = 1;
                  numberOfShifts--;
              }

            }
          }while(numberOfShifts > 0);


          return x;
    }

    private int[] generateLine(int[][] x, int lineIndex, int[] numberOfShifts, int[] pos) {
        int[] v = new int[WeekendShift.size];
        int minim = calculateMin(x, lineIndex); // calculate the minimum number of shifts assigned to any day so far
        int tries = 0;


        do {
            boolean loop = false;
            if (numberOfShifts[lineIndex] == 0) return v;

            for (int i = 0; i < v.length; i++) {
                if (numberOfShifts[lineIndex] == 0) return v;
                int count = 0;
                for (int j = 0; j < lineIndex; j++) {
                    if (x[j][i] == 1) count++;
                }

                if (count == minim) {
                    if (i == 0 && whatDay(pos[i], pos).equals("duminicaF") && v[i] == 0)  // if is the first day of the month and is a sunday and he worked last saturday, he cannot work this sunday
                    {
                        v[i] = 1;
                        if (--numberOfShifts[lineIndex] == 0) return v;
                        loop = true;
                    }// daca luna incepe cu o zi de duminica si a lucrat sambata in luna precedenta, nu poate lucra duminica
                    else if (i == WeekendShift.size - 1 && whatDay(pos[i], pos).equals("sambataF") && v[i] == 0) // if is the last day of the month and is a saturday
                    {
                        v[i] = 1;
                        if (--numberOfShifts[lineIndex] == 0) return v;
                        loop = true;

                    } else if (i > 0 && whatDay(pos[i], pos).equals("duminica") && v[i - 1] == 0 && pos[i - 1] + 1 == pos[i] && v[i] == 0) { // if is a sunday and the day before was not a shift and is indeed a saturday
                        v[i] = 1;
                        if(--numberOfShifts[lineIndex] == 0) return v;
                        loop = true;
                    }
                    else if( i > 0 && whatDay(pos[i], pos).equals("sambata") && v[i + 1] == 0 && pos[i+1] - 1 == pos[i] && i < WeekendShift.size - 1 && v[i] == 0){ // if is a saturday and the day before was not a shift and is indeed a friday
                          v[i] = 1;
                          if(--numberOfShifts[lineIndex] == 0) return v;
                          loop = true;
                      }



                }

            }
              if(!loop) minim++; // if we did not assign any shift in this iteration, we increase the minimum to allow more flexibility

            if( tries++ > 1000){
                System.out.println("Cannot assign shifts for employee at line index " + lineIndex);
                return v;
            }
           }while(numberOfShifts[lineIndex] > 0);



           return v;
    }

    private int calculateMin(int[][] x, int lineIndex){
        int minim = Integer.MAX_VALUE;
        for(int j = 0; j < x[0].length; j++) {
            int count = 0;
            for (int i = 0; i < lineIndex; i++) if (x[i][j] == 1) count++;

            if (count < minim) minim = count;
        }
        return minim;
    }

    private void resetStaticVariables(){
        WeekendShift.size = 0;
        WeekendShift.pos = new int[32];
        daysInMonth = 0;
        WeekendShift.sarbatoriSize = 0;
        WeekendShift.sarbatoare = new int[32];
    }

    private String addValue(String s, int val) {
        if (s.isEmpty()) return Integer.toString(val);
        try {
            int sum = Integer.parseInt(s) + val;
            return Integer.toString(sum);
        } catch (NumberFormatException e) {
            return s + " + " + val;
        }
    }

}
