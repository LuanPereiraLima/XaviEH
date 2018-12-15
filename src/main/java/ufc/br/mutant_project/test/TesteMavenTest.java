package ufc.br.mutant_project.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

import spoon.MavenLauncher;
import spoon.SpoonAPI;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.Filter;

public class TesteMavenTest {
	public static void main(String[] args) {
	
		Map<String, List<String>> mapsss = new HashedMap();
		
		SpoonAPI spoon = new MavenLauncher("/home/luan/commons-io", MavenLauncher.SOURCE_TYPE.TEST_SOURCE);
		spoon.buildModel();
		
		MavenLauncher spoo = new MavenLauncher("/home/luan/commons-io", MavenLauncher.SOURCE_TYPE.APP_SOURCE);
		
		spoo.buildModel();
		
		//spoon.getModel().getAllTypes().forEach( a -> {
			//System.out.println(a);
		//});
		
		//Iterator<?> i = spoon.getModel().getAllTypes().iterator();
		

		spoo.getModel().getElements(new Filter<CtClass<?>>() {
			@Override
			public boolean matches(CtClass<?> element) {
				// TODO Auto-generated method stub
				System.out.println(element.getQualifiedName());
				mapsss.put(element.getQualifiedName(), new ArrayList<>());
				return false;
			}
		});
		
		System.out.println("_---___--_-__----_--_-_--_");
		
		spoon.getModel().getElements(new Filter<CtClass<?>>() {
			@Override
			public boolean matches(CtClass<?> element) {
				// TODO Auto-generated method stub
				System.out.println(element.getQualifiedName());
				
				element.getUsedTypes(true).forEach(a -> {
					if(mapsss.containsKey(a.getQualifiedName())) {
						mapsss.get(a.getQualifiedName()).add(element.getQualifiedName());
					}
				});;
				return false;
			}
		});
		
		for(String s : mapsss.keySet()) {
			System.out.println("Class: "+s);
			for(String ss: mapsss.get(s)) {
				System.out.println("--: "+ss);
			}
		}
		
		
		//i.next();
		//i.next();
		//CtClass a = (CtClass) i.next();
		
		//System.out.println(a);
		
	/*	a.getUsedTypes(true).forEach(ab -> {
			System.out.println(ab);
		});
		System.out.println("--------");
		a.getReferencedTypes().forEach(ab -> {
			System.out.println(ab);
		});*/
		
		
	}
}
