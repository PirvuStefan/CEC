// java
package org.example.cec.list;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.example.cec.CellValue;
import org.example.cec.NormalizeName;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchEmployee implements CellValue {

    private final List<EmployeeResult> results = new ArrayList<>();
    private final DataFormatter dataFormatter = new DataFormatter();

    public SearchEmployee(String nameSearch) {
        ListConfig listConfig = new ListConfig();
        File file = listConfig.getFile();

        if (file == null || !file.exists() || !file.isFile()) {
            throw new IllegalStateException("Excel file not found: " + (file == null ? "null" : file.getAbsolutePath()));
        }

        String pwd = null;
        try {
            if (listConfig.password != null) {
                pwd = listConfig.password.getValue();
            }
        } catch (Exception ignored) {
            return;
        }

        Workbook workbook = null;

        try (FileInputStream fis = new FileInputStream(file)) {
            try {
                if (pwd != null && !pwd.isEmpty()) {
                    workbook = WorkbookFactory.create(fis, pwd);
                } else {
                    workbook = WorkbookFactory.create(fis);
                }
            } catch (EncryptedDocumentException ede) {
                if (pwd != null && !pwd.isEmpty()) {
                    workbook = WorkbookFactory.create(file, pwd);
                } else {
                    throw ede;
                }
            }

            try (Workbook wb = workbook) {
                String[] words = nameSearch.trim().split("\\s+");
                boolean isFamilyNameOnly = words.length == 1;

                // Build normalized-name → key map from EMPLOYEE_SEARCH sheet
                Map<String, Integer> nameToKey = new HashMap<>();
                Sheet searchSheet = wb.getSheetAt(ListSheet.EMPLOYEE_SEARCH.asInt());
                if (searchSheet != null) {
                    for (int i = 0; i <= searchSheet.getLastRowNum(); i++) {
                        Row row = searchSheet.getRow(i);
                        if (row == null) continue;
                        String name = (row.getCell(1) != null) ? row.getCell(1).getStringCellValue() : "";
                        if (name.isEmpty()) continue;
                        int key = getValueInt(row, 2);
                        String normName = NormalizeName.set(name).replaceAll("[\\s\\-]+", "");
                        nameToKey.put(normName, key);
                    }
                }

                Sheet listSheet = wb.getSheetAt(ListSheet.EMPLOYEE_LIST.asInt());
                if (listSheet == null) return;

                String normSearch = NormalizeName.set(nameSearch).replaceAll("[\\s\\-]+", "");

                for (int i = EmployeeRowList.EMPLOYEE_START_POS; i <= listSheet.getLastRowNum(); i++) {
                    Row personRow = listSheet.getRow(i);
                    if (personRow == null) continue;
                    String rowName = getString(personRow, EmployeeColumnList.NAME);
                    if (rowName.isEmpty()) continue;

                    boolean matches;
                    if (isFamilyNameOnly) {
                        matches = matchesFamilyName(rowName, nameSearch);
                    } else {
                        String normRowName = NormalizeName.set(rowName).replaceAll("[\\s\\-]+", "");
                        matches = compareName(normRowName, normSearch);
                    }

                    if (!matches) continue;

                    String normRowName = NormalizeName.set(rowName).replaceAll("[\\s\\-]+", "");
                    int key = nameToKey.getOrDefault(normRowName, -1);

                    String employmentDateStr = getDateString(personRow, EmployeeColumnList.EMPLOYMENT_DATE);
                    String valabilityStr = getDateString(personRow, EmployeeColumnList.VALABILITY);
                    String holidayPeriods = getString(personRow, EmployeeColumnList.HOLIDAY_PERIODS);
                    int holidayUsed = getValueInt(personRow, EmployeeColumnList.HOLIDAY_NUMBER_USED);
                    int holidayLeftCurrentYear = getValueInt(personRow, EmployeeColumnList.HOLIDAY_NUMBER_LEFT_CURRENT_YEAR);
                    int holidayLeftLastYears = getValueInt(personRow, EmployeeColumnList.HOLIDAY_NUMBER_LEFT_LAST_YEARS);

                    Person person = new Person.PersonBuilder()
                        .setName(rowName)
                        .setSalary(getString(personRow, EmployeeColumnList.SALARY))
                        .setEmploymentDate(getLocalDate(personRow, EmployeeColumnList.EMPLOYMENT_DATE))
                        .setCNP(getString(personRow, EmployeeColumnList.CNP))
                        .setJob(getString(personRow, EmployeeColumnList.JOB))
                        .setPhoneNumber(getString(personRow, EmployeeColumnList.PHONE_NUMBER))
                        .setCI(getString(personRow, EmployeeColumnList.CI))
                        .setGestiune(getString(personRow, EmployeeColumnList.GESTIUNE))
                        .setPlaceOfWork(getString(personRow, EmployeeColumnList.PLACE_OF_WORK))
                        .setDomicile(getString(personRow, EmployeeColumnList.DOMICILE))
                        .setValability(getLocalDate(personRow, EmployeeColumnList.VALABILITY))
                        .build();

                    results.add(new EmployeeResult(key, person, employmentDateStr, valabilityStr,
                        holidayPeriods, holidayUsed, holidayLeftCurrentYear, holidayLeftLastYears));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean matchesFamilyName(String rowName, String familyNameSearch) {
        // Family name is the first space/hyphen-delimited token of the full name
        String[] parts = rowName.trim().split("[\\s\\-]+");
        if (parts.length == 0) return false;
        String familyName = NormalizeName.set(parts[0]);
        String normSearch = NormalizeName.set(familyNameSearch);
        return familyName.equalsIgnoreCase(normSearch);
    }

    private boolean compareName(String name, String search) {
        if (name.equals(search)) return true;
        name = name.replace("-", " ");
        if (name.trim().equals(search)) return true;
        name = name.replaceAll("[()]", "");
        if (name.trim().equals(search)) return true;
        name = name.replaceAll("\\s+", " ");
        return name.trim().equalsIgnoreCase(search);
    }

    public List<EmployeeResult> getResults() {
        return results;
    }

    private String getString(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            default -> "";
        };
    }

    private String getDateString(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return "";
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return dataFormatter.formatCellValue(cell);
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        }
        return "";
    }

    private LocalDate getLocalDate(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }
        return null;
    }
}