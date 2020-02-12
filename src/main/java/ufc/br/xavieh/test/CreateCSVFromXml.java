package ufc.br.xavieh.test;

import ufc.br.xavieh.exceptions.ConfigPropertiesNotFoundException;
import ufc.br.xavieh.executers.ExecuteCSVFromXMLFinal;

public class CreateCSVFromXml {
    public static void main(String[] args) {
        try {
            new ExecuteCSVFromXMLFinal(false, false, false, false, false).execute();
        } catch (ConfigPropertiesNotFoundException e) {
            e.printStackTrace();
        }
    }
}
