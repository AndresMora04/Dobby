package dobby.motor.parser;

/**
 * Clase base abstracta para todos los nodos del árbol de sintaxis abstracta
 * (AST) que produce el {@link Parser} de Dobby.
 * <p>
 * Cada construcción del lenguaje (sentencia if/Protego, ciclo/Mientras,
 * función/Alohomora, asignación, llamada, etc.) se representa mediante una
 * subclase concreta de {@code Nodo}.
 */
public abstract class Nodo {

    /**
     * Número de línea del código fuente donde inicia este nodo, útil para
     * reportar errores en etapas posteriores (interpretación).
     */
    private int linea;

    /**
     * @return la línea del código fuente asociada a este nodo
     */
    public int getLinea() {
        // TODO: implementar
        return linea;
    }

    /**
     * @param linea nueva línea asociada a este nodo
     */
    public void setLinea(int linea) {
        // TODO: implementar
        this.linea = linea;
    }
}
