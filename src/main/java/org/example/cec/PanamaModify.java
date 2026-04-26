package org.example.cec;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.example.cec.panama.PanamaFriday;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import static org.example.cec.NormalizeName.set;

public class PanamaModify {

    private File mainSheet, panamaSheet;

    public PanamaModify(File mainSheet, File panamaSheet){
        this.mainSheet = mainSheet;
        this.panamaSheet = panamaSheet;

    }

    public File launch(){

            PanamaInitialise panamaInitialise = new PanamaInitialise(panamaSheet);

            Map<String, PanamaShift> panamaShifts = panamaInitialise.InitialisePanamaShifts();

            try (FileInputStream fis = new FileInputStream(mainSheet);
                 Workbook workbook = WorkbookFactory.create(fis)) {

                Sheet sheet = workbook.getSheetAt(0);

                for(int i = 0 ; i <= sheet.getLastRowNum(); i++){
                    Row row = sheet.getRow(i);
                    if(row == null) break;
                    String name = (row.getCell(2) != null) ? row.getCell(2).getStringCellValue() : "";
                    if (name == null || name.isEmpty()) break;
                    name = name.trim().toUpperCase();
                    if (name.isEmpty()) break;

                    assert panamaShifts != null;
                    if(panamaShifts.containsKey(name) || panamaShifts.containsKey(set(name))) {
                        PanamaShift shift = panamaShifts.get(name);
                        shift.setShift(row);


                        // Apply the Panama shifts to the main sheet as needed
                        // This is where you would implement the logic to modify the main sheet based on the Panama shifts
                    }
                }

                try (FileOutputStream fos = new FileOutputStream(mainSheet)) {
                    workbook.write(fos);
                }

                System.out.println("Main sheet updated with Panama shifts successfully!");

            } catch (IOException e) { // Added InvalidFormatException
                e.printStackTrace();
            }

            return mainSheet;
        }
}
