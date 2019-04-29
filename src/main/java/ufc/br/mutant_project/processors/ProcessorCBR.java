package ufc.br.mutant_project.processors;

import java.util.List;

import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtTry;
import spoon.reflect.reference.CtTypeReference;
import ufc.br.mutant_project.models.ParameterProcessorSubProcess;
import ufc.br.mutant_project.util.Util;

public class ProcessorCBR extends AbstractorProcessorSubProcess<CtTry> {
	
    @SuppressWarnings("unchecked")
	public void process(CtTry element) {
    	
    	System.out.println("process: ");
    	System.out.println("position Process CBR: "+ getPosition());
    	System.out.println("position Process parameter: "+getParameterVisitor().getPosition());
    	
    	if(getPosition() == getParameterVisitor().getPosition()) {
    		getParameterVisitor().setNeedModification(false);
    		getParameterVisitor().setBeginLine(element.getPosition().getLine());
    		getParameterVisitor().setEndLine(element.getPosition().getEndLine());
    		getParameterVisitor().setBefore(element.toString());
    		ParameterProcessorSubProcess pvsp = (ParameterProcessorSubProcess) getParameterVisitor();

    		if(getMap().containsKey(getPosition())) {
    			List<CtTypeReference<?>> lista = Util.getListOfDirectDerivedTypes(getMap().get(getPosition()));
    			
    			for(CtTypeReference<?> ctr: lista) {
    				System.out.println("Subprocess: ");
    		    	System.out.println("position Subprocess CBR: "+ getPositionProcess());
    		    	System.out.println("position Subprocess parameter: "+pvsp.getPositionProcess());
    				if(getPositionProcess() == pvsp.getPositionProcess()) {
    					if(element.getCatchers().isEmpty())
    						break;
    					
    					try {
							if(element.getCatchers().get(0).getParameter().getType().toString().equals(ctr.toString())){
								System.out.println("Elementos iguais, mutant não será gerado");
								break;
							}
						}catch(Exception e) {
							System.out.println("Elemento não pode ser obtido, pulado.");
							continue;
						}
    					
						CtCatch copy = getFactory().Core().clone(element.getCatchers().get(0));
						element.getCatchers().clear();
						String np = copy.getParameter().getSimpleName();
						copy.setParameter((CtCatchVariable<? extends Throwable>) getFactory().createCatchVariable().setType((CtTypeReference<Object>) ctr));
						copy.getParameter().setSimpleName(np);
						element.addCatcher(copy);
						getParameterVisitor().setNeedModification(true);
    					break;
    				}
    				incrementPositionProcess();
    			}
    			incrementPositionProcess();
    		}
    		getParameterVisitor().setAfter(element.toString());
    	}
    	incrementPosition();
    }
    
    public String pathIdentification() {
		return "CBR";
	}
    
    public String name() {
		return "Catch Block Replacement";
    }
}
