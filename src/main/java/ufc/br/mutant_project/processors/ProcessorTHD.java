package ufc.br.mutant_project.processors;

import java.util.List;
import java.util.Set;

import org.apache.commons.compress.utils.Lists;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;
import ufc.br.mutant_project.models.ParameterProcessorSubProcess;

public class ProcessorTHD extends AbstractorProcessorSubProcess<CtMethod<?>> {
	
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void process(CtMethod element) {

    	if(getPosition() == getParameterVisitor().getPosition()) {
    		getParameterVisitor().setBeginLine(element.getPosition().getLine());
    		getParameterVisitor().setEndLine(element.getPosition().getEndLine());
    		getParameterVisitor().setBefore(element.toString());
            ParameterProcessorSubProcess pvsp = (ParameterProcessorSubProcess) getParameterVisitor();

            Set<CtTypeReference<Throwable>> itemsThrowns = element.getThrownTypes();

            if(itemsThrowns!=null && itemsThrowns.size() > 0){

                System.out.println("ThrownTypes do método: "+itemsThrowns);

                List<CtTypeReference<Throwable>> myList = Lists.newArrayList(itemsThrowns.iterator());

                for (CtTypeReference<Throwable> throwableCtTypeReference : myList) {
                    if(pvsp.getPositionProcess() == getPositionProcess()) {
                        itemsThrowns.remove(throwableCtTypeReference);
                        break;
                        //throwableCtTypeReference.delete();
                        //element.setThrownTypes(myList.get);
                    }else
                        incrementPositionProcess();
                }
                
            }else{
                getParameterVisitor().setNeedModification(false);
            }

            getParameterVisitor().setAfter(element.toString());
    	}
    	incrementPosition();
    }
    
    public String pathIdentification() {
		return "THD";
	}
    
    public String name() {
		return "Throws Deletion";
    }
}
