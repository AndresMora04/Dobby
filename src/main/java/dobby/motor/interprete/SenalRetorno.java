package dobby.motor.interprete;

public class SenalRetorno extends RuntimeException {
    private final Object valor;
    private final int linea;

    public SenalRetorno(Object valor) {
        this(valor, 0);
    }

    public SenalRetorno(Object valor, int linea) {
        super(null, null, false, false);
        this.valor = valor;
        this.linea = linea;
    }

    public Object getValor() {
        return valor;
    }

    public int getLinea() {
        return linea;
    }
}
