package ufc.br.xavieh.models;

public class TotalCoveragedAndNotCoveragedCatch {
    private String projectName;
    private int quantityNotCoveraged;
    private int quantityCoveraged;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getQuantityNotCoveraged() {
        return quantityNotCoveraged;
    }

    public void setQuantityNotCoveraged(int quantityNotCoveraged) {
        this.quantityNotCoveraged = quantityNotCoveraged;
    }

    public int getQuantityCoveraged() {
        return quantityCoveraged;
    }

    public void setQuantityCoveraged(int quantityCoveraged) {
        this.quantityCoveraged = quantityCoveraged;
    }

    @Override
    public String toString() {
        return projectName + ',' + quantityNotCoveraged + "," + quantityCoveraged;
    }
}
