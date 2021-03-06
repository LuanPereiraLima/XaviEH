package ufc.br.xavieh.runners;

import java.io.File;
import org.apache.commons.io.FileUtils;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;
import ufc.br.xavieh.constants.PathProject;
import ufc.br.xavieh.exceptions.PomException;
import ufc.br.xavieh.models.ParameterProcessor;
import ufc.br.xavieh.processors.AbstractorProcessor;
import ufc.br.xavieh.util.Util;

public class Runner<A extends CtElement> extends AbstractRunner<A>{
	
	public Runner(String uriName, String subModule, boolean isMavenProject) {
		super(uriName, subModule, isMavenProject);
	}

	public void processor(AbstractorProcessor<?> cp) throws PomException {

		File source = new File(PathProject.makePathToJavaCode(uriName, subModule));
		System.out.println("Gerando mutantes nas classes...: "+PathProject.makePathToJavaCode(uriName, subModule));
		String[] typeFiles = new String[] { "java" };

		for (File f : FileUtils.listFiles(source, typeFiles, true)) {

			SpoonAPI spoon = new Launcher();
			spoon.getEnvironment().setNoClasspath(true);
			spoon.getEnvironment().setCommentEnabled(true);
			spoon.addInputResource(f.getAbsolutePath());

			System.out.println("Arquivo: " + f.getAbsolutePath());

			CtModel mo = spoon.buildModel();

			int qtd = mo.getElements((Filter<CtTry>) element -> true).size();

			System.out.println("Número de ocorrencias: " + qtd);

			for (int i = 1; i <= qtd; i++) {

				Util.clearOutputSpoonToProject();

				spoon = new Launcher();
				spoon.getEnvironment().setNoClasspath(true);
				spoon.getEnvironment().setCommentEnabled(true);
				//TODO ATIVANDO AUTO IMPORTS PARA TESTE
				spoon.getEnvironment().setAutoImports(true);
				//spoon.getEnvironment().setShouldCompile(true);
				spoon.getEnvironment().setPreserveLineNumbers(true);
				spoon.addInputResource(f.getAbsolutePath());
				spoon.setSourceOutputDirectory(PathProject.getPathTemp());

				cp.setParameterVisitor(new ParameterProcessor());
				cp.getParameterVisitor().setPosition(i);
				cp.setUriName(uriName);
				cp.resetPosition();
				cp.setSubModule(subModule);
				cp.setMavenProject(isMavenProject);
				
				System.out.println("getAbsolutePath: "+f.getAbsolutePath());
				System.out.println("URI: "+uriName);
				System.out.println("Sub: "+subModule);

				spoon.addProcessor(cp);
				spoon.run();

				if (cp.getParameterVisitor().isNeedModification()) {

					System.out.println("Posicao for " + i + " Número do mutante: " + numberMutant
							+ " precisa de modificação: " + cp.getParameterVisitor().isNeedModification());

					System.out.println("Saída do arquivo modificado: \n" + cp.getParameterVisitor());
					
					saveMutant(f, cp);
				}
			}
		}
		if(numberMutant > 0)
			finalResult(cp);
		else 
			System.out.println("Não foi necessário criar nenhum mutante do tipo " +cp.pathIdentification() + " para o projeto: "+uriName);
		
		resetResults();
	}
}
