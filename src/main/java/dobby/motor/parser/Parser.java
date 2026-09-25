package dobby.motor.parser;

import dobby.motor.lexer.Token;
import dobby.motor.lexer.TipoToken;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private List<Token> tokens;
    private int posicion;

    public NodoPrograma parsear(List<Token> tokens) {
        this.tokens = new ArrayList<>(tokens);
        this.posicion = 0;

        List<Nodo> sentencias = new ArrayList<>();
        while (!finDeTokens()) {
            sentencias.add(declaracionSuperior());
        }

        NodoPrograma programa = new NodoPrograma();
        programa.setSentencias(sentencias);
        return programa;
    }

    private Nodo declaracionSuperior() {
        if (verificar("Varita")) {
            return declaracionEstructura();
        }
        if (verificar("Floo")) {
            return declaracionImportacion();
        }
        if (verificar("Expecto")) {
            return declaracionFuncion(false);
        }
        if (verificar("Hogwarts")) {
            return declaracionFuncion(true);
        }
        throw error("Se esperaba una importacion, una funcion o una estructura Varita");
    }

    private NodoEstructura declaracionEstructura() {
        int linea = consumir("Varita").getLinea();
        String nombre = consumirIdentificador();
        Map<String, String> campos = new LinkedHashMap<>();
        Map<String, Integer> lineasCampos = new LinkedHashMap<>();
        consumir("{");
        while (!verificar("}") && !finDeTokens()) {
            int lineaCampo = actual().getLinea();
            String campo = consumirIdentificador();
            if (campos.containsKey(campo)) {
                throw error("El campo '" + campo + "' esta declarado mas de una vez");
            }
            consumir(":");
            campos.put(campo, tipo());
            lineasCampos.put(campo, lineaCampo);
            consumir(";");
        }
        consumir("}");
        NodoEstructura nodo = new NodoEstructura();
        nodo.setNombre(nombre);
        nodo.setCampos(campos);
        nodo.setLineasCampos(lineasCampos);
        nodo.setLinea(linea);
        return nodo;
    }

    private NodoImportacion declaracionImportacion() {
        int linea = actual().getLinea();
        consumir("Floo");
        String nombreFuncion = consumirIdentificador();
        consumir("desde");
        Token archivo = consumirTipo(TipoToken.TEXTO);
        consumir(";");

        NodoImportacion nodo = new NodoImportacion();
        nodo.setNombreFuncion(nombreFuncion);
        nodo.setArchivo(archivo.getValor());
        nodo.setLinea(linea);
        return nodo;
    }

    private NodoFuncion declaracionFuncion(boolean esPrincipal) {
        int linea = actual().getLinea();
        NodoFuncion nodo = new NodoFuncion();
        nodo.setEsPrincipal(esPrincipal);

        if (esPrincipal) {
            consumir("Hogwarts");
            nodo.setNombre("Hogwarts");
        } else {
            consumir("Expecto");
            nodo.setNombre(consumirIdentificador());
        }

        consumir("(");
        nodo.setParametros(parametros());
        consumir(")");

        if (!esPrincipal) {
            consumir(":");
            nodo.setTipoRetorno(tipo());
        }

        consumir("{");
        nodo.setCuerpo(bloque());
        consumir("}");

        nodo.setLinea(linea);
        return nodo;
    }

    private Map<String, String> parametros() {
        Map<String, String> parametros = new LinkedHashMap<>();
        if (verificar(")")) {
            return parametros;
        }
        do {
            Token tokenNombre = actual();
            String nombre = consumirIdentificador();
            if (parametros.containsKey(nombre)) {
                throw new RuntimeException("El parametro '" + nombre + "' esta declarado mas de una vez"
                    + " (linea " + tokenNombre.getLinea() + ")");
            }
            consumir(":");
            String tipoParametro = tipo();
            parametros.put(nombre, tipoParametro);
        } while (coincide(","));
        return parametros;
    }

    private List<Nodo> bloque() {
        List<Nodo> sentencias = new ArrayList<>();
        while (!verificar("}") && !finDeTokens()) {
            sentencias.add(sentencia());
        }
        return sentencias;
    }

    private Nodo sentencia() {
        if (verificar("Alohomora") || verificar("Gringotts")) {
            return declaracionVariable();
        }
        if (verificar("Revelio")) {
            return impresion();
        }
        if (verificar("Patronum")) {
            return retorno();
        }
        if (verificar("Protego")) {
            return si();
        }
        if (verificar("Imperio")) {
            return mientras();
        }
        if (verificar("Wingardium")) {
            return para();
        }
        if (verificar("Accio")) {
            return sentenciaLlamada();
        }
        if (verificar("Expelliarmus")) {
            return romper();
        }
        if (verificar("Reparo")) {
            return continuar();
        }
        if (actual().getTipo() == TipoToken.IDENTIFICADOR) {
            return asignacion();
        }
        throw error("Sentencia no reconocida");
    }

    private NodoDeclaracionVariable declaracionVariable() {
        int linea = actual().getLinea();
        String nombre;
        String tipoVariable;
        if (coincide("Alohomora")) {
            nombre = consumirIdentificador();
            consumir(":");
            tipoVariable = tipo();
        } else {
            tipoVariable = tipo();
            nombre = consumirIdentificador();
        }
        Nodo valorInicial = coincide("=") ? expresion() : null;
        consumir(";");

        NodoDeclaracionVariable nodo = new NodoDeclaracionVariable();
        nodo.setNombre(nombre);
        nodo.setTipo(tipoVariable);
        nodo.setValorInicial(valorInicial);
        nodo.setLinea(linea);
        return nodo;
    }

    private Nodo asignacion() {
        Nodo nodo = asignacionSinPuntoYComa();
        consumir(";");
        return nodo;
    }

    private Nodo asignacionSinPuntoYComa() {
        int linea = actual().getLinea();
        String nombre = consumirIdentificador();
        NodoVariable variable = new NodoVariable();
        variable.setNombre(nombre);
        variable.setLinea(linea);
        Nodo destino = accesos(variable);
        consumir("=");
        Nodo expresion = expresion();

        if (destino instanceof NodoAccesoArreglo acceso) {
            NodoAsignacionArreglo nodo = new NodoAsignacionArreglo();
            nodo.setDestino(acceso);
            nodo.setExpresion(expresion);
            nodo.setLinea(linea);
            return nodo;
        }
        if (destino instanceof NodoAccesoCampo acceso) {
            NodoAsignacionCampo nodo = new NodoAsignacionCampo();
            nodo.setDestino(acceso);
            nodo.setExpresion(expresion);
            nodo.setLinea(linea);
            return nodo;
        }
        NodoAsignacion nodo = new NodoAsignacion();
        nodo.setNombreVariable(nombre);
        nodo.setExpresion(expresion);
        nodo.setLinea(linea);
        return nodo;
    }

    private NodoImpresion impresion() {
        int linea = actual().getLinea();
        consumir("Revelio");
        Nodo expresion = expresion();
        consumir(";");

        NodoImpresion nodo = new NodoImpresion();
        nodo.setExpresion(expresion);
        nodo.setLinea(linea);
        return nodo;
    }

    private NodoRetorno retorno() {
        int linea = actual().getLinea();
        consumir("Patronum");
        Nodo expresion = expresion();
        consumir(";");

        NodoRetorno nodo = new NodoRetorno();
        nodo.setExpresion(expresion);
        nodo.setLinea(linea);
        return nodo;
    }

    private NodoSi si() {
        int linea = actual().getLinea();
        consumir("Protego");
        consumir("(");
        Nodo condicion = expresion();
        consumir(")");
        consumir("{");
        List<Nodo> sentenciasSiVerdadero = bloque();
        consumir("}");

        List<Nodo> sentenciasSiFalso = null;
        if (coincide("Finite")) {
            consumir("{");
            sentenciasSiFalso = bloque();
            consumir("}");
        }

        NodoSi nodo = new NodoSi();
        nodo.setCondicion(condicion);
        nodo.setSentenciasSiVerdadero(sentenciasSiVerdadero);
        nodo.setSentenciasSiFalso(sentenciasSiFalso);
        nodo.setLinea(linea);
        return nodo;
    }

    private NodoMientras mientras() {
        int linea = actual().getLinea();
        consumir("Imperio");
        consumir("(");
        Nodo condicion = expresion();
        consumir(")");
        consumir("{");
        List<Nodo> cuerpo = bloque();
        consumir("}");

        NodoMientras nodo = new NodoMientras();
        nodo.setCondicion(condicion);
        nodo.setCuerpo(cuerpo);
        nodo.setLinea(linea);
        return nodo;
    }

    private NodoPara para() {
        int linea = actual().getLinea();
        consumir("Wingardium");
        consumir("(");
        Nodo inicializacion = asignacionSinPuntoYComa();
        consumir(";");
        Nodo condicion = expresion();
        consumir(";");
        Nodo incremento = asignacionSinPuntoYComa();
        consumir(")");
        consumir("{");
        List<Nodo> cuerpo = bloque();
        consumir("}");

        NodoPara nodo = new NodoPara();
        nodo.setInicializacion(inicializacion);
        nodo.setCondicion(condicion);
        nodo.setIncremento(incremento);
        nodo.setCuerpo(cuerpo);
        nodo.setLinea(linea);
        return nodo;
    }

    private NodoLlamada sentenciaLlamada() {
        NodoLlamada nodo = llamada();
        consumir(";");
        return nodo;
    }

    private Nodo romper() {
        int linea = actual().getLinea();
        consumir("Expelliarmus");
        consumir(";");
        Nodo nodo = new NodoRomper();
        nodo.setLinea(linea);
        return nodo;
    }

    private Nodo continuar() {
        int linea = actual().getLinea();
        consumir("Reparo");
        consumir(";");
        Nodo nodo = new NodoContinuar();
        nodo.setLinea(linea);
        return nodo;
    }

    private NodoLlamada llamada() {
        int linea = actual().getLinea();
        consumir("Accio");
        String nombreFuncion = consumirIdentificador();
        consumir("(");
        List<Nodo> argumentos = new ArrayList<>();
        if (!verificar(")")) {
            do {
                argumentos.add(expresion());
            } while (coincide(","));
        }
        consumir(")");

        NodoLlamada nodo = new NodoLlamada();
        nodo.setNombreFuncion(nombreFuncion);
        nodo.setArgumentos(argumentos);
        nodo.setLinea(linea);
        return nodo;
    }

    private Nodo expresion() {
        return o();
    }

    private Nodo o() {
        Nodo izquierda = y();
        while (coincide("||")) {
            izquierda = combinar(izquierda, "||", y());
        }
        return izquierda;
    }

    private Nodo y() {
        Nodo izquierda = igualdad();
        while (coincide("&&")) {
            izquierda = combinar(izquierda, "&&", igualdad());
        }
        return izquierda;
    }

    private Nodo igualdad() {
        Nodo izquierda = comparacion();
        while (verificar("==") || verificar("!=")) {
            String operador = avanzar().getValor();
            izquierda = combinar(izquierda, operador, comparacion());
        }
        return izquierda;
    }

    private Nodo comparacion() {
        Nodo izquierda = suma();
        while (verificar(">") || verificar("<") || verificar(">=") || verificar("<=")) {
            String operador = avanzar().getValor();
            izquierda = combinar(izquierda, operador, suma());
        }
        return izquierda;
    }

    private Nodo suma() {
        Nodo izquierda = producto();
        while (verificar("+") || verificar("-")) {
            String operador = avanzar().getValor();
            izquierda = combinar(izquierda, operador, producto());
        }
        return izquierda;
    }

    private Nodo producto() {
        Nodo izquierda = unario();
        while (verificar("*") || verificar("/")) {
            String operador = avanzar().getValor();
            izquierda = combinar(izquierda, operador, unario());
        }
        return izquierda;
    }

    private Nodo unario() {
        if (verificar("!") || verificar("-")) {
            Token operadorToken = avanzar();
            NodoOperacionUnaria nodo = new NodoOperacionUnaria();
            nodo.setOperador(operadorToken.getValor());
            nodo.setOperando(unario());
            nodo.setLinea(operadorToken.getLinea());
            return nodo;
        }
        return postfijo();
    }

    private Nodo postfijo() {
        return accesos(primario());
    }

    private Nodo accesos(Nodo nodo) {
        while (verificar("[") || verificar(".")) {
            if (verificar("[")) {
                nodo = accesoArreglo(nodo);
            } else {
                int linea = actual().getLinea();
                consumir(".");
                NodoAccesoCampo campo = new NodoAccesoCampo();
                campo.setCampo(consumirIdentificador());
                campo.setEstructura(nodo);
                campo.setLinea(linea);
                nodo = campo;
            }
        }
        return nodo;
    }

    private NodoAccesoArreglo accesoArreglo(Nodo arreglo) {
        int linea = actual().getLinea();
        consumir("[");
        Nodo indice = expresion();
        consumir("]");
        NodoAccesoArreglo nodo = new NodoAccesoArreglo();
        nodo.setArreglo(arreglo);
        nodo.setIndice(indice);
        nodo.setLinea(linea);
        return nodo;
    }

    private NodoArreglo literalArreglo() {
        int linea = actual().getLinea();
        consumir("[");
        List<Nodo> elementos = new ArrayList<>();
        if (!verificar("]")) {
            do {
                elementos.add(expresion());
            } while (coincide(","));
        }
        consumir("]");
        NodoArreglo nodo = new NodoArreglo();
        nodo.setElementos(elementos);
        nodo.setLinea(linea);
        return nodo;
    }

    private Nodo combinar(Nodo izquierda, String operador, Nodo derecha) {
        NodoOperacionBinaria nodo = new NodoOperacionBinaria();
        nodo.setIzquierda(izquierda);
        nodo.setOperador(operador);
        nodo.setDerecha(derecha);
        nodo.setLinea(izquierda.getLinea());
        return nodo;
    }

    private Nodo primario() {
        Token t = actual();
        if (verificar("[")) {
            return literalArreglo();
        }

        if (t.getTipo() == TipoToken.NUMERO) {
            avanzar();
            return literalNumero(t);
        }
        if (t.getTipo() == TipoToken.TEXTO) {
            avanzar();
            return literalTexto(t);
        }
        if (verificar("Lumos") || verificar("Nox")) {
            avanzar();
            return literalBooleano(t);
        }
        if (verificar("Obliviate")) {
            avanzar();
            NodoLiteral nodo = new NodoLiteral();
            nodo.setValor(null);
            nodo.setLinea(t.getLinea());
            return nodo;
        }
        if (verificar("Legilimens")) {
            avanzar();
            consumir("(");
            consumir(")");
            NodoEntrada nodo = new NodoEntrada();
            nodo.setLinea(t.getLinea());
            return nodo;
        }
        if (verificar("Accio")) {
            return llamada();
        }
        if (verificar("(")) {
            avanzar();
            Nodo nodo = expresion();
            consumir(")");
            return nodo;
        }
        if (t.getTipo() == TipoToken.IDENTIFICADOR) {
            avanzar();
            if (verificar("{")) {
                return creacionEstructura(t);
            }
            NodoVariable nodo = new NodoVariable();
            nodo.setNombre(t.getValor());
            nodo.setLinea(t.getLinea());
            return nodo;
        }
        throw error("Se esperaba una expresion");
    }

    private NodoCreacionEstructura creacionEstructura(Token nombre) {
        consumir("{");
        Map<String, Nodo> campos = new LinkedHashMap<>();
        if (!verificar("}")) {
            do {
                String campo = consumirIdentificador();
                if (campos.containsKey(campo)) {
                    throw error("El campo '" + campo + "' tiene mas de un valor");
                }
                consumir(":");
                campos.put(campo, expresion());
            } while (coincide(","));
        }
        consumir("}");
        NodoCreacionEstructura nodo = new NodoCreacionEstructura();
        nodo.setNombre(nombre.getValor());
        nodo.setCampos(campos);
        nodo.setLinea(nombre.getLinea());
        return nodo;
    }

    private NodoLiteral literalNumero(Token t) {
        NodoLiteral nodo = new NodoLiteral();
        if (t.getValor().contains(".")) {
            nodo.setValor(Double.parseDouble(t.getValor()));
        } else {
            nodo.setValor(Integer.parseInt(t.getValor()));
        }
        nodo.setLinea(t.getLinea());
        return nodo;
    }

    private NodoLiteral literalTexto(Token t) {
        NodoLiteral nodo = new NodoLiteral();
        nodo.setValor(t.getValor());
        nodo.setLinea(t.getLinea());
        return nodo;
    }

    private NodoLiteral literalBooleano(Token t) {
        NodoLiteral nodo = new NodoLiteral();
        nodo.setValor(t.getValor().equals("Lumos"));
        nodo.setLinea(t.getLinea());
        return nodo;
    }

    private String tipo() {
        Token t = avanzar();
        String texto = t.getValor();
        if (coincide("<")) {
            String interno = tipo();
            // En declaraciones sin espacios, el cierre puede venir unido a la asignacion.
            if (verificar(">=")) {
                tokens.set(posicion, new Token(TipoToken.SIMBOLO, "=", actual().getLinea()));
            } else {
                consumir(">");
            }
            return texto + "<" + interno + ">";
        }
        return texto;
    }

    private String consumirIdentificador() {
        if (actual().getTipo() != TipoToken.IDENTIFICADOR) {
            throw error("Se esperaba un identificador");
        }
        return avanzar().getValor();
    }

    private Token consumirTipo(TipoToken tipoEsperado) {
        if (actual().getTipo() != tipoEsperado) {
            throw error("Se esperaba un token de tipo " + tipoEsperado);
        }
        return avanzar();
    }

    private Token consumir(String valorEsperado) {
        if (verificar(valorEsperado)) {
            return avanzar();
        }
        throw error("Se esperaba '" + valorEsperado + "'");
    }

    private boolean coincide(String... valores) {
        for (String valor : valores) {
            if (verificar(valor)) {
                avanzar();
                return true;
            }
        }
        return false;
    }

    private boolean verificar(String valor) {
        return !finDeTokens() && actual().getTipo() != TipoToken.TEXTO && actual().getValor().equals(valor);
    }

    private boolean finDeTokens() {
        return actual().getTipo() == TipoToken.EOF;
    }

    private Token actual() {
        return tokens.get(posicion);
    }

    private Token avanzar() {
        Token t = tokens.get(posicion);
        if (t.getTipo() != TipoToken.EOF) {
            posicion++;
        }
        return t;
    }

    private RuntimeException error(String mensaje) {
        return new RuntimeException(mensaje + " (linea " + actual().getLinea() + ", token '" + actual().getValor() + "')");
    }
}
