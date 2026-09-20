package dobby.vista;

import dobby.motor.lexer.Lexer;
import dobby.motor.lexer.TipoToken;
import dobby.motor.lexer.Token;
import dobby.util.TemaManager;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.Utilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class EditorPanel extends JPanel {
    private static final Color FONDO = new Color(20, 16, 34);
    private static final Color FONDO_NUMEROS = new Color(16, 12, 28);
    private static final Color BORDE_NUMEROS = new Color(48, 40, 68);
    private static final Color COLOR_NUMERO = new Color(112, 102, 132);
    private static final Color COLOR_LINEA_ACTUAL = new Color(255, 255, 255, 16);
    private static final Color TEXTO_NORMAL = new Color(224, 220, 210);
    private static final Color COLOR_TEXTO_LITERAL = new Color(150, 200, 140);
    private static final Color COLOR_NUMERO_LITERAL = new Color(190, 150, 220);

    private final JTextPane areaTexto;
    private final PanelNumerosLinea panelNumeros;
    private final Lexer lexer = new Lexer();
    private final Highlighter.HighlightPainter pintorLineaActual = new ResaltadoLinea(COLOR_LINEA_ACTUAL);
    private boolean actualizandoEstilo;
    private boolean cargandoTexto;
    private Runnable alCambiarTexto;
    private Object marcaLineaActual;

    private class PanelNumerosLinea extends JPanel {
        PanelNumerosLinea() {
            setPreferredSize(new java.awt.Dimension(48, 1));
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(FONDO_NUMEROS);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(BORDE_NUMEROS);
            g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());

            Rectangle clip = g.getClipBounds();
            g2.setFont(areaTexto.getFont());
            FontMetrics fm = g2.getFontMetrics();

            Element raiz = areaTexto.getDocument().getDefaultRootElement();
            int lineaActual = raiz.getElementIndex(areaTexto.getCaretPosition());
            int totalLineas = raiz.getElementCount();

            for (int linea = 0; linea < totalLineas; linea++) {
                try {
                    Rectangle r = areaTexto.modelToView2D(raiz.getElement(linea).getStartOffset()).getBounds();
                    if (r.y + r.height < clip.y) {
                        continue;
                    }
                    if (r.y > clip.y + clip.height) {
                        break;
                    }
                    String numero = String.valueOf(linea + 1);
                    int x = getWidth() - fm.stringWidth(numero) - 10;
                    g2.setColor(linea == lineaActual ? TemaManager.getInstancia().getColorAcentoClaro() : COLOR_NUMERO);
                    g2.drawString(numero, x, r.y + fm.getAscent());
                } catch (BadLocationException e) {
                }
            }
        }
    }

    private static class ResaltadoLinea implements Highlighter.HighlightPainter {
        private final Color color;

        ResaltadoLinea(Color color) {
            this.color = color;
        }

        @Override
        public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
            try {
                Rectangle r = c.modelToView2D(p0).getBounds();
                g.setColor(color);
                g.fillRect(0, r.y, c.getWidth(), r.height);
            } catch (BadLocationException e) {
            }
        }
    }

    public EditorPanel() {
        setLayout(new BorderLayout());
        areaTexto = new JTextPane() {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                Component padre = getParent();
                return padre == null || getUI().getPreferredSize(this).width < padre.getSize().width;
            }
        };
        areaTexto.setFont(new Font("Consolas", Font.PLAIN, 16));
        areaTexto.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        areaTexto.setBackground(FONDO);
        areaTexto.setForeground(TEXTO_NORMAL);
        areaTexto.setCaretColor(Color.WHITE);
        aplicarCursorVarita();

        areaTexto.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                resaltar();
                notificarCambio();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                resaltar();
                notificarCambio();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        areaTexto.addCaretListener(e -> actualizarLineaActual());

        panelNumeros = new PanelNumerosLinea();

        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(FONDO);
        scroll.setRowHeaderView(panelNumeros);
        add(scroll, BorderLayout.CENTER);

        TemaManager.getInstancia().agregarOyente(this::resaltar);
    }

    private void aplicarCursorVarita() {
        try (InputStream in = getClass().getResourceAsStream("/assets/imagenes/Varita.png")) {
            if (in == null) {
                return;
            }
            ImageIcon icono = new ImageIcon(in.readAllBytes());
            Image escalada = icono.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(escalada, new Point(28, 4), "cursorVarita");
            areaTexto.setCursor(cursor);
        } catch (IOException e) {
        }
    }

    private void actualizarLineaActual() {
        try {
            int inicio = Utilities.getRowStart(areaTexto, areaTexto.getCaretPosition());
            int fin = Utilities.getRowEnd(areaTexto, areaTexto.getCaretPosition());
            if (marcaLineaActual != null) {
                areaTexto.getHighlighter().removeHighlight(marcaLineaActual);
            }
            marcaLineaActual = areaTexto.getHighlighter().addHighlight(inicio, fin, pintorLineaActual);
        } catch (BadLocationException e) {
        }
        panelNumeros.repaint();
    }

    private void resaltar() {
        if (actualizandoEstilo) {
            return;
        }
        SwingUtilities.invokeLater(this::aplicarResaltado);
    }

    private void aplicarResaltado() {
        actualizandoEstilo = true;
        try {
            String texto = areaTexto.getText();
            StyledDocument documento = areaTexto.getStyledDocument();

            SimpleAttributeSet normal = new SimpleAttributeSet();
            StyleConstants.setForeground(normal, TEXTO_NORMAL);
            documento.setCharacterAttributes(0, texto.length(), normal, true);

            List<Token> tokens = lexer.tokenizar(texto);
            int posicion = 0;
            for (Token token : tokens) {
                if (token.getTipo() == TipoToken.EOF) {
                    break;
                }
                String valor = token.getValor();
                int inicio = texto.indexOf(valor, posicion);
                if (inicio < 0) {
                    continue;
                }
                SimpleAttributeSet estilo = estiloParaToken(token.getTipo());
                if (estilo != null) {
                    documento.setCharacterAttributes(inicio, valor.length(), estilo, false);
                }
                posicion = inicio + Math.max(valor.length(), 1);
            }
        } catch (RuntimeException e) {
        } finally {
            actualizandoEstilo = false;
        }
        panelNumeros.repaint();
    }

    private SimpleAttributeSet estiloParaToken(TipoToken tipo) {
        SimpleAttributeSet estilo = new SimpleAttributeSet();
        switch (tipo) {
            case PALABRA_RESERVADA -> {
                StyleConstants.setForeground(estilo, TemaManager.getInstancia().getColorAcentoClaro());
                StyleConstants.setBold(estilo, true);
            }
            case TEXTO -> StyleConstants.setForeground(estilo, COLOR_TEXTO_LITERAL);
            case NUMERO -> StyleConstants.setForeground(estilo, COLOR_NUMERO_LITERAL);
            default -> {
                return null;
            }
        }
        return estilo;
    }

    public String getTexto() {
        return areaTexto.getText();
    }

    public void setTexto(String texto) {
        cargandoTexto = true;
        try {
            areaTexto.setText(texto);
            areaTexto.setCaretPosition(0);
        } finally {
            cargandoTexto = false;
        }
    }

    public void limpiar() {
        setTexto("");
    }

    public void alCambiarTexto(Runnable alCambiarTexto) {
        this.alCambiarTexto = alCambiarTexto;
    }

    private void notificarCambio() {
        if (!cargandoTexto && alCambiarTexto != null) {
            alCambiarTexto.run();
        }
    }

    public int getPosicionCursor() {
        return areaTexto.getCaretPosition();
    }

    public void setPosicionCursor(int posicion) {
        areaTexto.setCaretPosition(Math.max(0, Math.min(posicion, areaTexto.getDocument().getLength())));
    }

    public void enfocar() {
        areaTexto.requestFocusInWindow();
    }

    public void insertarEnCursor(String texto) {
        try {
            int posicion = areaTexto.getCaretPosition();
            areaTexto.getDocument().insertString(posicion, texto, null);
            areaTexto.setCaretPosition(posicion + texto.length());
            areaTexto.requestFocusInWindow();
        } catch (BadLocationException e) {
        }
    }
}
