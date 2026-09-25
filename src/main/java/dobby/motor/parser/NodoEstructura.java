package dobby.motor.parser;

import java.util.Map;

public class NodoEstructura extends Nodo {
    private String nombre;
    private Map<String, String> campos;
    private Map<String, Integer> lineasCampos = Map.of();

    public Map<String, Integer> getLineasCampos() {
        return lineasCampos;
    }

    public void setLineasCampos(Map<String, Integer> lineasCampos) {
        this.lineasCampos = lineasCampos;
    }

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
