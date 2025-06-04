import java.io.*;
import java.util.regex.*;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class DotParser {
    public static AFN leerDesdeArchivo(String ruta) throws IOException {
        AFN afn = new AFN();
        Map<String, Estado> mapaEstados = new HashMap<>();

        BufferedReader br = new BufferedReader(new FileReader(ruta));
        String linea;
        Set<String> estadosFinalesTemp = new HashSet<>();

        while ((linea = br.readLine()) != null) {
            linea = linea.trim();

            // Saltar líneas decorativas
            if (linea.startsWith("digraph") ||
                    linea.startsWith("edge [") ||
                    linea.equals("rankdir=LR;") ||
                    linea.equals("node [shape = circle];") ||
                    linea.equals("{") || linea.equals("}")) {
                continue;
            }

            System.out.println(linea);

            // Estado final: node [shape = doublecircle]; q3;
            if (linea.startsWith("node [shape = doublecircle];")) {
                System.out.println("Entre");
                String[] partes = linea.split(";");
                for (int i = 1; i < partes.length; i++) {
                    String nombre = partes[i].trim();
                    estadosFinalesTemp.add(nombre);
                    System.out.println("estado final: " + estadosFinalesTemp);
                }
                continue;
            }

            // Estado inicial: inic -> q0;
            if (linea.startsWith("inic -> ")) {
                String destino = linea.split("->")[1].replace(";", "").trim();
                Estado inicial = mapaEstados.computeIfAbsent(destino, k -> new Estado(Integer.parseInt(k.substring(1))));
                afn.setEstadoInicial(inicial);
                continue;
            }

            // Transición: q0 -> q1 [label = "a|b|c"];
            Pattern p = Pattern.compile("q(\\d+) -> q(\\d+) \\[label = \"(.*)\"\\];");
            Matcher m = p.matcher(linea);
            if (m.find()) {
                Estado desde = mapaEstados.computeIfAbsent("q" + m.group(1), k -> new Estado(Integer.parseInt(m.group(1))));
                Estado hacia = mapaEstados.computeIfAbsent("q" + m.group(2), k -> new Estado(Integer.parseInt(m.group(2))));
                String simbolos = m.group(3);

                for (String simbolo : simbolos.split("\\|")) {
                    afn.agregarTransicion(desde, simbolo, hacia);
                }
            }
        }

        // Marcar estados finales
        for (String nombre : estadosFinalesTemp) {
            Estado est = mapaEstados.get(nombre);
            if (est != null) {
                afn.agregarEstadoFinal(est);
            }
        }

        br.close();
        return afn;
    }
}
