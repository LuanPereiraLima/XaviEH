package ufc.br.xavieh.processors;

import spoon.reflect.declaration.CtElement;

public abstract class AbstractorProcessorSubProcess<A extends CtElement> extends AbstractorProcessor<A>{
	private int positionProcess = 1;
	
	public void resetPositionProcess() {
		this.positionProcess = 1;
	}
	
	public int getPositionProcess() {
		return positionProcess;
	}
	
	public void setPositionProcess(int positionProcess) {
		this.positionProcess = positionProcess;
	}
	
	public void incrementPositionProcess() {
		this.positionProcess++;
	}
}
