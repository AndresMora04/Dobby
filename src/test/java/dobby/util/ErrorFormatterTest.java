package dobby.util;

import dobby.motor.enlazador.Enlazador;
import dobby.motor.interprete.Interprete;
import dobby.motor.interprete.ResultadoEjecucion;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ErrorFormatterTest {
    @Test
    void muestraUbicacionDeArchivoImportadoYSugerencia() {
        String texto = ErrorFormatter.formatearMensaje("Error semantico", "principal.dobby",
            "[biblioteca.dobby] La variable 'x' no ha sido declarada (linea 4)");
        assertTrue(texto.contains("Archivo: biblioteca.dobby"));
        assertTrue(texto.contains("Linea: 4"));
        assertTrue(texto.contains("Descripcion: La variable 'x'"));
        assertTrue(texto.contains("Posible causa: Declara"));
    }

    @Test
    void conservaElTokenDeUnErrorDeSintaxis() {
        String texto = ErrorFormatter.formatearMensaje("Error de sintaxis", "sin_titulo_1",
            "Se esperaba ';' (linea 3, token '}')");
        assertTrue(texto.contains("Archivo: sin_titulo_1"));
        assertTrue(texto.contains("Linea: 3"));
        assertTrue(texto.contains("(token '}')"));
    }

    @Test
    void noInventaUnaLineaCuandoNoEstaDisponible() {
        String texto = ErrorFormatter.formatear(null, null, 0, null, null);
        assertTrue(texto.contains("Sin titulo"));
        assertTrue(texto.contains("Linea: No disponible"));
        assertFalse(texto.contains("null"));
        assertNotNull(ErrorFormatter.formatearMensaje(null, null, null));
    }

    @Test
    void conservaSalidaAnteriorYUbicacionDeDivisionEntreCero() {
        var programa = new Enlazador(r -> "").enlazar(Path.of("principal.dobby"),
            "Hogwarts() {\n Revelio \"Antes\";\n Revelio 1 / 0;\n}", null).programa();
        ResultadoEjecucion resultado = new Interprete().ejecutar(programa);
        assertFalse(resultado.isExito());
        assertEquals("Antes", resultado.getSalida().trim());
        String texto = ErrorFormatter.formatearMensaje("Error de ejecucion", null, resultado.getErrores().getFirst());
        assertTrue(texto.contains("Archivo: principal.dobby"));
        assertTrue(texto.contains("Linea: 3"));
        assertTrue(texto.contains("divisor sea distinto de cero"));
    }

    @Test
    void unaEntradaInvalidaSeReportaSinLanzarExcepcionDeJava() {
        var programa = new Enlazador(r -> "").enlazar(Path.of("entrada.dobby"),
            "Hogwarts() {\n Alohomora x: Entero;\n x = Legilimens();\n}", null).programa();
        ResultadoEjecucion resultado = new Interprete(m -> "hola").ejecutar(programa);
        assertFalse(resultado.isExito());
        assertTrue(resultado.getErrores().getFirst().contains("[entrada.dobby]"));
        assertTrue(resultado.getErrores().getFirst().contains("linea 3"));
        assertTrue(resultado.getErrores().getFirst().contains("tipo Entero"));
    }

    @Test
    void reportaErrorEnRetornoDeFuncionImportada() {
        var programa = new Enlazador(r -> "Expecto leer(): Entero {\n Patronum Legilimens();\n}")
            .enlazar(Path.of("principal.dobby"),
                "Floo leer desde \"entrada.dobby\"; Hogwarts() { Revelio Accio leer(); }", null).programa();
        ResultadoEjecucion resultado = new Interprete(m -> "hola").ejecutar(programa);
        assertFalse(resultado.isExito());
        assertTrue(resultado.getErrores().getFirst().contains("[entrada.dobby]"));
        assertTrue(resultado.getErrores().getFirst().contains("linea 2"));
    }
}
