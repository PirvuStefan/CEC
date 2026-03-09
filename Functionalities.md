# CEC (Central Excel Controller) - Functionalities Documentation

## Project Overview
CEC is a JavaFX-based application designed to manage and automate employee shift scheduling in Excel workbooks. The application processes four types of Excel files: a main attendance sheet, a weekend shifts sheet, a Panama (uneven work-week) sheet, and a holidays/vacations sheet. It intelligently assigns shifts, tracks working hours, manages employee absences, and supports both standard (8h) and Panama-schedule (11h) employees in the same organisation.

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
private File panamaSheet;       // The Panama uneven-shift Excel file
public static int daysInMonth;  // Number of days in the current month
static boolean reset;           // Flag for resetting input fields
```

### File Selection Interface

The application now provides **four** file selectors:

```java
fileSelectors.getChildren().addAll(
    createFileSelector("Fisierul Principal",      file -> mainSheet = file),
    createFileSelector("Fisierul Weekend",         file -> weekendSheet = file),
    createFileSelector("Fisierul Munca Inegala",   file -> panamaSheet = file),
    createFileSelector("Fisierul Vacante",          file -> holidaysSheet = file)
);
```

`panamaSheet` is independent and is **not** passed into `WeekendModify` or `HolidayModify`. It feeds `PanamaModify` instead.

### Processing Logic

When the user clicks "Proceseaza Datele" (Process Data), the application determines which files are selected and processes them accordingly:

```java
// If only weekend sheet is selected
if(weekendSheet != null && holidaysSheet == null){
    File modifiedSheet = WeekendModify.launch(mainSheet, weekendSheet);
    // Copy to archive folder
}

// If only holidays sheet is selected
if(weekendSheet == null && holidaysSheet != null){
    File modifiedSheet = HolidayModify.launch(mainSheet, holidaysSheet);
    // Copy to archive folder
}

// If both sheets are selected
File modifiedSheet = HolidayModify.launch(mainSheet, holidaysSheet);
WeekendModify.launch(modifiedSheet, weekendSheet);
// Panama processing is invoked separately via PanamaModify.launch(mainSheet, panamaSheet)
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

## Placeholders Enum

A centralized enum that holds all column offset constants used across the entire application. Using an enum prevents magic numbers from being scattered across classes and makes column layout changes a single-point update.

### Definition

```java
public enum Placeholders {
    DAY_OFFSET("4"),        // Columns 0-3 are metadata; day 1 starts at col index 5 (offset 4)
    WORKING_OFFSET("5"),    // "Total working hours" column = lastDay + 5
    MEDICAL_OFFSET("6"),    // "Medical days" column = lastDay + 6
    HOLIDAY_OFFSET("7"),    // "Holiday days" column = lastDay + 7
    WEEKEND_OFFSET("9"),    // "Weekend shifts" column = lastDay + 9
    SARBATORI_OFFSET("10"), // "Holiday shifts" column = lastDay + 10
    ABSENTEE_OFFSET("15");  // "Absence days" column = lastDay + 15

    private final String value;

    public int asInt() {
        return Integer.parseInt(value);
    }
}
```

### Usage Pattern

```java
// Reading a day cell for day N:
int colIndex = N + DAY_OFFSET.asInt(); // N + 4
Cell cell = row.getCell(colIndex);

// Writing the total working hours:
row.getCell(daysInMonth + WORKING_OFFSET.asInt()).setCellValue(total);
```

**Design Note**: Every class that reads or writes a specific summary column imports from this enum via `static import`, keeping the code self-documenting.

---

## ParseIndividualHours Class

A companion to `ParseWorkingHours` that handles per-employee daily shift initialization. Instead of bulk-initializing all employees, this class is invoked individually (e.g., during weekend processing) to fill in 8-hour shifts only for the specific employee being processed.

### Main Method

```java
static void Parse(Row row) {
    if (!checkParse(row)) return; // guard: only proceed if the row needs filling

    for (int i = startDay; i <= daysInMonth; i++) {
        Cell cell = row.getCell(i);
        if (!checkColor(cell)) continue; // skip colored (non-working) cells
        if (cell.getCellType() == CellType.STRING) cell.setCellValue("8");
        else cell.setCellValue(8);
    }
}
```

### Gate Check: `checkParse`

Before filling, the method verifies the row actually needs modifications — i.e., it has at least one white/navy-blue cell that already contains a value. If the employee has zero pre-existing values, it means they haven't been touched yet and should be initialized.

```java
static boolean checkParse(Row row) {
    int count = 0;
    startDay = getStartDay(row); // reuses ParseWorkingHours.getStartDay

    for (int i = startDay; i < daysInMonth; i++) {
        Cell cell = row.getCell(i);
        if (!checkColor(cell)) continue;
        if (checkCell(cell)) count++; // cell has a non-empty numeric/string value
    }
    return (count > 0);
}
```

### Cell Value Check

```java
static boolean checkCell(Cell cell) {
    if (cell == null) return false;
    String cellValue = "";
    if (cell.getCellType() == CellType.STRING)
        cellValue = cell.getStringCellValue();
    else if (cell.getCellType() == CellType.NUMERIC)
        cellValue = String.valueOf((int) cell.getNumericCellValue());
    return !cellValue.isEmpty();
}
```

**Key difference from `ParseWorkingHours`**: This class operates on a single `Row` already in memory — it does not open the file itself. It is called inline during `WeekendModify` processing when an employee row is already loaded.

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

## Panama Sub-Package (`panama/`)

The `panama` package contains the domain model for the Panama shift schedule. It uses a classic **abstract class + concrete subclass** pattern to represent the two possible shift variants for any given work-week.

---

### Panama (Abstract Base Class)

```java
public abstract class Panama {
    int lastDay; // The Sunday of the specific week (anchor point for back-calculation)

    public abstract void setWeekShift(Row row);
    public abstract void print();
}
```

`lastDay` always holds the **Sunday** of a work-week. Both subclasses walk *backwards* from this anchor to determine which calendar days to fill in, which makes the algorithm resilient to months of any length or starting day.

---

### PanamaFriday — Tuesday / Thursday / Friday Pattern (3 working days)

Represents the "light week" in the Panama rotation: the employee works 3 days (Tue, Thu, Fri).

```java
public class PanamaFriday extends Panama {

    public PanamaFriday(int lastDay) {
        this.lastDay = lastDay; // Sunday of the week (day off for this pattern)
    }

    // Shift schedule: Friday, Thursday, Tuesday
    // Walking backwards from Sunday:
    //   Sunday - 2 = Friday
    //   Friday - 1 = Thursday
    //   Thursday - 2 = Tuesday
    public void setWeekShift(Row row) {
        lastDay = lastDay - 2; // → Friday
        if (lastDay > 0 && lastDay <= daysInMonth)
            row.getCell(lastDay).setCellValue(11);

        lastDay--;             // → Thursday
        if (lastDay > 0 && lastDay <= daysInMonth)
            row.getCell(lastDay).setCellValue(11);

        lastDay = lastDay - 2; // → Tuesday
        if (lastDay > 0 && lastDay <= daysInMonth)
            row.getCell(lastDay).setCellValue(11);
    }
}
```

**Why 11 hours?** Panama schedule employees work 11-hour shifts on their assigned days, not the standard 8 hours used for regular employees.

**Bounds checking**: Each assignment is guarded by `lastDay > 0 && lastDay <= daysInMonth` to safely handle partial weeks at the beginning or end of a month.

---

### PanamaSunday — Monday / Wednesday / Saturday / Sunday Pattern (4 working days)

Represents the "heavy week": the employee works 4 days (Mon, Wed, Sat, Sun).

```java
public class PanamaSunday extends Panama {

    public PanamaSunday(int lastDay) {
        this.lastDay = lastDay; // Sunday of the week (working day for this pattern)
    }

    // Shift schedule: Sunday, Saturday, Wednesday, Monday
    // Walking backwards from Sunday:
    //   Sunday       (lastDay)
    //   Saturday     (lastDay - 1)
    //   Wednesday    (Saturday - 3)
    //   Monday       (Wednesday - 2)
    public void setWeekShift(Row row) {
        if (lastDay > 0 && lastDay <= daysInMonth)
            row.getCell(lastDay).setCellValue(11);      // Sunday

        lastDay--;
        if (lastDay > 0 && lastDay <= daysInMonth)
            row.getCell(lastDay).setCellValue(11);      // Saturday

        lastDay = lastDay - 3;
        if (lastDay > 0 && lastDay <= daysInMonth)
            row.getCell(lastDay).setCellValue(11);      // Wednesday

        lastDay = lastDay - 2;
        if (lastDay > 0 && lastDay <= daysInMonth)
            row.getCell(lastDay).setCellValue(11);      // Monday
    }
}
```

---

## PanamaShift Class

Extends `WeekendShift` to reuse the weekend-day detection infrastructure (`pos[]`, `sarbatoare[]`, `whatDay()`, etc.) while building a list of `Panama` objects — one per work-week in the month — for a single employee row from the Panama sheet.

### Attributes

```java
ArrayList<Panama> panamaList = new ArrayList<>();    // One Panama object per Sat/Sun block
ArrayList<Boolean> sarbatoriList = new ArrayList<>(); // True if employee works each public holiday
```

### Constructor Logic

```java
PanamaShift(File path, Row row) {
    if (PanamaShift.size == -1) initialiseSize(path); // lazy static initialization

    // Iterate over every column that represents a weekend block or holiday
    for (int i = 0; i < sarbatoriSize + size; i++) {
        int currentDay = i + 2; // columns start at index 2 in the sheet
        Cell cell = row.getCell(currentDay);

        boolean granted = cell.getCellType() == CellType.STRING
                          && (cell.getStringCellValue().equals("X") || ...);

        if (checkColor(firstRow.getCell(currentDay))) {
            // White header cell → regular weekend block
            int day1 = getValueint(firstRow, currentDay);
            String dayType = whatDay(day1, PanamaShift.pos);

            if (dayType.equals("duminicaF"))      addPanama(granted, day1);
            else if (dayType.equals("samabataF")) addPanama(granted, day1 + 1);
            else {
                // Normal Sat/Sun pair: merge both cells' X values into one decision
                granted = granted || nextCellIsX;
                addPanama(granted, day1 + 1); // day1 + 1 = Sunday
                i++; // skip Sunday column since we consumed it here
            }
        } else {
            // Colored header cell → public holiday: record individually
            sarbatoriList.add(granted);
        }
    }
}
```

### Pattern Selector: `addPanama`

```java
private void addPanama(boolean value, int position) {
    if (value) panamaList.add(new PanamaFriday(position));  // X → 3-day (Fri) week
    else       panamaList.add(new PanamaSunday(position));  // no X → 4-day (Sun) week
}
```

The `position` passed is always the **Sunday** of that week, acting as the back-calculation anchor.

### Applying Shifts: `setShift`

```java
void setShift(Row row) {
    // 1. Apply public holiday shifts (forward order)
    for (int i = 0; i < sarbatoriList.size(); i++) {
        if (Boolean.TRUE.equals(sarbatoriList.get(i))) {
            int colIndex = WeekendShift.sarbatoare[i] + DAY_OFFSET.asInt();
            row.getCell(colIndex).setCellValue(11);
        }
    }

    // 2. Apply weekly patterns in REVERSE order
    //    (reverse avoids overwriting earlier days when lastDay mutates inside setWeekShift)
    for (int idx = panamaList.size() - 1; idx >= 0; idx--) {
        panamaList.get(idx).setWeekShift(row);
    }
}
```

**Why reverse?** `setWeekShift` mutates `lastDay` as it walks backwards. Processing in reverse order (last week of month → first week) prevents the mutated anchor from accidentally writing into a previous week's cells.

---

## PanamaModify Class

The orchestrator for Panama processing — analogous to `WeekendModify` for the weekend pipeline. It reads the Panama sheet, builds a `Map<String, PanamaShift>` keyed by employee name, then applies each shift into the main sheet.

### Initialization

```java
private static Map<String, PanamaShift> InitialisePanamaShifts(File panamaSheet) {
    Map<String, PanamaShift> map = new HashMap<>();

    Sheet sheet = workbook.getSheetAt(0);
    for (int i = 0; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        String name = row.getCell(1).getStringCellValue();
        if (name == null || name.isEmpty()) break;

        PanamaShift shift = new PanamaShift(panamaSheet, row);
        map.put(name, shift);
    }
    return map;
}
```

### Applying to Main Sheet

```java
private static File launch(File mainSheet, File panamaSheet) {
    Map<String, PanamaShift> panamaShifts = InitialisePanamaShifts(panamaSheet);

    Sheet sheet = mainWorkbook.getSheetAt(0);
    for (int i = 0; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        String name = row.getCell(2).getStringCellValue().trim().toUpperCase();
        if (name.isEmpty()) break;

        if (panamaShifts.containsKey(name) || panamaShifts.containsKey(normalizeName(name))) {
            PanamaShift shift = panamaShifts.get(name);
            shift.setShift(row); // writes 11-hour values into the appropriate cells
        }
    }
    // Save modified workbook back to mainSheet
}
```

**Name matching**: Uses the same `normalizeName` utility from `HelloApplication` to handle diacritic/whitespace variations between the Panama sheet and the main sheet.

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

1. **User selects files** through the GUI (main sheet, weekend sheet, Panama sheet, holidays sheet)
2. **Inputs days in month** for proper calculation
3. **Clicks "Proceseaza Datele"**
4. **ParseWorkingHours** initializes the main sheet with default 8-hour workdays (if ≤15% of employees already have data)
5. **HolidayModify** (if holidays file selected):
   - Reads holiday data from the holidays Excel file
   - Matches employees by normalized names
   - Colors appropriate cells based on holiday type
   - Removes conflicting weekend shifts
   - Updates leave day counters
6. **WeekendModify** (if weekend file selected):
   - Reads required shifts per employee
   - Generates fair shift distribution using min-max greedy algorithm
   - Assigns weekend shifts intelligently
   - Clears compensatory days off (Monday after Sat, Friday before Sun)
   - Updates weekend hours counters
7. **PanamaModify** (if Panama file selected — independent pipeline):
   - Reads the Panama sheet and builds `Map<String, PanamaShift>` per employee
   - For each weekly block, determines Friday (3-day) or Sunday (4-day) pattern from `X` markers
   - Writes **11-hour** values for each assigned day in the main sheet
   - Processes weeks in **reverse order** to avoid anchor mutation issues
   - Applies public holiday shifts from the `sarbatoriList` separately
8. **Result saved** to archive folder

---

## Key Design Patterns and Techniques

### Factory Pattern (Interface)
```java
@FunctionalInterface
interface FileConsumer {
    void setFile(File file);
}
```

### Template Method / Polymorphism (Panama Pattern)
```java
// Abstract base defines contract
public abstract class Panama {
    int lastDay;
    public abstract void setWeekShift(Row row);
}

// Concrete subclasses implement the pattern-specific day logic
PanamaFriday  → fills Fri, Thu, Tue  (3 days, 11h each)
PanamaSunday  → fills Sun, Sat, Wed, Mon  (4 days, 11h each)
```
The decision of which subclass to instantiate is deferred to `PanamaShift.addPanama()` based on the `X` marker in the Panama sheet — a classic runtime polymorphism.

### Name Normalization for Robust Matching
Handles various text encodings, diacritics, invisible characters, and whitespace variations to ensure employee names match correctly across different Excel files.

### Color-Based State Management
Uses Excel cell colors as a state machine to track and manage different types of days and employee statuses.

### Fair Distribution Algorithm
Implements a greedy algorithm that minimizes the maximum number of shifts per day, ensuring balanced workload distribution among employees.

### Reverse Iteration for Mutation Safety (Panama)
`PanamaShift.setShift()` iterates over its `panamaList` in **reverse** because each `Panama.setWeekShift()` mutates `lastDay` as it writes days backwards. Processing from the last week to the first prevents the mutated anchor from bleeding into an earlier week's day range.

---

## Technical Stack

- **JavaFX**: GUI framework
- **Apache POI (XSSF)**: Excel file manipulation
- **Java 17+**: Core language features including text normalization, records, and switch expressions

---

## Conclusion

The CEC application demonstrates sophisticated business logic for workforce management, combining GUI design, Excel manipulation, constraint satisfaction, and fair scheduling algorithms. It automates what would otherwise be hours of manual Excel work, while ensuring accuracy and fairness in shift distribution.

The addition of the **Panama shift pipeline** (`panama/` sub-package + `PanamaShift` + `PanamaModify`) extends the system to support employees on non-standard 11-hour uneven weekly rotations, using a clean polymorphic design that is decoupled entirely from the regular weekend pipeline. The `Placeholders` enum and `ParseIndividualHours` class further reduce coupling and make the codebase easier to extend for new column layouts or per-employee initialization needs.

