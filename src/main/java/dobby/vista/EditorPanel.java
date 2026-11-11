package dobby.vista;

import dobby.motor.lexer.Lexer;
import dobby.motor.lexer.TipoToken;
import dobby.motor.lexer.Token;
import dobby.util.LectorVoz;
import dobby.util.TemaManager;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
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
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditorPanel extends JPanel {
    private static final Color FONDO = new Color(20, 16, 34);
    private static final Color FONDO_NUMEROS = new Color(16, 12, 28);
    private static final Color BORDE_NUMEROS = new Color(48, 40, 68);
    private static final Color COLOR_NUMERO = new Color(112, 102, 132);
    private static final Color COLOR_LINEA_ACTUAL = new Color(255, 255, 255, 16);
    private static final Color COLOR_LINEA_ERROR = new Color(200, 40, 40, 80);
    private static final Color COLOR_NUMERO_ERROR = new Color(235, 90, 90);
    private static final Color TEXTO_NORMAL = new Color(224, 220, 210);
    private static final Color COLOR_TEXTO_LITERAL = new Color(150, 200, 140);
    private static final Color COLOR_NUMERO_LITERAL = new Color(190, 150, 220);

    private final JTextPane areaTexto;
    private final PanelNumerosLinea panelNumeros;
    private final Lexer lexer = new Lexer();
    private final UndoManager deshacerManager = new UndoManager();
    private final Highlighter.HighlightPainter pintorLineaActual = new ResaltadoLinea(COLOR_LINEA_ACTUAL);
    private final Highlighter.HighlightPainter pintorLineaError = new ResaltadoLinea(COLOR_LINEA_ERROR);
    private boolean actualizandoEstilo;
    private boolean cargandoTexto;
    private Runnable alCambiarTexto;
    private Object marcaLineaActual;
    private Object marcaError;
    private int lineaConError = -1;

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
                    Color color = COLOR_NUMERO;
                    if (linea == lineaConError) {
                        color = COLOR_NUMERO_ERROR;
                    } else if (linea == lineaActual) {
                        color = TemaManager.getInstancia().getColorAcentoClaro();
                    }
                    g2.setColor(color);
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
        configurarIndentacion();
        areaTexto.getDocument().addUndoableEditListener(evento -> {
            if (evento.getEdit() instanceof DocumentEvent documentoEvento
                && documentoEvento.getType() == DocumentEvent.EventType.CHANGE) {
                return;
            }
            deshacerManager.undoableEditHappened(evento);
        });

        areaTexto.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                resaltar();
                notificarCambio();
                anunciarLineaPorEnter(e);
                limpiarMarcaError();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                resaltar();
                notificarCambio();
                limpiarMarcaError();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        areaTexto.addCaretListener(e -> actualizarLineaActual());
        areaTexto.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                anunciarLineaPorClic();
            }
        });

        panelNumeros = new PanelNumerosLinea();

        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(FONDO);
        scroll.setRowHeaderView(panelNumeros);
        add(scroll, BorderLayout.CENTER);

        TemaManager.getInstancia().agregarOyente(this::resaltar);
    }

    private static final int ESPACIOS_POR_NIVEL = 4;

    private void configurarIndentacion() {
        areaTexto.getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "indentarTab");
        areaTexto.getActionMap().put("indentarTab", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertarEnCursor(" ".repeat(ESPACIOS_POR_NIVEL));
            }
        });

        areaTexto.getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "nuevaLineaIndentada");
        areaTexto.getActionMap().put("nuevaLineaIndentada", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertarNuevaLineaIndentada();
            }
        });

        areaTexto.getInputMap(WHEN_FOCUSED).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK),
            "formatearDocumento");
        areaTexto.getActionMap().put("formatearDocumento", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                formatear();
            }
        });

        int ctrl = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        areaTexto.getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ctrl), "deshacer");
        areaTexto.getActionMap().put("deshacer", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (deshacerManager.canUndo()) {
                        deshacerManager.undo();
                    }
                } catch (CannotUndoException ex) {
                }
            }
        });

        areaTexto.getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ctrl), "rehacer");
        areaTexto.getInputMap(WHEN_FOCUSED).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_Z, ctrl | InputEvent.SHIFT_DOWN_MASK), "rehacer");
        areaTexto.getActionMap().put("rehacer", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (deshacerManager.canRedo()) {
                        deshacerManager.redo();
                    }
                } catch (CannotRedoException ex) {
                }
            }
        });
    }

    public void formatear() {
        String textoActual = areaTexto.getText();
        if (textoActual.isEmpty()) {
            return;
        }
        int lineaCaret = getLineaActual();
        String formateado = reindentar(textoActual);
        if (formateado.equals(textoActual)) {
            return;
        }
        setTexto(formateado);
        irALinea(lineaCaret);
        notificarCambio();
    }

    private String reindentar(String texto) {
        Map<Integer, int[]> llavesPorLinea = new HashMap<>();
        try {
            for (Token token : lexer.tokenizar(texto)) {
                if (token.getTipo() != TipoToken.SIMBOLO) {
                    continue;
                }
                int[] conteo = llavesPorLinea.computeIfAbsent(token.getLinea(), k -> new int[2]);
                if ("{".equals(token.getValor())) {
                    conteo[0]++;
                } else if ("}".equals(token.getValor())) {
                    conteo[1]++;
                }
            }
        } catch (RuntimeException e) {
            return texto;
        }

        String[] lineas = texto.split("\n", -1);
        StringBuilder resultado = new StringBuilder();
        int nivel = 0;
        for (int i = 0; i < lineas.length; i++) {
            String contenido = lineas[i].strip();
            int[] conteo = llavesPorLinea.getOrDefault(i + 1, new int[2]);
            int aperturas = conteo[0];
            int cierres = conteo[1];

            if (contenido.isEmpty()) {
                resultado.append("");
            } else {
                if (contenido.startsWith("}") && cierres > 0) {
                    nivel = Math.max(0, nivel - 1);
                    cierres--;
                }
                resultado.append(" ".repeat(nivel * ESPACIOS_POR_NIVEL)).append(contenido);
                nivel = Math.max(0, nivel + aperturas - cierres);
            }
            if (i < lineas.length - 1) {
                resultado.append("\n");
            }
        }
        return resultado.toString();
    }

    private void irALinea(int numeroLinea) {
        Element raiz = areaTexto.getDocument().getDefaultRootElement();
        int indice = Math.max(0, Math.min(numeroLinea - 1, raiz.getElementCount() - 1));
        areaTexto.setCaretPosition(raiz.getElement(indice).getStartOffset());
    }

    private void insertarNuevaLineaIndentada() {
        try {
            int posicion = areaTexto.getCaretPosition();
            Element raiz = areaTexto.getDocument().getDefaultRootElement();
            int inicioLinea = raiz.getElement(raiz.getElementIndex(posicion)).getStartOffset();
            String textoLinea = areaTexto.getDocument().getText(inicioLinea, posicion - inicioLinea);
            String sangria = obtenerSangria(textoLinea);
            if (textoLinea.strip().endsWith("{")) {
                sangria = sangria + " ".repeat(ESPACIOS_POR_NIVEL);
            }
            areaTexto.getDocument().insertString(posicion, "\n" + sangria, null);
        } catch (BadLocationException e) {
        }
    }

    private String obtenerSangria(String texto) {
        int i = 0;
        while (i < texto.length() && (texto.charAt(i) == ' ' || texto.charAt(i) == '\t')) {
            i++;
        }
        return texto.substring(0, i);
    }

    private void aplicarCursorVarita() {
        try (InputStream in = getClass().getResourceAsStream("/assets/imagenes/Varita.png")) {
            if (in == null) {
                return;
            }
            ImageIcon icono = new ImageIcon(in.readAllBytes());
            BufferedImage volteada = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = volteada.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(icono.getImage(), 32, 0, -32, 32, null);
            g2.dispose();
            Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(volteada, new Point(4, 4), "cursorVarita");
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

    private void anunciarLineaPorClic() {
        if (!cargandoTexto && LectorVoz.isHabilitado()) {
            LectorVoz.leer("Línea " + getLineaActual() + ".");
        }
    }

    private void anunciarLineaPorEnter(DocumentEvent e) {
        if (cargandoTexto || !LectorVoz.isHabilitado()) {
            return;
        }
        try {
            String insertado = areaTexto.getDocument().getText(e.getOffset(), e.getLength());
            if (!"\n".equals(insertado)) {
                return;
            }
            Element raiz = areaTexto.getDocument().getDefaultRootElement();
            int linea = raiz.getElementIndex(e.getOffset() + e.getLength()) + 1;
            LectorVoz.leer("Línea " + linea + ".");
        } catch (BadLocationException ex) {
        }
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
            deshacerManager.discardAllEdits();
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

    public int getLineaActual() {
        Element raiz = areaTexto.getDocument().getDefaultRootElement();
        return raiz.getElementIndex(areaTexto.getCaretPosition()) + 1;
    }

    public void marcarLineaError(int numeroLinea) {
        limpiarMarcaError();
        Element raiz = areaTexto.getDocument().getDefaultRootElement();
        if (numeroLinea < 1 || numeroLinea > raiz.getElementCount()) {
            return;
        }
        Element elementoLinea = raiz.getElement(numeroLinea - 1);
        int inicio = elementoLinea.getStartOffset();
        int fin = Math.max(inicio, elementoLinea.getEndOffset() - 1);
        try {
            marcaError = areaTexto.getHighlighter().addHighlight(inicio, fin, pintorLineaError);
        } catch (BadLocationException e) {
            return;
        }
        lineaConError = numeroLinea - 1;
        areaTexto.setCaretPosition(inicio);
        panelNumeros.repaint();
    }

    public void limpiarMarcaError() {
        if (marcaError != null) {
            areaTexto.getHighlighter().removeHighlight(marcaError);
            marcaError = null;
        }
        if (lineaConError != -1) {
            lineaConError = -1;
            panelNumeros.repaint();
        }
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
