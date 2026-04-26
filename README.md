# Central Excel Controller (CEC)

A JavaFX desktop application designed to automate and streamline the management of employee shifts, weekend schedules, and vacation/holiday tracking through Excel file processing.
Please read the Functionalities.md for a more in depth explanation of the code and algorithms used in this project.

<img width="645" height="429" alt="Screenshot 2025-10-09 at 16 11 04" src="https://github.com/user-attachments/assets/e318c4a2-b22a-47af-a13e-1396555e6a57" style="display:block;margin-left:auto;margin-right:auto;" />

## Project Overview

The Central Excel Controller (CEC) is a powerful tool created to help managers and HR departments efficiently handle employee scheduling across multiple stores/locations. The application processes Excel files containing employee information and automatically:

<img width="1148" height="510" alt="Screenshot 2025-12-01 at 16 38 41" src="https://github.com/user-attachments/assets/02c40204-b19a-4459-9bf9-689495507a1e" />



- Allocates weekend shifts fairly among employees
- Manages vacation days, medical leave, and other absences
- Ensures compliance with work regulations and employee history
- Maintains accurate records with color-coded visual indicators
- Archives all processed files for future reference

### Key Features

- **User-Friendly Interface**: Simple file selection with drag-and-drop support
- **Intelligent Shift Allocation**: Automatically distributes weekend shifts based on employee availability and history
- **Holiday Management**: Color-codes different types of leave (vacation, medical, maternity, etc.)
- **Conflict Resolution**: Prevents scheduling conflicts by checking employee availability
- **Automated Archiving**: Saves all processed files in an organized `arhiva` folder
- **Validation**: Ensures correct file formats and provides helpful error messages

## System Requirements

- **Java**: JDK 17 or higher
- **JavaFX SDK**: 17+ (download from [OpenJFX](https://openjfx.io/))
- **Maven**: For dependency management and building
- **Excel Files**: `.xlsx` format (Microsoft Excel 2007+)

## Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/PirvuStefan/CEC.git
   cd CEC
   ```

2. **Build the project:**
   ```bash
   mvn clean install
   ```

3. **Run the application:**
   ```bash
   mvn javafx:run
   ```
   
This will build a fat JAR file located in the `target` directory.Also know in the java community as an uber jar.

## Usage

1. Launch the application
2. Enter the number of days in the current month
3. Select your Excel files:
   - **Fisierul Principal** (Main Sheet): Contains employee roster and daily schedules — **always required**
   - **Fisierul Weekend** (Weekend Sheet): Contains weekend shift requirements and employee availability — optional
   - **Fisierul Munca Inegala** (Panama Sheet): Contains Panama/uneven-work-week shift assignments — optional
   - **Fisierul Vacante** (Holidays Sheet): Contains vacation requests and leave information — optional
4. Click "Proceseaza Datele" (Process Data)
5. Find your processed files in the `arhiva` folder

> **Note:** You must select the Main Sheet plus at least one of the other files to proceed. The Panama sheet shares the same structure as the Weekend sheet.

## Base Functionality

### Overview
If the program has not done this on the main file before ( less than 15% of employees assigned ), all the employees that do not have a skip color will be assigned a shift on every normal-day work shift.
* This is no longer the case, this feature is not active anymore, the base-working shifts are individually assigned to each employee, once a weekend excell sheet is processed that contains that employee name, all the employees that have a start day market with `navy blue` will be assigned a shift on every normal-day work shift starting from their start day, until the end of the month.

<img width="907" height="391" alt="Screenshot 2025-12-01 at 16 40 44" src="https://github.com/user-attachments/assets/54add17b-246f-443c-8668-97398bd23bc9" />

If the employee does not have a start day market with `navy blue`, the start day will be the first day of the month (their employment has started before the current month).


## Weekend Functionality

### Overview
The weekend functionality automates the complex process of allocating weekend shifts to employees while ensuring fairness, compliance with labor regulations, and consideration of individual employee circumstances.

### How It Works

#### 1. **Weekend Shift Detection**
- Automatically identifies Saturday and Sunday shifts in the schedule
- Distinguishes between regular weekends and holiday weekends
- Tracks the first day of the month to handle edge cases (e.g., month starting on Sunday)

#### 2. **Employee Weekend History**
- Records which employees worked the previous Saturday (important for months starting on Sunday)
- Ensures employees who worked Saturday in the previous month don't work the following Sunday
- Maintains fair rotation of weekend shifts across the team

#### 3. **Shift Allocation Algorithm**
The application uses an intelligent algorithm that:

- **Prioritizes Fairness**: Distributes weekend shifts evenly among all eligible employees
- **Respects Constraints**: 
  - Maximum of 4 weekend shifts per employee per month
  - Minimum allocation ensures everyone shares weekend duty
  - Employees on vacation/leave are automatically excluded
  
- **Handles Special Cases**:
  - Months starting with Sunday (checks previous Saturday work)
  - Months ending with Saturday (ensures proper closure)
  - Consecutive weekend days (Saturday-Sunday pairs)
  - Isolated weekend days (when holidays split weekends)

#### 4. **Weekend Types**
The system recognizes different weekend day types:

- **sambata** (Saturday): Regular Saturday requiring both Saturday and Sunday coverage
- **duminica** (Sunday): Regular Sunday paired with previous Saturday
- **sambataF** (Final Saturday): Last Saturday of the month (may not have Sunday pair)
- **duminicaF** (Final Sunday): First Sunday when month starts on Sunday

#### 5. **Automatic Adjustments**
- **Holiday Conflicts**: If a weekend coincides with a holiday, the system automatically removes the shift
- **Vacation Overlaps**: Employees on vacation have their weekend shifts cleared
- **Daily Shift Integration**: Weekend shifts are integrated with daily 8-hour shift requirements

#### 6. **Validation & Correction**
- If an employee has fewer allocated shifts than required, the system force-assigns additional shifts
- Ensures minimum coverage requirements are met
- Prevents over-allocation (capped at 4 shifts per month)

#### 7. **Reporting**
The system automatically updates two columns in the main sheet:
- **Weekend Hours**: Total hours worked on regular weekends (up to 32 hours/month)
- **Holiday Weekend Hours**: Separate tracking for holiday weekend work

### Weekend Sheet Format

The Weekend Sheet must contain:
- **Column A**: Store/Location name
- **Column B**: Employee name
- **Columns C onwards**: Weekend days marked with "X" for availability
- Background colors indicate weekend days vs. holiday weekends

### Example Workflow

1. Employee "John Doe" has 3 weekend shifts allocated
2. System checks: Did John work last Saturday (if month starts Sunday)?
3. System finds available Saturdays with minimum prior assignments
4. Allocates shifts ensuring:
   - Fair distribution across the team
   - No conflicts with holidays or vacation
   - Proper Saturday-Sunday pairing when applicable
5. Updates main sheet with 8-hour shifts on allocated days
6. Clears any conflicting Monday shifts following weekend work
7. Records total weekend hours in reporting columns

## Panama Shifts Functionality

### Overview
The **Panama shift schedule** is a specific work rotation pattern designed for employees who work an uneven number of hours across the week — some weeks they work more days, some weeks fewer. This is common in retail and service environments that operate 7 days a week.

The application supports two Panama patterns:

| Pattern | Working Days | Days Off |
|---------|-------------|----------|
| **Panama Sunday** | Monday, Wednesday, Saturday, Sunday (4 days) | Tuesday, Thursday, Friday |
| **Panama Friday** | Tuesday, Thursday, Friday (3 days) | Monday, Wednesday, Saturday, Sunday |

Each week an employee alternates between these two patterns, producing a fair overall distribution of working and non-working days across the month.

### How It Works

1. **Select the "Fisierul Munca Inegala" file** — this is the Panama schedule sheet which lists which employees follow this rotation and whether they are on the 4-day (Sunday) or 3-day (Friday) variant for each weekend block.
2. The application reads each weekend block and determines which pattern applies for each employee based on an `X` marker in the sheet.
   - **X present** → the employee works the **Friday pattern** (Tue/Thu/Fri) for that week.
   - **No X** → the employee works the **Sunday pattern** (Mon/Wed/Sat/Sun) for that week.
3. For each public holiday column the shift is assigned individually: if the employee is marked with `X` for that holiday, they receive an **11-hour shift** on that day.
4. The shifts are then written into the main sheet — each assigned day gets a value of **11** (hours worked).
5. The system processes weeks in **reverse order** within the month to correctly apply the pattern without overwriting earlier days.

### Panama Sheet Format

The Panama sheet must follow this structure:

| Column A | Column B | Column C onwards |
|----------|----------|-----------------|
| Store name | Employee name | Weekend blocks (with `X` if working that block) |

- Row 1 (index 1 in the file) acts as the **header row** containing the day numbers for each weekend block.
- A **colored cell** in the header marks a public holiday; a **white cell** marks a regular weekend.
- Weekend blocks are always treated as Saturday+Sunday pairs. Isolated Sundays (first day of month) and isolated Saturdays (last day of month) are handled as special edge cases.

### Important Notes

- The Panama sheet file is optional. The main processing will still run without it.
- Employees not present in the Panama sheet are not affected.
- This functionality is independent from the regular Weekend functionality — do **not** add Panama employees to the Weekend sheet.

---

## Holiday/Vacation Functionality

### Overview
The holiday functionality provides comprehensive leave management, tracking different types of absences with visual color-coding and automatic schedule adjustments.

### Supported Leave Types

#### 1. **Concediu (CO)** - Vacation/Annual Leave
- **Color**: Green (`#00B050`)
- **Description**: Regular paid vacation days
- **Tracking**: Automatically counts days and updates vacation balance
- **Weekend Handling**: Weekends during vacation are excluded from work schedules

#### 2. **Medical (CM/M)** - Medical Leave
- **Color**: Aqua/Light Blue
- **Description**: Sick leave, medical appointments, recovery time
- **Tracking**: Separate counter for medical days
- **Documentation**: Typically requires medical certificate

#### 3. **Maternitate (M)** - Maternity Leave
- **Color**: Pink
- **Description**: Maternity/paternity leave
- **Duration**: Can span multiple months
- **Protection**: Automatically clears all shifts during period

#### 4. **Absenta (ABS)** - Absence
- **Color**: Orange
- **Description**: Unexcused absence or other non-standard leave
- **Tracking**: Flagged for review
- **Impact**: Clears scheduled shifts

#### 5. **Demisie (DEM)** - Resignation
- **Color**: Red
- **Description**: Employee resignation/termination
- **Effect**: 
  - All days from resignation date to month end are marked red
  - Employee name cell is highlighted in red
  - All remaining shifts are cleared
  - Final day calculation for payroll

### How Holiday Processing Works

#### 1. **Holiday Period Specification**
In the Holidays Sheet:
- **Column A**: Employee name
- **Column B**: Store/Location
- **Column C**: Period in format `DD*DD` (e.g., `10*23` for days 10-23)
- **Column D**: Leave type code (co, cm, m, abs, dem)

#### 2. **Automatic Schedule Adjustment**
When a holiday is processed:

1. **Identifies Employee**: Matches employee name using normalized comparison (handles accents, spaces, special characters)
2. **Marks Days**: Colors all days in the holiday period with appropriate color
3. **Clears Shifts**: Removes any scheduled work during the holiday
4. **Weekend Cleanup**: 
   - If holiday includes weekend, clears weekend shifts
   - Adjusts weekend hours calculation
5. **Adjacent Day Handling**: Checks and clears Monday shifts if weekend preceded by holiday

#### 3. **Smart Day Counting**
The system intelligently counts leave days:

- **Vacation Days**: Excludes weekends from the count
  - 10 calendar days vacation = 8 working days (excluding weekend)
- **Medical Days**: Counts consecutive days
- **Resignation**: Counts all remaining days in month

#### 4. **Multiple Leave Periods**
Employees can have multiple leave periods:
- Add separate rows for each period
- System processes all periods chronologically
- Combines totals in summary columns

#### 5. **Leave Balance Updates**
Automatically updates main sheet columns:
- **CO (Concediu)**: Total vacation days taken (working days only)
- **CM (Medical)**: Total medical leave days
- Located at columns after the month's days (dynamic based on month length)

#### 6. **Edge Case Handling**

**Monday After Holiday Weekend:**
```
If holiday ends on Friday AND weekend follows:
  → Clear Monday shift (extended weekend)
```

**Month-Start Holiday:**
```
If holiday starts on day 1:
  → Check previous month's Saturday
  → Adjust weekend allocation accordingly
```

**Resignation Mid-Month:**
```
If resignation on day 15:
  → Days 1-15: Normal processing
  → Days 16-31: All marked red
  → All future shifts cleared
```

### Holiday Sheet Format

Required columns:
1. **Nume** (Name): Employee full name
2. **Magazin** (Store): Location identifier
3. **Perioada** (Period): Format `DD*DD` (start day * end day)
4. **Tip Concediu** (Leave Type): co/cm/m/abs/dem

### Color Code Reference

| Leave Type | Code | Color | RGB Hex |
|------------|------|-------|---------|
| Vacation | co | Green | #00B050 |
| Medical | cm/m | Aqua | #00FFFF |
| Maternity | m | Pink | #FFC0CB |
| Absence | abs | Orange | #FFA500 |
| Resignation | dem | Red | #FF0000 |

### Example Scenarios

#### Scenario 1: Simple Vacation
```
Employee: Maria Pop
Period: 15*20 (6 days)
Type: co
Result:
- Days 15-20 colored green
- Weekend (18-19) excluded from count
- 4 working days deducted from vacation balance
- All shifts cleared for these days
```

#### Scenario 2: Medical Leave with Weekend
```
Employee: Ion Ionescu
Period: 10*17 (8 days)
Type: cm
Result:
- Days 10-17 colored aqua
- Weekend shifts (13-14) automatically removed
- Medical counter += 8 days
- Monday (16) shift cleared if followed weekend
```

#### Scenario 3: Resignation
```
Employee: Ana Popescu
Period: 20*20
Type: dem
Result:
- Day 20 marked as resignation date
- Days 20-31 all colored red
- Employee name highlighted red
- All remaining shifts removed
- Final working day = 19
```

## Employee List Management

The application maintains a dedicated employee registry stored as an Excel file at `arhiva/list/list.xlsx`. This file is separate from the main schedule sheet and tracks personal details, contract info, and holiday balances for every employee.

### List File Structure

Each employee row contains:

| Column | Field |
|--------|-------|
| NAME | Full name |
| HOLIDAY_PERIODS | Vacation periods taken this year (e.g. `1-5.JANUARY,10-14.MARCH`) |
| HOLIDAY_NUMBER_USED | Total vacation days used |
| HOLIDAY_NUMBER_USED_CURRENT_YEAR | Vacation days used in the current year |
| HOLIDAY_NUMBER_LEFT_LAST_YEARS | Carried-over days from previous years |
| HOLIDAY_NUMBER_LEFT_CURRENT_YEAR | Accrued days for the current year |
| SALARY | Monthly salary |
| EMPLOYMENT_DATE | Hire date (`dd.MM.yyyy`) |
| CNP | National ID number |
| JOB | Job title |
| PLACE_OF_WORK | Work location |
| GESTIUNE | Department/management unit |
| PHONE_NUMBER | Contact number |
| CI | ID card number |
| DOMICILE | Home address |
| VALABILITY | ID card expiry date |

### JSON Configuration (`arhiva/config.json`)

A small JSON file controls two shared defaults used by the list management features:

```json
{
  "salary": "4050",
  "lastUpdate": "2025-01-01"
}
```

- **`salary`** – default gross salary applied when adding a new employee and the salary field is left blank
- **`lastUpdate`** – the date used by the holiday accrual logic as the starting point for calculating earned days; updated automatically after each list update

The file is loaded from `arhiva/config.json` at runtime, falling back to the bundled `/config.json` resource if the external file is missing.

---

## Additional Commands

A dedicated **Comenzi Aditionale** (Additional Commands) screen is accessible from the main scene. It provides four actions that operate on the employee list file:

| Button | Action |
|--------|--------|
| Cauta Angajat | Open the Search Employee screen |
| Adauga Angajat | Open the Add Employee form |
| Migreaza Anul | Run the year-end holiday migration |
| Updateaza Lista | Accrue holiday days up to today |

### Search Employee

Type a name in the search box; the application normalises both the query and every entry in the list (strips accents, spaces, and hyphens) before comparing, so partial or accent-free input still finds the right row.

```
Search: "Ion Popescu"
Result: row key 42          ← internal row index in the list file
        "Niciun angajat..." ← when no match is found
```

### Add Employee

The **Adauga Angajat** form collects all required fields and writes the new employee into both the list file and the main schedule file in one step.

**Required fields:**
- Fisierul Principal (main schedule `.xlsx`)
- Nume (full name)
- CNP, CI, Domiciliu
- Data angajarii, Valabilitate fisa

**Optional fields** (fall back to `config.json` defaults when blank):
- Salariu → defaults to `salary` from `config.json`
- Functia → defaults to `"vanzator"`
- Data angajarii → defaults to today

**"Luna noua" checkbox** — when checked, a month-header row is inserted in the list before the new employee row, marking the start of a new hiring month.

**Example:**
```
Nume:            Maria Ionescu
Salariu:         (blank → uses config default)
Data angajarii:  2026-04-01
CNP:             2900401123456
Functia:         casier
Punct de lucru:  Magazin Nord
→ Employee added to list file and initialised in main schedule
```

### Holiday Balance Tracking

Every time a **Concediu (CO)** holiday period is processed from the Holidays Sheet, the application automatically updates the matching employee row in the list file:

- **HOLIDAY_NUMBER_USED** is incremented by the number of calendar days in the period
- **HOLIDAY_PERIODS** has the period appended in the format `firstDay-lastDay.MONTH`
- Days are first subtracted from **HOLIDAY_NUMBER_LEFT_LAST_YEARS**; any remainder comes from **HOLIDAY_NUMBER_LEFT_CURRENT_YEAR**
- **HOLIDAY_NUMBER_USED_CURRENT_YEAR** is updated accordingly

```
Employee: Maria Ionescu
Period:   10*17  (8 days, type: concediu)
Before:   left_last=3, left_current=12, used=0
After:    left_last=0, left_current=7,  used=8
          periods: "10-17.APRIL"
```

### List Update (Holiday Accrual)

The **Updateaza Lista** button accrues earned vacation days for every active employee from their last-update date (stored in `config.json`) up to today.

- Accrual rate: **0.055 days per calendar day** (≈ 20 days / 365)
- The effective start date is `max(lastUpdate, employmentDate)`, so newly hired employees are not over-credited
- Rows highlighted **yellow** in the list are skipped (used to flag special-status employees)
- After the update, `lastUpdate` in `config.json` is set to today

```
Employee: Ion Popescu
Employment date: 2025-01-01
Last update:     2025-12-01
Today:           2026-04-11
Days elapsed:    131
Days to add:     131 × 0.055 = 7.205
New left_current: previous + 7.205
```

### Year Migration

The **Migreaza Anul** button runs the end-of-year process (requires confirmation dialog):

1. For every employee row (skipping yellow-marked rows):
   - `HOLIDAY_NUMBER_LEFT_LAST_YEARS` = old `left_last` + old `left_current`
   - `HOLIDAY_NUMBER_LEFT_CURRENT_YEAR` = 0
   - `HOLIDAY_NUMBER_USED` = 0
   - `HOLIDAY_NUMBER_USED_CURRENT_YEAR` = 0
   - `HOLIDAY_PERIODS` cleared

This ensures unused days carry over into the next year while resetting all current-year counters.

---

## Holiday Polymorphism

The five leave types are implemented as concrete subclasses of the abstract `Holiday` class using the **Template Method** pattern. Each subclass only overrides the parts of the processing pipeline that differ.

```
Holiday  (abstract)
├── apply(row, headerRow, daysInMonth)   ← final template method
│     ├── beforeRange()   hook
│     ├── applyRange()    shared range logic
│     ├── afterRange()    hook
│     └── updateSummary() hook
│
├── ConcediuHoliday    → green, skips weekends, updates CO summary column
├── MedicalHoliday     → aqua,  does NOT skip weekends, updates CM column
├── MaternitateHoliday → pink,  skips weekends, no summary update
├── AbsentaHoliday     → orange, skips weekends, updates ABS column
└── DemisieHoliday     → red,   beforeRange colors name cell,
                                afterRange colors rest of month red
```

Creating the right subclass from a string reason code:
```java
Holiday h = Holiday.of(10, 17, "concediu", "Maria Ionescu", "Magazin Nord");
h.apply(employeeRow, headerRow, 30);
```

---

## File Structure

```
CEC/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/example/cec/
│   │   │       ├── HelloApplication.java      # Main application entry
│   │   │       ├── Launcher.java              # JAR entry point
│   │   │       ├── Employee.java              # Employee schedule model
│   │   │       ├── CellValue.java             # Cell read/write utilities
│   │   │       ├── NormalizeName.java         # Name normalisation helper
│   │   │       ├── WeekendShift.java          # Weekend shift data model
│   │   │       ├── WeekendModify.java         # Weekend shift processing
│   │   │       ├── WeekendModifyEmployee.java # Per-employee weekend writer
│   │   │       ├── WeekendInitialize.java     # Weekend sheet initialiser
│   │   │       ├── WeekendGenerate.java       # Weekend schedule generator
│   │   │       ├── HolidayModify.java         # Holiday processing orchestrator
│   │   │       ├── HolidayInitialize.java     # Holiday sheet parser
│   │   │       ├── PanamaShift.java           # Panama shift data model
│   │   │       ├── PanamaModify.java          # Panama processing logic
│   │   │       ├── ParseIndividualHours.java  # Per-employee daily shift parser
│   │   │       ├── WorkingHoursTotal.java     # Hours totalling logic
│   │   │       ├── VariableReset.java         # State reset between runs
│   │   │       ├── Placeholders.java          # Column offset constants (enum)
│   │   │       ├── holiday/
│   │   │       │   ├── Holiday.java           # Abstract holiday (template method)
│   │   │       │   ├── ConcediuHoliday.java   # Vacation leave
│   │   │       │   ├── MedicalHoliday.java    # Medical leave
│   │   │       │   ├── MaternitateHoliday.java# Maternity leave
│   │   │       │   ├── AbsentaHoliday.java    # Absence
│   │   │       │   └── DemisieHoliday.java    # Resignation
│   │   │       ├── panama/
│   │   │       │   ├── Panama.java            # Abstract Panama week model
│   │   │       │   ├── PanamaFriday.java      # Fri/Thu/Tue pattern
│   │   │       │   └── PanamaSunday.java      # Mon/Wed/Sat/Sun pattern
│   │   │       ├── list/
│   │   │       │   ├── ListConfig.java        # Singleton for list file access
│   │   │       │   ├── ListSheet.java         # Sheet index reference
│   │   │       │   ├── Person.java            # Employee data model (builder)
│   │   │       │   ├── EmployeeResult.java    # Search result: person + holiday summary
│   │   │       │   ├── EmployeeColumnList.java# Column index constants
│   │   │       │   ├── EmployeeRowList.java   # Row index constants
│   │   │       │   ├── MonthsPlaceholders.java# Month enum with row offsets
│   │   │       │   ├── Password.java          # Password-protected workbook helper
│   │   │       │   ├── SearchEmployee.java    # Name search; single word = family name (returns list)
│   │   │       │   ├── add/
│   │   │       │   │   ├── AddEmployee.java       # Add orchestrator
│   │   │       │   │   ├── AddToList.java         # Writes to list file
│   │   │       │   │   ├── AddToMain.java         # Writes to main schedule
│   │   │       │   │   ├── AddEmployeeToRow.java  # Row write interface
│   │   │       │   │   ├── FreePosition.java      # Finds first empty row
│   │   │       │   │   ├── NewMonthParser.java    # Inserts month-header row
│   │   │       │   │   └── config/
│   │   │       │   │       ├── JsonConfig.java    # Singleton for config.json
│   │   │       │   │       └── JsonFileReader.java# JSON file reader
│   │   │       │   └── update/
│   │   │       │       ├── CountUpdate.java       # Holiday accrual logic
│   │   │       │       ├── HolidayUpdate.java     # Syncs CO days to list file
│   │   │       │       ├── JsonDateUpdate.java    # Updates lastUpdate in config
│   │   │       │       └── NewYearMigrate.java    # Year-end migration
│   │   │       └── ui/
│   │   │           ├── MainScene.java         # Main processing screen
│   │   │           ├── CommandsScene.java     # Additional commands hub
│   │   │           ├── AddEmployeeScene.java  # Add employee form
│   │   │           ├── SearchEmployeeScene.java# Employee search (list-aware display)
│   │   │           ├── WeekendDetailsScene.java# Weekend schedule details
│   │   │           ├── VacanteDetailsScene.java# Holiday details view
│   │   │           ├── InstructionsScene.java # Instructions screen
│   │   │           ├── SceneController.java   # Scene navigation controller
│   │   │           ├── ColorStyle.java        # Shared button styling
│   │   │           └── validate/
│   │   │               ├── AlertUtility.java      # Alert helper
│   │   │               ├── ValidateAddEmployee.java# Add-employee form validation
│   │   │               └── ValidateDays.java      # Days-in-month validation
│   │   └── resources/
│   │       └── org/example/cec/
│   │           ├── hello-view.fxml            # FXML layout
│   │           └── icons/
│   │               └── icon.png               # Application icon
├── arhiva/                                     # Output folder (auto-created)
│   ├── list/
│   │   └── list.xlsx                          # Employee registry
│   └── config.json                            # Salary & last-update config
├── pom.xml
└── README.md
```


## Delete Progress Functionality

The 'Delete Progress' button allows users to erase every progress from the people that belong to a specific store/location in the main Excel sheet. This is useful when you want to reset the data for a particular store without affecting others.
### How It Works
1. **Select Store**: Choose the store/location from the dropdown menu.
2. **Delete Data**: Click the 'Delete Progress' button to remove all progress data for employees associated with the selected store.
3. **Data Removal**: The application will clear all progress-related entries (shifts, holidays, etc.) for the employees of that store in the main excel sheet.
4. **Confirmation**: A confirmation message will be displayed once the data has been successfully deleted.

This comes in handy when you want to 'rollback' the changes made to a specific store without affecting the data of other stores in the same file.
> **Note:** This feature is currently disabled because of already processed data that can not be 100% reversed without human intervention.

## Important Notes

### File Format Requirements

1. **All sheets must be at position 0** (first sheet in Excel file)
2. **Employee names must be consistent** across all files
3. **Date format** in period column: `DD*DD` (e.g., `5*12`)
4. **Leave type codes** are case-insensitive

### Best Practices

- **Backup Original Files**: Always keep copies before processing
- **Month Day Input**: Enter correct number of days (28-31) for accurate processing
- **Sequential Processing**: Process holidays first, then weekends for best results
- **Validation**: Check the instructions page for file format requirements
- **Archive Review**: Check `arhiva` folder for processed files before using

### Troubleshooting

**Error: "Cannot get NUMERIC value from STRING cell"**
- Ensure date/period columns contain properly formatted text or numbers
- Check for merged cells in the Excel file

**Error: "Te rog selecteaza toate fisierele necesare!"**
- You must select at least the main sheet and one other file (weekend or holidays)

**Weekend shifts not allocated correctly:**
- Verify weekend sheet has correct format
- Check that weekend days are properly colored/marked
- Ensure employee availability is marked with "X"

**Holiday not applying:**
- Check employee name matches exactly (ignoring accents/spaces)
- Verify period format is `DD*DD`
- Ensure leave type code is valid

**Additional**
- This will most probably not work on your system since you do not know the exact file structure and formats used in your organization. Please adapt accordingly by reading the Functionalities.md file and the code itself.
- The application is build for a specific use cases and excel sheets that are used in a specific organization.Those sheets might differ from the ones you have.For legal reasons I cannot share those files publicly.
- The application currently supports only `.xlsx` files (Excel 2007 and later).
- If you do want to extend the application to support your own file formats, please do not, this is strictly forbidden by the license agreement.

## License

This project is licensed under a License - see the LICENSE file for details.

---


