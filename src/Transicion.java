public class Transicion {
    public Estado desde;
    public String simbolo;
    public Estado hacia;

    public Transicion(Estado desde, String simbolo, Estado hacia) {
        this.desde = desde;
        this.simbolo = simbolo;
        this.hacia = hacia;
    }
    @Override
    public String toString() {
        return desde.id + " -> " + hacia.id + " [" + simbolo + "]";
    }
}
