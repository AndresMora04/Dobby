package dobby.vista;

import dobby.util.TemaManager;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class EjemplosPanel extends JPanel {
    private static final Color FONDO = new Color(18, 14, 30);
    private static final Color TEXTO = new Color(210, 205, 195);

    private final JList<EjemplosDobby.Ejemplo> lista;
    private Consumer<String> alInsertar;

    public EjemplosPanel() {
        setLayout(new BorderLayout());
        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        lista = new JList<>(EjemplosDobby.EJEMPLOS) {
            @Override
            public String getToolTipText(MouseEvent e) {
                int indice = locationToIndex(e.getPoint());
                if (indice < 0 || !getCellBounds(indice, indice).contains(e.getPoint())) {
                    return null;
                }
                return getModel().getElementAt(indice).descripcion();
            }
        };
        lista.setToolTipText("");
        lista.setBackground(FONDO);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setFixedCellHeight(30);
        lista.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                EjemplosDobby.Ejemplo seleccionado = lista.getSelectedValue();
                if (seleccionado != null && alInsertar != null) {
                    alInsertar.accept(seleccionado.codigo());
                }
            }
        });

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(FONDO);
        add(scroll, BorderLayout.CENTER);

        aplicarEstiloCeldas();
        TemaManager.getInstancia().agregarOyente(this::aplicarEstiloCeldas);
    }

    private void aplicarEstiloCeldas() {
        Color acento = TemaManager.getInstancia().getColorAcento();
        lista.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean seleccionado, boolean foco) {
                JLabel etiqueta = (JLabel) super.getListCellRendererComponent(list, value, index, seleccionado, foco);
                etiqueta.setOpaque(true);
                etiqueta.setBackground(seleccionado ? acento : FONDO);
                etiqueta.setForeground(seleccionado ? Color.WHITE : TEXTO);
                etiqueta.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return etiqueta;
            }
        });
        lista.repaint();
    }

    public void alInsertarEjemplo(Consumer<String> alInsertar) {
        this.alInsertar = alInsertar;
    }
}
