package dobby.util;

/**
 * Da formato consistente a los mensajes de error producidos durante la
 * tokenización, el análisis sintáctico o la interpretación de código
 * Dobby, incluyendo tipo de error, archivo, línea, descripción y causa
 * probable.
 */
public final class ErrorFormatter {

    private ErrorFormatter() {
        // Clase de utilidades: no debe instanciarse.
    }

    /**
     * Da formato a un mensaje de error completo.
     *
     * @param tipo           tipo de error (por ejemplo: "Error léxico", "Error sintáctico", "Error de ejecución")
     * @param nombreArchivo  nombre del archivo {@code .dobby} donde ocurrió el error
     * @param linea          línea del código fuente donde ocurrió el error
     * @param descripcion    descripción del error
     * @param causaProbable  causa probable del error, orientada a ayudar al estudiante a corregirlo
     * @return el mensaje de error formateado, listo para mostrarse en el {@code OutputPanel}
     */
    public static String formatear(String tipo, String nombreArchivo, int linea, String descripcion, String causaProbable) {
        // TODO: implementar formato consistente del mensaje de error
        return null;
    }
}
