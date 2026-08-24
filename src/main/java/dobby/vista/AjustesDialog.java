package dobby.vista;

import dobby.util.TemaManager;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Cursor;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;

public class AjustesDialog extends ModalDialog {

    private static final int ANCHO_CONTENIDO = 752;

    private static final int CAJA = 140;
    private static final int RELLENO = 12;
    private static final int ALTO_NOMBRE = 30;
    private static final int ANCHO_BOTON = CAJA + RELLENO * 2;
    private static final int ALTO_BOTON = CAJA + RELLENO * 2 + ALTO_NOMBRE;

    public AjustesDialog(Window propietario) {
        super(propietario, "Ajustes", 800, 480, construirContenido());
    }

    private static JPanel construirContenido() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        JLabel etiqueta = new JLabel("Elegí tu casa", SwingConstants.CENTER);
        etiqueta.setFont(new Font("SansSerif", Font.PLAIN, 16));
        etiqueta.setForeground(new Color(230, 220, 200));
        etiqueta.setBounds(0, 30, ANCHO_CONTENIDO, 26);
        panel.add(etiqueta);

        TemaManager.Casa[] casas = {
            TemaManager.Casa.GRYFFINDOR,
            TemaManager.Casa.SLYTHERIN,
            TemaManager.Casa.RAVENCLAW,
            TemaManager.Casa.HUFFLEPUFF
        };

        int espacio = 20;
        int anchoTotal = casas.length * ANCHO_BOTON + (casas.length - 1) * espacio;
        int x = (ANCHO_CONTENIDO - anchoTotal) / 2;
        int y = 95;

        for (TemaManager.Casa casa : casas) {
            BotonCasa boton = new BotonCasa(casa);
            boton.setBounds(x, y, ANCHO_BOTON, ALTO_BOTON);
            x += ANCHO_BOTON + espacio;
            panel.add(boton);
        }

        return panel;
    }

    private static class BotonCasa extends JButton {
        private final TemaManager.Casa casa;
        private final ImageIcon escudo;
        private boolean sobre = false;

        BotonCasa(TemaManager.Casa casa) {
            this.casa = casa;
            this.escudo = cargarImagen(casa.getArchivoEscudo());
            setToolTipText(casa.getEtiqueta());
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addActionListener(e -> {
                TemaManager.getInstancia().setCasaActual(casa);
                Window ventana = javax.swing.SwingUtilities.getWindowAncestor(this);
                if (ventana != null) {
                    ventana.dispose();
                }
            });
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    sobre = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    sobre = false;
                    repaint();
                }
            });
        }

        private static ImageIcon cargarImagen(String ruta) {
            try (InputStream in = BotonCasa.class.getResourceAsStream(ruta)) {
                if (in == null) {
                    return null;
                }
                return new ImageIcon(in.readAllBytes());
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            boolean seleccionada = TemaManager.getInstancia().getCasaActual() == casa;
            Color color = casa.getColor();
            int caja = CAJA;
            int relleno = RELLENO;

            if (seleccionada) {
                for (int i = 5; i >= 1; i--) {
                    int expansion = i * 3;
                    int alpha = Math.max(10, 46 - i * 7);
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
                    g2.fillRoundRect(relleno - expansion, relleno - expansion, caja + expansion * 2, caja + expansion * 2, 22, 22);
                }
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 70));
                g2.fillRoundRect(relleno, relleno, caja, caja, 20, 20);
                g2.setColor(color.brighter());
                g2.setStroke(new BasicStroke(2.6f));
                g2.drawRoundRect(relleno, relleno, caja - 1, caja - 1, 20, 20);
            } else if (sobre) {
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 45));
                g2.fillRoundRect(relleno - 4, relleno - 4, caja + 8, caja + 8, 20, 20);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 150));
                g2.setStroke(new BasicStroke(1.6f));
                g2.drawRoundRect(relleno, relleno, caja - 1, caja - 1, 20, 20);
            }

            if (escudo != null) {
                int anchoImg = escudo.getIconWidth();
                int altoImg = escudo.getIconHeight();
                double factor = seleccionada ? 1.0 : (sobre ? 0.97 : 0.9);
                double escala = Math.min((double) (caja - 16) / anchoImg, (double) (caja - 16) / altoImg) * factor;
                int dw = (int) (anchoImg * escala);
                int dh = (int) (altoImg * escala);
                int dx = relleno + (caja - dw) / 2;
                int dy = relleno + (caja - dh) / 2 - (sobre || seleccionada ? 3 : 0);
                g2.drawImage(escudo.getImage(), dx, dy, dw, dh, this);
            }

            g2.setColor(seleccionada ? Color.WHITE : (sobre ? new Color(238, 232, 220) : new Color(180, 172, 158)));
            g2.setFont(new Font("SansSerif", seleccionada ? Font.BOLD : Font.PLAIN, 14));
            FontMetrics fm = g2.getFontMetrics();
            String nombre = casa.getEtiqueta();
            int yNombre = relleno + caja + 22;
            g2.drawString(nombre, (getWidth() - fm.stringWidth(nombre)) / 2, yNombre);

            g2.dispose();
        }
    }
}
