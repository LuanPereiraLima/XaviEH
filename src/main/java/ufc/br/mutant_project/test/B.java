package ufc.br.mutant_project.test;

import java.io.IOException;

public class B {
	@SuppressWarnings("unused")
	public void ts() throws IOException, TW1 {
		
		if(false) {
			throw new TW1();
		}

		if(false){
			throw new TW4();
		}
		
		try {
			th();
		}catch(Throwable e) {

		}
		
		throw new TW5();
	}
	
	public void th() throws TW6 {
		throw new TW6();
	}
}
