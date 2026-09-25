package dobby.motor.lexer;

public class ErrorLexico extends RuntimeException {
    public ErrorLexico(String mensaje, int linea) {
        super(mensaje + " (linea " + linea + ")");
    }
}
