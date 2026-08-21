package dobby.motor.parser;

import java.util.List;

public class NodoPara extends Nodo {
    private Nodo inicializacion;
    private Nodo condicion;
    private Nodo incremento;
    private List<Nodo> cuerpo;

    public Nodo getInicializacion() {
        return inicializacion;
    }

    public void setInicializacion(Nodo inicializacion) {
        this.inicializacion = inicializacion;
    }

    public Nodo getCondicion() {
        return condicion;
    }

    public void setCondicion(Nodo condicion) {
        this.condicion = condicion;
    }

    public Nodo getIncremento() {
        return incremento;
    }

    public void setIncremento(Nodo incremento) {
        this.incremento = incremento;
    }

    public List<Nodo> getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(List<Nodo> cuerpo) {
        this.cuerpo = cuerpo;
    }
}
