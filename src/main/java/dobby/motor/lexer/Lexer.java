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

    public List<Token> tokenizar(String codigo) {
        this.codigo = codigo;
        this.tokens.clear();
        this.inicio = 0;
        this.posicion = 0;
        this.linea = 1;

        while (!finDeArchivo()) {
            inicio = posicion;
            escanearToken();
        }

        tokens.add(new Token(TipoToken.EOF, "", linea));
        return tokens;
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
                agregarToken(TipoToken.SIMBOLO, "/");
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
                }
                break;
            case '|':
                if (coincideSiguiente('|')) {
                    agregarToken(TipoToken.SIMBOLO, "||");
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
        agregarToken(TipoToken.NUMERO, codigo.substring(inicio, posicion));
    }

    private void escanearTexto() {
        while (mirarActual() != '"' && !finDeArchivo()) {
            if (mirarActual() == '\n') {
                linea++;
            }
            avanzar();
        }
        if (finDeArchivo()) {
            return;
        }
        avanzar();
        String valor = codigo.substring(inicio + 1, posicion - 1);
        agregarToken(TipoToken.TEXTO, valor);
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
        tokens.add(new Token(tipo, valor, linea));
    }
}
