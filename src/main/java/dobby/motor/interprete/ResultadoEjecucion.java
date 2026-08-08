package dobby.motor.interprete;

import java.util.List;

/**
 * Contenedor simple con el resultado de ejecutar (compilar + interpretar)
 * un programa Dobby: la salida generada, los errores encontrados y si la
 * ejecución fue exitosa.
 */
public class ResultadoEjecucion {

    private boolean exito;
    private String salida;
    private List<String> errores;

    /**
     * @return {@code true} si el programa se ejecutó sin errores
     */
    public boolean isExito() {
        // TODO: implementar
        return exito;
    }

    /**
     * @param exito nuevo estado de éxito de la ejecución
     */
    public void setExito(boolean exito) {
        // TODO: implementar
        this.exito = exito;
    }

    /**
     * @return la salida (texto) generada por el programa, por ejemplo mediante {@code Revelio}
     */
    public String getSalida() {
        // TODO: implementar
        return salida;
    }

    /**
     * @param salida nueva salida generada por el programa
     */
    public void setSalida(String salida) {
        // TODO: implementar
        this.salida = salida;
    }

    /**
     * @return la lista de errores (léxicos, sintácticos o de ejecución) encontrados
     */
    public List<String> getErrores() {
        // TODO: implementar
        return errores;
    }

    /**
     * @param errores nueva lista de errores
     */
    public void setErrores(List<String> errores) {
        // TODO: implementar
        this.errores = errores;
    }
}
