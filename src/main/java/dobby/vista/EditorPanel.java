package dobby.vista;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import java.awt.BorderLayout;
import java.awt.Font;

public class EditorPanel extends JPanel {
    private JTextPane areaTexto;

    public EditorPanel() {
        setLayout(new BorderLayout());
        areaTexto = new JTextPane();
        areaTexto.setFont(new Font("Consolas", Font.PLAIN, 16));
        areaTexto.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane scroll = new JScrollPane(areaTexto);
        add(scroll, BorderLayout.CENTER);
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
