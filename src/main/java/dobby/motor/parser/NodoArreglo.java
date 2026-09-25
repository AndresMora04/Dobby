package dobby.motor.parser;

import java.util.List;

public class NodoArreglo extends Nodo {
    private List<Nodo> elementos;
    private String tipoElemento;

    public List<Nodo> getElementos() {
        return elementos;
    }

    public void setElementos(List<Nodo> elementos) {
        this.elementos = elementos;
    }

    public String getTipoElemento() {
        return tipoElemento;
    }

    public void setTipoElemento(String tipoElemento) {
        this.tipoElemento = tipoElemento;
    }
}
