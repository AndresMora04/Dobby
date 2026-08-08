package dobby.motor.lexer;

/**
 * Representa una unidad léxica reconocida por el {@link Lexer} al procesar
 * el código fuente de Dobby: su tipo, el texto/valor original y la línea
 * del código fuente en la que aparece (usada para reportar errores).
 */
public class Token {

    private final TipoToken tipo;
    private final String valor;
    private final int linea;

    /**
     * Crea un nuevo token.
     *
     * @param tipo  tipo del token
     * @param valor valor textual del token tal como aparece en el código fuente
     * @param linea número de línea (1-indexada) donde aparece el token
     */
    public Token(TipoToken tipo, String valor, int linea) {
        this.tipo = tipo;
        this.valor = valor;
        this.linea = linea;
    }

    /**
     * @return el tipo de este token
     */
    public TipoToken getTipo() {
        // TODO: implementar
        return tipo;
    }

    /**
     * @return el valor textual de este token
     */
    public String getValor() {
        // TODO: implementar
        return valor;
    }

    /**
     * @return la línea del código fuente donde aparece este token
     */
    public int getLinea() {
        // TODO: implementar
        return linea;
    }

    /**
     * @return representación legible del token, útil para depuración
     */
    @Override
    public String toString() {
        // TODO: implementar
        return null;
    }
}
