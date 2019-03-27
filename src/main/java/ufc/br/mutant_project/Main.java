package ufc.br.mutant_project;

import ufc.br.mutant_project.exceptions.ConfigPropertiesNotFoundException;
import ufc.br.mutant_project.exceptions.InicializerException;
import ufc.br.mutant_project.exceptions.ListProjectsNotFoundException;
import ufc.br.mutant_project.exceptions.NotURLsException;
import ufc.br.mutant_project.executers.Execute;
import ufc.br.mutant_project.executers.ExecuterCloneAndRunTestsWithJaCoCo;
import ufc.br.mutant_project.executers.ExecuterEstatisticsCoverageEH;
import ufc.br.mutant_project.executers.ExecuterEstatisticsCoverageEHStudy2;

public class Main {

	private static String EXEC_TYPE_1 = "ExecuteMutationsAndCoverage";
	private static String EXEC_TYPE_2 = "ExecuterEstatisticsCoverageEH";
	private static String EXEC_TYPE_3 = "ExecuteCloneAndRunTestsWithJaCoCo";
	private static String EXEC_TYPE_4 = "ExecuterEstatisticsCoverageEH";

	private static String OUTPUT_FILE = "outputFile";
	private static String NO_CLONE_REPOSITORY = "noCloneRepository";
	private static String NO_VERIFY_PROJECT = "noVerifyProject";
	private static String NO_TEST_PROJECT = "noTestProject";


	public static void main(String[] args){
		
		boolean runInFile = false;
		boolean cloneRepository = true;
		boolean verifyIfProjectAlreadyRun = true;
		boolean testProject = true;

		Execute ex = null;

		if(args.length > 0) {
			for(int i=0; i < args.length; i++){
				if(args[i].equals(OUTPUT_FILE)){
					runInFile = true;
				}
				if(args[i].equals(NO_CLONE_REPOSITORY)){
					cloneRepository = false;
				}
				if(args[i].equals(NO_VERIFY_PROJECT)){
					verifyIfProjectAlreadyRun = false;
				}
				if(args[i].equals(NO_TEST_PROJECT)){
					testProject = false;
				}
				if(args[i].equals(EXEC_TYPE_1)){
					ex = new Execute(runInFile, cloneRepository, verifyIfProjectAlreadyRun, testProject);
				}else if(args[i].equals(EXEC_TYPE_2)){
					ex = new ExecuterEstatisticsCoverageEH(runInFile, cloneRepository, verifyIfProjectAlreadyRun, testProject);
				}else if(args[i].equals(EXEC_TYPE_3)){
					ex = new ExecuterCloneAndRunTestsWithJaCoCo(runInFile, cloneRepository, verifyIfProjectAlreadyRun, testProject);
				}else if(args[i].equals(EXEC_TYPE_4)){
					ex = new ExecuterEstatisticsCoverageEHStudy2(runInFile, cloneRepository, verifyIfProjectAlreadyRun, testProject);
				}else{
					ex = new Execute(runInFile, cloneRepository, verifyIfProjectAlreadyRun, testProject);
				}
			}
		}else{
			ex = new Execute(false, true, true, true);
		}
		
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