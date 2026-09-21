package dobby.vista;

import org.junit.jupiter.api.Test;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OutputPanelTest {
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
