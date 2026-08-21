package dobby.modelo;

import java.nio.file.Path;
import java.util.List;

public class Proyecto {
    private String nombre;
    private Path carpeta;
    private List<ArchivoDobby> archivos;
    private ArchivoDobby archivoPrincipal;

    public Proyecto(String nombre, Path carpeta) {
        this.nombre = nombre;
        this.carpeta = carpeta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Path getCarpeta() {
        return carpeta;
    }

    public void setCarpeta(Path carpeta) {
        this.carpeta = carpeta;
    }

    public List<ArchivoDobby> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<ArchivoDobby> archivos) {
        this.archivos = archivos;
    }

    public ArchivoDobby getArchivoPrincipal() {
        return archivoPrincipal;
    }

    public void setArchivoPrincipal(ArchivoDobby archivoPrincipal) {
        this.archivoPrincipal = archivoPrincipal;
    }

    public void agregarArchivo(ArchivoDobby archivo) {
    }
}
