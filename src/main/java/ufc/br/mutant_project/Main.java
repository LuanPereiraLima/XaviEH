package ufc.br.mutant_project;

import ufc.br.mutant_project.exceptions.ConfigPropertiesNotFoundException;
import ufc.br.mutant_project.exceptions.InicializerException;
import ufc.br.mutant_project.exceptions.ListProjectsNotFoundException;
import ufc.br.mutant_project.exceptions.NotURLsException;
import ufc.br.mutant_project.executers.Executer;
import ufc.br.mutant_project.executers.ExecuterEstatisticsOnlyTotalCoveredStatus;

public class Main {
	public static void main(String[] args){
		
		Executer ex = new ExecuterEstatisticsOnlyTotalCoveredStatus();
		//Executer ex = new ExecuterTestsProjects();
		
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