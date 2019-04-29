package ufc.br.mutant_project.processors;

import spoon.reflect.code.CtTry;

public class ProcessorFBD  extends AbstractorProcessor<CtTry>{
	
    public void process(CtTry element) {
    	if(getPosition() == getParameterVisitor().getPosition()) {
    		getParameterVisitor().setBeginLine(element.getPosition().getLine());
    		getParameterVisitor().setEndLine(element.getPosition().getEndLine());
    		getParameterVisitor().setBefore(element.toString());
            if(element.getFinalizer()!=null && element.getFinalizer().getStatements().size() > 0) {
            	
            	element.setFinalizer(null);
            	if(element.getCatchers()==null) {
            		element.setFinalizer(getFactory().createBlock());
            	}else if(element.getCatchers().size() == 0) {
            		element.setFinalizer(getFactory().createBlock());
            	}
            }else
            	getParameterVisitor().setNeedModification(false);

            getParameterVisitor().setAfter(element.toString());
    	}
    	incrementPosition();
    }
    
    public String pathIdentification() {
		return "FBD";
	}
    
    public String name() {
		return "Finally Block Deletion";
    }
}
