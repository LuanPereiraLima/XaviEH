package ufc.br.mutant_project;

import ufc.br.mutant_project.exceptions.ConfigPropertiesNotFoundException;
import ufc.br.mutant_project.exceptions.InicializerException;
import ufc.br.mutant_project.exceptions.ListProjectsNotFoundException;
import ufc.br.mutant_project.exceptions.NotURLsException;
import ufc.br.mutant_project.executers.Execute;

public class MainStatistics {

	public static void main(String[] args) {
		//ExecuterGeneralEstatistics ex = new ExecuterGeneralEstatistics(false, false, false, false);
		Execute ex = new Execute(false, false, false, false);
		//ExecuterEstatisticsCoverageEH ex = new ExecuterEstatisticsCoverageEH(false, false, true, false);
       //ExecuterEstatisticsCoverageEHStudy2 ex = new ExecuterEstatisticsCoverageEHStudy2(false, false, true, false);

		try {
			ex.execute();
		} catch (InicializerException e) {
			e.printStackTrace();
		} catch (ListProjectsNotFoundException e) {
			e.printStackTrace();
		} catch (NotURLsException e) {
			e.printStackTrace();
		} catch (ConfigPropertiesNotFoundException e) {
			e.printStackTrace();
		}
	}
}
