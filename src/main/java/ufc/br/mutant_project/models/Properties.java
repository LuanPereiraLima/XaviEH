package ufc.br.mutant_project.models;

public class Properties {
	private String homeMaven;
	private String projectsFile;
	private String urlMutations;
	private boolean deleteProject;
	
	public String getHomeMaven() {
		return homeMaven;
	}
	
	public void setHomeMaven(String homeMaven) {
		this.homeMaven = homeMaven;
	}

	public String getProjectsFile() {
		return projectsFile;
	}

	public void setProjectsFile(String projectsFile) {
		this.projectsFile = projectsFile;
	}

	public String getUrlMutations() {
		return urlMutations;
	}

	public void setUrlMutations(String urlMutations) {
		this.urlMutations = urlMutations;
	}

	public boolean isDeleteProject() {
		return deleteProject;
	}

	public void setDeleteProject(boolean deleteProject) {
		this.deleteProject = deleteProject;
	}
}
