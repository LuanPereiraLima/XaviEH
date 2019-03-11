package ufc.br.mutant_project.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Model;
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

import ufc.br.mutant_project.constants.Jacoco;
import ufc.br.mutant_project.exceptions.JacocoException;
import ufc.br.mutant_project.exceptions.PomException;
import ufc.br.mutant_project.models.FinalResultSavedByProject;

public class TesteInvokr {
	
	public static void main(String[] args) {
		invokerOthers("/media/loopback/C4DAE5FEDAE5EC9C/Users/luan_/mutationsTests/junit4-4.12/junit4-4.12", Collections.singletonList("test"), Collections.singletonList("httpclient5-osgi"));
	}
	
	public static int invokerOthers(String copyProjectPath, List<String> goals, List<String> projects) {
    	try {
			Invoker invoker = new DefaultInvoker();
			
			invoker.setMavenHome( new File( "/opt/apache-maven" ) );
			
			InvocationRequest request = new DefaultInvocationRequest();
			request.setBaseDirectory( new File(copyProjectPath) );
			//request.setPomFile( new File( copyProjectPath ) );
			request.setGoals( goals );
			//request.setLocalRepositoryDirectory(new File(copyProjectPath));
			//request.setProjects( projects );
			//request.setDebug(true);
			
			InvocationResult result = invoker.execute( request );
			return result.getExitCode();
			
		} catch (MavenInvocationException e) {
			e.printStackTrace();
			return 0;
		}
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
				
			    System.out.println("modulos: "+model.getModules());
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
}
