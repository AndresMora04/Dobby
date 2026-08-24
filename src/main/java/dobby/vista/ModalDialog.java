package dobby.vista;

import dobby.util.TemaManager;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class ModalDialog extends JDialog {

    private static final int MARGEN_RESPLANDOR = 18;
    private static final Pattern MARCAS_DIACRITICAS = Pattern.compile("\\p{M}");

    private final JPanel tarjeta;

    public ModalDialog(Window propietario, String titulo, int anchoTarjeta, int altoTarjeta, JComponent contenido) {
        super(propietario, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setResizable(false);

        boolean traslucidoSoportado = false;
        GraphicsEnvironment entorno = GraphicsEnvironment.getLocalGraphicsEnvironment();
        if (!entorno.isHeadlessInstance()) {
            GraphicsDevice dispositivo = entorno.getDefaultScreenDevice();
            traslucidoSoportado = dispositivo.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSLUCENT);
        }

        if (propietario != null) {
            setBounds(propietario.getBounds());
        } else {
            setBounds(0, 0, 1024, 680);
        }

        int alphaFondo = traslucidoSoportado ? 150 : 235;
        JPanel fondo = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(4, 4, 14, alphaFondo));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        fondo.setOpaque(traslucidoSoportado ? false : true);
        fondo.setBounds(0, 0, getWidth(), getHeight());
        fondo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        });
        setContentPane(fondo);

        if (traslucidoSoportado) {
            setBackground(new Color(0, 0, 0, 0));
        }

        int anchoTotal = anchoTarjeta + MARGEN_RESPLANDOR * 2;
        int altoTotal = altoTarjeta + MARGEN_RESPLANDOR * 2;

        tarjeta = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color acento = TemaManager.getInstancia().getColorAcento();
                int capas = 6;
                for (int i = capas; i >= 1; i--) {
                    int expansion = i * 3;
                    int alpha = Math.max(6, 26 - i * 3);
                    g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), alpha));
                    g2.fillRoundRect(MARGEN_RESPLANDOR - expansion, MARGEN_RESPLANDOR - expansion,
                        anchoTarjeta + expansion * 2, altoTarjeta + expansion * 2, 30, 30);
                }

                GradientPaint gradiente = new GradientPaint(
                    0, MARGEN_RESPLANDOR, new Color(32, 24, 52),
                    0, MARGEN_RESPLANDOR + altoTarjeta, new Color(16, 12, 28));
                g2.setPaint(gradiente);
                g2.fillRoundRect(MARGEN_RESPLANDOR, MARGEN_RESPLANDOR, anchoTarjeta - 1, altoTarjeta - 1, 24, 24);

                g2.setColor(acento);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(MARGEN_RESPLANDOR, MARGEN_RESPLANDOR, anchoTarjeta - 1, altoTarjeta - 1, 24, 24);

                g2.dispose();
            }

            @Override
            protected void processMouseEvent(MouseEvent e) {
                e.consume();
            }
        };
        tarjeta.setOpaque(false);
        int x = (getWidth() - anchoTotal) / 2;
        int y = (getHeight() - altoTotal) / 2;
        tarjeta.setBounds(Math.max(x, 0), Math.max(y, 0), anchoTotal, altoTotal);
        fondo.add(tarjeta);

        JLabel etiquetaTitulo = new JLabel(sinTildes(titulo), SwingConstants.CENTER);
        etiquetaTitulo.setFont(cargarFuenteTitulo(24f));
        etiquetaTitulo.setForeground(new Color(248, 226, 168));
        etiquetaTitulo.setBounds(50 + MARGEN_RESPLANDOR, 16 + MARGEN_RESPLANDOR, anchoTarjeta - 100, 36);
        tarjeta.add(etiquetaTitulo);

        BotonCerrar botonCerrar = new BotonCerrar();
        botonCerrar.setBounds(anchoTarjeta - 50 + MARGEN_RESPLANDOR, 14 + MARGEN_RESPLANDOR, 34, 34);
        botonCerrar.addActionListener(e -> dispose());
        tarjeta.add(botonCerrar);

        contenido.setBounds(24 + MARGEN_RESPLANDOR, 64 + MARGEN_RESPLANDOR, anchoTarjeta - 48, altoTarjeta - 88);
        tarjeta.add(contenido);
    }

    protected static String sinTildes(String texto) {
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return MARCAS_DIACRITICAS.matcher(normalizado).replaceAll("");
    }

    protected static Font cargarFuenteTitulo(float tamano) {
        try (InputStream in = ModalDialog.class.getResourceAsStream("/assets/fuentes/HARRYP__.TTF")) {
            if (in == null) {
                return new Font("SansSerif", Font.BOLD, (int) tamano);
            }
            return Font.createFont(Font.TRUETYPE_FONT, in).deriveFont(Font.BOLD, tamano);
        } catch (Exception e) {
            return new Font("SansSerif", Font.BOLD, (int) tamano);
        }
    }

    private static class BotonCerrar extends javax.swing.JButton {
        private boolean sobre = false;

        BotonCerrar() {
            super("✕");
            setFont(new Font("SansSerif", Font.BOLD, 15));
            setForeground(new Color(230, 210, 180));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setMargin(new java.awt.Insets(0, 0, 0, 0));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
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

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int d = Math.min(getWidth(), getHeight());
            g2.setColor(sobre ? new Color(120, 40, 40, 220) : new Color(50, 30, 30, 180));
            g2.fillOval(0, 0, d - 1, d - 1);
            g2.setColor(TemaManager.getInstancia().getColorAcento());
            g2.drawOval(0, 0, d - 1, d - 1);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
