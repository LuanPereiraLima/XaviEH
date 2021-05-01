package ufc.br.xavieh.models;

public class ResultCoverageMethodsPaper {
	private String type;
	private String nameClass;
	private boolean coveraged;
	private String methodName;
	private String methodNumberLine;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNameClass() {
		return nameClass;
	}

	public void setNameClass(String nameClass) {
		this.nameClass = nameClass;
	}

	public boolean isCoveraged() {
		return coveraged;
	}

	public void setCoveraged(boolean coveraged) {
		this.coveraged = coveraged;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getMethodNumberLine() {
		return methodNumberLine;
	}

	public void setMethodNumberLine(String methodNumberLine) {
		this.methodNumberLine = methodNumberLine;
	}

	@Override
	public String toString() {
		//PROJECT,CLASS_NAME,METHOD_NAME,TYPE_METHOD,COVERAGED
		return nameClass+","+methodName+","+type+","+coveraged+","+methodNumberLine;
	}
}
