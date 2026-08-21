package dobby.motor.parser;

public class NodoRetorno extends Nodo {
    private Nodo expresion;

    public Nodo getExpresion() {
        return expresion;
    }

    public void setExpresion(Nodo expresion) {
        this.expresion = expresion;
    }
}
