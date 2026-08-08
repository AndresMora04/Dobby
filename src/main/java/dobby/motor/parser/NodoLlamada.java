package dobby.motor.parser;

import java.util.List;

/**
 * Nodo del AST que representa la llamada a una función definida por el
 * usuario o a una función nativa de Dobby (por ejemplo, {@code Revelio}
 * para imprimir en pantalla).
 */
public class NodoLlamada extends Nodo {

    private String nombreFuncion;
    private List<Nodo> argumentos;

    /**
     * @return el nombre de la función que se está invocando
     */
    public String getNombreFuncion() {
        // TODO: implementar
        return nombreFuncion;
    }

    /**
     * @param nombreFuncion nuevo nombre de la función invocada
     */
    public void setNombreFuncion(String nombreFuncion) {
        // TODO: implementar
        this.nombreFuncion = nombreFuncion;
    }

    /**
     * @return la lista de expresiones que se pasan como argumentos
     */
    public List<Nodo> getArgumentos() {
        // TODO: implementar
        return argumentos;
    }

    /**
     * @param argumentos nueva lista de argumentos de la llamada
     */
    public void setArgumentos(List<Nodo> argumentos) {
        // TODO: implementar
        this.argumentos = argumentos;
    }
}
