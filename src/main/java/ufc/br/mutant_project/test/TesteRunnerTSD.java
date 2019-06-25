package ufc.br.mutant_project.test;

import ufc.br.mutant_project.exceptions.ConfigPropertiesNotFoundException;
import ufc.br.mutant_project.exceptions.InicializerException;
import ufc.br.mutant_project.exceptions.ListProjectsNotFoundException;
import ufc.br.mutant_project.exceptions.NotURLsException;
import ufc.br.mutant_project.executers.Execute;
import ufc.br.mutant_project.executers.ExecuteOnlyMutant;

public class TesteRunnerTSD {

    public static void main(String[] args) {
//        try {

            //try {
            //    System.setOut(new PrintStream(new FileOutputStream("Saida-Teste-TSD-Output.txt")));
            //} catch (FileNotFoundException e) {
            //    e.printStackTrace();
            //}

            /*AbstractRunner<CtThrow> abs = new RunnerThrow<>("commons-io-2.6", null, true);
            System.out.println("--Iniciando Mutações TSD para o projeto");
            abs.processor(new ProcessorTSD());
            System.out.println("---OK!");

            System.out.close();*/



  //      } catch (PomException e1) {
    //        System.out.println(e1.getMessage());
     //       e1.printStackTrace();
      //  }

        Execute ex = new ExecuteOnlyMutant(true, true, false, false,false);

        try {
            ex.execute();
        } catch (InicializerException e) {
            e.printStackTrace();
        } catch (ListProjectsNotFoundException e) {
            e.printStackTrace();
        } catch (NotURLsException e) {
            e.printStackTrace();
        } catch (ConfigPropertiesNotFoundException e) {
            e.printStackTrace();
        }
    }
}
