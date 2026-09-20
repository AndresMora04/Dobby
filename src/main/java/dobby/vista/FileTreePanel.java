package dobby.vista;

import dobby.modelo.ArchivoDobby;
import dobby.modelo.Proyecto;
import dobby.util.TemaManager;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class FileTreePanel extends JPanel {
    private static final Color FONDO = new Color(18, 14, 30);
    private static final Color TEXTO = new Color(210, 205, 195);
    private static final String CARTA_VACIA = "vacia";
    private static final String CARTA_ARBOL = "arbol";

    private record EntradaArchivo(Path ruta, String nombre) {
        @Override
        public String toString() {
            return nombre;
        }
    }

    private record EntradaCarpeta(Path ruta, String nombre) {
        @Override
        public String toString() {
            return nombre;
        }
    }

    private final JTree arbol;
    private final DefaultTreeModel modeloArbol;
    private Path carpetaProyecto;
    private Consumer<Path> alNuevoArchivo;
    private Consumer<Path> alRenombrar;
    private Consumer<Path> alEliminar;
    private final CardLayout cartas = new CardLayout();
    private final JPanel contenido = new JPanel(cartas);
    private Consumer<Path> alAbrirArchivo;
    private Path rutaActiva;
    private Set<Path> rutasModificadas = Collections.emptySet();

    public FileTreePanel(Runnable alPedirCarpeta) {
        setLayout(new BorderLayout());
        modeloArbol = new DefaultTreeModel(new DefaultMutableTreeNode("Sin proyecto abierto"));
        arbol = new JTree(modeloArbol);
        arbol.setRootVisible(true);
        arbol.setBackground(FONDO);
        arbol.setRowHeight(24);

        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        contenido.setBackground(FONDO);

        JScrollPane scroll = new JScrollPane(arbol);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(FONDO);
        contenido.add(crearEstadoVacio(alPedirCarpeta), CARTA_VACIA);
        contenido.add(scroll, CARTA_ARBOL);
        add(contenido, BorderLayout.CENTER);
        cartas.show(contenido, CARTA_VACIA);

        arbol.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    mostrarMenu(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    mostrarMenu(e);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1 || alAbrirArchivo == null) {
                    return;
                }
                TreePath camino = arbol.getPathForLocation(e.getX(), e.getY());
                if (camino != null && camino.getLastPathComponent() instanceof DefaultMutableTreeNode nodo
                    && nodo.getUserObject() instanceof EntradaArchivo entrada) {
                    alAbrirArchivo.accept(entrada.ruta());
                }
            }
        });

        aplicarEstiloCeldas();
        TemaManager.getInstancia().agregarOyente(this::aplicarEstiloCeldas);
    }

    private JPanel crearEstadoVacio(Runnable alPedirCarpeta) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.setBackground(FONDO);
        JPanel columna = new JPanel();
        columna.setOpaque(false);
        columna.setLayout(new javax.swing.BoxLayout(columna, javax.swing.BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Sin proyecto abierto", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        titulo.setForeground(TEXTO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel ayuda = new JLabel("<html><div style='text-align:center'>Abre una carpeta con<br>tus archivos .dobby</div></html>", SwingConstants.CENTER);
        ayuda.setFont(new Font("SansSerif", Font.PLAIN, 12));
        ayuda.setForeground(new Color(140, 134, 152));
        ayuda.setAlignmentX(Component.CENTER_ALIGNMENT);

        BotonBarra boton = new BotonBarra("Abrir carpeta", BotonBarra.Icono.NINGUNO);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.addActionListener(e -> alPedirCarpeta.run());

        columna.add(javax.swing.Box.createRigidArea(new Dimension(0, 36)));
        columna.add(titulo);
        columna.add(javax.swing.Box.createRigidArea(new Dimension(0, 6)));
        columna.add(ayuda);
        columna.add(javax.swing.Box.createRigidArea(new Dimension(0, 18)));
        columna.add(boton);
        panel.add(columna);
        return panel;
    }

    private void aplicarEstiloCeldas() {
        Color acento = TemaManager.getInstancia().getColorAcento();
        DefaultTreeCellRenderer render = new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree arbol, Object valor, boolean seleccionado,
                                                          boolean expandido, boolean hoja, int fila, boolean foco) {
                super.getTreeCellRendererComponent(arbol, valor, seleccionado, expandido, hoja, fila, foco);
                if (valor instanceof DefaultMutableTreeNode nodo && nodo.getUserObject() instanceof EntradaArchivo entrada
                    && rutasModificadas.contains(entrada.ruta())) {
                    setText(entrada.nombre() + "  ●");
                }
                return this;
            }
        };
        render.setBackgroundNonSelectionColor(FONDO);
        render.setBackgroundSelectionColor(acento);
        render.setTextNonSelectionColor(TEXTO);
        render.setTextSelectionColor(Color.WHITE);
        render.setBorderSelectionColor(acento);
        arbol.setCellRenderer(render);
        arbol.repaint();
    }

    public void alAbrirArchivo(Consumer<Path> alAbrirArchivo) {
        this.alAbrirArchivo = alAbrirArchivo;
    }

    public void alNuevoArchivo(Consumer<Path> alNuevoArchivo) {
        this.alNuevoArchivo = alNuevoArchivo;
    }

    public void alRenombrar(Consumer<Path> alRenombrar) {
        this.alRenombrar = alRenombrar;
    }

    public void alEliminar(Consumer<Path> alEliminar) {
        this.alEliminar = alEliminar;
    }

    private void mostrarMenu(MouseEvent e) {
        TreePath camino = arbol.getPathForLocation(e.getX(), e.getY());
        if (camino == null || carpetaProyecto == null
            || !(camino.getLastPathComponent() instanceof DefaultMutableTreeNode nodo)) {
            return;
        }
        arbol.setSelectionPath(camino);

        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(FONDO);
        menu.setBorder(new LineBorder(TemaManager.getInstancia().getColorAcento(), 1));
        Object objeto = nodo.getUserObject();
        if (objeto instanceof EntradaArchivo archivo) {
            menu.add(crearItem("Renombrar...", () -> alRenombrar.accept(archivo.ruta())));
            menu.add(crearItem("Eliminar", () -> alEliminar.accept(archivo.ruta())));
        } else {
            Path carpeta = objeto instanceof EntradaCarpeta entrada ? entrada.ruta() : carpetaProyecto;
            menu.add(crearItem("Nuevo archivo aquí", () -> alNuevoArchivo.accept(carpeta)));
        }
        menu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent evento) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent evento) {
                SwingUtilities.invokeLater(FileTreePanel.this::seleccionarActivo);
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent evento) {
            }
        });
        menu.show(arbol, e.getX(), e.getY());
    }

    private JMenuItem crearItem(String texto, Runnable accion) {
        JMenuItem item = new JMenuItem(texto);
        item.setFont(new Font("SansSerif", Font.PLAIN, 14));
        item.setForeground(TEXTO);
        item.setBackground(FONDO);
        item.setOpaque(true);
        item.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 18));
        item.addActionListener(e -> accion.run());
        return item;
    }

    public void cargarProyecto(Proyecto proyecto) {
        carpetaProyecto = proyecto.getCarpeta();
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode(proyecto.getNombre());
        Map<Path, DefaultMutableTreeNode> carpetas = new HashMap<>();
        carpetas.put(proyecto.getCarpeta(), raiz);

        for (ArchivoDobby archivo : proyecto.getArchivos()) {
            Path ruta = archivo.getRuta();
            DefaultMutableTreeNode padre = nodoCarpeta(carpetas, raiz, proyecto.getCarpeta(), ruta.getParent());
            padre.add(new DefaultMutableTreeNode(new EntradaArchivo(ruta, archivo.getNombre())));
        }
        ordenar(raiz);

        modeloArbol.setRoot(raiz);
        modeloArbol.reload();
        expandirTodo();
        cartas.show(contenido, CARTA_ARBOL);
        seleccionarActivo();
    }

    private DefaultMutableTreeNode nodoCarpeta(Map<Path, DefaultMutableTreeNode> carpetas, DefaultMutableTreeNode raiz,
                                               Path carpetaRaiz, Path carpeta) {
        DefaultMutableTreeNode existente = carpetas.get(carpeta);
        if (existente != null) {
            return existente;
        }
        DefaultMutableTreeNode padre = nodoCarpeta(carpetas, raiz, carpetaRaiz, carpeta.getParent());
        DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(new EntradaCarpeta(carpeta, carpeta.getFileName().toString()));
        padre.add(nodo);
        carpetas.put(carpeta, nodo);
        return nodo;
    }

    private void ordenar(DefaultMutableTreeNode nodo) {
        java.util.List<DefaultMutableTreeNode> hijos = new java.util.ArrayList<>();
        for (Enumeration<?> e = nodo.children(); e.hasMoreElements(); ) {
            hijos.add((DefaultMutableTreeNode) e.nextElement());
        }
        hijos.sort((a, b) -> {
            if (a.isLeaf() != b.isLeaf()) {
                return a.isLeaf() ? 1 : -1;
            }
            return a.toString().compareToIgnoreCase(b.toString());
        });
        nodo.removeAllChildren();
        for (DefaultMutableTreeNode hijo : hijos) {
            nodo.add(hijo);
            ordenar(hijo);
        }
    }

    private void expandirTodo() {
        for (int i = 0; i < arbol.getRowCount(); i++) {
            arbol.expandRow(i);
        }
    }

    public void actualizarEstado(Path activa, Set<Path> modificadas) {
        rutaActiva = activa;
        rutasModificadas = modificadas;
        seleccionarActivo();
        arbol.repaint();
    }

    private void seleccionarActivo() {
        if (rutaActiva == null || !(modeloArbol.getRoot() instanceof DefaultMutableTreeNode raiz)) {
            arbol.clearSelection();
            return;
        }
        for (Enumeration<?> e = raiz.depthFirstEnumeration(); e.hasMoreElements(); ) {
            DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) e.nextElement();
            if (nodo.getUserObject() instanceof EntradaArchivo entrada && entrada.ruta().equals(rutaActiva)) {
                TreePath camino = new TreePath(nodo.getPath());
                arbol.setSelectionPath(camino);
                arbol.scrollPathToVisible(camino);
                return;
            }
        }
        arbol.clearSelection();
    }

    public void limpiar() {
        carpetaProyecto = null;
        modeloArbol.setRoot(new DefaultMutableTreeNode("Sin proyecto abierto"));
        modeloArbol.reload();
        cartas.show(contenido, CARTA_VACIA);
    }

    public JTree getArbol() {
        return arbol;
    }
}
