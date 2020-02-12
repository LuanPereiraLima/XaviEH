package ufc.br.mutant_project.processors;

import spoon.reflect.code.CtThrow;

public class ProcessorTSD extends AbstractorProcessor<CtThrow> {
	
    public void process(CtThrow element) {

    	if(getPosition() == getParameterVisitor().getPosition()) {
    		getParameterVisitor().setBeginLine(element.getPosition().getLine());
    		getParameterVisitor().setEndLine(element.getPosition().getEndLine());
    		getParameterVisitor().setBefore(element.getParent().getParent().toString());
    		element.delete();
            getParameterVisitor().setAfter(element.getParent().getParent().toString());
    	}
    	incrementPosition();
    }
    
    public String pathIdentification() {
		return "TSD";
	}
    
    public String name() {
		return "Throw Statement Deletion";
    }
}
