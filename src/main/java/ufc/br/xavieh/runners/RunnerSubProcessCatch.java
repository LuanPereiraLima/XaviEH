package ufc.br.xavieh.runners;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import ufc.br.xavieh.constants.PathProject;
import ufc.br.xavieh.exceptions.PomException;
import ufc.br.xavieh.models.CHE;
import ufc.br.xavieh.models.ParameterProcessorSubProcess;
import ufc.br.xavieh.processors.AbstractorProcessor;
import ufc.br.xavieh.processors.AbstractorProcessorSubProcess;
import ufc.br.xavieh.util.Util;

public class RunnerSubProcessCatch<D extends CtElement> extends AbstractRunner<D> {
	
	public RunnerSubProcessCatch(String uriName, String subModule, boolean isMavenProject) {
		super(uriName, subModule, isMavenProject);
	}
	
	@Override
	public void processor(AbstractorProcessor<?> cp1) throws PomException {
		AbstractorProcessorSubProcess<?> cp = (AbstractorProcessorSubProcess<?>) cp1;
		
		File source = new File(PathProject.makePathToJavaCode(uriName, subModule));
		System.out.println("Gerando mutantes nas classes...");
		String[] tiposDeArquivo = new String[] { "java" };

		for (File f : FileUtils.listFiles(source, tiposDeArquivo, true)) {

			SpoonAPI spoon = new Launcher();
			spoon.getEnvironment().setNoClasspath(true);
			spoon.getEnvironment().setCommentEnabled(true);
			spoon.addInputResource(f.getAbsolutePath());

			System.out.println("Arquivo: " + f.getAbsolutePath());

			Map<Integer, CHE> map = new HashMap<Integer, CHE>();
		
			int qtd = getQtdAndMapOfTryCHEDerivedTypes(spoon, map);
			
			System.out.println("Número de ocorrencias CtTry: " + qtd);
			
			cp.setMap(map);

			for (int i = 1; i < qtd; i++) {
				if(map.containsKey(i)) {
					List<CtTypeReference<?>> listaTR = Util.getListOfDirectDerivedTypes(map.get(i));
					for(int j=1; j <= listaTR.size(); j++) {
						
						Util.clearOutputSpoonToProject();
		
						spoon = new Launcher();
						spoon.getEnvironment().setNoClasspath(true);
						spoon.getEnvironment().setCommentEnabled(true);
						spoon.getEnvironment().setPreserveLineNumbers(true);
						spoon.getEnvironment().setAutoImports(false);
						spoon.addInputResource(f.getAbsolutePath());
						spoon.setSourceOutputDirectory(PathProject.getPathTemp());
		
						ParameterProcessorSubProcess pv = new ParameterProcessorSubProcess();
						pv.setPosition(i);
						pv.setPositionProcess(j);
						cp.resetPosition();
						cp.setUriName(uriName);
						cp.resetPositionProcess();
						cp.setParameterVisitor(pv);
						cp.setSubModule(subModule);
						cp.setMavenProject(isMavenProject);
				
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
			}
		}
		
		if(numberMutant > 0)
			finalResult(cp);
		else
			System.out.println("Não foi necessário criar nenhum mutante do tipo " +cp.pathIdentification() + " para o projeto: "+uriName);
		
		resetResults();
	}
	
	private int getQtdAndMapOfTryCHEDerivedTypes(SpoonAPI spoon, final Map<Integer, CHE> map) {
		return spoon.buildModel().getElements((Filter<CtTry>) element -> {

			if(element.getCatchers().size() > 0) {

				CtClass<?> c = element.getParent(new Filter<CtClass<?>>() {
					public boolean matches(CtClass<?> element) {return true;};
				});

				if(c!=null) {

					String name = null;

					if(c.getQualifiedName().contains("$")) {
						int index = c.getQualifiedName().indexOf("$");

						if(index != -1)
							name = c.getQualifiedName().substring(0, index);

						if(name==null)
							name = c.getQualifiedName();

						System.out.println(name);
					}else
						name = c.getQualifiedName();

					Integer count = 1;
					CtType<?> tipos = Util.getClassByModel(name, PathProject.makePathToProjectMaven(uriName, subModule), isMavenProject);
					if(tipos==null){
						return true;
					}
					for(CtTry tr : tipos.getElements(new Filter<CtTry>() {
						public boolean matches(CtTry element) {
							return true;
						}
					})){
						CHE che = Util.getCHE(tr);
						if(che!=null)
							map.put(count, che);
						count++;
					}
				}

			}
			return true;
		}).size();
	}
}
