import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

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

    public void concatenarAFN(AFN otro) {
        // Paso 1: Renombrar los estados del segundo AFN con un offset
        int offset = this.obtenerMaxIdEstado() + 1;
        otro.renombrarEstadosConOffset(offset);

        // Paso 2: Agregar transiciones lambda desde los estados finales de este AFN al estado inicial del segundo
        for (Estado final1 : this.estadosFinales) {
            this.agregarTransicion(final1, "_", otro.estadoInicial);
        }

        // Paso 3: Unir todos los componentes del segundo AFN a este
        this.estados.addAll(otro.estados);
        this.transiciones.addAll(otro.transiciones);
        this.alfabeto.addAll(otro.alfabeto);

        // Paso 4: Actualizar los estados finales (solo los del segundo AFN quedan como finales)
        this.estadosFinales = new HashSet<>(otro.estadosFinales);
    }

    public void renombrarEstadosConOffset(int offset) {
        // Mapeo de estados antiguos a nuevos
        Map<Estado, Estado> nuevoEstadoMap = new HashMap<>();

        // Crear nuevos estados renombrados
        for (Estado est : estados) {
            Estado nuevo = new Estado(est.id + offset);
            nuevoEstadoMap.put(est, nuevo);
        }

        // Reemplazar estado inicial
        estadoInicial = nuevoEstadoMap.get(estadoInicial);

        // Reemplazar estados finales
        Set<Estado> nuevosFinales = new HashSet<>();
        for (Estado ef : estadosFinales) {
            nuevosFinales.add(nuevoEstadoMap.get(ef));
        }
        estadosFinales = nuevosFinales;

        // Reemplazar transiciones
        List<Transicion> nuevasTransiciones = new ArrayList<>();
        for (Transicion t : transiciones) {
            Estado nuevoDesde = nuevoEstadoMap.get(t.desde);
            Estado nuevoHacia = nuevoEstadoMap.get(t.hacia);
            nuevasTransiciones.add(new Transicion(nuevoDesde, t.simbolo, nuevoHacia));
        }
        transiciones = nuevasTransiciones;

        // Reemplazar conjunto de estados
        estados = new HashSet<>(nuevoEstadoMap.values());
    }

    public int obtenerMaxIdEstado() {
        int max = -1;
        for (Estado e : estados) {
            if (e.id > max) {
                max = e.id;
            }
        }
        return max;
    }

}
