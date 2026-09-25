package dobby.motor.parser;

public class NodoAsignacionArreglo extends Nodo {
    private NodoAccesoArreglo destino;
    private Nodo expresion;

    public NodoAccesoArreglo getDestino() {
        return destino;
    }

    public void setDestino(NodoAccesoArreglo destino) {
        this.destino = destino;
    }

    public Nodo getExpresion() {
        return expresion;
    }

    public void setExpresion(Nodo expresion) {
        this.expresion = expresion;
    }
}
