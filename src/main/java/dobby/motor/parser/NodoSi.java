package dobby.motor.parser;

import java.util.List;

public class NodoSi extends Nodo {
    private Nodo condicion;
    private List<Nodo> sentenciasSiVerdadero;
    private List<Nodo> sentenciasSiFalso;

    public Nodo getCondicion() {
        return condicion;
    }

    public void setCondicion(Nodo condicion) {
        this.condicion = condicion;
    }

    public List<Nodo> getSentenciasSiVerdadero() {
        return sentenciasSiVerdadero;
    }

    public void setSentenciasSiVerdadero(List<Nodo> sentenciasSiVerdadero) {
        this.sentenciasSiVerdadero = sentenciasSiVerdadero;
    }

    public List<Nodo> getSentenciasSiFalso() {
        return sentenciasSiFalso;
    }

    public void setSentenciasSiFalso(List<Nodo> sentenciasSiFalso) {
        this.sentenciasSiFalso = sentenciasSiFalso;
    }
}
