package dobby.controlador;

import dobby.flow.FlowController;
import dobby.motor.interprete.Interprete;
import dobby.motor.interprete.ResultadoEjecucion;
import dobby.motor.lexer.Lexer;
import dobby.motor.lexer.Token;
import dobby.motor.parser.NodoPrograma;
import dobby.motor.parser.Parser;
import dobby.vista.MainView;

import java.util.ArrayList;
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
        inicializarListeners();
    }

    private void inicializarListeners() {
        vista.setControlador(this);
    }

    public void manejarNuevoArchivo() {
        String ejemplo = """
                Hogwarts() {
                    Alohomora edad: Entero;
                    edad = 11;
                    Revelio "Puede entrar a Hogwarts";
                }
                """;
        vista.getPanelEditor().setTexto(ejemplo);
        vista.getPanelSalida().limpiar();
        vista.getPanelSalida().agregarMensaje("Archivo nuevo creado en memoria.");
        vista.getPanelSalida().agregarMensaje("Ya puedes modificarlo y compilarlo.");
    }

    public void manejarAbrir() {
        vista.getPanelSalida().agregarMensaje("Abrir archivos queda pendiente para la siguiente etapa.");
    }

    public void manejarGuardar() {
        vista.getPanelSalida().agregarMensaje("Guardar archivos queda pendiente para la siguiente etapa.");
    }

    public void manejarCompilar() {
        vista.getPanelSalida().limpiar();
        String codigo = vista.getPanelEditor().getTexto();

        if (codigo == null || codigo.trim().isEmpty()) {
            registrarError("No hay codigo para compilar.");
            return;
        }

        try {
            List<Token> tokens = lexer.tokenizar(codigo);
            NodoPrograma programa = parser.parsear(tokens);

            ResultadoEjecucion resultado = new ResultadoEjecucion();
            resultado.setExito(true);
            resultado.setSalida("Compilacion exitosa.");
            resultado.setErrores(new ArrayList<>());
            flowController.setUltimoResultado(resultado);

            vista.getPanelSalida().agregarMensaje("Compilacion exitosa.");
            vista.getPanelSalida().agregarMensaje("Tokens encontrados: " + Math.max(0, tokens.size() - 1));
            vista.getPanelSalida().agregarMensaje("Declaraciones principales: " + programa.getSentencias().size());
        } catch (RuntimeException e) {
            registrarError(e.getMessage());
        }
    }

    public void manejarEjecutar() {
        ResultadoEjecucion ultimoResultado = flowController.getUltimoResultado();
        if (ultimoResultado == null || !ultimoResultado.isExito()) {
            vista.getPanelSalida().agregarError("Primero debes compilar sin errores.");
            return;
        }
        vista.getPanelSalida().agregarMensaje("Ejecucion pendiente: falta implementar el interprete.");
    }

    private void registrarError(String mensaje) {
        ResultadoEjecucion resultado = new ResultadoEjecucion();
        resultado.setExito(false);
        resultado.setSalida("");
        resultado.setErrores(List.of(mensaje));
        flowController.setUltimoResultado(resultado);
        vista.getPanelSalida().agregarError(mensaje);
    }
}
