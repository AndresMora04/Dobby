package dobby.motor.parser;

public class NodoAsignacionCampo extends Nodo {
    private NodoAccesoCampo destino;
    private Nodo expresion;

    public NodoAccesoCampo getDestino() {
        return destino;
    }

    public void setDestino(NodoAccesoCampo destino) {
        this.destino = destino;
    }

    public Nodo getExpresion() {
        return expresion;
    }

    public void setExpresion(Nodo expresion) {
        this.expresion = expresion;
    }
}
