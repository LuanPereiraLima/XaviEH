package ufc.br.mutant_project.test;

import java.util.ArrayList;
import java.util.List;

import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.Filter;

public class Teste {
	
	public static void main(String[] args) throws Exception {
		final SpoonAPI sp = new Launcher();
		sp.addInputResource("src/main/java");
		
		sp.buildModel();
		
		for(CtElement e : sp.getModel().getElements(new Filter<CtElement>() {
			public boolean matches(CtElement element) {
				if(element instanceof CtClass) {
					@SuppressWarnings("rawtypes")
					CtClass ct = (CtClass)element;
					if(ct.getSimpleName().equals("A")) {
						return true;
					}
				}
				return false;
			}
		})) {
				for(final CtTry ct : e.getElements(new Filter<CtTry>() {
					public boolean matches(CtTry element) {
						return true;
					}
				})) {
					
					//System.out.println(ct.getCatchers().get(0).getParameter().getType().toString());
					
					//CHE s = Util.getCHE(ct);
					//s.print(0, s);
					
					System.out.println("---------");
					
					final List<CtInvocation<?>> listaDeAssigments = new ArrayList<CtInvocation<?>>();
					
					ct.getElements(new Filter<CtAssignment<?, ?>>() {
						public boolean matches(final CtAssignment<?, ?> assigment) {
							System.out.println("ASSIGMENT");
							System.out.println(assigment);
							System.out.println(assigment.getAssigned());
							System.out.println(assigment.getShortRepresentation());
							
							if(assigment.getParent(new Filter<CtTry>() {
								public boolean matches(CtTry element) {
									return true;
								}
							}).equals(ct)) {
							
								assigment.getParent(new Filter<CtMethod<?>>() {
	
									public boolean matches(final CtMethod<?> method) {
										
										System.out.println("metodo: "+method);
										
										method.getBody().getElements(new Filter<CtInvocation<?>>() {
											public boolean matches(CtInvocation<?> invocation) {
												
												System.out.println("Metod element: "+ invocation);
												System.out.println(invocation.getShortRepresentation());
	
												if(verificandoPai(invocation.getParent(), method)) {
													if(invocation.getTarget().toString().equals(assigment.getAssigned().toString())) {
														listaDeAssigments.add(invocation);
														invocation.delete();
														System.out.println(listaDeAssigments);
														System.out.println("Opa, deu replace");
														System.out.println("metodo novamente: "+method);
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
						ct.getBody().addStatement(listaDeAssigments.get(0));
					
					
					System.out.println("-------------");
					
					System.out.println(ct);
					
					
					//System.out.println(s.print(0, null));

					//break;
					/*CHE main = new CHE();
					List<CtTypeReference<?>> classes = new ArrayList();
					
					for(CtCatch caths : ct.getCatchers()) {
						System.out.println(caths.getParameter().getType());
						classes.add(caths.getParameter().getType());
					}
					
					System.out.println(ct);
					for(CtInvocation<?> met : ct.getBody().getElements(new Filter<CtInvocation<?>>() {
						public boolean matches(CtInvocation<?> element) {
							return true;
						}
					})) {
						System.out.println(met);

						System.out.println(met.getTarget());
						
						System.out.println(met.getTarget().getType());
						
						System.out.println(met.getExecutable().getDeclaration());
						
						System.out.println(met.getExecutable().getDeclaration().getThrownTypes());
						
						System.out.println(met.getExecutable().getDeclaringType());
						
						for(CtTry cty : met.getExecutable().getDeclaringType().getElements(new Filter<CtTry>() {
							public boolean matches(CtTry element) {
								return true;
							}
						})) {
							
						}
						
						System.out.println("---------");
						*/
					//}
				}
		}
	}
	
	public static boolean verificandoPai(CtElement cte, CtMethod<?> possivelPai) {
		if(cte!=null) {
			if(cte instanceof CtTry) {
				return false;
			}else {
				if(possivelPai.equals(cte)) {
					return true;
				}else {
					return verificandoPai(cte.getParent(), possivelPai);
				}
			}
		}
		return false;
	}
}
