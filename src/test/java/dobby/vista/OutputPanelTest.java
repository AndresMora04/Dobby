package dobby.vista;

import org.junit.jupiter.api.Test;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OutputPanelTest {
    @Test
    void muestraSalidaLargaSinAnimacionYRespetaElOrden() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            OutputPanel panel = new OutputPanel();
            String salida = "1234567890".repeat(100);
            panel.agregarMensaje("Encabezado");
            panel.agregarMensaje(salida);
            JScrollPane scroll = (JScrollPane) panel.getComponent(0);
            JTextArea area = (JTextArea) scroll.getViewport().getView();
            assertEquals("Encabezado" + System.lineSeparator() + salida, area.getText());
            panel.agregarMensaje("Mensaje posterior");
            panel.agregarError("Fin");
            assertEquals("Encabezado" + System.lineSeparator() + salida + System.lineSeparator()
                + "Mensaje posterior" + System.lineSeparator() + "[ERROR] Fin", area.getText());
            panel.limpiar();
        });
    }

    @Test
    void muestraErrorInmediatamenteYConservaSalidaPendiente() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            OutputPanel panel = new OutputPanel();
            panel.agregarMensaje("Salida anterior");
            panel.agregarError("Error de prueba");
            JScrollPane scroll = (JScrollPane) panel.getComponent(0);
            JTextArea area = (JTextArea) scroll.getViewport().getView();
            assertEquals("Salida anterior" + System.lineSeparator() + "[ERROR] Error de prueba", area.getText());
            panel.limpiar();
            assertEquals("", area.getText());
        });
    }
}
