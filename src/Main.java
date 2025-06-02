import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        AFN afn = DotParser.leerDesdeArchivo("C:\\Users\\Gerfs\\OneDrive\\Escritorio\\Proyectos Teoria de la computacion\\Automatas-Finitos\\a_b_c.dot");
        //DotExporter.escribirAFN(afn, "C:\\Users\\Gerfs\\OneDrive\\Escritorio\\Proyectos Teoria de la computacion\\Automatas-Finitos\\salida.dot", "salida_a_b_c");
        //GraphvizExporter.generarImagen("salida.dot", "salida.png");

        System.out.println(afn.estadosFinales);
        System.out.println(afn.estadoInicial);
        System.out.println(afn.transiciones);
        System.out.println(afn.estados);

    }
}
