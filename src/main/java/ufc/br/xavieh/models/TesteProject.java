package ufc.br.xavieh.models;

public class TesteProject {
	private boolean passInTests;
	private boolean jacocoGenerate;
	private String nameProject;
	private String urlProject;
	private String commitProject;
	
	public TesteProject(boolean passInTests, boolean jacocoGenerate, String nameProject, String urlProject,
			String commitProject) {
		this.passInTests = passInTests;
		this.jacocoGenerate = jacocoGenerate;
		this.nameProject = nameProject;
		this.urlProject = urlProject;
		this.commitProject = commitProject;
	}
	
	public boolean isPassInTests() {
		return passInTests;
	}
	public void setPassInTests(boolean passInTests) {
		this.passInTests = passInTests;
	}
	public boolean isJacocoGenerate() {
		return jacocoGenerate;
	}
	public void setJacocoGenerate(boolean jacocoGenerate) {
		this.jacocoGenerate = jacocoGenerate;
	}
	public String getNameProject() {
		return nameProject;
	}
	public void setNameProject(String nameProject) {
		this.nameProject = nameProject;
	}
	public String getUrlProject() {
		return urlProject;
	}
	public void setUrlProject(String urlProject) {
		this.urlProject = urlProject;
	}
	public String getCommitProject() {
		return commitProject;
	}
	public void setCommitProject(String commitProject) {
		this.commitProject = commitProject;
	}
	
	
	public String toCSV() {
		return getNameProject() + "," + getUrlProject() + "," + getCommitProject() + "," + isPassInTests() + "," + isJacocoGenerate();
	}
	
}
