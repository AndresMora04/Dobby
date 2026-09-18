package dobby.motor.interprete;

public class ErrorEjecucion extends RuntimeException {
    private final int linea;

    public ErrorEjecucion(String mensaje, int linea) {
        super(mensaje);
        this.linea = linea;
    }

    public int getLinea() {
        return linea;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " (linea " + linea + ")";
    }
}
