package ufc.br.mutant_project.executers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import ufc.br.mutant_project.constants.ConfigPropierties;
import ufc.br.mutant_project.constants.PathProject;
import ufc.br.mutant_project.exceptions.CloneRepositoryException;
import ufc.br.mutant_project.exceptions.ConfigPropertiesNotFoundException;
import ufc.br.mutant_project.exceptions.InicializerException;
import ufc.br.mutant_project.exceptions.JacocoException;
import ufc.br.mutant_project.exceptions.ListProjectsNotFoundException;
import ufc.br.mutant_project.exceptions.NotURLsException;
import ufc.br.mutant_project.exceptions.PomException;
import ufc.br.mutant_project.exceptions.TestFailMavenInvokerException;
import ufc.br.mutant_project.models.Properties;
import ufc.br.mutant_project.runners.AbstractRunner;
import ufc.br.mutant_project.util.Util;

public class ExecuterCloneAndRunTestsWithJaCoCo extends Executer{
	
	public static final String VERSION_URL = "-v";
	public static final String COMMIT_URL = "-c";
	public static final String	MODULE_URL = "-m";
	private String path = null;
	
	public ExecuterCloneAndRunTestsWithJaCoCo() {
		super(false);
	}
	
	public void execute() throws InicializerException, ListProjectsNotFoundException, NotURLsException, ConfigPropertiesNotFoundException {
		String fields = "";
		for(String f: ConfigPropierties.fields)
			fields += f+"\n";
		try {
			Properties p = Util.getProperties();
			if(p.getHomeMaven()==null) {
				throw new ConfigPropertiesNotFoundException("Alguma propriedade do arquivo 'config.propierties' necessária não foi encontrado (Para resolver o problema, adiciona as propriedades necessárias para o projeto do projeto \n[ propriedades disponíveis: \n"+fields+" ]).");
			}
		} catch (IOException e1) {
			throw new ConfigPropertiesNotFoundException("Nenhum arquivo de propriedades foi encontrado (Para resolver o problema, crie um arquivo 'config.properties' com as propriedades necessárias para o projeto do projeto \n[ propriedades dispníveis: \n"+fields+" ]).");
		}
		
		if(this.saveOutputInFile) {
			try {
				System.setOut(new PrintStream(new FileOutputStream(new Date()+"-output.txt")));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		System.out.println("  _ __ ___  _   _| |_ __ _ _ __ | |_  | |_ ___ ___| |_    __ _  ___ _ __   ___ _ __ __ _| |_ ___  _ __ \n" + 
				" | '_ ` _ \\| | | | __/ _` | '_ \\| __| | __/ _ \\ __| __|  / _` |/ _ \\ '_ \\ / _ \\ '__/ _` | __/ _ \\| '__|\n" + 
				" | | | | | | |_| | |_ (_| | | | | |_  | |_  __\\__ \\ |_  | (_| |  __/ | | |  __/ | | (_| | |_ (_) | |   \n" + 
				" |_| |_| |_|\\__,_|\\__\\__,_|_| |_|\\__|  \\__\\___|___/\\__|  \\__, |\\___|_| |_|\\___|_|  \\__,_|\\__\\___/|_|   \n" + 
				"                                                         |___/                                         ");
		
		if(!Util.preparePathInit()) {
			throw new InicializerException("Projeto não pode ser iniciado, falha na criação da pasta 'mutations', ela é necessária para a criação dos mutantes.");
		}
		
		List<String> list = null;
		try {
			list = Util.listProjects("projetos.txt");
		} catch (FileNotFoundException e) {
			throw new ListProjectsNotFoundException("Nenhum arquivo de lista de url de repositório foi encontrado (Para resolver o problema, crie um arquivo 'repositories.txt' com as URLs do GIT na raiz do projeto).");
		}
		
		if(list.isEmpty()) {
			throw new NotURLsException("Não existe URLs de projetos no arquivo 'repositories.txt' (Para resolver o problema, edite o arquivo, adicionando as URLs do GIT dos projetos).");
		}
		
		AbstractRunner.listSaveMutantResultType = Util.getListSaveMutantResultTypeFromXml(PathProject.USER_REFERENCE_TO_PROJECT);
		
		for(int i=0; i < list.size(); i++) {
			
			if(list.get(i).startsWith("-")) {
				System.out.println("Jumping URL: "+list.get(i)+" By signal - .");
				continue;
			}
			
			String[] linha = list.get(i).split(" ");
			
			String version = getItemByUrl(linha, VERSION_URL);
			String commit = getItemByUrl(linha, COMMIT_URL);
			String submodule = getItemByUrl(linha, MODULE_URL);
			
			path = Util.validateAndGetNameRepository(linha[0]);
			
			if(version!=null)
				path=path+"-"+version;
			
			if(AbstractRunner.listSaveMutantResultType!=null) {
				System.out.println("-Verificando se o projeto já foi rodado...");
				boolean projectAlreadyRunned = false;
				for(String projeto : AbstractRunner.listSaveMutantResultType.keySet()) {
					if(path.equals(projeto)) {
						projectAlreadyRunned = true;
						break;
					}
				}
				System.out.println("--OK!");
				if(projectAlreadyRunned) {
					System.out.println("-O projeto "+path+" já possui resultados já rodados, o mesmo será pulado...");
					continue;
				}
			}
			
			if(!(path!=null && !path.trim().isEmpty())) {
				System.out.println("Incorrect GIT URL: "+list.get(i)+" (Para resolver o problema analise as URLs adicionadas no arquivo 'repositiores.txt')");
				continue;
			}

			System.out.println("--------------------------------");
			System.out.println("-Cloning Repository: "+path+" ...");
			
			
			try {
				Util.cloneRepository(linha[0], path, commit);
			} catch (CloneRepositoryException e) {
				System.out.println("-Não foi possível clonar a URL GIT: "+list.get(i)+" O projeto será ignorado. (Para resolver o problema analise as URLs adicionadas no arquivo 'repositiores.txt', ou verifique a conexão com a internet)");
				e.printStackTrace();
				continue;
			}
			
			
			try {
				System.out.println("-Reportando a cobertura do projeto usando o JaCoCo");
				Util.createReportJaCoCo(PathProject.makePathToProjectMaven(path, null), submodule);
				System.out.println("--OK!");
			} catch (PomException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			} catch (JacocoException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			} catch (TestFailMavenInvokerException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
		System.out.close();
				
		if(saveOutputInFile)	
			System.out.close();
	}
}