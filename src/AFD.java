import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AFD extends AFN{
    public Estado estadoInicial;
    public Set<Estado> estadosFinales = new HashSet<>();
    public List<Transicion> transiciones = new ArrayList<>();
    public Set<Estado> estados = new HashSet<>();
    public Set<String> alfabeto = new HashSet<>();
}
