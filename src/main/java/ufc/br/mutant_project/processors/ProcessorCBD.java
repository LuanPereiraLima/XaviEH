package ufc.br.mutant_project.processors;

import spoon.reflect.code.CtTry;

public class ProcessorCBD  extends AbstractorProcessor<CtTry>{
	
    public void process(CtTry element) {
    	if(getPosition() == getParameterVisitor().getPosition()) {
    		getParameterVisitor().setBeginLine(element.getPosition().getLine());
    		getParameterVisitor().setEndLine(element.getPosition().getEndLine());
    		getParameterVisitor().setBefore(element.toString());
    		
    		if(element.getCatchers()!=null && element.getCatchers().size() > 0) {
	            element.getCatchers().clear();
	            if(element.getFinalizer()==null)
	            	element.setFinalizer(getFactory().createBlock());
    		}else
    			getParameterVisitor().setNeedModification(false);

            getParameterVisitor().setAfter(element.toString());
    	}
    	incrementPosition();
    }
    
    public String pathIdentification() {
		return "CBD";
	}
    
    public String name() {
		return "Catch Block Deletion";
    }
}
