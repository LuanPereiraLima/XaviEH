package ufc.br.xavieh.models;

public class ResultSavedByMutant {
	private Boolean mutantDead;
	private String pathFile;
	private Integer beginLine;
	private Integer endLine;
	private String codeBefore;
	private String codeAfter;
	
	public ResultSavedByMutant(Boolean mutantDead, String pathFile, Integer beginLine, Integer endLine,
			String codeBefore, String codeAfter) {
		super();
		this.mutantDead = mutantDead;
		this.pathFile = pathFile;
		this.beginLine = beginLine;
		this.endLine = endLine;
		this.codeBefore = codeBefore;
		this.codeAfter = codeAfter;
	}

	public Boolean getMutantDead() {
		return mutantDead;
	}
	public void setMutantDead(Boolean mutantDead) {
		this.mutantDead = mutantDead;
	}
	public String getPathFile() {
		return pathFile;
	}
	public void setPathFile(String pathFile) {
		this.pathFile = pathFile;
	}
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
	public String getCodeBefore() {
		return codeBefore;
	}
	public void setCodeBefore(String codeBefore) {
		this.codeBefore = codeBefore;
	}
	public String getCodeAfter() {
		return codeAfter;
	}
	public void setCodeAfter(String codeAfter) {
		this.codeAfter = codeAfter;
	}
	
	
	
}
