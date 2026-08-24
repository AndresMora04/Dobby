package dobby.vista;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.util.LinkedHashMap;
import java.util.Map;

public class AyudaDialog extends ModalDialog {

    private static final Map<String, String> TEMAS = construirTemas();

    public AyudaDialog(Window propietario) {
        super(propietario, "Ayuda - Referencia de Dobby", 800, 480, construirContenido());
    }

    private static JPanel construirContenido() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);

        DefaultListModel<String> modelo = new DefaultListModel<>();
        for (String tema : TEMAS.keySet()) {
            modelo.addElement(tema);
        }

        JList<String> listaTemas = new JList<>(modelo);
        listaTemas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaTemas.setBackground(new Color(30, 24, 48));
        listaTemas.setForeground(new Color(230, 220, 200));
        listaTemas.setSelectionBackground(new Color(80, 60, 30));
        listaTemas.setSelectionForeground(Color.WHITE);
        listaTemas.setFont(new Font("SansSerif", Font.PLAIN, 13));
        listaTemas.setFixedCellHeight(26);

        JTextArea areaContenido = new JTextArea();
        areaContenido.setEditable(false);
        areaContenido.setLineWrap(true);
        areaContenido.setWrapStyleWord(true);
        areaContenido.setBackground(new Color(18, 14, 32));
        areaContenido.setForeground(new Color(225, 218, 205));
        areaContenido.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaContenido.setBorder(new EmptyBorder(10, 12, 10, 12));

        listaTemas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String tema = listaTemas.getSelectedValue();
                areaContenido.setText(TEMAS.getOrDefault(tema, ""));
                areaContenido.setCaretPosition(0);
            }
        });
        listaTemas.setSelectedIndex(0);

        JScrollPane scrollLista = new JScrollPane(listaTemas);
        scrollLista.setPreferredSize(new java.awt.Dimension(160, 0));
        JScrollPane scrollContenido = new JScrollPane(areaContenido);

        panel.add(scrollLista, BorderLayout.WEST);
        panel.add(scrollContenido, BorderLayout.CENTER);

        return panel;
    }

    private static Map<String, String> construirTemas() {
        Map<String, String> temas = new LinkedHashMap<>();

        temas.put("Palabras reservadas",
            "Alohomora   -> declarar variable\n"
            + "Expecto     -> declarar funcion\n"
            + "Patronum    -> retornar valor (return)\n"
            + "Revelio     -> salida de datos (imprimir)\n"
            + "Legilimens  -> entrada de datos (input)\n"
            + "Accio       -> llamar funcion\n"
            + "Wingardium  -> ciclo for\n"
            + "Imperio     -> ciclo while\n"
            + "Protego / Finite -> if / else\n"
            + "Expelliarmus -> break\n"
            + "Reparo      -> continue\n"
            + "Lumos / Nox -> true / false\n"
            + "Hogwarts    -> funcion principal (main)\n"
            + "Gringotts   -> arreglo\n"
            + "Varita      -> struct\n"
            + "Floo        -> importar funcion de otro archivo\n"
            + "Obliviate   -> tipo/valor nulo");

        temas.put("Sintaxis",
            "Declaracion:   Alohomora nombre: Tipo;\n"
            + "Asignacion:    nombre = expresion;\n"
            + "Entrada:       nombre = Legilimens();\n"
            + "Salida:        Revelio expresion;\n"
            + "Llamada:       Accio funcion(args);\n"
            + "Importacion:   Floo funcion desde \"archivo.dobby\";\n"
            + "Bloques delimitados con { }. La indentacion no es obligatoria.");

        temas.put("Estructuras de control",
            "Condicional:\n"
            + "Protego (condicion) {\n"
            + "    ...\n"
            + "} Finite {\n"
            + "    ...\n"
            + "}\n\n"
            + "El condicional multiple se logra anidando Protego/Finite.\n\n"
            + "Ciclo while:\n"
            + "Imperio (condicion) { ... }\n\n"
            + "Ciclo for:\n"
            + "Wingardium (i = 0; i < 10; i = i + 1) { ... }");

        temas.put("Funciones",
            "Expecto sumar(a: Entero, b: Entero): Entero {\n"
            + "    Patronum a + b;\n"
            + "}\n\n"
            + "Hogwarts() {\n"
            + "    ...\n"
            + "}\n\n"
            + "Toda funcion Expecto que no sea de tipo Obliviate\n"
            + "debe incluir un Patronum antes de cerrar su cuerpo.\n"
            + "Hogwarts es el unico punto de entrada del programa.");

        temas.put("Operaciones",
            "Aritmeticas: +  -  *  /\n"
            + "Relacionales: ==  !=  >  <  >=  <=\n"
            + "Logicas: && (y)   || (o)   ! (no)");

        temas.put("Semantica",
            "- Todo programa inicia con Hogwarts() { }\n"
            + "- Toda variable debe declararse con Alohomora antes de usarse\n"
            + "- Los bloques se delimitan con { }\n"
            + "- No se puede llamar una funcion no declarada con Expecto\n"
            + "- Las importaciones (Floo) se validan en compilacion:\n"
            + "  existencia de archivo/funcion, cantidad de argumentos,\n"
            + "  tipos, duplicados e importaciones circulares");

        temas.put("Tipos de datos",
            "Simples:\n"
            + "  Entero, Decimal, Booleano, Texto, Caracter, Obliviate (nulo)\n\n"
            + "Compuestos:\n"
            + "  Gringotts (arreglo)\n"
            + "  Varita (struct)");

        temas.put("Importaciones",
            "Floo funcion desde \"archivo.dobby\";\n\n"
            + "Permite usar en este archivo una funcion definida\n"
            + "en otro archivo del mismo proyecto.");

        temas.put("Ejemplos",
            "Expecto sumar(a: Entero, b: Entero): Entero {\n"
            + "    Patronum a + b;\n"
            + "}\n\n"
            + "Hogwarts() {\n"
            + "    Alohomora edad: Entero;\n"
            + "    edad = Legilimens();\n"
            + "    Protego (edad >= 11) {\n"
            + "        Revelio \"Puede entrar a Hogwarts\";\n"
            + "    } Finite {\n"
            + "        Revelio \"Muy joven para Hogwarts\";\n"
            + "    }\n"
            + "}");

        return temas;
    }
}
