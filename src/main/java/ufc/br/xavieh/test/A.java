package ufc.br.xavieh.test;

import java.io.IOException;

public class A {
	public void teste() throws IOException, TW1{
		B b = null;
		try {
			
			
			try {
				b = new B();
				b.ts();
			}catch(TW1 e) {
				throw e;
			}
			
		}catch(TW2 i) {
			
		}catch(Exception e) {
			
		}catch(Throwable i) {
			
		}
		
		b.ts();
	}
}
