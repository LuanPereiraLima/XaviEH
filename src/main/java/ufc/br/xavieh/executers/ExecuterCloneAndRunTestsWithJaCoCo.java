package ufc.br.xavieh.executers;

import java.util.List;

import ufc.br.xavieh.exceptions.CloneRepositoryException;
import ufc.br.xavieh.exceptions.ConfigPropertiesNotFoundException;
import ufc.br.xavieh.exceptions.InicializerException;
import ufc.br.xavieh.exceptions.ListProjectsNotFoundException;
import ufc.br.xavieh.exceptions.NotURLsException;
import ufc.br.xavieh.util.Util;

public class ExecuterCloneAndRunTestsWithJaCoCo extends Execute {
	
	private String path = null;
	
	public ExecuterCloneAndRunTestsWithJaCoCo() {
		super(false);
	}

	public ExecuterCloneAndRunTestsWithJaCoCo(boolean saveInFile, boolean cloneRepository, boolean verifyIfProjectAlreadyRun, boolean testProject) {
		super(saveInFile, cloneRepository, verifyIfProjectAlreadyRun, testProject);
	}
	
	public void execute() throws InicializerException, ListProjectsNotFoundException, NotURLsException, ConfigPropertiesNotFoundException {
		
		initializer();
		
		List<String> list = listProjects;
		
		for(int i=0; i < list.size(); i++) {
			
			if(list.get(i).startsWith("-")) {
				System.out.println("Jumping URL: "+list.get(i)+" By signal - .");
				continue;
			}

			String[] linha = list.get(i).split(" ");
			
			String version = getItemByUrl(linha, VERSION_URL);
			String commit = getItemByUrl(linha, COMMIT_URL);
			String submodule = getItemByUrl(linha, MODULE_URL);
			String build = getItemByUrl(linha, BUILD_URL);
			String pathProject = getItemByUrl(linha, PATH_PROJECT_URL);

			if(build != null && build.equals("g"))
				this.isProjectMaven = false;

			path = Util.validateAndGetNameRepository(linha[0]);
			
			if(version!=null)
				path=path+"-"+version;

			if(verifyIfProjectAlreadyRun) {
				if(verifyIfProjectAlreadyRun(path))
					continue;
			}
			
			if(!(path!=null && !path.trim().isEmpty())) {
				System.out.println("Incorrect GIT URL: "+list.get(i)+" (Para resolver o problema analise as URLs adicionadas no arquivo 'repositiores.txt')");
				continue;
			}

			System.out.println("--------------------------------");
			System.out.println("-Cloning Repository: "+path+" ...");


			if(cloneRepository) {
				try {
					Util.cloneRepository(linha[0], path, commit);
				} catch (CloneRepositoryException e) {
					System.out.println("-Não foi possível clonar a URL GIT: " + list.get(i) + " O projeto será ignorado. (Para resolver o problema analise as URLs adicionadas no arquivo 'repositiores.txt', ou verifique a conexão com a internet)");
					e.printStackTrace();
					continue;
				}
			}

			if(createReportJaCoCo){
				reportJacoco(path, submodule);
			}
		}

		if(saveOutputInFile)
			System.out.close();
	}
}