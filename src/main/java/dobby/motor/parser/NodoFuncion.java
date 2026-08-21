package dobby.motor.parser;

import java.util.List;

public class NodoFuncion extends Nodo {
    private String nombre;
    private List<String> parametros;
    private List<Nodo> cuerpo;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<String> getParametros() {
        return parametros;
    }

    public void setParametros(List<String> parametros) {
        this.parametros = parametros;
    }

    public List<Nodo> getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(List<Nodo> cuerpo) {
        this.cuerpo = cuerpo;
    }
}
