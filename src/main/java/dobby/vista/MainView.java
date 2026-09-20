package dobby.vista;

import dobby.controlador.Controlador;
import dobby.util.TemaManager;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainView extends JFrame {
    private static final String CARTA_BIENVENIDA = "bienvenida";
    private static final String CARTA_TRANSICION = "transicion";
    private static final String CARTA_PRINCIPAL = "principal";
    private static final Color FONDO_BARRA = new Color(18, 14, 30);
    private static final Color FONDO_BOTON = new Color(30, 24, 48);

    private FileTreePanel panelArbolArchivos;
    private EjemplosPanel panelEjemplos;
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
    private final List<JToggleButton> botonesPestanaLateral = new ArrayList<>();

    private final CardLayout cartas = new CardLayout();
    private final JPanel contenedorCartas = new JPanel(cartas);
    private BienvenidaPanel panelBienvenida;
    private TransicionAndenPanel panelTransicion;
    private String cartaActual = CARTA_BIENVENIDA;

    public MainView() {
        super("Dobby");
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setSize(1024, 680);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        aplicarIconoVentana();

        panelBienvenida = new BienvenidaPanel();
        panelTransicion = new TransicionAndenPanel();
        panelBienvenida.alPresionarEntrar(() -> {
            mostrarCarta(CARTA_TRANSICION);
            panelTransicion.iniciar(() -> mostrarCarta(CARTA_PRINCIPAL));
        });

        panelArbolArchivos = new FileTreePanel();
        panelEjemplos = new EjemplosPanel();
        panelEditor = new EditorPanel();
        panelSalida = new OutputPanel();
        barraHerramientas = crearBarraHerramientas();
        barraMenu = crearBarraMenu();

        panelEjemplos.alInsertarEjemplo(panelEditor::insertarEnCursor);

        JPanel panelLateral = crearPanelLateral();

        JSplitPane divisionVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelEditor, panelSalida);
        divisionVertical.setResizeWeight(0.75);

        JSplitPane divisionHorizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelLateral, divisionVertical);
        divisionHorizontal.setResizeWeight(0.2);

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.add(barraHerramientas, BorderLayout.NORTH);
        panelPrincipal.add(divisionHorizontal, BorderLayout.CENTER);

        contenedorCartas.add(panelBienvenida, CARTA_BIENVENIDA);
        contenedorCartas.add(panelTransicion, CARTA_TRANSICION);
        contenedorCartas.add(panelPrincipal, CARTA_PRINCIPAL);

        setContentPane(contenedorCartas);
        mostrarCarta(CARTA_BIENVENIDA);

        aplicarColoresBarra();
        TemaManager.getInstancia().agregarOyente(this::aplicarColoresBarra);
    }

    private JToolBar crearBarraHerramientas() {
        JToolBar barra = new JToolBar();
        barra.setFloatable(false);
        barra.setBackground(FONDO_BARRA);
        barra.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        botonNuevo = new BotonBarra("Nuevo", BotonBarra.Icono.NUEVO);
        botonAbrir = new BotonBarra("Abrir", BotonBarra.Icono.ABRIR);
        botonGuardar = new BotonBarra("Guardar", BotonBarra.Icono.GUARDAR);
        botonCompilar = new BotonBarra("Compilar", BotonBarra.Icono.COMPILAR);
        botonEjecutar = new BotonBarra("Ejecutar", BotonBarra.Icono.EJECUTAR);
        botonesBarra.add(botonNuevo);
        botonesBarra.add(botonAbrir);
        botonesBarra.add(botonGuardar);
        botonesBarra.add(botonCompilar);
        botonesBarra.add(botonEjecutar);

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
        barra.add(Box.createHorizontalStrut(8));
        barra.add(botonAbrir);
        barra.add(Box.createHorizontalStrut(8));
        barra.add(botonGuardar);
        barra.add(Box.createHorizontalStrut(18));
        barra.add(botonCompilar);
        barra.add(Box.createHorizontalStrut(8));
        barra.add(botonEjecutar);
        return barra;
    }

    private void aplicarIconoVentana() {
        try (InputStream in = getClass().getResourceAsStream("/assets/imagenes/Dobby.png")) {
            if (in == null) {
                return;
            }
            setIconImage(new ImageIcon(in.readAllBytes()).getImage());
        } catch (IOException e) {
        }
    }

    private JPanel crearPanelLateral() {
        CardLayout cartasLaterales = new CardLayout();
        JPanel contenidoLateral = new JPanel(cartasLaterales);
        contenidoLateral.add(panelArbolArchivos, "archivos");
        contenidoLateral.add(panelEjemplos, "ejemplos");

        JToggleButton botonArchivos = crearBotonPestanaLateral("Archivos");
        JToggleButton botonEjemplos = crearBotonPestanaLateral("Ejemplos");
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(botonArchivos);
        grupo.add(botonEjemplos);
        botonArchivos.setSelected(true);
        botonArchivos.addActionListener(e -> {
            cartasLaterales.show(contenidoLateral, "archivos");
            aplicarColoresPestanaLateral();
        });
        botonEjemplos.addActionListener(e -> {
            cartasLaterales.show(contenidoLateral, "ejemplos");
            aplicarColoresPestanaLateral();
        });

        JPanel cabecera = new JPanel(new GridLayout(1, 2));
        cabecera.setBackground(FONDO_BARRA);
        cabecera.add(botonArchivos);
        cabecera.add(botonEjemplos);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(cabecera, BorderLayout.NORTH);
        panel.add(contenidoLateral, BorderLayout.CENTER);
        return panel;
    }

    private JToggleButton crearBotonPestanaLateral(String texto) {
        JToggleButton boton = new JToggleButton(texto);
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
        botonesPestanaLateral.add(boton);
        return boton;
    }

    private void aplicarColoresPestanaLateral() {
        Color acento = TemaManager.getInstancia().getColorAcento();
        for (JToggleButton boton : botonesPestanaLateral) {
            boton.setBackground(boton.isSelected() ? acento : FONDO_BOTON);
            boton.setForeground(boton.isSelected() ? Color.WHITE : new Color(210, 205, 195));
        }
    }

    private void mostrarCarta(String nombre) {
        cartaActual = nombre;
        cartas.show(contenedorCartas, nombre);
        setJMenuBar(CARTA_PRINCIPAL.equals(nombre) ? barraMenu : null);
        revalidate();
        repaint();
    }

    private void aplicarColoresBarra() {
        aplicarColoresPestanaLateral();
        botonesBarra.forEach(JButton::repaint);
        barraMenu = crearBarraMenu();
        if (CARTA_PRINCIPAL.equals(cartaActual)) {
            setJMenuBar(barraMenu);
        }
        revalidate();
        repaint();
    }

    private JMenu estilizarMenu(JMenu menu) {
        menu.setFont(ModalDialog.cargarFuenteTitulo(14f));
        menu.setForeground(new Color(232, 227, 218));
        menu.setBackground(FONDO_BARRA);
        menu.setOpaque(true);
        menu.getPopupMenu().setBorder(new LineBorder(TemaManager.getInstancia().getColorAcento(), 1));
        return menu;
    }

    private JMenuItem estilizarItem(JMenuItem item) {
        item.setFont(new Font("SansSerif", Font.PLAIN, 14));
        item.setForeground(new Color(224, 220, 210));
        item.setBackground(FONDO_BARRA);
        item.setOpaque(true);
        item.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 18));
        return item;
    }

    private void aplicarColoresUIManagerMenu() {
        Color acento = TemaManager.getInstancia().getColorAcento();
        Color texto = new Color(224, 220, 210);
        UIManager.put("Menu.background", FONDO_BARRA);
        UIManager.put("Menu.foreground", texto);
        UIManager.put("Menu.selectionBackground", acento);
        UIManager.put("Menu.selectionForeground", Color.WHITE);
        UIManager.put("MenuItem.background", FONDO_BARRA);
        UIManager.put("MenuItem.foreground", texto);
        UIManager.put("MenuItem.selectionBackground", acento);
        UIManager.put("MenuItem.selectionForeground", Color.WHITE);
        UIManager.put("PopupMenu.background", FONDO_BARRA);
        UIManager.put("Separator.background", FONDO_BARRA);
        UIManager.put("Separator.foreground", acento);
    }

    private JMenuBar crearBarraMenu() {
        aplicarColoresUIManagerMenu();

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(FONDO_BARRA);
        menuBar.setBorder(BorderFactory.createEmptyBorder());

        JMenu menuArchivo = estilizarMenu(new JMenu("Archivo"));
        JMenuItem itemNuevo = estilizarItem(new JMenuItem("Nuevo"));
        JMenuItem itemAbrir = estilizarItem(new JMenuItem("Abrir"));
        JMenuItem itemGuardar = estilizarItem(new JMenuItem("Guardar"));
        JMenuItem itemInicio = estilizarItem(new JMenuItem("Inicio"));
        JMenuItem itemSalir = estilizarItem(new JMenuItem("Salir"));
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
            mostrarCarta(CARTA_TRANSICION);
            panelTransicion.iniciar(() -> mostrarCarta(CARTA_BIENVENIDA));
        });
        itemSalir.addActionListener(e -> dispose());
        menuArchivo.add(itemNuevo);
        menuArchivo.add(itemAbrir);
        menuArchivo.add(itemGuardar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemInicio);
        menuArchivo.add(itemSalir);

        JMenu menuProyecto = estilizarMenu(new JMenu("Proyecto"));
        JMenuItem itemCompilar = estilizarItem(new JMenuItem("Compilar"));
        JMenuItem itemEjecutar = estilizarItem(new JMenuItem("Ejecutar"));
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
