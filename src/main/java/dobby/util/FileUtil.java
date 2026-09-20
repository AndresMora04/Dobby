package dobby.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public final class FileUtil {
    private FileUtil() {
    }

    public static String leerArchivo(Path ruta) throws IOException {
        return Files.readString(ruta);
    }

    public static void escribirArchivo(Path ruta, String contenido) throws IOException {
        Files.writeString(ruta, contenido);
    }

    public static List<Path> listarArchivosDobby(Path carpeta) throws IOException {
        try (Stream<Path> flujo = Files.list(carpeta)) {
            return flujo.filter(FileUtil::esArchivoDobby).toList();
        }
    }

    public static List<Path> listarArchivosDobbyRecursivo(Path carpeta, int profundidad) throws IOException {
        try (Stream<Path> flujo = Files.walk(carpeta, profundidad)) {
            return flujo.filter(Files::isRegularFile)
                .filter(FileUtil::esArchivoDobby)
                .filter(ruta -> !estaEnCarpetaOculta(carpeta, ruta))
                .sorted()
                .toList();
        }
    }

    private static boolean estaEnCarpetaOculta(Path raiz, Path ruta) {
        Path relativa = raiz.relativize(ruta);
        for (int i = 0; i < relativa.getNameCount() - 1; i++) {
            if (relativa.getName(i).toString().startsWith(".")) {
                return true;
            }
        }
        return false;
    }

    public static boolean esArchivoDobby(Path ruta) {
        return ruta.getFileName().toString().endsWith(".dobby");
    }
}
