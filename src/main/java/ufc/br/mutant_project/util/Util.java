package ufc.br.mutant_project.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProgressListener;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.ResultHandler;

import com.thoughtworks.xstream.XStream;

import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.SpoonAPI;
import spoon.compiler.Environment;
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
import ufc.br.mutant_project.constants.Maven;
import ufc.br.mutant_project.constants.PathProject;
import ufc.br.mutant_project.exceptions.CloneRepositoryException;
import ufc.br.mutant_project.exceptions.JacocoException;
import ufc.br.mutant_project.exceptions.PomException;
import ufc.br.mutant_project.exceptions.TestFailMavenInvokerException;
import ufc.br.mutant_project.models.CHE;
import ufc.br.mutant_project.models.FinalResultSavedByProject;
import ufc.br.mutant_project.models.Properties;

public class Util {

	private static MavenLauncher launcher;
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
				System.err.println("Não foi possível obter o arquivo properties, tente novamente.");
				e1.printStackTrace();
				return -1;
			}
			
			if(!showInConsole)
				invoker.setOutputHandler(new InvocationOutputHandler() {
					public void consumeLine(String arg0) {
					}
				});
			
			final InvocationRequest request = new DefaultInvocationRequest();
			request.setBaseDirectory( new File( copyProjectPath ) );
			request.setGoals( Maven.GOALS_PROJECT_DISABLE_CHECK );
			request.setDebug(false);
			
			if(submodules!=null) {
				request.setProjects(submodules);
				System.out.println("Realizando os testes dentro dos submodulos: "+submodules);
			}
			Thread t = new Thread(() -> {
				try {
					result = invoker.execute( request );
				} catch (MavenInvocationException e) {
					e.printStackTrace();
				}
			});

			t.start();
			
			for (int i = 0; i < (60*10*2); ++i) {
				
			    try { Thread.sleep (500); } catch (InterruptedException ex) {}
			    
			    if(result!=null)
			    	return result.getExitCode();

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

	//MÉTODO UTILIZADO PARA REALIZAR O TESTE NO MUTANTE CRIADO EM PROJETOS GRADLE
	public static int invokerGradle(String copyProjectPath, List<String> submodules, boolean showInConsole) {
		String GRADLE_TASK = "test";
		final int[] result = new int[1];
		GradleConnector connector;
		connector = GradleConnector.newConnector();
		//connector.useInstallation(new File(gradleInstallationDir));
		connector.forProjectDirectory(new File(copyProjectPath));

		ProjectConnection connection = connector.connect();
		BuildLauncher build = connection.newBuild();
		build.addProgressListener((ProgressListener) progressEvent -> System.out.println(progressEvent.getDescription()));

		if (showInConsole) {
			build.setStandardOutput(System.out);
			build.setStandardError(System.out);
		}

		build.forTasks(GRADLE_TASK);
		build.run(new ResultHandler<Void>() {
			@Override
			public void onComplete(Void aVoid) {
				result[0] = 0;
			}

			@Override
			public void onFailure(GradleConnectionException e) {
				result[0] = -1;
			}
		});

		connection.close();

		return result[0];
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
			request.setBaseDirectory( new File( copyProjectPath ) );
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
    
    //MÉTODO UTILIZADO PARA LISTAR OS PROJETOS QUE ESTÃO NO ARQUIVO
    public static List<String> listProjects(String name) throws FileNotFoundException {
    	if(name==null) {
    		name = "repositories.txt";
    	}
		Stream<String> stream;
		final List<String> list = new ArrayList<String>();
		try {
			stream = Files.lines(Paths.get(name));
			stream.forEach(t -> list.add(t));
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
			e.printStackTrace();
			throw new CloneRepositoryException();
		} catch (TransportException e) {
			e.printStackTrace();
			throw new CloneRepositoryException();
		} catch (GitAPIException e) {
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
    	File source = new File(PathProject.getPathTemp());
    	File dest = new File(toPath);
    	try {
    		System.out.println("Gerando copia do arquivo... copyOutputSpoonToProject toPath: "+toPath+" fromPath: "+PathProject.getPathTemp());

    		File files[] = source.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.contains("package.html")){
                        return true;
                    }
                    return false;
                }
            });
            for (int i = 0; i < files.length; i++) {
                FileUtils.forceDelete(files[i]);
            }

            FileUtils.copyDirectory(source, dest);
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    }
    
    //MÉTODO UTILIZADO PARA CRIAR A CÓPIA DO PROJETO NA QUAL SERÁ O MUTANTE
    public static void clearOutputSpoonToProject(){
    	File source = new File(PathProject.getPathTemp());
    	try {
    		System.out.println("Limpando copyOutputSpoonToProject");
    		FileUtils.deleteDirectory(source);
    		FileUtils.forceMkdir(source);
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    }

    public static void removeDirectoryAndCreate(String pathDC){
		File source = new File(pathDC);
		try {
			System.out.println("APAGANDO E RECRIANDO MUTANTE");
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
			Environment env = launcher.getEnvironment();
			env.setAutoImports(true);
//			env.setCommentEnabled(true);
			env.setPreserveLineNumbers(true);
			env.setNoClasspath(true);
			launcher.buildModel();
			return launcher.getModel();
    	}catch(Exception e) {
    		return null;
    	}
	}
    
  //MODELO DO PROJETO SEM A UTILIZAÇÃO DO MAVEN
    public static CtModel getModelNoMaven(String projectPath) {
    	try {
			SpoonAPI spoon = new Launcher();
			spoon = new Launcher();
			Environment env = spoon.getEnvironment();
			env.setNoClasspath(true);
			env.setPreserveLineNumbers(true);
			env.setSourceClasspath(new String[] { projectPath });
			spoon.addInputResource(projectPath);
			spoon.buildModel();
			return spoon.getModel();
    	}catch(Exception e) {
    		return null;
    	}
	}
    
    //OBTENDO CLASSE PROCESSADA DO PROJETO COMPILADA 
    public static CtType<?> getClassByModel(String name, String projectPath, boolean isMavenProject){
    	CtModel model = null;
    	
    	if(isMavenProject)
    		model = getModel(projectPath);
		else {
			System.out.println("PATH:"+projectPath+PathProject.PROJECT_PATH_FILES_DEFAULT);
			model = getModelNoMaven(projectPath+PathProject.PROJECT_PATH_FILES_DEFAULT);
		}
        		
    	for(CtType<?> ty : model.getAllTypes()) {
    		if(ty instanceof CtClass<?>) {
    			CtClass<?> ctc = (CtClass<?>) ty;
	    		System.out.println("OPA: "+ctc.getQualifiedName());
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
    
    //MÉTODO QUE RETORNA OS TIPOS DERIVADOS DE EXCEÇÕES DA ÁRVORE DE EXCEÇÕES
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
	
	public static Properties getProperties() throws IOException {

    	if(properties!=null)
			return properties;

		java.util.Properties prop = geFileProp();
		
		properties = new Properties();
		properties.setHomeMaven(prop.getProperty("homeMaven", null));
		properties.setUrlMutations(prop.getProperty("urlMutations", null));
		properties.setProjectsFile(prop.getProperty("projectsFile", null));

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
	
	@SuppressWarnings("unchecked")
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
