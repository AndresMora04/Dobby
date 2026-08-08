package dobby.flow;

import dobby.modelo.ArchivoDobby;
import dobby.modelo.Proyecto;
import dobby.motor.interprete.ResultadoEjecucion;

/**
 * Centraliza el estado global de la aplicación Dobby: el proyecto activo,
 * el archivo activo dentro de ese proyecto y el resultado de la última
 * compilación/ejecución.
 * <p>
 * No contiene lógica de negocio ni interactúa directamente con la interfaz
 * gráfica; es simplemente el punto único de verdad del estado, consultado
 * y actualizado por el {@code Controlador}.
 */
public class FlowController {

    private Proyecto proyectoActivo;
    private ArchivoDobby archivoActivo;
    private ResultadoEjecucion ultimoResultado;

    /**
     * Crea un FlowController sin proyecto ni archivo activos.
     */
    public FlowController() {
        // TODO: implementar inicialización de estado por defecto
    }

    /**
     * @return el proyecto actualmente abierto, o {@code null} si no hay ninguno
     */
    public Proyecto getProyectoActivo() {
        // TODO: implementar
        return proyectoActivo;
    }

    /**
     * @param proyectoActivo nuevo proyecto activo
     */
    public void setProyectoActivo(Proyecto proyectoActivo) {
        // TODO: implementar
        this.proyectoActivo = proyectoActivo;
    }

    /**
     * @return el archivo actualmente abierto en el editor, o {@code null} si no hay ninguno
     */
    public ArchivoDobby getArchivoActivo() {
        // TODO: implementar
        return archivoActivo;
    }

    /**
     * @param archivoActivo nuevo archivo activo
     */
    public void setArchivoActivo(ArchivoDobby archivoActivo) {
        // TODO: implementar
        this.archivoActivo = archivoActivo;
    }

    /**
     * @return el resultado de la última compilación/ejecución, o {@code null} si aún no se ha ejecutado nada
     */
    public ResultadoEjecucion getUltimoResultado() {
        // TODO: implementar
        return ultimoResultado;
    }

    /**
     * @param ultimoResultado nuevo resultado de compilación/ejecución
     */
    public void setUltimoResultado(ResultadoEjecucion ultimoResultado) {
        // TODO: implementar
        this.ultimoResultado = ultimoResultado;
    }
}
