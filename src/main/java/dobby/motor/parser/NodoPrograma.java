package dobby.motor.parser;

import java.util.List;

/**
 * Nodo raíz del AST: representa un programa Dobby completo como una
 * secuencia de sentencias.
 */
public class NodoPrograma extends Nodo {

    private List<Nodo> sentencias;

    /**
     * @return la lista de sentencias (nodos hijos) que componen el programa
     */
    public List<Nodo> getSentencias() {
        // TODO: implementar
        return sentencias;
    }

    /**
     * @param sentencias nueva lista de sentencias del programa
     */
    public void setSentencias(List<Nodo> sentencias) {
        // TODO: implementar
        this.sentencias = sentencias;
    }
}
