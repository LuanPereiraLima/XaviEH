package ufc.br.xavieh.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Test2 {
    public static void main(String[] args) {
        List<String> valores = new ArrayList<>();
        valores.add("Luan");
        valores.add("Luan2");
        valores.add("Luana");
        valores.add("Luanv");

        Collections.shuffle(valores);

        System.out.println(valores);
    }
}
