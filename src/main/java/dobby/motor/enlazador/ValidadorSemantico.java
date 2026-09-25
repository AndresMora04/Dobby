package dobby.motor.enlazador;

import dobby.motor.parser.Nodo;
import dobby.motor.parser.NodoArreglo;
import dobby.motor.parser.NodoAccesoArreglo;
import dobby.motor.parser.NodoAsignacionArreglo;
import dobby.motor.parser.NodoLongitud;
import dobby.motor.parser.NodoEstructura;
import dobby.motor.parser.NodoCreacionEstructura;
import dobby.motor.parser.NodoAccesoCampo;
import dobby.motor.parser.NodoAsignacionCampo;
import dobby.motor.parser.NodoAsignacion;
import dobby.motor.parser.NodoContinuar;
import dobby.motor.parser.NodoDeclaracionVariable;
import dobby.motor.parser.NodoEntrada;
import dobby.motor.parser.NodoFuncion;
import dobby.motor.parser.NodoImpresion;
import dobby.motor.parser.NodoLiteral;
import dobby.motor.parser.NodoLlamada;
import dobby.motor.parser.NodoMientras;
import dobby.motor.parser.NodoOperacionBinaria;
import dobby.motor.parser.NodoOperacionUnaria;
import dobby.motor.parser.NodoPara;
import dobby.motor.parser.NodoRetorno;
import dobby.motor.parser.NodoRomper;
import dobby.motor.parser.NodoSi;
import dobby.motor.parser.NodoVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ValidadorSemantico {
    private static final String TIPO_ENTRADA = "<entrada>";
    private NodoFuncion funcion;
    private int profundidadExpresion;
    private Map<String, NodoEstructura> estructuras = Map.of();
    private String archivo;
    private final Set<String> declaracionesPosibles = new HashSet<>();

    public void validar(NodoFuncion funcion) {
        this.funcion = funcion;
        this.profundidadExpresion = 0;
        this.estructuras = funcion.getEstructuras();
        this.archivo = funcion.getArchivo();
        declaracionesPosibles.clear();
        declaracionesPosibles.addAll(funcion.getParametros().keySet());
        if (funcion.isEsPrincipal() && !funcion.getParametros().isEmpty()) {
            throw error("Hogwarts() no puede recibir parametros", funcion.getLinea());
        }
        Map<String, String> variables = new HashMap<>(funcion.getParametros());
        for (String tipo : variables.values()) {
            validarTipo(tipo, funcion.getLinea());
        }
        if (!funcion.isEsPrincipal()) {
            validarTipo(funcion.getTipoRetorno(), funcion.getLinea());
        }
        validarBloque(funcion.getCuerpo(), variables, 0);
        if (!funcion.isEsPrincipal() && !"Obliviate".equals(funcion.getTipoRetorno())
            && !retornaSiempre(funcion.getCuerpo())) {
            throw error("La funcion '" + funcion.getNombre()
                + "' debe retornar " + funcion.getTipoRetorno() + " con Patronum en todos sus caminos",
                funcion.getLinea());
        }
    }

    public void validarEstructuras(Map<String, NodoEstructura> propias,
                                  Map<String, NodoEstructura> disponibles, String archivo) {
        this.estructuras = disponibles;
        this.archivo = archivo;
        for (NodoEstructura estructura : propias.values()) {
            for (var campo : estructura.getCampos().entrySet()) {
                validarTipo(campo.getValue(), estructura.getLineasCampos().getOrDefault(campo.getKey(), estructura.getLinea()));
            }
        }
    }

    private void validarBloque(List<Nodo> sentencias, Map<String, String> variables, int ciclos) {
        if (sentencias != null) {
            for (Nodo sentencia : sentencias) {
                validarSentencia(sentencia, variables, ciclos);
            }
        }
    }

    private void validarSentencia(Nodo nodo, Map<String, String> variables, int ciclos) {
        switch (nodo) {
            case NodoDeclaracionVariable n -> {
                validarTipo(n.getTipo(), n.getLinea());
                if (declaracionesPosibles.contains(n.getNombre())) {
                    throw error("La variable '" + n.getNombre() + "' ya habia sido declarada", n.getLinea());
                }
                if (n.getValorInicial() != null) {
                    validarValor(n.getTipo(), n.getValorInicial(), variables);
                }
                variables.put(n.getNombre(), n.getTipo());
                declaracionesPosibles.add(n.getNombre());
            }
            case NodoAsignacion n -> validarValor(tipoVariable(n.getNombreVariable(), variables, n.getLinea()),
                n.getExpresion(), variables);
            case NodoAsignacionArreglo n -> validarValor(tipoDe(n.getDestino(), variables),
                n.getExpresion(), variables);
            case NodoAsignacionCampo n -> {
                String tipo = tipoDe(n.getDestino(), variables);
                if (n.getDestino().isLongitudArreglo()) {
                    throw error("La longitud de Gringotts es de solo lectura", n.getDestino().getLinea());
                }
                validarValor(tipo, n.getExpresion(), variables);
            }
            case NodoImpresion n -> tipoDe(n.getExpresion(), variables);
            case NodoLlamada n -> tipoDe(n, variables);
            case NodoRetorno n -> {
                if (funcion.isEsPrincipal()) {
                    throw error("Hogwarts() no puede usar Patronum", n.getLinea());
                }
                validarValor(funcion.getTipoRetorno(), n.getExpresion(), variables);
            }
            case NodoSi n -> {
                comprobarBooleano(tipoDe(n.getCondicion(), variables), n.getLinea());
                Map<String, String> verdadero = new HashMap<>(variables);
                Map<String, String> falso = new HashMap<>(variables);
                Set<String> antes = new HashSet<>(declaracionesPosibles);
                validarBloque(n.getSentenciasSiVerdadero(), verdadero, ciclos);
                Set<String> declaracionesVerdadero = new HashSet<>(declaracionesPosibles);
                declaracionesPosibles.clear();
                declaracionesPosibles.addAll(antes);
                validarBloque(n.getSentenciasSiFalso(), falso, ciclos);
                declaracionesPosibles.addAll(declaracionesVerdadero);
                // Solo quedan disponibles las declaraciones presentes en ambos caminos.
                for (String nombre : verdadero.keySet()) {
                    if (verdadero.get(nombre).equals(falso.get(nombre))) {
                        variables.put(nombre, verdadero.get(nombre));
                    }
                }
            }
            case NodoMientras n -> {
                comprobarBooleano(tipoDe(n.getCondicion(), variables), n.getLinea());
                validarBloque(n.getCuerpo(), new HashMap<>(variables), ciclos + 1);
            }
            case NodoPara n -> {
                if (n.getInicializacion() instanceof NodoAsignacion inicio
                    && !variables.containsKey(inicio.getNombreVariable())) {
                    if (declaracionesPosibles.contains(inicio.getNombreVariable())) {
                        throw error("La variable '" + inicio.getNombreVariable()
                            + "' no ha sido declarada en todos los caminos", inicio.getLinea());
                    }
                    String tipo = tipoDe(inicio.getExpresion(), variables);
                    variables.put(inicio.getNombreVariable(), esTexto(tipo) ? "Texto" : tipo);
                    declaracionesPosibles.add(inicio.getNombreVariable());
                } else {
                    validarSentencia(n.getInicializacion(), variables, ciclos);
                }
                comprobarBooleano(tipoDe(n.getCondicion(), variables), n.getLinea());
                validarBloque(n.getCuerpo(), new HashMap<>(variables), ciclos + 1);
                validarSentencia(n.getIncremento(), variables, ciclos + 1);
            }
            case NodoRomper n -> validarCiclo("Expelliarmus", ciclos, n.getLinea());
            case NodoContinuar n -> validarCiclo("Reparo", ciclos, n.getLinea());
            default -> throw error("Sentencia no reconocida", nodo.getLinea());
        }
    }

    private String tipoDe(Nodo nodo, Map<String, String> variables) {
        if (profundidadExpresion >= 64) {
            throw error("Se supero el limite de 64 niveles de expresion", nodo.getLinea());
        }
        profundidadExpresion++;
        try {
            return analizarTipo(nodo, variables);
        } finally {
            profundidadExpresion--;
        }
    }

    private String analizarTipo(Nodo nodo, Map<String, String> variables) {
        return switch (nodo) {
            case null -> "Obliviate";
            case NodoLiteral n -> {
                Object valor = n.getValor();
                if (valor == null) yield "Obliviate";
                if (valor instanceof Integer) yield "Entero";
                if (valor instanceof Double) yield "Decimal";
                if (valor instanceof Boolean) yield "Booleano";
                yield ((String) valor).length() == 1 ? "Caracter" : "Texto";
            }
            case NodoVariable n -> tipoVariable(n.getNombre(), variables, n.getLinea());
            case NodoArreglo n -> inferirArreglo(n, variables);
            case NodoCreacionEstructura n -> validarCreacion(n, variables);
            case NodoAccesoCampo n -> tipoCampo(n, variables);
            case NodoAccesoArreglo n -> {
                String elemento = tipoElemento(tipoDe(n.getArreglo(), variables), n.getLinea());
                if (!"Entero".equals(tipoDe(n.getIndice(), variables))) {
                    throw error("El indice de Gringotts debe ser de tipo Entero", n.getLinea());
                }
                yield elemento;
            }
            case NodoLongitud n -> {
                tipoElemento(tipoDe(n.getArreglo(), variables), n.getLinea());
                yield "Entero";
            }
            // Legilimens devuelve texto; la conversion ocurre al asignarlo o pasarlo como argumento.
            case NodoEntrada n -> TIPO_ENTRADA;
            case NodoLlamada n -> validarLlamada(n, variables);
            case NodoOperacionUnaria n -> {
                String tipo = tipoDe(n.getOperando(), variables);
                if (n.getOperador().equals("!")) {
                    comprobarBooleano(tipo, n.getLinea());
                    yield "Booleano";
                }
                comprobarNumero(tipo, n.getLinea());
                yield tipo;
            }
            case NodoOperacionBinaria n -> validarOperacion(n, variables);
            default -> throw error("Expresion no reconocida", nodo.getLinea());
        };
    }

    private String validarLlamada(NodoLlamada llamada, Map<String, String> variables) {
        NodoFuncion destino = funcion.getAmbito().get(llamada.getNombreFuncion());
        if (destino == null) {
            throw error("La funcion '" + llamada.getNombreFuncion()
                + "' no esta declarada ni importada", llamada.getLinea());
        }
        List<String> tipos = new ArrayList<>(destino.getParametros().values());
        if (llamada.getArgumentos().size() != tipos.size()) {
            throw error("La funcion '" + destino.getNombre() + "' espera " + tipos.size()
                + " argumento(s) pero recibio " + llamada.getArgumentos().size(), llamada.getLinea());
        }
        for (int i = 0; i < tipos.size(); i++) {
            if (llamada.getArgumentos().get(i) instanceof NodoArreglo && esArreglo(tipos.get(i))) {
                validarValor(tipos.get(i), llamada.getArgumentos().get(i), variables);
                continue;
            }
            String recibido = tipoDe(llamada.getArgumentos().get(i), variables);
            if (!esCompatible(tipos.get(i), recibido)) {
                throw error("El argumento " + (i + 1) + " de '" + destino.getNombre()
                    + "' debe ser de tipo " + tipos.get(i) + " pero recibio " + recibido, llamada.getLinea());
            }
        }
        return destino.getTipoRetorno();
    }

    private String validarCreacion(NodoCreacionEstructura nodo, Map<String, String> variables) {
        NodoEstructura estructura = estructuras.get(nodo.getNombre());
        if (estructura == null) {
            throw error("Estructura Varita desconocida: " + nodo.getNombre(), nodo.getLinea());
        }
        for (var campo : nodo.getCampos().entrySet()) {
            String tipo = estructura.getCampos().get(campo.getKey());
            if (tipo == null) {
                throw error("La estructura '" + nodo.getNombre() + "' no tiene el campo '"
                    + campo.getKey() + "'", campo.getValue().getLinea());
            }
            validarValor(tipo, campo.getValue(), variables);
        }
        for (String campo : estructura.getCampos().keySet()) {
            if (!nodo.getCampos().containsKey(campo)) {
                throw error("Falta el campo '" + campo + "' al crear " + nodo.getNombre(), nodo.getLinea());
            }
        }
        nodo.setDefinicion(estructura);
        return estructura.getNombre();
    }

    private String tipoCampo(NodoAccesoCampo nodo, Map<String, String> variables) {
        String tipo = tipoDe(nodo.getEstructura(), variables);
        nodo.setLongitudArreglo(false);
        if (esArreglo(tipo) && nodo.getCampo().equals("longitud")) {
            nodo.setLongitudArreglo(true);
            return "Entero";
        }
        NodoEstructura estructura = estructuras.get(tipo);
        if (estructura == null) {
            throw error("Se requiere un arreglo Gringotts para longitud o una estructura Varita para acceder al campo '"
                + nodo.getCampo() + "', pero se recibio " + tipo, nodo.getLinea());
        }
        String campo = estructura.getCampos().get(nodo.getCampo());
        if (campo == null) {
            throw error("La estructura '" + tipo + "' no tiene el campo '" + nodo.getCampo() + "'", nodo.getLinea());
        }
        return campo;
    }

    private String validarOperacion(NodoOperacionBinaria n, Map<String, String> variables) {
        String izquierda = tipoDe(n.getIzquierda(), variables);
        String derecha = tipoDe(n.getDerecha(), variables);
        String operador = n.getOperador();
        if (operador.equals("&&") || operador.equals("||")) {
            comprobarBooleano(izquierda, n.getLinea());
            comprobarBooleano(derecha, n.getLinea());
            return "Booleano";
        }
        if (operador.equals("==") || operador.equals("!=")) {
            return "Booleano";
        }
        if (operador.equals("+") && (esTexto(izquierda) || esTexto(derecha))) {
            return "Texto";
        }
        comprobarNumero(izquierda, n.getLinea());
        comprobarNumero(derecha, n.getLinea());
        if (List.of("<", ">", "<=", ">=").contains(operador)) {
            return "Booleano";
        }
        return izquierda.equals("Decimal") || derecha.equals("Decimal") ? "Decimal" : "Entero";
    }

    private String tipoVariable(String nombre, Map<String, String> variables, int linea) {
        if (!variables.containsKey(nombre)) {
            throw error("La variable '" + nombre + "' no ha sido declarada en este camino", linea);
        }
        return variables.get(nombre);
    }

    private boolean esArreglo(String tipo) {
        return tipo != null && tipo.startsWith("Gringotts<") && tipo.endsWith(">");
    }

    private String tipoElemento(String tipo, int linea) {
        if (!esArreglo(tipo)) {
            throw error("Se requiere un arreglo Gringotts, pero se recibio " + tipo, linea);
        }
        return tipo.substring(10, tipo.length() - 1);
    }

    private void validarValor(String esperado, Nodo valor, Map<String, String> variables) {
        if (valor instanceof NodoArreglo arreglo && esArreglo(esperado)) {
            String elemento = tipoElemento(esperado, arreglo.getLinea());
            arreglo.setTipoElemento(elemento);
            for (Nodo item : arreglo.getElementos()) {
                validarValor(elemento, item, variables);
            }
        } else {
            comprobarTipo(esperado, tipoDe(valor, variables), valor != null ? valor.getLinea() : funcion.getLinea());
        }
    }

    private String inferirArreglo(NodoArreglo arreglo, Map<String, String> variables) {
        String tipo = null;
        for (Nodo elemento : arreglo.getElementos()) {
            String actual = tipoDe(elemento, variables);
            if (actual.equals(TIPO_ENTRADA)) {
                actual = "Texto";
            }
            if (tipo == null || tipo.equals(actual)) {
                tipo = actual;
            } else if ((tipo.equals("Entero") || tipo.equals("Decimal"))
                && (actual.equals("Entero") || actual.equals("Decimal"))) {
                tipo = "Decimal";
            } else if (esTexto(tipo) && esTexto(actual)) {
                tipo = "Texto";
            } else {
                throw error("Los elementos de Gringotts deben tener tipos compatibles: "
                    + tipo + " y " + actual, elemento.getLinea());
            }
        }
        if (tipo == null) {
            tipo = "Obliviate";
        }
        arreglo.setTipoElemento(tipo);
        return "Gringotts<" + tipo + ">";
    }

    private void comprobarTipo(String esperado, String recibido, int linea) {
        if (!esCompatible(esperado, recibido)) {
            throw error("Se esperaba tipo " + esperado + " pero se recibio " + recibido, linea);
        }
    }

    private boolean esCompatible(String esperado, String recibido) {
        return esperado.equals(recibido)
            || esperado.equals("Decimal") && recibido.equals("Entero")
            || esperado.equals("Texto") && recibido.equals("Caracter")
            || recibido.equals(TIPO_ENTRADA) && List.of("Entero", "Decimal", "Texto", "Caracter", "Booleano").contains(esperado);
    }

    private boolean esTexto(String tipo) {
        return tipo.equals("Texto") || tipo.equals("Caracter") || tipo.equals(TIPO_ENTRADA);
    }

    private void comprobarNumero(String tipo, int linea) {
        if (!tipo.equals("Entero") && !tipo.equals("Decimal")) {
            throw error("La operacion requiere un numero, pero recibio " + tipo, linea);
        }
    }

    private void comprobarBooleano(String tipo, int linea) {
        if (!"Booleano".equals(tipo)) {
            throw error("La condicion u operacion logica requiere Booleano, pero recibio " + tipo, linea);
        }
    }

    private void validarTipo(String tipo, int linea) {
        if (tipo != null && (List.of("Entero", "Decimal", "Booleano", "Texto", "Caracter", "Obliviate").contains(tipo)
            || estructuras.containsKey(tipo))) {
            return;
        }
        if (tipo != null && tipo.startsWith("Gringotts<") && tipo.endsWith(">")) {
            validarTipo(tipo.substring(10, tipo.length() - 1), linea);
            return;
        }
        throw error("Tipo de dato desconocido: " + tipo, linea);
    }

    private void validarCiclo(String nombre, int ciclos, int linea) {
        if (ciclos == 0) {
            throw error(nombre + " solo se puede usar dentro de un ciclo", linea);
        }
    }

    private boolean retornaSiempre(List<Nodo> sentencias) {
        if (sentencias == null) return false;
        for (Nodo nodo : sentencias) {
            if (nodo instanceof NodoRetorno) return true;
            if (nodo instanceof NodoSi n && retornaSiempre(n.getSentenciasSiVerdadero())
                && retornaSiempre(n.getSentenciasSiFalso())) return true;
        }
        return false;
    }

    private ErrorEnlace error(String mensaje, int linea) {
        return new ErrorEnlace((archivo != null ? "[" + archivo + "] " : "")
            + mensaje + " (linea " + linea + ")");
    }
}
