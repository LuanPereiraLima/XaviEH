package ufc.br.mutant_project.executers;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.CtModel;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.Filter;
import ufc.br.mutant_project.constants.PathProject;
import ufc.br.mutant_project.exceptions.CloneRepositoryException;
import ufc.br.mutant_project.exceptions.ConfigPropertiesNotFoundException;
import ufc.br.mutant_project.exceptions.InicializerException;
import ufc.br.mutant_project.exceptions.ListProjectsNotFoundException;
import ufc.br.mutant_project.exceptions.NotURLsException;
import ufc.br.mutant_project.models.ResultsStatisticsLength;
import ufc.br.mutant_project.runners.AbstractRunner;
import ufc.br.mutant_project.util.Util;
import ufc.br.mutant_project.util.UtilWriteReader;

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
		
		inicializer();
		
		List<String> list = listProjects;
		
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
			
			if(AbstractRunner.listSavedMutantResultType!=null) {
				System.out.println("-Verificando se o projeto já foi rodado...");
				boolean projectAlreadyRunned = false;
				for(String projeto : AbstractRunner.listSavedMutantResultType.keySet()) {
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
		
		
		UtilWriteReader.writeCsvFileEstatisticsLength(listaRSL);
		
		if(saveOutputInFile)	
			System.out.close();
		
		
	}
}
