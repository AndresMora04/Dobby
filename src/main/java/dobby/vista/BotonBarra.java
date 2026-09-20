package dobby.vista;

import dobby.util.TemaManager;

import javax.swing.JButton;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public class BotonBarra extends JButton {

    public enum Icono {
        NUEVO, ABRIR, GUARDAR, COMPILAR, EJECUTAR, NINGUNO
    }

    private final Icono icono;
    private boolean sobre;

    public BotonBarra(String texto, Icono icono) {
        super(texto);
        this.icono = icono;
        setFont(ModalDialog.cargarFuenteTitulo(15f));
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(0, 0, 0, 0));
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
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        int ancho = fm.stringWidth(getText()) + (icono == Icono.NINGUNO ? 40 : 56);
        return new Dimension(ancho, 40);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Color acento = TemaManager.getInstancia().getColorAcento();
        Color acentoClaro = TemaManager.getInstancia().getColorAcentoClaro();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        RoundRectangle2D.Float fondo = new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 1, 14, 14);
        g2.setColor(sobre ? acento : new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 38));
        g2.fill(fondo);
        g2.setColor(acento);
        g2.draw(fondo);

        g2.setColor(sobre ? Color.WHITE : acentoClaro);
        if (icono != Icono.NINGUNO) {
            dibujarIcono(g2, 14, h / 2 - 8, 16);
        }

        g2.setFont(getFont());
        g2.setColor(sobre ? Color.WHITE : new Color(232, 227, 218));
        FontMetrics fm = g2.getFontMetrics();
        int yTexto = (h - fm.getHeight()) / 2 + fm.getAscent();
        int xTexto = icono == Icono.NINGUNO ? (w - fm.stringWidth(getText())) / 2 : 38;
        g2.drawString(getText(), xTexto, yTexto);

        g2.dispose();
    }

    private void dibujarIcono(Graphics2D g2, int x, int y, int t) {
        g2.setStroke(new BasicStroke(1.8f));
        switch (icono) {
            case NUEVO -> {
                g2.drawLine(x + t / 2, y + 1, x + t / 2, y + t - 1);
                g2.drawLine(x + 1, y + t / 2, x + t - 1, y + t / 2);
            }
            case ABRIR -> {
                g2.drawRect(x + 1, y + 5, t - 3, 2);
                Path2D cuerpo = new Path2D.Float();
                cuerpo.moveTo(x, y + 5);
                cuerpo.lineTo(x + 4, y + 5);
                cuerpo.lineTo(x + 6, y + 2);
                cuerpo.lineTo(x + t - 1, y + 2);
                cuerpo.lineTo(x + t - 1, y + 5);
                g2.draw(cuerpo);
                g2.drawRect(x, y + 5, t - 2, t - 6);
            }
            case GUARDAR -> {
                g2.drawRoundRect(x, y, t - 2, t, 3, 3);
                g2.drawRect(x + 3, y, t - 8, 5);
                g2.drawRect(x + 2, y + t - 6, t - 6, 5);
            }
            case COMPILAR -> {
                Path2D marca = new Path2D.Float();
                marca.moveTo(x, y + t / 2);
                marca.lineTo(x + t / 2 - 2, y + t - 2);
                marca.lineTo(x + t, y);
                g2.draw(marca);
            }
            case NINGUNO -> {
            }
            case EJECUTAR -> {
                Path2D triangulo = new Path2D.Float();
                triangulo.moveTo(x + 1, y);
                triangulo.lineTo(x + 1, y + t);
                triangulo.lineTo(x + t - 1, y + t / 2);
                triangulo.closePath();
                g2.fill(triangulo);
            }
        }
    }
}
