# Automatas Finitos - Conversor AFN a AFD

Este proyecto es una implementación en Java de un conversor de Autómatas Finitos No Deterministas (AFN) a Autómatas Finitos Deterministas (AFD). Además, permite cargar autómatas desde archivos en formato DOT, visualizarlos y realizar operaciones básicas como la concatenación.


## Características principales

🚀 Carga de AFN desde archivos DOT

🎨 Visualización de autómatas (genera imágenes PNG)

🔄 Operaciones con AFN: concatenación

⚙️ Conversión de AFN a AFD mediante algoritmo de construcción de subconjuntos

💾 Exportación del AFD resultante a formato DOT y PNG

## Requisitos del sistema

- Java JDK 8 o superior
- Graphviz (para generación de imágenes)

### Instalación

```bash
# Ubuntu
sudo apt-get install graphviz

# macOS
brew install graphviz

# Windows: Descargar desde https://graphviz.org/download/
```

## Explicacion de Clases

`Estado.java`

Representa estados de autómatas con funcionalidad dual:

AFN: Estados simples `(q0, q1)`

AFD: Estados compuestos `({q0,q1})`

```java
public class Estado {
    public int id;                 // Para AFN
    public Set<Integer> conjunto;  // Para AFD
    public String nombre;          // Representación visual
    
    // Constructor para AFN
    public Estado(int id) {
        this.id = id;
        this.nombre = "q" + id;
    }
    
    // Constructor para AFD
    public Estado(Set<Integer> conjunto) {
        this.conjunto = conjunto;
        this.nombre = "\"" + conjunto.stream().sorted()
                    .map(id -> "q" + id)
                    .collect(Collectors.joining(",")) + "\"";
    }
}
```
`Transicion.java`

*Modela transiciones entre estados:*
```java
public class Transicion {
    public Estado desde;
    public String simbolo;
    public Estado hacia;
}
```

`AFN.java`

*Implementa un Autómata Finito No Determinista con operaciones clave:*
```java
public class AFN {
    // Estructuras de datos
    public Estado estadoInicial;
    public Set<Estado> estadosFinales = new HashSet<>();
    public List<Transicion> transiciones = new ArrayList<>();
    public Set<Estado> estados = new HashSet<>();
    public Set<String> alfabeto = new HashSet<>();
    
    // Algoritmo principal
    public AFD convertirADeterminista() {
        // Implementación del algoritmo de construcción de subconjuntos
    }
    
    // Otras operaciones
    public void concatenarAFN(AFN otro) { ... }
    public Set<Estado> cierreLambda(Set<Estado> estados) { ... }
}
```
`AFD.java`

Extiende `AFN` para representar el resultado de la conversión:
```java
public class AFD extends AFN {
    // Hereda toda la estructura de AFN
    // con comportamiento determinista
}
```

`DotExporter.java`

*Genera representaciones DOT de autómatas:*
```java
public class DotExporter {
    public static void escribirAFN(AFN afn, String archivo, String nombre) { ... }
    public static void escribirAFD(AFD afd, String archivo, String nombre) { ... }
}
```

`GraphVizExporter.java`

*Genera imágenes PNG desde archivos DOT:*
```java
public class GraphvizExporter {
    public static void generarImagen(String dotPath, String pngPath) {
        // Ejecuta el comando Graphviz
        Runtime.getRuntime().exec("dot -Tpng " + dotPath + " -o " + pngPath);
    }
}
```

## Metodos claves

`ConcatenarAFN():` Combina dos AFNs con transiciones ε

`renombrarEstadosConOffset():` Renombra estados para evitar colisiones

`convertirADeterminista():` Implementa el algoritmo de conversión