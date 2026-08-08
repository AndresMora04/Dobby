package dobby.modelo;

import java.nio.file.Path;

/**
 * Representa un único archivo fuente de Dobby ({@code .dobby}) dentro de un
 * {@link Proyecto}.
 * <p>
 * Guarda la ruta del archivo en disco y su contenido en memoria mientras se
 * edita desde el {@code EditorPanel}.
 */
public class ArchivoDobby {

    private Path ruta;
    private String nombre;
    private String contenido;
    private boolean modificado;

    /**
     * Crea una representación de un archivo Dobby.
     *
     * @param ruta ubicación del archivo en el sistema de archivos
     */
    public ArchivoDobby(Path ruta) {
        // TODO: inicializar nombre a partir de la ruta y cargar contenido inicial
        this.ruta = ruta;
    }

    /**
     * @return la ruta del archivo en disco
     */
    public Path getRuta() {
        // TODO: implementar
        return ruta;
    }

    /**
     * @param ruta nueva ruta del archivo
     */
    public void setRuta(Path ruta) {
        // TODO: implementar
        this.ruta = ruta;
    }

    /**
     * @return el nombre del archivo (sin la ruta completa)
     */
    public String getNombre() {
        // TODO: implementar
        return nombre;
    }

    /**
     * @param nombre nuevo nombre del archivo
     */
    public void setNombre(String nombre) {
        // TODO: implementar
        this.nombre = nombre;
    }

    /**
     * @return el contenido actual del archivo en memoria
     */
    public String getContenido() {
        // TODO: implementar
        return contenido;
    }

    /**
     * @param contenido nuevo contenido del archivo
     */
    public void setContenido(String contenido) {
        // TODO: implementar
        this.contenido = contenido;
    }

    /**
     * @return {@code true} si el archivo tiene cambios sin guardar
     */
    public boolean isModificado() {
        // TODO: implementar
        return modificado;
    }

    /**
     * @param modificado nuevo estado de modificación del archivo
     */
    public void setModificado(boolean modificado) {
        // TODO: implementar
        this.modificado = modificado;
    }
}
