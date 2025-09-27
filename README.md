# Central Excel Controller (CEC)

A JavaFX desktop application for processing and modifying Excel sheets related to employee shifts, weekends, and holidays.

## Features

- Select and process three types of Excel files:
  - Main sheet
  - Weekend shifts
  - Holidays/vacations
- Automatically allocates weekend shifts based on employee history and requirements.
- Marks holidays, medical leave, maternity, absence, and resignation with distinct colors.
- Handles weekends and holidays, ensuring correct allocation and removal of shifts.
- User-friendly interface with instructions and details for each functionality.
- Modified files are saved in the `arhiva` folder for easy access.

## Requirements

- Java 17+ (JDK)
- JavaFX SDK (download from [OpenJFX](https://openjfx.io/))
- Maven (for building)
- Excel files in `.xlsx` format

## Setup

1. Clone the repository
  ```
  git clone https://github.com/PirvuStefan/CEC.git
  ```
3. Install dependencies:
   - JavaFX: Add to your `pom.xml` as shown in the project files.
   - Apache POI: Already included for Excel processing.
