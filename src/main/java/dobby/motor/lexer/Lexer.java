package dobby.motor.lexer;

import java.util.List;

/**
 * Analizador léxico de Dobby.
 * <p>
 * Recorre el código fuente carácter por carácter y lo convierte en una
 * lista de {@link Token}, reconociendo palabras reservadas (ver
 * {@code dobby.util.PalabrasReservadas}), identificadores, números,
 * literales de texto y símbolos.
 */
public class Lexer {

    /**
     * Tokeniza el código fuente Dobby recibido.
     *
     * @param codigo código fuente completo de un archivo {@code .dobby}
     * @return la lista de tokens reconocidos, terminada en un token {@link TipoToken#EOF}
     */
    public List<Token> tokenizar(String codigo) {
        // TODO: implementar análisis léxico
        return null;
    }
}
