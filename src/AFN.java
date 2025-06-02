import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AFN {
    public Estado estadoInicial;
    public Set<Estado> estadosFinales = new HashSet<>();
    public List<Transicion> transiciones = new ArrayList<>();
    public Set<Estado> estados = new HashSet<>();
    public Set<String> alfabeto = new HashSet<>();

    public void agregarEstado(Estado estado) {
        estados.add(estado);
    }

    public void agregarTransicion(Estado desde, String simbolo, Estado hacia) {
        transiciones.add(new Transicion(desde, simbolo, hacia));
        estados.add(desde);
        estados.add(hacia);
        alfabeto.add(simbolo);
    }

    public void agregarEstadoFinal(Estado estado) {
        estadosFinales.add(estado);
    }

    public void setEstadoInicial(Estado estado) {
        estadoInicial = estado;
        estados.add(estado);
    }
}
