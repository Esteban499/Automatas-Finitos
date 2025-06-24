import java.util.*;
import java.util.stream.Collectors;

public class Estado {
    public int id;  // Para estados de AFN
    public Set<Integer> conjunto;  // Para estados de AFD (conjunto de ids de estados AFN)
    public String nombre;

    // Constructor para estados de AFN
    public Estado(int id) {
        this.id = id;
        this.nombre = "q" + id;
    }

    // Constructor para estados de AFD (representa un conjunto)
    public Estado(Set<Integer> conjunto) {
        this.conjunto = new HashSet<>(conjunto);
        // Generar nombre entre comillas dobles
        this.nombre = "\"" + conjunto.stream()
                .sorted()
                .map(id -> "q" + id)
                .collect(Collectors.joining(",")) + "\"";
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Estado estado = (Estado) o;
        // Comparación para estados AFN
        if (conjunto == null && estado.conjunto == null) {
            return id == estado.id;
        }
        // Comparación para estados AFD
        if (conjunto != null && estado.conjunto != null) {
            return conjunto.equals(estado.conjunto);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (conjunto != null) {
            return conjunto.hashCode();
        } else {
            return Objects.hash(id);
        }
    }
}