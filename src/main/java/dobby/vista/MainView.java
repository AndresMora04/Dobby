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
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    private PestanasEditor panelPestanas;
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
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salir();
            }
        });
        aplicarIconoVentana();

        panelBienvenida = new BienvenidaPanel();
        panelTransicion = new TransicionAndenPanel();
        panelBienvenida.alPresionarEntrar(() -> {
            mostrarCarta(CARTA_TRANSICION);
            panelTransicion.iniciar(() -> mostrarCarta(CARTA_PRINCIPAL));
        });

        panelArbolArchivos = new FileTreePanel(() -> {
            if (controlador != null) {
                controlador.manejarAbrirCarpeta();
            }
        });
        panelEjemplos = new EjemplosPanel();
        panelEditor = new EditorPanel();
        panelPestanas = new PestanasEditor();
        panelSalida = new OutputPanel();
        barraHerramientas = crearBarraHerramientas();
        barraMenu = crearBarraMenu();

        panelEjemplos.alInsertarEjemplo(panelEditor::insertarEnCursor);

        JPanel panelLateral = crearPanelLateral();

        JPanel zonaEditor = new JPanel(new BorderLayout());
        zonaEditor.add(panelPestanas, BorderLayout.NORTH);
        zonaEditor.add(panelEditor, BorderLayout.CENTER);

        JSplitPane divisionVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT, zonaEditor, panelSalida);
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

    private JMenuItem crearItem(String texto, KeyStroke atajo, Runnable accion) {
        JMenuItem item = estilizarItem(new JMenuItem(texto));
        if (atajo != null) {
            item.setAccelerator(atajo);
        }
        item.addActionListener(e -> {
            if (controlador != null) {
                accion.run();
            }
        });
        return item;
    }

    private KeyStroke atajo(int tecla, boolean mayuscula) {
        int mascara = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        return KeyStroke.getKeyStroke(tecla, mayuscula ? mascara | InputEvent.SHIFT_DOWN_MASK : mascara);
    }

    private JMenuBar crearBarraMenu() {
        aplicarColoresUIManagerMenu();

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(FONDO_BARRA);
        menuBar.setBorder(BorderFactory.createEmptyBorder());

        JMenu menuArchivo = estilizarMenu(new JMenu("Archivo"));
        menuArchivo.add(crearItem("Nuevo", atajo(KeyEvent.VK_N, false), () -> controlador.manejarNuevoArchivo()));
        menuArchivo.add(crearItem("Abrir archivo...", atajo(KeyEvent.VK_O, false), () -> controlador.manejarAbrir()));
        menuArchivo.add(crearItem("Abrir carpeta...", atajo(KeyEvent.VK_O, true), () -> controlador.manejarAbrirCarpeta()));
        menuArchivo.addSeparator();
        menuArchivo.add(crearItem("Guardar", atajo(KeyEvent.VK_S, false), () -> controlador.manejarGuardar()));
        menuArchivo.add(crearItem("Guardar como...", atajo(KeyEvent.VK_S, true), () -> controlador.manejarGuardarComo()));
        menuArchivo.add(crearItem("Guardar todo", null, () -> controlador.manejarGuardarTodo()));
        menuArchivo.addSeparator();
        menuArchivo.add(crearItem("Cerrar pestaña", atajo(KeyEvent.VK_W, false), () -> controlador.manejarCerrarPestana()));
        menuArchivo.addSeparator();
        JMenuItem itemInicio = estilizarItem(new JMenuItem("Inicio"));
        itemInicio.addActionListener(e -> {
            mostrarCarta(CARTA_TRANSICION);
            panelTransicion.iniciar(() -> mostrarCarta(CARTA_BIENVENIDA));
        });
        menuArchivo.add(itemInicio);
        JMenuItem itemSalir = estilizarItem(new JMenuItem("Salir"));
        itemSalir.addActionListener(e -> salir());
        menuArchivo.add(itemSalir);

        JMenu menuProyecto = estilizarMenu(new JMenu("Proyecto"));
        menuProyecto.add(crearItem("Compilar", KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), () -> controlador.manejarCompilar()));
        menuProyecto.add(crearItem("Ejecutar", KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), () -> controlador.manejarEjecutar()));

        JMenu menuAyuda = estilizarMenu(new JMenu("Ayuda"));
        JMenuItem itemReferencia = estilizarItem(new JMenuItem("Referencia de Dobby"));
        itemReferencia.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        itemReferencia.addActionListener(e -> new AyudaDialog(this).setVisible(true));
        JMenuItem itemAcercaDe = estilizarItem(new JMenuItem("Acerca de Dobby"));
        itemAcercaDe.addActionListener(e -> new AcercaDeDialog(this).setVisible(true));
        JMenuItem itemCreditos = estilizarItem(new JMenuItem("Créditos"));
        itemCreditos.addActionListener(e -> new CreditosDialog(this).setVisible(true));
        menuAyuda.add(itemReferencia);
        menuAyuda.addSeparator();
        menuAyuda.add(itemAcercaDe);
        menuAyuda.add(itemCreditos);

        menuBar.add(menuArchivo);
        menuBar.add(menuProyecto);
        menuBar.add(menuAyuda);
        return menuBar;
    }

    private void salir() {
        if (controlador != null) {
            controlador.manejarSalir();
        } else {
            System.exit(0);
        }
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

    public PestanasEditor getPanelPestanas() {
        return panelPestanas;
    }

    public OutputPanel getPanelSalida() {
        return panelSalida;
    }
}
