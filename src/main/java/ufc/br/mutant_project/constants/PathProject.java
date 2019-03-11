 package ufc.br.mutant_project.constants;

import java.io.File;

import ufc.br.mutant_project.exceptions.PomException;
import ufc.br.mutant_project.util.Util;

public class PathProject {
	public static String USER_REFERENCE_TO_PROJECT = "/media/loopback/C4DAE5FEDAE5EC9C/Users/luan_"+(File.separator)+"mutationsTests"+(File.separator);
	
	//public static String USER_REFERENCE_TO_PROJECT = FileUtils.getUserDirectory()+(File.separator)+"mutationsTests2"+(File.separator);
	public static String PROJECT_PATH_TEMP = USER_REFERENCE_TO_PROJECT+"temp";
	
	//TODO ADICIONAR NOVOS CAMINHOS DE ACORDO COM O NECESSÁRIO / PASSAR POR PARAMETRO
	public static String PROJECT_PATH_FILES_DEFAULT = "src"+(File.separator)+"java";
	
	public static String makePathToJavaCode(String uriName, String module) throws PomException {
		return makePathToProjectMaven(uriName, module)+Util.getSourceDirectory(makePathToProjectMaven(uriName, module));
	}
	
	public static String makePathToPathFiles(String uriName, String module) throws PomException {
		if(module == null)
			return Util.getSourceDirectory(makePathToProjectMaven(uriName, null));
		
		return module+File.separator+Util.getSourceDirectory(makePathToProjectMaven(uriName, module));
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
