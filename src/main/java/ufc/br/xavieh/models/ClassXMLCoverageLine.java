package ufc.br.xavieh.models;

public abstract class ClassXMLCoverageLine {
	
	public final String BRANCH = "BRANCH";
	public final String INSTRUCTION = "INSTRUCTION";
	private int numberLine;
	private String type;
	
	public int getNumberLine() {
		return numberLine;
	}
	
	public void setNumberLine(int numberLine) {
		this.numberLine = numberLine;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public abstract String toStringCSV();
	
	public abstract boolean verifyCoverage();
}