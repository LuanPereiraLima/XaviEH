package ufc.br.mutant_project.executers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ufc.br.mutant_project.constants.PathProject;
import ufc.br.mutant_project.exceptions.ConfigPropertiesNotFoundException;
import ufc.br.mutant_project.exceptions.InicializerException;
import ufc.br.mutant_project.exceptions.ListProjectsNotFoundException;
import ufc.br.mutant_project.exceptions.NotURLsException;
import ufc.br.mutant_project.models.CoverageResult;
import ufc.br.mutant_project.models.TotalCoveredStatus;
import ufc.br.mutant_project.util.Util;
import ufc.br.mutant_project.util.UtilWriteReader;
import ufc.br.mutant_project.util.XmlJacoco;

public class ExecuterEstatisticsOnlyTotalCoveredStatus extends Execute {
	
	public ExecuterEstatisticsOnlyTotalCoveredStatus() {
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