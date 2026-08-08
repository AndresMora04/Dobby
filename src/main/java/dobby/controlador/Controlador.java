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

/**
 * Controlador de la aplicación Dobby.
 * <p>
 * Escucha los eventos generados por los botones y menús de la
 * {@link MainView} (Nuevo, Abrir, Guardar, Compilar, Ejecutar) y orquesta
 * las llamadas correspondientes entre la Vista, el {@link FlowController}
 * y el motor de Dobby ({@link Lexer}, {@link Parser}, {@link Interprete}).
 * <p>
 * No implementa lógica propia del lenguaje Dobby: únicamente coordina a
 * los demás componentes.
 */
public class Controlador {

    private final MainView vista;
    private final FlowController flowController;
    private final Lexer lexer;
    private final Parser parser;
    private final Interprete interprete;

    /**
     * Crea el controlador y lo asocia a la vista principal y al estado global.
     *
     * @param vista          ventana principal de la aplicación
     * @param flowController estado global de la aplicación
     */
    public Controlador(MainView vista, FlowController flowController) {
        // TODO: inicializar lexer, parser, interprete y registrar listeners en la vista
        this.vista = vista;
        this.flowController = flowController;
        this.lexer = new Lexer();
        this.parser = new Parser();
        this.interprete = new Interprete();
    }

    /**
     * Registra los listeners de los botones/menús de la vista, delegando
     * cada acción al método correspondiente de este controlador.
     */
    private void inicializarListeners() {
        // TODO: implementar
    }

    /**
     * Maneja la acción "Nuevo archivo": crea un nuevo {@code ArchivoDobby}
     * vacío y lo muestra en el editor.
     */
    public void manejarNuevoArchivo() {
        // TODO: implementar
    }

    /**
     * Maneja la acción "Abrir": permite seleccionar un archivo o carpeta
     * de proyecto y lo carga en el {@link FlowController} y en la vista.
     */
    public void manejarAbrir() {
        // TODO: implementar
    }

    /**
     * Maneja la acción "Guardar": persiste en disco el contenido actual
     * del editor para el archivo activo.
     */
    public void manejarGuardar() {
        // TODO: implementar
    }

    /**
     * Maneja la acción "Compilar": ejecuta las fases de tokenización y
     * análisis sintáctico sobre el archivo activo, mostrando errores en el
     * {@code OutputPanel} si los hay.
     */
    public void manejarCompilar() {
        // TODO: implementar usando lexer.tokenizar(...) y parser.parsear(...)
    }

    /**
     * Maneja la acción "Ejecutar": compila (si es necesario) e interpreta
     * el archivo activo, mostrando la salida o los errores en el
     * {@code OutputPanel}.
     */
    public void manejarEjecutar() {
        // TODO: implementar usando interprete.ejecutar(...)
    }
}
