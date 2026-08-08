package dobby.motor.parser;

/**
 * Nodo del AST que representa la asignación de un valor a una variable
 * ({@code Accio} en Dobby).
 */
public class NodoAsignacion extends Nodo {

    private String nombreVariable;
    private Nodo expresion;

    /**
     * @return el nombre de la variable a la que se asigna el valor
     */
    public String getNombreVariable() {
        // TODO: implementar
        return nombreVariable;
    }

    /**
     * @param nombreVariable nuevo nombre de la variable
     */
    public void setNombreVariable(String nombreVariable) {
        // TODO: implementar
        this.nombreVariable = nombreVariable;
    }

    /**
     * @return el nodo de expresión cuyo valor se asigna a la variable
     */
    public Nodo getExpresion() {
        // TODO: implementar
        return expresion;
    }

    /**
     * @param expresion nueva expresión a asignar
     */
    public void setExpresion(Nodo expresion) {
        // TODO: implementar
        this.expresion = expresion;
    }
}
