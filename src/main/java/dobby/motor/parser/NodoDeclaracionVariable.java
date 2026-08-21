package dobby.motor.parser;

public class NodoDeclaracionVariable extends Nodo {
    private String nombre;
    private String tipo;
    private Nodo valorInicial;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Nodo getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(Nodo valorInicial) {
        this.valorInicial = valorInicial;
    }
}
