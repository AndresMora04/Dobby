package dobby.motor.lexer;

import dobby.util.PalabrasReservadas;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private String codigo;
    private final List<Token> tokens = new ArrayList<>();
    private int inicio;
    private int posicion;
    private int linea;
    private int lineaInicio;

    public List<Token> tokenizar(String codigo) {
        if (codigo == null) {
            throw new ErrorLexico("No se recibio codigo para analizar", 1);
        }
        if (codigo.length() > 1_000_000) {
            throw new ErrorLexico("El archivo supera el limite de 1000000 caracteres", 1);
        }
        this.codigo = codigo;
        this.tokens.clear();
        this.inicio = 0;
        this.posicion = 0;
        this.linea = 1;

        while (!finDeArchivo()) {
            inicio = posicion;
            lineaInicio = linea;
            escanearToken();
        }

        tokens.add(new Token(TipoToken.EOF, "", linea));
        return new ArrayList<>(tokens);
    }

    private void escanearToken() {
        char c = avanzar();
        switch (c) {
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                linea++;
                break;
            case '(':
                agregarToken(TipoToken.SIMBOLO, "(");
                break;
            case '[':
            case ']':
            case '.':
                agregarToken(TipoToken.SIMBOLO, String.valueOf(c));
                break;
            case ')':
                agregarToken(TipoToken.SIMBOLO, ")");
                break;
            case '{':
                agregarToken(TipoToken.SIMBOLO, "{");
                break;
            case '}':
                agregarToken(TipoToken.SIMBOLO, "}");
                break;
            case ';':
                agregarToken(TipoToken.SIMBOLO, ";");
                break;
            case ':':
                agregarToken(TipoToken.SIMBOLO, ":");
                break;
            case ',':
                agregarToken(TipoToken.SIMBOLO, ",");
                break;
            case '+':
                agregarToken(TipoToken.SIMBOLO, "+");
                break;
            case '-':
                agregarToken(TipoToken.SIMBOLO, "-");
                break;
            case '*':
                agregarToken(TipoToken.SIMBOLO, "*");
                break;
            case '/':
                if (coincideSiguiente('/')) {
                    while (!finDeArchivo() && mirarActual() != '\n') avanzar();
                } else if (coincideSiguiente('*')) {
                    escanearComentario();
                } else {
                    agregarToken(TipoToken.SIMBOLO, "/");
                }
                break;
            case '=':
                agregarToken(TipoToken.SIMBOLO, coincideSiguiente('=') ? "==" : "=");
                break;
            case '!':
                agregarToken(TipoToken.SIMBOLO, coincideSiguiente('=') ? "!=" : "!");
                break;
            case '>':
                agregarToken(TipoToken.SIMBOLO, coincideSiguiente('=') ? ">=" : ">");
                break;
            case '<':
                agregarToken(TipoToken.SIMBOLO, coincideSiguiente('=') ? "<=" : "<");
                break;
            case '&':
                if (coincideSiguiente('&')) {
                    agregarToken(TipoToken.SIMBOLO, "&&");
                } else {
                    throw error("Operador incompleto: usa && en lugar de &");
                }
                break;
            case '|':
                if (coincideSiguiente('|')) {
                    agregarToken(TipoToken.SIMBOLO, "||");
                } else {
                    throw error("Operador incompleto: usa || en lugar de |");
                }
                break;
            case '"':
                escanearTexto();
                break;
            default:
                if (Character.isDigit(c)) {
                    escanearNumero();
                } else if (Character.isLetter(c) || c == '_') {
                    escanearIdentificador();
                } else if (c != '\uFEFF' || inicio != 0) {
                    throw error("Simbolo no reconocido: '" + c + "'");
                }
                break;
        }
    }

    private void escanearIdentificador() {
        while (Character.isLetterOrDigit(mirarActual()) || mirarActual() == '_') {
            avanzar();
        }
        String texto = codigo.substring(inicio, posicion);
        if (PalabrasReservadas.esPalabraReservada(texto)) {
            agregarToken(TipoToken.PALABRA_RESERVADA, texto);
        } else {
            agregarToken(TipoToken.IDENTIFICADOR, texto);
        }
    }

    private void escanearNumero() {
        while (Character.isDigit(mirarActual())) {
            avanzar();
        }
        if (mirarActual() == '.' && Character.isDigit(mirarSiguiente())) {
            avanzar();
            while (Character.isDigit(mirarActual())) {
                avanzar();
            }
        }
        if (Character.isLetter(mirarActual()) || mirarActual() == '_') {
            throw error("Numero invalido: separa el numero del identificador");
        }
        agregarToken(TipoToken.NUMERO, codigo.substring(inicio, posicion));
    }

    private void escanearTexto() {
        StringBuilder texto = new StringBuilder();
        while (mirarActual() != '"' && !finDeArchivo()) {
            char c = avanzar();
            if (c == '\\') {
                if (finDeArchivo()) throw error("Texto sin comillas de cierre");
                char escape = avanzar();
                texto.append(switch (escape) {
                    case '"' -> '"';
                    case '\\' -> '\\';
                    case 'n' -> '\n';
                    case 'r' -> '\r';
                    case 't' -> '\t';
                    default -> throw new ErrorLexico("Secuencia de escape no reconocida: \\" + escape, linea);
                });
            } else {
                if (c == '\n') linea++;
                texto.append(c);
            }
        }
        if (finDeArchivo()) {
            throw error("Texto sin comillas de cierre");
        }
        avanzar();
        agregarToken(TipoToken.TEXTO, texto.toString());
    }

    private void escanearComentario() {
        while (!finDeArchivo()) {
            if (mirarActual() == '*' && mirarSiguiente() == '/') {
                avanzar();
                avanzar();
                return;
            }
            if (avanzar() == '\n') linea++;
        }
        throw error("Comentario sin cierre */");
    }

    private ErrorLexico error(String mensaje) {
        return new ErrorLexico(mensaje, lineaInicio);
    }

    private boolean finDeArchivo() {
        return posicion >= codigo.length();
    }

    private char avanzar() {
        char c = codigo.charAt(posicion);
        posicion++;
        return c;
    }

    private boolean coincideSiguiente(char esperado) {
        if (finDeArchivo() || codigo.charAt(posicion) != esperado) {
            return false;
        }
        posicion++;
        return true;
    }

    private char mirarActual() {
        if (finDeArchivo()) {
            return '\0';
        }
        return codigo.charAt(posicion);
    }

    private char mirarSiguiente() {
        if (posicion + 1 >= codigo.length()) {
            return '\0';
        }
        return codigo.charAt(posicion + 1);
    }

    private void agregarToken(TipoToken tipo, String valor) {
        if (tokens.size() >= 100_000) {
            throw error("El archivo supera el limite de 100000 tokens");
        }
        tokens.add(new Token(tipo, valor, lineaInicio));
    }
}
