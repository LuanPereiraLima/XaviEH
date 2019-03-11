package ufc.br.mutant_project.executers;

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
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;
import ufc.br.mutant_project.constants.PathProject;
import ufc.br.mutant_project.exceptions.ConfigPropertiesNotFoundException;
import ufc.br.mutant_project.exceptions.InicializerException;
import ufc.br.mutant_project.exceptions.ListProjectsNotFoundException;
import ufc.br.mutant_project.exceptions.NotURLsException;
import ufc.br.mutant_project.models.ClassXMLCoverage;
import ufc.br.mutant_project.models.ClassXMLCoverageLine;
import ufc.br.mutant_project.models.ClassXMLCoverageLineNormal;
import ufc.br.mutant_project.models.CoverageResult;
import ufc.br.mutant_project.models.TotalCoveredStatus;
import ufc.br.mutant_project.runners.AbstractRunner;
import ufc.br.mutant_project.util.Util;
import ufc.br.mutant_project.util.UtilWriteReader;
import ufc.br.mutant_project.util.XmlJacoco;

public class ExecuterEstatisticsCoverageEH extends Executer{
	
	private ClassXMLCoverage cc = null;
	private String path = null;
	
	public ExecuterEstatisticsCoverageEH() {
		super(false);
	}
	
	public void execute() throws InicializerException, ListProjectsNotFoundException, NotURLsException, ConfigPropertiesNotFoundException {

		inicializer();
		
		List<String> list = listProjects;
		List<CoverageResult> resultCoverageTotal = new ArrayList<CoverageResult>();
		Map<String, TotalCoveredStatus> tcs = new HashMap<>();
		
		for(int i=0; i < list.size(); i++) {
			
			if(list.get(i).startsWith("-")) {
				System.out.println("Jumping URL: "+list.get(i)+" By signal - .");
				continue;
			}
			
			String[] linha = list.get(i).split(" ");
			
			String version = getItemByUrl(linha, VERSION_URL);
			String commit = getItemByUrl(linha, COMMIT_URL);
			String submodule = getItemByUrl(linha, MODULE_URL);
			String build = getItemByUrl(linha, BUILD_URL);
			
			path = Util.validateAndGetNameRepository(linha[0]);
			
			if(version!=null)
				path=path+"-"+version;
			
			if(AbstractRunner.listSavedMutantResultType!=null) {
				System.out.println("-Verificando se o projeto já foi rodado...");
				boolean projectAlreadyRunned = false;
				for(String projeto : AbstractRunner.listSavedMutantResultType.keySet()) {
					if(path.equals(projeto)) {
						projectAlreadyRunned = true;
						break;
					}
				}
				System.out.println("--OK!");
				if(projectAlreadyRunned) {
					System.out.println("-O projeto "+path+" já possui resultados já rodados, o mesmo será pulado...");
					continue;
				}
			}
			
			if(!(path!=null && !path.trim().isEmpty())) {
				System.out.println("Incorrect GIT URL: "+list.get(i)+" (Para resolver o problema analise as URLs adicionadas no arquivo 'repositiores.txt')");
				continue;
			}

			System.out.println("--------------------------------");
			System.out.println("-Cloning Repository: "+path+" ...");
			
			
//			try {
//				Util.cloneRepository(linha[0], path, commit);
//			} catch (CloneRepositoryException e) {
//				System.out.println("-Não foi possível clonar a URL GIT: "+list.get(i)+" O projeto será ignorado. (Para resolver o problema analise as URLs adicionadas no arquivo 'repositiores.txt', ou verifique a conexão com a internet)");
//				e.printStackTrace();
//				continue;
//			}
			
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
			
			
			System.out.println("-Verificando se o projeto é compatível com o SPOON para a criação de modelos.");
			
//			try {
//				if(Util.getModel(PathProject.makePathToProjectMaven(path)).getAllTypes().size() == 0){
//					System.out.println("--Este projeto não é compatível com o Spoon Model. Projeto pulado.");
//					continue;
//				}
//			}catch(Exception e) {
//				System.out.println("--Este projeto não é compatível com o Spoon Model. Projeto pulado.");
//				continue;
//			}
			System.out.println("--OK!");
			
			CtModel model = null;
			
			System.out.println(PathProject.makePathToProjectMaven(path, submodule));
			
			if(build != null && build.equals("g"))
				model = Util.getModelNoMaven(PathProject.makePathToProjectMaven(path, submodule)+"src/main/java");
			else
				model = Util.getModel(PathProject.makePathToProjectMaven(path, submodule));
				
			System.out.println();
			
			System.out.println("-----------------");
			System.out.println("Projeto: "+path);
			
//			try {
//				System.out.println("-Reportando a cobertura do projeto usando o JaCoCo");
//				Util.createReportJaCoCo(PathProject.makePathToProjectMaven(path), submodule);
//				System.out.println("--OK!");
//			} catch (PomException e) {
//				System.out.println(e.getMessage());
//				e.printStackTrace();
//			} catch (JacocoException e) {
//				System.out.println(e.getMessage());
//				e.printStackTrace();
//			} catch (TestFailMavenInvokerException e) {
//				System.out.println(e.getMessage());
//				e.printStackTrace();
//			}
//			
			List<ClassXMLCoverage> listaxml = null;
		//	List<ClassXMLCoverage>  listaxmlMethods = null;
			TotalCoveredStatus totalCoveredStatus = null;
			
			listaxml = XmlJacoco.listaClassCoverageFromXMLJaCoCo(PathProject.makePathToProjectMavenToJacoco(path, submodule));
			//listaxmlMethods = XmlJacoco.listaClassCoverageFromXMLJaCoCoMethods(PathProject.makePathToProjectMavenToJacoco(path, submodule));
			totalCoveredStatus = XmlJacoco.listaClassCoverageFromXMLJaCoCoTotalCoveredStatus(PathProject.makePathToProjectMavenToJacoco(path, submodule));
			
			for(CtType<?> tp : model.getAllTypes()) {
			
				System.out.println("Analisando a classe: "+tp.getQualifiedName()+" do "+path);
				
				if(tp.isClass()) {
			
					List<CoverageResult> resultCoverage = new ArrayList<CoverageResult>();
					//Obtendo a linha correspondente no jacoco
					boolean encontrada = false;
					
					for(ClassXMLCoverage cx : listaxml) {
						//System.out.println("ali: "+cx.getFullName());
//						System.out.println("L: "+tp.isTopLevel());
						if(cx.getFullName().replace(".java", "").equals(tp.getQualifiedName())) {
							cc = cx;
							encontrada = true;
							break;
						}
					}
					
					if(!encontrada) {
						System.out.println("Uma classe não foi encontrada na cobertura do jacoco.");
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
					
					System.out.println("Nome da classe do Model: "+tp.getQualifiedName());
					System.out.println("Nome da classe pareada no jacoco: "+cc.getFullName());
					
					
					//All Raisings
					
					List<CtThrow> raisers = tp.getElements(new TypeFilter<CtThrow>(CtThrow.class));

					if (!raisers.isEmpty()) {
						for (CtThrow raiser : raisers) {
							//System.out.println(
								//	"-- Throws [" + raiser.getPosition().getLine() + "," + raiser.getPosition().getEndLine()
									//		+ "]: " + raiser.getThrownExpression().getType().getQualifiedName());
							
							CoverageResult cr = new CoverageResult();
							cr.setCoveraged(false);
							cr.setClassName(tp.getQualifiedName());
							cr.setLineCode(raiser.getPosition().getLine());
							cr.setLineContent(raiser.toString());
							cr.setProject(path);
							cr.setTypeCode(CoverageResult.ALL_RASINGS);
							cr.setType(CoverageResult.THROW);
							
							boolean entrou = false;
							for(ClassXMLCoverageLine cl: cc.getLineDetails()) {
								if(cl.getNumberLine() == raiser.getPosition().getLine()) {
									cr.setCoveraged(cl.verifyCoverage());
									cr.setCoverageLine(cl);
									entrou = true;
								}
							}
							
							if(cr.getCoverageLine()==null) {
								System.out.println("Coverage liene null");
							}
							
							if(entrou)
								resultCoverage.add(cr);
						}
					}
					
					//Raisings of Programmer Defined Exception
					
					System.out.println("Raisings of Programmer Defined Exception");
					List<CtThrow> raisers2 = findRaisers(true, tp);

					if (!raisers2.isEmpty()) {
						for (CtThrow raiser : raisers2) {
							//System.out.println(
								//	"-- Throws [" + raiser.getPosition().getLine() + "," + raiser.getPosition().getEndLine()
//											+ "]: " + raiser.getThrownExpression().getType().getQualifiedName());
							
							CoverageResult cr = new CoverageResult();
							cr.setCoveraged(false);
							cr.setClassName(tp.getQualifiedName());
							cr.setLineCode(raiser.getPosition().getLine());
							cr.setLineContent(raiser.toString());
							cr.setProject(path);
							cr.setTypeCode(CoverageResult.RAISINGS_PROGRAMMER_DEFINED);
							cr.setType(CoverageResult.THROW);
							
							boolean entrou = false;
							for(ClassXMLCoverageLine cl: cc.getLineDetails()) {
								if(cl.getNumberLine() == raiser.getPosition().getLine()) {
									cr.setCoveraged(cl.verifyCoverage());
									cr.setCoverageLine(cl);
									entrou = true;
								}
							}
							
							if(cr.getCoverageLine()==null) {
								System.out.println("Coverage liene null");
							}
							if(entrou) {
								resultCoverage.add(cr);
								System.out.println("ADDD Raisings of Programmer Defined Exception");
							}else {
								System.out.println("Não entrou");
							}
						}
					}
					
					//Raisings of Non Programmer Defined Exception
					
					List<CtThrow> raisers3 = findRaisers(false, tp);

					if (!raisers3.isEmpty()) {
						for (CtThrow raiser : raisers3) {
//							System.out.println(
	//								"-- Throws [" + raiser.getPosition().getLine() + "," + raiser.getPosition().getEndLine()
//
	//										+ "]: " + raiser.getThrownExpression().getType().getQualifiedName());
							
							CoverageResult cr = new CoverageResult();
							cr.setCoveraged(false);
							cr.setClassName(tp.getQualifiedName());
							cr.setLineCode(raiser.getPosition().getLine());
							cr.setLineContent(raiser.toString());
							cr.setProject(path);
							cr.setTypeCode(CoverageResult.RAISINGS_NON_PROGRAMMER_DEFINED);
							cr.setType(CoverageResult.THROW);
							
							boolean entrou = false;
							for(ClassXMLCoverageLine cl: cc.getLineDetails()) {
								if(cl.getNumberLine() == raiser.getPosition().getLine()) {
									cr.setCoveraged(cl.verifyCoverage());
									cr.setCoverageLine(cl);
									entrou = true;
								}
							}
							
							if(cr.getCoverageLine()==null) {
								System.out.println("Coverage liene null");
							}
							if(entrou) {
								resultCoverage.add(cr);
								System.out.println("NON Raisings of Programmer Defined Exception");
							}
						}
					}
					
					//All Handlings
					List<CtCatch> catchers = tp.getElements(new TypeFilter<CtCatch>(CtCatch.class));

					if (!catchers.isEmpty()) {
						for(CtCatch handler : catchers) {
							//System.out.println(
							//		"-- Catch [" + handler.getPosition().getLine() + "," + handler.getPosition().getEndLine()
								//			+ "]: " + handler.getParameter().getMultiTypes().toString());
							
							handler.getBody().getElements(new Filter<CtBlock<?>>() {

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
										cr.setTypeCode(CoverageResult.ALL_HANDLINGS);
										cr.setType(CoverageResult.CATCH);
									
										if(st instanceof CtLoop) {
											CtLoop p = (CtLoop)st;
											cr.setLineContent(p.toString().replace(p.getBody().toString(), ""));
										}
										else if(st instanceof CtIf) {
											CtIf i = (CtIf) st;
											if(i.getElseStatement()==null) {
												cr.setLineContent(st.toString().replace(i.getThenStatement().toString(), ""));
											}else {
												cr.setLineContent(st.toString().replace(i.getThenStatement().toString(), "").replace(i.getElseStatement().toString(), "").replace("else", ""));
											}
										}else {
											cr.setLineContent(st.toString());
										}
										
										for(ClassXMLCoverageLine cl: cc.getLineDetails()) {
											if(cl.getNumberLine() == st.getPosition().getLine()) {
												cr.setCoveraged(cl.verifyCoverage());
												cr.setCoverageLine(cl);
											}
										}
										
										if(cr.getCoverageLine()==null)
											continue;
										
										//System.out.println(cr);
										resultCoverage.add(cr);
									}
									return false;
								}
							});
						}
					}
					
					//Handlings of Programmer Defined Exception
					
					List<CtCatch> catchers2 = findHandlers(true, tp);

					if (!catchers2.isEmpty()) {
						for(CtCatch handler : catchers2) {
							//System.out.println(
								//	"-- Catch [" + handler.getPosition().getLine() + "," + handler.getPosition().getEndLine()
									//		+ "]: " + handler.getParameter().getMultiTypes().toString());
							//passando por cada elemento do catch
							handler.getBody().getElements(new Filter<CtBlock<?>>() {

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
										cr.setTypeCode(CoverageResult.HANDLINGS_PROGRAMMER_DEFINED);
										cr.setType(CoverageResult.CATCH);
									
										if(st instanceof CtLoop) {
											CtLoop p = (CtLoop)st;
											cr.setLineContent(p.toString().replace(p.getBody().toString(), ""));
										}
										else if(st instanceof CtIf) {
											CtIf i = (CtIf) st;
											if(i.getElseStatement()==null) {
												cr.setLineContent(st.toString().replace(i.getThenStatement().toString(), ""));
											}else {
												cr.setLineContent(st.toString().replace(i.getThenStatement().toString(), "").replace(i.getElseStatement().toString(), "").replace("else", ""));
											}
										}else {
											cr.setLineContent(st.toString());
										}
										
										for(ClassXMLCoverageLine cl: cc.getLineDetails()) {
											if(cl.getNumberLine() == st.getPosition().getLine()) {
												cr.setCoveraged(cl.verifyCoverage());
												cr.setCoverageLine(cl);
											}
										}
										
										if(cr.getCoverageLine()==null)
											continue;
										
										//System.out.println(cr);
										resultCoverage.add(cr);
									}
									return false;
								}
							});
						}
					}
					
					//Handlings of Non Programmer Defined Exception
					
					List<CtCatch> catchers3 = findHandlers(false, tp);
					
					if (!catchers3.isEmpty()) {
						for(CtCatch handler : catchers3) {
							//System.out.println(
								//	"-- Catch [" + handler.getPosition().getLine() + "," + handler.getPosition().getEndLine()
									//		+ "]: " + handler.getParameter().getMultiTypes().toString());
							
							//passando por cada elemento do catch
							handler.getBody().getElements(new Filter<CtBlock<?>>() {

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
										cr.setTypeCode(CoverageResult.HANDLINGS_NON_PROGRAMMER_DEFINED);
										cr.setType(CoverageResult.CATCH);
									
										if(st instanceof CtLoop) {
											CtLoop p = (CtLoop)st;
											cr.setLineContent(p.toString().replace(p.getBody().toString(), ""));
										}
										else if(st instanceof CtIf) {
											CtIf i = (CtIf) st;
											if(i.getElseStatement()==null) {
												cr.setLineContent(st.toString().replace(i.getThenStatement().toString(), ""));
											}else {
												cr.setLineContent(st.toString().replace(i.getThenStatement().toString(), "").replace(i.getElseStatement().toString(), "").replace("else", ""));
											}
										}else {
											cr.setLineContent(st.toString());
										}
										
										for(ClassXMLCoverageLine cl: cc.getLineDetails()) {
											if(cl.getNumberLine() == st.getPosition().getLine()) {
												cr.setCoveraged(cl.verifyCoverage());
												cr.setCoverageLine(cl);
											}
										}
										
										if(cr.getCoverageLine()==null)
											continue;
										
										//System.out.println(cr);
										resultCoverage.add(cr);
									}
									return false;
								}
							});
						}
					}
					
					resultCoverageTotal.addAll(resultCoverage);
					
					System.out.println("analisando "+path);
					if(!tcs.containsKey(path)) {
						System.out.println("adicionando "+path);
						tcs.put(path, new TotalCoveredStatus());
					}
					
					for(CoverageResult rc: resultCoverage) {
						
						if(rc.getType().equals(CoverageResult.THROW) && rc.getTypeCode().equals(CoverageResult.ALL_RASINGS)) {
							if(rc.getCoverageLine() instanceof ClassXMLCoverageLineNormal) {
								ClassXMLCoverageLineNormal cn = (ClassXMLCoverageLineNormal) rc.getCoverageLine();
								if(rc.isCoveraged()) {
									tcs.get(path).setTHROW_CI_TotalCoveredInstructionsThrowStatements(tcs.get(path).getTHROW_CI_TotalCoveredInstructionsThrowStatements()+2);
									tcs.get(path).setTHROW_MI_TotalMissedInstructionsThrowStatements(tcs.get(path).getTHROW_MI_TotalMissedInstructionsThrowStatements()+0);
								}else {
									tcs.get(path).setTHROW_CI_TotalCoveredInstructionsThrowStatements(tcs.get(path).getTHROW_CI_TotalCoveredInstructionsThrowStatements()+0);
									tcs.get(path).setTHROW_MI_TotalMissedInstructionsThrowStatements(tcs.get(path).getTHROW_MI_TotalMissedInstructionsThrowStatements()+2);
								}
							}else {
								System.out.println("NÃO ENTROU THROW");
							}
							
						}else if(rc.getType().equals(CoverageResult.THROW) && rc.getTypeCode().equals(CoverageResult.RAISINGS_PROGRAMMER_DEFINED)) {
							if(rc.getCoverageLine() instanceof ClassXMLCoverageLineNormal) {
								ClassXMLCoverageLineNormal cn = (ClassXMLCoverageLineNormal) rc.getCoverageLine();
								if(rc.isCoveraged()) {
									tcs.get(path).setTHROW_I_CI_TotalCoveredInstructionsThrowStatements(tcs.get(path).getTHROW_I_CI_TotalCoveredInstructionsThrowStatements()+2);
									tcs.get(path).setTHROW_I_MI_TotalMissedInstructionsThrowStatements(tcs.get(path).getTHROW_I_MI_TotalMissedInstructionsThrowStatements()+0);
								}else {
									tcs.get(path).setTHROW_I_CI_TotalCoveredInstructionsThrowStatements(tcs.get(path).getTHROW_I_CI_TotalCoveredInstructionsThrowStatements()+0);
									tcs.get(path).setTHROW_I_MI_TotalMissedInstructionsThrowStatements(tcs.get(path).getTHROW_I_MI_TotalMissedInstructionsThrowStatements()+2);
								}	
							}else {
								System.out.println("NÃO ENTROU THROW");
							}
							
						}else if(rc.getType().equals(CoverageResult.THROW) && rc.getTypeCode().equals(CoverageResult.RAISINGS_NON_PROGRAMMER_DEFINED)) {
							if(rc.getCoverageLine() instanceof ClassXMLCoverageLineNormal) {
								ClassXMLCoverageLineNormal cn = (ClassXMLCoverageLineNormal) rc.getCoverageLine();
								if(rc.isCoveraged()) {
									tcs.get(path).setTHROW_E_CI_TotalCoveredInstructionsThrowStatements(tcs.get(path).getTHROW_E_CI_TotalCoveredInstructionsThrowStatements()+2);
									tcs.get(path).setTHROW_E_MI_TotalMissedInstructionsThrowStatements(tcs.get(path).getTHROW_E_MI_TotalMissedInstructionsThrowStatements()+0);
								}else {
									tcs.get(path).setTHROW_E_CI_TotalCoveredInstructionsThrowStatements(tcs.get(path).getTHROW_E_CI_TotalCoveredInstructionsThrowStatements()+0);
									tcs.get(path).setTHROW_E_MI_TotalMissedInstructionsThrowStatements(tcs.get(path).getTHROW_E_MI_TotalMissedInstructionsThrowStatements()+2);
								}		
							}else {
								System.out.println("NÃO ENTROU THROW");
							}
							
						}
						/*else if(rc.getType().equals("THROWS")) {
							
							if(rc.getCoverageLine() instanceof ClassXMLCoverageLineMethod) {
								ClassXMLCoverageLineMethod cm = (ClassXMLCoverageLineMethod) rc.getCoverageLine();
								tcs.get(path).setTHROWS_CM_TotalCoveredMethodsWithThrows(tcs.get(path).getTHROWS_CM_TotalCoveredMethodsWithThrows()+cm.getCm());
								tcs.get(path).setTHROWS_MM_TotalMissedMethodsWithThrows(tcs.get(path).getTHROWS_MM_TotalMissedMethodsWithThrows()+cm.getMm());
							}else {
								System.out.println("NÃO ENTROU THROWS");
							}
							
						}else if(rc.getType().equals("TRY")) {
							
							if(rc.getCoverageLine() instanceof ClassXMLCoverageLineNormal) {
								ClassXMLCoverageLineNormal cn = (ClassXMLCoverageLineNormal) rc.getCoverageLine();
								tcs.get(path).setTRY_MI_TotalMissedInstructionsTryBlocks(tcs.get(path).getTRY_MI_TotalMissedInstructionsTryBlocks()+cn.getMi());
								tcs.get(path).setTRY_CI_TotalCoveredInstructionsTryBlocks(tcs.get(path).getTRY_CI_TotalCoveredInstructionsTryBlocks()+cn.getCi());
								tcs.get(path).setTRY_MB_TotalMissedBrachesTryBlocks(tcs.get(path).getTRY_MB_TotalMissedBrachesTryBlocks()+cn.getMb());
								tcs.get(path).setTRY_CB_TotalCoveredBrachesTryBlocks(tcs.get(path).getTRY_CB_TotalCoveredBrachesTryBlocks()+cn.getCb());
							}else {
								System.out.println("NÃO ENTROU TRY");
							}
							
						}*/else if(rc.getType().equals(CoverageResult.CATCH) && rc.getTypeCode().equals(CoverageResult.ALL_HANDLINGS)) {

							if(rc.getCoverageLine() instanceof ClassXMLCoverageLineNormal) {
								ClassXMLCoverageLineNormal cn = (ClassXMLCoverageLineNormal) rc.getCoverageLine();
								tcs.get(path).setCATCH_MI_TotalMissedInstructionsCatchBlocks(tcs.get(path).getCATCH_MI_TotalMissedInstructionsCatchBlocks()+cn.getMi());
								tcs.get(path).setCATCH_CI_TotalCoveredInstructionsCatchBlocks(tcs.get(path).getCATCH_CI_TotalCoveredInstructionsCatchBlocks()+cn.getCi());
								tcs.get(path).setCATCH_MB_TotalMissedBrachesCatchBlocks(tcs.get(path).getCATCH_MB_TotalMissedBrachesCatchBlocks()+cn.getMb());
								tcs.get(path).setCATCH_CB_TotalCoveredBrachesCatchBlocks(tcs.get(path).getCATCH_CB_TotalCoveredBrachesCatchBlocks()+cn.getCb());
							}
							
						}else if(rc.getType().equals(CoverageResult.CATCH) && rc.getTypeCode().equals(CoverageResult.HANDLINGS_PROGRAMMER_DEFINED)) {
							if(rc.getCoverageLine() instanceof ClassXMLCoverageLineNormal) {
								ClassXMLCoverageLineNormal cn = (ClassXMLCoverageLineNormal) rc.getCoverageLine();
								tcs.get(path).setCATCH_I_MI_TotalMissedInstructionsCatchBlocks(tcs.get(path).getCATCH_I_MI_TotalMissedInstructionsCatchBlocks()+cn.getMi());
								tcs.get(path).setCATCH_I_CI_TotalCoveredInstructionsCatchBlocks(tcs.get(path).getCATCH_I_CI_TotalCoveredInstructionsCatchBlocks()+cn.getCi());
								tcs.get(path).setCATCH_I_MB_TotalMissedBrachesCatchBlocks(tcs.get(path).getCATCH_I_MB_TotalMissedBrachesCatchBlocks()+cn.getMb());
								tcs.get(path).setCATCH_I_CB_TotalCoveredBrachesCatchBlocks(tcs.get(path).getCATCH_I_CB_TotalCoveredBrachesCatchBlocks()+cn.getCb());
							}
						}else if(rc.getType().equals(CoverageResult.CATCH) && rc.getTypeCode().equals(CoverageResult.HANDLINGS_NON_PROGRAMMER_DEFINED)) {
							if(rc.getCoverageLine() instanceof ClassXMLCoverageLineNormal) {
								ClassXMLCoverageLineNormal cn = (ClassXMLCoverageLineNormal) rc.getCoverageLine();
								tcs.get(path).setCATCH_E_MI_TotalMissedInstructionsCatchBlocks(tcs.get(path).getCATCH_E_MI_TotalMissedInstructionsCatchBlocks()+cn.getMi());
								tcs.get(path).setCATCH_E_CI_TotalCoveredInstructionsCatchBlocks(tcs.get(path).getCATCH_E_CI_TotalCoveredInstructionsCatchBlocks()+cn.getCi());
								tcs.get(path).setCATCH_E_MB_TotalMissedBrachesCatchBlocks(tcs.get(path).getCATCH_E_MB_TotalMissedBrachesCatchBlocks()+cn.getMb());
								tcs.get(path).setCATCH_E_CB_TotalCoveredBrachesCatchBlocks(tcs.get(path).getCATCH_E_CB_TotalCoveredBrachesCatchBlocks()+cn.getCb());
							}
						}
						/*else if(rc.getType().equals("FINALLY")) {
							
							if(rc.getCoverageLine() instanceof ClassXMLCoverageLineNormal) {
								ClassXMLCoverageLineNormal cn = (ClassXMLCoverageLineNormal) rc.getCoverageLine();
								tcs.get(path).setFINALLY_MI_TotalMissedInstructionsFinallyBlocks(tcs.get(path).getFINALLY_MI_TotalMissedInstructionsFinallyBlocks()+cn.getMi());
								tcs.get(path).setFINALLY_CI_TotalCoveredInstructionsFinallyBlocks(tcs.get(path).getFINALLY_CI_TotalCoveredInstructionsFinallyBlocks()+cn.getCi());
								tcs.get(path).setFINALLY_MB_TotalMissedBrachesFinallyBlocks(tcs.get(path).getFINALLY_MB_TotalMissedBrachesFinallyBlocks()+cn.getMb());
								tcs.get(path).setFINALLY_CB_TotalCoveredBrachesFinallyBlocks(tcs.get(path).getFINALLY_CB_TotalCoveredBrachesFinallyBlocks()+cn.getCb());
							}else {
								System.out.println("NÃO ENTROU FINNALY");
							}
							
						}*/
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
		
		System.out.close();
				
		//Util.writeCsvFileEstatistics2(resultCoverageTotal);
		UtilWriteReader.writeCsvFileEstatisticsCoveraveTypeCode(tcs);
		
		if(saveOutputInFile)	
			System.out.close();
	}


	private static List<CtThrow> findRaisers(boolean isProgramerDefined, CtType<?> element) {

		List<CtThrow> result = new ArrayList<CtThrow>();
		for (CtThrow raiser : element.getElements(new TypeFilter<CtThrow>(CtThrow.class))) {
			CtType<?> declaredType = raiser.getThrownExpression().getType().getTypeDeclaration();

			if (isProgramerDefined) {
				if (declaredType != null && !declaredType.isShadow()) {
					result.add(raiser);
				}
			} else {
				if (declaredType == null || declaredType.isShadow()) {
					result.add(raiser);
				}
			}
		}
		return result;
	}


	private static List<CtCatch> findHandlers(boolean isProgramerDefined, CtType<?> element) {

		List<CtCatch> result = new ArrayList<CtCatch>();
		for (CtCatch handler : element.getElements(new TypeFilter<CtCatch>(CtCatch.class))) {
			for (CtTypeReference<?> exceptionType : handler.getParameter().getMultiTypes()) {
				CtType<?> declaredType = exceptionType.getTypeDeclaration();

				if (isProgramerDefined) {
					if (declaredType != null && !declaredType.isShadow()) {
						result.add(handler);
						break;
					}
				} else {
					if (declaredType == null || declaredType.isShadow()) {
						result.add(handler);
						break;
					}
				}
			}
		}
		return result;
	}
}