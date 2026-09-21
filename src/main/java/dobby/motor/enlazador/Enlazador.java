package dobby.motor.enlazador;

import dobby.motor.lexer.Lexer;
import dobby.motor.lexer.Token;
import dobby.motor.parser.Nodo;
import dobby.motor.parser.NodoFuncion;
import dobby.motor.parser.NodoImportacion;
import dobby.motor.parser.NodoPrograma;
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
        boolean hayPrincipal = programa.getSentencias().stream()
            .anyMatch(n -> n instanceof NodoFuncion f && f.isEsPrincipal());
        if (!hayPrincipal) {
            throw new ErrorEnlace(prefijo(etiqueta) + "No se encontro la funcion principal Hogwarts()");
        }
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
            throw new ErrorEnlace(e.getTipo(), prefijo(etiqueta) + e.getMessage());
        } catch (RuntimeException e) {
            throw new ErrorEnlace("Error de sintaxis", prefijo(etiqueta) + e.getMessage());
        }
    }

    private Modulo construirModulo(NodoPrograma programa, Path clave, String etiqueta, Path carpeta) {
        Map<String, NodoFuncion> propias = new LinkedHashMap<>();
        List<NodoImportacion> importaciones = new ArrayList<>();
        for (Nodo sentencia : programa.getSentencias()) {
            if (sentencia instanceof NodoFuncion funcion) {
                if (propias.putIfAbsent(funcion.getNombre(), funcion) != null) {
                    throw new ErrorEnlace(prefijo(etiqueta) + "La funcion '" + funcion.getNombre()
                        + "' esta declarada mas de una vez (linea " + funcion.getLinea() + ")");
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
            new ValidadorSemantico().validar(funcion);
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

    private Path normalizar(Path ruta) {
        return ruta.toAbsolutePath().normalize();
    }

    private String prefijo(String etiqueta) {
        return etiqueta != null ? "[" + etiqueta + "] " : "";
    }

    private ErrorEnlace error(String etiqueta, String mensaje, int linea) {
        return new ErrorEnlace("Error de importacion", prefijo(etiqueta) + mensaje + " (linea " + linea + ")");
    }
}
