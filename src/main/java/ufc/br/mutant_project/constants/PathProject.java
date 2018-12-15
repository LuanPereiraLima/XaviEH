 package ufc.br.mutant_project.constants;

import java.io.File;

import org.apache.commons.io.FileUtils;

import ufc.br.mutant_project.exceptions.PomException;
import ufc.br.mutant_project.util.Util;

public class PathProject {
	//public static String USER_REFERENCE_TO_PROJECT = "/media/luan/OPA"+(File.separator)+"mutationsTests"+(File.separator);
	
	public static String USER_REFERENCE_TO_PROJECT = FileUtils.getUserDirectory()+(File.separator)+"mutationsTests"+(File.separator);
	public static String PROJECT_PATH_TEMP = USER_REFERENCE_TO_PROJECT+"temp";
	public static String PROJECT_PATH_FILES_DEFAULT = "src"+(File.separator)+"main"+(File.separator)+"java";
	
	public static String makePathToJavaCode(String uriName, String module) throws PomException {
		return makePathToProjectMaven(uriName, module)+Util.getSourceDirectory(makePathToProjectMaven(uriName, module));
	}
	
	public static String makePathToPathFiles(String uriName, String module) throws PomException {
		if(module != null)
			return Util.getSourceDirectory(makePathToProjectMaven(uriName, module));
		
		return module+File.separator+Util.getSourceDirectory(makePathToProjectMaven(uriName, module));
	}
	
	public static String makePathToProject(String uriName) {
		return USER_REFERENCE_TO_PROJECT+uriName+File.separator;
	}
	
	//public static String makePathToProjectMaven(String uriName) {
	//	return USER_REFERENCE_TO_PROJECT+uriName+File.separator+uriName+File.separator;
	//}
	
	public static String makePathToProjectMaven(String uriName, String module) {
		if(module != null)
			return USER_REFERENCE_TO_PROJECT+uriName+File.separator+uriName+File.separator+module+File.separator;
		
		return USER_REFERENCE_TO_PROJECT+uriName+File.separator+uriName+File.separator;
	}
	
	public static String makePathToProjectMavenToJacoco(String uriName, String module) {
		return PathProject.makePathToProjectMaven(uriName, module) + "target/site/jacoco/jacoco.xml";
	}
}
