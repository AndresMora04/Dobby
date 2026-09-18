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
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class EditorPanel extends JPanel {
    private static final Color FONDO = new Color(20, 16, 34);
    private static final Color TEXTO_NORMAL = new Color(224, 220, 210);
    private static final Color COLOR_TEXTO_LITERAL = new Color(150, 200, 140);
    private static final Color COLOR_NUMERO = new Color(190, 150, 220);

    private final JTextPane areaTexto;
    private final Lexer lexer = new Lexer();
    private boolean actualizandoEstilo;

    public EditorPanel() {
        setLayout(new BorderLayout());
        areaTexto = new JTextPane();
        areaTexto.setFont(new Font("Consolas", Font.PLAIN, 16));
        areaTexto.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        areaTexto.setBackground(FONDO);
        areaTexto.setForeground(TEXTO_NORMAL);
        areaTexto.setCaretColor(Color.WHITE);
        aplicarCursorVarita();

        areaTexto.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                resaltar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                resaltar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setBorder(BorderFactory.createEmptyBorder());
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
    }

    private SimpleAttributeSet estiloParaToken(TipoToken tipo) {
        SimpleAttributeSet estilo = new SimpleAttributeSet();
        switch (tipo) {
            case PALABRA_RESERVADA -> {
                StyleConstants.setForeground(estilo, TemaManager.getInstancia().getColorAcentoClaro());
                StyleConstants.setBold(estilo, true);
            }
            case TEXTO -> StyleConstants.setForeground(estilo, COLOR_TEXTO_LITERAL);
            case NUMERO -> StyleConstants.setForeground(estilo, COLOR_NUMERO);
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
        areaTexto.setText(texto);
    }

    public void limpiar() {
        areaTexto.setText("");
    }
}
