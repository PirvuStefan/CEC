# Central Excel Controller (CEC)

A JavaFX desktop application designed to automate and streamline the management of employee shifts, weekend schedules, and vacation/holiday tracking through Excel file processing.
Please read the Functionalities.md for a more in depth explanation of the code and algorithms used in this project.

## Project Overview

The Central Excel Controller (CEC) is a powerful tool created to help managers and HR departments efficiently handle employee scheduling across multiple stores/locations. The application processes Excel files containing employee information and automatically:

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
   - **Main Sheet**: Contains employee roster and daily schedules
   - **Weekend Sheet**: Contains weekend shift requirements and employee availability
   - **Holidays Sheet**: Contains vacation requests and leave information
4. Click "Proceseaza Datele" (Process Data)
5. Find your processed files in the `arhiva` folder

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

## File Structure

```
CEC/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/example/cec/
│   │   │       ├── HelloApplication.java    # Main application
│   │   │       ├── Employee.java            # Employee model
│   │   │       ├── Holiday.java             # Holiday model
│   │   │       └── WeekendShift.java        # Weekend shift model
│   │   └── resources/
│   └── test/
├── arhiva/                                   # Output folder (auto-created)
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


