package ufc.br.mutant_project;

import ufc.br.mutant_project.exceptions.ConfigPropertiesNotFoundException;
import ufc.br.mutant_project.exceptions.InicializerException;
import ufc.br.mutant_project.exceptions.ListProjectsNotFoundException;
import ufc.br.mutant_project.exceptions.NotURLsException;
import ufc.br.mutant_project.executers.ExecuterEstatisticsCoverageEH;

public class MainStatistics {

	public static void main(String[] args) {
		ExecuterEstatisticsCoverageEH ex = new ExecuterEstatisticsCoverageEH();
//		ExecuterGeneralEstatistics ex = new ExecuterGeneralEstatistics(false);
		//ExecuterCloneAndRunTestsWithJaCoCo ex = new ExecuterCloneAndRunTestsWithJaCoCo();
		//ExecuterEstatisticsOnlyTotalCoveredStatus ex = new ExecuterEstatisticsOnlyTotalCoveredStatus();
		try {
			ex.execute();
		} catch (InicializerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ListProjectsNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotURLsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigPropertiesNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
