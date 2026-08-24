package dobby.vista;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

public class CreditosDialog extends ModalDialog {

    private static final int ANCHO_CONTENIDO = 752;
    private static final int ALTO_CONTENIDO = 392;
    private static final int ANCHO_BLOQUE = 480;

    public CreditosDialog(Window propietario) {
        super(propietario, "Créditos", 800, 480, construirContenido());
    }

    private static JPanel construirContenido() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        List<JLabel> etiquetas = new ArrayList<>();
        int y = 0;
        y = agregarSeccion(etiquetas, y, "Integrantes",
            "José Andrés Mora Mora\nDarien Arroyo Castro");
        y = agregarSeccion(etiquetas, y, "Curso",
            "Paradigmas de Programación\nUniversidad Nacional, Sede Regional Brunca\nCampus Pérez Zeledón - Prof. Pablo A. Venegas Elizondo");

        int altoTotal = y - 18;
        int offsetX = (ANCHO_CONTENIDO - ANCHO_BLOQUE) / 2;
        int offsetY = Math.max(0, (ALTO_CONTENIDO - altoTotal) / 2);

        for (JLabel etiqueta : etiquetas) {
            etiqueta.setBounds(etiqueta.getX() + offsetX, etiqueta.getY() + offsetY, etiqueta.getWidth(), etiqueta.getHeight());
            panel.add(etiqueta);
        }

        return panel;
    }

    private static int agregarSeccion(List<JLabel> etiquetas, int y, String titulo, String cuerpo) {
        JLabel etiquetaTitulo = new JLabel(titulo);
        etiquetaTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        etiquetaTitulo.setForeground(new Color(244, 220, 160));
        etiquetaTitulo.setBounds(0, y, ANCHO_BLOQUE, 26);
        etiquetas.add(etiquetaTitulo);
        y += 30;

        String[] lineas = cuerpo.split("\n");
        for (String linea : lineas) {
            JLabel etiquetaLinea = new JLabel(linea, SwingConstants.LEFT);
            etiquetaLinea.setFont(new Font("SansSerif", Font.PLAIN, 16));
            etiquetaLinea.setForeground(new Color(225, 218, 205));
            etiquetaLinea.setBounds(0, y, ANCHO_BLOQUE, 22);
            etiquetas.add(etiquetaLinea);
            y += 24;
        }
        return y + 22;
    }
}
