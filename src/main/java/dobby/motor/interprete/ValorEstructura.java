package dobby.motor.interprete;

import dobby.motor.parser.NodoEstructura;
import java.util.LinkedHashMap;
import java.util.Map;

public class ValorEstructura {
    private final NodoEstructura definicion;
    private final Map<String, Object> valores = new LinkedHashMap<>();

    public ValorEstructura(NodoEstructura definicion, Map<String, Object> valores) {
        this.definicion = definicion;
        for (String campo : definicion.getCampos().keySet()) {
            this.valores.put(campo, valores.get(campo));
        }
    }

    public NodoEstructura getDefinicion() {
        return definicion;
    }

    public String tipoCampo(String campo, int linea) {
        String tipo = definicion.getCampos().get(campo);
        if (tipo == null) {
            throw new ErrorEjecucion("La estructura '" + definicion.getNombre()
                + "' no tiene el campo '" + campo + "'", linea);
        }
        return tipo;
    }

    public Object obtener(String campo, int linea) {
        tipoCampo(campo, linea);
        return valores.get(campo);
    }

    public void asignar(String campo, Object valor, int linea) {
        tipoCampo(campo, linea);
        valores.put(campo, valor);
    }
}
