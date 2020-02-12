package ufc.br.xavieh.models;

import java.util.List;

public class ClassXMLCoverage {
	
	private String name;
	private String packageName;
	private String fullName;
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

	@Override
	public String toString() {
		return "ClassXMLCoverage [name=" + name + ", packageName=" + packageName + ", fullName=" + fullName
				+ ", lineDetails=" + lineDetails + "]";
	}
}


