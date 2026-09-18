package dobby.vista;

import dobby.controlador.Controlador;
import dobby.util.TemaManager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class MainView extends JFrame {
    private static final String CARTA_BIENVENIDA = "bienvenida";
    private static final String CARTA_TRANSICION = "transicion";
    private static final String CARTA_PRINCIPAL = "principal";
    private static final Color FONDO_BARRA = new Color(18, 14, 30);
    private static final Color FONDO_BOTON = new Color(30, 24, 48);

    private FileTreePanel panelArbolArchivos;
    private EditorPanel panelEditor;
    private OutputPanel panelSalida;
    private JToolBar barraHerramientas;
    private JMenuBar barraMenu;
    private Controlador controlador;
    private JButton botonNuevo;
    private JButton botonAbrir;
    private JButton botonGuardar;
    private JButton botonCompilar;
    private JButton botonEjecutar;
    private final List<JButton> botonesBarra = new ArrayList<>();

    private final CardLayout cartas = new CardLayout();
    private final JPanel contenedorCartas = new JPanel(cartas);
    private BienvenidaPanel panelBienvenida;
    private TransicionAndenPanel panelTransicion;

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
        panelTransicion = new TransicionAndenPanel();
        panelBienvenida.alPresionarEntrar(() -> {
            cartas.show(contenedorCartas, CARTA_TRANSICION);
            panelTransicion.iniciar(() -> cartas.show(contenedorCartas, CARTA_PRINCIPAL));
        });

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
        contenedorCartas.add(panelTransicion, CARTA_TRANSICION);
        contenedorCartas.add(panelPrincipal, CARTA_PRINCIPAL);

        setJMenuBar(barraMenu);
        setContentPane(contenedorCartas);
        cartas.show(contenedorCartas, CARTA_BIENVENIDA);

        aplicarColoresBarra();
        TemaManager.getInstancia().agregarOyente(this::aplicarColoresBarra);
    }

    private JToolBar crearBarraHerramientas() {
        JToolBar barra = new JToolBar();
        barra.setFloatable(false);
        barra.setBackground(FONDO_BARRA);
        barra.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        botonNuevo = crearBotonBarra("Nuevo");
        botonAbrir = crearBotonBarra("Abrir");
        botonGuardar = crearBotonBarra("Guardar");
        botonCompilar = crearBotonBarra("Compilar");
        botonEjecutar = crearBotonBarra("Ejecutar");

        botonNuevo.addActionListener(e -> {
            if (controlador != null) {
                controlador.manejarNuevoArchivo();
            }
        });
        botonAbrir.addActionListener(e -> {
            if (controlador != null) {
                controlador.manejarAbrir();
            }
        });
        botonGuardar.addActionListener(e -> {
            if (controlador != null) {
                controlador.manejarGuardar();
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
        barra.add(botonAbrir);
        barra.add(botonGuardar);
        barra.addSeparator();
        barra.add(botonCompilar);
        barra.add(botonEjecutar);
        return barra;
    }

    private JButton crearBotonBarra(String texto) {
        JButton boton = new JButton(texto);
        boton.setFocusPainted(false);
        boton.setBackground(FONDO_BOTON);
        boton.setOpaque(true);
        botonesBarra.add(boton);
        return boton;
    }

    private void aplicarColoresBarra() {
        Color acento = TemaManager.getInstancia().getColorAcento();
        Color acentoClaro = TemaManager.getInstancia().getColorAcentoClaro();
        for (JButton boton : botonesBarra) {
            boton.setForeground(acentoClaro);
            boton.setBorder(new LineBorder(acento, 1));
        }
    }

    private JMenuBar crearBarraMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemNuevo = new JMenuItem("Nuevo");
        JMenuItem itemAbrir = new JMenuItem("Abrir");
        JMenuItem itemGuardar = new JMenuItem("Guardar");
        JMenuItem itemInicio = new JMenuItem("Inicio");
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemNuevo.addActionListener(e -> {
            if (controlador != null) {
                controlador.manejarNuevoArchivo();
            }
        });
        itemAbrir.addActionListener(e -> {
            if (controlador != null) {
                controlador.manejarAbrir();
            }
        });
        itemGuardar.addActionListener(e -> {
            if (controlador != null) {
                controlador.manejarGuardar();
            }
        });
        itemInicio.addActionListener(e -> {
            cartas.show(contenedorCartas, CARTA_TRANSICION);
            panelTransicion.iniciar(() -> cartas.show(contenedorCartas, CARTA_BIENVENIDA));
        });
        itemSalir.addActionListener(e -> dispose());
        menuArchivo.add(itemNuevo);
        menuArchivo.add(itemAbrir);
        menuArchivo.add(itemGuardar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemInicio);
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
