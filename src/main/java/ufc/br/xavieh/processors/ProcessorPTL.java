package ufc.br.xavieh.processors;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.Filter;

public class ProcessorPTL  extends AbstractorProcessor<CtTry>{
	
    public void process(final CtTry element) {
    	if(getPosition() == getParameterVisitor().getPosition()) {
    		getParameterVisitor().setNeedModification(false);
    		getParameterVisitor().setBeginLine(element.getPosition().getLine());
    		getParameterVisitor().setEndLine(element.getPosition().getEndLine());
    		getParameterVisitor().setBefore(element.toString());
    		
    		//System.out.println("---------");
			
			final List<CtInvocation<?>> listaDeAssigments = new ArrayList<CtInvocation<?>>();
			
			element.getElements(new Filter<CtAssignment<?, ?>>() {
				public boolean matches(final CtAssignment<?, ?> assigment) {
					/*System.out.println("ASSIGMENT");
					System.out.println(assigment);
					System.out.println(assigment.getAssigned());
					System.out.println(assigment.getShortRepresentation());*/
					
					if(assigment.getParent(new Filter<CtTry>() {
						public boolean matches(CtTry element) {
							return true;
						}
					}).equals(element)) {
					
						assigment.getParent(new Filter<CtMethod<?>>() {

							public boolean matches(final CtMethod<?> method) {
								
								//System.out.println("metodo: "+method);
								
								method.getBody().getElements(new Filter<CtInvocation<?>>() {
									public boolean matches(CtInvocation<?> invocation) {
										
										//System.out.println("Metod element: "+ invocation);
										//System.out.println(invocation.getShortRepresentation());

										if(verifyParent(invocation.getParent(), method)) {
											if(invocation.getTarget()!=null && assigment.getAssigned()!=null) {
												if(invocation.getTarget().toString().equals(assigment.getAssigned().toString())) {
													listaDeAssigments.add(invocation);
													invocation.delete();
													/*System.out.println(listaDeAssigments);
													System.out.println("Opa, deu replace");
													System.out.println("metodo novamente: "+method);*/
													getParameterVisitor().setNeedModification(true);
												}
											}
										}
										return false;
									}
								});
								return false;	
							}
						});
					}
					return false;
				}
			});
			
			if(listaDeAssigments.size() > 0)
				element.getBody().addStatement(listaDeAssigments.get(0));
			
			
			//System.out.println("-------------");
			
			//System.out.println(element);


            getParameterVisitor().setAfter(element.toString());
    	}
    	incrementPosition();
    }
    
	public boolean verifyParent(CtElement cte, CtMethod<?> possivelPai) {
		if(cte!=null) {
			if(cte instanceof CtTry) {
				return false;
			}else {
				if(possivelPai.equals(cte)) {
					return true;
				}else {
					return verifyParent(cte.getParent(), possivelPai);
				}
			}
		}
		return false;
	}
    
    public String pathIdentification() {
		return "PTL";
	}
    
    public String name() {
		return "Placing Try Block Later";
    }
}
