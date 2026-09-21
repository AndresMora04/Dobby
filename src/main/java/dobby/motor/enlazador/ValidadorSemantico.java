package dobby.motor.enlazador;

import dobby.motor.parser.Nodo;
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
    private NodoFuncion funcion;
    private final Set<String> declaracionesPosibles = new HashSet<>();

    public void validar(NodoFuncion funcion) {
        this.funcion = funcion;
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
                    comprobarTipo(n.getTipo(), tipoDe(n.getValorInicial(), variables), n.getLinea());
                }
                variables.put(n.getNombre(), n.getTipo());
                declaracionesPosibles.add(n.getNombre());
            }
            case NodoAsignacion n -> comprobarTipo(tipoVariable(n.getNombreVariable(), variables, n.getLinea()),
                tipoDe(n.getExpresion(), variables), n.getLinea());
            case NodoImpresion n -> tipoDe(n.getExpresion(), variables);
            case NodoLlamada n -> tipoDe(n, variables);
            case NodoRetorno n -> {
                if (funcion.isEsPrincipal()) {
                    throw error("Hogwarts() no puede usar Patronum", n.getLinea());
                }
                comprobarTipo(funcion.getTipoRetorno(), tipoDe(n.getExpresion(), variables), n.getLinea());
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
                NodoAsignacion inicio = (NodoAsignacion) n.getInicializacion();
                if (!variables.containsKey(inicio.getNombreVariable())) {
                    if (declaracionesPosibles.contains(inicio.getNombreVariable())) {
                        throw error("La variable '" + inicio.getNombreVariable()
                            + "' no ha sido declarada en todos los caminos", inicio.getLinea());
                    }
                    String tipo = tipoDe(inicio.getExpresion(), variables);
                    variables.put(inicio.getNombreVariable(), esTexto(tipo) ? "Texto" : tipo);
                    declaracionesPosibles.add(inicio.getNombreVariable());
                } else {
                    validarSentencia(inicio, variables, ciclos);
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
            // Legilimens devuelve texto; la conversion ocurre al asignarlo o pasarlo como argumento.
            case NodoEntrada n -> "Entrada";
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
            String recibido = tipoDe(llamada.getArgumentos().get(i), variables);
            if (!esCompatible(tipos.get(i), recibido)) {
                throw error("El argumento " + (i + 1) + " de '" + destino.getNombre()
                    + "' debe ser de tipo " + tipos.get(i) + " pero recibio " + recibido, llamada.getLinea());
            }
        }
        return destino.getTipoRetorno();
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

    private void comprobarTipo(String esperado, String recibido, int linea) {
        if (!esCompatible(esperado, recibido)) {
            throw error("Se esperaba tipo " + esperado + " pero se recibio " + recibido, linea);
        }
    }

    private boolean esCompatible(String esperado, String recibido) {
        return esperado.equals(recibido)
            || esperado.equals("Decimal") && recibido.equals("Entero")
            || esperado.equals("Texto") && recibido.equals("Caracter")
            || recibido.equals("Entrada") && List.of("Entero", "Decimal", "Texto", "Caracter", "Booleano").contains(esperado);
    }

    private boolean esTexto(String tipo) {
        return tipo.equals("Texto") || tipo.equals("Caracter") || tipo.equals("Entrada");
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
        if (tipo != null && List.of("Entero", "Decimal", "Booleano", "Texto", "Caracter", "Obliviate", "Varita").contains(tipo)) {
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
        String archivo = funcion.getArchivo();
        return new ErrorEnlace((archivo != null ? "[" + archivo + "] " : "")
            + mensaje + " (linea " + linea + ")");
    }
}
