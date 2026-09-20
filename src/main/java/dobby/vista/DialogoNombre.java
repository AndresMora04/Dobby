package dobby.vista;

import dobby.util.TemaManager;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class DialogoNombre {
    private static final int ANCHO_TARJETA = 560;
    private static final int ALTO_TARJETA = 260;
    private static final int ANCHO_CONTENIDO = ANCHO_TARJETA - 48;

    private DialogoNombre() {
    }

    public static String pedir(Window propietario, String titulo, String etiqueta, String valorInicial) {
        String[] resultado = {null};
        ModalDialog[] dialogo = new ModalDialog[1];
        JTextField campo = crearCampo(valorInicial);

        Runnable aceptar = () -> {
            resultado[0] = campo.getText();
            dialogo[0].dispose();
        };
        Runnable cancelar = () -> dialogo[0].dispose();

        JPanel contenido = construirContenido(etiqueta, campo, aceptar, cancelar);
        dialogo[0] = new ModalDialog(propietario, titulo, ANCHO_TARJETA, ALTO_TARJETA, contenido);
        dialogo[0].addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                campo.requestFocusInWindow();
                campo.selectAll();
            }
        });
        campo.registerKeyboardAction(e -> aceptar.run(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);
        campo.registerKeyboardAction(e -> cancelar.run(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_FOCUSED);
        dialogo[0].setVisible(true);
        return resultado[0];
    }

    private static JTextField crearCampo(String valorInicial) {
        JTextField campo = new JTextField(valorInicial);
        campo.setFont(new Font("SansSerif", Font.PLAIN, 17));
        campo.setForeground(new Color(232, 227, 218));
        campo.setCaretColor(Color.WHITE);
        campo.setBackground(new Color(14, 11, 24));
        campo.setSelectionColor(TemaManager.getInstancia().getColorAcento());
        campo.setSelectedTextColor(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(TemaManager.getInstancia().getColorAcento(), 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        return campo;
    }

    private static JPanel construirContenido(String etiqueta, JTextField campo, Runnable aceptar, Runnable cancelar) {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        JLabel texto = new JLabel(etiqueta, JLabel.CENTER);
        texto.setFont(new Font("SansSerif", Font.PLAIN, 16));
        texto.setForeground(new Color(225, 218, 205));
        texto.setBounds(0, 4, ANCHO_CONTENIDO, 22);
        panel.add(texto);

        campo.setBounds(40, 38, ANCHO_CONTENIDO - 80, 42);
        panel.add(campo);

        BotonBarra botonAceptar = new BotonBarra("Aceptar", BotonBarra.Icono.NINGUNO);
        BotonBarra botonCancelar = new BotonBarra("Cancelar", BotonBarra.Icono.NINGUNO);
        botonAceptar.addActionListener(e -> aceptar.run());
        botonCancelar.addActionListener(e -> cancelar.run());

        int anchoAceptar = botonAceptar.getPreferredSize().width;
        int anchoCancelar = botonCancelar.getPreferredSize().width;
        int x = (ANCHO_CONTENIDO - (anchoAceptar + 14 + anchoCancelar)) / 2;
        botonAceptar.setBounds(x, 104, anchoAceptar, 40);
        botonCancelar.setBounds(x + anchoAceptar + 14, 104, anchoCancelar, 40);
        panel.add(botonAceptar);
        panel.add(botonCancelar);
        return panel;
    }
}
