package dobby.motor.parser;

public class NodoAccesoArreglo extends Nodo {
    private Nodo arreglo;
    private Nodo indice;

    public Nodo getArreglo() {
        return arreglo;
    }

    public void setArreglo(Nodo arreglo) {
        this.arreglo = arreglo;
    }

    public Nodo getIndice() {
        return indice;
    }

    public void setIndice(Nodo indice) {
        this.indice = indice;
    }
}
