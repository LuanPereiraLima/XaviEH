package ufc.br.mutant_project.processors;

import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;

public class ProcessorCRE  extends AbstractorProcessor<CtTry>{
	
    public void process(CtTry element) {
    	if(getPosition() == getParameterVisitor().getPosition()) {
    		
    		getParameterVisitor().setBeginLine(element.getPosition().getLine());
    		getParameterVisitor().setEndLine(element.getPosition().getEndLine());
    		getParameterVisitor().setBefore(element.toString());
    		
    		if(element.getCatchers()!=null && element.getCatchers().size() > 0) {
	    		for(CtCatch ct : element.getCatchers()) {
	    			CtThrow cth = getFactory().createCtThrow(ct.getParameter().getSimpleName());
	    			if(ct.getBody().getStatements().size() > 0)
	    				if(ct.getBody().getLastStatement() instanceof CtReturn<?> || ct.getBody().getLastStatement() instanceof CtThrow)
	        				ct.getBody().getLastStatement().delete();

        			ct.getBody().addStatement(cth);
	    		}
    		}else
    			getParameterVisitor().setNeedModification(false);
    		getParameterVisitor().setAfter(element.toString());
    	}
    	incrementPosition();
    }
    
    public String pathIdentification() {
		return "CRE";
	}
    
    public String name() {
		return "Catch and Rethrow Exception";
    }
}
