package dobby.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ErrorFormatter {
    private static final Pattern UBICACION = Pattern.compile(
        "(?s)^(.*) \\(linea (\\d+)(?:, token '(.*)')?\\)$");

    private ErrorFormatter() {
    }

    public static String formatear(String tipo, String nombreArchivo, int linea, String descripcion, String causaProbable) {
        return textoO(tipo, "Error") + System.lineSeparator()
            + "Archivo: " + textoO(nombreArchivo, "Sin titulo") + System.lineSeparator()
            + "Linea: " + (linea > 0 ? linea : "No disponible") + System.lineSeparator()
            + "Descripcion: " + textoO(descripcion, "No se recibio una descripcion del error.") + System.lineSeparator()
            + "Posible causa: " + textoO(causaProbable, "Revisa la instruccion indicada.");
    }

    public static String formatearMensaje(String tipo, String nombreArchivo, String mensaje) {
        String descripcion = textoO(mensaje, "No se recibio una descripcion del error.");
        int linea = 0;
        if (descripcion.startsWith("[") && descripcion.contains("] ")) {
            int cierre = descripcion.indexOf("] ");
            nombreArchivo = descripcion.substring(1, cierre);
            descripcion = descripcion.substring(cierre + 2);
        }
        Matcher ubicacion = UBICACION.matcher(descripcion);
        if (ubicacion.matches()) {
            descripcion = ubicacion.group(1);
            try {
                linea = Integer.parseInt(ubicacion.group(2));
            } catch (NumberFormatException e) {
                linea = 0;
            }
            if (ubicacion.group(3) != null) {
                descripcion += " (token '" + ubicacion.group(3) + "')";
            }
        }
        return formatear(tipo, nombreArchivo, linea, descripcion, sugerencia(tipo, descripcion));
    }

    private static String sugerencia(String tipo, String descripcion) {
        String mensaje = descripcion.toLowerCase(Locale.ROOT);
        if (mensaje.contains("limite")) {
            return "Reduce el tamano o anidamiento del programa; revisa la condicion de los ciclos y el caso base de la recursion.";
        }
        if ("Error lexico".equals(tipo)) {
            return "Revisa simbolos, operadores completos, comillas, escapes y el cierre de los comentarios.";
        }
        if (mensaje.contains("fuera de rango") && !mensaje.contains("indice")) {
            return "El numero excede el rango de Entero o Decimal; revisa el literal, la entrada y las operaciones.";
        }
        if (mensaje.contains("cancelada") || mensaje.contains("interrumpida")) {
            return "La ejecucion se detuvo sin completar el programa. Puedes volver a ejecutarlo.";
        }
        if ("Error de proyecto".equals(tipo)) {
            return "Revisa la carpeta del proyecto: debe existir un unico bloque Hogwarts() entre sus archivos .dobby.";
        }
        if ("Error de sintaxis".equals(tipo)) {
            return "Revisa la escritura, los parentesis, las llaves y los puntos y comas cerca de esa linea.";
        }
        if ("Error de importacion".equals(tipo)) {
            return "Revisa la ruta de Floo, la funcion o estructura importada y las dependencias entre archivos.";
        }
        if (mensaje.contains("indice") || mensaje.contains("fuera de rango")) {
            return "Usa un indice Entero entre 0 y arreglo.longitud - 1; un arreglo vacio no tiene posiciones.";
        }
        if (mensaje.contains("gringotts") && mensaje.contains("no ha sido inicializado")) {
            return "Asigna un arreglo, por ejemplo [10, 20], antes de consultar su longitud o sus posiciones.";
        }
        if (mensaje.contains("varita") && mensaje.contains("no ha sido inicializada")) {
            return "Crea un valor como Punto {x: 1, y: 2} antes de consultar o modificar sus campos.";
        }
        if (mensaje.contains("no ha sido inicializada")) {
            return "Asigna un valor a la variable antes de leerla, imprimirla o usarla en una operacion.";
        }
        if (mensaje.contains("campo")) {
            return "Revisa los campos de Varita: sus nombres, sus tipos y que cada campo tenga un unico valor al crear la estructura.";
        }
        if (mensaje.contains("no ha sido declarada")) {
            return "Declara la variable con Alohomora antes de usarla en ese camino del programa.";
        }
        if (mensaje.contains("declarada") || mensaje.contains("declarado")) {
            return "Revisa los nombres: evita duplicados y declara las funciones antes de llamarlas.";
        }
        if (mensaje.contains("hogwarts")) {
            return "El archivo principal debe tener un unico Hogwarts(), sin parametros ni Patronum.";
        }
        if (mensaje.contains("patronum")) {
            return "Agrega un Patronum del tipo indicado en cada camino que pueda terminar la funcion.";
        }
        if (mensaje.contains("ciclo")) {
            return "Coloca Expelliarmus o Reparo dentro de Imperio o Wingardium.";
        }
        if (mensaje.contains("cero")) {
            return "Comprueba que el divisor sea distinto de cero antes de dividir.";
        }
        if (mensaje.contains("argumento")) {
            return "Revisa la cantidad, el orden y los tipos de los argumentos de Accio.";
        }
        if (mensaje.contains("tipo") || mensaje.contains("numero") || mensaje.contains("booleano")) {
            return "Revisa el tipo declarado y los valores usados en la asignacion, retorno u operacion.";
        }
        return "Revisa la instruccion indicada y los valores utilizados.";
    }

    private static String textoO(String texto, String alternativa) {
        return texto == null || texto.isBlank() ? alternativa : texto;
    }
}
