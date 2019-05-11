package ufc.br.mutant_project.test;

import ufc.br.mutant_project.exceptions.ConfigPropertiesNotFoundException;
import ufc.br.mutant_project.executers.ExecuteCSVFromXMLFinal;

public class CreateCSVFromXml {
    public static void main(String[] args) {
        try {
            new ExecuteCSVFromXMLFinal(false, false, false, false, false).execute();
        } catch (ConfigPropertiesNotFoundException e) {
            e.printStackTrace();
        }
    }
}
