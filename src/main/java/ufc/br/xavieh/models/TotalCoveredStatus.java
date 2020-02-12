package ufc.br.xavieh.models;

public class TotalCoveredStatus {
	
	private int MI_TotalMissedInstructions;
	private int CI_TotalCoveredInstructions;
	private int MB_TotalMissedBraches;
	private int CB_TotalCoveredBraches;
	private int MM_TotalMissedMethods;
	private int CM_TotalCoveredMethods;
	private int MC_TotalMissedClasses;
	private int CC_TotalCoveredClasses;
	
	private int TRY_MI_TotalMissedInstructionsTryBlocks;
	private int TRY_CI_TotalCoveredInstructionsTryBlocks;
	private int TRY_MB_TotalMissedBrachesTryBlocks;
	private int TRY_CB_TotalCoveredBrachesTryBlocks;
	
	private int CATCH_MI_TotalMissedInstructionsCatchBlocks;
	private int CATCH_CI_TotalCoveredInstructionsCatchBlocks;
	private int CATCH_MB_TotalMissedBrachesCatchBlocks;
	private int CATCH_CB_TotalCoveredBrachesCatchBlocks;
	private int CATCH_I_MI_TotalMissedInstructionsCatchBlocks;
	private int CATCH_I_CI_TotalCoveredInstructionsCatchBlocks;
	private int CATCH_I_MB_TotalMissedBrachesCatchBlocks;
	private int CATCH_I_CB_TotalCoveredBrachesCatchBlocks;
	private int CATCH_E_MI_TotalMissedInstructionsCatchBlocks;
	private int CATCH_E_CI_TotalCoveredInstructionsCatchBlocks;
	private int CATCH_E_MB_TotalMissedBrachesCatchBlocks;
	private int CATCH_E_CB_TotalCoveredBrachesCatchBlocks;
	
	private int FINALLY_MI_TotalMissedInstructionsFinallyBlocks;
	private int FINALLY_CI_TotalCoveredInstructionsFinallyBlocks;
	private int FINALLY_MB_TotalMissedBrachesFinallyBlocks;
	private int FINALLY_CB_TotalCoveredBrachesFinallyBlocks; 
	
	private int THROW_MI_TotalMissedInstructionsThrowStatements;
	private int THROW_CI_TotalCoveredInstructionsThrowStatements; 
	private int THROW_I_MI_TotalMissedInstructionsThrowStatements;
	private int THROW_I_CI_TotalCoveredInstructionsThrowStatements; 
	private int THROW_E_MI_TotalMissedInstructionsThrowStatements;
	private int THROW_E_CI_TotalCoveredInstructionsThrowStatements; 
	
	private int THROWS_MM_TotalMissedMethodsWithThrows;
	private int THROWS_CM_TotalCoveredMethodsWithThrows;
	
	public int getMI_TotalMissedInstructions() {
		return MI_TotalMissedInstructions;
	}
	public void setMI_TotalMissedInstructions(int mI_TotalMissedInstructions) {
		MI_TotalMissedInstructions = mI_TotalMissedInstructions;
	}
	public int getCI_TotalCoveredInstructions() {
		return CI_TotalCoveredInstructions;
	}
	public void setCI_TotalCoveredInstructions(int cI_TotalCoveredInstructions) {
		CI_TotalCoveredInstructions = cI_TotalCoveredInstructions;
	}
	public int getMB_TotalMissedBraches() {
		return MB_TotalMissedBraches;
	}
	public void setMB_TotalMissedBraches(int mB_TotalMissedBraches) {
		MB_TotalMissedBraches = mB_TotalMissedBraches;
	}
	public int getCB_TotalCoveredBraches() {
		return CB_TotalCoveredBraches;
	}
	public void setCB_TotalCoveredBraches(int cB_TotalCoveredBraches) {
		CB_TotalCoveredBraches = cB_TotalCoveredBraches;
	}
	public int getMM_TotalMissedMethods() {
		return MM_TotalMissedMethods;
	}
	public void setMM_TotalMissedMethods(int mM_TotalMissedMethods) {
		MM_TotalMissedMethods = mM_TotalMissedMethods;
	}
	public int getCM_TotalCoveredMethods() {
		return CM_TotalCoveredMethods;
	}
	public void setCM_TotalCoveredMethods(int cM_TotalCoveredMethods) {
		CM_TotalCoveredMethods = cM_TotalCoveredMethods;
	}
	public int getTRY_MI_TotalMissedInstructionsTryBlocks() {
		return TRY_MI_TotalMissedInstructionsTryBlocks;
	}
	public void setTRY_MI_TotalMissedInstructionsTryBlocks(int tRY_MI_TotalMissedInstructionsTryBlocks) {
		TRY_MI_TotalMissedInstructionsTryBlocks = tRY_MI_TotalMissedInstructionsTryBlocks;
	}
	public int getTRY_CI_TotalCoveredInstructionsTryBlocks() {
		return TRY_CI_TotalCoveredInstructionsTryBlocks;
	}
	public void setTRY_CI_TotalCoveredInstructionsTryBlocks(int tRY_CI_TotalCoveredInstructionsTryBlocks) {
		TRY_CI_TotalCoveredInstructionsTryBlocks = tRY_CI_TotalCoveredInstructionsTryBlocks;
	}
	public int getTRY_MB_TotalMissedBrachesTryBlocks() {
		return TRY_MB_TotalMissedBrachesTryBlocks;
	}
	public void setTRY_MB_TotalMissedBrachesTryBlocks(int tRY_MB_TotalMissedBrachesTryBlocks) {
		TRY_MB_TotalMissedBrachesTryBlocks = tRY_MB_TotalMissedBrachesTryBlocks;
	}
	public int getTRY_CB_TotalCoveredBrachesTryBlocks() {
		return TRY_CB_TotalCoveredBrachesTryBlocks;
	}
	public void setTRY_CB_TotalCoveredBrachesTryBlocks(int tRY_CB_TotalCoveredBrachesTryBlocks) {
		TRY_CB_TotalCoveredBrachesTryBlocks = tRY_CB_TotalCoveredBrachesTryBlocks;
	}
	public int getCATCH_MI_TotalMissedInstructionsCatchBlocks() {
		return CATCH_MI_TotalMissedInstructionsCatchBlocks;
	}
	public void setCATCH_MI_TotalMissedInstructionsCatchBlocks(int cATCH_MI_TotalMissedInstructionsCatchBlocks) {
		CATCH_MI_TotalMissedInstructionsCatchBlocks = cATCH_MI_TotalMissedInstructionsCatchBlocks;
	}
	public int getCATCH_CI_TotalCoveredInstructionsCatchBlocks() {
		return CATCH_CI_TotalCoveredInstructionsCatchBlocks;
	}
	public void setCATCH_CI_TotalCoveredInstructionsCatchBlocks(int cATCH_CI_TotalCoveredInstructionsCatchBlocks) {
		CATCH_CI_TotalCoveredInstructionsCatchBlocks = cATCH_CI_TotalCoveredInstructionsCatchBlocks;
	}
	public int getCATCH_MB_TotalMissedBrachesCatchBlocks() {
		return CATCH_MB_TotalMissedBrachesCatchBlocks;
	}
	public void setCATCH_MB_TotalMissedBrachesCatchBlocks(int cATCH_MB_TotalMissedBrachesCatchBlocks) {
		CATCH_MB_TotalMissedBrachesCatchBlocks = cATCH_MB_TotalMissedBrachesCatchBlocks;
	}
	public int getCATCH_CB_TotalCoveredBrachesCatchBlocks() {
		return CATCH_CB_TotalCoveredBrachesCatchBlocks;
	}
	public void setCATCH_CB_TotalCoveredBrachesCatchBlocks(int cATCH_CB_TotalCoveredBrachesCatchBlocks) {
		CATCH_CB_TotalCoveredBrachesCatchBlocks = cATCH_CB_TotalCoveredBrachesCatchBlocks;
	}
	public int getFINALLY_MI_TotalMissedInstructionsFinallyBlocks() {
		return FINALLY_MI_TotalMissedInstructionsFinallyBlocks;
	}
	public void setFINALLY_MI_TotalMissedInstructionsFinallyBlocks(int fINALLY_MI_TotalMissedInstructionsFinallyBlocks) {
		FINALLY_MI_TotalMissedInstructionsFinallyBlocks = fINALLY_MI_TotalMissedInstructionsFinallyBlocks;
	}
	public int getFINALLY_CI_TotalCoveredInstructionsFinallyBlocks() {
		return FINALLY_CI_TotalCoveredInstructionsFinallyBlocks;
	}
	public void setFINALLY_CI_TotalCoveredInstructionsFinallyBlocks(int fINALLY_CI_TotalCoveredInstructionsFinallyBlocks) {
		FINALLY_CI_TotalCoveredInstructionsFinallyBlocks = fINALLY_CI_TotalCoveredInstructionsFinallyBlocks;
	}
	public int getFINALLY_MB_TotalMissedBrachesFinallyBlocks() {
		return FINALLY_MB_TotalMissedBrachesFinallyBlocks;
	}
	public void setFINALLY_MB_TotalMissedBrachesFinallyBlocks(int fINALLY_MB_TotalMissedBrachesFinallyBlocks) {
		FINALLY_MB_TotalMissedBrachesFinallyBlocks = fINALLY_MB_TotalMissedBrachesFinallyBlocks;
	}
	public int getFINALLY_CB_TotalCoveredBrachesFinallyBlocks() {
		return FINALLY_CB_TotalCoveredBrachesFinallyBlocks;
	}
	public void setFINALLY_CB_TotalCoveredBrachesFinallyBlocks(int fINALLY_CB_TotalCoveredBrachesFinallyBlocks) {
		FINALLY_CB_TotalCoveredBrachesFinallyBlocks = fINALLY_CB_TotalCoveredBrachesFinallyBlocks;
	}
	public int getTHROW_MI_TotalMissedInstructionsThrowStatements() {
		return THROW_MI_TotalMissedInstructionsThrowStatements;
	}
	public void setTHROW_MI_TotalMissedInstructionsThrowStatements(int tHROW_MI_TotalMissedInstructionsThrowStatements) {
		THROW_MI_TotalMissedInstructionsThrowStatements = tHROW_MI_TotalMissedInstructionsThrowStatements;
	}
	public int getTHROW_CI_TotalCoveredInstructionsThrowStatements() {
		return THROW_CI_TotalCoveredInstructionsThrowStatements;
	}
	public void setTHROW_CI_TotalCoveredInstructionsThrowStatements(int tHROW_CI_TotalCoveredInstructionsThrowStatements) {
		THROW_CI_TotalCoveredInstructionsThrowStatements = tHROW_CI_TotalCoveredInstructionsThrowStatements;
	}
	public int getTHROWS_MM_TotalMissedMethodsWithThrows() {
		return THROWS_MM_TotalMissedMethodsWithThrows;
	}
	public void setTHROWS_MM_TotalMissedMethodsWithThrows(int tHROWS_MM_TotalMissedMethodsWithThrows) {
		THROWS_MM_TotalMissedMethodsWithThrows = tHROWS_MM_TotalMissedMethodsWithThrows;
	}
	public int getTHROWS_CM_TotalCoveredMethodsWithThrows() {
		return THROWS_CM_TotalCoveredMethodsWithThrows;
	}
	public void setTHROWS_CM_TotalCoveredMethodsWithThrows(int tHROWS_CM_TotalCoveredMethodsWithThrows) {
		THROWS_CM_TotalCoveredMethodsWithThrows = tHROWS_CM_TotalCoveredMethodsWithThrows;
	}
	
	public int getCC_TotalCoveredClasses() {
		return CC_TotalCoveredClasses;
	}
	
	public void setMC_TotalMissedClasses(int mC_TotalMissedClasses) {
		MC_TotalMissedClasses = mC_TotalMissedClasses;
	}
	
	public int getMC_TotalMissedClasses() {
		return MC_TotalMissedClasses;
	}
	
	public void setCC_TotalCoveredClasses(int cC_TotalCoveredClasses) {
		CC_TotalCoveredClasses = cC_TotalCoveredClasses;
	}
	public int getCATCH_I_MI_TotalMissedInstructionsCatchBlocks() {
		return CATCH_I_MI_TotalMissedInstructionsCatchBlocks;
	}
	public void setCATCH_I_MI_TotalMissedInstructionsCatchBlocks(int cATCH_I_MI_TotalMissedInstructionsCatchBlocks) {
		CATCH_I_MI_TotalMissedInstructionsCatchBlocks = cATCH_I_MI_TotalMissedInstructionsCatchBlocks;
	}
	public int getCATCH_I_CI_TotalCoveredInstructionsCatchBlocks() {
		return CATCH_I_CI_TotalCoveredInstructionsCatchBlocks;
	}
	public void setCATCH_I_CI_TotalCoveredInstructionsCatchBlocks(int cATCH_I_CI_TotalCoveredInstructionsCatchBlocks) {
		CATCH_I_CI_TotalCoveredInstructionsCatchBlocks = cATCH_I_CI_TotalCoveredInstructionsCatchBlocks;
	}
	public int getCATCH_I_MB_TotalMissedBrachesCatchBlocks() {
		return CATCH_I_MB_TotalMissedBrachesCatchBlocks;
	}
	public void setCATCH_I_MB_TotalMissedBrachesCatchBlocks(int cATCH_I_MB_TotalMissedBrachesCatchBlocks) {
		CATCH_I_MB_TotalMissedBrachesCatchBlocks = cATCH_I_MB_TotalMissedBrachesCatchBlocks;
	}
	public int getCATCH_I_CB_TotalCoveredBrachesCatchBlocks() {
		return CATCH_I_CB_TotalCoveredBrachesCatchBlocks;
	}
	public void setCATCH_I_CB_TotalCoveredBrachesCatchBlocks(int cATCH_I_CB_TotalCoveredBrachesCatchBlocks) {
		CATCH_I_CB_TotalCoveredBrachesCatchBlocks = cATCH_I_CB_TotalCoveredBrachesCatchBlocks;
	}
	public int getCATCH_E_MI_TotalMissedInstructionsCatchBlocks() {
		return CATCH_E_MI_TotalMissedInstructionsCatchBlocks;
	}
	public void setCATCH_E_MI_TotalMissedInstructionsCatchBlocks(int cATCH_E_MI_TotalMissedInstructionsCatchBlocks) {
		CATCH_E_MI_TotalMissedInstructionsCatchBlocks = cATCH_E_MI_TotalMissedInstructionsCatchBlocks;
	}
	public int getCATCH_E_CI_TotalCoveredInstructionsCatchBlocks() {
		return CATCH_E_CI_TotalCoveredInstructionsCatchBlocks;
	}
	public void setCATCH_E_CI_TotalCoveredInstructionsCatchBlocks(int cATCH_E_CI_TotalCoveredInstructionsCatchBlocks) {
		CATCH_E_CI_TotalCoveredInstructionsCatchBlocks = cATCH_E_CI_TotalCoveredInstructionsCatchBlocks;
	}
	public int getCATCH_E_MB_TotalMissedBrachesCatchBlocks() {
		return CATCH_E_MB_TotalMissedBrachesCatchBlocks;
	}
	public void setCATCH_E_MB_TotalMissedBrachesCatchBlocks(int cATCH_E_MB_TotalMissedBrachesCatchBlocks) {
		CATCH_E_MB_TotalMissedBrachesCatchBlocks = cATCH_E_MB_TotalMissedBrachesCatchBlocks;
	}
	public int getCATCH_E_CB_TotalCoveredBrachesCatchBlocks() {
		return CATCH_E_CB_TotalCoveredBrachesCatchBlocks;
	}
	public void setCATCH_E_CB_TotalCoveredBrachesCatchBlocks(int cATCH_E_CB_TotalCoveredBrachesCatchBlocks) {
		CATCH_E_CB_TotalCoveredBrachesCatchBlocks = cATCH_E_CB_TotalCoveredBrachesCatchBlocks;
	}
	public int getTHROW_I_MI_TotalMissedInstructionsThrowStatements() {
		return THROW_I_MI_TotalMissedInstructionsThrowStatements;
	}
	public void setTHROW_I_MI_TotalMissedInstructionsThrowStatements(
			int tHROW_I_MI_TotalMissedInstructionsThrowStatements) {
		THROW_I_MI_TotalMissedInstructionsThrowStatements = tHROW_I_MI_TotalMissedInstructionsThrowStatements;
	}
	public int getTHROW_I_CI_TotalCoveredInstructionsThrowStatements() {
		return THROW_I_CI_TotalCoveredInstructionsThrowStatements;
	}
	public void setTHROW_I_CI_TotalCoveredInstructionsThrowStatements(
			int tHROW_I_CI_TotalCoveredInstructionsThrowStatements) {
		THROW_I_CI_TotalCoveredInstructionsThrowStatements = tHROW_I_CI_TotalCoveredInstructionsThrowStatements;
	}
	public int getTHROW_E_MI_TotalMissedInstructionsThrowStatements() {
		return THROW_E_MI_TotalMissedInstructionsThrowStatements;
	}
	public void setTHROW_E_MI_TotalMissedInstructionsThrowStatements(
			int tHROW_E_MI_TotalMissedInstructionsThrowStatements) {
		THROW_E_MI_TotalMissedInstructionsThrowStatements = tHROW_E_MI_TotalMissedInstructionsThrowStatements;
	}
	public int getTHROW_E_CI_TotalCoveredInstructionsThrowStatements() {
		return THROW_E_CI_TotalCoveredInstructionsThrowStatements;
	}
	public void setTHROW_E_CI_TotalCoveredInstructionsThrowStatements(
			int tHROW_E_CI_TotalCoveredInstructionsThrowStatements) {
		THROW_E_CI_TotalCoveredInstructionsThrowStatements = tHROW_E_CI_TotalCoveredInstructionsThrowStatements;
	}
	
	
}
