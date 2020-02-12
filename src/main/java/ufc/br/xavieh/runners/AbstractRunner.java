package ufc.br.xavieh.runners;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.XStream;

import spoon.reflect.declaration.CtElement;
import ufc.br.xavieh.constants.Configurations;
import ufc.br.xavieh.constants.PathProject;
import ufc.br.xavieh.exceptions.PomException;
import ufc.br.xavieh.models.FinalResultSavedByProject;
import ufc.br.xavieh.models.ParameterProcessor;
import ufc.br.xavieh.models.ResultSavedByMutant;
import ufc.br.xavieh.processors.AbstractorProcessor;
import ufc.br.xavieh.util.Util;

public abstract class AbstractRunner<A extends CtElement> {

	protected int numberMutant = 0;
	protected int qtdMutantDead = 0;
	public static Map<String, List<FinalResultSavedByProject>> listSavedMutantResultType;
	
	protected String uriName;
	protected String subModule;
	protected boolean isMavenProject;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public AbstractRunner(String uriName, String subModule, boolean isMavenProject) {
		this.uriName = uriName;
		this.subModule = subModule;
		if(listSavedMutantResultType==null)
			listSavedMutantResultType = new HashMap();
		
		this.isMavenProject = isMavenProject;
	}

	public abstract void processor(AbstractorProcessor<?> myAbstractorProcessor) throws PomException;

	protected void resetResults() {
		qtdMutantDead = 0;
		numberMutant = 0;
	}
	
	public void resetListSaveMutantResultType() {
		listSavedMutantResultType.clear();
	}
	
	//SALVANDO MUTANT NA SUA DEVIDA PASTA
	protected void saveMutant(File f, AbstractorProcessor<?> myAbstractorProcessor) throws PomException {
		
		numberMutant++;
		
		String mutantPath = PathProject.makePathToProject(uriName) + myAbstractorProcessor.pathIdentification() + (File.separator) + (numberMutant);

		Util.createACopyMutantTest(mutantPath, PathProject.makePathToProjectMaven(uriName, null));
		System.out.println("Copy Mutant Test: " + mutantPath);
	
		Util.copyOutputSpoonToProject(mutantPath + (File.separator) + PathProject.makePathToPathFiles(uriName, subModule));

		System.out.println("-Verificando se o mutant: ( "+mutantPath+" ) passa ou não nos testes.");
		
		List<String> submodules = null;
		
		if(this.subModule!=null)
			submodules = Collections.singletonList(this.subModule);

		int result = 0;
		//TODO ADICIONADO TESTE COM O GRADLEW

		if(isMavenProject) {
			result = Util.invoker(mutantPath, submodules, true);
		}else{
			result = Util.invokerGradle(mutantPath, submodules, true);
		}
		//TODO ------------------------------

		System.out.println("--OK! Morto? Result: "+(result!=0));
		
		//TODO ADICIONADO PARA APAGAR OS ARQUIVOS NÃO NECESSÁRIOS (NECESSÁRIOS POR FALTA DE ESPAÇO) -----------------

		//System.out.println("MUTANT PATH: "+mutantPath);

		if(Configurations.DELETE_FILES_PROJECT) {
			Util.removeDirectoryAndCreate(mutantPath);

			//COPIAR NOVAMENTE OS ARQUIVOS IMPORTANTES CRIADOS
			Util.copyOutputSpoonToProject(mutantPath + (File.separator) + PathProject.makePathToPathFiles(uriName, subModule));
		}
		//TODO ----------------------------------------

		createResult(f.getAbsolutePath(), result, myAbstractorProcessor.getParameterVisitor(), myAbstractorProcessor.pathIdentification());
	}

	// PRINTANDO E SALVANDO O RESULTADO NO ARQUIVO
	protected void finalResult(AbstractorProcessor<?> myAbstractorProcessor) {
	
		double resultFrac = Double.parseDouble(qtdMutantDead + "") / Double.parseDouble(numberMutant + "");
		
		System.out.println("----------------");
		System.out.println("Quantidade de Mutants: " + numberMutant);
		System.out.println("Quantidade de Mutants Mortos: " + qtdMutantDead);
		System.out.println("Quantidade de Mutants Vivos: " + (numberMutant - qtdMutantDead));
		System.out.println("Fração de Mutants / Mutants Mortos: " + resultFrac);
		System.out.println("----------------");

		String pathResultFile = PathProject.makePathToProject(uriName) + myAbstractorProcessor.pathIdentification()
				+ (File.separator) + "finalResult.xml";

		FinalResultSavedByProject fr = new FinalResultSavedByProject(myAbstractorProcessor.name(), myAbstractorProcessor.pathIdentification(),
				numberMutant, numberMutant - qtdMutantDead, qtdMutantDead, resultFrac);
		
		
		if(!listSavedMutantResultType.containsKey(uriName))
			listSavedMutantResultType.put(uriName, new ArrayList<FinalResultSavedByProject>());
		
		listSavedMutantResultType.get(uriName).add(fr);
		
		XStream xstream = new XStream();

		try {
			xstream.toXML(fr, new FileOutputStream(new File(pathResultFile)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// SALVANDO O RESULTADO INDIVIDUAL PARA CADA PROJETO DE MUTANTE
	protected void createResult(String pathFile, int result, ParameterProcessor parameter, String namePath) {
		
		String pathResultFile = PathProject.makePathToProject(uriName) + namePath + (File.separator) + (numberMutant)
				+ (File.separator) + "result.xml";
		
		if(result!=0)
			qtdMutantDead++;
		
		ResultSavedByMutant rsbm = new ResultSavedByMutant(result != 0, pathFile, parameter.getBeginLine(), parameter.getEndLine(), parameter.getBefore(), parameter.getAfter());

		XStream xstream = new XStream();

		try {
			xstream.toXML(rsbm, new FileOutputStream(new File(pathResultFile)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
