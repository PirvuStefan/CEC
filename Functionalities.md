# CEC (Central Excel Controller) - Functionalities Documentation

## Project Overview
CEC is a JavaFX-based application designed to manage and automate employee shift scheduling in Excel workbooks. The application processes three types of Excel files: a main attendance sheet, a weekend shifts sheet, and a holidays/vacations sheet. It intelligently assigns shifts, tracks working hours, and manages employee absences.

---

## Launcher
The entry point of the application. This class launches the JavaFX application by delegating to the `HelloApplication` class.

```java
public class Launcher {
    public static void main(String[] args) {
        HelloApplication.launch(HelloApplication.class, args);
    }
}
```

**Purpose**: Required to properly package and run the JavaFX application as a JAR file, as JavaFX applications need a launcher class that doesn't extend `Application`.

---

## Main Application Page (HelloApplication)

The main GUI application that provides a user-friendly interface for selecting Excel files and processing employee shift data. Upon launch, it creates an archive folder if it doesn't exist and presents the user with file selection options.

### Key Attributes

```java
private File mainSheet;         // The main attendance Excel file
private File weekendSheet;      // The weekend shifts Excel file
private File holidaysSheet;     // The holidays/vacations Excel file
static int daysInMonth;         // Number of days in the current month
private boolean reset;          // Flag for resetting input fields
```

### File Selection Interface

The application provides three file selectors for the user:

```java
fileSelectors.getChildren().addAll(
    createFileSelector("Fisierul Principal", file -> mainSheet = file),
    createFileSelector("Fisierul Weekend", file -> weekendSheet = file),
    createFileSelector("Fisierul Vacante", file -> holidaysSheet = file)
);
```

### Processing Logic

When the user clicks "Proceseaza Datele" (Process Data), the application determines which files are selected and processes them accordingly:

```java
// If only weekend sheet is selected
if(weekendSheet != null && holidaysSheet == null){
    File modifiedSheet = WeekendModify.Launch(mainSheet, weekendSheet);
    // Copy to archive folder
}

// If only holidays sheet is selected
if(weekendSheet == null && holidaysSheet != null){
    File modifiedSheet = HolidayModify.Launch(mainSheet, holidaysSheet);
    // Copy to archive folder
}

// If both sheets are selected
File modifiedSheet = HolidayModify.Launch(mainSheet, holidaysSheet);
WeekendModify.Launch(modifiedSheet, weekendSheet);
```

### Name Normalization

The application includes a sophisticated name normalization function to handle different text encodings and special characters:

```java
static String normalizeName(String s) {
    String t = Normalizer.normalize(s, Normalizer.Form.NFKC);
    t = t.replace('\u00A0', ' ');                // NBSP -> normal space
    t = t.replaceAll("[\\u200B\\uFEFF\\p{Cf}]", ""); // zero-width chars
    t = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    t = t.replaceAll("\\s+", " ").trim();
    return t.toUpperCase();
}
```

---

## Employee Class

Represents an employee with their shift requirements and weekend work history.

### Attributes

```java
String name;                    // Employee name
int numberOfShifts;             // Number of weekend shifts required for the month
boolean hasWorkedSaturday;      // Whether they worked Saturday in previous month
WeekendShift shift;             // Associated weekend shift data
```

### Constructor Example

```java
Employee(String name, int numberOfShifts, WeekendShift shift) {
    this.name = name;
    this.numberOfShifts = numberOfShifts;
    this.hasWorkedSaturday = false;
    this.shift = shift;
}
```

**Use Case**: Tracking whether an employee worked the last Saturday of the previous month is crucial when the current month starts on Sunday, as it affects fair shift distribution.

---

## Holiday Class

Represents a vacation or absence period for an employee.

### Attributes

```java
private final int firstDay;     // First day of the holiday/absence
private final int lastDay;      // Last day of the holiday/absence
private String reason;          // Type: concediu, medical, absenta, demisie, maternitate
private String name;            // Employee name
private String magazin;         // Store location
```

### Holiday Types

- **co/CO**: Concediu (Vacation) - colored green (#00B050)
- **cm/CM/m/M**: Medical leave - colored aqua
- **abs/ABS**: Absenta (Absence) - colored orange
- **dem/DEM**: Demisie (Resignation) - colored red

### Example Usage

```java
Holiday holiday = new Holiday(10, 23, "concediu", "Ion Popescu", "Magazin Central");
// Employee "Ion Popescu" has vacation from day 10 to day 23
```

---

## ParseWorkingHours Class

Initializes and validates the main Excel sheet by automatically filling in default 8-hour workdays for white cells (non-special days).

### Main Method

```java
static void initializeSheet(File mainSheet, int daysInMonth) {
    if( mainSheet == null || !mainSheet.exists() ) {
        throw new IllegalArgumentException("Main sheet file is null or does not exist.");
    }
    
    if( testModify(mainSheet, daysInMonth) ) ModifyMainWithDaily(mainSheet, daysInMonth);
    else System.out.println("No significant modifications needed in the main sheet.");
}
```

### Test Modification Logic

The `testModify` method checks if the sheet needs daily shift initialization by analyzing what percentage of employees already have "8" values in their white cells:

```java
private static boolean testModify(File mainSheet, int daysInMonth) {
    int count = 0;
    int work = 0;
    
    for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
        Row row = sheet.getRow(rowIndex);
        // Check if employee has "8" in white cells
        for(int i = 1; i <= daysInMonth; i++){
            Cell cell = row.getCell(i + 4);
            boolean b = checkColor(cell);
            if(!b) continue; // skip non-white cells
            
            if (cellValue.equals("8")) {
                ok = true;
                break;
            }
        }
        if(ok) work++;
        count++;
    }
    
    return count > 0 && ((double) work / count) <= 0.15; // Need modification if ≤15% have data
}
```

### Color Checking

The class uses color checking to identify which cells should be modified:

```java
private static boolean checkColor(Cell cell) {
    XSSFColor color = (XSSFColor) cell.getCellStyle().getFillForegroundColorColor();
    String rgbHex = "#FFFFFF"; // default white color
    if(color != null){
        String hexColor = color.getARGBHex();
        rgbHex = hexColor.substring(2, 8); // remove alpha channel
        rgbHex = "#" + rgbHex.toUpperCase();
    }
    return (rgbHex.equals("#FFFFFF") || rgbHex.equals("#002060")); // white or navy blue
}
```

**Key Feature**: The navy blue color (#002060) marks the employee's start date at the company, and the function respects this by only filling cells after this date.

---

## HolidayModify Class

Processes employee holidays and absences by coloring the appropriate cells in the main sheet and updating leave counters.

### Launch Method

```java
static File Launch(File mainSheet, File holidaysSheet) {
    List<Holiday> holidays;
    
    ParseWorkingHours.initializeSheet(mainSheet, daysInMonth);
    holidays = InitialiseHolidaysList(holidaysSheet);
    
    // Process each holiday and modify the main sheet
    // Apply colors based on holiday type
    // Remove weekend shifts if they fall during holidays
}
```

### Holiday Processing Example

```java
for (int i = firstDay; i <= lastDay; i++) {
    Cell cell = row.getCell(i + 4);
    
    // Skip weekends during holidays
    if (headerRow.getCell(colIndex) != null && 
        (rgbHex.equals("#FFFF00") || rgbHex.equals("#CC00FF")) && 
        !reason.equals("medical") && !reason.equals("demisie")) {
        cell.setCellValue(""); // clear weekend shift
        continue;
    }
    
    // Apply color based on reason
    if (reason.equals("concediu")) {
        newStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(0x00, 0xB0, 0x50), null));
    } else if (reason.equals("medical")) {
        newStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
    }
}
```

### Working Hours Calculation

```java
static int getWorkingHoursTotal(Row row) {
    int total = 0;
    for (int j = 1; j <= daysInMonth; j++) {
        Cell cell = row.getCell(j + 4);
        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            total += (int) cell.getNumericCellValue();
        } else {
            try {
                total += Integer.parseInt(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                // ignore non-numeric values
            }
        }
    }
    return total;
}
```

**Special Handling**: When an employee resigns (demisie), all days after the resignation date are marked red, and their name cell is also colored red.

---

## WeekendShift Class

Manages weekend shift data including weekend days and holidays (sarbatori) from the weekend Excel sheet.

### Static Attributes

```java
static int size;                // Number of weekend days in the month
static int sarbatoriSize;       // Number of holidays in the month
static int[] pos;               // Array of weekend day positions (max 32)
static int[] sarbatoare;        // Array of holiday positions (max 10)
```

### Initialization Example

```java
public void initialiseSize(File weekendFile) {
    int count = 0;
    int sarbatoriCount = 0;
    pos = new int[32];
    sarbatoare = new int[10];
    
    Workbook workbook = new XSSFWorkbook(fis);
    Sheet sheet = workbook.getSheetAt(0);
    Row row = sheet.getRow(1);
    
    while (row.getCell(count + 2) != null) {
        if(!checkColor(row.getCell(count + 2))) {
            // Non-white cell = holiday
            sarbatoare[sarbatoriCount++] = getValueint(row, count + 2);
        } else {
            // White cell = regular weekend day
            pos[position++] = getValueint(row, count + 2);
        }
        count++;
    }
    size = position - 2;
    sarbatoriSize = sarbatoriCount;
}
```

**Purpose**: Distinguishes between regular weekend days (white cells) and public holidays (colored cells) to properly allocate shifts.

---

## WeekendModify Class

The most complex class in the application. It intelligently assigns weekend shifts to employees while ensuring fair distribution and respecting constraints.

### Launch Method Flow

```java
static File Launch(File mainSheet, File weekendSheet){
    WeekendShift test = new WeekendShift();
    test.initialiseSize(weekendSheet);
    
    Map<String, List<Employee>> weekendEmployees = InitialiseWeekendList(weekendSheet);
    ParseWorkingHours.initializeSheet(mainSheet, daysInMonth);
    
    // For each store
    for(String magazin : weekendEmployees.keySet()) {
        List<Employee> employees = weekendEmployees.get(magazin);
        
        // Generate shift assignments
        int[][] x = generateShift1(x, numberOfShifts, WeekendShift.pos);
        int[][] y = generateShiftEmployeesHolidays(employees, weekendSheet);
        
        // Apply shifts to each employee
        for(int i = 0; i < employees.size(); i++) {
            mainSheet = WeekendModifyEmployee(mainSheet, employees.get(i).name, 
                                             x[i], WeekendShift.pos, y[i], WeekendShift.sarbatoare);
        }
    }
}
```

### Shift Generation Algorithm

The algorithm ensures fair distribution by assigning shifts to days with the minimum number of already assigned employees:
This assures the normal distribution of shifts among all the employees from a specific store using a Greedy approach.

```java
private static int[] generateLine(int[][] x, int lineIndex, int[] numberOfShifts, int[] pos) {
    int[] v = new int[WeekendShift.size];
    int minim = calculateMin(x, lineIndex); // find minimum shifts assigned per day
    
    do {
        for (int i = 0; i < v.length; i++) {
            int count = 0;
            for (int j = 0; j < lineIndex; j++) {
                if (x[j][i] == 1) count++;
            }
            
            if (count == minim) {
                // Assign Saturday shift (includes both Sat & Sun)
                if(whatDay(pos[i], pos).equals("sambata") && v[i + 1] == 0) {
                    v[i] = 1;
                    numberOfShifts[lineIndex]--;
                }
                // Assign Sunday shift
                else if (whatDay(pos[i], pos).equals("duminica") && v[i - 1] == 0) {
                    v[i] = 1;
                    numberOfShifts[lineIndex]--;
                }
            }
        }
        if(!loop) minim++; // increase minimum to allow more flexibility
    } while(numberOfShifts[lineIndex] > 0);
    
    return v;
}
```

### Day Type Identification

```java
private static String whatDay(int x, int[] v) {
    // Check if it's a standalone Sunday at start of month
    if(x == v[0] && v[0] + 1 != v[1]) return "duminicaF";
    
    // Check if it's a standalone Saturday at end of month
    if(x == v[WeekendShift.size - 1] && 
       v[WeekendShift.size - 1] - 1 != v[WeekendShift.size - 2]) return "sambataF";
    
    // Check for Saturday-Sunday pair
    for(int i = 0; i < WeekendShift.size; i++) {
        if(v[i] == x && i + 1 < WeekendShift.size && v[i] + 1 == v[i + 1]) 
            return "sambata";
        else if(v[i] == x && i - 1 >= 0 && v[i] - 1 == v[i - 1]) 
            return "duminica";
    }
    return "none";
}
```

### Shift Application Example

```java
switch (whatDay(day, pos)) {
    case "sambataF" -> row.getCell(colIndex).setCellValue(8);
    case "duminicaF" -> row.getCell(colIndex).setCellValue(8);
    case "sambata" -> {
        row.getCell(colIndex).setCellValue(8);           // Saturday
        if (day + 2 <= daysInMonth) 
            row.getCell(colIndex + 2).setCellValue("");  // Clear Monday
        if (day + 1 <= daysInMonth) 
            row.getCell(colIndex + 1).setCellValue("");  // Clear Sunday
    }
    case "duminica" -> {
        row.getCell(colIndex).setCellValue(8);           // Sunday
        if (day > 2) 
            row.getCell(colIndex - 2).setCellValue("");  // Clear Friday
        if (day > 1) 
            row.getCell(colIndex - 1).setCellValue("");  // Clear Saturday
    }
}
```

**Key Feature**: The algorithm automatically clears regular working days (Monday after Sat shift, or Friday before Sun shift) when assigning weekend shifts, as those are compensatory days off.

---

## DeleteModify Class

Removes all progress data for employees from a specific store while preserving structural information like start dates.

### Launch Method

```java
static File Launch(File mainSheet, String center) {
    Sheet sheet = workbook.getSheetAt(0);
    center = normalizeName(center).replaceAll("[\\s\\-]+", "");
    
    for(int i = 0 ; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        String magazin = row.getCell(1).getStringCellValue();
        magazin = normalizeName(magazin).replaceAll("[\\s\\-]+", "");
        
        if(magazin.equals(center)) {
            System.out.println("Deleting holidays for employee: " + name);
            deleteRow(sheet, i);
        }
    }
}
```

### Color-Based Deletion Logic

```java
private static String checkColorValability(Cell cell) {
    XSSFColor color = (XSSFColor) cell.getCellStyle().getFillForegroundColorColor();
    String rgbHex = "#FFFFFF";
    if (color != null) {
        String hexColor = color.getARGBHex();
        rgbHex = "#" + hexColor.substring(2, 8).toUpperCase();
    }
    
    // Holiday colors (green, aqua, orange, red) -> Convert to white
    if (rgbHex.equals("#00B050") || rgbHex.equals("#00FFFF") || 
        rgbHex.equals("#FFA500") || rgbHex.equals("#FF0000"))
        return "DELETE_COLOR";
        
    // Preserve structural colors (navy blue = start date, white = base)
    if (rgbHex.equals("#002060") || rgbHex.equals("#FFFFFF"))
        return "NOT_DELETE";
        
    // Remove shift progress (yellow = weekend, purple = holiday shifts)
    return "DELETE_PROGRESS";
}
```

### Reset Working Hours

```java
private static void resetHoursWorked(Row row) {
    row.getCell(daysInMonth + 5).setCellValue("");  // Total hours
    row.getCell(daysInMonth + 6).setCellValue("");  // CM (medical) days
    row.getCell(daysInMonth + 7).setCellValue("");  // CO (vacation) days
    row.getCell(daysInMonth + 9).setCellValue("");  // Weekend shifts
    row.getCell(daysInMonth + 10).setCellValue(""); // Holiday shifts
}
```

---

## HelloController Class

A minimal controller for the FXML view (currently not actively used in the application).

```java
public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
```

---

## Color Coding System

The application uses a sophisticated color-coding system to represent different states:

| Color | Hex Code | Meaning |
|-------|----------|---------|
| White | #FFFFFF | Regular working day |
| Navy Blue | #002060 | Employee start date |
| Yellow | #FFFF00 | Weekend shift worked |
| Purple | #CC00FF | Holiday shift worked |
| Green | #00B050 | Vacation (concediu) |
| Aqua | #00FFFF | Medical leave |
| Orange | #FFA500 | Absence |
| Red | #FF0000 | Resignation (demisie) |
| Pink | - | Maternity leave |

---

## Processing Flow

1. **User selects files** through the GUI (main sheet, weekend sheet, holidays sheet)
2. **Inputs days in month** for proper calculation
3. **Clicks "Proceseaza Datele"**
4. **ParseWorkingHours** initializes the main sheet with default 8-hour workdays
5. **HolidayModify** (if holidays file selected):
   - Reads holiday data from the holidays Excel file
   - Matches employees by normalized names
   - Colors appropriate cells based on holiday type
   - Removes conflicting weekend shifts
   - Updates leave day counters
6. **WeekendModify** (if weekend file selected):
   - Reads required shifts per employee
   - Generates fair shift distribution using min-max algorithm
   - Assigns weekend shifts intelligently
   - Clears compensatory days off
   - Updates weekend hours counters
7. **Result saved** to archive folder with timestamp

---

## Key Design Patterns and Techniques

### Factory Pattern (Interface)
```java
@FunctionalInterface
interface FileConsumer {
    void setFile(File file);
}
```

### Name Normalization for Robust Matching
Handles various text encodings, diacritics, invisible characters, and whitespace variations to ensure employee names match correctly across different Excel files.

### Color-Based State Management
Uses Excel cell colors as a state machine to track and manage different types of days and employee statuses.

### Fair Distribution Algorithm
Implements a greedy algorithm that minimizes the maximum number of shifts per day, ensuring balanced workload distribution among employees.

---

## Technical Stack

- **JavaFX**: GUI framework
- **Apache POI (XSSF)**: Excel file manipulation
- **Java 11+**: Core language features including text normalization

---

## Conclusion

The CEC application demonstrates sophisticated business logic for workforce management, combining GUI design, Excel manipulation, constraint satisfaction, and fair scheduling algorithms. It automates what would otherwise be hours of manual Excel work, while ensuring accuracy and fairness in shift distribution.

