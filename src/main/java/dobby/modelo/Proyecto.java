package dobby.modelo;

import java.nio.file.Path;
import java.util.List;

/**
 * Representa un proyecto Dobby: una carpeta en disco que contiene uno o
 * varios archivos {@code .dobby}.
 * <p>
 * Mantiene la lista de {@link ArchivoDobby} que pertenecen al proyecto y
 * cuál de ellos se considera el archivo principal (punto de entrada).
 */
public class Proyecto {

    private String nombre;
    private Path carpeta;
    private List<ArchivoDobby> archivos;
    private ArchivoDobby archivoPrincipal;

    /**
     * Crea un proyecto Dobby a partir de su carpeta raíz.
     *
     * @param nombre  nombre del proyecto
     * @param carpeta carpeta raíz del proyecto en disco
     */
    public Proyecto(String nombre, Path carpeta) {
        // TODO: inicializar lista de archivos (posiblemente vacía o cargada desde carpeta)
        this.nombre = nombre;
        this.carpeta = carpeta;
    }

    /**
     * @return el nombre del proyecto
     */
    public String getNombre() {
        // TODO: implementar
        return nombre;
    }

    /**
     * @param nombre nuevo nombre del proyecto
     */
    public void setNombre(String nombre) {
        // TODO: implementar
        this.nombre = nombre;
    }

    /**
     * @return la carpeta raíz del proyecto
     */
    public Path getCarpeta() {
        // TODO: implementar
        return carpeta;
    }

    /**
     * @param carpeta nueva carpeta raíz del proyecto
     */
    public void setCarpeta(Path carpeta) {
        // TODO: implementar
        this.carpeta = carpeta;
    }

    /**
     * @return la lista de archivos {@code .dobby} que pertenecen al proyecto
     */
    public List<ArchivoDobby> getArchivos() {
        // TODO: implementar
        return archivos;
    }

    /**
     * @param archivos nueva lista de archivos del proyecto
     */
    public void setArchivos(List<ArchivoDobby> archivos) {
        // TODO: implementar
        this.archivos = archivos;
    }

    /**
     * @return el archivo principal (punto de entrada) del proyecto
     */
    public ArchivoDobby getArchivoPrincipal() {
        // TODO: implementar
        return archivoPrincipal;
    }

    /**
     * @param archivoPrincipal nuevo archivo principal del proyecto
     */
    public void setArchivoPrincipal(ArchivoDobby archivoPrincipal) {
        // TODO: implementar
        this.archivoPrincipal = archivoPrincipal;
    }

    /**
     * Agrega un archivo a la lista de archivos del proyecto.
     *
     * @param archivo archivo a agregar
     */
    public void agregarArchivo(ArchivoDobby archivo) {
        // TODO: implementar
    }
}
