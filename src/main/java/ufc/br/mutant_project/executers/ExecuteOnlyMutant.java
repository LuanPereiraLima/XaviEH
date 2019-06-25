package ufc.br.mutant_project.executers;

import spoon.reflect.code.CtThrow;
import ufc.br.mutant_project.constants.PathProject;
import ufc.br.mutant_project.exceptions.CloneRepositoryException;
import ufc.br.mutant_project.exceptions.ConfigPropertiesNotFoundException;
import ufc.br.mutant_project.exceptions.InicializerException;
import ufc.br.mutant_project.exceptions.ListProjectsNotFoundException;
import ufc.br.mutant_project.exceptions.NotURLsException;
import ufc.br.mutant_project.exceptions.PomException;
import ufc.br.mutant_project.processors.ProcessorTSD;
import ufc.br.mutant_project.runners.AbstractRunner;
import ufc.br.mutant_project.runners.RunnerThrow;
import ufc.br.mutant_project.util.Util;
import ufc.br.mutant_project.util.UtilWriteReader;

public class ExecuteOnlyMutant extends Execute{

	public ExecuteOnlyMutant(boolean saveInFile) {
		super(saveInFile);
	}

	public ExecuteOnlyMutant(boolean saveInFile, boolean cloneRepository, boolean verifyIfProjectAlreadyRun, boolean testProject, boolean spoonVerify) {
		super(saveInFile, cloneRepository, verifyIfProjectAlreadyRun, testProject, spoonVerify);
	}

	public void execute() throws InicializerException, ListProjectsNotFoundException, NotURLsException, ConfigPropertiesNotFoundException {

		initializer();
		
		for(int i=0; i < listProjects.size(); i++) {

			if (listProjects.get(i).startsWith("-")) {
				System.out.println("Jumping URL: " + listProjects.get(i) + " By signal - .");
				continue;
			}

			String[] linha = listProjects.get(i).split(" ");

			String version = getItemByUrl(linha, VERSION_URL);
			String commit = getItemByUrl(linha, COMMIT_URL);
			String submodule = getItemByUrl(linha, MODULE_URL);
			String build = getItemByUrl(linha, BUILD_URL);
			String pathProject = getItemByUrl(linha, PATH_PROJECT_URL);

			if (pathProject != null) {
				PathProject.PROJECT_PATH_FILES_DEFAULT = pathProject;
			}

			String path = Util.validateAndGetNameRepository(linha[0]);

			if (version != null)
				path = path + "-" + version;

			if (verifyIfProjectAlreadyRun)
				if (verifyIfProjectAlreadyRun(path)) {
					System.out.println("-O projeto já existe, então o mesmo será pulado.");
					continue;
				}

			if (path == null || path.trim().isEmpty()) {
				System.out.println("Incorrect GIT URL: " + listProjects.get(i) + " (Para resolver o problema analise as URLs adicionadas no arquivo 'repositiores.txt')");
				continue;
			}

			if (cloneRepository) {
				System.out.println("--------------------------------");
				System.out.println("-Cloning Repository: " + path + " ...");
				try {
					Util.cloneRepository(linha[0], path, commit);
				} catch (CloneRepositoryException e) {
					System.out.println("-Não foi possível clonar a URL GIT: " + listProjects.get(i) + " O projeto será ignorado. (Para resolver o problema analise as URLs adicionadas no arquivo 'repositiores.txt', ou verifique a conexão com a internet)");
					e.printStackTrace();
					continue;
				}
				System.out.println("--Ok!");
			}


			System.out.println("Existe Submodulo? :" + submodule);

			if (testProject)
				if (!testProject(submodule, path))
					continue;

			if (testProjectSPOONCompability) {
				System.out.println("-Verificando se o projeto é compatível com o SPOON para a criação de modelos.");
				if (!projectSPOONCompatibility(build, path, submodule, pathProject))
					continue;
				System.out.println("--OK!");
			}

			try {
				AbstractRunner<?> abs = new RunnerThrow<CtThrow>(path, submodule, isProjectMaven);
				System.out.println("--Iniciando Mutações TSD para o projeto");
				abs.processor(new ProcessorTSD());
				System.out.println("---OK!");
			} catch (PomException e1) {
				System.out.println(e1.getMessage());
				e1.printStackTrace();
			}

			System.out.println("-Gerando CSV para o projeto em específico");
			UtilWriteReader.writeCsvFileByProject(path, AbstractRunner.listSavedMutantResultType);
			System.out.println("--OK!");
		}

		if(saveOutputInFile)
			System.out.close();
	}
}
