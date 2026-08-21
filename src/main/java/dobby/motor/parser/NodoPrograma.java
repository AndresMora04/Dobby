package dobby.motor.parser;

import java.util.List;

public class NodoPrograma extends Nodo {
    private List<Nodo> sentencias;

    public List<Nodo> getSentencias() {
        return sentencias;
    }

    public void setSentencias(List<Nodo> sentencias) {
        this.sentencias = sentencias;
    }
}
