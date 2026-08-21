package dobby.motor.parser;

import java.util.Map;

public class NodoEstructura extends Nodo {
    private String nombre;
    private Map<String, String> campos;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Map<String, String> getCampos() {
        return campos;
    }

    public void setCampos(Map<String, String> campos) {
        this.campos = campos;
    }
}
