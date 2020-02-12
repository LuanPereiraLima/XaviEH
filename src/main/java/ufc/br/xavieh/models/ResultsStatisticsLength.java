package ufc.br.xavieh.models;

public class ResultsStatisticsLength {
	
	private String nameProject;
	private int numTryBlock;
	private int numCatchBlock;
	private int numFinallyBlock;
	private int numClass;
	private int numLineCode;
	private int numThrows;
	
	public ResultsStatisticsLength(String nameProject, int numTryBlock, int numCatchBlock, int numFinallyBlock,
			int numClass, int numLineCode, int numThrows) {
		this.nameProject = nameProject;
		this.numTryBlock = numTryBlock;
		this.numCatchBlock = numCatchBlock;
		this.numFinallyBlock = numFinallyBlock;
		this.numClass = numClass;
		this.numLineCode = numLineCode;
		this.numThrows = numThrows;
	}
	
	public String getNameProject() {
		return nameProject;
	}
	public void setNameProject(String nameProject) {
		this.nameProject = nameProject;
	}
	public int getNumTryBlock() {
		return numTryBlock;
	}
	public void setNumTryBlock(int numTryBlock) {
		this.numTryBlock = numTryBlock;
	}
	public int getNumCatchBlock() {
		return numCatchBlock;
	}
	public void setNumCatchBlock(int numCatchBlock) {
		this.numCatchBlock = numCatchBlock;
	}
	public int getNumFinallyBlock() {
		return numFinallyBlock;
	}
	public void setNumFinallyBlock(int numFinallyBlock) {
		this.numFinallyBlock = numFinallyBlock;
	}
	public int getNumClass() {
		return numClass;
	}
	public void setNumClass(int numClass) {
		this.numClass = numClass;
	}
	public int getNumLineCode() {
		return numLineCode;
	}
	public void setNumLineCode(int numLineCode) {
		this.numLineCode = numLineCode;
	}
	public int getNumThrows() {
		return numThrows;
	}
	public void setNumThrows(int numThrows) {
		this.numThrows = numThrows;
	}
	public String toStringCSV() {
		return nameProject+","+numTryBlock+","+numCatchBlock+","+numFinallyBlock+","+numThrows+","+numClass+","+numLineCode;
	}
}
