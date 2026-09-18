package dobby.vista;

import dobby.util.TemaManager;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

public class OutputPanel extends JPanel {
    private static final Color FONDO = new Color(16, 13, 26);
    private static final Color TEXTO = new Color(224, 220, 210);

    private final JTextArea areaSalida;
    private final JScrollPane scroll;

    public OutputPanel() {
        setLayout(new BorderLayout());
        areaSalida = new JTextArea();
        areaSalida.setEditable(false);
        areaSalida.setLineWrap(true);
        areaSalida.setWrapStyleWord(true);
        areaSalida.setFont(new Font("Consolas", Font.PLAIN, 14));
        areaSalida.setForeground(TEXTO);
        areaSalida.setBackground(FONDO);
        areaSalida.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        scroll = new JScrollPane(areaSalida);
        add(scroll, BorderLayout.CENTER);

        aplicarBorde();
        TemaManager.getInstancia().agregarOyente(this::aplicarBorde);
    }

    private void aplicarBorde() {
        scroll.setBorder(new LineBorder(TemaManager.getInstancia().getColorAcento(), 1));
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
