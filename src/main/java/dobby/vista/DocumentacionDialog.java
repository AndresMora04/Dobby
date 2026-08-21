package dobby.vista;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextArea;

public class DocumentacionDialog extends JDialog {

    public enum Tema {
        PALABRAS_RESERVADAS,
        SINTAXIS,
        ESTRUCTURAS_DE_CONTROL,
        FUNCIONES,
        OPERACIONES,
        SEMANTICA,
        TIPOS_DE_DATOS,
        IMPORTACIONES,
        EJEMPLOS
    }

    private JList<Tema> listaTemas;
    private JTextArea areaContenido;

    public DocumentacionDialog(JFrame propietario) {
        super(propietario, "Documentación de Dobby", true);
    }

    public void mostrarTema(Tema tema) {
    }

    private String obtenerContenido(Tema tema) {
        return null;
    }
}
