package ufc.br.mutant_project.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import com.thoughtworks.xstream.XStream;

import edu.emory.mathcs.backport.java.util.Arrays;
import spoon.MavenLauncher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import ufc.br.mutant_project.constants.Jacoco;
import ufc.br.mutant_project.constants.PathProject;
import ufc.br.mutant_project.constants.Processors;
import ufc.br.mutant_project.exceptions.CloneRepositoryException;
import ufc.br.mutant_project.exceptions.JacocoException;
import ufc.br.mutant_project.exceptions.PomException;
import ufc.br.mutant_project.exceptions.TestFailMavenInvokerException;
import ufc.br.mutant_project.models.CHE;
import ufc.br.mutant_project.models.CoverageResult;
import ufc.br.mutant_project.models.FinalResultSavedByProject;
import ufc.br.mutant_project.models.Properties;
import ufc.br.mutant_project.models.ResultsStatisticsLength;
import ufc.br.mutant_project.models.TesteProject;
import ufc.br.mutant_project.models.TotalCoveredStatus;

public class Util {

	private static MavenLauncher launcher;
    private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";
	private static InvocationResult result;
	private static Properties properties;

	//MÉTODO UTILIZADO PARA REALIZAR O TESTE NO MUTANTE CRIADO
    @SuppressWarnings("deprecation")
	public static int invoker(String copyProjectPath, List<String> submodules, boolean showInConsole) {
    		result = null;
			final Invoker invoker = new DefaultInvoker();
			try {
				invoker.setMavenHome( new File( getProperties().getHomeMaven() ) );
			} catch (IOException e1) {
				System.err.println("Não foi possível obter o arquivo propierties, tente novamente.");
				e1.printStackTrace();
				return -1;
			}
			
			if(!showInConsole)
				invoker.setOutputHandler(new InvocationOutputHandler() {
					public void consumeLine(String arg0) {
					}
				});
			
			final InvocationRequest request = new DefaultInvocationRequest();
			request.setPomFile( new File( copyProjectPath ) );
			request.setGoals( Arrays.asList(new String[] {"test", "-Dcheckstyle.skip"}) );
			request.setDebug(false);
			
			if(submodules!=null)
				request.setProjects(submodules);
			
			Thread t = new Thread(new Runnable() {
				public void run() {
					try {
						result = invoker.execute( request );
					} catch (MavenInvocationException e) {
						e.printStackTrace();
					}
				}
			});
			
			t.start();
			
			for (int i = 0; i < (60*10*2); ++i) {
				
			    try { Thread.sleep (500); } catch (InterruptedException ex) {}
			    
			    if(result!=null) {
			    	return result.getExitCode();
			    }
			    if(i == ((60*10*2)-1)) {
			    	t.interrupt();
			    	t.stop();
			    	System.out.println("T: "+t.isAlive());
			    	return -1;
			    }
			}
			    
			if(result!=null)
				return result.getExitCode();
			else
				return -1;
    }
    
    //MÉTODO UTILIZADO PARA CRIAR AS PASTAS QUE O PROJETO NECESSITA PARA O FUNCIONAMENTO
    public static boolean preparePathInit() {
    	File f = new File(PathProject.USER_REFERENCE_TO_PROJECT);
    	if(!f.exists()) {
    		return f.mkdir();
    	}
    	return true;
    }
    
    //MÉTODO UTILIZADO PARA REALIZAR O OBJETIVO MAVEN NO PROJETO
    public static int invokerOthers(String copyProjectPath, List<String> goals, List<String> submodules, boolean showInConsole) {
    	try {
			Invoker invoker = new DefaultInvoker();
			
			try {
				invoker.setMavenHome( new File( getProperties().getHomeMaven() ) );
			} catch (IOException e1) {
				System.out.println("Não foi possível obter o arquivo propierties, tente novamente.");
				e1.printStackTrace();
				return -1;
			}
			
			if(!showInConsole)
				invoker.setOutputHandler(new InvocationOutputHandler() {
					public void consumeLine(String arg0) {
					}
				});
			
			InvocationRequest request = new DefaultInvocationRequest();
			request.setPomFile( new File( copyProjectPath ) );
			request.setGoals( goals );
//			request.setLocalRepositoryDirectory(new File(copyProjectPath));
			
			if(submodules!=null)
			request.setProjects(submodules);
			
			InvocationResult result = invoker.execute( request );
			return result.getExitCode();
			
		} catch (MavenInvocationException e) {
			e.printStackTrace();
			return 0;
		}
    }
   
    //MÉTODO UTILIZADO PARA VERIFICAR A URL DO GIT E OBTER O NOME DO REPOSITÓRIO DA MESMA
    public static String validateAndGetNameRepository(String uri) {
    	if(!uri.matches("(.+@)*([\\w\\d\\.]+):(.*)"))
			return null;
		
    	return uri.substring(uri.lastIndexOf("/")+1, uri.lastIndexOf(".git"));
    }
    
    //MÉTODO UTILIZADO PARA REALIZAR O OBJETIVO MAVEN NO PROJETO
    public static List<String> listProjects(String name) throws FileNotFoundException {
    	if(name==null) {
    		name = "repositories.txt";
    	}
		Stream<String> stream;
		final List<String> list = new ArrayList<String>();
		try {
			stream = Files.lines(Paths.get(name));
			stream.forEach(new Consumer<String>() {
				public void accept(String t) {
					list.add(t);
				}
			});
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new FileNotFoundException();
		}
	      return list;
    }
    
    //MÉTODO UTILIZADO PARA CLONAR O REPOSITÓRIO GIT
    public static void 	cloneRepository(String uri, String directory, String commit) throws CloneRepositoryException {
    	File directoryF = new File(PathProject.makePathToProject(directory));
    	if(directoryF.exists()) {
			try {
				FileUtils.deleteDirectory(directoryF);
			} catch (IOException e1) {
				e1.printStackTrace();
				throw new CloneRepositoryException();
			}
    	}
    	
    	try {
    		Git g = Git.cloneRepository()
			  .setURI( uri )
			  .setDirectory( new File(PathProject.makePathToProjectMaven(directory, null)) )
			  .call();

    		if(commit!=null)
    			g.checkout().setCreateBranch( true ).setName( commit ).setStartPoint( commit ).call();
    		
		} catch (InvalidRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new CloneRepositoryException();
		} catch (TransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new CloneRepositoryException();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new CloneRepositoryException();
		}
    }
	
    //MÉTODO UTILIZADO PARA CRIAR A CÓPIA DO PROJETO NA QUAL SERÁ O MUTANTE
    public static void createACopyMutantTest(String toPath, String projectPath){
    	File source = new File(projectPath);
    	File dest = new File(toPath);
    	try {
    		System.out.println("Gerando copia do arquivo...");
    	    FileUtils.copyDirectory(source, dest);
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    }
   
    //MÉTODO UTILIZADO PARA CRIAR A CÓPIA DO PROJETO NA QUAL SERÁ O MUTANTE
    public static void copyOutputSpoonToProject(String toPath){
    	File source = new File(PathProject.PROJECT_PATH_TEMP);
    	File dest = new File(toPath);
    	try {
    		System.out.println("Gerando copia do arquivo... copyOutputSpoonToProject toPath: "+toPath+" fromPath: "+PathProject.PROJECT_PATH_TEMP);
    	    FileUtils.copyDirectory(source, dest);
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    }
    
    //MÉTODO UTILIZADO PARA CRIAR A CÓPIA DO PROJETO NA QUAL SERÁ O MUTANTE
    public static void clearOutputSpoonToProject(){
    	File source = new File(PathProject.PROJECT_PATH_TEMP);
    	try {
    		System.out.println("Limpando copyOutputSpoonToProject");
    		FileUtils.deleteDirectory(source);
    		FileUtils.forceMkdir(source);
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    }
    
    //MODELO DO PROJETO
    public static CtModel getModel(String projectPath) {
    	try {
			launcher = new MavenLauncher(projectPath, MavenLauncher.SOURCE_TYPE.APP_SOURCE);
			launcher.getEnvironment().setAutoImports(false);
//			launcher.getEnvironment().setCommentEnabled(true);
			launcher.getEnvironment().setPreserveLineNumbers(true);
			launcher.getEnvironment().setNoClasspath(true);
			launcher.buildModel();
			return launcher.getModel();
    	}catch(Exception e) {
    		return null;
    	}
	}
    
    //OBTENDO CLASSE PROCESSADA DO PROJETO COMPILADA COM O MAVEN LAUNCHER
    public static CtType<?> getClassByModel(String name, String projectPath){
    	for(CtType<?> ty : getModel(projectPath).getAllTypes()) {
    		if(ty instanceof CtClass<?>) {
    			CtClass<?> ctc = (CtClass<?>) ty;
	    		//System.out.println("OPA: "+ctc.getQualifiedName());
	    		if(ctc.getQualifiedName().equals(name))
	    			return ty;
    		}
    	}
    	return null;
    }
    
    //CRIAÇÃO DA ARVORE	DE EXCEÇÕES
    public static CHE getCHE(final CtTry blockTry) {
		CHE chTemp = new CHE();
		
		for(CtCatch ctc : blockTry.getCatchers()) {
			try {
				chTemp.addCtTypeReference(ctc.getParameter().getType());	
			}catch(Exception e){}
		}
		
		for(CtTry inv : blockTry.getBody().getElements(new Filter<CtTry>() {
			public boolean matches(CtTry element) {
				return element.getParent().getParent().equals(blockTry);
			}
		})) {
			chTemp.addFilho(getCHE(inv));
		}
		
		for(final CtInvocation<?> inv : blockTry.getBody().getElements(new Filter<CtInvocation<?>>() {
			public boolean matches(CtInvocation<?> element) {
				return element.getParent().getParent().equals(blockTry);
			}
		})) {
			
			CHE cchild = new CHE();
			boolean adicionou = false;
			
			if(inv.getExecutable()!=null && inv.getExecutable().getDeclaration()!=null && inv.getExecutable().getDeclaration().getBody()!=null)
				for(CtThrow cf : inv.getExecutable().getDeclaration().getBody().getElements(new Filter<CtThrow>() {
					public boolean matches(CtThrow element) {
						/*System.out.println("----------------------------");
						System.out.println("Elemento: "+element);
						System.out.println("Pai de cima: " + inv.getExecutable().getDeclaration());
						System.out.println("Pai: " + element.getParent(new Filter<CtInvocation<?>>() {
							public boolean matches(CtInvocation<?> element) {
								return true;
							}
						}));
						System.out.println("----------------------------");
						return element.getParent(new Filter<CtInvocation<?>>() {
							public boolean matches(CtInvocation<?> element) {
								return true;
							}
						}).equals(inv.getExecutable().getDeclaration());
						*/
						return true;
					}
				})) {
					adicionou = true;
					cchild.addCtTypeReference(cf.getThrownExpression().getType());
				}
			if(inv.getExecutable()!=null && inv.getExecutable().getDeclaration()!=null && inv.getExecutable().getDeclaration().getBody()!=null)
				for(CtTry cf : inv.getExecutable().getDeclaration().getBody().getElements(new Filter<CtTry>() {
					public boolean matches(CtTry element) {
						return element.getParent().getParent().equals(inv.getExecutable().getDeclaration());
					}
				})) {
					adicionou = true;
					cchild.addFilho(getCHE(cf));
				}
			
			if(adicionou)
				chTemp.addFilho(cchild);
		}

		return chTemp;
	}
    
    //MÉTODO UTILIZADO PARA GERAR A LISTA DE CLASSES NECESSÁRIAS PARA ADICIONAR NO TRY{} ...
    private static void generateListOfCatchersToAdd(List<CtTypeReference<?>> lista, CHE che) {
    	if(che.getClasses()!=null)
    		for(CtTypeReference<?> ctr : che.getClasses())
    			if(!lista.contains(ctr))
    				lista.add(ctr);
    	
    	if(che.getFilhos()!=null)
    		for(CHE c: che.getFilhos())
    			generateListOfCatchersToAdd(lista, c);
    }
    
    //MÉTODO QUE TRANSFORMA A ÁRVORE CHE EM LISTA
    public static List<CtTypeReference<?>> getListOfCatchersToAdd(CHE che){
    	List<CtTypeReference<?>> lista = new ArrayList<CtTypeReference<?>>();
    	generateListOfCatchersToAdd(lista, che);
    	if(che.getClasses()!=null)
    		for(CtTypeReference<?> cth : che.getClasses())
    			lista.remove(cth);
    	return lista;
    }
    
    //MÉTODO QUE RETORNA OS TIPOS DIRETOS DE EXCEÇÕES DA ÁRVORE DE EXCEÇÕES
    public static List<CtTypeReference<?>> getListOfDirectDerivedTypes(CHE che){
    	List<CtTypeReference<?>> lista = new ArrayList<CtTypeReference<?>>();
    	if(che!=null && che.getFilhos()!=null)
    		for(CHE fi : che.getFilhos())
    			if(fi.getClasses()!=null)
    				for(CtTypeReference<?> ctr : fi.getClasses())
    					if(!lista.contains(ctr))
    						lista.add(ctr);
    	return lista;
    }
	
    //MÉTODO QUE CRIA O CABEÇALHO DO CSV PARA CADA PROJETO
	private static String createFileHeaderByProject() {
		return "typeMutation,nameMutation,numberOfMutants,amountOfLiveMutants,amountOfDeadMutants,"
				+ "fractionOfMutantsKilledByNumberOfMutants,"
				+ ""
				+ "numberOfMutantsOfProject,amountOfLiveMutantsOfProject,amountOfDeadMutantsOfProject,fractionOfMutantsKilledByNumberOfMutantsOfProject";
		
	}
	
	//MÉTODO QUE CRIA O CABEÇALHO DO CSV PARA CADA PROJETO
	private static String createFileHeaderStatistic2() {
		return "PROJECT,CLASS NAME,LINE NUMBER,TYPE,COVERAGED,MI,CI,MB,CB,MM,CM,BRANCH OR NSTRUCTION";
	}
	
	//MÉTODO QUE CRIA O CABEÇALHO DO CSV PARA STATISTICS LENGTH
		private static String createFileHeaderStatisticLength() {
			return "nameProject^numTryBlock^numCatchBlock^numFinallyBlock^numClass^numLineCode";
		}
	
	//MÉTODO QUE CRIA O CABEÇALHO DO CSV PARA CADA PROJETO
		private static String createFileHeaderTestsProjects() {
			return "NAME URL COMMIT PASS_TESTS JACOCO_GENERATE";
		}
	
	//MÉTODO QUE CRIA O CABEÇALHO DO CSV PARA CADA PROJETO
	private static String createFileHeaderStatistic2Status() {
		return    "PROJECT,MI,CI,MB,CB,MM,CM,"
				+ "TRY_MI,TRY_CI,TRY_MB,TRY_CB,"
				+ "CATCH_MI,CATCH_CI,CATCH_MB,CATCH_CB,"
				+ "FINALLY_MI,FINALLY_CI,FINALLY_MB,FINALLY_CB,"
				+ "THROW_MI,THROW_CI,"
				+ "THROWS_MM,THROWS_CM";
	}
	
	//MÉTODO QUE CRIA O CABEÇALHO DO CSV PARA CADA PROJETO
		private static String createFileHeaderStatistic2StatusTotal() {
			return    "PROJECT^MI^CI^MB^CB^MM^CM^MC^CC";
		}
	
	public static void writeCsvFileEstatistics2Total(Map<String, TotalCoveredStatus> totalCoveredStatus) {
		FileWriter fileWriter = null;
		
		try {
			fileWriter = new FileWriter(PathProject.USER_REFERENCE_TO_PROJECT+"finalResultForProjectStatusOnlyTotalStatus.csv");

			fileWriter.append(createFileHeaderStatistic2StatusTotal());
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			for(String project: totalCoveredStatus.keySet()) {
				fileWriter.append(project+"^");
				
				fileWriter.append(totalCoveredStatus.get(project).getMI_TotalMissedInstructions()+"^");
				fileWriter.append(totalCoveredStatus.get(project).getCI_TotalCoveredInstructions()+"^");
				fileWriter.append(totalCoveredStatus.get(project).getMB_TotalMissedBraches()+"^");
				fileWriter.append(totalCoveredStatus.get(project).getCB_TotalCoveredBraches()+"^");
				fileWriter.append(totalCoveredStatus.get(project).getMM_TotalMissedMethods()+"^");
				fileWriter.append(totalCoveredStatus.get(project).getCM_TotalCoveredMethods()+"^");
				fileWriter.append(totalCoveredStatus.get(project).getMC_TotalMissedClasses()+"^");
				fileWriter.append(totalCoveredStatus.get(project).getCC_TotalCoveredClasses()+"^");
				
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
			}
			
		}
	}

	public static void writeCsvFileEstatistics2(Map<String, TotalCoveredStatus> totalCoveredStatus) {
		FileWriter fileWriter = null;
		
		try {
			fileWriter = new FileWriter(PathProject.USER_REFERENCE_TO_PROJECT+"finalResultForProjectStatus.csv");

			fileWriter.append(createFileHeaderStatistic2Status());
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			for(String project: totalCoveredStatus.keySet()) {
				fileWriter.append(project+",");
				
				fileWriter.append(totalCoveredStatus.get(project).getMI_TotalMissedInstructions()+",");
				fileWriter.append(totalCoveredStatus.get(project).getCI_TotalCoveredInstructions()+",");
				fileWriter.append(totalCoveredStatus.get(project).getMB_TotalMissedBraches()+",");
				fileWriter.append(totalCoveredStatus.get(project).getCB_TotalCoveredBraches()+",");
				fileWriter.append(totalCoveredStatus.get(project).getMM_TotalMissedMethods()+",");
				fileWriter.append(totalCoveredStatus.get(project).getCM_TotalCoveredMethods()+",");
				
				fileWriter.append(totalCoveredStatus.get(project).getTRY_MI_TotalMissedInstructionsTryBlocks()+",");
				fileWriter.append(totalCoveredStatus.get(project).getTRY_CI_TotalCoveredInstructionsTryBlocks()+",");
				fileWriter.append(totalCoveredStatus.get(project).getTRY_MB_TotalMissedBrachesTryBlocks()+",");
				fileWriter.append(totalCoveredStatus.get(project).getTRY_CB_TotalCoveredBrachesTryBlocks()+",");
				
				fileWriter.append(totalCoveredStatus.get(project).getCATCH_MI_TotalMissedInstructionsCatchBlocks()+",");
				fileWriter.append(totalCoveredStatus.get(project).getCATCH_CI_TotalCoveredInstructionsCatchBlocks()+",");
				fileWriter.append(totalCoveredStatus.get(project).getCATCH_MB_TotalMissedBrachesCatchBlocks()+",");
				fileWriter.append(totalCoveredStatus.get(project).getCATCH_CB_TotalCoveredBrachesCatchBlocks()+",");
				
				fileWriter.append(totalCoveredStatus.get(project).getFINALLY_MI_TotalMissedInstructionsFinallyBlocks()+",");
				fileWriter.append(totalCoveredStatus.get(project).getFINALLY_CI_TotalCoveredInstructionsFinallyBlocks()+",");
				fileWriter.append(totalCoveredStatus.get(project).getFINALLY_MB_TotalMissedBrachesFinallyBlocks()+",");
				fileWriter.append(totalCoveredStatus.get(project).getFINALLY_CB_TotalCoveredBrachesFinallyBlocks()+",");
				
				fileWriter.append(totalCoveredStatus.get(project).getTHROW_MI_TotalMissedInstructionsThrowStatements()+",");
				fileWriter.append(totalCoveredStatus.get(project).getTHROW_CI_TotalCoveredInstructionsThrowStatements()+",");
				fileWriter.append(totalCoveredStatus.get(project).getTHROWS_MM_TotalMissedMethodsWithThrows()+",");
				fileWriter.append(totalCoveredStatus.get(project).getTHROWS_CM_TotalCoveredMethodsWithThrows()+",");

				fileWriter.append(NEW_LINE_SEPARATOR);
			}
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
			}
			
		}
	}
	
	public static void writeCsvFileEstatisticsLength(List<ResultsStatisticsLength> listResultsStatisticsLength) {
		FileWriter fileWriter = null;
		
		try {
			fileWriter = new FileWriter(PathProject.USER_REFERENCE_TO_PROJECT+"ResultsStatisticsLength.csv");

			fileWriter.append(createFileHeaderStatisticLength());
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			for(ResultsStatisticsLength cr: listResultsStatisticsLength) {
				fileWriter.append(cr.toStringCSV());
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
			}
			
		}
	}
	
	public static void writeCsvFileEstatistics2(List<CoverageResult> resultCoverage) {
		FileWriter fileWriter = null;
		
		try {
			fileWriter = new FileWriter(PathProject.USER_REFERENCE_TO_PROJECT+"finalResultForProject.csv");

			fileWriter.append(createFileHeaderStatistic2());
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			for(CoverageResult cr: resultCoverage) {
				fileWriter.append(cr.toStringCSV());
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
			}
			
		}
	}
	
	public static void writeCsvFileTests(List<TesteProject> testsProjects) {
		FileWriter fileWriter = null;
		
		try {
			fileWriter = new FileWriter(PathProject.USER_REFERENCE_TO_PROJECT+"result.csv");

			fileWriter.append(createFileHeaderTestsProjects());
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			for(TesteProject cr: testsProjects) {
				fileWriter.append(cr.toCSV());
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
			}
			
		}
	}
	
	public static List<TesteProject> readerCsvFileTests() {
		FileReader fileReader = null;
		
		try {
			fileReader = new FileReader(PathProject.USER_REFERENCE_TO_PROJECT+"result.csv");
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			List<String> linhas = new ArrayList<>();
			
			
			bufferedReader.readLine();
			String linha = bufferedReader.readLine();
			while(linha != null) {
				linhas.add(linha);
				linha = bufferedReader.readLine();
			}
			
			List<TesteProject> listaProjetosAnalisados = new ArrayList<>();
			
			for(int i=0; i < linhas.size(); i++) {
				String[] divisoes = linhas.get(i).split(" ");
				boolean test = false;
				boolean jacoco = false;
				
				if(divisoes[3].equals("true"))
					test = true;
				
				if(divisoes[4].equals("true"))
					jacoco = true;
				
				listaProjetosAnalisados.add(new TesteProject(test, jacoco, divisoes[0], divisoes[1], divisoes[2]));
				System.out.println("Mee: " + listaProjetosAnalisados.get(listaProjetosAnalisados.size()-1));
			}
			bufferedReader.close();
			return listaProjetosAnalisados;
			
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
    
	public static void writeCsvFileByProject(String uriName, Map<String, List<FinalResultSavedByProject>> map) {
		FileWriter fileWriter = null;
				
		try {
			fileWriter = new FileWriter(PathProject.makePathToProject(uriName)+"finalResultForProject.csv");

			fileWriter.append(createFileHeaderByProject());
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			List<FinalResultSavedByProject> list = map.get(uriName);
			
			int numberOftotalMutants = 0;
			int amountOfLiveMutants = 0;
			int amountOfDeadMutants = 0;
			
			for (FinalResultSavedByProject finalr : list) {
				numberOftotalMutants += finalr.getNumberOfMutants();
				amountOfLiveMutants += finalr.getAmountOfLiveMutants();
				amountOfDeadMutants += finalr.getAmountOfDeadMutants();
			}
			
			boolean ft = true;
			
			Double fractionOfMutantsKilledByNumberOfMutants = Double.parseDouble(amountOfDeadMutants+"") / Double.parseDouble(numberOftotalMutants+"");
			
			for (FinalResultSavedByProject finalr : list) {
				fileWriter.append(finalr.getAbbreviationMutationType());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(finalr.getMutationType());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(String.valueOf(finalr.getNumberOfMutants()));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(String.valueOf(finalr.getAmountOfLiveMutants()));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(String.valueOf(finalr.getAmountOfDeadMutants()));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(String.valueOf(finalr.getFractionOfMutantsKilledByNumberOfMutants()));
				
				if(ft) {
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(String.valueOf(numberOftotalMutants));
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(String.valueOf(amountOfLiveMutants));
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(String.valueOf(amountOfDeadMutants));
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(fractionOfMutantsKilledByNumberOfMutants+"");
					ft = false;
				}
				
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			
			System.out.println("CSV file was created successfully !!!");
			
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
			}
			
		}
	}
	
	//MÉTODO QUE CRIA O CABEÇALHO DO CSV PARA CADA PROJETO
	public static String createFileHeaderByAllProjects() {
		
		String header = "project,";
		
		for(String name: Processors.typeProcessors)
			header += "numberOfMutants ("+name+"),amountOfLiveMutants ("+name+"),amountOfDeadMutants ("+name+"),fractionOfMutantsKilledByNumberOfMutants ("+name+"),";
					
		header += "numberOfMutantsTotal,amountOfLiveMutantsTotal,amountOfDeadMutantsTotal,totalfractionOfMutantsKilledByNumberOfMutantsThisProject";
		header += ",numberOfMutantsOfAllProjects,amountOfLiveMutantsOfAllProjects,amountOfDeadMutantsOfAllProjects,fractionOfMutantsKilledByNumberOfMutantsOfAllProjects";
		return header;
	}
	
	public static void writeCsvFileByAllProjects(Map<String, List<FinalResultSavedByProject>> map) {
		FileWriter fileWriter = null;
				
		try {
			fileWriter = new FileWriter(PathProject.USER_REFERENCE_TO_PROJECT+"finalResult.csv");

			fileWriter.append(createFileHeaderByAllProjects());
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			int numberOftotalMutantsAllprojects = 0;
			int amountOfLiveMutantsAllprojects = 0;
			int amountOfDeadMutantsAllprojects = 0;
			
			for(String key : map.keySet()) {
				for (FinalResultSavedByProject finalr : map.get(key)) {
					numberOftotalMutantsAllprojects += finalr.getNumberOfMutants();
					amountOfLiveMutantsAllprojects += finalr.getAmountOfLiveMutants();
					amountOfDeadMutantsAllprojects += finalr.getAmountOfDeadMutants();
				}
			}
			
			boolean ft = true;
			
			Double fractionOfMutantsKilledByNumberOfMutantsAllprojects = Double.parseDouble(amountOfDeadMutantsAllprojects+"") / Double.parseDouble(numberOftotalMutantsAllprojects+"");
			
			for(String key: map.keySet()) {
				List<FinalResultSavedByProject> list = map.get(key);
				fileWriter.append(key);
				int numberMutantsTotal = 0;
				int amountOfLiveMutantsTotal = 0;
				int amountOfDeadMutantsTotal = 0;
				boolean allMutantsProcess = true;
				for (String ty : Processors.typeProcessors) {
					FinalResultSavedByProject finalr = getFinalResultSavedByProjectByTypeMutant(ty, list);
					if(finalr==null) {
						System.out.println("Projeto "+key+" não tem processado o mutant: "+ty);
						
						fileWriter.append(COMMA_DELIMITER);
						fileWriter.append("0");
						fileWriter.append(COMMA_DELIMITER);
						fileWriter.append("0");
						fileWriter.append(COMMA_DELIMITER);
						fileWriter.append("0");
						fileWriter.append(COMMA_DELIMITER);
						fileWriter.append("0");
						continue;
					}
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(String.valueOf(finalr.getNumberOfMutants()));
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(String.valueOf(finalr.getAmountOfLiveMutants()));
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(String.valueOf(finalr.getAmountOfDeadMutants()));
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(String.valueOf(finalr.getFractionOfMutantsKilledByNumberOfMutants()));
					numberMutantsTotal+=finalr.getNumberOfMutants();
					amountOfLiveMutantsTotal+=finalr.getAmountOfLiveMutants();
					amountOfDeadMutantsTotal+=finalr.getAmountOfDeadMutants();
					
				}
				if(allMutantsProcess) {
					Double fractionOfMutantsKilledByNumberOfMutantsTotal = Double.parseDouble(amountOfDeadMutantsTotal+"") / Double.parseDouble(numberMutantsTotal+"");
					
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(String.valueOf(numberMutantsTotal));
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(String.valueOf(amountOfLiveMutantsTotal));
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(String.valueOf(amountOfDeadMutantsTotal));
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(fractionOfMutantsKilledByNumberOfMutantsTotal+"");
				}
				if(ft && allMutantsProcess) {
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(String.valueOf(numberOftotalMutantsAllprojects));
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(String.valueOf(amountOfLiveMutantsAllprojects));
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(String.valueOf(amountOfDeadMutantsAllprojects));
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(fractionOfMutantsKilledByNumberOfMutantsAllprojects+"");
					ft = false;
					fileWriter.append(NEW_LINE_SEPARATOR);
					continue;
				}
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			
			System.out.println("CSV file to all project was created successfully !!!");
			
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter to all project !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter to all project !!!");
                e.printStackTrace();
			}
			
		}
	}
	
	public static FinalResultSavedByProject getFinalResultSavedByProjectByTypeMutant(String type, List<FinalResultSavedByProject> list) {
		for(FinalResultSavedByProject fr: list) {
			if(fr.getAbbreviationMutationType().equals(type))
				return fr;
		}
		return null;
	}
	
	public static Properties getProperties() throws IOException {
		if(properties!=null)
			return properties;
		java.util.Properties prop = geFileProp();
		properties = new Properties();
		properties.setHomeMaven(prop.getProperty("homeMaven", null));
		return properties;
	}
	
	private static java.util.Properties geFileProp() throws IOException {
		java.util.Properties props = new java.util.Properties();
		FileInputStream file = new FileInputStream(
				"config.properties");
		props.load(file);
		return props;
	}
	
	public static void createReportJaCoCo(String pathToProject, String submodule) throws PomException, JacocoException, TestFailMavenInvokerException {
		System.out.println("Modificando o POM do projeto.");

		modifyPomToJaCoCo(pathToProject);
		
		System.out.println("Realizando a cobertura do projeto.");
		
		int result = 1;
		if(submodule!=null)
			result = invokerOthers(pathToProject, Collections.singletonList("test"), Collections.singletonList(submodule), true);
		else
			result = invokerOthers(pathToProject, Collections.singletonList("test"), null, true);
		
		if(result==0) {
			System.out.println("Coverage JaCoCo realizado com sucesso!");
		}else {
			System.out.println("Os tests realizados para a criação do report após a modificação do POM falharam. Verifique se o projeto está buildando corretamente.");
			System.out.println("Realizando apenas o report (jacoco:report)");
			if(submodule!=null)
				result = invokerOthers(pathToProject, Collections.singletonList("jacoco:report"), Collections.singletonList(submodule), true);
			else
				result = invokerOthers(pathToProject, Collections.singletonList("jacoco:report"), null, true);
			
			if(result!=0)
				throw new TestFailMavenInvokerException("Os tests realizados para a criação do report após a modificação do POM falharam. Verifique se o projeto está buildando corretamente.");
			else
				System.out.println("Coverage JaCoCo realizado com sucesso!");
		}
	}
	
	public static String getSourceDirectory(String pathToProject) throws PomException {
	    MavenXpp3Reader reader = new MavenXpp3Reader();
	    Model model;
	    File f = new File(pathToProject+"pom.xml");
	    if (f.exists()) {
	    	FileReader fr;
			try {
				fr = new FileReader(f);
				model = reader.read(fr);
				
				if(model.getBuild() != null && model.getBuild().getSourceDirectory()!=null)
					return model.getBuild().getSourceDirectory();
			
				return PathProject.PROJECT_PATH_FILES_DEFAULT;
			} catch (IOException e) {
				e.printStackTrace();
				throw new PomException("Falha na leitura ou escrita do arquivo pom.xml");
			} catch (XmlPullParserException e) {
				e.printStackTrace();
				throw new PomException("Falha na leitura do arquivo pom.xml");
			}
	    }else
	    	throw new PomException("Pom.xml não encontrado para o projeto: "+pathToProject);
	 }
	
	private static void modifyPomToJaCoCo(String pathToProject) throws PomException, JacocoException {
	    MavenXpp3Reader reader = new MavenXpp3Reader();
	    Model model;
	    File f = new File(pathToProject+"pom.xml");
	    if (f.exists()) {
	    	FileReader fr;
			try {
				fr = new FileReader(f);
				model = reader.read(fr);
				
			    for(int i=0; i < model.getBuild().getPlugins().size(); i++)
			    	if(model.getBuild().getPlugins().get(i).getArtifactId().equals(Jacoco.ARTIFACT_ID_JACOCO) &&
			    			model.getBuild().getPlugins().get(i).getGroupId().equals(Jacoco.GROUP_ID_JACOCO)) {
			    		model.getBuild().getPlugins().remove(i);
			    		break;
			    	}
			    
			    model.getBuild().addPlugin(generatePluginJaCoCo());

			    MavenXpp3Writer pomWriter = new MavenXpp3Writer();
			    pomWriter.write(new FileWriter(new File(pathToProject+"pom.xml")), model);
			} catch (IOException e) {
				e.printStackTrace();
				throw new JacocoException("Falha na leitura ou escrita do arquivo pom.xml");
			} catch (XmlPullParserException e) {
				e.printStackTrace();
				throw new JacocoException("Falha na leitura do arquivo pom.xml");
			}
	    }else
	    	throw new PomException("Pom.xml não encontrado para o projeto: "+pathToProject);
	  }
	
	public static boolean createXmlListSaveMutantResultType(String projectPath, Map<String, List<FinalResultSavedByProject>> listSaveMutantResultType) {
		XStream xs = new XStream();
		try {
			xs.toXML(listSaveMutantResultType, new FileOutputStream(projectPath+"filesResultsMutants.xml"));
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static Map<String, List<FinalResultSavedByProject>> getListSaveMutantResultTypeFromXml(String projectPath) {
		XStream xs = new XStream();
		try {
			return (Map<String, List<FinalResultSavedByProject>>) xs.fromXML(new FileInputStream(projectPath+"filesResultsMutants.xml"));
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			return null;
		}
	}
	
	private static Plugin generatePluginJaCoCo() {
		Plugin p = new Plugin();
		p.setGroupId(Jacoco.GROUP_ID_JACOCO);
		p.setArtifactId(Jacoco.ARTIFACT_ID_JACOCO);
		p.setVersion(Jacoco.VERSION_JACOCO);
		PluginExecution pe = new PluginExecution();
		pe.addGoal("prepare-agent");
		PluginExecution pe2 = new PluginExecution();
		pe2.addGoal("report");
		pe2.setPhase("test");
		pe2.setId("report");
		p.getExecutions().add(pe);
		p.getExecutions().add(pe2);
		return p;
	 }
}
