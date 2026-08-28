package dobby.vista;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

public class OutputPanel extends JPanel {
    private JTextArea areaSalida;

    public OutputPanel() {
        setLayout(new BorderLayout());
        areaSalida = new JTextArea();
        areaSalida.setEditable(false);
        areaSalida.setLineWrap(true);
        areaSalida.setWrapStyleWord(true);
        areaSalida.setFont(new Font("Consolas", Font.PLAIN, 14));
        areaSalida.setForeground(new Color(230, 230, 230));
        areaSalida.setBackground(new Color(28, 28, 35));
        areaSalida.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JScrollPane(areaSalida), BorderLayout.CENTER);
    }

    public void agregarMensaje(String mensaje) {
        areaSalida.append(mensaje + System.lineSeparator());
    }

    public void agregarError(String mensajeError) {
        areaSalida.append("[ERROR] " + mensajeError + System.lineSeparator());
    }

    public void limpiar() {
        areaSalida.setText("");
    }
}
