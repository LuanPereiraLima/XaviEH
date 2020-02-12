package ufc.br.xavieh.executers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spoon.reflect.CtModel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import ufc.br.xavieh.constants.PathProject;
import ufc.br.xavieh.exceptions.*;
import ufc.br.xavieh.models.ClassXMLCoverage;
import ufc.br.xavieh.models.ClassXMLCoverageLine;
import ufc.br.xavieh.models.ClassXMLCoverageLineMethod;
import ufc.br.xavieh.models.ClassXMLCoverageLineNormal;
import ufc.br.xavieh.models.CoverageResult;
import ufc.br.xavieh.models.TotalCoveredStatus;
import ufc.br.xavieh.runners.AbstractRunner;
import ufc.br.xavieh.util.Util;
import ufc.br.xavieh.util.UtilWriteReader;
import ufc.br.xavieh.util.XmlJacoco;

public class ExecuterEstatisticsCoverageEHStudy2 extends Execute {
	
	private ClassXMLCoverage cc = null;
	private ClassXMLCoverage ccMethod = null;
	private String path = null;
	
	public ExecuterEstatisticsCoverageEHStudy2() {
		super(false);
	}

	public ExecuterEstatisticsCoverageEHStudy2(boolean saveInFile, boolean cloneRepository, boolean verifyIfProjectAlreadyRun, boolean testProject) {
		super(saveInFile, cloneRepository, verifyIfProjectAlreadyRun, testProject);
	}

	public void execute() throws InicializerException, ListProjectsNotFoundException, NotURLsException, ConfigPropertiesNotFoundException {

		initializer();
		
		List<String> list = listProjects;
		
		List<CoverageResult> resultCoverageTotal = new ArrayList<CoverageResult>();
		Map<String, TotalCoveredStatus> tcs = new HashMap<>();
		
		for(int i=0; i < list.size(); i++) {

			if (list.get(i).startsWith("-")) {
				System.out.println("Jumping URL: " + list.get(i) + " By signal - .");
				continue;
			}

			String[] linha = list.get(i).split(" ");

			String version = getItemByUrl(linha, VERSION_URL);
			String submodule = getItemByUrl(linha, MODULE_URL);
			String commit = getItemByUrl(linha, COMMIT_URL);
			String build = getItemByUrl(linha, BUILD_URL);
			String pathProjectUrl = getItemByUrl(linha, PATH_PROJECT_URL);


			path = Util.validateAndGetNameRepository(linha[0]);

			if (version != null)
				path = path + "-" + version;

			if (AbstractRunner.listSavedMutantResultType != null) {
				System.out.println("-Verificando se o projeto já foi rodado...");
				boolean projectAlreadyRunned = false;
				for (String projeto : AbstractRunner.listSavedMutantResultType.keySet()) {
					if (path.equals(projeto)) {
						projectAlreadyRunned = true;
						break;
					}
				}
				System.out.println("--OK!");
				if (projectAlreadyRunned) {
					System.out.println("-O projeto " + path + " já possui resultados já rodados, o mesmo será pulado...");
					continue;
				}
			}

			if (!(path != null && !path.trim().isEmpty())) {
				System.out.println("Incorrect GIT URL: " + list.get(i) + " (Para resolver o problema analise as URLs adicionadas no arquivo 'repositiores.txt')");
				continue;
			}

			System.out.println("--------------------------------");
			System.out.println("-Cloning Repository: " + path + " ...");

			if(cloneRepository){
				try {
					Util.cloneRepository(linha[0], path, commit);
				} catch (CloneRepositoryException e) {
					System.out.println("-Não foi possível clonar a URL GIT: " + list.get(i) + " O projeto será ignorado. (Para resolver o problema analise as URLs adicionadas no arquivo 'repositiores.txt', ou verifique a conexão com a internet)");
					e.printStackTrace();
					continue;
				}
			}
			
			System.out.println("--Ok!");
			
			System.out.println("-Verificanndo se o projeto está passando nos testes inicialmente.");
			int result = 0;//Util.invoker(PathProject.makePathToProjectMaven(path), Collections.singletonList(submodule), true);
			
			if(result!=0) {
				System.out.println("--O projeto: "+path+" está com os testes falhando, este projeto será pulado.");
				continue;
			}
			System.out.println("--OK!");
			
			System.out.println("-Fazendo uma limpeza no projeto usando o Maven Clean.");
			//result = Util.invokerOthers(PathProject.makePathToProjectMaven(path), Arrays.asList("clean"), Collections.singletonList(submodule), true);
			
			if(result!=0) {
				System.out.println("--O projeto: "+path+" está com os clean falhando, este projeto será pulado.");
				continue;
			}
			System.out.println("--OK!");
			
			
			if(!testProjectSPOONCompability){
				if(!projectSPOONCompatibility(build, path, pathProjectUrl)){
					continue;

				}
				System.out.println("--OK!");
			}

			CtModel model = null;
			
			System.out.println(PathProject.makePathToProjectMaven(path, submodule));

			if(pathProjectUrl!=null){
				PathProject.PROJECT_PATH_FILES_DEFAULT = pathProjectUrl;
			}

			try {

				if(build != null && build.equals("g")) {
					model = Util.getModelNoMaven(PathProject.makePathToProjectMaven(path, submodule)+PathProject.PROJECT_PATH_FILES_DEFAULT);
				}else {
					model = Util.getModel(PathProject.makePathToProjectMaven(path, submodule));
				}

				if(model.getAllTypes().size() == 0){
					System.out.println("--Este projeto não é compatível com o Spoon Model. Projeto pulado.");
					continue;
				}

			}catch(Exception e) {
				System.out.println("--Este projeto não é compatível com o Spoon Model. Projeto pulado.");
				continue;
			}
			
			System.out.println();
			
			System.out.println("-----------------");
			System.out.println("Projeto: "+path);
			
			List<ClassXMLCoverage> listaxml = null;
			List<ClassXMLCoverage>  listaxmlMethods = null;
			TotalCoveredStatus totalCoveredStatus = null;
			
			listaxml = XmlJacoco.listaClassCoverageFromXMLJaCoCo(PathProject.makePathToProjectMavenToJacoco(path, submodule));
			listaxmlMethods = XmlJacoco.listaClassCoverageFromXMLJaCoCoMethods(PathProject.makePathToProjectMavenToJacoco(path, submodule));
			totalCoveredStatus = XmlJacoco.listaClassCoverageFromXMLJaCoCoTotalCoveredStatus(PathProject.makePathToProjectMavenToJacoco(path, submodule));
			
			System.out.println("quantidade: "+model.getAllTypes().size());
			for(CtType<?> tp : model.getAllTypes()) {
				System.out.println("Analisando "+tp+" do "+path);
				if(tp.isClass()) {

					List<CoverageResult> resultCoverage = new ArrayList<CoverageResult>();
					boolean encontrada = false;
					for(ClassXMLCoverage cx : listaxml) {
						if(cx.getFullName().replace(".java", "").equals(tp.getQualifiedName())) {
							cc = cx;
							encontrada = true;
							break;
						}
					}
					
					if(!encontrada) {
						System.out.println("Uma classe não foi encontrada.");
						System.out.println("nome: "+tp.getQualifiedName());
						
						continue;
						
//						CtClass<?> c = (CtClass<?>)tp;
//						System.out.println("pan pan pa: "+c.isTopLevel());
//
//						System.out.println("lista de classes disponíveis");
//						for(ClassXMLCoverage cx : listaxml) {
//							System.out.println("_- ");
//							System.out.println(cx.getFullName());
//						}	
//						System.exit(0);
						
					}
					
					System.out.println("Nome da classe: "+tp.getQualifiedName());
					System.out.println("Nome da classe pareada: "+cc.getFullName());
					
					//OBTENDO METODOS DA CLASSE
					List<CtTry> listTrys = tp.getElements(new Filter<CtTry>() {
						
						public boolean matches(CtTry element) {
							
							if(!element.getParent(new Filter<CtClass<?>>() {
								@Override
								public boolean matches(CtClass<?> element) {
									return true;
								}	
							}).getSimpleName().equals(tp.getSimpleName()))
								return false;
							
							return true;
						}
					});
					
					
					//OBTENDO INFORMAÇÕES DO TRY E SUAS LINHAS INTERNAS
					for(CtTry element: listTrys) {
						element.getBody().getElements(new Filter<CtBlock<?>>() {
							@Override
							public boolean matches(CtBlock<?> element) {
								for(CtStatement st: element.getStatements()) {
									
									if(st.isImplicit())
										continue;
									
									if(st instanceof CtThrow)
										continue;
									
									CoverageResult cr = new CoverageResult();
									cr.setClassName(tp.getQualifiedName());
									cr.setLineCode(st.getPosition().getLine());
									cr.setProject(path);
									cr.setCoveraged(false);
									cr.setType("TRY");
								
									if(st instanceof CtLoop) {
										CtLoop p = (CtLoop)st;
										cr.setLineContent(p.toString().replace(p.getBody().toString(), ""));
									}
									else if(st instanceof CtIf) {
										CtIf i = (CtIf) st;
										if(i.getElseStatement()==null)
											cr.setLineContent(st.toString().replace(i.getThenStatement().toString(), ""));
										else
											cr.setLineContent(st.toString().replace(i.getThenStatement().toString(), "").replace(i.getElseStatement().toString(), "").replace("else", ""));
									}else
										cr.setLineContent(st.toString());
									
									for(ClassXMLCoverageLine cl: cc.getLineDetails()) {
										if(cl.getNumberLine() == st.getPosition().getLine()) {
											cr.setCoveraged((cl.verifyCoverage()));
											cr.setCoverageLine(cl);
										}
									}
									
									if(cr.getCoverageLine()==null)
										continue;
									
									System.out.println(cr);
									resultCoverage.add(cr);
								}
								return false;
							}
							
						});
							
						//OBTENDO O CORPO DO FINALLY
						if(element.getFinalizer()!=null) {
							element.getFinalizer().getElements(new Filter<CtBlock<?>>() {
								@Override
								public boolean matches(CtBlock<?> element) {
									for(CtStatement st: element.getStatements()) {
										
										if(st instanceof CtThrow)
											continue;
										
										CoverageResult cr = new CoverageResult();
										cr.setClassName(tp.getQualifiedName());
										cr.setLineCode(st.getPosition().getLine());
										cr.setProject(path);
										cr.setCoveraged(false);
										cr.setType("FINALLY");
									
										if(st instanceof CtLoop) {
											CtLoop p = (CtLoop)st;
											cr.setLineContent(p.toString().replace(p.getBody().toString(), ""));
										}
										else if(st instanceof CtIf) {
											CtIf i = (CtIf) st;
											if(i.getElseStatement()==null)
												cr.setLineContent(st.toString().replace(i.getThenStatement().toString(), ""));
											else
												cr.setLineContent(st.toString().replace(i.getThenStatement().toString(), "").replace(i.getElseStatement().toString(), "").replace("else", ""));
										}else
											cr.setLineContent(st.toString());
										
										for(ClassXMLCoverageLine cl: cc.getLineDetails()) {
											if(cl.getNumberLine() == st.getPosition().getLine()) {
												cr.setCoveraged(cl.verifyCoverage());
												cr.setCoverageLine(cl);
											}
										}
										
										if(cr.getCoverageLine()==null)
											continue;
										
										System.out.println(cr);
										resultCoverage.add(cr);
									}
									return false;
								}
							
							});
						}
						
						//OBTENDO O CORPO DOS CATCHS
						for(CtCatch elementCatch: element.getCatchers()) {
							elementCatch.getBody().getElements(new Filter<CtBlock<?>>() {
								@Override
								public boolean matches(CtBlock<?> element) {

									for(CtStatement st: element.getStatements()) {
										
										if(st instanceof CtThrow)
											continue;
										
										if(st.isImplicit())
											continue;
										
										CoverageResult cr = new CoverageResult();
										cr.setClassName(tp.getQualifiedName());
										cr.setLineCode(st.getPosition().getLine());
										cr.setProject(path);
										cr.setCoveraged(false);
										cr.setType("CATCH");
									
										if(st instanceof CtLoop) {
											CtLoop p = (CtLoop)st;
											cr.setLineContent(p.toString().replace(p.getBody().toString(), ""));
										}
										else if(st instanceof CtIf) {
											CtIf i = (CtIf) st;
											if(i.getElseStatement()==null)
												cr.setLineContent(st.toString().replace(i.getThenStatement().toString(), ""));
											else
												cr.setLineContent(st.toString().replace(i.getThenStatement().toString(), "").replace(i.getElseStatement().toString(), "").replace("else", ""));
										}else
											cr.setLineContent(st.toString());
										
										for(ClassXMLCoverageLine cl: cc.getLineDetails()) {
											if(cl.getNumberLine() == st.getPosition().getLine()) {
												cr.setCoveraged(cl.verifyCoverage());
												cr.setCoverageLine(cl);
											}
										}
										
										if(cr.getCoverageLine()==null)
											continue;
										
										System.out.println(cr);
										resultCoverage.add(cr);
									}
									return false;
								}
								
							});
						
						}
					 }
					
					//OBTENDO METODOS DA CLASSE
					List<CtThrow> listThrows = tp.getElements(new Filter<CtThrow>() {
						
						public boolean matches(CtThrow element) {
							if(!element.getParent(new Filter<CtClass<?>>() {
								@Override
								public boolean matches(CtClass<?> element) {
									return true;
								}	
							}).getSimpleName().equals(tp.getSimpleName()))
								return false;

							return true;
						}
					});
					
					//Obtendo os Throw
					for(CtThrow element: listThrows) {
						CoverageResult cr = new CoverageResult();
						cr.setCoveraged(false);
						cr.setClassName(tp.getQualifiedName());
						cr.setLineCode(element.getPosition().getLine());
						cr.setLineContent(element.toString());
						cr.setProject(path);
						cr.setType("THROW");
						
						boolean entrou = false;
						for(ClassXMLCoverageLine cl: cc.getLineDetails()) {
							if(cl.getNumberLine() == element.getPosition().getLine()) {
								cr.setCoveraged(cl.verifyCoverage());
								cr.setCoverageLine(cl);
								entrou = true;
							}
						}
						
						if(!entrou) {
							System.out.println("classe atual: "+tp.getQualifiedName());
							System.out.println("classe: "+cc.getFullName());
							System.out.println("element.toString() "+element.toString());
							System.out.println("linha: "+element.getPosition().getLine());
							System.out.println("é implicito: "+element.isImplicit());
							System.out.println("linhas:");
							
							for(ClassXMLCoverageLine cl: cc.getLineDetails())
								System.out.println("----\n"+cl.getNumberLine());
						}
						
						if(cr.getCoverageLine()==null) {
							System.out.println("Coverage liene null");
						}
						if(entrou)
							resultCoverage.add(cr);
					}
					
					for(ClassXMLCoverage cx : listaxmlMethods) {
						if(cx.getFullName().replace(".java", "").equals(tp.getQualifiedName())) {
							ccMethod = cx;
							break;
						}
					}
					
					List<CtMethod<?>> listMethods = tp.getElements(new Filter<CtMethod<?>>() {

						@Override
						public boolean matches(CtMethod<?> element) {
							if(!element.getParent(new Filter<CtClass<?>>() {
								@Override
								public boolean matches(CtClass<?> element) {
									return true;
								}	
							}).getSimpleName().equals(tp.getSimpleName())) {
								return false;
							}
							
							if(element.getThrownTypes()==null)
								return false;
							
							if(element.getThrownTypes().size() == 0)
								return false;
							
							if(element.isAbstract())
								return false;
							
							return true;
						}
					});
					
					System.out.println("--");
					for(CtMethod<?> me : listMethods) {
						String nome = me.getSignature()+" throws ";
					
						for(CtTypeReference<? extends Throwable> ee : me.getThrownTypes()) {
							nome+=ee.getQualifiedName()+", ";
						}
					
						CoverageResult cr = new CoverageResult();
						cr.setClassName(tp.getQualifiedName());
						cr.setLineCode(me.getPosition().getLine());
						cr.setProject(path);
						cr.setCoveraged(false);
						cr.setLineContent(nome);
						cr.setType("THROWS");
						
						boolean entrou = false;
						for(ClassXMLCoverageLine cl: ccMethod.getLineDetails()) {
							ClassXMLCoverageLineMethod clm = (ClassXMLCoverageLineMethod) cl;
							if(me.getSimpleName().equals(clm.getMethodName())){
								if(me.getPosition().getLine() <= clm.getNumberLine() && clm.getNumberLine() <= me.getPosition().getEndLine()) {
									cr.setCoveraged(clm.verifyCoverage());
									cr.setCoverageLine(clm);
									entrou = true;
									break;
								}else {
									System.out.println("O IF da linha n entrou");
								}
							}
						}
						if(!entrou) {
							System.out.println("--");
							System.out.println("WAU "+ me.getSimpleName());
							System.out.println("-Nome da classe: "+tp.getQualifiedName());
							System.out.println("-Nome da classe do ccMethod: "+ccMethod.getFullName());
							System.out.println("-");
							for(ClassXMLCoverageLine cl: ccMethod.getLineDetails()) {
								ClassXMLCoverageLineMethod clm = (ClassXMLCoverageLineMethod) cl;
								System.out.println(clm.getMethodName());
							}
							System.out.println("-");
							System.out.println(cr);
							System.out.println("--");
						}
						resultCoverage.add(cr);
					}
					
					resultCoverageTotal.addAll(resultCoverage);
					
					System.out.println("analisando "+path);
					if(!tcs.containsKey(path)) {
						System.out.println("adicionando "+path);
						tcs.put(path, new TotalCoveredStatus());
					}
					
					for(CoverageResult rc: resultCoverage) {
						
						if(rc.getType().equals("THROW")) {
							
							if(rc.getCoverageLine() instanceof ClassXMLCoverageLineNormal) {
								ClassXMLCoverageLineNormal cn = (ClassXMLCoverageLineNormal) rc.getCoverageLine();
								tcs.get(path).setTHROW_CI_TotalCoveredInstructionsThrowStatements(tcs.get(path).getTHROW_CI_TotalCoveredInstructionsThrowStatements()+cn.getCi());
								tcs.get(path).setTHROW_MI_TotalMissedInstructionsThrowStatements(tcs.get(path).getTHROW_MI_TotalMissedInstructionsThrowStatements()+cn.getMi());
							}else
								System.out.println("NÃO ENTROU THROW");
							
						}else if(rc.getType().equals("THROWS")) {
							
							if(rc.getCoverageLine() instanceof ClassXMLCoverageLineMethod) {
								ClassXMLCoverageLineMethod cm = (ClassXMLCoverageLineMethod) rc.getCoverageLine();
								tcs.get(path).setTHROWS_CM_TotalCoveredMethodsWithThrows(tcs.get(path).getTHROWS_CM_TotalCoveredMethodsWithThrows()+cm.getCm());
								tcs.get(path).setTHROWS_MM_TotalMissedMethodsWithThrows(tcs.get(path).getTHROWS_MM_TotalMissedMethodsWithThrows()+cm.getMm());
							}else
								System.out.println("NÃO ENTROU THROWS");
							
						}else if(rc.getType().equals("TRY")) {
							
							if(rc.getCoverageLine() instanceof ClassXMLCoverageLineNormal) {
								ClassXMLCoverageLineNormal cn = (ClassXMLCoverageLineNormal) rc.getCoverageLine();
								tcs.get(path).setTRY_MI_TotalMissedInstructionsTryBlocks(tcs.get(path).getTRY_MI_TotalMissedInstructionsTryBlocks()+cn.getMi());
								tcs.get(path).setTRY_CI_TotalCoveredInstructionsTryBlocks(tcs.get(path).getTRY_CI_TotalCoveredInstructionsTryBlocks()+cn.getCi());
								tcs.get(path).setTRY_MB_TotalMissedBrachesTryBlocks(tcs.get(path).getTRY_MB_TotalMissedBrachesTryBlocks()+cn.getMb());
								tcs.get(path).setTRY_CB_TotalCoveredBrachesTryBlocks(tcs.get(path).getTRY_CB_TotalCoveredBrachesTryBlocks()+cn.getCb());
							}else
								System.out.println("NÃO ENTROU TRY");
							
						}else if(rc.getType().equals("CATCH")) {

							if(rc.getCoverageLine() instanceof ClassXMLCoverageLineNormal) {
								ClassXMLCoverageLineNormal cn = (ClassXMLCoverageLineNormal) rc.getCoverageLine();
								tcs.get(path).setCATCH_MI_TotalMissedInstructionsCatchBlocks(tcs.get(path).getCATCH_MI_TotalMissedInstructionsCatchBlocks()+cn.getMi());
								tcs.get(path).setCATCH_CI_TotalCoveredInstructionsCatchBlocks(tcs.get(path).getCATCH_CI_TotalCoveredInstructionsCatchBlocks()+cn.getCi());
								tcs.get(path).setCATCH_MB_TotalMissedBrachesCatchBlocks(tcs.get(path).getCATCH_MB_TotalMissedBrachesCatchBlocks()+cn.getMb());
								tcs.get(path).setCATCH_CB_TotalCoveredBrachesCatchBlocks(tcs.get(path).getCATCH_CB_TotalCoveredBrachesCatchBlocks()+cn.getCb());
							}
							
						}else if(rc.getType().equals("FINALLY")) {
							
							if(rc.getCoverageLine() instanceof ClassXMLCoverageLineNormal) {
								ClassXMLCoverageLineNormal cn = (ClassXMLCoverageLineNormal) rc.getCoverageLine();
								tcs.get(path).setFINALLY_MI_TotalMissedInstructionsFinallyBlocks(tcs.get(path).getFINALLY_MI_TotalMissedInstructionsFinallyBlocks()+cn.getMi());
								tcs.get(path).setFINALLY_CI_TotalCoveredInstructionsFinallyBlocks(tcs.get(path).getFINALLY_CI_TotalCoveredInstructionsFinallyBlocks()+cn.getCi());
								tcs.get(path).setFINALLY_MB_TotalMissedBrachesFinallyBlocks(tcs.get(path).getFINALLY_MB_TotalMissedBrachesFinallyBlocks()+cn.getMb());
								tcs.get(path).setFINALLY_CB_TotalCoveredBrachesFinallyBlocks(tcs.get(path).getFINALLY_CB_TotalCoveredBrachesFinallyBlocks()+cn.getCb());
							}else
								System.out.println("NÃO ENTROU FINNALY");
						}
					}
				}
			}
			
			
			System.out.println("olhando este "+path);
			if(tcs.get(path)==null) {
				System.out.println("não existe este");
				System.out.println("opa, ta nulzão");
			}
			
			tcs.get(path).setCI_TotalCoveredInstructions(totalCoveredStatus.getCI_TotalCoveredInstructions());
			tcs.get(path).setCB_TotalCoveredBraches(totalCoveredStatus.getCB_TotalCoveredBraches());
			tcs.get(path).setMB_TotalMissedBraches(totalCoveredStatus.getMB_TotalMissedBraches());
			tcs.get(path).setMI_TotalMissedInstructions(totalCoveredStatus.getMI_TotalMissedInstructions());
			tcs.get(path).setMM_TotalMissedMethods(totalCoveredStatus.getMM_TotalMissedMethods());
			tcs.get(path).setCM_TotalCoveredMethods(totalCoveredStatus.getCM_TotalCoveredMethods());
	
			/**
			 *  vou resumir as informações que quero que você extraia do código fonte com respeito ao tratamento de exceção:
			 *  ok - linhas de código que tem lançamento (throw),
			 *   ok - área protegida (try) + linha de cada instrução do bloco,
			 *   no removed - sinalização (throws),
			 *    ok - tratamento (catch) + linha de cada instrução dentro do bloco e 
			 *     ok - ação de limpeza (finally) + linha de cada instrução dentro do bloco. 
			 *      Tudo isso associado a respectiva classe.
			 */
		
		}
		
		try {
			System.setOut(new PrintStream(new FileOutputStream("PLIN-output.txt")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		System.out.println(resultCoverageTotal);


		//TODO REMOVIDO, CONTEÙDO QUE MOSTRA PARA QUE CADA LINHA ERA RELACIONADA
		//UtilWriteReader.writeCsvFileEstatistics2(resultCoverageTotal);
		UtilWriteReader.writeCsvFileEstatistics2(tcs);
		
		if(saveOutputInFile)	
			System.out.close();
	}
}