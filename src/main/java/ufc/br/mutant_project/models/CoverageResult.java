package ufc.br.mutant_project.models;

public class CoverageResult {
	
	private String project;
	private String className;
	private ClassXMLCoverageLine coverageLine;
	private int lineCode;
	private String lineContent;
	private String type;
	private boolean coveraged;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
				+ coveraged + "]";
	}
	public String toStringCSV() {
		return project + "," + className + "," + lineCode + "," + type +  "," + coveraged + ","
				+ coverageLine.toStringCSV();
	}
}
