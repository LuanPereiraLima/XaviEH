package ufc.br.xavieh.executers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ufc.br.xavieh.constants.PathProject;
import ufc.br.xavieh.exceptions.ConfigPropertiesNotFoundException;
import ufc.br.xavieh.exceptions.InicializerException;
import ufc.br.xavieh.exceptions.ListProjectsNotFoundException;
import ufc.br.xavieh.exceptions.NotURLsException;
import ufc.br.xavieh.models.CoverageResult;
import ufc.br.xavieh.models.TotalCoveredStatus;
import ufc.br.xavieh.util.Util;
import ufc.br.xavieh.util.UtilWriteReader;
import ufc.br.xavieh.util.XmlJacoco;

public class ExecuterEstatisticsOnlyTotalCoveredStatus extends Execute {

	public ExecuterEstatisticsOnlyTotalCoveredStatus() {
		super(false);
	}

	public ExecuterEstatisticsOnlyTotalCoveredStatus(boolean saveInFile, boolean cloneRepository, boolean verifyIfProjectAlreadyRun, boolean testProject) {
		super(saveInFile, cloneRepository, verifyIfProjectAlreadyRun, testProject);
	}
	
	public void execute() throws InicializerException, ListProjectsNotFoundException, NotURLsException, ConfigPropertiesNotFoundException {

		initializer();
		
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
			String submodule = getItemByUrl(linha, MODULE_URL);
			
			String path = Util.validateAndGetNameRepository(linha[0]);
			
			if(version!=null)
				path=path+"-"+version;
			
			if(!(path!=null && !path.trim().isEmpty())) {
				System.out.println("Incorrect GIT URL: "+list.get(i)+" (Para resolver o problema analise as URLs adicionadas no arquivo 'repositiores.txt')");
				continue;
			}
			
			TotalCoveredStatus totalCoveredStatus = null;
			
			totalCoveredStatus = XmlJacoco.listaClassCoverageFromXMLJaCoCoTotalCoveredStatusTotal(PathProject.makePathToProjectMaven(path, submodule)+"target/site/jacoco/jacoco.xml");
			
			tcs.put(path, totalCoveredStatus);
		}
		
		System.out.println(resultCoverageTotal);
		
		System.out.close();
				
		UtilWriteReader.writeCsvFileEstatistics2Total(tcs);
		
		if(saveOutputInFile)	
			System.out.close();
	}
}