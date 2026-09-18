package dobby.motor.interprete;

import java.util.HashMap;
import java.util.Map;

public class Entorno {
    private final Map<String, Object> valores = new HashMap<>();
    private final Map<String, String> tipos = new HashMap<>();

    public void declarar(String nombre, String tipo, int linea) {
        if (tipos.containsKey(nombre)) {
            throw new ErrorEjecucion("La variable '" + nombre + "' ya habia sido declarada", linea);
        }
        tipos.put(nombre, tipo);
        valores.put(nombre, null);
    }

    public void asignar(String nombre, Object valor, int linea) {
        if (!tipos.containsKey(nombre)) {
            throw new ErrorEjecucion("La variable '" + nombre + "' no ha sido declarada", linea);
        }
        valores.put(nombre, valor);
    }

    public Object obtener(String nombre, int linea) {
        if (!tipos.containsKey(nombre)) {
            throw new ErrorEjecucion("La variable '" + nombre + "' no ha sido declarada", linea);
        }
        return valores.get(nombre);
    }

    public String obtenerTipo(String nombre) {
        return tipos.get(nombre);
    }

    public boolean existe(String nombre) {
        return tipos.containsKey(nombre);
    }
}
