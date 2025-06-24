import java.util.*;
import java.util.stream.Collectors;

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
        Map<Estado, Estado> nuevoEstadoMap = new HashMap<>();
        Set<Estado> nuevosEstados = new HashSet<>();

        for (Estado est : estados) {
            // Crear nuevo estado con nuevo ID y nuevo nombre
            Estado nuevo = new Estado(est.id + offset);
            nuevo.nombre = "q" + (est.id + offset);  // Actualizar el nombre
            nuevoEstadoMap.put(est, nuevo);
            nuevosEstados.add(nuevo);
        }
        estados = nuevosEstados;

        estadoInicial = nuevoEstadoMap.get(estadoInicial);

        Set<Estado> nuevosFinales = new HashSet<>();
        for (Estado ef : estadosFinales) {
            nuevosFinales.add(nuevoEstadoMap.get(ef));
        }
        estadosFinales = nuevosFinales;

        List<Transicion> nuevasTransiciones = new ArrayList<>();
        for (Transicion t : transiciones) {
            Estado nuevoDesde = nuevoEstadoMap.get(t.desde);
            Estado nuevoHacia = nuevoEstadoMap.get(t.hacia);
            nuevasTransiciones.add(new Transicion(nuevoDesde, t.simbolo, nuevoHacia));
        }
        transiciones = nuevasTransiciones;
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

    //Agrega todos los estados y busca los estados a los que llegan las transiciones desde lambda
    public Set<Estado> cierreLambda(Set<Estado> estados) {
        Set<Estado> cierre = new HashSet<>(estados);
        Stack<Estado> pila = new Stack<>();
        pila.addAll(estados);

        while (!pila.isEmpty()) {
            Estado e = pila.pop();
            for (Transicion t : transiciones) {
                if (t.desde.equals(e) && t.simbolo.equals("_") && !cierre.contains(t.hacia)) {
                    cierre.add(t.hacia);
                    pila.push(t.hacia);
                }
            }
        }

        return cierre;
    }

    public Set<Estado> mover(Set<Estado> estados, String simbolo) {
        Set<Estado> resultado = new HashSet<>();
        for (Estado e : estados) {
            for (Transicion t : transiciones) {
                if (t.desde.equals(e) && t.simbolo.equals(simbolo)) {
                    resultado.add(t.hacia);
                }
            }
        }
        return resultado;
    }

    public AFD convertirADeterminista() {

        AFD afd = new AFD();
        Map<String, Estado> subconjuntoToEstado = new HashMap<>();
        Queue<Set<Integer>> pendientes = new LinkedList<>();

        Set<Estado> cierreInicial = cierreLambda(Set.of(estadoInicial));
        Set<Integer> conjuntoInicial = cierreInicial.stream()
                .map(e -> e.id)
                .collect(Collectors.toSet());

        String claveInicial = clave(conjuntoInicial);
        Estado estadoInicialAFD = new Estado(conjuntoInicial);
        afd.estadoInicial = estadoInicialAFD;
        afd.estados.add(estadoInicialAFD);
        subconjuntoToEstado.put(claveInicial, estadoInicialAFD);
        pendientes.add(conjuntoInicial);

        // Paso 2: Proceso de subconjuntos
        while (!pendientes.isEmpty()) {
            Set<Integer> actual = pendientes.poll(); //¿Que hace el metodo poll?
            String claveActual = clave(actual);
            Estado estadoDesde = subconjuntoToEstado.get(claveActual);

            for (String simbolo : alfabeto) {
                if (simbolo.equals("_")) continue;

                // Mover y cerrar
                Set<Estado> estadosActuales = obtenerEstadosPorIDs(actual);
                Set<Estado> alcanzados = mover(estadosActuales, simbolo);
                Set<Estado> cierreAlcanzados = cierreLambda(alcanzados);

                Set<Integer> conjuntoAlcanzado = cierreAlcanzados.stream()
                        .map(e -> e.id)
                        .collect(Collectors.toSet());

                if (conjuntoAlcanzado.isEmpty()) continue;

                String claveAlcanzado = clave(conjuntoAlcanzado);
                Estado estadoHacia = subconjuntoToEstado.get(claveAlcanzado);

                if (estadoHacia == null) {
                    estadoHacia = new Estado(conjuntoAlcanzado);
                    subconjuntoToEstado.put(claveAlcanzado, estadoHacia);
                    afd.estados.add(estadoHacia);
                    pendientes.add(conjuntoAlcanzado);
                }

                afd.transiciones.add(new Transicion(estadoDesde, simbolo, estadoHacia));
            }
        }

        // Paso 3: Determinar estados finales
        Set<Integer> idsFinales = estadosFinales.stream()
                .map(e -> e.id)
                .collect(Collectors.toSet());

        for (Estado estadoAFD : afd.estados) {
            for (Integer id : estadoAFD.conjunto) {
                if (idsFinales.contains(id)) {
                    afd.estadosFinales.add(estadoAFD);
                    break;
                }
            }
        }

        // Paso 4: Alfabeto sin λ
        afd.alfabeto.addAll(alfabeto);
        afd.alfabeto.remove("_");

        return afd;
    }

    // Métodos auxiliares
    private String clave(Set<Integer> conjunto) {
        return conjunto.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    private Set<Estado> obtenerEstadosPorIDs(Set<Integer> ids) {
        return estados.stream()
                .filter(e -> ids.contains(e.id))
                .collect(Collectors.toSet());
    }
}