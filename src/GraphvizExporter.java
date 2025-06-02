import java.io.IOException;

public class GraphvizExporter {

    public static void generarImagen(String dotPath, String pngPath) throws IOException, InterruptedException {
        // Asumimos que "dot" está en el PATH
        ProcessBuilder pb = new ProcessBuilder("C:\\Program Files\\Graphviz\\bin\\dot.exe", "-Tpng", dotPath, "-o", pngPath);
        pb.inheritIO(); // Mostrar errores en consola
        Process proceso = pb.start();
        int exitCode = proceso.waitFor();

        if (exitCode == 0) {
            System.out.println("Imagen PNG generada exitosamente: " + pngPath);
        } else {
            System.err.println("Error al generar la imagen PNG. Código de salida: " + exitCode);
        }
    }
}
