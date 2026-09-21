package dobby.vista;

import dobby.modelo.ArchivoDobby;
import dobby.modelo.Proyecto;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FileTreePanelTest {
    @Test
    void distinguePrincipalActivoYModificadoYLimpiaLaMarca() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Path carpeta = Path.of("proyecto").toAbsolutePath();
            Proyecto proyecto = new Proyecto("proyecto", carpeta);
            ArchivoDobby entrada = new ArchivoDobby(carpeta.resolve("inicio.dobby"));
            entrada.setNombre("inicio.dobby");
            proyecto.agregarArchivo(entrada);
            proyecto.setArchivoPrincipal(entrada);
            FileTreePanel panel = new FileTreePanel(() -> {});
            panel.cargarProyecto(proyecto);
            panel.actualizarEstado(entrada.getRuta(), Set.of(entrada.getRuta()));
            JTree arbol = panel.getArbol();
            DefaultMutableTreeNode raiz = (DefaultMutableTreeNode) arbol.getModel().getRoot();
            Object nodo = raiz.getChildAt(0);
            JLabel etiqueta = (JLabel) arbol.getCellRenderer()
                .getTreeCellRendererComponent(arbol, nodo, true, false, true, 1, false);
            assertTrue(etiqueta.getText().contains("(principal)"));
            assertTrue(etiqueta.getText().length() > "inicio.dobby (principal)".length());
            assertEquals(nodo, arbol.getLastSelectedPathComponent());
            panel.actualizarPrincipal(null, "Hay dos Hogwarts");
            etiqueta = (JLabel) arbol.getCellRenderer()
                .getTreeCellRendererComponent(arbol, nodo, true, false, true, 1, false);
            assertFalse(etiqueta.getText().contains("(principal)"));
            panel.limpiar();
            assertEquals("Sin proyecto abierto", arbol.getModel().getRoot().toString());
        });
    }
}
