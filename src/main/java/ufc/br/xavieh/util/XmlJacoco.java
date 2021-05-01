
package ufc.br.xavieh.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ufc.br.xavieh.models.ClassXMLCoverage;
import ufc.br.xavieh.models.ClassXMLCoverageLine;
import ufc.br.xavieh.models.ClassXMLCoverageLineMethod;
import ufc.br.xavieh.models.ClassXMLCoverageLineNormal;
import ufc.br.xavieh.models.TotalCoveredStatus;

public class XmlJacoco {
	public static List<ClassXMLCoverage> listaClassCoverageFromXMLJaCoCo(String pathXML){
		
		try {
			File fXmlFile = new File(pathXML);//PathProject.USER_REFERENCE_TO_PROJECT+"jacoco.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
	
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			
			List<ClassXMLCoverage> listaDeClasses = new ArrayList<ClassXMLCoverage>();
			
			NodeList lista = doc.getElementsByTagName("package");
			for(int i=0; i < lista.getLength(); i++) {
				for(int j=0; j < lista.item(i).getChildNodes().getLength(); j++) {
					if(lista.item(i).getChildNodes().item(j).getNodeName().equals("sourcefile")) {
						//System.out.println(lista.item(i).getChildNodes().item(j).getAttributes().item(0).getNodeValue());
						lista.item(i).getChildNodes().item(j).getChildNodes();
						//System.out.println("Package: "+lista.item(i).getAttributes().item(0).getNodeValue().replaceAll("/", "."));
						ClassXMLCoverage cc = new ClassXMLCoverage();
						cc.setName(lista.item(i).getChildNodes().item(j).getAttributes().item(0).getNodeValue());
						cc.setPackageName(lista.item(i).getAttributes().item(0).getNodeValue().replaceAll("/", "."));
						cc.setFullName(lista.item(i).getAttributes().item(0).getNodeValue().replaceAll("/", ".")+"."+lista.item(i).getChildNodes().item(j).getAttributes().item(0).getNodeValue());
						List<ClassXMLCoverageLine> listaLines = new ArrayList<ClassXMLCoverageLine>();
						for(int l=0; l < lista.item(i).getChildNodes().item(j).getChildNodes().getLength(); l++) {
							if(lista.item(i).getChildNodes().item(j).getChildNodes().item(l).getNodeName().equals("line")) {
								ClassXMLCoverageLineNormal line = new ClassXMLCoverageLineNormal();
								line.setNumberLine(Integer.parseInt(lista.item(i).getChildNodes().item(j).getChildNodes().item(l).getAttributes().getNamedItem("nr").getNodeValue()));
								line.setMi(Integer.parseInt(lista.item(i).getChildNodes().item(j).getChildNodes().item(l).getAttributes().getNamedItem("mi").getNodeValue()));
								line.setCi(Integer.parseInt(lista.item(i).getChildNodes().item(j).getChildNodes().item(l).getAttributes().getNamedItem("ci").getNodeValue()));
								line.setMb(Integer.parseInt(lista.item(i).getChildNodes().item(j).getChildNodes().item(l).getAttributes().getNamedItem("mb").getNodeValue()));
								line.setCb(Integer.parseInt(lista.item(i).getChildNodes().item(j).getChildNodes().item(l).getAttributes().getNamedItem("cb").getNodeValue()));
								listaLines.add(line);
							}
						}
						cc.setLineDetails(listaLines);
						listaDeClasses.add(cc);
					}
				}
			}
			
			return listaDeClasses;
			
		}catch (SAXException e) {
			e.printStackTrace();
			return null;
		}catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static TotalCoveredStatus listaClassCoverageFromXMLJaCoCoTotalCoveredStatus(String pathXML){
		
		try {
			File fXmlFile = new File(pathXML);//PathProject.USER_REFERENCE_TO_PROJECT+"jacoco.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
	
			TotalCoveredStatus tcs = new TotalCoveredStatus();
			
			NodeList report = doc.getElementsByTagName("report");
			
			for(int i=0; i < report.getLength(); i++) {
				
				for(int j=0; j < report.item(i).getChildNodes().getLength(); j++) {
					NodeList lista = report.item(i).getChildNodes();
					if(lista.item(j).getNodeName().equals("counter")) {
						System.out.println("Opa Opa: ");
						if(lista.item(j).getAttributes().getNamedItem("type").getNodeValue().equals("INSTRUCTION")){
							tcs.setMI_TotalMissedInstructions(Integer.parseInt(lista.item(j).getAttributes().getNamedItem("missed").getNodeValue()));
							tcs.setCI_TotalCoveredInstructions(Integer.parseInt(lista.item(j).getAttributes().getNamedItem("covered").getNodeValue()));
						}else if(lista.item(j).getAttributes().getNamedItem("type").getNodeValue().equals("BRANCH")){
							tcs.setMB_TotalMissedBraches(Integer.parseInt(lista.item(j).getAttributes().getNamedItem("missed").getNodeValue()));
							tcs.setCB_TotalCoveredBraches(Integer.parseInt(lista.item(j).getAttributes().getNamedItem("covered").getNodeValue()));
						}else if(lista.item(j).getAttributes().getNamedItem("type").getNodeValue().equals("METHOD")){
							tcs.setMM_TotalMissedMethods(Integer.parseInt(lista.item(j).getAttributes().getNamedItem("missed").getNodeValue()));
							tcs.setCM_TotalCoveredMethods(Integer.parseInt(lista.item(j).getAttributes().getNamedItem("covered").getNodeValue()));
						}
					}
				}
			}
			
			return tcs;
			
		}catch (SAXException e) {
			e.printStackTrace();
			return null;
		}catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static TotalCoveredStatus listaClassCoverageFromXMLJaCoCoTotalCoveredStatusTotal(String pathXML){
	
		try {
			File fXmlFile = new File(pathXML);//PathProject.USER_REFERENCE_TO_PROJECT+"jacoco.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
	
			TotalCoveredStatus tcs = new TotalCoveredStatus();
			
			NodeList report = doc.getElementsByTagName("report");
			
			for(int i=0; i < report.getLength(); i++) {
				
				for(int j=0; j < report.item(i).getChildNodes().getLength(); j++) {
					NodeList lista = report.item(i).getChildNodes();
					if(lista.item(j).getNodeName().equals("counter")) {
						System.out.println("Opa Opa: ");
						if(lista.item(j).getAttributes().getNamedItem("type").getNodeValue().equals("INSTRUCTION")){
							tcs.setMI_TotalMissedInstructions(Integer.parseInt(lista.item(j).getAttributes().getNamedItem("missed").getNodeValue()));
							tcs.setCI_TotalCoveredInstructions(Integer.parseInt(lista.item(j).getAttributes().getNamedItem("covered").getNodeValue()));
						}else if(lista.item(j).getAttributes().getNamedItem("type").getNodeValue().equals("BRANCH")){
							tcs.setMB_TotalMissedBraches(Integer.parseInt(lista.item(j).getAttributes().getNamedItem("missed").getNodeValue()));
							tcs.setCB_TotalCoveredBraches(Integer.parseInt(lista.item(j).getAttributes().getNamedItem("covered").getNodeValue()));
						}else if(lista.item(j).getAttributes().getNamedItem("type").getNodeValue().equals("METHOD")){
							tcs.setMM_TotalMissedMethods(Integer.parseInt(lista.item(j).getAttributes().getNamedItem("missed").getNodeValue()));
							tcs.setCM_TotalCoveredMethods(Integer.parseInt(lista.item(j).getAttributes().getNamedItem("covered").getNodeValue()));
						}else if(lista.item(j).getAttributes().getNamedItem("type").getNodeValue().equals("CLASS")){
							tcs.setMC_TotalMissedClasses(Integer.parseInt(lista.item(j).getAttributes().getNamedItem("missed").getNodeValue()));
							tcs.setCC_TotalCoveredClasses(Integer.parseInt(lista.item(j).getAttributes().getNamedItem("covered").getNodeValue()));
						}
					}
				}
			}
			
				return tcs;
				
			}catch (SAXException e) {
				e.printStackTrace();
				return null;
			}catch (ParserConfigurationException e) {
				e.printStackTrace();
				return null;
			}catch (IOException e) {
				e.printStackTrace();
				return null;
			}
	}


	public static List<ClassXMLCoverage> listaClassCoverageFromXMLJaCoCoMethods(String pathXML){
		
		try {
			File fXmlFile = new File(pathXML);//PathProject.USER_REFERENCE_TO_PROJECT+"jacoco.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
	
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			
			List<ClassXMLCoverage> listaDeClasses = new ArrayList<ClassXMLCoverage>();
			
			NodeList lista = doc.getElementsByTagName("package");
			//PERCORRENDO OS PACOTES
			for(int i=0; i < lista.getLength(); i++) {
				//PERCORRENDO AS CLASSES, ENTRE OUTROS
				for(int j=0; j < lista.item(i).getChildNodes().getLength(); j++) {
					//VERIFICANDO SE É CLASSE
					if(lista.item(i).getChildNodes().item(j).getNodeName().equals("class")) {
						
						ClassXMLCoverage cc = new ClassXMLCoverage();
						//OBTENDO NOME DA CLASSE
						String nome = lista.item(i).getChildNodes().item(j).getAttributes().getNamedItem("name").getNodeValue();
						cc.setName(nome.split("/")[nome.split("/").length-1]);
						//OBTENDO NOME DO PACOTE
						cc.setPackageName(lista.item(i).getAttributes().item(0).getNodeValue().replaceAll("/", "."));
						//NOME DA CLASSE + PACOTE
						cc.setFullName(lista.item(i).getChildNodes().item(j).getAttributes().item(0).getNodeValue().replaceAll("/", "."));
						
						List<ClassXMLCoverageLine> listaLines = new ArrayList<ClassXMLCoverageLine>();
						//PERCORRENDO OS ITENS DA CLASSE
						for(int k=0; k < lista.item(i).getChildNodes().item(j).getChildNodes().getLength(); k++) {
							//System.out.println("OPA: "+lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getNodeName());
							//OBTENDO APENANS OS MÉTODOS
							if(lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("method")) {
								//System.out.println("WAII: "+lista.item(i).getChildNodes().item(j).getChildNodes().item(0).getAttributes().getNamedItem("type").getNodeName());
								//PERCORRE TODOS OS ITEMS DENTRO DA TAG METHOD
								for(int l=0; l < lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getChildNodes().getLength(); l++) {
								//	System.out.println("OPA OPA: "+lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(l).getNodeName());
									//System.out.println("EOQ: "+lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(l).getAttributes().getNamedItem("type").getNodeName());
									if(lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(l).getNodeName().equals("counter") && lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(l).getAttributes().getNamedItem("type").getNodeValue().equals("METHOD")) {
										ClassXMLCoverageLineMethod line = new ClassXMLCoverageLineMethod();
										//System.out.println("adicionei a linha");
										line.setMethodName(lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getAttributes().getNamedItem("name").getNodeValue());
										line.setNumberLine(Integer.parseInt(lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getAttributes().getNamedItem("line").getNodeValue()));
										line.setCm(Integer.parseInt(lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(l).getAttributes().getNamedItem("covered").getNodeValue()));
										line.setMm(Integer.parseInt(lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(l).getAttributes().getNamedItem("missed").getNodeValue()));
										listaLines.add(line);
									}
									
								}
								
								//System.out.println(cc);
							}
							if(lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("counter")) {
								if(lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getAttributes().getNamedItem("type").getNodeValue().equals("INSTRUCTION")){
									cc.setMI_MissedInstructions(Integer.parseInt(lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getAttributes().getNamedItem("missed").getNodeValue()));
									cc.setCI_CoveredInstructions(Integer.parseInt(lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getAttributes().getNamedItem("covered").getNodeValue()));
								}else if(lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getAttributes().getNamedItem("type").getNodeValue().equals("BRANCH")){
									cc.setMB_MissedBraches(Integer.parseInt(lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getAttributes().getNamedItem("missed").getNodeValue()));
									cc.setCB_CoveredBraches(Integer.parseInt(lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getAttributes().getNamedItem("covered").getNodeValue()));
								}else if(lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getAttributes().getNamedItem("type").getNodeValue().equals("METHOD")){
									cc.setMM_MissedMethods(Integer.parseInt(lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getAttributes().getNamedItem("missed").getNodeValue()));
									cc.setCM_CoveredMethods(Integer.parseInt(lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getAttributes().getNamedItem("covered").getNodeValue()));
								}else if(lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getAttributes().getNamedItem("type").getNodeValue().equals("CLASS")){
									cc.setMC_MissedClasses(Integer.parseInt(lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getAttributes().getNamedItem("missed").getNodeValue()));
									cc.setCC_CoveredClasses(Integer.parseInt(lista.item(i).getChildNodes().item(j).getChildNodes().item(k).getAttributes().getNamedItem("covered").getNodeValue()));
								}
							}
						}
						cc.setLineDetails(listaLines);
						listaDeClasses.add(cc);					}
				}
			}
				
				return listaDeClasses;
				
			}catch (SAXException e) {
				e.printStackTrace();
				return null;
			}catch (ParserConfigurationException e) {
				e.printStackTrace();
				return null;
			}catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
