package dobby.motor.interprete;

import java.util.ArrayList;
import java.util.List;

public class ValorArreglo {
    private final String tipoElemento;
    private final List<Object> elementos;

    public ValorArreglo(String tipoElemento, List<Object> elementos) {
        this.tipoElemento = tipoElemento;
        this.elementos = new ArrayList<>(elementos);
    }

    public String getTipoElemento() {
        return tipoElemento;
    }

    public int longitud() {
        return elementos.size();
    }

    public Object obtener(int indice, int linea) {
        validarIndice(indice, linea);
        return elementos.get(indice);
    }

    public void asignar(int indice, Object valor, int linea) {
        validarIndice(indice, linea);
        elementos.set(indice, valor);
    }

    public void validarIndice(int indice, int linea) {
        if (indice < 0 || indice >= elementos.size()) {
            throw new ErrorEjecucion("Indice " + indice + " fuera de rango en Gringotts de longitud "
                + elementos.size(), linea);
        }
    }
}
