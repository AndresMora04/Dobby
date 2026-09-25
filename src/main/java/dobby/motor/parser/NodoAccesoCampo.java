package dobby.motor.parser;

public class NodoAccesoCampo extends Nodo {
    private Nodo estructura;
    private String campo;
    private boolean longitudArreglo;

    public Nodo getEstructura() {
        return estructura;
    }

    public void setEstructura(Nodo estructura) {
        this.estructura = estructura;
    }

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }

    public boolean isLongitudArreglo() {
        return longitudArreglo;
    }

    public void setLongitudArreglo(boolean longitudArreglo) {
        this.longitudArreglo = longitudArreglo;
    }
}
