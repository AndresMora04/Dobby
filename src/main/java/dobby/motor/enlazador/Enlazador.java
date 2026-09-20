package dobby.motor.enlazador;

import dobby.motor.lexer.Lexer;
import dobby.motor.lexer.Token;
import dobby.motor.parser.Nodo;
import dobby.motor.parser.NodoAsignacion;
import dobby.motor.parser.NodoDeclaracionVariable;
import dobby.motor.parser.NodoFuncion;
import dobby.motor.parser.NodoImportacion;
import dobby.motor.parser.NodoImpresion;
import dobby.motor.parser.NodoLiteral;
import dobby.motor.parser.NodoLlamada;
import dobby.motor.parser.NodoMientras;
import dobby.motor.parser.NodoOperacionBinaria;
import dobby.motor.parser.NodoOperacionUnaria;
import dobby.motor.parser.NodoPara;
import dobby.motor.parser.NodoPrograma;
import dobby.motor.parser.NodoRetorno;
import dobby.motor.parser.NodoSi;
import dobby.motor.parser.Parser;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Enlazador {
    private record Modulo(Map<String, NodoFuncion> propias) {
    }

    private final Lexer lexer = new Lexer();
    private final Parser parser = new Parser();
    private final ProveedorCodigo proveedor;
    private final Map<Path, Modulo> cache = new HashMap<>();
    private final List<Path> pila = new ArrayList<>();
    private int tokensEntrada;

    public Enlazador(ProveedorCodigo proveedor) {
        this.proveedor = proveedor;
    }

    public Enlace enlazar(Path rutaEntrada, String codigo, Path carpetaBase) {
        cache.clear();
        pila.clear();
        Path clave = rutaEntrada != null ? normalizar(rutaEntrada) : null;
        Path carpeta = clave != null ? clave.getParent() : carpetaBase;
        String etiqueta = clave != null ? clave.getFileName().toString() : null;
        NodoPrograma programa = parsear(codigo, etiqueta, true);
        construirModulo(programa, clave, etiqueta, carpeta);
        return new Enlace(programa, cache.size(), tokensEntrada);
    }

    private NodoPrograma parsear(String codigo, String etiqueta, boolean esEntrada) {
        try {
            List<Token> tokens = lexer.tokenizar(codigo);
            if (esEntrada) {
                tokensEntrada = Math.max(0, tokens.size() - 1);
            }
            return parser.parsear(tokens);
        } catch (ErrorEnlace e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ErrorEnlace(prefijo(etiqueta) + e.getMessage());
        }
    }

    private Modulo construirModulo(NodoPrograma programa, Path clave, String etiqueta, Path carpeta) {
        Map<String, NodoFuncion> propias = new LinkedHashMap<>();
        List<NodoImportacion> importaciones = new ArrayList<>();
        for (Nodo sentencia : programa.getSentencias()) {
            if (sentencia instanceof NodoFuncion funcion) {
                if (propias.putIfAbsent(funcion.getNombre(), funcion) != null) {
                    throw error(etiqueta, "La funcion '" + funcion.getNombre() + "' esta declarada mas de una vez", funcion.getLinea());
                }
            } else if (sentencia instanceof NodoImportacion importacion) {
                importaciones.add(importacion);
            }
        }

        Map<String, NodoFuncion> ambito = new HashMap<>(propias);
        if (clave != null) {
            pila.add(clave);
        }
        try {
            for (NodoImportacion importacion : importaciones) {
                NodoFuncion importada = resolverImportacion(importacion, etiqueta, carpeta);
                if (ambito.containsKey(importacion.getNombreFuncion())) {
                    throw error(etiqueta, "La funcion '" + importacion.getNombreFuncion()
                        + "' ya esta declarada o importada en este archivo", importacion.getLinea());
                }
                ambito.put(importacion.getNombreFuncion(), importada);
            }
        } finally {
            if (clave != null) {
                pila.remove(pila.size() - 1);
            }
        }

        for (NodoFuncion funcion : propias.values()) {
            funcion.setAmbito(ambito);
            funcion.setArchivo(etiqueta);
        }
        for (NodoFuncion funcion : propias.values()) {
            validarSentencias(funcion.getCuerpo(), ambito, etiqueta);
        }
        return new Modulo(propias);
    }

    private NodoFuncion resolverImportacion(NodoImportacion importacion, String etiqueta, Path carpeta) {
        if (carpeta == null) {
            throw error(etiqueta, "Guarda el archivo o abre un proyecto para poder usar Floo", importacion.getLinea());
        }
        Path ruta;
        try {
            ruta = normalizar(carpeta.resolve(importacion.getArchivo()));
        } catch (InvalidPathException e) {
            throw error(etiqueta, "La ruta '" + importacion.getArchivo() + "' no es valida", importacion.getLinea());
        }
        String nombreArchivo = ruta.getFileName().toString();

        if (pila.contains(ruta)) {
            throw error(etiqueta, "Importacion circular: " + cadenaCircular(ruta), importacion.getLinea());
        }

        Modulo modulo = cache.get(ruta);
        if (modulo == null) {
            String codigo;
            try {
                codigo = proveedor.leer(ruta);
            } catch (NoSuchFileException e) {
                throw error(etiqueta, "El archivo '" + importacion.getArchivo() + "' no existe", importacion.getLinea());
            } catch (IOException e) {
                throw error(etiqueta, "No se pudo leer el archivo '" + importacion.getArchivo() + "'", importacion.getLinea());
            }
            NodoPrograma programa = parsear(codigo, nombreArchivo, false);
            modulo = construirModulo(programa, ruta, nombreArchivo, ruta.getParent());
            cache.put(ruta, modulo);
        }

        NodoFuncion funcion = modulo.propias().get(importacion.getNombreFuncion());
        if (funcion == null) {
            throw error(etiqueta, "El archivo '" + nombreArchivo + "' no declara la funcion '"
                + importacion.getNombreFuncion() + "'", importacion.getLinea());
        }
        if (funcion.isEsPrincipal()) {
            throw error(etiqueta, "No se puede importar la funcion principal Hogwarts", importacion.getLinea());
        }
        return funcion;
    }

    private String cadenaCircular(Path destino) {
        StringBuilder cadena = new StringBuilder();
        boolean dentro = false;
        for (Path ruta : pila) {
            if (ruta.equals(destino)) {
                dentro = true;
            }
            if (dentro) {
                cadena.append(ruta.getFileName()).append(" -> ");
            }
        }
        return cadena.append(destino.getFileName()).toString();
    }

    private void validarSentencias(List<Nodo> sentencias, Map<String, NodoFuncion> ambito, String etiqueta) {
        if (sentencias == null) {
            return;
        }
        for (Nodo sentencia : sentencias) {
            validarNodo(sentencia, ambito, etiqueta);
        }
    }

    private void validarNodo(Nodo nodo, Map<String, NodoFuncion> ambito, String etiqueta) {
        switch (nodo) {
            case null -> {
            }
            case NodoDeclaracionVariable n -> validarNodo(n.getValorInicial(), ambito, etiqueta);
            case NodoAsignacion n -> validarNodo(n.getExpresion(), ambito, etiqueta);
            case NodoImpresion n -> validarNodo(n.getExpresion(), ambito, etiqueta);
            case NodoRetorno n -> validarNodo(n.getExpresion(), ambito, etiqueta);
            case NodoSi n -> {
                validarNodo(n.getCondicion(), ambito, etiqueta);
                validarSentencias(n.getSentenciasSiVerdadero(), ambito, etiqueta);
                validarSentencias(n.getSentenciasSiFalso(), ambito, etiqueta);
            }
            case NodoMientras n -> {
                validarNodo(n.getCondicion(), ambito, etiqueta);
                validarSentencias(n.getCuerpo(), ambito, etiqueta);
            }
            case NodoPara n -> {
                validarNodo(n.getInicializacion(), ambito, etiqueta);
                validarNodo(n.getCondicion(), ambito, etiqueta);
                validarNodo(n.getIncremento(), ambito, etiqueta);
                validarSentencias(n.getCuerpo(), ambito, etiqueta);
            }
            case NodoOperacionBinaria n -> {
                validarNodo(n.getIzquierda(), ambito, etiqueta);
                validarNodo(n.getDerecha(), ambito, etiqueta);
            }
            case NodoOperacionUnaria n -> validarNodo(n.getOperando(), ambito, etiqueta);
            case NodoLlamada n -> validarLlamada(n, ambito, etiqueta);
            default -> {
            }
        }
    }

    private void validarLlamada(NodoLlamada llamada, Map<String, NodoFuncion> ambito, String etiqueta) {
        NodoFuncion funcion = ambito.get(llamada.getNombreFuncion());
        if (funcion == null) {
            throw error(etiqueta, "La funcion '" + llamada.getNombreFuncion() + "' no esta declarada ni importada", llamada.getLinea());
        }
        List<String> tipos = new ArrayList<>(funcion.getParametros().values());
        List<Nodo> argumentos = llamada.getArgumentos();
        if (argumentos.size() != tipos.size()) {
            throw error(etiqueta, "La funcion '" + funcion.getNombre() + "' espera " + tipos.size()
                + " argumento(s) pero recibio " + argumentos.size(), llamada.getLinea());
        }
        for (int i = 0; i < argumentos.size(); i++) {
            Nodo argumento = argumentos.get(i);
            if (argumento instanceof NodoLiteral literal && !esCompatible(tipos.get(i), literal.getValor())) {
                throw error(etiqueta, "El argumento " + (i + 1) + " de '" + funcion.getNombre() + "' debe ser de tipo "
                    + tipos.get(i), llamada.getLinea());
            }
            validarNodo(argumento, ambito, etiqueta);
        }
    }

    private boolean esCompatible(String tipo, Object valor) {
        if (valor == null || tipo == null) {
            return true;
        }
        return switch (tipo) {
            case "Entero" -> valor instanceof Integer;
            case "Decimal" -> valor instanceof Integer || valor instanceof Double;
            case "Booleano" -> valor instanceof Boolean;
            case "Texto", "Caracter" -> valor instanceof String;
            default -> true;
        };
    }

    private Path normalizar(Path ruta) {
        return ruta.toAbsolutePath().normalize();
    }

    private String prefijo(String etiqueta) {
        return etiqueta != null ? "[" + etiqueta + "] " : "";
    }

    private ErrorEnlace error(String etiqueta, String mensaje, int linea) {
        return new ErrorEnlace(prefijo(etiqueta) + mensaje + " (linea " + linea + ")");
    }
}
