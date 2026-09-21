package dobby.flow;

import dobby.modelo.ArchivoDobby;
import dobby.modelo.Proyecto;
import dobby.motor.enlazador.Enlace;
import dobby.motor.enlazador.Enlazador;
import dobby.motor.enlazador.ErrorEnlace;
import dobby.motor.enlazador.ProveedorCodigo;
import dobby.motor.lexer.Lexer;
import dobby.motor.parser.Nodo;
import dobby.motor.parser.NodoFuncion;
import dobby.motor.parser.NodoPrograma;
import dobby.motor.parser.Parser;
import dobby.util.FileUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class GestorProyecto {
    private final ProveedorCodigo proveedor;

    public GestorProyecto(ProveedorCodigo proveedor) {
        this.proveedor = proveedor;
    }

    public Proyecto crear(Path carpetaPadre, String nombre) throws IOException {
        if (nombre == null || nombre.isBlank() || !nombre.equals(nombre.trim())
            || nombre.equals(".") || nombre.equals("..") || nombre.endsWith(".")
            || nombre.matches(".*[\\\\/:*?\"<>|\\p{Cntrl}].*")) {
            throw new IllegalArgumentException("El nombre del proyecto no es valido.");
        }
        Path carpeta = carpetaPadre.toAbsolutePath().normalize().resolve(nombre);
        Files.createDirectory(carpeta);
        Files.writeString(carpeta.resolve("principal.dobby"), """
            Hogwarts() {
                Revelio "Hola, mundo";
            }
            """, StandardOpenOption.CREATE_NEW);
        return cargar(carpeta);
    }

    public Proyecto cargar(Path carpeta) throws IOException {
        carpeta = carpeta.toAbsolutePath().normalize();
        if (!Files.isDirectory(carpeta)) {
            throw new IOException("La carpeta del proyecto no existe: " + carpeta);
        }
        Path nombre = carpeta.getFileName();
        Proyecto proyecto = new Proyecto(nombre != null ? nombre.toString() : carpeta.toString(), carpeta);
        actualizarArchivos(proyecto);
        return proyecto;
    }

    private void actualizarArchivos(Proyecto proyecto) throws IOException {
        List<ArchivoDobby> archivos = new ArrayList<>();
        for (Path ruta : FileUtil.listarArchivosDobbyRecursivo(proyecto.getCarpeta(), Integer.MAX_VALUE)) {
            ArchivoDobby archivo = new ArchivoDobby(ruta);
            archivo.setNombre(ruta.getFileName().toString());
            archivos.add(archivo);
        }
        proyecto.setArchivos(archivos);
    }

    public ArchivoDobby buscarPrincipal(Proyecto proyecto) {
        proyecto.setArchivoPrincipal(null);
        try {
            actualizarArchivos(proyecto);
        } catch (IOException | UncheckedIOException e) {
            throw errorProyecto(proyecto, "No se pudo leer la carpeta del proyecto.");
        }
        ArchivoDobby principal = null;
        List<String> ubicaciones = new ArrayList<>();
        for (ArchivoDobby archivo : proyecto.getArchivos()) {
            String etiqueta = proyecto.getCarpeta().relativize(archivo.getRuta()).toString();
            NodoPrograma programa;
            try {
                String codigo = proveedor.leer(archivo.getRuta());
                programa = new Parser().parsear(new Lexer().tokenizar(codigo));
            } catch (IOException e) {
                throw new ErrorEnlace("Error de proyecto", "[" + etiqueta + "] No se pudo leer el archivo.");
            } catch (RuntimeException e) {
                throw new ErrorEnlace("Error de sintaxis", "[" + etiqueta + "] " + e.getMessage());
            }
            for (Nodo nodo : programa.getSentencias()) {
                if (nodo instanceof NodoFuncion funcion && funcion.isEsPrincipal()) {
                    principal = archivo;
                    ubicaciones.add(etiqueta + " (linea " + funcion.getLinea() + ")");
                }
            }
        }
        if (ubicaciones.isEmpty()) {
            throw errorProyecto(proyecto, "No se encontro Hogwarts(). El proyecto necesita un archivo principal.");
        }
        if (ubicaciones.size() > 1) {
            throw errorProyecto(proyecto, "El proyecto debe tener un unico Hogwarts(). Se encontraron: "
                + String.join(", ", ubicaciones));
        }
        proyecto.setArchivoPrincipal(principal);
        return principal;
    }

    public Enlace enlazar(Proyecto proyecto) {
        ArchivoDobby principal = buscarPrincipal(proyecto);
        try {
            String codigo = proveedor.leer(principal.getRuta());
            return new Enlazador(ruta -> {
                if (!ruta.startsWith(proyecto.getCarpeta())) {
                    throw new ErrorEnlace("Error de importacion", "[" + principal.getNombre()
                        + "] La importacion '" + ruta + "' esta fuera de la carpeta del proyecto.");
                }
                if (!Files.isRegularFile(ruta)) {
                    throw new NoSuchFileException(ruta.toString());
                }
                return proveedor.leer(ruta);
            }).enlazar(principal.getRuta(), codigo, proyecto.getCarpeta());
        } catch (IOException e) {
            throw errorProyecto(proyecto, "No se pudo leer el archivo principal.");
        }
    }

    private ErrorEnlace errorProyecto(Proyecto proyecto, String mensaje) {
        return new ErrorEnlace("Error de proyecto", "[" + proyecto.getNombre() + "] " + mensaje);
    }
}
