package dobby.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Utilidades estáticas para leer, escribir y listar archivos {@code .dobby}
 * en el sistema de archivos, apoyadas en {@code java.nio.file.Files}.
 */
public final class FileUtil {

    private FileUtil() {
        // Clase de utilidades: no debe instanciarse.
    }

    /**
     * Lee el contenido completo de un archivo de texto.
     *
     * @param ruta ruta del archivo a leer
     * @return el contenido del archivo como cadena de texto
     * @throws IOException si ocurre un error de lectura
     */
    public static String leerArchivo(Path ruta) throws IOException {
        // TODO: implementar usando Files.readString o similar
        return null;
    }

    /**
     * Escribe (o sobrescribe) el contenido de un archivo de texto.
     *
     * @param ruta      ruta del archivo a escribir
     * @param contenido contenido a guardar en el archivo
     * @throws IOException si ocurre un error de escritura
     */
    public static void escribirArchivo(Path ruta, String contenido) throws IOException {
        // TODO: implementar usando Files.writeString o similar
    }

    /**
     * Lista los archivos {@code .dobby} contenidos (recursivamente) en una carpeta.
     *
     * @param carpeta carpeta raíz a explorar
     * @return la lista de rutas de archivos {@code .dobby} encontrados
     * @throws IOException si ocurre un error al recorrer la carpeta
     */
    public static List<Path> listarArchivosDobby(Path carpeta) throws IOException {
        // TODO: implementar usando Files.walk o similar
        return null;
    }

    /**
     * Verifica si una ruta corresponde a un archivo con extensión {@code .dobby}.
     *
     * @param ruta ruta a verificar
     * @return {@code true} si la ruta tiene extensión {@code .dobby}
     */
    public static boolean esArchivoDobby(Path ruta) {
        // TODO: implementar
        return false;
    }
}
