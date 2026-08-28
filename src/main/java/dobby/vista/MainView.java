package dobby.vista;

import dobby.controlador.Controlador;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
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
    private JButton botonNuevo;
    private JButton botonCompilar;
    private JButton botonEjecutar;

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
        barraHerramientas = crearBarraHerramientas();
        barraMenu = crearBarraMenu();

        JSplitPane divisionVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelEditor, panelSalida);
        divisionVertical.setResizeWeight(0.75);

        JSplitPane divisionHorizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelArbolArchivos, divisionVertical);
        divisionHorizontal.setResizeWeight(0.2);

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.add(barraHerramientas, BorderLayout.NORTH);
        panelPrincipal.add(divisionHorizontal, BorderLayout.CENTER);

        contenedorCartas.add(panelBienvenida, CARTA_BIENVENIDA);
        contenedorCartas.add(panelPrincipal, CARTA_PRINCIPAL);

        setJMenuBar(barraMenu);
        setContentPane(contenedorCartas);
        cartas.show(contenedorCartas, CARTA_BIENVENIDA);
    }

    private JToolBar crearBarraHerramientas() {
        JToolBar barra = new JToolBar();
        barra.setFloatable(false);

        botonNuevo = new JButton("Nuevo");
        botonCompilar = new JButton("Compilar");
        botonEjecutar = new JButton("Ejecutar");

        botonNuevo.addActionListener(e -> {
            if (controlador != null) {
                controlador.manejarNuevoArchivo();
            }
        });
        botonCompilar.addActionListener(e -> {
            if (controlador != null) {
                controlador.manejarCompilar();
            }
        });
        botonEjecutar.addActionListener(e -> {
            if (controlador != null) {
                controlador.manejarEjecutar();
            }
        });

        barra.add(botonNuevo);
        barra.addSeparator();
        barra.add(botonCompilar);
        barra.add(botonEjecutar);
        return barra;
    }

    private JMenuBar crearBarraMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemNuevo = new JMenuItem("Nuevo");
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemNuevo.addActionListener(e -> {
            if (controlador != null) {
                controlador.manejarNuevoArchivo();
            }
        });
        itemSalir.addActionListener(e -> dispose());
        menuArchivo.add(itemNuevo);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);

        JMenu menuProyecto = new JMenu("Proyecto");
        JMenuItem itemCompilar = new JMenuItem("Compilar");
        JMenuItem itemEjecutar = new JMenuItem("Ejecutar");
        itemCompilar.addActionListener(e -> {
            if (controlador != null) {
                controlador.manejarCompilar();
            }
        });
        itemEjecutar.addActionListener(e -> {
            if (controlador != null) {
                controlador.manejarEjecutar();
            }
        });
        menuProyecto.add(itemCompilar);
        menuProyecto.add(itemEjecutar);

        menuBar.add(menuArchivo);
        menuBar.add(menuProyecto);
        return menuBar;
    }

    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
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
