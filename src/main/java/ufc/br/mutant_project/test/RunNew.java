package ufc.br.mutant_project.test;

import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.code.CtThrow;
import spoon.reflect.visitor.Filter;


public class RunNew {

	public static void main(String[] args) {

		//String path2 = "/Users/lincolnrocha/Documents/tpii-2018-workspace/bank-sys/src/main/java";

		//String path = "/home/loopback/mutationsTests/xstream-1.4.11.1/xstream-1.4.11.1/xstream/src/java";

		//String path = "/media/loopback/C4DAE5FEDAE5EC9C/Users/luan_/mutationsTests/commons-math-3.6.1/commons-math-3.6.1";//"/home/loopback/hadoop/hadoop-common-project/hadoop-kms";///src/main/java/org/apache/hadoop/mapred/ShuffleHandler.java";
		String path = "/media/loopback/C4DAE5FEDAE5EC9C/Users/luan_/mutationsTests2/commons-dbcp-2.5/commons-dbcp-2.5";

		//String path = "/home/loopback/mutationsDocker/hadoop-3.1.2-20/hadoop-3.1.2-20/hadoop-common-project/hadoop-kms";

		//Launcher spoon = new Launcher();

		//Launcher spoon = new MavenLauncher(path, MavenLauncher.SOURCE_TYPE.APP_SOURCE);

		SpoonAPI spoon = new Launcher();
		spoon.getEnvironment().setNoClasspath(true);
		spoon.getEnvironment().setCommentEnabled(true);
		spoon.addInputResource(path);

		spoon.getEnvironment().setNoClasspath(true);

		//spoon.getEnvironment().setSourceClasspath(new String[]{path});

		//spoon.addInputResource( path );

		//spoon.getEnvironment().setAutoImports(true);

		//spoon.setSourceOutputDirectory("/home/loopback/spoon");

		//spoon.getEnvironment().setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(spoon.getEnvironment()));

		//System.out.println(spoon.createPrettyPrinter().getResult());

		//spoon.run();

		//spoon.buildModel();

		//spoon.prettyprint();
		/*spoon.addProcessor(new AbstractProcessor<CtTry>() {
			@Override
			public void process(CtTry element) {

				//if(!element.getQualifiedName().equals("ShuffleHandler")){
					//return;
				//}

				//System.out.println(element.toStringWithImports());
//				System.exit(0);
				if(element.getCatchers()!=null && element.getCatchers().size() > 0){
					if(element.getFinalizer()==null){
						element.setFinalizer(getFactory().createBlock());

					}
					element.setCatchers(null);
				}
			}
		});

		spoon.run();*/

		spoon.buildModel();

		System.out.println(spoon.getModel().getElements((Filter<CtThrow>) ctElement -> true).stream().count());

		/*CtModel model = spoon.getModel();

		for(CtType<?> md : model.getAllTypes()){

			if(md.isClass()) {
				CtClass cl = (CtClass) md;
			}
			System.out.println();
			System.out.println("IE: "+md.isTopLevel());
			System.out.println(md.isClass());
			System.out.println(md.getSimpleName());
		}*/
	}

		/*

		System.out.println("All Raisings");

		for (CtClass<?> element : model.getElements(new TypeFilter<CtClass<?>>(CtClass.class))) {

			List<CtThrow> raisers = element.getElements(new TypeFilter<CtThrow>(CtThrow.class));

			if (!raisers.isEmpty()) {

				System.out.println(element.getQualifiedName());

				for (CtThrow raiser : raisers) {

					System.out.println(

							"-- Throws [" + raiser.getPosition().getLine() + "," + raiser.getPosition().getEndLine()

									+ "]: " + raiser.getThrownExpression().getType().getQualifiedName());

				}

			}

		}

		

		System.out.println("\n\nRaisings of Programmer Defined Exception");

		for (CtClass<?> element : model.getElements(new TypeFilter<CtClass<?>>(CtClass.class))) {

			List<CtThrow> raisers = findRaisers(true, element);

			if (!raisers.isEmpty()) {

				System.out.println(element.getQualifiedName());

				for (CtThrow raiser : raisers) {

					System.out.println(

							"-- Throws [" + raiser.getPosition().getLine() + "," + raiser.getPosition().getEndLine()

									+ "]: " + raiser.getThrownExpression().getType().getQualifiedName());

				}

			}

		}


		System.out.println("\n\nRaisings of Non Programmer Defined Exception");

		for (CtClass<?> element : model.getElements(new TypeFilter<CtClass<?>>(CtClass.class))) {

			List<CtThrow> raisers = findRaisers(false, element);

			if (!raisers.isEmpty()) {

				System.out.println(element.getQualifiedName());

				for (CtThrow raiser : raisers) {

					System.out.println(

							"-- Throws [" + raiser.getPosition().getLine() + "," + raiser.getPosition().getEndLine()

									+ "]: " + raiser.getThrownExpression().getType().getQualifiedName());

				}

			}

		}

		

		System.out.println("\n\nAll Handlings");

		for (CtClass<?> element : model.getElements(new TypeFilter<CtClass<?>>(CtClass.class))) {

			List<CtCatch> catchers = element.getElements(new TypeFilter<CtCatch>(CtCatch.class));

			if (!catchers.isEmpty()) {

				System.out.println(element.getQualifiedName());

				for(CtCatch handler : catchers) {

					System.out.println(

							"-- Catch [" + handler.getPosition().getLine() + "," + handler.getPosition().getEndLine()

									+ "]: " + handler.getParameter().getMultiTypes().toString());

				}

			}

		}

		

		System.out.println("\n\nHandlings of Programmer Defined Exception");

		for (CtClass<?> element : model.getElements(new TypeFilter<CtClass<?>>(CtClass.class))) {

			List<CtCatch> catchers = findHandlers(true, element);

			if (!catchers.isEmpty()) {

				System.out.println(element.getQualifiedName());

				for(CtCatch handler : catchers) {

					System.out.println(

							"-- Catch [" + handler.getPosition().getLine() + "," + handler.getPosition().getEndLine()

									+ "]: " + handler.getParameter().getMultiTypes().toString());

				}

			}

		}

		

		System.out.println("\n\nHandlings of Non Programmer Defined Exception");

		for (CtClass<?> element : model.getElements(new TypeFilter<CtClass<?>>(CtClass.class))) {

			List<CtCatch> catchers = findHandlers(false, element);

			if (!catchers.isEmpty()) {

				System.out.println(element.getQualifiedName());

				for(CtCatch handler : catchers) {

					System.out.println(

							"-- Catch [" + handler.getPosition().getLine() + "," + handler.getPosition().getEndLine()

									+ "]: " + handler.getParameter().getMultiTypes().toString());

				}

			}

		}

	}


	private static List<CtThrow> findRaisers(boolean isProgramerDefined, CtClass<?> element) {

		List<CtThrow> result = new ArrayList<CtThrow>();

		for (CtThrow raiser : element.getElements(new TypeFilter<CtThrow>(CtThrow.class))) {

			CtType<?> declaredType = raiser.getThrownExpression().getType().getTypeDeclaration();


			if (isProgramerDefined) {

				if (declaredType != null && !declaredType.isShadow()) {

					result.add(raiser);

				}

			} else {

				if (declaredType == null || declaredType.isShadow()) {

					result.add(raiser);

				}

			}

		}

		return result;

	}


	private static List<CtCatch> findHandlers(boolean isProgramerDefined, CtClass<?> element) {

		List<CtCatch> result = new ArrayList<CtCatch>();

		for (CtCatch handler : element.getElements(new TypeFilter<CtCatch>(CtCatch.class))) {

			for (CtTypeReference<?> exceptionType : handler.getParameter().getMultiTypes()) {

				CtType<?> declaredType = exceptionType.getTypeDeclaration();


				if (isProgramerDefined) {

					if (declaredType != null && !declaredType.isShadow()) {

						result.add(handler);

						break;

					}

				} else {

					if (declaredType == null || declaredType.isShadow()) {

						result.add(handler);

						break;
					}
				}
			}
		}
		return result;
	}*/
}