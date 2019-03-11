package ufc.br.mutant_project.test;

import java.util.ArrayList;
import java.util.List;

import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtThrow;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;


public class RunNew {

	public static void main(String[] args) {

		//String path2 = "/Users/lincolnrocha/Documents/tpii-2018-workspace/bank-sys/src/main/java";
		
		String path = "/home/loopback/mutationsTests/xstream-1.4.11.1/xstream-1.4.11.1/xstream/src/java";

		SpoonAPI spoon = new Launcher();

		spoon = new Launcher();

		spoon.getEnvironment().setNoClasspath(true);

		spoon.getEnvironment().setSourceClasspath(new String[] { path });

		spoon.addInputResource(path);

		spoon.buildModel();
		
		CtModel model = spoon.getModel();


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
	}
}