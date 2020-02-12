package ufc.br.mutant_project.test;

import ufc.br.mutant_project.exceptions.PomException;
import ufc.br.mutant_project.processors.ProcessorTHD;
import ufc.br.mutant_project.runners.AbstractRunner;
import ufc.br.mutant_project.runners.RunnerSubProcessThrows;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class TesteRunnerTHD {

    @SuppressWarnings("rawtypes")
	public static void main(String[] args) {
        try {

            try {
                System.setOut(new PrintStream(new FileOutputStream("Saida-Teste-THD-Output.txt")));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            AbstractRunner<?> abs = new RunnerSubProcessThrows("commons-io-2.6", null, true);
            System.out.println("--Iniciando Mutações CBD para o projeto");
            abs.processor(new ProcessorTHD());
            System.out.println("---OK!");

            System.out.close();

        } catch (PomException e1) {
            System.out.println(e1.getMessage());
            e1.printStackTrace();
        }
    }
}
