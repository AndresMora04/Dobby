package dobby.motor.parser;

public class NodoAsignacion extends Nodo {
    private String nombreVariable;
    private Nodo expresion;

    public String getNombreVariable() {
        return nombreVariable;
    }

    public void setNombreVariable(String nombreVariable) {
        this.nombreVariable = nombreVariable;
    }

    public Nodo getExpresion() {
        return expresion;
    }

    public void setExpresion(Nodo expresion) {
        this.expresion = expresion;
    }
}
