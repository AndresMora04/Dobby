package dobby.vista;

import dobby.modelo.Proyecto;
import dobby.util.TemaManager;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import java.awt.BorderLayout;
import java.awt.Color;

public class FileTreePanel extends JPanel {
    private static final Color FONDO = new Color(18, 14, 30);
    private static final Color TEXTO = new Color(210, 205, 195);

    private final JTree arbol;
    private final DefaultTreeModel modeloArbol;

    public FileTreePanel() {
        setLayout(new BorderLayout());
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Sin proyecto abierto");
        modeloArbol = new DefaultTreeModel(raiz);
        arbol = new JTree(modeloArbol);
        arbol.setRootVisible(true);
        arbol.setBackground(FONDO);

        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        JScrollPane scroll = new JScrollPane(arbol);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(FONDO);
        add(scroll, BorderLayout.CENTER);

        aplicarEstiloCeldas();
        TemaManager.getInstancia().agregarOyente(this::aplicarEstiloCeldas);
    }

    private void aplicarEstiloCeldas() {
        DefaultTreeCellRenderer render = new DefaultTreeCellRenderer();
        Color acento = TemaManager.getInstancia().getColorAcento();
        render.setBackgroundNonSelectionColor(FONDO);
        render.setBackgroundSelectionColor(acento);
        render.setTextNonSelectionColor(TEXTO);
        render.setTextSelectionColor(Color.WHITE);
        render.setBorderSelectionColor(acento);
        arbol.setCellRenderer(render);
        arbol.repaint();
    }

    public void cargarProyecto(Proyecto proyecto) {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode(proyecto.getNombre());
        if (proyecto.getArchivos() != null) {
            proyecto.getArchivos().forEach(archivo -> raiz.add(new DefaultMutableTreeNode(archivo.getNombre())));
        }
        modeloArbol.setRoot(raiz);
        modeloArbol.reload();
    }

    public void limpiar() {
        modeloArbol.setRoot(new DefaultMutableTreeNode("Sin proyecto abierto"));
        modeloArbol.reload();
    }

    public JTree getArbol() {
        return arbol;
    }
}
