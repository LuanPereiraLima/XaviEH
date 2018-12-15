package ufc.br.mutant_project.executers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import edu.emory.mathcs.backport.java.util.concurrent.helpers.Utils;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.Filter;
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
import ufc.br.mutant_project.models.ResultsStatisticsLength;
import ufc.br.mutant_project.processors.ProcessorCBD;
import ufc.br.mutant_project.processors.ProcessorCBI;
import ufc.br.mutant_project.processors.ProcessorCBR;
import ufc.br.mutant_project.processors.ProcessorCRE;
import ufc.br.mutant_project.processors.ProcessorFBD;
import ufc.br.mutant_project.processors.ProcessorPTL;
import ufc.br.mutant_project.runners.AbstractRunner;
import ufc.br.mutant_project.util.Util;

public class ExecuterGeneralEstatistics extends Executer{
	
	private boolean saveOutputInFile = true;
	private int qtdCatchs = 0;
	private int qtdFinallys = 0;
	private String path = null;
	
	public ExecuterGeneralEstatistics(boolean saveInFile) {
		super(saveInFile);
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
			list = Util.listProjects("projetos.txt");
		} catch (FileNotFoundException e) {
			throw new ListProjectsNotFoundException("Nenhum arquivo de lista de url de repositório foi encontrado (Para resolver o problema, crie um arquivo 'repositories.txt' com as URLs do GIT na raiz do projeto).");
		}
		
		if(list.isEmpty()) {
			throw new NotURLsException("Não existe URLs de projetos no arquivo 'repositories.txt' (Para resolver o problema, edite o arquivo, adicionando as URLs do GIT dos projetos).");
		}
		
		AbstractRunner.listSaveMutantResultType = Util.getListSaveMutantResultTypeFromXml(PathProject.USER_REFERENCE_TO_PROJECT);
		
		List<ResultsStatisticsLength> listaRSL = new ArrayList<>();
		
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
			System.out.println("--Ok!");
			
			System.out.println("-Verificanndo se o projeto está passando nos testes inicialmente.");
			int result = 0; //Util.invoker(PathProject.makePathToProjectMaven(path), null, true);
			
			if(result!=0) {
				System.out.println("--O projeto: "+path+" está com os testes falhando, este projeto será pulado.");
				continue;
			}
			System.out.println("--OK!");
			
			System.out.println("-Fazendo uma limpeza no projeto usando o Maven Clean.");
			result = 0; //Util.invokerOthers(PathProject.makePathToProjectMaven(path), Arrays.asList("clean"), null, true);
			
			if(result!=0) {
				System.out.println("--O projeto: "+path+" está com os clean falhando, este projeto será pulado.");
				continue;
			}
			System.out.println("--OK!");
			
			
			System.out.println("-Verificando se o projeto é compatível com o SPOON para a criação de modelos.");
			
//			try {
//				if(Util.getModel(PathProject.makePathToProjectMaven(path)).getAllTypes().size() == 0){
//					System.out.println("--Este projeto não é compatível com o Spoon Model. Projeto pulado.");
//					continue;
//				}
//			}catch(Exception e) {
//				System.out.println("--Este projeto não é compatível com o Spoon Model. Projeto pulado.");
//				continue;
//			}
//			System.out.println("--OK!");
//			
			CtModel model = Util.getModel(PathProject.makePathToProjectMaven(path, submodule));
			qtdCatchs = 0;
			qtdFinallys = 0;
			
			System.out.println("-----------------");
			System.out.println("Projeto: "+path);
			int numTrys  = model.getElements(new Filter<CtTry>() {
				public boolean matches(CtTry element) {
					if(element.getCatchers()!=null) {
						qtdCatchs += element.getCatchers().size();
					}
					if(element.getFinalizer()!=null) {
						qtdFinallys += 1;
					}
					return true;
				}
			}).size();
			
			System.out.println("Número de blocos try: " + numTrys);
			
			int numeroLinhas = 0;
			int numeroClasses = 0;
			for(CtType<?> tp : model.getAllTypes()) {
				if(tp.isClass()) {
					numeroClasses++;
				}
				numeroLinhas += tp.getPosition().getEndLine();
				
			}
			System.out.println("Número de Catchs: "+qtdCatchs);
			System.out.println("Número de Finallys: "+qtdFinallys);
			System.out.println("Número de classes: "+numeroClasses);
			System.out.println("Número de linhas de código: "+numeroLinhas);
			
			ResultsStatisticsLength rsl = new ResultsStatisticsLength(path, numTrys, qtdCatchs, qtdFinallys, numeroClasses, numeroLinhas);
			
			listaRSL.add(rsl);	
		}
		
		
		Util.writeCsvFileEstatisticsLength(listaRSL);
		
		if(saveOutputInFile)	
			System.out.close();
		
		
	}
}
