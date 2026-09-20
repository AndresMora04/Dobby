package dobby.vista;

import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;

public final class DialogoOpciones {
    private static final int ANCHO_TARJETA = 600;
    private static final int ALTO_LINEA = 26;
    private static final int SEPARACION_BOTONES = 14;

    private DialogoOpciones() {
    }

    public static int elegir(Window propietario, String titulo, String[] mensaje, String... opciones) {
        int[] eleccion = {-1};
        ModalDialog[] dialogo = new ModalDialog[1];

        int altoContenido = mensaje.length * ALTO_LINEA + 36 + 40;
        JPanel contenido = construirContenido(mensaje, opciones, indice -> {
            eleccion[0] = indice;
            dialogo[0].dispose();
        }, ANCHO_TARJETA - 48);

        dialogo[0] = new ModalDialog(propietario, titulo, ANCHO_TARJETA, altoContenido + 88, contenido);
        dialogo[0].setVisible(true);
        return eleccion[0];
    }

    private static JPanel construirContenido(String[] mensaje, String[] opciones,
                                             java.util.function.IntConsumer alElegir, int anchoContenido) {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        int y = 4;
        for (String linea : mensaje) {
            JLabel etiqueta = new JLabel(linea, JLabel.CENTER);
            etiqueta.setFont(new Font("SansSerif", Font.PLAIN, 16));
            etiqueta.setForeground(new Color(225, 218, 205));
            etiqueta.setBounds(0, y, anchoContenido, ALTO_LINEA - 4);
            panel.add(etiqueta);
            y += ALTO_LINEA;
        }

        BotonBarra[] botones = new BotonBarra[opciones.length];
        int anchoTotal = SEPARACION_BOTONES * (opciones.length - 1);
        for (int i = 0; i < opciones.length; i++) {
            final int indice = i;
            botones[i] = new BotonBarra(opciones[i], BotonBarra.Icono.NINGUNO);
            botones[i].addActionListener(e -> alElegir.accept(indice));
            anchoTotal += botones[i].getPreferredSize().width;
        }

        int x = (anchoContenido - anchoTotal) / 2;
        int yBotones = y + 24;
        for (BotonBarra boton : botones) {
            Dimension tamano = boton.getPreferredSize();
            boton.setBounds(x, yBotones, tamano.width, tamano.height);
            panel.add(boton);
            x += tamano.width + SEPARACION_BOTONES;
        }
        return panel;
    }
}
