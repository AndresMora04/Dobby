package dobby.vista;

import dobby.modelo.Proyecto;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class FileTreePanel extends JPanel {
    private JTree arbol;
    private DefaultTreeModel modeloArbol;

    public FileTreePanel() {
    }

    public void cargarProyecto(Proyecto proyecto) {
    }

    public void limpiar() {
    }

    public JTree getArbol() {
        return arbol;
    }
}
