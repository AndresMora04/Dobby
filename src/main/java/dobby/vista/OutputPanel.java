package dobby.vista;

import dobby.util.TemaManager;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayDeque;
import java.util.Deque;

public class OutputPanel extends JPanel {
    private static final Color FONDO = new Color(16, 13, 26);
    private static final Color TEXTO = new Color(224, 220, 210);
    private static final int VELOCIDAD_MS = 35;

    private final JTextArea areaSalida;
    private final JScrollPane scroll;
    private final Timer temporizador;
    private final Deque<String> colaMensajes = new ArrayDeque<>();
    private String lineaActual;
    private int posicion;

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

        temporizador = new Timer(VELOCIDAD_MS, e -> escribirSiguienteCaracter());
    }

    private void aplicarBorde() {
        scroll.setBorder(new LineBorder(TemaManager.getInstancia().getColorAcento(), 1));
    }

    private void escribirSiguienteCaracter() {
        if (lineaActual != null && posicion < lineaActual.length()) {
            areaSalida.append(String.valueOf(lineaActual.charAt(posicion)));
            posicion++;
            return;
        }
        if (!colaMensajes.isEmpty()) {
            if (areaSalida.getDocument().getLength() > 0) {
                areaSalida.append(System.lineSeparator());
            }
            lineaActual = colaMensajes.poll();
            posicion = 0;
            return;
        }
        lineaActual = null;
        temporizador.stop();
    }

    private void encolar(String texto) {
        colaMensajes.add(texto);
        if (!temporizador.isRunning()) {
            temporizador.start();
        }
    }

    public void agregarMensaje(String mensaje) {
        encolar(mensaje);
    }

    public void agregarError(String mensajeError) {
        temporizador.stop();
        if (lineaActual != null) {
            areaSalida.append(lineaActual.substring(posicion));
        }
        while (!colaMensajes.isEmpty()) {
            if (areaSalida.getDocument().getLength() > 0) {
                areaSalida.append(System.lineSeparator());
            }
            areaSalida.append(colaMensajes.poll());
        }
        if (areaSalida.getDocument().getLength() > 0) {
            areaSalida.append(System.lineSeparator());
        }
        areaSalida.append("[ERROR] " + mensajeError);
        lineaActual = null;
        posicion = 0;
    }

    public void limpiar() {
        temporizador.stop();
        colaMensajes.clear();
        lineaActual = null;
        posicion = 0;
        areaSalida.setText("");
    }
}
