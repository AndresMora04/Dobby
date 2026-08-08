package dobby.vista;

import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Panel de solo lectura que muestra la salida de ejecución y los mensajes
 * de error/compilación generados por el {@code motor} de Dobby.
 */
public class OutputPanel extends JPanel {

    private JTextArea areaSalida;

    /**
     * Construye el panel de salida, inicialmente vacío y de solo lectura.
     */
    public OutputPanel() {
        // TODO: inicializar JTextArea (no editable) y layout del panel
    }

    /**
     * Agrega una línea de texto a la salida.
     *
     * @param mensaje mensaje a mostrar
     */
    public void agregarMensaje(String mensaje) {
        // TODO: implementar
    }

    /**
     * Agrega un mensaje de error, formateado de manera distinguible del
     * resto de la salida (por ejemplo, con color o prefijo).
     *
     * @param mensajeError mensaje de error a mostrar
     */
    public void agregarError(String mensajeError) {
        // TODO: implementar
    }

    /**
     * Elimina todo el contenido del panel de salida.
     */
    public void limpiar() {
        // TODO: implementar
    }
}
