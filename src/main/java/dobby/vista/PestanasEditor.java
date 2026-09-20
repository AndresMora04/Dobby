package dobby.vista;

import dobby.modelo.ArchivoDobby;
import dobby.util.TemaManager;

import javax.swing.JPanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PestanasEditor extends JPanel {
    private static final Color FONDO_BARRA = new Color(18, 14, 30);
    private static final Color FONDO_PESTANA = new Color(14, 11, 24);
    private static final Color FONDO_SOBRE = new Color(28, 22, 46);
    private static final Color FONDO_ACTIVA = new Color(20, 16, 34);
    private static final Color SEPARADOR = new Color(44, 37, 64);
    private static final Color TEXTO_APAGADO = new Color(150, 144, 160);
    private static final Color TEXTO_ACTIVO = new Color(236, 231, 222);
    private static final Font FUENTE = new Font("SansSerif", Font.PLAIN, 13);
    private static final int ALTO = 38;
    private static final int ANCHO_MAXIMO = 210;
    private static final int ANCHO_MINIMO = 84;
    private static final int LADO_CIERRE = 18;

    private final List<ArchivoDobby> archivos = new ArrayList<>();
    private ArchivoDobby activo;
    private int sobre = -1;
    private boolean sobreCierre;
    private Consumer<ArchivoDobby> alSeleccionar;
    private Consumer<ArchivoDobby> alCerrar;

    public PestanasEditor() {
        setOpaque(true);
        setBackground(FONDO_BARRA);
        setPreferredSize(new Dimension(100, ALTO));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setToolTipText("");

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                actualizarSobre(e);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                sobre = -1;
                sobreCierre = false;
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int indice = indiceEn(e.getX());
                if (indice < 0) {
                    return;
                }
                ArchivoDobby archivo = archivos.get(indice);
                boolean botonMedio = e.getButton() == MouseEvent.BUTTON2;
                if (botonMedio || (e.getButton() == MouseEvent.BUTTON1 && enCierre(indice, e.getX(), e.getY()))) {
                    if (alCerrar != null) {
                        alCerrar.accept(archivo);
                    }
                } else if (e.getButton() == MouseEvent.BUTTON1 && alSeleccionar != null) {
                    alSeleccionar.accept(archivo);
                }
            }
        });

        TemaManager.getInstancia().agregarOyente(this::repaint);
    }

    public void alSeleccionar(Consumer<ArchivoDobby> alSeleccionar) {
        this.alSeleccionar = alSeleccionar;
    }

    public void alCerrar(Consumer<ArchivoDobby> alCerrar) {
        this.alCerrar = alCerrar;
    }

    public void refrescar(List<ArchivoDobby> abiertos, ArchivoDobby archivoActivo) {
        archivos.clear();
        archivos.addAll(abiertos);
        activo = archivoActivo;
        sobre = -1;
        repaint();
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        int indice = indiceEn(e.getX());
        if (indice < 0) {
            return null;
        }
        ArchivoDobby archivo = archivos.get(indice);
        return archivo.getRuta() != null ? archivo.getRuta().toString() : "Archivo sin guardar";
    }

    private int anchoPestana() {
        if (archivos.isEmpty()) {
            return ANCHO_MAXIMO;
        }
        return Math.max(ANCHO_MINIMO, Math.min(ANCHO_MAXIMO, getWidth() / archivos.size()));
    }

    private int indiceEn(int x) {
        int indice = x / anchoPestana();
        return x >= 0 && indice < archivos.size() ? indice : -1;
    }

    private Rectangle rectanguloCierre(int indice) {
        int ancho = anchoPestana();
        int x = indice * ancho + ancho - LADO_CIERRE - 8;
        return new Rectangle(x, (ALTO - LADO_CIERRE) / 2, LADO_CIERRE, LADO_CIERRE);
    }

    private boolean enCierre(int indice, int x, int y) {
        return rectanguloCierre(indice).contains(x, y);
    }

    private void actualizarSobre(MouseEvent e) {
        int indice = indiceEn(e.getX());
        boolean cierre = indice >= 0 && enCierre(indice, e.getX(), e.getY());
        if (indice != sobre || cierre != sobreCierre) {
            sobre = indice;
            sobreCierre = cierre;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int ancho = getWidth();
        Color acento = TemaManager.getInstancia().getColorAcento();
        g2.setColor(FONDO_BARRA);
        g2.fillRect(0, 0, ancho, ALTO);
        g2.setColor(SEPARADOR);
        g2.drawLine(0, ALTO - 1, ancho, ALTO - 1);

        int anchoPestana = anchoPestana();
        g2.setFont(FUENTE);
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i < archivos.size(); i++) {
            ArchivoDobby archivo = archivos.get(i);
            boolean seleccionada = archivo == activo;
            int x = i * anchoPestana;

            g2.setColor(seleccionada ? FONDO_ACTIVA : (i == sobre ? FONDO_SOBRE : FONDO_PESTANA));
            g2.fillRect(x, 0, anchoPestana, ALTO - (seleccionada ? 0 : 1));
            if (seleccionada) {
                g2.setColor(acento);
                g2.fillRect(x, 0, anchoPestana, 3);
            }
            g2.setColor(SEPARADOR);
            g2.drawLine(x + anchoPestana - 1, 0, x + anchoPestana - 1, ALTO - 1);

            int xTexto = x + 14;
            if (archivo.isModificado()) {
                g2.setColor(TemaManager.getInstancia().getColorAcentoClaro());
                g2.fillOval(x + 12, ALTO / 2 - 4, 8, 8);
                xTexto = x + 28;
            }

            int anchoDisponible = x + anchoPestana - LADO_CIERRE - 16 - xTexto;
            g2.setColor(seleccionada ? TEXTO_ACTIVO : TEXTO_APAGADO);
            g2.drawString(recortar(archivo.getNombre(), fm, anchoDisponible), xTexto, (ALTO + fm.getAscent() - fm.getDescent()) / 2 + 1);

            dibujarCierre(g2, i, seleccionada);
        }
        g2.dispose();
    }

    private void dibujarCierre(Graphics2D g2, int indice, boolean seleccionada) {
        Rectangle r = rectanguloCierre(indice);
        boolean resaltado = indice == sobre && sobreCierre;
        if (resaltado) {
            g2.setColor(new Color(150, 60, 60, 210));
            g2.fillOval(r.x, r.y, r.width, r.height);
        }
        g2.setColor(resaltado ? Color.WHITE : (seleccionada || indice == sobre ? TEXTO_ACTIVO : TEXTO_APAGADO));
        g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int margen = 5;
        g2.drawLine(r.x + margen, r.y + margen, r.x + r.width - margen, r.y + r.height - margen);
        g2.drawLine(r.x + r.width - margen, r.y + margen, r.x + margen, r.y + r.height - margen);
    }

    private String recortar(String texto, FontMetrics fm, int anchoDisponible) {
        if (fm.stringWidth(texto) <= anchoDisponible) {
            return texto;
        }
        String puntos = "...";
        int fin = texto.length();
        while (fin > 1 && fm.stringWidth(texto.substring(0, fin) + puntos) > anchoDisponible) {
            fin--;
        }
        return texto.substring(0, fin) + puntos;
    }
}
