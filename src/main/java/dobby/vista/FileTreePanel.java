package dobby.vista;

import dobby.modelo.Proyecto;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Panel que muestra el árbol de archivos ({@code .dobby}) del proyecto
 * activo mediante un {@link JTree}.
 * <p>
 * Permite al usuario navegar y seleccionar archivos del proyecto para
 * abrirlos en el {@link EditorPanel}.
 */
public class FileTreePanel extends JPanel {

    private JTree arbol;
    private DefaultTreeModel modeloArbol;

    /**
     * Construye el panel del árbol de archivos, inicialmente vacío.
     */
    public FileTreePanel() {
        // TODO: inicializar JTree y layout del panel
    }

    /**
     * Reconstruye el árbol de archivos a partir del proyecto dado.
     *
     * @param proyecto proyecto cuyo contenido se debe mostrar en el árbol
     */
    public void cargarProyecto(Proyecto proyecto) {
        // TODO: implementar
    }

    /**
     * Limpia el árbol de archivos, dejándolo vacío.
     */
    public void limpiar() {
        // TODO: implementar
    }

    /**
     * @return el componente {@link JTree} utilizado por este panel
     */
    public JTree getArbol() {
        // TODO: implementar
        return arbol;
    }
}
