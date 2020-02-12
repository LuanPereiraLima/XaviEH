package ufc.br.xavieh.models;

public class FinalResultSavedByProject {
	
	private String mutationType;
	private String abbreviationMutationType;
	private Integer numberOfMutants;
	private Integer amountOfLiveMutants;
	private Integer amountOfDeadMutants;
	private Double fractionOfMutantsKilledByNumberOfMutants;
	
	public FinalResultSavedByProject(String mutationType, String abbreviationMutationType, Integer numberOfMutants,
			Integer amountOfLiveMutants, Integer amountOfDeadMutants, Double fractionOfMutantsKilledByNumberOfMutants) {
		this.mutationType = mutationType;
		this.abbreviationMutationType = abbreviationMutationType;
		this.numberOfMutants = numberOfMutants;
		this.amountOfLiveMutants = amountOfLiveMutants;
		this.amountOfDeadMutants = amountOfDeadMutants;
		this.fractionOfMutantsKilledByNumberOfMutants = fractionOfMutantsKilledByNumberOfMutants;
	}
	
	public FinalResultSavedByProject() {
	}
	
	public String getMutationType() {
		return mutationType;
	}
	public void setMutationType(String mutationType) {
		this.mutationType = mutationType;
	}
	public String getAbbreviationMutationType() {
		return abbreviationMutationType;
	}
	public void setAbbreviationMutationType(String abbreviationMutationType) {
		this.abbreviationMutationType = abbreviationMutationType;
	}
	public Integer getAmountOfLiveMutants() {
		return amountOfLiveMutants;
	}
	public void setAmountOfLiveMutants(Integer amountOfLiveMutants) {
		this.amountOfLiveMutants = amountOfLiveMutants;
	}
	public Integer getAmountOfDeadMutants() {
		return amountOfDeadMutants;
	}
	public void setAmountOfDeadMutants(Integer amountOfDeadMutants) {
		this.amountOfDeadMutants = amountOfDeadMutants;
	}
	public Double getFractionOfMutantsKilledByNumberOfMutants() {
		return this.fractionOfMutantsKilledByNumberOfMutants;
	}
	public void setFractionOfMutantsKilledByNumberOfMutants(Double fractionOfMutantsKilledByNumberOfMutants) {
		this.fractionOfMutantsKilledByNumberOfMutants = fractionOfMutantsKilledByNumberOfMutants;
	}
	public Integer getNumberOfMutants() {
		return numberOfMutants;
	}
	public void setNumberOfMutants(Integer numberOfMutants) {
		this.numberOfMutants = numberOfMutants;
	}
}
