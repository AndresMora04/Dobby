package dobby.motor.parser;

import java.util.List;

/**
 * Nodo del AST que representa un ciclo de repetición mientras se cumpla
 * una condición ({@code Wingardium} en Dobby, equivalente a while).
 */
public class NodoMientras extends Nodo {

    private Nodo condicion;
    private List<Nodo> cuerpo;

    /**
     * @return el nodo de expresión que representa la condición del ciclo
     */
    public Nodo getCondicion() {
        // TODO: implementar
        return condicion;
    }

    /**
     * @param condicion nueva condición del ciclo
     */
    public void setCondicion(Nodo condicion) {
        // TODO: implementar
        this.condicion = condicion;
    }

    /**
     * @return las sentencias que forman el cuerpo del ciclo
     */
    public List<Nodo> getCuerpo() {
        // TODO: implementar
        return cuerpo;
    }

    /**
     * @param cuerpo nuevo cuerpo del ciclo
     */
    public void setCuerpo(List<Nodo> cuerpo) {
        // TODO: implementar
        this.cuerpo = cuerpo;
    }
}
