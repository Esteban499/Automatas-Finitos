import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    static Scanner scanner = new Scanner(System.in);
    static Map<String, AFN> automatas = new LinkedHashMap<>();
    static int contadorAFN = 1;

    public static void main(String[] args) throws IOException, InterruptedException {
        int opcion;
        do {
            limpiarPantalla();
            System.out.println("\n--- Menú de Autómatas Finitos No Deterministas (AFN) ---");
            System.out.println("1. Cargar AFN desde archivo DOT");
            System.out.println("2. Mostrar AFNs cargados (genera DOT + PNG)");
            System.out.println("3. Operaciones con AFN");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1:
                    limpiarPantalla();
                    cargarAFN();
                    pausar();
                    break;
                case 2:
                    limpiarPantalla();
                    mostrarAFNs();
                    pausar();
                    break;
                case 3:
                    limpiarPantalla();
                    submenuOperaciones();
                    pausar();
                    break;
                case 0:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción inválida. Intente de nuevo.");
                    break;
            }
        } while (opcion != 0);
    }

    private static void cargarAFN() {
        System.out.print("Ingrese el nombre del archivo DOT (en carpeta assets/, por ejemplo: a_b_c.dot): ");
        String archivo = scanner.nextLine().trim();
        String ruta = "dotIniciales/" + archivo;
        try {
            AFN afn = DotParser.leerDesdeArchivo(ruta);
            String nombreAFN = "af" + contadorAFN++;
            automatas.put(nombreAFN, afn);
            System.out.println("✔ AFN cargado como: " + nombreAFN + " = " + archivo);
        } catch (IOException e) {
            System.err.println("❌ Error al leer el archivo: " + e.getMessage());
        }
    }

    private static void mostrarAFNs() throws IOException, InterruptedException {
        if (automatas.isEmpty()) {
            System.out.println("No hay AFNs cargados.");
            return;
        }

        for (Map.Entry<String, AFN> entrada : automatas.entrySet()) {
            String nombre = entrada.getKey();
            AFN afn = entrada.getValue();
            String dotPath = "archivos_dot/" + nombre + ".dot";
            String pngPath = "automatas_png/" + nombre + ".png";

            DotExporter.escribirAFN(afn, dotPath, nombre);
            GraphvizExporter.generarImagen(dotPath, pngPath);

            System.out.println("✅ Visualización generada para " + nombre + ":");
            System.out.println("   DOT: " + dotPath);
            System.out.println("   PNG: " + pngPath);
        }
    }

    private static int leerEntero() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                System.out.print("Entrada inválida. Ingrese un número: ");
            }
        }
    }

    private static void submenuOperaciones() throws IOException, InterruptedException {
        limpiarPantalla();
        int opcion;
        System.out.println("=== OPERACIONES CON AFN ===");
        System.out.println("1. Unión (pendiente)");
        System.out.println("2. Concatenación");
        System.out.println("3. Clausura (pendiente)");
        System.out.println("0. Volver al menú principal");
        System.out.print("Seleccione una opción: ");
        opcion = leerEntero();

        switch (opcion) {
            case 1:
                System.out.println("Funcionalidad de Unión: Pendiente de implementación.");
                break;
            case 2:
                concatenarAFNs();
                break;
            case 3:
                System.out.println("Funcionalidad de Clausura: Pendiente de implementación.");
                break;
            case 0:
                break;
            default:
                System.out.println("Opción inválida.");
        }
    }




    private static void pausar() {
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private static void concatenarAFNs() throws IOException, InterruptedException {
        if (automatas.size() < 2) {
            System.out.println("Se requieren al menos dos AFNs cargados.");
            pausar();
            return;
        }

        System.out.println("AFNs disponibles:");
        for (String nombre : automatas.keySet()) {
            System.out.println("- " + nombre);
        }

        System.out.print("Nombre del primer AFN: ");
        String nombre1 = scanner.nextLine();
        System.out.print("Nombre del segundo AFN: ");
        String nombre2 = scanner.nextLine();

        AFN afn1 = automatas.get(nombre1);
        AFN afn2 = automatas.get(nombre2);

        if (afn1 == null || afn2 == null) {
            System.out.println("Uno de los AFNs no existe.");
            pausar();
            return;
        }

        // Crear una copia profunda del segundo AFN para evitar modificar el original
        AFN copiaAFN2 = copiarAFN(afn2); // Necesitas tener o implementar este método

        // Concatenar directamente usando el método de la clase AFN
        afn1.concatenarAFN(copiaAFN2);

        // Guardar el resultado con nuevo nombre
        String nombreResultado = nombre1 + "_concat_" + nombre2;
        automatas.put(nombreResultado, afn1); // ahora afn1 contiene el autómata resultante

        String salidaDot = "archivos_dot/" + nombreResultado + ".dot";
        String salidaPng = "automatas_png/" + nombreResultado + ".png";

        DotExporter.escribirAFN(afn1, salidaDot, nombreResultado);
        GraphvizExporter.generarImagen(salidaDot, salidaPng);

        System.out.println("AFN concatenado generado como: " + nombreResultado);
        pausar();
    }

    private static AFN copiarAFN(AFN original) {
        AFN copia = new AFN();
        Map<Estado, Estado> mapeo = new HashMap<>();

        // Copiar estados
        for (Estado est : original.estados) {
            Estado nuevo = new Estado(est.id);
            mapeo.put(est, nuevo);
            if (est.equals(original.estadoInicial)) {
                copia.setEstadoInicial(nuevo);
            }
            if (original.estadosFinales.contains(est)) {
                copia.agregarEstadoFinal(nuevo);
            }
        }

        // Copiar transiciones
        for (Transicion t : original.transiciones) {
            Estado desde = mapeo.get(t.desde);
            Estado hacia = mapeo.get(t.hacia);
            copia.agregarTransicion(desde, t.simbolo, hacia);
        }

        // Copiar alfabeto
        copia.alfabeto.addAll(original.alfabeto);

        return copia;
    }



    private static void limpiarPantalla() {
        for (int i = 0; i < 50; i++) System.out.println();
    }
}
