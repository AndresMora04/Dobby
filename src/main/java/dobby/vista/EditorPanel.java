package dobby.vista;

import javax.swing.JPanel;
import javax.swing.JTextPane;

/**
 * Panel de edición de código Dobby.
 * <p>
 * Contiene un {@link JTextPane} donde el usuario escribe el código fuente
 * {@code .dobby} del archivo actualmente abierto. En el futuro podrá
 * incorporar resaltado de sintaxis básico usando
 * {@code dobby.util.PalabrasReservadas}.
 */
public class EditorPanel extends JPanel {

    private JTextPane areaTexto;

    /**
     * Construye el panel del editor, inicialmente vacío.
     */
    public EditorPanel() {
        // TODO: inicializar JTextPane y layout del panel
    }

    /**
     * @return el texto actual del editor
     */
    public String getTexto() {
        // TODO: implementar
        return null;
    }

    /**
     * Reemplaza el contenido del editor con el texto dado.
     *
     * @param texto nuevo contenido del editor
     */
    public void setTexto(String texto) {
        // TODO: implementar
    }

    /**
     * Elimina todo el contenido del editor.
     */
    public void limpiar() {
        // TODO: implementar
    }
}
