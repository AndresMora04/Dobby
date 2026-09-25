package dobby.motor.parser;

import java.util.Map;

public class NodoCreacionEstructura extends Nodo {
    private String nombre;
    private Map<String, Nodo> campos;
    private NodoEstructura definicion;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Map<String, Nodo> getCampos() {
        return campos;
    }

    public void setCampos(Map<String, Nodo> campos) {
        this.campos = campos;
    }

    public NodoEstructura getDefinicion() {
        return definicion;
    }

    public void setDefinicion(NodoEstructura definicion) {
        this.definicion = definicion;
    }
}
