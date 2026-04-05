package org.example.cec;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import static org.example.cec.Placeholders.DAY_OFFSET;
import static org.example.cec.ui.MainScene.daysInMonth;

public class WorkingHoursTotal implements CellValue {

    public static int get(Row row) {
        int total = 0;
        WorkingHoursTotal instance = new WorkingHoursTotal();
        for (int j = 1; j <= daysInMonth; j++) { // starting from column F (index 5)
            int i = j + DAY_OFFSET.asInt();
            if (i >= row.getLastCellNum()) break; // skip if column index is
            Cell cell = row.getCell(i);

            total += instance.getValueInt(cell);
        }
        return total;
    }
}
