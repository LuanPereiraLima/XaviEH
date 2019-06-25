package ufc.br.mutant_project.executers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

import ufc.br.mutant_project.constants.ConfigProperties;
import ufc.br.mutant_project.constants.PathProject;
import ufc.br.mutant_project.exceptions.ConfigPropertiesNotFoundException;
import ufc.br.mutant_project.models.Properties;
import ufc.br.mutant_project.runners.AbstractRunner;
import ufc.br.mutant_project.util.Util;
import ufc.br.mutant_project.util.UtilWriteReader;

public class ExecuteCSVFromXMLFinal extends Execute{

	public ExecuteCSVFromXMLFinal(boolean saveInFile, boolean cloneRepository, boolean verifyIfProjectAlreadyRun, boolean testProject, boolean spoonVerify) {
		super(saveInFile, cloneRepository, verifyIfProjectAlreadyRun, testProject, spoonVerify);
	}

	public void execute() throws ConfigPropertiesNotFoundException {

		showName();

		String fields = "";

		Properties properties = null;

		for(String f: ConfigProperties.fields)
			fields += f+"\n";

		try {
			properties = Util.getProperties();
			if(properties.getHomeMaven() == null || properties.getProjectsFile() == null || properties.getUrlMutations() == null)
				throw new ConfigPropertiesNotFoundException("Alguma propriedade do arquivo 'config.properties' necessária não foi encontrado (Para resolver o problema, adiciona as propriedades necessárias para o projeto do projeto \n[ propriedades disponíveis: \n\n"+fields+"\n\n].");

		} catch (IOException e1) {
			throw new ConfigPropertiesNotFoundException("Nenhum arquivo de propriedades foi encontrado (Para resolver o problema, crie um arquivo 'config.properties' com as propriedades necessárias para o projeto do projeto \n[ propriedades dispníveis: \n"+fields+" ]).");
		}

		if(this.saveOutputInFile) {
			try {
				System.setOut(new PrintStream(new FileOutputStream(new Date()+"-output.txt")));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		PathProject.USER_REFERENCE_TO_PROJECT = properties.getUrlMutations();

		AbstractRunner.listSavedMutantResultType = Util.getListSaveMutantResultTypeFromXml(PathProject.USER_REFERENCE_TO_PROJECT);
		System.out.println("-Gerando CSV para todos os projetos");
		UtilWriteReader.writeCsvFileByAllProjects(AbstractRunner.listSavedMutantResultType);
		System.out.println("--OK!");
	}
}