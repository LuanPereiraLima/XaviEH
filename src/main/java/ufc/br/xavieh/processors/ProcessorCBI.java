package ufc.br.xavieh.processors;

import java.util.List;

import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import ufc.br.xavieh.constants.PathProject;
import ufc.br.xavieh.models.CHE;
import ufc.br.xavieh.util.Util;

public class ProcessorCBI extends AbstractorProcessor<CtTry>{
	
    @SuppressWarnings("unchecked")
	public void process(CtTry element) {
    	if(getPosition() == getParameterVisitor().getPosition()) {
    		getParameterVisitor().setBeginLine(element.getPosition().getLine());
    		getParameterVisitor().setEndLine(element.getPosition().getEndLine());
    		getParameterVisitor().setBefore(element.toString());
    		
    		if(element.getCatchers().size() > 0) {

				System.out.println(element.getParent().getParent());

        		CtClass<?> c = element.getParent(new Filter<CtClass<?>>() {
        			public boolean matches(CtClass<?> element) {return true;};
				});
        		if(c!=null) {
        			String nome = null;
        			
        			if(c.getQualifiedName().contains("$")) {
	        			int index = c.getQualifiedName().indexOf("$");
	        			
	        			if(index != -1) {
	        				nome = c.getQualifiedName().substring(0, index);
	        			}
	        			
	        			if(nome==null)
	        				nome = c.getQualifiedName();
	        			
	        			System.out.println(nome);
        			}else {
        				nome = c.getQualifiedName();
        			}
					System.out.println("ERRO AQUI: -----------");
					System.out.println("NOME: "+ nome);
					System.out.println("PATH TO PROJECT: "+ PathProject.makePathToProjectMaven(getUriName(), getSubModule()));
					System.out.println("IS MAVEN PROJECT: "+isMavenProject());
					System.out.println("ERRO AQUI: -----------/");
    				CtType<?> tipos = Util.getClassByModel(nome, PathProject.makePathToProjectMaven(getUriName(), getSubModule()), isMavenProject());
        			int count = 1;
        			if(tipos==null){
						getParameterVisitor().setNeedModification(false);
						return;
					}
        			for(CtTry tr : tipos.getElements(new Filter<CtTry>() {
        				public boolean matches(CtTry element) {
        					return true;
        				}
					})){
        				if(count == getParameterVisitor().getPosition()) {
        					
        					CHE che = Util.getCHE(tr);
        					che.print(0, che);
        					
        					if(che.getFilhos()!=null && che.getFilhos().size() > 0) {
            					List<CtTypeReference<?>> lista = Util.getListOfCatchersToAdd(che);
            					
            					if(lista!=null && lista.size() > 0) {
            						for(CtTypeReference<?> ctr : lista) {
            							CtCatch copiado = getFactory().Core().clone(element.getCatchers().get(0));
            							String np = copiado.getParameter().getSimpleName();
            							copiado.setParameter((CtCatchVariable<? extends Throwable>) getFactory().createCatchVariable().setType((CtTypeReference<Object>) ctr));
            							copiado.getParameter().setSimpleName(np);
            							element.addCatcher(copiado);
            						}
            					}else
            						getParameterVisitor().setNeedModification(false);
            					
        					}else
        						getParameterVisitor().setNeedModification(false);
        				}
        				count++;
        			}
        		}
        		
    		}else
    			getParameterVisitor().setNeedModification(false);
    		
    		getParameterVisitor().setAfter(element.toString());
    	}
    	incrementPosition();
    }
    
    public String pathIdentification() {
		return "CBI";
	}
    
    public String name() {
		return "Catch Block Insertion";
    }
}
