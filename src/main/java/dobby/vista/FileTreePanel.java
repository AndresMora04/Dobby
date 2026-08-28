package dobby.vista;

import dobby.modelo.Proyecto;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.BorderLayout;

public class FileTreePanel extends JPanel {
    private JTree arbol;
    private DefaultTreeModel modeloArbol;

    public FileTreePanel() {
        setLayout(new BorderLayout());
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Sin proyecto abierto");
        modeloArbol = new DefaultTreeModel(raiz);
        arbol = new JTree(modeloArbol);
        arbol.setRootVisible(true);
        setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        add(new JScrollPane(arbol), BorderLayout.CENTER);
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
