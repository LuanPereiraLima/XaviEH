package ufc.br.xavieh.models;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.reference.CtTypeReference;

public class CHE {
	private List<CtTypeReference<?>> classes;
	private List<CHE> children;
	
	public List<CtTypeReference<?>> getClasses() {
		return classes;
	}
	
	public List<CHE> getFilhos() {
		return children;
	}
	
	public void setFilhos(List<CHE> children) {
		this.children = children;
	}
	
	public void addFilho(CHE child) {
		if(children!=null) {
			if(children.contains(child)) return;
			children.add(child);
		}else {
			children = new ArrayList<CHE>();
			children.add(child);
		}
	}
	
	public void setClasses(List<CtTypeReference<?>> classes) {
		this.classes = classes;
	}
	
	public void addCtTypeReference(CtTypeReference<?> ref) {
		if(classes!=null) {
			if(classes.contains(ref)) return;
			classes.add(ref);
		}else {
			classes = new ArrayList<CtTypeReference<?>>();
			classes.add(ref);
		}
	}
	
	

	public void print(int level, CHE che) {
		
		if(che.getClasses()!=null) {
			for(int i=0; i<level; i++)
				System.out.print("\t");
			System.out.print(che.getClasses()+"\n");
		}
		
		if(che.getFilhos()!=null) {
			for(int i=0; i < che.getFilhos().size(); i++) {
				print(level+1+i, che.getFilhos().get(i));
			}
		}
		
		
	}
	
	public void print(int nivel, List<CHE> filhos) {
		if(filhos==null)
			return;
		for(CHE c : filhos) {
			for(int i=0; i<nivel; i++)
				System.out.print("\t");
			
			System.out.println(c.getClasses());
			
			print(nivel++, c.getFilhos());
		}
	}
}
