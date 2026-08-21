package dobby.flow;

import dobby.modelo.ArchivoDobby;
import dobby.modelo.Proyecto;
import dobby.motor.interprete.ResultadoEjecucion;

public class FlowController {
    private Proyecto proyectoActivo;
    private ArchivoDobby archivoActivo;
    private ResultadoEjecucion ultimoResultado;

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
}
