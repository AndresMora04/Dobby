package dobby.vista;

import dobby.controlador.Controlador;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

/**
 * Ventana principal de la aplicación Dobby.
 * <p>
 * Contiene y organiza los paneles principales de la interfaz:
 * {@link FileTreePanel} (árbol de archivos), {@link EditorPanel} (editor de
 * código) y {@link OutputPanel} (salida/errores), además de la barra de
 * herramientas con las acciones de Compilar, Ejecutar, Nuevo, Abrir y
 * Guardar. No contiene lógica de negocio: delega todo en el
 * {@link Controlador}.
 */
public class MainView extends JFrame {

    private FileTreePanel panelArbolArchivos;
    private EditorPanel panelEditor;
    private OutputPanel panelSalida;
    private JToolBar barraHerramientas;
    private JMenuBar barraMenu;
    private Controlador controlador;

    /**
     * Construye la ventana principal, inicializando todos sus componentes
     * y el controlador asociado.
     */
    public MainView() {
        super("Dobby - Entorno de Desarrollo");
        // TODO: inicializar controlador, paneles y layout general
        // TODO: llamar a inicializarComponentes()
    }

    /**
     * Inicializa y organiza los componentes visuales de la ventana
     * (menú, barra de herramientas, paneles y su distribución mediante
     * {@link JSplitPane}).
     */
    private void inicializarComponentes() {
        // TODO: implementar
    }

    /**
     * Construye la barra de herramientas con los botones de acción
     * (Nuevo, Abrir, Guardar, Compilar, Ejecutar).
     *
     * @return la barra de herramientas configurada
     */
    private JToolBar crearBarraHerramientas() {
        // TODO: implementar
        return null;
    }

    /**
     * Construye la barra de menú de la aplicación.
     *
     * @return la barra de menú configurada
     */
    private JMenuBar crearBarraMenu() {
        // TODO: implementar
        return null;
    }

    /**
     * @return el panel del árbol de archivos
     */
    public FileTreePanel getPanelArbolArchivos() {
        // TODO: implementar
        return panelArbolArchivos;
    }

    /**
     * @return el panel del editor de código
     */
    public EditorPanel getPanelEditor() {
        // TODO: implementar
        return panelEditor;
    }

    /**
     * @return el panel de salida/errores
     */
    public OutputPanel getPanelSalida() {
        // TODO: implementar
        return panelSalida;
    }
}
