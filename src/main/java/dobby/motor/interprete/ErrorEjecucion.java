package dobby.motor.interprete;

public class ErrorEjecucion extends RuntimeException {
    private final int linea;
    private String archivo;

    public ErrorEjecucion(String mensaje, int linea) {
        super(mensaje);
        this.linea = linea;
    }

    public int getLinea() {
        return linea;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    @Override
    public String getMessage() {
        String prefijo = archivo != null ? "[" + archivo + "] " : "";
        return prefijo + super.getMessage() + " (linea " + linea + ")";
    }
}
