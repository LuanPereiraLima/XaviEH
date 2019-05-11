package ufc.br.mutant_project;

import ufc.br.mutant_project.exceptions.ConfigPropertiesNotFoundException;
import ufc.br.mutant_project.exceptions.InicializerException;
import ufc.br.mutant_project.exceptions.ListProjectsNotFoundException;
import ufc.br.mutant_project.exceptions.NotURLsException;
import ufc.br.mutant_project.executers.*;

public class Main {

	private static String EXEC_TYPE_1 = "ExecuteMutationsAndCoverage";
	private static String EXEC_TYPE_2 = "ExecuterEstatisticsCoverageEH";
	private static String EXEC_TYPE_3 = "ExecuteCloneAndRunTestsWithJaCoCo";
	private static String EXEC_TYPE_4 = "ExecuterEstatisticsCoverageEH";
	private static String EXEC_TYPE_5 = "ExecuterOnlyMutant";

	private static String OUTPUT_FILE = "outputFile";
	private static String NO_CLONE_REPOSITORY = "noCloneRepository";
	private static String NO_VERIFY_PROJECT = "noVerifyProject";
	private static String NO_TEST_PROJECT = "noTestProject";
	private static String NO_SPOON_VERIFY_PROJECT = "noSPOONVerify";
	private static String NO_CREATE_JACOCO_REPORT = "noCreateJaCoCoReport";

	public static void main(String[] args){
		
		boolean runInFile = false;
		boolean cloneRepository = true;
		boolean verifyIfProjectAlreadyRun = true;
		boolean testProject = true;
		boolean spoonVerify = true;
		boolean createJaCoCoReport = true;

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
				if(args[i].equals(NO_CREATE_JACOCO_REPORT)){
					createJaCoCoReport = false;
				}
				if(args[i].equals(NO_SPOON_VERIFY_PROJECT)){
					spoonVerify = false;
				}
				if(args[i].equals(EXEC_TYPE_1)){
					System.out.println("EXEC_TYPE_1: ExecuteMutationsAndCoverage");
					ex = new Execute(runInFile, cloneRepository, verifyIfProjectAlreadyRun, testProject, spoonVerify, createJaCoCoReport);
				}else if(args[i].equals(EXEC_TYPE_2)){
					System.out.println("EXEC_TYPE_2: ExecuterEstatisticsCoverageEH");
					ex = new ExecuterEstatisticsCoverageEH(runInFile, cloneRepository, verifyIfProjectAlreadyRun, testProject);
				}else if(args[i].equals(EXEC_TYPE_3)){
					System.out.println("EXEC_TYPE_3: ExecuteCloneAndRunTestsWithJaCoCo");
					ex = new ExecuterCloneAndRunTestsWithJaCoCo(runInFile, cloneRepository, verifyIfProjectAlreadyRun, testProject);
				}else if(args[i].equals(EXEC_TYPE_4)){
					System.out.println("EXEC_TYPE_4: ExecuterEstatisticsCoverageEH");
					ex = new ExecuterEstatisticsCoverageEHStudy2(runInFile, cloneRepository, verifyIfProjectAlreadyRun, testProject);
				}else if(args[i].equals(EXEC_TYPE_5)){
					System.out.println("EXEC_TYPE_5: ExecuterOnlyMutant");
					ex = new ExecuteOnlyMutant(runInFile, cloneRepository, verifyIfProjectAlreadyRun, testProject,false);
				}
			}
		}else{
			ex = new Execute(false, true, true, true);
		}
		
		try {
			if(ex!=null)
				ex.execute();
			else
				System.err.println("Nenhuma execução escolhida");
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