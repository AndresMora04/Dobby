package dobby.motor.parser;

import java.util.List;

/**
 * Nodo del AST que representa la definición de una función
 * ({@code Alohomora} en Dobby).
 */
public class NodoFuncion extends Nodo {

    private String nombre;
    private List<String> parametros;
    private List<Nodo> cuerpo;

    /**
     * @return el nombre de la función
     */
    public String getNombre() {
        // TODO: implementar
        return nombre;
    }

    /**
     * @param nombre nuevo nombre de la función
     */
    public void setNombre(String nombre) {
        // TODO: implementar
        this.nombre = nombre;
    }

    /**
     * @return la lista de nombres de los parámetros de la función
     */
    public List<String> getParametros() {
        // TODO: implementar
        return parametros;
    }

    /**
     * @param parametros nueva lista de parámetros de la función
     */
    public void setParametros(List<String> parametros) {
        // TODO: implementar
        this.parametros = parametros;
    }

    /**
     * @return las sentencias que forman el cuerpo de la función
     */
    public List<Nodo> getCuerpo() {
        // TODO: implementar
        return cuerpo;
    }

    /**
     * @param cuerpo nuevo cuerpo de la función
     */
    public void setCuerpo(List<Nodo> cuerpo) {
        // TODO: implementar
        this.cuerpo = cuerpo;
    }
}
