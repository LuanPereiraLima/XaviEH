package ufc.br.xavieh.models;

public class ClassXMLCoverageLineMethod extends ClassXMLCoverageLine{
	
	private int mm;
	private int cm;
	private String methodName;
	
	public int getMm() {
		return mm;
	}
	public void setMm(int mm) {
		this.mm = mm;
	}
	public int getCm() {
		return cm;
	}
	public void setCm(int cm) {
		this.cm = cm;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	@Override
	public String toString() {
		return "ClassXMLCoverageLineMethod [mm=" + mm + ", cm=" + cm + ", getNumberLine()=" + getNumberLine() + "]";
	}

	@Override
	public String toStringCSV() {
		return ",,,,"+mm+","+cm+","+getType();
	}	
	
	public boolean verifyCoverage() {
		setType(INSTRUCTION);
		if(mm==1) {
			return false;
		}
		if(cm==1) {
			return true;
		}
		return false;
	}
}