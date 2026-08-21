package dobby.motor.parser;

public class NodoOperacionUnaria extends Nodo {
    private String operador;
    private Nodo operando;

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public Nodo getOperando() {
        return operando;
    }

    public void setOperando(Nodo operando) {
        this.operando = operando;
    }
}
