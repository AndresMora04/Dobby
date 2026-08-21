package dobby.modelo;

import java.nio.file.Path;

public class ArchivoDobby {
    private Path ruta;
    private String nombre;
    private String contenido;
    private boolean modificado;

    public ArchivoDobby(Path ruta) {
        this.ruta = ruta;
    }

    public Path getRuta() {
        return ruta;
    }

    public void setRuta(Path ruta) {
        this.ruta = ruta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public boolean isModificado() {
        return modificado;
    }

    public void setModificado(boolean modificado) {
        this.modificado = modificado;
    }
}
