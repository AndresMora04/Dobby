package dobby.motor.parser;

import java.util.List;

/**
 * Nodo del AST que representa una sentencia condicional
 * ({@code Protego}/{@code Finite} en Dobby, equivalente a if/else).
 */
public class NodoSi extends Nodo {

    private Nodo condicion;
    private List<Nodo> sentenciasSiVerdadero;
    private List<Nodo> sentenciasSiFalso;

    /**
     * @return el nodo de expresión que representa la condición evaluada
     */
    public Nodo getCondicion() {
        // TODO: implementar
        return condicion;
    }

    /**
     * @param condicion nueva condición de la sentencia
     */
    public void setCondicion(Nodo condicion) {
        // TODO: implementar
        this.condicion = condicion;
    }

    /**
     * @return las sentencias a ejecutar cuando la condición es verdadera
     */
    public List<Nodo> getSentenciasSiVerdadero() {
        // TODO: implementar
        return sentenciasSiVerdadero;
    }

    /**
     * @param sentenciasSiVerdadero nuevas sentencias para el caso verdadero
     */
    public void setSentenciasSiVerdadero(List<Nodo> sentenciasSiVerdadero) {
        // TODO: implementar
        this.sentenciasSiVerdadero = sentenciasSiVerdadero;
    }

    /**
     * @return las sentencias a ejecutar cuando la condición es falsa (puede ser {@code null} si no hay bloque alternativo)
     */
    public List<Nodo> getSentenciasSiFalso() {
        // TODO: implementar
        return sentenciasSiFalso;
    }

    /**
     * @param sentenciasSiFalso nuevas sentencias para el caso falso
     */
    public void setSentenciasSiFalso(List<Nodo> sentenciasSiFalso) {
        // TODO: implementar
        this.sentenciasSiFalso = sentenciasSiFalso;
    }
}
