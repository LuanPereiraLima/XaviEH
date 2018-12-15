package ufc.br.mutant_project.models;

public class ClassXMLCoverageLineNormal extends ClassXMLCoverageLine{
	
	private int mi;
	private int ci;
	private int mb;
	private int cb;
	
	public int getMi() {
		return mi;
	}
	
	public void setMi(int mi) {
		this.mi = mi;
	}
	
	public int getCi() {
		return ci;
	}
	
	public void setCi(int ci) {
		this.ci = ci;
	}
	
	public int getMb() {
		return mb;
	}
	
	public void setMb(int mb) {
		this.mb = mb;
	}
	
	public int getCb() {
		return cb;
	}
	
	public void setCb(int cb) {
		this.cb = cb;
	}

	@Override
	public String toString() {
		return "ClassXMLCoverageLine [numberLine=" + getNumberLine() + ", mi=" + mi + ", ci=" + ci + ", mb=" + mb + ", cb="
				+ cb + "]";
	}
	
	@Override
	public String toStringCSV() {
		return ""+mi+","+ci+","+mb+","+cb+",,,"+getType();
	}	
	
	public boolean verifyCoverage() {
		if(getMb() > 0 || getCb() > 0) {
			setType(this.BRANCH);
			if(getCb() / (getMb() + getCb()) == 0) {
				return (false);
			}else if(getCb() / (getMb() + getCb()) == 1) {
				return (true);
			}else {
				return (true);
			}
		}else if(getMb() == 0 && getCb() == 0){
			setType(this.INSTRUCTION);
			if(getMi() == 0) {
				return (true);
			}else {
				return (false);
			}
		}
		return false;
	}
}