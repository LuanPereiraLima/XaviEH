package ufc.br.xavieh.models;

import java.util.List;

public class ClassXMLCoverage {
	
	private String name;
	private String packageName;
	private String fullName;
	private int MI_MissedInstructions;
	private int CI_CoveredInstructions;
	private int MB_MissedBraches;
	private int CB_CoveredBraches;
	private int MM_MissedMethods;
	private int CM_CoveredMethods;
	private int MC_MissedClasses;
	private int CC_CoveredClasses;
	private double percentI;
	private double percentB;
	private boolean allCoverage;

	private List<ClassXMLCoverageLine> lineDetails;

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public List<ClassXMLCoverageLine> getLineDetails() {
		return lineDetails;
	}
	
	public void setLineDetails(List<ClassXMLCoverageLine> lineDetails) {
		this.lineDetails = lineDetails;
	}

	public int getMI_MissedInstructions() {
		return MI_MissedInstructions;
	}

	public void setMI_MissedInstructions(int MI_MissedInstructions) {
		this.MI_MissedInstructions = MI_MissedInstructions;
	}

	public int getCI_CoveredInstructions() {
		return CI_CoveredInstructions;
	}

	public void setCI_CoveredInstructions(int CI_CoveredInstructions) {
		this.CI_CoveredInstructions = CI_CoveredInstructions;
	}

	public int getMB_MissedBraches() {
		return MB_MissedBraches;
	}

	public void setMB_MissedBraches(int MB_MissedBraches) {
		this.MB_MissedBraches = MB_MissedBraches;
	}

	public int getCB_CoveredBraches() {
		return CB_CoveredBraches;
	}

	public void setCB_CoveredBraches(int CB_CoveredBraches) {
		this.CB_CoveredBraches = CB_CoveredBraches;
	}

	public int getMM_MissedMethods() {
		return MM_MissedMethods;
	}

	public void setMM_MissedMethods(int MM_MissedMethods) {
		this.MM_MissedMethods = MM_MissedMethods;
	}

	public int getCM_CoveredMethods() {
		return CM_CoveredMethods;
	}

	public void setCM_CoveredMethods(int CM_CoveredMethods) {
		this.CM_CoveredMethods = CM_CoveredMethods;
	}

	public int getMC_MissedClasses() {
		return MC_MissedClasses;
	}

	public void setMC_MissedClasses(int MC_MissedClasses) {
		this.MC_MissedClasses = MC_MissedClasses;
	}

	public int getCC_CoveredClasses() {
		return CC_CoveredClasses;
	}

	public void setCC_CoveredClasses(int CC_CoveredClasses) {
		this.CC_CoveredClasses = CC_CoveredClasses;
	}

	public double getPercentI() {
		return percentI;
	}

	public void setPercentI(double percentI) {
		this.percentI = percentI;
	}

	public double getPercentB() {
		return percentB;
	}

	public void setPercentB(double percentB) {
		this.percentB = percentB;
	}

	public boolean isAllCoverage() {
		return allCoverage;
	}

	public void setAllCoverage(boolean allCoverage) {
		this.allCoverage = allCoverage;
	}

	@Override
	public String toString() {
		return "ClassXMLCoverage{" +
				"name='" + name + '\'' +
				", packageName='" + packageName + '\'' +
				", fullName='" + fullName + '\'' +
				", MI_MissedInstructions=" + MI_MissedInstructions +
				", CI_CoveredInstructions=" + CI_CoveredInstructions +
				", MB_MissedBraches=" + MB_MissedBraches +
				", CB_CoveredBraches=" + CB_CoveredBraches +
				", MM_MissedMethods=" + MM_MissedMethods +
				", CM_CoveredMethods=" + CM_CoveredMethods +
				", MC_MissedClasses=" + MC_MissedClasses +
				", CC_CoveredClasses=" + CC_CoveredClasses +
				", lineDetails=" + lineDetails +
				'}';
	}
}


