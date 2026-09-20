package dobby.flow;

import dobby.modelo.ArchivoDobby;
import dobby.modelo.Proyecto;
import dobby.motor.interprete.ResultadoEjecucion;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FlowController {
    private Proyecto proyectoActivo;
    private ArchivoDobby archivoActivo;
    private ResultadoEjecucion ultimoResultado;
    private final List<ArchivoDobby> archivosAbiertos = new ArrayList<>();

    public FlowController() {
    }

    public Proyecto getProyectoActivo() {
        return proyectoActivo;
    }

    public void setProyectoActivo(Proyecto proyectoActivo) {
        this.proyectoActivo = proyectoActivo;
    }

    public ArchivoDobby getArchivoActivo() {
        return archivoActivo;
    }

    public void setArchivoActivo(ArchivoDobby archivoActivo) {
        this.archivoActivo = archivoActivo;
    }

    public ResultadoEjecucion getUltimoResultado() {
        return ultimoResultado;
    }

    public void setUltimoResultado(ResultadoEjecucion ultimoResultado) {
        this.ultimoResultado = ultimoResultado;
    }

    public List<ArchivoDobby> getArchivosAbiertos() {
        return archivosAbiertos;
    }

    public ArchivoDobby buscarAbierto(Path ruta) {
        for (ArchivoDobby archivo : archivosAbiertos) {
            if (ruta.equals(archivo.getRuta())) {
                return archivo;
            }
        }
        return null;
    }
}
