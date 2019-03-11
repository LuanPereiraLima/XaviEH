package ufc.br.mutant_project.models;

public class CoverageResult {
	
	public static final String ALL_RASINGS = "ALL_RASINGS";
	public static final String THROW = "THROW";
	public static final String CATCH = "CATCH";
	public static final String RAISINGS_PROGRAMMER_DEFINED = "RAISINGS_PROGRAMMER_DEFINED";
	public static final String RAISINGS_NON_PROGRAMMER_DEFINED = "RAISINGS_NON_PROGRAMMER_DEFINED";
	public static final String ALL_HANDLINGS = "ALL_HANDLINGS";
	public static final String HANDLINGS_PROGRAMMER_DEFINED = "HANDLINGS_PROGRAMMER_DEFINED";
	public static final String HANDLINGS_NON_PROGRAMMER_DEFINED = "HANDLINGS_NON_PROGRAMMER_DEFINED";
	
	private String project;
	private String className;
	private ClassXMLCoverageLine coverageLine;
	private int lineCode;
	private String lineContent;
	private String type;
	private boolean coveraged;
	private String typeCode;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public boolean isCoveraged() {
		return coveraged;
	}
	public void setCoveraged(boolean coveraged) {
		this.coveraged = coveraged;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public int getLineCode() {
		return lineCode;
	}
	public void setLineCode(int lineCode) {
		this.lineCode = lineCode;
	}
	public String getLineContent() {
		return lineContent;
	}
	public void setLineContent(String lineContent) {
		this.lineContent = lineContent;
	}
	public ClassXMLCoverageLine getCoverageLine() {
		return coverageLine;
	}
	public void setCoverageLine(ClassXMLCoverageLine coverageLine) {
		this.coverageLine = coverageLine;
	}
	
	@Override
	public String toString() {
		return "CoverageResult [project=" + project + ", className=" + className + ", coverageLine=" + coverageLine
				+ ", lineCode=" + lineCode + ", lineContent=" + lineContent + ", type=" + type + ", coveraged="
				+ coveraged + "typeCode = " + typeCode +"]";
	}
	
	public String toStringCSV() {
		return project + "," + className + "," + lineCode + "," + type +  "," + typeCode + "," + coveraged + "," + coverageLine.toStringCSV();
	}
}
