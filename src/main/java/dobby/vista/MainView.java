package dobby.vista;

import dobby.controlador.Controlador;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

public class MainView extends JFrame {
    private FileTreePanel panelArbolArchivos;
    private EditorPanel panelEditor;
    private OutputPanel panelSalida;
    private JToolBar barraHerramientas;
    private JMenuBar barraMenu;
    private Controlador controlador;

    public MainView() {
        super("Dobby - Entorno de Desarrollo");
    }

    private void inicializarComponentes() {
    }

    private JToolBar crearBarraHerramientas() {
        return null;
    }

    private JMenuBar crearBarraMenu() {
        return null;
    }

    public FileTreePanel getPanelArbolArchivos() {
        return panelArbolArchivos;
    }

    public EditorPanel getPanelEditor() {
        return panelEditor;
    }

    public OutputPanel getPanelSalida() {
        return panelSalida;
    }
}
