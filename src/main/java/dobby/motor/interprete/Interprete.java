package dobby.motor.interprete;

import dobby.motor.parser.NodoPrograma;

/**
 * Intérprete de Dobby.
 * <p>
 * Recorre (evalúa) el AST producido por el {@code Parser} y ejecuta el
 * programa, produciendo la salida y capturando los errores en tiempo de
 * ejecución.
 */
public class Interprete {

    /**
     * Ejecuta el programa representado por el AST recibido.
     *
     * @param programa nodo raíz del AST a ejecutar
     * @return el resultado de la ejecución (salida, errores y éxito/fallo)
     */
    public ResultadoEjecucion ejecutar(NodoPrograma programa) {
        // TODO: implementar interpretación del AST
        return null;
    }
}
