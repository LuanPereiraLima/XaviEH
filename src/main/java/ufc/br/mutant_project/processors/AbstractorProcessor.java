package ufc.br.mutant_project.processors;

import java.util.Map;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtElement;
import ufc.br.mutant_project.models.CHE;
import ufc.br.mutant_project.models.ParameterProcessor;

public abstract class AbstractorProcessor<A extends CtElement> extends AbstractProcessor<A>{
	private int position = 1;
	private Map<Integer, CHE> map;
	
	private ParameterProcessor parameterVisitor;
	private String uriName;
	private String subModule;
	
	public abstract String pathIdentification();
	public abstract String name();

	public void resetPosition() {
		this.position = 1;
	}

	public ParameterProcessor getParameterVisitor() {
		return parameterVisitor;
	}
	
	public void setParameterVisitor(ParameterProcessor parameterVisitor) {
		this.parameterVisitor = parameterVisitor;
	}
	
	public Map<Integer, CHE> getMap() {
		return map;
	}
	
	public void setMap(Map<Integer, CHE> map) {
		this.map = map;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public String getUriName() {
		return uriName;
	}
	
	public void setUriName(String uriName) {
		this.uriName = uriName;
	}
	
	public String getSubModule() {
		return subModule;
	}
	
	public void setSubModule(String subModule) {
		this.subModule = subModule;
	}
	 
	public void incrementPosition() {
		this.position++;
	}
}
