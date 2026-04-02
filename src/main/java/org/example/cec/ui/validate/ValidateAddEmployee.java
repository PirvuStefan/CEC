package org.example.cec.ui.validate;

import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class ValidateAddEmployee {

    public static void clearFields(TextField numeField, TextField salariuField, DatePicker dataAngajariiField,
                                   TextField cnpField, TextField functiaField, TextField punctDeLucruField,
                                   TextField gestiuneField, TextField telefonField, TextField ciField,
                                   TextField domiciliuField, DatePicker valabilitateFisaField, CheckBox newMonth) {
        getFileds(numeField, salariuField, dataAngajariiField, cnpField, functiaField, punctDeLucruField, gestiuneField, telefonField, ciField, domiciliuField, valabilitateFisaField, newMonth);
    }

    public static void getFileds(TextField numeField, TextField salariuField, DatePicker dataAngajariiField, TextField cnpField, TextField functiaField, TextField punctDeLucruField, TextField gestiuneField, TextField telefonField, TextField ciField, TextField domiciliuField, DatePicker valabilitateFisaField, CheckBox newMonth) {
        numeField.clear();
        salariuField.clear();
        dataAngajariiField.setValue(null);
        cnpField.clear();
        functiaField.clear();
        punctDeLucruField.clear();
        gestiuneField.clear();
        telefonField.clear();
        ciField.clear();
        domiciliuField.clear();
        valabilitateFisaField.setValue(null);
        newMonth.setSelected(false);
    }

    public static boolean check(TextField numeField, TextField salariuField, DatePicker dataAngajariiField, TextField cnpField, TextField functiaField, TextField punctDeLucruField, TextField gestiuneField, TextField telefonField, TextField ciField, TextField domiciliuField, DatePicker valabilitateFisaField) {
        return numeField.getText().isEmpty()
                || salariuField.getText().isEmpty()
                || dataAngajariiField.getValue() == null
                || cnpField.getText().isEmpty()
                || functiaField.getText().isEmpty()
                || punctDeLucruField.getText().isEmpty()
                || gestiuneField.getText().isEmpty()
                || telefonField.getText().isEmpty()
                || ciField.getText().isEmpty()
                || domiciliuField.getText().isEmpty()
                || valabilitateFisaField.getValue() == null;
    }

}
