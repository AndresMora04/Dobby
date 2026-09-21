package dobby.motor.enlazador;

public class ErrorEnlace extends RuntimeException {
    private final String tipo;

    public ErrorEnlace(String mensaje) {
        this("Error semantico", mensaje);
    }

    public ErrorEnlace(String tipo, String mensaje) {
        super(mensaje);
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}
