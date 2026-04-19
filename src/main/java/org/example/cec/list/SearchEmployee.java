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
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;

public class SearchEmployee implements CellValue {
    private int key = -1;
    private Person person;
    private String employmentDateStr = "";
    private String valabilityStr = "";

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

        boolean modified = false;
        Workbook workbook = null;

        try (FileInputStream fis = new FileInputStream(file)) {
            try {
                if (pwd != null && !pwd.isEmpty()) {
                    workbook = WorkbookFactory.create(fis, pwd);
                } else {
                    workbook = WorkbookFactory.create(fis);
                }
            } catch (EncryptedDocumentException ede) {
                // if opening from stream failed due to encryption, try file-based open with password
                if (pwd != null && !pwd.isEmpty()) {
                    workbook = WorkbookFactory.create(file, pwd);
                } else {
                    throw ede;
                }
            }

            try (Workbook wb = workbook) {
                Sheet sheet = wb.getSheetAt(ListSheet.EMPLOYEE_SEARCH.asInt());
                if (sheet == null) return;

                String normSearch = NormalizeName.set(nameSearch).replaceAll("[\\s\\-]+", "");

                for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue; // skip empty rows safely

                    String name = (row.getCell(1) != null) ? row.getCell(1).getStringCellValue() : "";
                    int keyNow = getValueInt(row, 2);
                    if (name == null || name.isEmpty()) continue;

                    String normName = NormalizeName.set(name).replaceAll("[\\s\\-]+", "");
                    if (compareName(normName, normSearch)) {
                        this.key = keyNow;
                        break;
                    }
                }

                if (this.key != -1) {
                    Sheet listSheet = wb.getSheetAt(ListSheet.EMPLOYEE_LIST.asInt());
                    if (listSheet != null) {
                        for (int i = EmployeeRowList.EMPLOYEE_START_POS; i <= listSheet.getLastRowNum(); i++) {
                            Row personRow = listSheet.getRow(i);
                            if (personRow == null) continue;
                            String rowName = getString(personRow, EmployeeColumnList.NAME);
                            if (rowName.isEmpty()) continue;
                            String normRowName = NormalizeName.set(rowName).replaceAll("[\\s\\-]+", "");
                            if (compareName(normRowName, normSearch)) {
                                this.employmentDateStr = getDateString(personRow, EmployeeColumnList.EMPLOYMENT_DATE);
                                this.valabilityStr = getDateString(personRow, EmployeeColumnList.VALABILITY);
                                this.person = new Person.PersonBuilder()
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
                                break;
                            }
                        }
                    }
                }

                // only write back if workbook was modified (keeps behavior safe)
                if (modified) {
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        wb.write(fos);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public int getKey() {
        return key;
    }

    public Person getPerson() {
        return person;
    }

    public String getEmploymentDateStr() {
        return employmentDateStr;
    }

    public String getValabilityStr() {
        return valabilityStr;
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
