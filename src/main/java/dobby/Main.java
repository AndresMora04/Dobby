package dobby;

import dobby.vista.MainView;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada de la aplicación Dobby.
 * <p>
 * Se encarga únicamente de inicializar y mostrar la ventana principal
 * ({@link MainView}) en el hilo de eventos de Swing (EDT).
 */
public class Main {

    /**
     * Método principal. Lanza la interfaz gráfica de Dobby.
     *
     * @param args argumentos de línea de comandos (no utilizados por ahora)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainView ventanaPrincipal = new MainView();
            ventanaPrincipal.setVisible(true);
        });
    }
}
