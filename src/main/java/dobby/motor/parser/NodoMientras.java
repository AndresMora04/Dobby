package dobby.motor.parser;

import java.util.List;

public class NodoMientras extends Nodo {
    private Nodo condicion;
    private List<Nodo> cuerpo;

    public Nodo getCondicion() {
        return condicion;
    }

    public void setCondicion(Nodo condicion) {
        this.condicion = condicion;
    }

    public List<Nodo> getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(List<Nodo> cuerpo) {
        this.cuerpo = cuerpo;
    }
}
