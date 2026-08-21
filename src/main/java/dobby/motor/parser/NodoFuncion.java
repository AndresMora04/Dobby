package dobby.motor.parser;

import java.util.List;
import java.util.Map;

public class NodoFuncion extends Nodo {
    private String nombre;
    private Map<String, String> parametros;
    private String tipoRetorno;
    private List<Nodo> cuerpo;
    private boolean esPrincipal;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Map<String, String> getParametros() {
        return parametros;
    }

    public void setParametros(Map<String, String> parametros) {
        this.parametros = parametros;
    }

    public String getTipoRetorno() {
        return tipoRetorno;
    }

    public void setTipoRetorno(String tipoRetorno) {
        this.tipoRetorno = tipoRetorno;
    }

    public List<Nodo> getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(List<Nodo> cuerpo) {
        this.cuerpo = cuerpo;
    }

    public boolean isEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }
}
