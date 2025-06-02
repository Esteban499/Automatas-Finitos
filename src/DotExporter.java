import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class DotExporter {
    public static void escribirAFN(AFN afn, String nombreArchivo, String nombreGrafo) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo));
        pw.printf("digraph %s {%n", nombreGrafo);
        pw.println("\tnode [fontname=\"Helvetica,Arial,sans-serif\"]");
        pw.println("\tedge [fontname=\"Helvetica,Arial,sans-serif\"]");
        pw.println("\trankdir=LR;");

        // Estados finales
        if (!afn.estadosFinales.isEmpty()) {
            pw.print("\tnode [shape = doublecircle]; ");
            for (Estado ef : afn.estadosFinales) {
                pw.print(ef + "; ");
            }
            pw.println();
        }

        pw.println("\tnode [shape = circle];");
        pw.println("\tinic[shape=point];");
        pw.printf("\tinic -> %s;%n", afn.estadoInicial);

        // Agrupar transiciones con mismo origen y destino
        Map<String, List<String>> agrupadas = new HashMap<>();
        for (Transicion t : afn.transiciones) {
            String clave = t.desde + "->" + t.hacia;
            agrupadas.computeIfAbsent(clave, k -> new ArrayList<>()).add(t.simbolo);
        }

        for (Map.Entry<String, List<String>> entrada : agrupadas.entrySet()) {
            String[] partes = entrada.getKey().split("->");
            String desde = partes[0];
            String hacia = partes[1];
            String etiqueta = String.join("|", entrada.getValue());
            pw.printf("\t%s -> %s [label = \"%s\"];%n", desde, hacia, etiqueta);
        }

        pw.println("}");
        pw.close();
    }
}

