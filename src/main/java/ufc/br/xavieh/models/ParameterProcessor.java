package ufc.br.xavieh.models;

public class ParameterProcessor {
	private Integer beginLine;
	private Integer endLine;
	private Integer position;
	private String before;
	private String after;
	private boolean needModification = true;
	
	public Integer getBeginLine() {
		return beginLine;
	}
	public void setBeginLine(Integer beginLine) {
		this.beginLine = beginLine;
	}
	public Integer getEndLine() {
		return endLine;
	}
	public void setEndLine(Integer endLine) {
		this.endLine = endLine;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public String getBefore() {
		return before;
	}
	public void setBefore(String before) {
		this.before = before;
	}
	public String getAfter() {
		return after;
	}
	public void setAfter(String after) {
		this.after = after;
	}

	public boolean isNeedModification() {
		return needModification;
	}
	
	public void setNeedModification(boolean needModification) {
		this.needModification = needModification;
	}
	
	@Override
	public String toString() {
		return  "\nbeginLine=" + beginLine + 
				", \n\nendLine=" + endLine + 
				", \n\nCodeBefore=\n" + before + 
				", \n\nCodeAfter=\n" + after + 
				"\n";
	}
}
