package ufc.br.xavieh.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ufc.br.xavieh.constants.PathProject;
import ufc.br.xavieh.constants.Processors;
import ufc.br.xavieh.models.CoverageResult;
import ufc.br.xavieh.models.FinalResultSavedByProject;
import ufc.br.xavieh.models.ResultsStatisticsLength;
import ufc.br.xavieh.models.TesteProject;
import ufc.br.xavieh.models.TotalCoveredStatus;

public class UtilWriteReader {

    private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";
	
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
		return "NameProject,NoTryBlock,NoCatchBlock,NoFinallyBlock,NoThrows,NoClass,NoLineCode";
	}
	
	//MÉTODO QUE CRIA O CABEÇALHO DO CSV PARA CADA PROJETO
	private static String createFileHeaderTestsProjects() {
		return "NAME,URL,COMMIT,PASS_TESTS,JACOCO_GENERATE";
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
				return "PROJECT^MI^CI^MB^CB^MM^CM^MC^CC";
			}
		
	//MÉTODO QUE CRIA O CABEÇALHO DO CSV PARA CADA PROJETO
	private static String createFileHeaderStatisticTypeCode() {
		return    "PROJECT,MI,CI,MB,CB,MM,CM,"
				
				+ "THROW_MI,THROW_CI,THROWI_MI,THROWI_CI,THROWE_MI,THROWE_CI,"
				
				+ "CATCH_MI,CATCH_CI,CATCH_MB,CATCH_CB,"
				+ "CATCHI_MI,CATCHI_CI,CATCHI_MB,CATCHI_CB,"
				+ "CATCHE_MI,CATCHE_CI,CATCHE_MB,CATCHE_CB";

				//+ "THROWS_CM,THROWS_MM,"

				//+ "FINALLY_MI,FINALLY_CI,FINALLY_MB,FINALLY_CB,";
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
					fileWriter.append(totalCoveredStatus.get(project).getTHROWS_CM_TotalCoveredMethodsWithThrows()+"");

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
		
	public static void writeCsvFileEstatisticsCoveraveTypeCode(Map<String, TotalCoveredStatus> totalCoveredStatus) {
			FileWriter fileWriter = null;
			
			try {
				fileWriter = new FileWriter(PathProject.USER_REFERENCE_TO_PROJECT+"finalResultForProjectStatusInternalExternalExceptions.csv");

				fileWriter.append(createFileHeaderStatisticTypeCode());
				fileWriter.append(NEW_LINE_SEPARATOR);
				
				for(String project: totalCoveredStatus.keySet()) {
					fileWriter.append(project+",");
					
					fileWriter.append(totalCoveredStatus.get(project).getMI_TotalMissedInstructions()+",");
					fileWriter.append(totalCoveredStatus.get(project).getCI_TotalCoveredInstructions()+",");
					fileWriter.append(totalCoveredStatus.get(project).getMB_TotalMissedBraches()+",");
					fileWriter.append(totalCoveredStatus.get(project).getCB_TotalCoveredBraches()+",");
					fileWriter.append(totalCoveredStatus.get(project).getMM_TotalMissedMethods()+",");
					fileWriter.append(totalCoveredStatus.get(project).getCM_TotalCoveredMethods()+",");

					fileWriter.append(totalCoveredStatus.get(project).getTHROW_MI_TotalMissedInstructionsThrowStatements()+",");
					fileWriter.append(totalCoveredStatus.get(project).getTHROW_CI_TotalCoveredInstructionsThrowStatements()+",");
					fileWriter.append(totalCoveredStatus.get(project).getTHROW_I_MI_TotalMissedInstructionsThrowStatements()+",");
					fileWriter.append(totalCoveredStatus.get(project).getTHROW_I_CI_TotalCoveredInstructionsThrowStatements()+",");
					fileWriter.append(totalCoveredStatus.get(project).getTHROW_E_MI_TotalMissedInstructionsThrowStatements()+",");
					fileWriter.append(totalCoveredStatus.get(project).getTHROW_E_CI_TotalCoveredInstructionsThrowStatements()+",");
					
					fileWriter.append(totalCoveredStatus.get(project).getCATCH_MI_TotalMissedInstructionsCatchBlocks()+",");
					fileWriter.append(totalCoveredStatus.get(project).getCATCH_CI_TotalCoveredInstructionsCatchBlocks()+",");
					fileWriter.append(totalCoveredStatus.get(project).getCATCH_MB_TotalMissedBrachesCatchBlocks()+",");
					fileWriter.append(totalCoveredStatus.get(project).getCATCH_CB_TotalCoveredBrachesCatchBlocks()+",");
					
					fileWriter.append(totalCoveredStatus.get(project).getCATCH_I_MI_TotalMissedInstructionsCatchBlocks()+",");
					fileWriter.append(totalCoveredStatus.get(project).getCATCH_I_CI_TotalCoveredInstructionsCatchBlocks()+",");
					fileWriter.append(totalCoveredStatus.get(project).getCATCH_I_MB_TotalMissedBrachesCatchBlocks()+",");
					fileWriter.append(totalCoveredStatus.get(project).getCATCH_I_CB_TotalCoveredBrachesCatchBlocks()+",");
					
					fileWriter.append(totalCoveredStatus.get(project).getCATCH_E_MI_TotalMissedInstructionsCatchBlocks()+",");
					fileWriter.append(totalCoveredStatus.get(project).getCATCH_E_CI_TotalCoveredInstructionsCatchBlocks()+",");
					fileWriter.append(totalCoveredStatus.get(project).getCATCH_E_MB_TotalMissedBrachesCatchBlocks()+",");
					fileWriter.append(totalCoveredStatus.get(project).getCATCH_E_CB_TotalCoveredBrachesCatchBlocks()+"");

					//TODO ADDED
					/*fileWriter.append(totalCoveredStatus.get(project).getTHROWS_CM_TotalCoveredMethodsWithThrows()+",");
					fileWriter.append(totalCoveredStatus.get(project).getTHROWS_MM_TotalMissedMethodsWithThrows()+",");

					fileWriter.append(totalCoveredStatus.get(project).getFINALLY_MI_TotalMissedInstructionsFinallyBlocks()+",");
					fileWriter.append(totalCoveredStatus.get(project).getFINALLY_CI_TotalCoveredInstructionsFinallyBlocks()+",");
					fileWriter.append(totalCoveredStatus.get(project).getFINALLY_MB_TotalMissedBrachesFinallyBlocks()+",");
					fileWriter.append(totalCoveredStatus.get(project).getFINALLY_CB_TotalCoveredBrachesFinallyBlocks()+"");
					*///TODO ADDED F

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
		
	public static FinalResultSavedByProject getFinalResultSavedByProjectByTypeMutant(String type, List<FinalResultSavedByProject> list) {
		for(FinalResultSavedByProject fr: list) {
			if(fr.getAbbreviationMutationType().equals(type))
				return fr;
		}
		return null;
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
}
