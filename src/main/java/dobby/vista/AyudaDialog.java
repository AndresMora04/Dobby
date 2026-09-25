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

        temas.put("Palabras reservadas", """
            Alohomora   -> declarar variable
            Expecto     -> declarar función
            Patronum    -> devolver un valor (return)
            Revelio     -> mostrar un valor (imprimir)
            Legilimens  -> pedir un dato al usuario (input)
            Accio       -> llamar una función
            Wingardium  -> ciclo for
            Imperio     -> ciclo while
            Protego     -> condicional (if)
            Finite      -> alternativa del condicional (else)
            Expelliarmus -> salir de un ciclo (break)
            Reparo      -> saltar a la siguiente vuelta (continue)
            Lumos / Nox -> verdadero / falso
            Hogwarts    -> función principal (main)
            Floo        -> importar una función o estructura de otro archivo
            desde       -> acompaña a Floo
            Obliviate   -> tipo y valor nulo
            Gringotts   -> arreglo de elementos del mismo tipo
            Varita      -> estructura con campos de distintos tipos
            """);

        temas.put("Sintaxis", """
            Declarar variable:  Alohomora nombre: Tipo;
            Asignar:            nombre = expresión;
            Pedir un dato:      nombre = Legilimens();
            Mostrar:            Revelio expresión;
            Llamar función:     Accio función(argumentos);
            Importar:           Floo función desde "archivo.dobby";

            - Toda sentencia simple termina en punto y coma (;).
            - Los bloques se delimitan con llaves { }. La indentación es solo estética.
            - Los textos van entre comillas dobles: "Hola".
            - Los decimales usan punto: 3.14
            - Una llamada con Accio puede ser una sentencia o parte de una expresión:
                Accio saludar("Harry");
                c = Accio sumar(a, b);
                Revelio Accio sumar(2, 3);
            - Legilimens lee el dato como texto y lo convierte al tipo de la variable
              (para un Booleano se escribe Lumos o Nox).
            """);

        temas.put("Estructuras de control", """
            Condicional:
            Protego (condición) {
                ...
            } Finite {
                ...
            }

            Para elegir entre varias opciones se anida Protego dentro de Finite.

            Ciclo while:
            Imperio (condición) { ... }

            Ciclo for:
            Wingardium (i = 0; i < 10; i = i + 1) { ... }
            (la variable del ciclo se declara sola si aún no existe)

            Dentro de un ciclo:
            Expelliarmus;   sale del ciclo
            Reparo;         salta a la siguiente vuelta

            La condición debe ser un Booleano (Lumos o Nox).
            """);

        temas.put("Funciones", """
            Expecto nombre(parámetro: Tipo, ...): TipoRetorno {
                ...
                Patronum valor;
            }

            Hogwarts() {
                ...
            }

            - Toda función que no sea de tipo Obliviate debe terminar con
              Patronum valor;. Patronum siempre lleva un valor.
            - Una función Obliviate no devuelve nada y no necesita Patronum.
            - Los parámetros y las variables son locales a su función.
              No existen variables globales.
            - Los bloques (Protego, Imperio, Wingardium) no crean un ámbito nuevo:
              una variable declarada dentro existe hasta que termina la función.
            - Una función puede llamarse a sí misma (recursión).
            - Hogwarts() es el punto de entrada: sin parámetros y sin tipo de retorno.
            """);

        temas.put("Operaciones", """
            Aritméticas:   +  -  *  /
            Relacionales:  ==  !=  >  <  >=  <=
            Lógicas:       && (y)   || (o)   ! (no)

            - Se pueden usar paréntesis: (10 + 5) * 2
            - + también une textos: "Hola, " + nombre
            - La división entre dos Enteros es entera: 7 / 2 da 3.
            - Dividir entre cero es un error de ejecución.
            - Con > < >= <= ambos lados deben ser números.
            """);

        temas.put("Tipos de datos", """
            Simples:
              Entero      11
              Decimal     3.14
              Booleano    Lumos / Nox
              Texto       "Harry"
              Caracter    un solo carácter (se maneja como texto)
              Obliviate   nulo; también es el tipo de una función sin retorno

            Revelio muestra los Booleanos como Lumos / Nox y el nulo como Obliviate.

            Compuestos:
              Gringotts   arreglo (implementado)
              Varita      estructura con campos (implementado)
            """);

        temas.put("Arreglos (Gringotts)", """
            Declaración e inicialización:
                Alohomora notas: Gringotts<Entero> = [80, 90, 100];
            También se permite:
                Gringotts<Entero> notas = [80, 90, 100];

            Consultar, modificar y obtener el tamaño:
                Revelio notas[0];
                notas[1] = 95;
                Revelio notas.longitud;

            Recorrer:
                Wingardium (i = 0; i < notas.longitud; i = i + 1) {
                    Revelio notas[i];
                }

            - Los índices son Entero y empiezan en cero.
            - Cada arreglo mantiene su tamaño. longitud es de solo lectura.
            - [] crea un arreglo vacío. Una variable declarada sin valor debe
              inicializarse antes de consultar posiciones o longitud.
            - Los elementos deben respetar el tipo declarado. Decimal admite
              elementos Entero y Texto admite Caracter.
            - Los índices negativos o mayores o iguales a longitud producen un
              error de ejecución con archivo y línea.
            - Se pueden recibir y retornar arreglos en funciones, incluidas las
              importadas con Floo.
            - Asignar un arreglo a otra variable o pasarlo a una función comparte
              el mismo arreglo. Modificar una posición afecta a ambas referencias.
            - Asignar un nuevo literal, como notas = [70, 80], reemplaza solamente
              la referencia de esa variable. No cambia el arreglo anterior.
            - == y != comparan referencias, no el contenido de los arreglos.
            - No hay operaciones para agregar o eliminar posiciones.

            Arreglos anidados:
                Gringotts<Gringotts<Entero>> tabla = [[1, 2], [3, 4]];
                tabla[0][1] = 9;
                Revelio tabla[1].longitud;
            """);

        temas.put("Estructuras (Varita)", """
            Declarar fuera de Hogwarts y de las funciones:
                Varita Punto { x: Entero; y: Decimal; }

            Crear y modificar dentro de una función:
                Alohomora p: Punto = Punto {x: 2, y: 5};
                p.x = 8;
                Revelio p.x;
                Revelio p;

            - El tipo de la variable es el nombre declarado, como Punto, no Varita.
            - Cada campo debe recibir un valor al crear la estructura. No se
              permiten campos faltantes, repetidos, desconocidos o de otro tipo.
            - Los campos pueden escribirse en cualquier orden. Se evalúan en
              el orden escrito y se imprimen en el orden de la declaración.
            - Se admiten tipos simples, otras estructuras y arreglos como campos.
              También se permite Gringotts<Punto> y accesos como puntos[0].x.
            - Las funciones pueden recibir y devolver estructuras.
            - Asignar o pasar una estructura comparte la referencia. Modificar
              un campo afecta a quienes tengan esa misma referencia.
            - Asignar otro literal reemplaza la referencia de esa variable.
              == y != comparan referencias, no el contenido.
            - Una estructura sin inicializar no permite consultar ni modificar
              campos. Debe asignarse un valor completo primero.
            - Un campo puede llamarse longitud; la propiedad longitud de los
              arreglos sigue siendo de solo lectura.
            - Revelio muestra los campos. Si hay referencias circulares,
              muestra <ciclo> en el punto donde se repite la referencia.

            Importar una definición:
                Floo Punto desde "tipos.dobby";

            Floo también hace disponibles los tipos del archivo importado y sus
            dependencias. No se pueden reunir definiciones distintas con el mismo
            nombre: comparte una definición e impórtala desde los otros archivos.
            """);

        temas.put("Semántica", """
            - Todo programa inicia con Hogwarts() { }.
            - Toda variable debe declararse antes de usarse, con Alohomora
              o con la forma Gringotts<Tipo> nombre para arreglos.
            - Una variable no puede declararse dos veces en la misma función.
            - No se puede llamar una función que no esté declarada con Expecto
              en el archivo o importada con Floo.
            - Toda función que no sea Obliviate debe devolver un valor con Patronum.
            - Las importaciones (Floo) se validan al compilar: existencia del
              archivo, existencia de la función o estructura, argumentos, tipos,
              declaraciones duplicadas e importaciones circulares.
            """);

        temas.put("Importaciones (Floo)", """
            Floo función desde "archivo.dobby";

            Permite usar una función o estructura definida en otro archivo.
            Para una estructura: Floo Punto desde "tipos.dobby";

            operaciones.dobby
                Expecto sumar(a: Entero, b: Entero): Entero {
                    Patronum a + b;
                }

            principal.dobby
                Floo sumar desde "operaciones.dobby";

                Hogwarts() {
                    Revelio Accio sumar(2, 3);
                }

            Reglas:
            - Va entre comillas y con la extensión .dobby.
            - La ruta es relativa a la carpeta del archivo que importa:
              "lib/operaciones.dobby" o "../otro.dobby".
            - Una línea Floo por cada función o estructura que se quiera importar.
              El nombre debe estar declarado en ese archivo, no ser otro Floo.
            - Con proyecto abierto se ejecuta su principal; sin proyecto, el
              archivo activo. No se puede importar Hogwarts.
            - Cada archivo ve solo sus propias funciones y las que importó. Dos
              archivos pueden tener una función con el mismo nombre sin chocar.
            - Floo incorpora las estructuras visibles del archivo importado,
              incluidas sus dependencias. Importar varias funciones que usan
              una misma definición no la duplica.
            - Dos definiciones distintas de Varita con el mismo nombre producen
              un error al reunirlas mediante Floo.
            - Si un archivo importado está abierto con cambios sin guardar, se
              usa lo que se ve en pantalla.
            - Guarda el archivo o abre una carpeta como proyecto antes de usar
              Floo, para que se sepa dónde buscar.
            - Errores posibles: el archivo no existe, no declara esa función,
              la función ya existe en este archivo, importación circular,
              cantidad o tipo de argumentos incorrectos.
            """);

        temas.put("Ejemplos", """
            Hola mundo
                Hogwarts() {
                    Revelio "Hola, mundo";
                }

            Condicional y entrada
                Hogwarts() {
                    Alohomora edad: Entero;
                    edad = Legilimens();
                    Protego (edad >= 11) {
                        Revelio "Puede entrar a Hogwarts";
                    } Finite {
                        Revelio "Muy joven para Hogwarts";
                    }
                }

            Función recursiva
                Expecto factorial(n: Entero): Entero {
                    Protego (n <= 1) {
                        Patronum 1;
                    }
                    Patronum n * Accio factorial(n - 1);
                }

                Hogwarts() {
                    Revelio Accio factorial(5);
                }

            Ciclo con salida anticipada
                Hogwarts() {
                    Wingardium (i = 0; i < 10; i = i + 1) {
                        Protego (i == 5) {
                            Expelliarmus;
                        }
                        Revelio i;
                    }
                }

            Hay más ejemplos listos para insertar en la pestaña Ejemplos del
            panel izquierdo.
            """);

        temas.put("Proyectos y archivos", """
            - Archivo > Nuevo proyecto...: elige una carpeta y un nombre.
              Se crea una carpeta nueva con un principal.dobby listo para ejecutar.
            - Archivo > Abrir carpeta...: abre una carpeta como proyecto. El árbol
              muestra sus archivos .dobby, incluidas las subcarpetas.
            - Un click en el árbol abre el archivo en una pestaña.
            - Click derecho en una carpeta: Nuevo archivo aquí.
              Click derecho en un archivo: Renombrar o Eliminar.
            - Cada pestaña muestra un punto si tiene cambios sin guardar y se
              cierra con su X.
            - Guardar, Guardar como y Guardar todo están en el menú Archivo.
            - Al cerrar una pestaña o la ventana con cambios pendientes, se pregunta
              si se quieren guardar.
            - El principal es el archivo que declara Hogwarts(), cualquiera que sea
              su nombre. Se marca con (principal) en el árbol.
            - El proyecto debe contener un único Hogwarts(). Si falta o hay varios,
              Compilar y Ejecutar muestran un error.
            - Con un proyecto abierto, Compilar y Ejecutar parten de su principal,
              aunque la pestaña activa sea un archivo auxiliar. Se usan también los
              cambios sin guardar de los archivos del proyecto.
            - Los archivos nuevos del proyecto empiezan vacíos, sin otro Hogwarts().
            - Proyecto > Actualizar archivos: vuelve a leer el árbol y el principal.
            - Proyecto > Cerrar proyecto: conserva las pestañas y permite compilar
              o ejecutar solamente el archivo activo.
            """);

        temas.put("Atajos de teclado", """
            Ctrl + N            Nuevo archivo
            Ctrl + Shift + N    Nuevo proyecto
            Ctrl + O            Abrir archivo
            Ctrl + Shift + O    Abrir carpeta
            Ctrl + S            Guardar
            Ctrl + Shift + S    Guardar como
            Ctrl + W            Cerrar pestaña
            F6                  Compilar
            F5                  Ejecutar
            F1                  Esta ayuda
            """);

        temas.put("Robustez y limites", """
            Errores léxicos:
            - Los símbolos desconocidos no se ignoran. Usa && y || completos.
            - Los textos sin comillas de cierre y comentarios sin cerrar indican
              la línea donde comenzaron.
            - Se permiten comentarios // de una línea y /* de varias líneas */.
              Los comentarios de bloque no se anidan.
            - Escapes en textos: \\" para comillas, \\\\ para barra invertida,
              \\n para salto de línea, \\r para retorno y \\t para tabulación.
              Las rutas de Floo pueden usar /, por ejemplo "lib/tipos.dobby".

            Valores:
            - Entero admite de -2147483648 a 2147483647. Una operación fuera
              de rango se detiene, sin producir un número incorrecto.
            - Decimal no admite NaN ni infinito.
            - 1 == 1.0 es Lumos. Arreglos y estructuras comparan referencias.
            - Leer una variable sin inicializar produce un error; Obliviate
              conserva su valor nulo.
            - Una declaración dentro de un ciclo se reinicia en cada vuelta.
            - Cancelar Legilimens detiene el programa.

            Límites fijos para programas educativos:
            - Por archivo: 1 000 000 caracteres y 100 000 tokens.
            - Parser: 64 niveles, contando bloques y expresiones.
            - Expresiones en validación: 64 niveles.
            - Importaciones: 64 archivos con ruta en la cadena activa.
            - Ejecución: 100 000 pasos; cuentan sentencias, expresiones y salida.
            - Llamadas simultáneas: 64, incluyendo Hogwarts.
            - Expresiones activas al ejecutar: 128 niveles.
            - Impresión de valores anidados: 64 niveles.
            - Texto individual: 65 536 caracteres.
            - Salida acumulada: 20 000 caracteres, incluidos saltos de línea.

            Un límite detiene el programa con un error y conserva la salida
            anterior. También puede detener programas finitos muy grandes.
            Los contadores se reinician al ejecutar de nuevo.
            La ejecución sigue siendo síncrona, con estos límites de protección.
            Las salidas de más de 200 caracteres aparecen sin animación.
            """);

        temas.put("Errores comunes", """
            Los errores se muestran así:  [archivo.dobby] mensaje (linea N)

            Se esperaba ';'
                Falta el punto y coma al final de una sentencia.

            La variable 'x' no ha sido declarada
                Falta Alohomora antes de usarla, o el nombre está mal escrito.

            La funcion 'x' no esta declarada ni importada
                Falta el Expecto o el Floo, o el nombre está mal escrito.

            La funcion 'x' espera N argumento(s) pero recibio M
                La llamada con Accio pasa una cantidad distinta de argumentos.

            El argumento N de 'x' debe ser de tipo T
                Se pasó un valor literal de otro tipo.

            La funcion 'x' ya esta declarada o importada en este archivo
                Hay una función propia y un Floo con el mismo nombre.

            El archivo 'x' no existe
                La ruta del Floo es incorrecta. Recuerda las comillas y .dobby.

            Importacion circular: a.dobby -> b.dobby -> a.dobby
                Dos archivos se importan entre sí.

            Se esperaba un valor booleano (Lumos/Nox) en la condicion
                La condición de Protego, Imperio o Wingardium no es Booleana.

            No se encontro la funcion principal Hogwarts()
                Ejecutaste un archivo que no tiene Hogwarts.
            """);

        return temas;
    }
}
