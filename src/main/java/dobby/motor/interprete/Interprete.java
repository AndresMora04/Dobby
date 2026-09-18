package dobby.motor.interprete;

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
import dobby.motor.parser.NodoPrograma;
import dobby.motor.parser.NodoRetorno;
import dobby.motor.parser.NodoRomper;
import dobby.motor.parser.NodoSi;
import dobby.motor.parser.NodoVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Interprete {
    private final Map<String, NodoFuncion> funciones = new HashMap<>();
    private final StringBuilder salida = new StringBuilder();
    private final ProveedorEntrada proveedorEntrada;

    public Interprete() {
        this(mensaje -> "");
    }

    public Interprete(ProveedorEntrada proveedorEntrada) {
        this.proveedorEntrada = proveedorEntrada;
    }

    public ResultadoEjecucion ejecutar(NodoPrograma programa) {
        funciones.clear();
        salida.setLength(0);
        ResultadoEjecucion resultado = new ResultadoEjecucion();
        try {
            registrarFunciones(programa);
            NodoFuncion principal = obtenerFuncionPrincipal();
            invocarFuncion(principal, List.of());
            resultado.setExito(true);
            resultado.setSalida(salida.toString());
            resultado.setErrores(new ArrayList<>());
        } catch (ErrorEjecucion e) {
            resultado.setExito(false);
            resultado.setSalida(salida.toString());
            resultado.setErrores(List.of(e.getMessage()));
        }
        return resultado;
    }

    private void registrarFunciones(NodoPrograma programa) {
        for (Nodo sentencia : programa.getSentencias()) {
            if (sentencia instanceof NodoFuncion funcion) {
                funciones.put(funcion.getNombre(), funcion);
            }
        }
    }

    private NodoFuncion obtenerFuncionPrincipal() {
        for (NodoFuncion funcion : funciones.values()) {
            if (funcion.isEsPrincipal()) {
                return funcion;
            }
        }
        throw new ErrorEjecucion("No se encontro la funcion principal Hogwarts()", 0);
    }

    private Object invocarFuncion(NodoFuncion funcion, List<Object> argumentos) {
        List<String> nombresParametros = new ArrayList<>(funcion.getParametros().keySet());
        List<String> tiposParametros = new ArrayList<>(funcion.getParametros().values());

        if (argumentos.size() != nombresParametros.size()) {
            throw new ErrorEjecucion("La funcion '" + funcion.getNombre() + "' esperaba "
                + nombresParametros.size() + " argumento(s) pero recibio " + argumentos.size(), funcion.getLinea());
        }

        Entorno entorno = new Entorno();
        for (int i = 0; i < nombresParametros.size(); i++) {
            String nombre = nombresParametros.get(i);
            String tipo = tiposParametros.get(i);
            entorno.declarar(nombre, tipo, funcion.getLinea());
            entorno.asignar(nombre, coercionar(argumentos.get(i), tipo), funcion.getLinea());
        }

        try {
            ejecutarBloque(funcion.getCuerpo(), entorno);
        } catch (SenalRetorno senal) {
            return senal.getValor();
        }

        String tipoRetorno = funcion.getTipoRetorno();
        if (tipoRetorno != null && !tipoRetorno.equals("Obliviate") && !funcion.isEsPrincipal()) {
            throw new ErrorEjecucion("La funcion '" + funcion.getNombre() + "' debia retornar un valor de tipo "
                + tipoRetorno + " pero no lo hizo", funcion.getLinea());
        }
        return null;
    }

    private void ejecutarBloque(List<Nodo> sentencias, Entorno entorno) {
        for (Nodo sentencia : sentencias) {
            ejecutarSentencia(sentencia, entorno);
        }
    }

    private void ejecutarInicializacionPara(Nodo inicializacion, Entorno entorno) {
        if (inicializacion instanceof NodoAsignacion asignacion && !entorno.existe(asignacion.getNombreVariable())) {
            Object valor = evaluarExpresion(asignacion.getExpresion(), entorno);
            entorno.declarar(asignacion.getNombreVariable(), inferirTipo(valor), asignacion.getLinea());
            entorno.asignar(asignacion.getNombreVariable(), valor, asignacion.getLinea());
        } else {
            ejecutarSentencia(inicializacion, entorno);
        }
    }

    private String inferirTipo(Object valor) {
        if (valor instanceof Integer) {
            return "Entero";
        }
        if (valor instanceof Double) {
            return "Decimal";
        }
        if (valor instanceof Boolean) {
            return "Booleano";
        }
        if (valor instanceof String) {
            return "Texto";
        }
        return null;
    }

    private void ejecutarSentencia(Nodo nodo, Entorno entorno) {
        switch (nodo) {
            case NodoDeclaracionVariable n -> {
                entorno.declarar(n.getNombre(), n.getTipo(), n.getLinea());
                if (n.getValorInicial() != null) {
                    Object valor = evaluarExpresion(n.getValorInicial(), entorno);
                    entorno.asignar(n.getNombre(), coercionar(valor, n.getTipo()), n.getLinea());
                }
            }
            case NodoAsignacion n -> {
                Object valor = evaluarExpresion(n.getExpresion(), entorno);
                String tipoDeclarado = entorno.obtenerTipo(n.getNombreVariable());
                entorno.asignar(n.getNombreVariable(), coercionar(valor, tipoDeclarado), n.getLinea());
            }
            case NodoImpresion n -> {
                Object valor = evaluarExpresion(n.getExpresion(), entorno);
                salida.append(convertirATexto(valor)).append(System.lineSeparator());
            }
            case NodoRetorno n -> {
                Object valor = n.getExpresion() != null ? evaluarExpresion(n.getExpresion(), entorno) : null;
                throw new SenalRetorno(valor);
            }
            case NodoSi n -> {
                if (esVerdadero(evaluarExpresion(n.getCondicion(), entorno), n.getLinea())) {
                    ejecutarBloque(n.getSentenciasSiVerdadero(), entorno);
                } else if (n.getSentenciasSiFalso() != null) {
                    ejecutarBloque(n.getSentenciasSiFalso(), entorno);
                }
            }
            case NodoMientras n -> {
                while (esVerdadero(evaluarExpresion(n.getCondicion(), entorno), n.getLinea())) {
                    try {
                        ejecutarBloque(n.getCuerpo(), entorno);
                    } catch (SenalRomper romper) {
                        break;
                    } catch (SenalContinuar continuar) {
                    }
                }
            }
            case NodoPara n -> {
                ejecutarInicializacionPara(n.getInicializacion(), entorno);
                while (esVerdadero(evaluarExpresion(n.getCondicion(), entorno), n.getLinea())) {
                    try {
                        ejecutarBloque(n.getCuerpo(), entorno);
                    } catch (SenalRomper romper) {
                        break;
                    } catch (SenalContinuar continuar) {
                    }
                    ejecutarSentencia(n.getIncremento(), entorno);
                }
            }
            case NodoLlamada n -> evaluarLlamada(n, entorno);
            case NodoRomper n -> throw new SenalRomper();
            case NodoContinuar n -> throw new SenalContinuar();
            default -> throw new ErrorEjecucion("Sentencia no reconocida por el interprete", nodo.getLinea());
        }
    }

    private Object evaluarExpresion(Nodo nodo, Entorno entorno) {
        return switch (nodo) {
            case NodoLiteral n -> n.getValor();
            case NodoVariable n -> entorno.obtener(n.getNombre(), n.getLinea());
            case NodoOperacionBinaria n -> evaluarBinaria(n, entorno);
            case NodoOperacionUnaria n -> evaluarUnaria(n, entorno);
            case NodoEntrada n -> proveedorEntrada.leer("Ingrese un valor:");
            case NodoLlamada n -> evaluarLlamada(n, entorno);
            default -> throw new ErrorEjecucion("Expresion no reconocida por el interprete", nodo.getLinea());
        };
    }

    private Object evaluarLlamada(NodoLlamada nodo, Entorno entorno) {
        NodoFuncion funcion = funciones.get(nodo.getNombreFuncion());
        if (funcion == null) {
            throw new ErrorEjecucion("La funcion '" + nodo.getNombreFuncion() + "' no esta declarada", nodo.getLinea());
        }
        List<Object> argumentos = new ArrayList<>();
        for (Nodo argumento : nodo.getArgumentos()) {
            argumentos.add(evaluarExpresion(argumento, entorno));
        }
        return invocarFuncion(funcion, argumentos);
    }

    private Object evaluarUnaria(NodoOperacionUnaria nodo, Entorno entorno) {
        Object valor = evaluarExpresion(nodo.getOperando(), entorno);
        return switch (nodo.getOperador()) {
            case "-" -> negar(valor, nodo.getLinea());
            case "!" -> !esVerdadero(valor, nodo.getLinea());
            default -> throw new ErrorEjecucion("Operador unario desconocido: " + nodo.getOperador(), nodo.getLinea());
        };
    }

    private Object negar(Object valor, int linea) {
        if (valor instanceof Integer i) {
            return -i;
        }
        if (valor instanceof Double d) {
            return -d;
        }
        throw new ErrorEjecucion("El operador '-' unario requiere un numero", linea);
    }

    private Object evaluarBinaria(NodoOperacionBinaria nodo, Entorno entorno) {
        String operador = nodo.getOperador();

        if (operador.equals("&&") || operador.equals("||")) {
            boolean izquierda = esVerdadero(evaluarExpresion(nodo.getIzquierda(), entorno), nodo.getLinea());
            if (operador.equals("&&") && !izquierda) {
                return false;
            }
            if (operador.equals("||") && izquierda) {
                return true;
            }
            return esVerdadero(evaluarExpresion(nodo.getDerecha(), entorno), nodo.getLinea());
        }

        Object izquierda = evaluarExpresion(nodo.getIzquierda(), entorno);
        Object derecha = evaluarExpresion(nodo.getDerecha(), entorno);
        int linea = nodo.getLinea();

        return switch (operador) {
            case "+" -> sumar(izquierda, derecha, linea);
            case "-" -> aritmetica(izquierda, derecha, linea, (a, b) -> a - b, (a, b) -> a - b);
            case "*" -> aritmetica(izquierda, derecha, linea, (a, b) -> a * b, (a, b) -> a * b);
            case "/" -> dividir(izquierda, derecha, linea);
            case "==" -> Objects.equals(izquierda, derecha);
            case "!=" -> !Objects.equals(izquierda, derecha);
            case ">" -> comparar(izquierda, derecha, linea) > 0;
            case "<" -> comparar(izquierda, derecha, linea) < 0;
            case ">=" -> comparar(izquierda, derecha, linea) >= 0;
            case "<=" -> comparar(izquierda, derecha, linea) <= 0;
            default -> throw new ErrorEjecucion("Operador desconocido: " + operador, linea);
        };
    }

    private Object sumar(Object izquierda, Object derecha, int linea) {
        if (izquierda instanceof String || derecha instanceof String) {
            return convertirATexto(izquierda) + convertirATexto(derecha);
        }
        return aritmetica(izquierda, derecha, linea, Integer::sum, Double::sum);
    }

    private interface OperacionEntera {
        int aplicar(int a, int b);
    }

    private interface OperacionDecimal {
        double aplicar(double a, double b);
    }

    private Object aritmetica(Object izquierda, Object derecha, int linea, OperacionEntera opEntera, OperacionDecimal opDecimal) {
        if (izquierda instanceof Integer a && derecha instanceof Integer b) {
            return opEntera.aplicar(a, b);
        }
        if (izquierda instanceof Number a && derecha instanceof Number b) {
            return opDecimal.aplicar(a.doubleValue(), b.doubleValue());
        }
        throw new ErrorEjecucion("Operacion aritmetica invalida entre " + tipoDeValor(izquierda) + " y " + tipoDeValor(derecha), linea);
    }

    private Object dividir(Object izquierda, Object derecha, int linea) {
        if (!(izquierda instanceof Number) || !(derecha instanceof Number)) {
            throw new ErrorEjecucion("La division requiere numeros", linea);
        }
        double divisor = ((Number) derecha).doubleValue();
        if (divisor == 0.0) {
            throw new ErrorEjecucion("Division entre cero", linea);
        }
        if (izquierda instanceof Integer a && derecha instanceof Integer b) {
            return a / b;
        }
        return ((Number) izquierda).doubleValue() / divisor;
    }

    private int comparar(Object izquierda, Object derecha, int linea) {
        if (izquierda instanceof Number a && derecha instanceof Number b) {
            return Double.compare(a.doubleValue(), b.doubleValue());
        }
        throw new ErrorEjecucion("No se pueden comparar " + tipoDeValor(izquierda) + " y " + tipoDeValor(derecha), linea);
    }

    private boolean esVerdadero(Object valor, int linea) {
        if (valor instanceof Boolean b) {
            return b;
        }
        throw new ErrorEjecucion("Se esperaba un valor booleano (Lumos/Nox) en la condicion", linea);
    }

    private Object coercionar(Object valor, String tipoDeclarado) {
        if (!(valor instanceof String texto) || tipoDeclarado == null) {
            return valor;
        }
        return switch (tipoDeclarado) {
            case "Entero" -> Integer.parseInt(texto.trim());
            case "Decimal" -> Double.parseDouble(texto.trim());
            case "Booleano" -> texto.trim().equalsIgnoreCase("Lumos");
            default -> valor;
        };
    }

    private String convertirATexto(Object valor) {
        if (valor == null) {
            return "Obliviate";
        }
        if (valor instanceof Boolean b) {
            return b ? "Lumos" : "Nox";
        }
        return String.valueOf(valor);
    }

    private String tipoDeValor(Object valor) {
        if (valor == null) {
            return "Obliviate";
        }
        return valor.getClass().getSimpleName();
    }
}
