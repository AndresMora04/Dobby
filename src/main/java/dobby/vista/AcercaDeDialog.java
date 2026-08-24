package dobby.vista;

import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Window;

public class AcercaDeDialog extends ModalDialog {

    private static final int ANCHO_CONTENIDO = 752;
    private static final int ALTO_CONTENIDO = 392;
    private static final int ANCHO_BLOQUE = 560;

    public AcercaDeDialog(Window propietario) {
        super(propietario, "Acerca de Dobby", 800, 480, construirContenido());
    }

    private static JPanel construirContenido() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        String[] lineas = {
            "Dobby es un lenguaje de programación educativo",
            "con temática de Harry Potter.",
            "",
            "Su objetivo es bajar la barrera de entrada a la",
            "programación usando una sintaxis memorable, y",
            "ser accesible para personas con discapacidad",
            "visual mediante texto-a-voz y navegación por",
            "teclado.",
            "",
            "Extensión de archivo: .dobby",
            "Proyecto de Paradigmas de Programación, UNA."
        };

        int alturaLinea = 24;
        int altoTotal = lineas.length * alturaLinea;
        int offsetX = (ANCHO_CONTENIDO - ANCHO_BLOQUE) / 2;
        int offsetY = Math.max(0, (ALTO_CONTENIDO - altoTotal) / 2);

        int y = offsetY;
        for (String linea : lineas) {
            JLabel etiqueta = new JLabel(linea);
            etiqueta.setFont(new Font("SansSerif", Font.PLAIN, 16));
            etiqueta.setForeground(new Color(225, 218, 205));
            etiqueta.setBounds(offsetX, y, ANCHO_BLOQUE, 22);
            panel.add(etiqueta);
            y += alturaLinea;
        }

        return panel;
    }
}
