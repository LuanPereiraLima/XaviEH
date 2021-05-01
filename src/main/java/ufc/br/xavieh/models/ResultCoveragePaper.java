package ufc.br.xavieh.models;

/*
CLASS_NAME,QUANTITY_CLASS_NOT_ALL_COVERAGED,NUMBER_CLASSES_SAMPLE,CLASSES_SORTED
 */

public class ResultCoveragePaper {
	private int quantityClassNotCoverage;
	private String nameClass;
	private long numberClassSample;
	private String classesSorted;

	public int getQuantityClassNotCoverage() {
		return quantityClassNotCoverage;
	}

	public void setQuantityClassNotCoverage(int quantityClassNotCoverage) {
		this.quantityClassNotCoverage = quantityClassNotCoverage;
	}

	public String getNameClass() {
		return nameClass;
	}

	public void setNameClass(String nameClass) {
		this.nameClass = nameClass;
	}

	public long getNumberClassSample() {
		return numberClassSample;
	}

	public void setNumberClassSample(long numberClassSample) {
		this.numberClassSample = numberClassSample;
	}

	public String getClassesSorted() {
		return classesSorted;
	}

	public void setClassesSorted(String classesSorted) {
		this.classesSorted = classesSorted;
	}
}
