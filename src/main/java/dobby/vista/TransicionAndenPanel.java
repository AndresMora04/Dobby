package dobby.vista;

import dobby.util.TemaManager;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TransicionAndenPanel extends JPanel {
    private static final Color CIELO_ARRIBA = new Color(8, 8, 26);
    private static final Color CIELO_ABAJO = new Color(46, 28, 74);
    private static final Color SILUETA = new Color(10, 8, 18);
    private static final Color LADRILLO_CLARO = new Color(96, 52, 44);
    private static final Color LADRILLO_OSCURO = new Color(70, 36, 30);
    private static final Color MEZCLA = new Color(40, 24, 30);
    private static final long DURACION_MS = 7000;
    private static final double FRACCION_LLEGADA = 0.62;
    private static final Font FUENTE_FRASE = new Font("Serif", Font.ITALIC, 22);
    private static final Font FUENTE_AUTOR = new Font("Serif", Font.ITALIC, 16);
    private static final Color COLOR_FRASE = new Color(232, 214, 178);

    private static class Chispa {
        double x;
        double y;
        double vx;
        double vy;
        float alpha;
    }

    private final List<Chispa> chispas = new ArrayList<>();
    private final Random azar = new Random();
    private final BufferedImage insignia = cargarInsignia();

    private double progreso;
    private long inicio;
    private Timer temporizador;
    private Runnable alTerminar;
    private boolean explosionHecha;
    private FrasesInspiradoras.Frase fraseActual;

    public TransicionAndenPanel() {
        setOpaque(true);
    }

    private static BufferedImage cargarInsignia() {
        try {
            return ImageIO.read(TransicionAndenPanel.class.getResource("/assets/imagenes/Estacion.png"));
        } catch (IOException e) {
            return null;
        }
    }

    public void iniciar(Runnable alTerminar) {
        this.alTerminar = alTerminar;
        this.progreso = 0;
        this.chispas.clear();
        this.explosionHecha = false;
        this.fraseActual = FrasesInspiradoras.FRASES[azar.nextInt(FrasesInspiradoras.FRASES.length)];
        this.inicio = System.currentTimeMillis();

        if (temporizador != null) {
            temporizador.stop();
        }
        temporizador = new Timer(16, e -> {
            long transcurrido = System.currentTimeMillis() - inicio;
            progreso = Math.min(1.0, transcurrido / (double) DURACION_MS);
            actualizarChispas();
            if (progreso >= FRACCION_LLEGADA && !explosionHecha) {
                generarExplosion();
                explosionHecha = true;
            }
            repaint();
            if (progreso >= 1.0) {
                temporizador.stop();
                if (this.alTerminar != null) {
                    this.alTerminar.run();
                }
            }
        });
        temporizador.start();
    }

    private double posicionParedX() {
        return Math.max(getWidth(), 1) * 0.68;
    }

    private double posicionCarretillaX() {
        double paredX = posicionParedX();
        double inicioX = -140;
        double t = Math.min(1.0, progreso / FRACCION_LLEGADA);
        double suavizado = t * t * (3 - 2 * t);
        return inicioX + (paredX - inicioX) * suavizado;
    }

    private void generarExplosion() {
        double paredX = posicionParedX();
        int alto = getHeight();
        double y = alto * 0.72 - 45;
        for (int i = 0; i < 40; i++) {
            Chispa c = new Chispa();
            c.x = paredX;
            c.y = y;
            double angulo = azar.nextDouble() * Math.PI * 2;
            double velocidad = 1.2 + azar.nextDouble() * 3.2;
            c.vx = Math.cos(angulo) * velocidad;
            c.vy = Math.sin(angulo) * velocidad;
            c.alpha = 1f;
            chispas.add(c);
        }
    }

    private void actualizarChispas() {
        for (Chispa c : chispas) {
            c.x += c.vx;
            c.y += c.vy;
            c.vy += 0.03;
            c.alpha = Math.max(0f, c.alpha - 0.015f);
        }
        chispas.removeIf(c -> c.alpha <= 0f);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int ancho = getWidth();
        int alto = getHeight();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setPaint(new GradientPaint(0, 0, CIELO_ARRIBA, 0, alto, CIELO_ABAJO));
        g2.fillRect(0, 0, ancho, alto);

        int baseY = (int) (alto * 0.72);
        double paredX = posicionParedX();

        dibujarPared(g2, paredX, baseY, alto);

        double xCarretilla = posicionCarretillaX();
        float opacidadCarreta = 1f;
        if (progreso > FRACCION_LLEGADA) {
            double t = (progreso - FRACCION_LLEGADA) / (1.0 - FRACCION_LLEGADA);
            opacidadCarreta = (float) Math.max(0, 1 - t * 2.2);
        }
        if (opacidadCarreta > 0.01f) {
            dibujarCarretilla(g2, xCarretilla, baseY, opacidadCarreta);
        }

        Color acento = TemaManager.getInstancia().getColorAcento();
        for (Chispa c : chispas) {
            g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), (int) (c.alpha * 220)));
            double r = 2 + 2 * c.alpha;
            g2.fill(new Ellipse2D.Double(c.x - r, c.y - r, r * 2, r * 2));
        }

        dibujarFrase(g2, ancho, alto, baseY);

        g2.dispose();
    }

    private void dibujarPared(Graphics2D g2Base, double paredX, int baseY, int alto) {
        int anchoPared = 150;
        int x0 = (int) (paredX - anchoPared / 2.0);
        int y0 = (int) (alto * 0.2);
        int altoPared = baseY - y0 + 6;

        Graphics2D g2 = (Graphics2D) g2Base.create();
        g2.clipRect(x0, y0, anchoPared, altoPared);

        g2.setColor(MEZCLA);
        g2.fillRect(x0, y0, anchoPared, altoPared);

        int altoLadrillo = 18;
        int anchoLadrillo = 30;
        boolean colorAlterno = false;
        for (int y = y0; y < y0 + altoPared; y += altoLadrillo) {
            int desplazamiento = colorAlterno ? anchoLadrillo / 2 : 0;
            for (int x = x0 - anchoLadrillo; x < x0 + anchoPared; x += anchoLadrillo) {
                g2.setColor(((x / anchoLadrillo) % 2 == 0) ? LADRILLO_CLARO : LADRILLO_OSCURO);
                g2.fillRect(x + desplazamiento + 2, y + 2, anchoLadrillo - 3, altoLadrillo - 3);
            }
            colorAlterno = !colorAlterno;
        }

        if (insignia != null) {
            int diametro = anchoPared - 14;
            int xIns = (int) (paredX - diametro / 2.0);
            int yIns = y0 + 16;
            g2.drawImage(insignia, xIns, yIns, diametro, diametro, null);
        }

        g2.dispose();
    }

    private void dibujarCarretilla(Graphics2D g2Base, double x, int baseY, float opacidad) {
        Graphics2D g2 = (Graphics2D) g2Base.create();
        Composite anterior = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacidad));

        g2.setColor(SILUETA);
        g2.fill(new Ellipse2D.Double(x, baseY, 24, 24));
        g2.fill(new Ellipse2D.Double(x + 69, baseY, 24, 24));
        g2.fill(new RoundRectangle2D.Double(x - 6, baseY - 21, 108, 24, 9, 9));

        g2.fill(new RoundRectangle2D.Double(x + 6, baseY - 87, 60, 69, 9, 9));
        g2.fill(new RoundRectangle2D.Double(x + 15, baseY - 114, 39, 30, 6, 6));
        g2.setColor(SILUETA.brighter());
        g2.fillRect((int) x + 21, baseY - 108, 5, 18);
        g2.fillRect((int) x + 44, baseY - 108, 5, 18);

        g2.setComposite(anterior);
        g2.dispose();
    }

    private float opacidadFrase() {
        double finAparicion = FRACCION_LLEGADA * 0.10;
        double inicioDesvanecido = FRACCION_LLEGADA * 0.85;
        if (progreso >= FRACCION_LLEGADA) {
            return 0f;
        }
        if (progreso < finAparicion) {
            return (float) (progreso / finAparicion);
        }
        if (progreso > inicioDesvanecido) {
            return (float) ((FRACCION_LLEGADA - progreso) / (FRACCION_LLEGADA - inicioDesvanecido));
        }
        return 1f;
    }

    private void dibujarFrase(Graphics2D g2Base, int ancho, int alto, int baseY) {
        float opacidad = opacidadFrase();
        if (opacidad <= 0.01f || fraseActual == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g2Base.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacidad));

        FontMetrics fm = g2.getFontMetrics(FUENTE_FRASE);
        FontMetrics fmAutor = g2.getFontMetrics(FUENTE_AUTOR);
        int anchoMaximo = (int) (ancho * 0.62);
        List<String> lineas = partirEnLineas(fraseActual.texto(), fm, anchoMaximo);
        String autor = fraseActual.autor();

        int altoLinea = fm.getHeight();
        int altoTexto = lineas.size() * altoLinea;
        int altoTotal = altoTexto + (autor != null ? 6 + fmAutor.getHeight() : 0);
        int zonaY0 = baseY + 44;
        int zonaAltura = Math.max(altoTotal, alto - zonaY0 - 18);
        int yTop = zonaY0 + Math.max(0, (zonaAltura - altoTotal) / 2);
        int yInicial = yTop + fm.getAscent();

        g2.setFont(FUENTE_FRASE);
        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i);
            int xTexto = (ancho - fm.stringWidth(linea)) / 2;
            int yTexto = yInicial + i * altoLinea;
            g2.setColor(SILUETA);
            g2.drawString(linea, xTexto + 1, yTexto + 1);
            g2.setColor(COLOR_FRASE);
            g2.drawString(linea, xTexto, yTexto);
        }

        if (autor != null) {
            int yAutor = yTop + altoTexto + 6 + fmAutor.getAscent();
            int xAutor = (ancho - fmAutor.stringWidth(autor)) / 2;
            g2.setFont(FUENTE_AUTOR);
            g2.setColor(SILUETA);
            g2.drawString(autor, xAutor + 1, yAutor + 1);
            g2.setColor(COLOR_FRASE);
            g2.drawString(autor, xAutor, yAutor);
        }

        g2.dispose();
    }

    private List<String> partirEnLineas(String texto, FontMetrics fm, int anchoMaximo) {
        List<String> lineas = new ArrayList<>();
        String[] palabras = texto.split(" ");
        StringBuilder actual = new StringBuilder();
        for (String palabra : palabras) {
            String prueba = actual.isEmpty() ? palabra : actual + " " + palabra;
            if (fm.stringWidth(prueba) > anchoMaximo && !actual.isEmpty()) {
                lineas.add(actual.toString());
                actual = new StringBuilder(palabra);
            } else {
                actual = new StringBuilder(prueba);
            }
        }
        if (!actual.isEmpty()) {
            lineas.add(actual.toString());
        }
        return lineas;
    }
}
