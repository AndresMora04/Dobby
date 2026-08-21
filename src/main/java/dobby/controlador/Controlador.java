package dobby.controlador;

import dobby.flow.FlowController;
import dobby.motor.interprete.Interprete;
import dobby.motor.interprete.ResultadoEjecucion;
import dobby.motor.lexer.Lexer;
import dobby.motor.lexer.Token;
import dobby.motor.parser.NodoPrograma;
import dobby.motor.parser.Parser;
import dobby.vista.MainView;

import java.util.List;

public class Controlador {
    private final MainView vista;
    private final FlowController flowController;
    private final Lexer lexer;
    private final Parser parser;
    private final Interprete interprete;

    public Controlador(MainView vista, FlowController flowController) {
        this.vista = vista;
        this.flowController = flowController;
        this.lexer = new Lexer();
        this.parser = new Parser();
        this.interprete = new Interprete();
    }

    private void inicializarListeners() {
    }

    public void manejarNuevoArchivo() {
    }

    public void manejarAbrir() {
    }

    public void manejarGuardar() {
    }

    public void manejarCompilar() {
    }

    public void manejarEjecutar() {
    }
}
