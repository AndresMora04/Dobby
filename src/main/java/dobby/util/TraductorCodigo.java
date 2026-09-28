package dobby.util;

import dobby.motor.lexer.Lexer;
import dobby.motor.lexer.TipoToken;
import dobby.motor.lexer.Token;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TraductorCodigo {
    private static final Map<String, String> SIMBOLOS = Map.ofEntries(
        Map.entry("(", "abre paréntesis"),
        Map.entry(")", "cierra paréntesis"),
        Map.entry("{", "abre llave"),
        Map.entry("}", "cierra llave"),
        Map.entry("[", "abre corchete"),
        Map.entry("]", "cierra corchete"),
        Map.entry(";", "punto y coma"),
        Map.entry(":", "dos puntos"),
        Map.entry(",", "coma"),
        Map.entry(".", "punto"),
        Map.entry("+", "más"),
        Map.entry("-", "menos"),
        Map.entry("*", "por"),
        Map.entry("/", "entre"),
        Map.entry("==", "igual a"),
        Map.entry("!=", "distinto de"),
        Map.entry(">=", "mayor o igual que"),
        Map.entry("<=", "menor o igual que"),
        Map.entry(">", "mayor que"),
        Map.entry("<", "menor que"),
        Map.entry("&&", "y"),
        Map.entry("||", "o"),
        Map.entry("!", "no"),
        Map.entry("=", "igual")
    );

    private static final Pattern ENTRE_COMILLAS_SIMPLES = Pattern.compile("'([^']*)'");

    private TraductorCodigo() {
    }

    public static String mensajeATextoHablado(String mensaje) {
        if (mensaje == null || mensaje.isBlank()) {
            return mensaje;
        }
        Matcher coincidencia = ENTRE_COMILLAS_SIMPLES.matcher(mensaje);
        StringBuilder resultado = new StringBuilder();
        while (coincidencia.find()) {
            String interior = coincidencia.group(1);
            String reemplazo = SIMBOLOS.getOrDefault(interior, interior);
            coincidencia.appendReplacement(resultado, Matcher.quoteReplacement(reemplazo));
        }
        coincidencia.appendTail(resultado);
        return resultado.toString();
    }

    public static String aTextoHablado(String codigoFuente) {
        return tokensATexto(codigoFuente, -1);
    }

    public static String lineaATextoHablado(String codigoFuente, int numeroLinea) {
        String texto = tokensATexto(codigoFuente, numeroLinea);
        return texto.isEmpty() ? "Línea vacía." : texto;
    }

    private static String tokensATexto(String codigoFuente, int soloLinea) {
        if (codigoFuente == null || codigoFuente.isBlank()) {
            return "";
        }
        List<Token> tokens;
        try {
            tokens = new Lexer().tokenizar(codigoFuente);
        } catch (RuntimeException e) {
            return lineaCruda(codigoFuente, soloLinea);
        }

        StringBuilder resultado = new StringBuilder();
        for (Token token : tokens) {
            if (token.getTipo() == TipoToken.EOF) {
                continue;
            }
            if (soloLinea > 0 && token.getLinea() != soloLinea) {
                continue;
            }
            agregarToken(resultado, token);
        }
        return resultado.toString().trim();
    }

    private static void agregarToken(StringBuilder resultado, Token token) {
        if (!resultado.isEmpty()) {
            resultado.append(" ");
        }
        if (token.getTipo() == TipoToken.SIMBOLO) {
            resultado.append(SIMBOLOS.getOrDefault(token.getValor(), token.getValor()));
        } else if (token.getTipo() == TipoToken.TEXTO) {
            resultado.append("comillas ").append(token.getValor()).append(" comillas");
        } else {
            resultado.append(token.getValor());
        }
    }

    private static String lineaCruda(String codigoFuente, int numeroLinea) {
        if (numeroLinea <= 0) {
            return codigoFuente.trim();
        }
        String[] lineas = codigoFuente.split("\n", -1);
        return numeroLinea <= lineas.length ? lineas[numeroLinea - 1].trim() : "";
    }
}
