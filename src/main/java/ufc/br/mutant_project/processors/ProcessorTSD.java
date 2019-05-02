package ufc.br.mutant_project.processors;

import org.apache.commons.compress.utils.Lists;
import spoon.reflect.code.CtThrow;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import ufc.br.mutant_project.models.ParameterProcessorSubProcess;

import java.util.List;
import java.util.Set;

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
