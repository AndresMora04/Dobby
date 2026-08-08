package dobby.motor.parser;

import dobby.motor.lexer.Token;

import java.util.List;

/**
 * Analizador sintáctico de Dobby.
 * <p>
 * Toma la lista de {@link Token} producida por el {@code Lexer} y construye
 * el árbol de sintaxis abstracta (AST) del programa, validando que la
 * secuencia de tokens respete la gramática del lenguaje.
 */
public class Parser {

    /**
     * Analiza la lista de tokens recibida y construye el AST del programa.
     *
     * @param tokens lista de tokens producida por el analizador léxico
     * @return el nodo raíz del AST que representa el programa completo
     */
    public NodoPrograma parsear(List<Token> tokens) {
        // TODO: implementar análisis sintáctico
        return null;
    }
}
