package dobby.util;

import java.util.Map;

/**
 * Define el vocabulario de palabras reservadas del lenguaje Dobby y su
 * correspondencia con el concepto de programación estándar que representan.
 * <p>
 * Utilizada por el {@code Lexer} para reconocer palabras reservadas y por
 * el {@code EditorPanel} para un futuro resaltado de sintaxis.
 */
public final class PalabrasReservadas {

    /**
     * Mapa de palabra reservada de Dobby (por ejemplo {@code "Protego"}) a
     * su significado en términos de programación estándar (por ejemplo
     * {@code "IF"}).
     */
    public static final Map<String, String> MAPA = construirMapa();

    private PalabrasReservadas() {
        // Clase de utilidades: no debe instanciarse.
    }

    /**
     * Construye el mapa de palabras reservadas de Dobby.
     * <p>
     * Palabras a incluir: Alohomora, Expecto, Revelio, Accio, Wingardium,
     * Imperio, Protego, Finite, Expelliarmus, Reparo, Lumos, Nox, Hogwarts,
     * Gringotts, Varita.
     *
     * @return el mapa de palabra reservada a su significado estándar
     */
    private static Map<String, String> construirMapa() {
        // TODO: implementar el mapa completo de palabras reservadas
        return null;
    }

    /**
     * Indica si una palabra dada es una palabra reservada de Dobby.
     *
     * @param palabra palabra a verificar
     * @return {@code true} si la palabra es reservada en Dobby
     */
    public static boolean esPalabraReservada(String palabra) {
        // TODO: implementar
        return false;
    }
}
