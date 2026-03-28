package org.example.cec;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import static org.example.cec.HelloApplication.daysInMonth;
import static org.example.cec.Placeholders.DAY_OFFSET;

public interface WorkingHoursTotal {

    static int get(Row row) {
        int total = 0;
        for (int j = 1; j <= daysInMonth; j++) { // starting from column F (index 5)
            int i = j + DAY_OFFSET.asInt();
            if (i >= row.getLastCellNum()) break; // skip if column index is
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                total += (int) cell.getNumericCellValue();
                continue;
            }


            try {
                assert cell != null;
                total += Integer.parseInt( cell.getStringCellValue() );
            } catch (NumberFormatException e) {
                // do nothing
            }

        }
        return total;
    }
}
