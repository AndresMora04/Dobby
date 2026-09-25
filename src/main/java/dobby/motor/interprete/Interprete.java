package dobby.motor.interprete;

import dobby.motor.parser.Nodo;
import dobby.motor.parser.NodoArreglo;
import dobby.motor.parser.NodoAccesoArreglo;
import dobby.motor.parser.NodoAsignacionArreglo;
import dobby.motor.parser.NodoLongitud;
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
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Set;

public class Interprete {
    private static final int MAX_PASOS = 100_000;
    private static final int MAX_LLAMADAS = 64;
    private static final int MAX_TEXTO = 65_536;
    private static final int MAX_SALIDA = 20_000;
    private int pasos;
    private int llamadas;
    private int profundidadExpresion;
    private int ultimaLinea;
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
        pasos = 0;
        llamadas = 0;
        profundidadExpresion = 0;
        ultimaLinea = 1;
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
        llamadas++;
        try {
            return ejecutarFuncion(funcion, argumentos);
        } catch (StackOverflowError e) {
            ErrorEjecucion error = new ErrorEjecucion("Se supero el limite de anidamiento de ejecucion", ultimaLinea);
            error.setArchivo(funcion.getArchivo());
            throw error;
        } finally {
            llamadas--;
        }
    }

    private Object ejecutarFuncion(NodoFuncion funcion, List<Object> argumentos) {
        List<String> nombresParametros = new ArrayList<>(funcion.getParametros().keySet());
        List<String> tiposParametros = new ArrayList<>(funcion.getParametros().values());

        if (argumentos.size() != nombresParametros.size()) {
            throw new ErrorEjecucion("La funcion '" + funcion.getNombre() + "' esperaba "
                + nombresParametros.size() + " argumento(s) pero recibio " + argumentos.size(), funcion.getLinea());
        }

        Entorno entorno = new Entorno(funcion.getAmbito() != null ? funcion.getAmbito() : funciones);
        try {
            for (int i = 0; i < nombresParametros.size(); i++) {
                String nombre = nombresParametros.get(i);
                String tipo = tiposParametros.get(i);
                entorno.declarar(nombre, tipo, funcion.getLinea());
                entorno.asignar(nombre, coercionar(argumentos.get(i), tipo, funcion.getLinea()), funcion.getLinea());
            }
            try {
                ejecutarBloque(funcion.getCuerpo(), entorno);
            } catch (SenalRetorno senal) {
                return coercionar(senal.getValor(), funcion.getTipoRetorno(), senal.getLinea());
            }
        } catch (ErrorEjecucion error) {
            if (error.getArchivo() == null) {
                error.setArchivo(funcion.getArchivo());
            }
            throw error;
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
        if (valor instanceof ValorEstructura estructura) {
            return estructura.getDefinicion().getNombre();
        }
        if (valor instanceof ValorArreglo arreglo) {
            return "Gringotts<" + arreglo.getTipoElemento() + ">";
        }
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
        return "Obliviate";
    }

    private void ejecutarSentencia(Nodo nodo, Entorno entorno) {
        contarPaso(nodo.getLinea());
        switch (nodo) {
            case NodoDeclaracionVariable n -> {
                entorno.declarar(n.getNombre(), n.getTipo(), n.getLinea());
                if (n.getValorInicial() != null) {
                    Object valor = evaluarExpresion(n.getValorInicial(), entorno);
                    entorno.asignar(n.getNombre(), coercionar(valor, n.getTipo(), n.getLinea()), n.getLinea());
                }
            }
            case NodoAsignacion n -> {
                Object valor = evaluarExpresion(n.getExpresion(), entorno);
                String tipoDeclarado = entorno.obtenerTipo(n.getNombreVariable());
                entorno.asignar(n.getNombreVariable(), coercionar(valor, tipoDeclarado, n.getLinea()), n.getLinea());
            }
            case NodoAsignacionArreglo n -> {
                NodoAccesoArreglo destino = n.getDestino();
                ValorArreglo arreglo = obtenerArreglo(destino.getArreglo(), entorno, destino.getLinea());
                int indice = evaluarIndice(destino.getIndice(), entorno);
                arreglo.validarIndice(indice, destino.getLinea());
                Object valor = evaluarExpresion(n.getExpresion(), entorno);
                arreglo.asignar(indice, coercionar(valor, arreglo.getTipoElemento(), n.getLinea()), n.getLinea());
            }
            case NodoAsignacionCampo n -> {
                NodoAccesoCampo destino = n.getDestino();
                if (destino.isLongitudArreglo()) {
                    throw new ErrorEjecucion("La longitud de Gringotts es de solo lectura", destino.getLinea());
                }
                ValorEstructura estructura = obtenerEstructura(destino.getEstructura(), entorno, destino.getLinea());
                String tipo = estructura.tipoCampo(destino.getCampo(), destino.getLinea());
                Object valor = evaluarExpresion(n.getExpresion(), entorno);
                estructura.asignar(destino.getCampo(), coercionar(valor, tipo, n.getLinea()), n.getLinea());
            }
            case NodoImpresion n -> {
                Object valor = evaluarExpresion(n.getExpresion(), entorno);
                String texto = convertirATexto(valor, n.getLinea());
                String salto = System.lineSeparator();
                if (salida.length() + texto.length() + salto.length() > MAX_SALIDA) {
                    throw new ErrorEjecucion("Se supero el limite de " + MAX_SALIDA + " caracteres de salida", n.getLinea());
                }
                salida.append(texto).append(salto);
            }
            case NodoRetorno n -> {
                Object valor = n.getExpresion() != null ? evaluarExpresion(n.getExpresion(), entorno) : null;
                throw new SenalRetorno(valor, n.getLinea());
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
        contarPaso(nodo.getLinea());
        if (profundidadExpresion >= 128) {
            throw new ErrorEjecucion("Se supero el limite de 128 niveles de expresion en ejecucion", nodo.getLinea());
        }
        profundidadExpresion++;
        try {
            Object valor = evaluarValor(nodo, entorno);
            if (valor instanceof String texto && texto.length() > MAX_TEXTO) {
                throw new ErrorEjecucion("Se supero el limite de " + MAX_TEXTO + " caracteres por texto", nodo.getLinea());
            }
            return valor;
        } finally {
            profundidadExpresion--;
        }
    }

    private Object evaluarValor(Nodo nodo, Entorno entorno) {
        return switch (nodo) {
            case NodoLiteral n -> n.getValor();
            case NodoVariable n -> entorno.obtener(n.getNombre(), n.getLinea());
            case NodoArreglo n -> evaluarArreglo(n, entorno);
            case NodoCreacionEstructura n -> evaluarEstructura(n, entorno);
            case NodoAccesoCampo n -> {
                if (n.isLongitudArreglo()) {
                    yield obtenerArreglo(n.getEstructura(), entorno, n.getLinea()).longitud();
                }
                yield obtenerEstructura(n.getEstructura(), entorno, n.getLinea()).obtener(n.getCampo(), n.getLinea());
            }
            case NodoAccesoArreglo n -> {
                ValorArreglo arreglo = obtenerArreglo(n.getArreglo(), entorno, n.getLinea());
                yield arreglo.obtener(evaluarIndice(n.getIndice(), entorno), n.getLinea());
            }
            case NodoLongitud n -> obtenerArreglo(n.getArreglo(), entorno, n.getLinea()).longitud();
            case NodoOperacionBinaria n -> evaluarBinaria(n, entorno);
            case NodoOperacionUnaria n -> evaluarUnaria(n, entorno);
            case NodoEntrada n -> {
                String entrada = proveedorEntrada.leer("Ingrese un valor:");
                if (entrada == null) {
                    throw new ErrorEjecucion("Ejecucion cancelada durante Legilimens", n.getLinea());
                }
                yield entrada;
            }
            case NodoLlamada n -> evaluarLlamada(n, entorno);
            default -> throw new ErrorEjecucion("Expresion no reconocida por el interprete", nodo.getLinea());
        };
    }

    private Object evaluarLlamada(NodoLlamada nodo, Entorno entorno) {
        if (llamadas >= MAX_LLAMADAS) {
            throw new ErrorEjecucion("Se supero el limite de " + MAX_LLAMADAS + " llamadas simultaneas", nodo.getLinea());
        }
        NodoFuncion funcion = entorno.getAmbito().get(nodo.getNombreFuncion());
        if (funcion == null) {
            throw new ErrorEjecucion("La funcion '" + nodo.getNombreFuncion() + "' no esta declarada", nodo.getLinea());
        }
        List<Object> argumentos = new ArrayList<>();
        for (Nodo argumento : nodo.getArgumentos()) {
            argumentos.add(evaluarExpresion(argumento, entorno));
        }
        return invocarFuncion(funcion, argumentos);
    }

    private ValorArreglo evaluarArreglo(NodoArreglo nodo, Entorno entorno) {
        List<Object> elementos = new ArrayList<>();
        for (Nodo elemento : nodo.getElementos()) {
            Object valor = evaluarExpresion(elemento, entorno);
            elementos.add(coercionar(valor, nodo.getTipoElemento(), elemento.getLinea()));
        }
        return new ValorArreglo(nodo.getTipoElemento(), elementos);
    }

    private ValorEstructura evaluarEstructura(NodoCreacionEstructura nodo, Entorno entorno) {
        Map<String, Object> valores = new LinkedHashMap<>();
        for (var campo : nodo.getCampos().entrySet()) {
            String tipo = nodo.getDefinicion().getCampos().get(campo.getKey());
            Object valor = evaluarExpresion(campo.getValue(), entorno);
            valores.put(campo.getKey(), coercionar(valor, tipo, campo.getValue().getLinea()));
        }
        return new ValorEstructura(nodo.getDefinicion(), valores);
    }

    private ValorEstructura obtenerEstructura(Nodo expresion, Entorno entorno, int linea) {
        Object valor = evaluarExpresion(expresion, entorno);
        if (!(valor instanceof ValorEstructura estructura)) {
            throw new ErrorEjecucion(valor == null ? "La estructura Varita no ha sido inicializada"
                : "Se esperaba una estructura Varita", linea);
        }
        return estructura;
    }

    private ValorArreglo obtenerArreglo(Nodo expresion, Entorno entorno, int linea) {
        Object valor = evaluarExpresion(expresion, entorno);
        if (!(valor instanceof ValorArreglo arreglo)) {
            throw new ErrorEjecucion(valor == null ? "El arreglo Gringotts no ha sido inicializado"
                : "Se esperaba un arreglo Gringotts", linea);
        }
        return arreglo;
    }

    private int evaluarIndice(Nodo expresion, Entorno entorno) {
        Object valor = evaluarExpresion(expresion, entorno);
        if (!(valor instanceof Integer indice)) {
            throw new ErrorEjecucion("El indice de Gringotts debe ser de tipo Entero", expresion.getLinea());
        }
        return indice;
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
            try {
                return Math.negateExact(i);
            } catch (ArithmeticException e) {
                throw new ErrorEjecucion("Resultado Entero fuera de rango", linea);
            }
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
            case "-" -> aritmetica(izquierda, derecha, linea, Math::subtractExact, (a, b) -> a - b);
            case "*" -> aritmetica(izquierda, derecha, linea, Math::multiplyExact, (a, b) -> a * b);
            case "/" -> dividir(izquierda, derecha, linea);
            case "==" -> iguales(izquierda, derecha);
            case "!=" -> !iguales(izquierda, derecha);
            case ">" -> comparar(izquierda, derecha, linea) > 0;
            case "<" -> comparar(izquierda, derecha, linea) < 0;
            case ">=" -> comparar(izquierda, derecha, linea) >= 0;
            case "<=" -> comparar(izquierda, derecha, linea) <= 0;
            default -> throw new ErrorEjecucion("Operador desconocido: " + operador, linea);
        };
    }

    private Object sumar(Object izquierda, Object derecha, int linea) {
        if (izquierda instanceof String || derecha instanceof String) {
            String a = convertirATexto(izquierda, linea);
            String b = convertirATexto(derecha, linea);
            if (a.length() + b.length() > MAX_TEXTO) {
                throw new ErrorEjecucion("Se supero el limite de " + MAX_TEXTO + " caracteres por texto", linea);
            }
            return a + b;
        }
        return aritmetica(izquierda, derecha, linea, Math::addExact, Double::sum);
    }

    private boolean iguales(Object izquierda, Object derecha) {
        if (izquierda instanceof Number a && derecha instanceof Number b) {
            return a.doubleValue() == b.doubleValue();
        }
        return Objects.equals(izquierda, derecha);
    }

    private interface OperacionEntera {
        int aplicar(int a, int b);
    }

    private interface OperacionDecimal {
        double aplicar(double a, double b);
    }

    private Object aritmetica(Object izquierda, Object derecha, int linea, OperacionEntera opEntera, OperacionDecimal opDecimal) {
        if (izquierda instanceof Integer a && derecha instanceof Integer b) {
            try {
                return opEntera.aplicar(a, b);
            } catch (ArithmeticException e) {
                throw new ErrorEjecucion("Resultado Entero fuera de rango", linea);
            }
        }
        if (izquierda instanceof Number a && derecha instanceof Number b) {
            return decimalFinito(opDecimal.aplicar(a.doubleValue(), b.doubleValue()), linea);
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
            if (a == Integer.MIN_VALUE && b == -1) {
                throw new ErrorEjecucion("Resultado Entero fuera de rango", linea);
            }
            return a / b;
        }
        return decimalFinito(((Number) izquierda).doubleValue() / divisor, linea);
    }

    private int comparar(Object izquierda, Object derecha, int linea) {
        if (izquierda instanceof Number a && derecha instanceof Number b) {
            if (a.doubleValue() == b.doubleValue()) return 0;
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

    private Object coercionar(Object valor, String tipoDeclarado, int linea) {
        if (valor == null && tipoDeclarado != null && !tipoDeclarado.equals("Obliviate")) {
            throw new ErrorEjecucion("No se recibio un valor de tipo " + tipoDeclarado, linea);
        }
        if (valor instanceof ValorEstructura estructura) {
            if (!estructura.getDefinicion().getNombre().equals(tipoDeclarado)) {
                throw new ErrorEjecucion("Se esperaba un valor de tipo " + tipoDeclarado, linea);
            }
            return valor;
        }
        if (tipoDeclarado != null && tipoDeclarado.startsWith("Gringotts<")) {
            String elemento = tipoDeclarado.substring(10, tipoDeclarado.length() - 1);
            if (!(valor instanceof ValorArreglo arreglo) || !elemento.equals(arreglo.getTipoElemento())) {
                throw new ErrorEjecucion("Se esperaba un arreglo de tipo " + tipoDeclarado, linea);
            }
            return valor;
        }
        if ("Decimal".equals(tipoDeclarado) && valor instanceof Number numero) {
            return numero.doubleValue();
        }
        if (!(valor instanceof String texto) || tipoDeclarado == null) {
            return valor;
        }
        try {
            return switch (tipoDeclarado) {
                case "Entero" -> Integer.parseInt(texto.trim());
                case "Decimal" -> decimalFinito(Double.parseDouble(texto.trim()), linea);
                case "Booleano" -> {
                    if (!texto.trim().equalsIgnoreCase("Lumos") && !texto.trim().equalsIgnoreCase("Nox")) {
                        throw new ErrorEjecucion("El tipo Booleano requiere Lumos o Nox", linea);
                    }
                    yield texto.trim().equalsIgnoreCase("Lumos");
                }
                case "Caracter" -> {
                    if (texto.length() != 1) {
                        throw new ErrorEjecucion("El tipo Caracter requiere un solo caracter", linea);
                    }
                    yield texto;
                }
                default -> valor;
            };
        } catch (NumberFormatException e) {
            throw new ErrorEjecucion("No se pudo convertir la entrada a tipo " + tipoDeclarado, linea);
        }
    }

    private double decimalFinito(double valor, int linea) {
        if (!Double.isFinite(valor)) {
            throw new ErrorEjecucion("Valor Decimal fuera de rango: no se permiten NaN ni infinito", linea);
        }
        return valor;
    }

    private void contarPaso(int linea) {
        ultimaLinea = linea;
        if (Thread.currentThread().isInterrupted()) {
            throw new ErrorEjecucion("Ejecucion interrumpida", linea);
        }
        if (++pasos > MAX_PASOS) {
            throw new ErrorEjecucion("Se supero el limite de " + MAX_PASOS + " pasos de ejecucion", linea);
        }
    }

    private String convertirATexto(Object valor, int linea) {
        StringBuilder texto = new StringBuilder();
        escribirValor(valor, texto, new HashSet<>(), linea);
        return texto.toString();
    }

    private void escribirValor(Object valor, StringBuilder texto, Set<Object> recorrido, int linea) {
        contarPaso(linea);
        // Se limita el recorrido antes de expandir colecciones grandes o referencias circulares.
        if (valor instanceof ValorArreglo || valor instanceof ValorEstructura) {
            if (recorrido.size() >= 64) {
                throw new ErrorEjecucion("Se supero el limite de 64 niveles al mostrar un valor", linea);
            }
            if (!recorrido.add(valor)) {
                agregarTexto(texto, "<ciclo>", linea);
                return;
            }
        }
        if (valor instanceof ValorArreglo arreglo) {
            agregarTexto(texto, "[", linea);
            for (int i = 0; i < arreglo.longitud(); i++) {
                if (i > 0) agregarTexto(texto, ", ", linea);
                escribirValor(arreglo.obtener(i, linea), texto, recorrido, linea);
            }
            agregarTexto(texto, "]", linea);
            recorrido.remove(valor);
        } else if (valor instanceof ValorEstructura estructura) {
            agregarTexto(texto, estructura.getDefinicion().getNombre() + " {", linea);
            boolean primero = true;
            for (String campo : estructura.getDefinicion().getCampos().keySet()) {
                if (!primero) agregarTexto(texto, ", ", linea);
                agregarTexto(texto, campo + ": ", linea);
                escribirValor(estructura.obtener(campo, linea), texto, recorrido, linea);
                primero = false;
            }
            agregarTexto(texto, "}", linea);
            recorrido.remove(valor);
        } else {
            agregarTexto(texto, valor == null ? "Obliviate"
                : valor instanceof Boolean b ? (b ? "Lumos" : "Nox") : String.valueOf(valor), linea);
        }
    }

    private void agregarTexto(StringBuilder destino, String texto, int linea) {
        if (destino.length() + texto.length() > MAX_TEXTO) {
            throw new ErrorEjecucion("Se supero el limite de " + MAX_TEXTO + " caracteres por texto", linea);
        }
        destino.append(texto);
    }

    private String tipoDeValor(Object valor) {
        if (valor == null) {
            return "Obliviate";
        }
        return valor.getClass().getSimpleName();
    }
}
