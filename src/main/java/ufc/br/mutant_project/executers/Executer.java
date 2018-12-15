package ufc.br.mutant_project.executers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Collections;
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
import ufc.br.mutant_project.processors.ProcessorCBD;
import ufc.br.mutant_project.processors.ProcessorCBI;
import ufc.br.mutant_project.processors.ProcessorCBR;
import ufc.br.mutant_project.processors.ProcessorCRE;
import ufc.br.mutant_project.processors.ProcessorFBD;
import ufc.br.mutant_project.processors.ProcessorPTL;
import ufc.br.mutant_project.runners.AbstractRunner;
import ufc.br.mutant_project.runners.Runner;
import ufc.br.mutant_project.runners.RunnerSubProcessCatch;
import ufc.br.mutant_project.util.Util;

public class Executer {
	
	protected boolean saveOutputInFile = true;
	public static final String VERSION_URL = "-v";
	public static final String COMMIT_URL = "-c";
	public static final String	MODULE_URL = "-m";
	
	public Executer(boolean saveInFile) {
		this.saveOutputInFile = saveInFile;
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
			list = Util.listProjects("repositoriesNew.txt");
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
			
			String path = Util.validateAndGetNameRepository(linha[0]);
			
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
			System.out.println("--Ok!");
			
			System.out.println("-Verificanndo se o projeto está passando nos testes inicialmente.");
			int result = 0;
			
			if(submodule == null)
				result = Util.invoker(PathProject.makePathToProjectMaven(path, null), null, true);
			else
				result = Util.invoker(PathProject.makePathToProjectMaven(path, null), Collections.singletonList(submodule), true);
			
			
			if(result!=0) {
				System.out.println("--O projeto: "+path+" está com os testes falhando, este projeto será pulado.");
				continue;
			}
			System.out.println("--OK!");
			
			System.out.println("-Fazendo uma limpeza no projeto usando o Maven Clean.");
			
			if(submodule == null)
				result = Util.invokerOthers(PathProject.makePathToProjectMaven(path, null), Arrays.asList("clean"), null, true);
			else
				result = Util.invokerOthers(PathProject.makePathToProjectMaven(path, null), Arrays.asList("clean"), Collections.singletonList(submodule), true);
			
			if(result!=0) {
				System.out.println("--O projeto: "+path+" está com os clean falhando, este projeto será pulado.");
				continue;
			}
			System.out.println("--OK!");
			
			
			System.out.println("-Verificando se o projeto é compatível com o SPOON para a criação de modelos.");
			
			try {
				if(Util.getModel(PathProject.makePathToProjectMaven(path, submodule)).getAllTypes().size() == 0){
					System.out.println("--Este projeto não é compatível com o Spoon Model. Projeto pulado.");
					continue;
				}
			}catch(Exception e) {
				System.out.println("--Este projeto não é compatível com o Spoon Model. Projeto pulado.");
				continue;
			}
			
			System.out.println("--OK!");
			
			try {
				System.out.println("-Iniciando Mutações para o projeto");
				AbstractRunner abs = new Runner(path, submodule);
				
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
				
				abs = new RunnerSubProcessCatch(path, submodule);
				System.out.println("--Iniciando Mutações CBR para o projeto");
				abs.processor(new ProcessorCBR());
				System.out.println("---OK!");
			} catch (PomException e1) {
				System.out.println(e1.getMessage());
				e1.printStackTrace();
			}
			
			System.out.println("-Gerando CSV para o projeto em específico");
			Util.writeCsvFileByProject(path, AbstractRunner.listSaveMutantResultType);
			System.out.println("--OK!");
			
			System.out.println("-Gerando arquivo xml para se for preciso continuar depois...");
			Util.createXmlListSaveMutantResultType(PathProject.USER_REFERENCE_TO_PROJECT, AbstractRunner.listSaveMutantResultType);
			System.out.println("-OK!");
			
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

		System.out.println("-Gerando CSV para todos os projetos");
		Util.writeCsvFileByAllProjects(AbstractRunner.listSaveMutantResultType);
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

	public void setSaveOutputInFile(boolean saveOutputInFile) {
		this.saveOutputInFile = saveOutputInFile;
	}
}
