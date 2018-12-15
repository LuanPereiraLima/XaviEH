package ufc.br.mutant_project.executers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
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
import ufc.br.mutant_project.models.TesteProject;
import ufc.br.mutant_project.util.Util;

public class ExecuterTestsProjects extends Executer{
	
	private String path = null;
	
	public ExecuterTestsProjects() {
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
				System.setOut(new PrintStream(new FileOutputStream(new Date()+"-output-tests.txt")));
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
			list = Util.listProjects("commits.csv");
		} catch (FileNotFoundException e) {
			throw new ListProjectsNotFoundException("Nenhum arquivo de lista de url de repositório foi encontrado (Para resolver o problema, crie um arquivo 'repositories.txt' com as URLs do GIT na raiz do projeto).");
		}
		
		if(list.isEmpty() || list.size() == 1) {
			throw new NotURLsException("Não existe linhas de projetos no arquivo 'commits.csv' (Para resolver o problema, edite o arquivo, adicionando as URLs do GIT dos projetos).");
		}
		
		List<TesteProject> tsp = new ArrayList<>();
		
		List<TesteProject> projectsAnalyzed = Util.readerCsvFileTests();
				
		for(int i=0; i < list.size(); i++) {
			
			boolean passInTest = true;
			boolean passInJacoco = false;
			
			if(list.get(i).startsWith("-")) {
				System.out.println("Jumping URL: "+list.get(i)+" By signal - .");
				continue;
			}
			
			String[] linha = list.get(i).split(" ");
			String name = null;
			String commit = null;
			String url = null;
			if(linha.length > 0) {
				name = linha[0];
				if(linha.length > 1)
					url = linha[1];
					if(linha.length > 2)
							commit = linha[2];
			}
			
			System.out.println(name);
			System.out.println(url);
			System.out.println(commit);
			
			path = Util.validateAndGetNameRepository(url);
			
			if(!(path!=null && !path.trim().isEmpty())) {
				System.out.println("Incorrect GIT URL: "+list.get(i)+" (Para resolver o problema analise as URLs adicionadas no arquivo 'repositiores.txt')");
				continue;
			}
			
			System.out.println("- verificando se o projeto já foi analisado");
			

			boolean alreadyTest = false;
			for(TesteProject tp : projectsAnalyzed) {
				
				System.out.println(tp.toCSV());
				
				System.out.println(name);
				
				if(tp.getNameProject().equals(name)) {
					alreadyTest = true;
					break;
				}
			}
			
			if(alreadyTest) {
				System.out.println("-Projeto já testado.");
				continue;
			}
			

			System.out.println("--------------------------------");
			System.out.println("-Cloning Repository: "+path+" ...");
			
			try {
				Util.cloneRepository(url, path, commit);
			} catch (CloneRepositoryException e) {
				System.out.println("-Não foi possível clonar a URL GIT: "+list.get(i)+" O projeto será ignorado. (Para resolver o problema analise as URLs adicionadas no arquivo 'repositiores.txt', ou verifique a conexão com a internet)");
				e.printStackTrace();
				continue;
			}
			
			System.out.println("--Ok!");
			
			System.out.println("-Verificanndo se o projeto está passando nos testes inicialmente.");
			int result = Util.invoker(PathProject.makePathToProjectMaven(path, null), null, true);
			
			if(result!=0) {
				System.out.println("--O projeto: "+path+" está com os testes falhando, este projeto será pulado.");
				passInTest = false;
			}
			System.out.println("--OK!");
			
			if(passInTest) {
				try {
					System.out.println("-Reportando a cobertura do projeto usando o JaCoCo");
					Util.createReportJaCoCo(PathProject.makePathToProjectMaven(path, null), null);
					System.out.println("--OK!");
					passInJacoco = true;
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
			
			tsp.add(new TesteProject(passInTest, passInJacoco, name, url, commit));
			
			Util.writeCsvFileTests(tsp);
		}
		
		System.out.close();
		
		if(saveOutputInFile)	
			System.out.close();
	}
}