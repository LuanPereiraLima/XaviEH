package ufc.br.mutant_project.executers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Struct;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Collections;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import ufc.br.mutant_project.constants.ConfigProperties;
import ufc.br.mutant_project.constants.Configurations;
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
import ufc.br.mutant_project.processors.*;
import ufc.br.mutant_project.runners.*;
import ufc.br.mutant_project.util.Util;
import ufc.br.mutant_project.util.UtilWriteReader;

public class Execute {
	
	protected boolean saveOutputInFile = true;
	protected boolean verifyIfProjectAlreadyRun = true;
	protected boolean cloneRepository = true;
	protected boolean testProject = true;
	protected boolean testProjectSPOONCompability = true;
	protected boolean createReportJaCoCo = true;
	protected boolean deleteFilesProject = true;
	
	protected static final String VERSION_URL = "-v";
	protected static final String COMMIT_URL = "-c";
	protected static final String MODULE_URL = "-m";
	protected static final String BUILD_URL = "-p";
	protected static final String PATH_PROJECT_URL = "-pp";
	
	protected boolean isProjectMaven = true;
	protected List<String> listProjects = null;
	
	public Execute(boolean saveInFile) {
		this.saveOutputInFile = saveInFile;
	}

	public Execute(boolean saveInFile, boolean cloneRepository) {
		this.saveOutputInFile = saveInFile;
		this.cloneRepository = cloneRepository;
	}

	public Execute(boolean saveInFile, boolean cloneRepository, boolean verifyIfProjectAlreadyRun) {
		this.saveOutputInFile = saveInFile;
		this.cloneRepository = cloneRepository;
		this.verifyIfProjectAlreadyRun = verifyIfProjectAlreadyRun;
	}

	public Execute(boolean saveInFile, boolean cloneRepository, boolean verifyIfProjectAlreadyRun, boolean testProject) {
		this.saveOutputInFile = saveInFile;
		this.cloneRepository = cloneRepository;
		this.verifyIfProjectAlreadyRun = verifyIfProjectAlreadyRun;
		this.testProject = testProject;
	}

	public Execute(boolean saveInFile, boolean cloneRepository, boolean verifyIfProjectAlreadyRun, boolean testProject, boolean spoonVerify) {
		this.saveOutputInFile = saveInFile;
		this.cloneRepository = cloneRepository;
		this.verifyIfProjectAlreadyRun = verifyIfProjectAlreadyRun;
		this.testProject = testProject;
		this.testProjectSPOONCompability = spoonVerify;
	}

	public Execute(boolean saveInFile, boolean cloneRepository, boolean verifyIfProjectAlreadyRun, boolean testProject, boolean spoonVerify, boolean createReportJaCoCo) {
		this.saveOutputInFile = saveInFile;
		this.cloneRepository = cloneRepository;
		this.verifyIfProjectAlreadyRun = verifyIfProjectAlreadyRun;
		this.testProject = testProject;
		this.testProjectSPOONCompability = spoonVerify;
		this.createReportJaCoCo = createReportJaCoCo;
	}

	public Execute(boolean saveInFile, boolean cloneRepository, boolean verifyIfProjectAlreadyRun, boolean testProject, boolean spoonVerify, boolean createReportJaCoCo, boolean deleteFilesProject) {
		this.saveOutputInFile = saveInFile;
		this.cloneRepository = cloneRepository;
		this.verifyIfProjectAlreadyRun = verifyIfProjectAlreadyRun;
		this.testProject = testProject;
		this.testProjectSPOONCompability = spoonVerify;
		this.createReportJaCoCo = createReportJaCoCo;
		this.deleteFilesProject = deleteFilesProject;
	}

	/*
	 *	Inicializa todos os itens antes de inicializar o projeto
	 */
	protected void initializer() throws ConfigPropertiesNotFoundException, InicializerException, NotURLsException, ListProjectsNotFoundException{

		showName();

		Configurations.DELETE_FILES_PROJECT = this.deleteFilesProject;

		String fields = "";
		
		Properties properties = null;
		
		for(String f: ConfigProperties.fields)
			fields += f+"\n";
		
		try {
			properties = Util.getProperties();
			if(properties.getHomeMaven() == null || properties.getProjectsFile() == null || properties.getUrlMutations() == null)
				throw new ConfigPropertiesNotFoundException("Alguma propriedade do arquivo 'config.properties' necessária não foi encontrado (Para resolver o problema, adiciona as propriedades necessárias para o projeto do projeto \n[ propriedades disponíveis: \n\n"+fields+"\n\n].");

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
		
		PathProject.USER_REFERENCE_TO_PROJECT = properties.getUrlMutations();

		if(!Util.preparePathInit()) {
			throw new InicializerException("Projeto não pode ser iniciado, falha na criação da pasta 'mutations', ela é necessária para a criação dos mutantes.");
		}
		
		try {
			listProjects = Util.listProjects(properties.getProjectsFile());
		} catch (FileNotFoundException e) {
			throw new ListProjectsNotFoundException("Nenhum arquivo de lista de url de repositório foi encontrado (Para resolver o problema, crie um arquivo '.txt' com as URLs do GIT na raiz do projeto).");
		}
		
		if(listProjects.isEmpty()) {
			throw new NotURLsException("Não existe URLs de projetos no arquivo '.txt' (Para resolver o problema, edite o arquivo, adicionando as URLs do GIT dos projetos).");
		}

		AbstractRunner.listSavedMutantResultType = Util.getListSaveMutantResultTypeFromXml(PathProject.USER_REFERENCE_TO_PROJECT);
	}

	/*
	 *	retorna verdade se o projeto está com os testes passando
	 */
	protected boolean testProject(String submodule, String path){

		System.out.println("-Verificanndo se o projeto está passando nos testes inicialmente.");

		int result = 0;

		if (submodule == null)
			result = Util.invoker(PathProject.makePathToProjectMaven(path, null), null, true);
		else
			result = Util.invoker(PathProject.makePathToProjectMaven(path, null), Collections.singletonList(submodule), true);

		if (result != 0) {
			System.out.println("--O projeto: " + path + " está com os testes falhando, este projeto será pulado.");
			return false;
		}

		System.out.println("--OK!");

		System.out.println("-Fazendo uma limpeza no projeto usando o Maven Clean.");

		if (submodule == null)
			result = Util.invokerOthers(PathProject.makePathToProjectMaven(path, null), Arrays.asList("clean"), null, true);
		else
			result = Util.invokerOthers(PathProject.makePathToProjectMaven(path, null), Arrays.asList("clean"), Collections.singletonList(submodule), true);

		if (result != 0) {
			System.out.println("--O projeto: " + path + " está com os clean falhando, este projeto será pulado.");
			return false;
		}
		System.out.println("--OK!");

		return true;
	}

	protected void reportJacoco(String path, String submodule){
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

	/*
	 *	retorna verdade se o projeto é compatível com o SPOON
	 */
	protected boolean projectSPOONCompatibility(String build, String path, String submodule){
		return projectSPOONCompatibility(build, path, submodule, null);
	}

	protected boolean projectSPOONCompatibility(String build, String path, String submodule, String pathProject){
		try {
			CtModel model = null;
			if(build != null && build.equals("g")) {
				if(pathProject!=null)
					PathProject.PROJECT_PATH_FILES_DEFAULT = pathProject;

				model = Util.getModelNoMaven(PathProject.makePathToProjectMaven(path, submodule) + PathProject.PROJECT_PATH_FILES_DEFAULT);
				System.out.println("mE:"+PathProject.makePathToProjectMaven(path, submodule) + PathProject.PROJECT_PATH_FILES_DEFAULT);

				isProjectMaven = false;
			}else {
				model = Util.getModel(PathProject.makePathToProjectMaven(path, submodule));
			}
			if(model.getAllTypes().size() == 0){
				System.out.println("--Este projeto não é compatível com o Spoon Model. Projeto pulado.");
				return false;
			}
		}catch(Exception e) {
			System.out.println("--Este projeto não é compatível com o Spoon Model. Projeto pulado.");
			return false;
		}
		return true;
	}

	/*
	 *	retorna verdade se o projeto já foi rodado
	 */
	protected boolean verifyIfProjectAlreadyRun(String path){
		System.out.println("-Verificando se o projeto já foi rodado...");
		if(AbstractRunner.listSavedMutantResultType!=null) {
			boolean projectAlreadyRunned = false;
			for(String project : AbstractRunner.listSavedMutantResultType.keySet()) {
				if(path.equals(project)) {
					projectAlreadyRunned = true;
					break;
				}
			}
			System.out.println("--OK!");
			return projectAlreadyRunned;
		}
		System.out.println("--OK!");
		return false;
	}

	public void execute() throws InicializerException, ListProjectsNotFoundException, NotURLsException, ConfigPropertiesNotFoundException {

		initializer();
		
		for(int i=0; i < listProjects.size(); i++) {
			
			if(listProjects.get(i).startsWith("-")) {
				System.out.println("Jumping URL: "+listProjects.get(i)+" By signal - .");
				continue;
			}
			
			String[] linha = listProjects.get(i).split(" ");
			
			String version = getItemByUrl(linha, VERSION_URL);
			String commit = getItemByUrl(linha, COMMIT_URL);
			String submodule = getItemByUrl(linha, MODULE_URL);
			String build = getItemByUrl(linha, BUILD_URL);
			String pathProject = getItemByUrl(linha, PATH_PROJECT_URL);

			if(pathProject!=null){
				PathProject.PROJECT_PATH_FILES_DEFAULT  = pathProject;

			}

			if(build != null && build.equals("g")) {
				this.isProjectMaven = false;
			}
			
			String path = Util.validateAndGetNameRepository(linha[0]);

			if(version!=null)
				path=path+"-"+version;

			if(submodule!=null)
				path=path+"-"+submodule;

			if(verifyIfProjectAlreadyRun)
				if(verifyIfProjectAlreadyRun(path)) {
					System.out.println("-O projeto já existe, então o mesmo será pulado.");
					continue;
				}

			if(path==null || path.trim().isEmpty()) {
				System.out.println("Incorrect GIT URL: "+listProjects.get(i)+" (Para resolver o problema analise as URLs adicionadas no arquivo 'repositiores.txt')");
				continue;
			}

			if(cloneRepository) {
				System.out.println("--------------------------------");
				System.out.println("-Cloning Repository: "+path+" ...");
				try {
					Util.cloneRepository(linha[0], path, commit);
				} catch (CloneRepositoryException e) {
					System.out.println("-Não foi possível clonar a URL GIT: " + listProjects.get(i) + " O projeto será ignorado. (Para resolver o problema analise as URLs adicionadas no arquivo 'repositiores.txt', ou verifique a conexão com a internet)");
					e.printStackTrace();
					continue;
				}
				System.out.println("--Ok!");
			}

			
			System.out.println("Existe Submodulo? :"+submodule);

			if(testProject)
				if(!testProject(submodule, path))
					continue;

			if(testProjectSPOONCompability){
				System.out.println("-Verificando se o projeto é compatível com o SPOON para a criação de modelos.");
				if(!projectSPOONCompatibility(build, path, submodule, pathProject))
					continue;
				System.out.println("--OK!");
			}


			try {
				System.out.println("-Iniciando Mutações para o projeto");
				AbstractRunner abs = new Runner<CtTry>(path, submodule, isProjectMaven);
				
				System.out.println("--Iniciando Mutações CBD para o projeto");
				abs.processor(new ProcessorCBD());
				System.out.println("---OK!");
				System.out.println("--Iniciando Mutações CBI para o projeto");
				abs.processor(new ProcessorCBI());
				System.out.println("---OK!");
				System.out.println("--Iniciando Mutações CRE para o projeto");
				abs.processor(new ProcessorCRE());
				System.out.println("---OK!");
				System.out.println("--Iniciando Mutações FBD para o projeto");
				abs.processor(new ProcessorFBD());
				System.out.println("---OK!");
				System.out.println("--Iniciando Mutações PLT para o projeto");
				abs.processor(new ProcessorPTL());
				System.out.println("---OK!");

				abs = new RunnerThrow<CtThrow>(path, submodule, isProjectMaven);
				System.out.println("--Iniciando Mutações TSD para o projeto");
				abs.processor(new ProcessorTSD());
				System.out.println("---OK!");

				abs = new RunnerSubProcessCatch<CtThrow>(path, submodule, isProjectMaven);
				System.out.println("--Iniciando Mutações CBR para o projeto");
				abs.processor(new ProcessorCBR());
				System.out.println("---OK!");

			} catch (PomException e1) {
				System.out.println(e1.getMessage());
				e1.printStackTrace();
			}
			
			System.out.println("-Gerando CSV para o projeto em específico");
			UtilWriteReader.writeCsvFileByProject(path, AbstractRunner.listSavedMutantResultType);
			System.out.println("--OK!");
			
			System.out.println("-Gerando arquivo xml para se for preciso continuar depois...");
			Util.createXmlListSaveMutantResultType(PathProject.USER_REFERENCE_TO_PROJECT, AbstractRunner.listSavedMutantResultType);
			System.out.println("-OK!");

			if(this.createReportJaCoCo) {
				reportJacoco(path, submodule);
			}
		}

		System.out.println("-Gerando CSV para todos os projetos");
		UtilWriteReader.writeCsvFileByAllProjects(AbstractRunner.listSavedMutantResultType);
		System.out.println("--OK!");
		
		if(saveOutputInFile)
			System.out.close();
	}
	
	public String getItemByUrl(String[] url, String command) {
		for(int i=0; i < url.length; i++) {
			if(url[i].equals(command)) {
				if((i+1) == url.length)
					return null;
				else
					return url[i+1];
			}
		}
		return null;
	}
	
	protected void showName() {
		System.out.println("  _ __ ___  _   _| |_ __ _ _ __ | |_  | |_ ___ ___| |_    __ _  ___ _ __   ___ _ __ __ _| |_ ___  _ __ \n" + 
				" | '_ ` _ \\| | | | __/ _` | '_ \\| __| | __/ _ \\ __| __|  / _` |/ _ \\ '_ \\ / _ \\ '__/ _` | __/ _ \\| '__|\n" + 
				" | | | | | | |_| | |_ (_| | | | | |_  | |_  __\\__ \\ |_  | (_| |  __/ | | |  __/ | | (_| | |_ (_) | |   \n" + 
				" |_| |_| |_|\\__,_|\\__\\__,_|_| |_|\\__|  \\__\\___|___/\\__|  \\__, |\\___|_| |_|\\___|_|  \\__,_|\\__\\___/|_|   \n" + 
				"                                                         |___/                                         ");
	}
}
