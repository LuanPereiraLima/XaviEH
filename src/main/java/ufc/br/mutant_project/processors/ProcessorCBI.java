package ufc.br.mutant_project.processors;

import java.util.List;

import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import ufc.br.mutant_project.constants.PathProject;
import ufc.br.mutant_project.models.CHE;
import ufc.br.mutant_project.util.Util;

public class ProcessorCBI extends AbstractorProcessor<CtTry>{
	
    @SuppressWarnings("unchecked")
	public void process(CtTry element) {
    	if(getPosition() == getParameterVisitor().getPosition()) {
    		getParameterVisitor().setBeginLine(element.getPosition().getLine());
    		getParameterVisitor().setEndLine(element.getPosition().getEndLine());
    		getParameterVisitor().setBefore(element.toString());
    		
    		if(element.getCatchers().size() > 0) {

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
    				CtType<?> tipos = Util.getClassByModel(nome, PathProject.makePathToProjectMaven(getUriName(), getSubModule()), isMavenProject());
        			int count = 1;
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
