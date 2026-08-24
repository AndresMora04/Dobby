package dobby.vista;

import dobby.util.TemaManager;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.Image;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.SwingUtilities;

public class BienvenidaPanel extends JPanel {

    private record Estrella(double x, double y, double radio, double fase) {
    }

    private record Edificio(int x, int ancho, int alto, boolean torreon) {
    }

    private static class Chispa {
        double x;
        double y;
        double velocidadY;
        double fase;
        double desplazamientoX;
    }

    private static final Color CIELO_ARRIBA = new Color(8, 8, 26);
    private static final Color CIELO_ABAJO = new Color(46, 28, 74);
    private static final Color SILUETA_CASTILLO = new Color(18, 14, 34);

    private static Color acento() {
        return TemaManager.getInstancia().getColorAcento();
    }

    private static Color acentoClaro() {
        return TemaManager.getInstancia().getColorAcentoClaro();
    }

    private final List<Estrella> estrellas = new ArrayList<>();
    private final List<Edificio> edificios = new ArrayList<>();
    private final List<Chispa> chispas = new ArrayList<>();
    private final Random azar = new Random();

    private ImageIcon mascota;
    private ImageIcon iconoVarita;
    private Font fuenteTitulo;
    private Font fuenteNombre;

    private int anchoGenerado = -1;
    private int altoGenerado = -1;
    private int tick = 0;

    private double snitchX = -100;
    private double snitchY = -100;
    private double snitchObjetivoX = -1;
    private double snitchObjetivoY = -1;
    private long proximoCambioSnitch = 0;

    private BotonPrincipal botonEntrar;

    public BienvenidaPanel() {
        setBackground(CIELO_ARRIBA);
        setLayout(null);
        cargarAssets();
        construirBotones();
        aplicarCursorVarita();
        TemaManager.getInstancia().agregarOyente(this::repaint);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                regenerarEscenografia();
                posicionarComponentes();
            }
        });

        Timer temporizador = new Timer(30, e -> {
            tick++;
            actualizarChispas();
            actualizarSnitch();
            repaint();
        });
        temporizador.start();
    }

    public void alPresionarEntrar(Runnable accion) {
        botonEntrar.addActionListener(e -> accion.run());
    }

    private void cargarAssets() {
        mascota = cargarImagen("/assets/imagenes/Dobby.png");
        iconoVarita = cargarImagen("/assets/imagenes/Varita.png");
        fuenteTitulo = cargarFuente("/assets/fuentes/HARRYP__.TTF", Font.PLAIN, 26f);
        fuenteNombre = cargarFuente("/assets/fuentes/HARRYP__.TTF", Font.BOLD, 48f);
    }

    private void aplicarCursorVarita() {
        if (iconoVarita == null) {
            return;
        }
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int tam = 32;
        Image escalada = iconoVarita.getImage().getScaledInstance(tam, tam, Image.SCALE_SMOOTH);
        Cursor cursorVarita = toolkit.createCustomCursor(escalada, new Point(tam - 4, 4), "cursorVarita");
        setCursor(cursorVarita);
    }

    private ImageIcon cargarImagen(String ruta) {
        try (InputStream in = getClass().getResourceAsStream(ruta)) {
            if (in == null) {
                return null;
            }
            return new ImageIcon(in.readAllBytes());
        } catch (IOException e) {
            return null;
        }
    }

    private Font cargarFuente(String ruta, int estilo, float tamano) {
        try (InputStream in = getClass().getResourceAsStream(ruta)) {
            if (in == null) {
                return new Font("Serif", estilo, (int) tamano);
            }
            return Font.createFont(Font.TRUETYPE_FONT, in).deriveFont(estilo, tamano);
        } catch (Exception e) {
            return new Font("Serif", estilo, (int) tamano);
        }
    }

    private void construirBotones() {
        botonEntrar = new BotonPrincipal("Alohomora");
        add(botonEntrar);

        BotonIcono botonAjustes = botonIcono("⚙", "Ajustes");
        botonAjustes.addActionListener(e -> new AjustesDialog(ventanaPropietaria()).setVisible(true));
        add(botonAjustes);

        BotonIcono botonCreditos = botonIcono("✦", "Créditos");
        botonCreditos.addActionListener(e -> new CreditosDialog(ventanaPropietaria()).setVisible(true));
        add(botonCreditos);

        BotonIcono botonAyuda = botonIcono("?", "Ayuda");
        botonAyuda.addActionListener(e -> new AyudaDialog(ventanaPropietaria()).setVisible(true));
        add(botonAyuda);

        BotonIcono botonAcerca = botonIcono("i", "Acerca de");
        botonAcerca.addActionListener(e -> new AcercaDeDialog(ventanaPropietaria()).setVisible(true));
        add(botonAcerca);
    }

    private Window ventanaPropietaria() {
        return SwingUtilities.getWindowAncestor(this);
    }

    private BotonIcono botonIcono(String glifo, String descripcion) {
        BotonIcono boton = new BotonIcono(glifo);
        boton.setToolTipText(descripcion);
        return boton;
    }

    private void regenerarEscenografia() {
        int ancho = getWidth();
        int alto = getHeight();
        if (ancho <= 0 || alto <= 0 || (ancho == anchoGenerado && alto == altoGenerado)) {
            return;
        }
        anchoGenerado = ancho;
        altoGenerado = alto;

        Random semilla = new Random(42);

        estrellas.clear();
        int cantidadEstrellas = Math.max(60, ancho * alto / 9000);
        for (int i = 0; i < cantidadEstrellas; i++) {
            double x = semilla.nextDouble() * ancho;
            double y = semilla.nextDouble() * alto * 0.75;
            double radio = 0.6 + semilla.nextDouble() * 1.6;
            double fase = semilla.nextDouble() * Math.PI * 2;
            estrellas.add(new Estrella(x, y, radio, fase));
        }

        edificios.clear();
        int x = -20;
        while (x < ancho + 20) {
            int anchoEdificio = 30 + semilla.nextInt(50);
            boolean torreon = semilla.nextDouble() < 0.3;
            int altoEdificio = 40 + semilla.nextInt(90) + (torreon ? 40 : 0);
            edificios.add(new Edificio(x, anchoEdificio, altoEdificio, torreon));
            x += anchoEdificio - semilla.nextInt(10);
        }

        chispas.clear();
        for (int i = 0; i < 26; i++) {
            Chispa c = new Chispa();
            c.x = azar.nextDouble() * ancho;
            c.y = azar.nextDouble() * alto;
            c.velocidadY = 0.15 + azar.nextDouble() * 0.35;
            c.fase = azar.nextDouble() * Math.PI * 2;
            c.desplazamientoX = azar.nextDouble() * 2 - 1;
            chispas.add(c);
        }

        posicionarComponentes();
    }

    private void posicionarComponentes() {
        int ancho = getWidth();
        int alto = getHeight();

        botonEntrar.setSize(botonEntrar.getPreferredSize());
        botonEntrar.setLocation((ancho - botonEntrar.getWidth()) / 2, (int) (alto * 0.79));

        int margen = 24;
        int tam = 48;
        int espacio = 16;
        int cx = ancho - margen - tam;
        int cantidadIconos = 0;
        for (var comp : getComponents()) {
            if (comp instanceof BotonIcono) {
                cantidadIconos++;
            }
        }
        int i = 0;
        for (var comp : getComponents()) {
            if (comp instanceof BotonIcono) {
                comp.setBounds(cx - (cantidadIconos - 1 - i) * (tam + espacio), margen, tam, tam);
                i++;
            }
        }
    }

    private void actualizarChispas() {
        int alto = getHeight();
        for (Chispa c : chispas) {
            c.y -= c.velocidadY;
            c.x += Math.sin((tick + c.fase) * 0.02) * c.desplazamientoX * 0.3;
            if (c.y < -10) {
                c.y = alto + 10;
                c.x = azar.nextDouble() * getWidth();
            }
        }
    }

    private void actualizarSnitch() {
        long ahora = System.currentTimeMillis();
        int ancho = getWidth();
        int alto = getHeight();
        if (ancho <= 0 || alto <= 0) {
            return;
        }
        if (snitchObjetivoX < 0 || ahora >= proximoCambioSnitch) {
            snitchObjetivoX = 40 + azar.nextDouble() * (ancho - 80);
            snitchObjetivoY = 40 + azar.nextDouble() * (alto * 0.55);
            proximoCambioSnitch = ahora + 2200 + azar.nextInt(1800);
            if (snitchX < 0) {
                snitchX = snitchObjetivoX;
                snitchY = snitchObjetivoY;
            }
        }
        snitchX += (snitchObjetivoX - snitchX) * 0.03;
        snitchY += (snitchObjetivoY - snitchY) * 0.03;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int ancho = getWidth();
        int alto = getHeight();
        if (estrellas.isEmpty()) {
            regenerarEscenografia();
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setPaint(new GradientPaint(0, 0, CIELO_ARRIBA, 0, alto, CIELO_ABAJO));
        g2.fillRect(0, 0, ancho, alto);

        dibujarLuna(g2, ancho);
        dibujarEstrellas(g2);
        dibujarCastillo(g2, ancho, alto);
        dibujarMascota(g2, ancho, alto);
        dibujarChispas(g2);
        dibujarSnitch(g2);
        dibujarTextos(g2, ancho, alto);

        g2.dispose();
    }

    private void dibujarLuna(Graphics2D g2, int ancho) {
        float cx = ancho * 0.8f;
        float cy = 90;
        float r = 130;
        RadialGradientPaint brillo = new RadialGradientPaint(
            cx, cy, r,
            new float[]{0f, 0.35f, 1f},
            new Color[]{new Color(255, 250, 230, 180), new Color(255, 250, 230, 60), new Color(255, 250, 230, 0)});
        g2.setPaint(brillo);
        g2.fillOval((int) (cx - r), (int) (cy - r), (int) (r * 2), (int) (r * 2));

        g2.setColor(new Color(250, 246, 222));
        g2.fillOval((int) (cx - 38), (int) (cy - 38), 76, 76);
    }

    private void dibujarEstrellas(Graphics2D g2) {
        for (Estrella e : estrellas) {
            double brillo = 0.4 + 0.6 * (0.5 + 0.5 * Math.sin(tick * 0.04 + e.fase()));
            g2.setColor(new Color(1f, 1f, 1f, (float) brillo));
            double r = e.radio();
            g2.fill(new Ellipse2D.Double(e.x() - r, e.y() - r, r * 2, r * 2));
        }
    }

    private void dibujarCastillo(Graphics2D g2, int ancho, int alto) {
        int base = (int) (alto * 0.9);
        g2.setColor(SILUETA_CASTILLO);
        g2.fillRect(0, base, ancho, alto - base);

        for (Edificio e : edificios) {
            int y = base - e.alto();
            g2.fillRect(e.x(), y, e.ancho(), e.alto() + (alto - base));
            if (e.torreon()) {
                int[] xs = {e.x() - 4, e.x() + e.ancho() / 2, e.x() + e.ancho() + 4};
                int[] ys = {y, y - 26, y};
                g2.fillPolygon(xs, ys, 3);
            }
        }
    }

    private void dibujarMascota(Graphics2D g2, int ancho, int alto) {
        if (mascota == null) {
            return;
        }
        int altoDeseado = (int) (alto * 0.34);
        int anchoImg = mascota.getIconWidth();
        int altoImg = mascota.getIconHeight();
        if (anchoImg <= 0 || altoImg <= 0) {
            return;
        }
        double escala = (double) altoDeseado / altoImg;
        int w = (int) (anchoImg * escala);
        int h = altoDeseado;
        int x = (ancho - w) / 2;
        int y = (int) (alto * 0.16);

        float pulso = 0.5f + 0.5f * (float) Math.sin(tick * 0.03);
        RadialGradientPaint aura = new RadialGradientPaint(
            x + w / 2f, y + h / 2f, Math.max(w, h) * 0.65f,
            new float[]{0f, 1f},
            new Color[]{new Color(acento().getRed(), acento().getGreen(), acento().getBlue(), (int) (70 + 40 * pulso)),
                new Color(acento().getRed(), acento().getGreen(), acento().getBlue(), 0)});
        g2.setPaint(aura);
        g2.fillOval(x - w / 3, y - h / 3, (int) (w * 1.66), (int) (h * 1.66));

        g2.drawImage(mascota.getImage(), x, y, w, h, this);
    }

    private void dibujarChispas(Graphics2D g2) {
        for (Chispa c : chispas) {
            double brillo = 0.3 + 0.7 * (0.5 + 0.5 * Math.sin(tick * 0.05 + c.fase));
            g2.setColor(new Color(acento().getRed(), acento().getGreen(), acento().getBlue(), (int) (brillo * 220)));
            double r = 1.6 + brillo * 1.6;
            g2.fill(new Ellipse2D.Double(c.x - r, c.y - r, r * 2, r * 2));
        }
    }

    private void dibujarSnitch(Graphics2D g2) {
        if (snitchX < 0) {
            return;
        }
        double r = 14;
        g2.setColor(new Color(255, 255, 255, 160));
        g2.fill(new Ellipse2D.Double(snitchX - r * 2.1, snitchY - r * 0.4, r * 1.8, r * 1.1));
        g2.fill(new Ellipse2D.Double(snitchX + r * 0.3, snitchY - r * 0.4, r * 1.8, r * 1.1));

        g2.setColor(acentoClaro());
        g2.fill(new Ellipse2D.Double(snitchX - r, snitchY - r, r * 2, r * 2));
        g2.setColor(acento().darker());
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new Ellipse2D.Double(snitchX - r, snitchY - r, r * 2, r * 2));
    }

    private void dibujarTextos(Graphics2D g2, int ancho, int alto) {
        int y = (int) (alto * 0.58);

        g2.setFont(fuenteTitulo);
        FontMetrics fmTitulo = g2.getFontMetrics();
        String tagline = "Un lenguaje libre, como Dobby.";
        int xTagline = (ancho - fmTitulo.stringWidth(tagline)) / 2;
        g2.setColor(new Color(0, 0, 0, 110));
        g2.drawString(tagline, xTagline + 2, y + 2);
        g2.setColor(acentoClaro());
        g2.drawString(tagline, xTagline, y);

        y += fmTitulo.getHeight() + 26;

        g2.setFont(fuenteNombre);
        FontMetrics fmNombre = g2.getFontMetrics();
        String nombre = "Dobby";
        int xNombre = (ancho - fmNombre.stringWidth(nombre)) / 2;
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(nombre, xNombre + 3, y + 3);
        g2.setColor(acentoClaro().brighter());
        g2.drawString(nombre, xNombre, y);
    }

    private static class BotonPrincipal extends JButton {
        private boolean sobre = false;

        BotonPrincipal(String texto) {
            super(texto);
            setFont(ModalDialog.cargarFuenteTitulo(19f));
            setForeground(acentoClaro());
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setMargin(new java.awt.Insets(0, 0, 0, 0));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setHorizontalAlignment(SwingConstants.CENTER);
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
        public java.awt.Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            int ancho = fm.stringWidth(getText()) + 90;
            return new java.awt.Dimension(Math.max(ancho, 260), 52);
        }

        @Override
        protected void paintComponent(Graphics g) {
            setForeground(acentoClaro());
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            Color relleno = sobre ? new Color(70, 50, 30, 235) : new Color(40, 28, 20, 210);
            g2.setColor(relleno);
            g2.fillRoundRect(0, 0, w - 1, h - 1, h, h);

            g2.setColor(acento());
            g2.setStroke(new BasicStroke(1.6f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class BotonIcono extends JButton {
        private boolean sobre = false;

        BotonIcono(String glifo) {
            super(glifo);
            setFont(new Font("SansSerif", Font.PLAIN, 22));
            setForeground(acentoClaro());
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
            setForeground(acentoClaro());
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int d = Math.min(getWidth(), getHeight());
            g2.setColor(sobre ? new Color(80, 60, 30, 200) : new Color(40, 32, 24, 150));
            g2.fillOval(0, 0, d - 1, d - 1);
            g2.setColor(acento().darker());
            g2.drawOval(0, 0, d - 1, d - 1);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
