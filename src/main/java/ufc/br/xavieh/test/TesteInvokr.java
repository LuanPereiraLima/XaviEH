package ufc.br.xavieh.test;

import java.io.File;
import java.util.Collections;
import java.util.List;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

public class TesteInvokr {
	
	public static void main(String[] args) {
		invokerOthers("/home/loopback/mutationsTests3/commons-io-2.6/commons-io-2.6", Collections.singletonList("test"), null);
	}
	
	public static int invokerOthers(String copyProjectPath, List<String> goals, List<String> projects) {
    	try {
			Invoker invoker = new DefaultInvoker();
			
			invoker.setMavenHome( new File( "/opt/apache-maven" ) );
			
			InvocationRequest request = new DefaultInvocationRequest();
			request.setBaseDirectory( new File(copyProjectPath) );
			request.setGoals( goals );
			
			InvocationResult result = invoker.execute( request );
			return result.getExitCode();
			
		} catch (MavenInvocationException e) {
			e.printStackTrace();
			return 0;
		}
    }
}
