package dobby.motor.lexer;

/**
 * Enumera los distintos tipos de {@link Token} que puede producir el
 * {@link Lexer} al recorrer el código fuente de Dobby.
 */
public enum TipoToken {

    /** Palabra reservada del lenguaje Dobby (Alohomora, Protego, Revelio, etc.). */
    PALABRA_RESERVADA,

    /** Nombre de variable, función u otro identificador definido por el usuario. */
    IDENTIFICADOR,

    /** Literal numérico (entero o decimal). */
    NUMERO,

    /** Literal de texto (cadena de caracteres). */
    TEXTO,

    /** Símbolo o operador (por ejemplo: {@code +}, {@code =}, {@code (}, {@code )}). */
    SIMBOLO,

    /** Marca el final del código fuente. */
    EOF
}
