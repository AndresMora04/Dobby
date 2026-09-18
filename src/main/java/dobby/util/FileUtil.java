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

    public static boolean esArchivoDobby(Path ruta) {
        return ruta.getFileName().toString().endsWith(".dobby");
    }
}
