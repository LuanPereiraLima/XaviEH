 package ufc.br.mutant_project.constants;

import java.io.File;

import org.apache.commons.io.FileUtils;

import ufc.br.mutant_project.exceptions.PomException;

public class PathProject {
	//public static String USER_REFERENCE_TO_PROJECT = "/media/loopback/C4DAE5FEDAE5EC9C/Users/luan_"+(File.separator)+"mutationsTests"+(File.separator);
	
	public static String USER_REFERENCE_TO_PROJECT = FileUtils.getUserDirectory()+(File.separator)+"mutationsTests"+(File.separator);

	public static String getPathTemp(){
		return USER_REFERENCE_TO_PROJECT+"temp";
	}
	
	public static String PROJECT_PATH_FILES_DEFAULT = "src"+(File.separator)+"main"+(File.separator)+"java";
	
	public static String makePathToJavaCode(String uriName, String module) throws PomException {
		return makePathToProjectMaven(uriName, module)+PROJECT_PATH_FILES_DEFAULT;
	}
	
	public static String makePathToPathFiles(String uriName, String module) throws PomException {
		if(module == null)
			return PROJECT_PATH_FILES_DEFAULT;
		
		return module+File.separator+PROJECT_PATH_FILES_DEFAULT;
	}
	
	public static String makePathToProject(String uriName) {
		return USER_REFERENCE_TO_PROJECT+uriName+File.separator;
	}
	
	public static String makePathToProjectMaven(String uriName, String module) {
		if(module != null)
			return USER_REFERENCE_TO_PROJECT+uriName+File.separator+uriName+File.separator+module+File.separator;
		
		return USER_REFERENCE_TO_PROJECT+uriName+File.separator+uriName+File.separator;
	}
	
	public static String makePathToProjectMavenToJacoco(String uriName, String module) {
		return PathProject.makePathToProjectMaven(uriName, module) + "target"+File.separator+"site"+File.separator+"jacoco"+File.separator+"jacoco.xml";
	}
}
