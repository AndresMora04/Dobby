package dobby.motor.interprete;

import dobby.motor.parser.NodoFuncion;

import java.util.HashMap;
import java.util.Map;

public class Entorno {
    private final Map<String, Object> valores = new HashMap<>();
    private final Map<String, String> tipos = new HashMap<>();
    private final Map<String, NodoFuncion> ambito;

    public Entorno(Map<String, NodoFuncion> ambito) {
        this.ambito = ambito;
    }

    public Map<String, NodoFuncion> getAmbito() {
        return ambito;
    }

    public void declarar(String nombre, String tipo, int linea) {
        // El validador rechaza duplicados; una declaracion dentro de un ciclo se reinicia en cada vuelta.
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
        Object valor = valores.get(nombre);
        String tipo = tipos.get(nombre);
        if (valor == null && !"Obliviate".equals(tipo)) {
            if (tipo != null && tipo.startsWith("Gringotts<")) {
                throw new ErrorEjecucion("El arreglo Gringotts '" + nombre + "' no ha sido inicializado", linea);
            }
            if (!java.util.List.of("Entero", "Decimal", "Booleano", "Texto", "Caracter").contains(tipo)) {
                throw new ErrorEjecucion("La estructura Varita '" + nombre + "' no ha sido inicializada", linea);
            }
            throw new ErrorEjecucion("La variable '" + nombre + "' no ha sido inicializada", linea);
        }
        return valor;
    }

    public String obtenerTipo(String nombre) {
        return tipos.get(nombre);
    }

    public boolean existe(String nombre) {
        return tipos.containsKey(nombre);
    }
}
