package dobby.vista;

import dobby.controlador.Controlador;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import java.awt.CardLayout;
import java.awt.Dimension;

public class MainView extends JFrame {
    private static final String CARTA_BIENVENIDA = "bienvenida";
    private static final String CARTA_PRINCIPAL = "principal";

    private FileTreePanel panelArbolArchivos;
    private EditorPanel panelEditor;
    private OutputPanel panelSalida;
    private JToolBar barraHerramientas;
    private JMenuBar barraMenu;
    private Controlador controlador;

    private final CardLayout cartas = new CardLayout();
    private final JPanel contenedorCartas = new JPanel(cartas);
    private BienvenidaPanel panelBienvenida;

    public MainView() {
        super("Dobby - Entorno de Desarrollo");
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setSize(1024, 680);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        panelBienvenida = new BienvenidaPanel();
        panelBienvenida.alPresionarEntrar(() -> cartas.show(contenedorCartas, CARTA_PRINCIPAL));

        panelArbolArchivos = new FileTreePanel();
        panelEditor = new EditorPanel();
        panelSalida = new OutputPanel();

        JSplitPane divisionVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelEditor, panelSalida);
        divisionVertical.setResizeWeight(0.75);

        JSplitPane divisionHorizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelArbolArchivos, divisionVertical);
        divisionHorizontal.setResizeWeight(0.2);

        contenedorCartas.add(panelBienvenida, CARTA_BIENVENIDA);
        contenedorCartas.add(divisionHorizontal, CARTA_PRINCIPAL);

        setContentPane(contenedorCartas);
        cartas.show(contenedorCartas, CARTA_BIENVENIDA);
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
