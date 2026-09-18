package dobby.motor.interprete;

public class SenalRetorno extends RuntimeException {
    private final Object valor;

    public SenalRetorno(Object valor) {
        super(null, null, false, false);
        this.valor = valor;
    }

    public Object getValor() {
        return valor;
    }
}
