package dobby.motor.enlazador;

import dobby.motor.interprete.Interprete;
import dobby.motor.interprete.ResultadoEjecucion;
import dobby.motor.lexer.ErrorLexico;
import dobby.motor.lexer.Lexer;
import dobby.motor.lexer.TipoToken;
import dobby.motor.parser.Parser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.time.Duration;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RobustezTest {
    private Enlace compilar(String codigo) {
        return new Enlazador(r -> "").enlazar(Path.of("principal.dobby"), codigo, null);
    }

    private ResultadoEjecucion ejecutar(String cuerpo) {
        return new Interprete().ejecutar(compilar("Hogwarts() {\n" + cuerpo + "\n}").programa());
    }

    private void comprobarSalida(String cuerpo, String esperado) {
        var resultado = ejecutar(cuerpo);
        assertTrue(resultado.isExito(), resultado.getErrores().toString());
        assertEquals(esperado, resultado.getSalida().replace("\r\n", "\n").stripTrailing());
    }

    static Stream<Arguments> erroresLexicos() {
        return Stream.of(
            Arguments.of("Revelio 1; @", "Simbolo no reconocido"),
            Arguments.of("Revelio Lumos & Nox;", "usa &&"),
            Arguments.of("Revelio Lumos | Nox;", "usa ||"),
            Arguments.of("Revelio 'hola';", "Simbolo no reconocido"),
            Arguments.of("Revelio \"sin cerrar", "comillas de cierre"),
            Arguments.of("Revelio \"hola\\q\";", "escape"),
            Arguments.of("/* comentario", "Comentario sin cierre"),
            Arguments.of("Revelio 123abc;", "Numero invalido"),
            Arguments.of("Revelio 1_000;", "Numero invalido")
        );
    }

    @ParameterizedTest
    @MethodSource("erroresLexicos")
    void rechazaErroresLexicosConUbicacion(String cuerpo, String esperado) {
        ErrorEnlace error = assertThrows(ErrorEnlace.class,
            () -> compilar("Hogwarts() {\n" + cuerpo + "\n}"));
        assertEquals("Error lexico", error.getTipo());
        assertTrue(error.getMessage().contains(esperado), error.getMessage());
        assertTrue(error.getMessage().contains("[principal.dobby]"));
        assertTrue(error.getMessage().contains("linea 2"));
    }

    @Test
    void noIgnoraErroresDespuesDeUnProgramaCompleto() {
        assertThrows(ErrorEnlace.class, () -> compilar("Hogwarts() {} \""));
        assertThrows(ErrorEnlace.class, () -> compilar("Hogwarts() {} @"));
    }

    @Test
    void soportaComentariosSinConfundirDivisionNiTextos() {
        comprobarSalida("""
            // Hogwarts() {} no es otra funcion
            /* comentario con " y {
               y varias lineas */
            Revelio 12 / 3;
            Revelio "/* texto */ // texto";
            """, "4\n/* texto */ // texto");
        assertDoesNotThrow(() -> compilar("Hogwarts() {} // final sin salto"));
        assertDoesNotThrow(() -> compilar("\uFEFFHogwarts() {}"));
    }

    @Test
    void interpretaEscapesYConservaLaLineaInicialDeTextos() {
        comprobarSalida("Revelio \"a\\n\\\"b\\\"\\\\c\\tfin\";", "a\n\"b\"\\c\tfin");
        var tokens = new Lexer().tokenizar("\n\"primera\nsegunda\"\nRevelio");
        assertEquals(TipoToken.TEXTO, tokens.get(0).getTipo());
        assertEquals(2, tokens.get(0).getLinea());
        assertEquals(4, tokens.get(1).getLinea());
    }

    @Test
    void lexerNoModificaListasAnterioresYSeRecuperaTrasUnError() {
        Lexer lexer = new Lexer();
        var anteriores = lexer.tokenizar("Revelio 1;");
        assertThrows(ErrorLexico.class, () -> lexer.tokenizar("@"));
        lexer.tokenizar("Hogwarts() {}");
        assertEquals(4, anteriores.size());
        assertEquals("Revelio", anteriores.getFirst().getValor());
        assertEquals(1, anteriores.getFirst().getLinea());
    }

    @Test
    void conservaErrorLexicoDelArchivoImportado() {
        var enlazador = new Enlazador(r -> "Expecto f(): Obliviate {\n @\n}");
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> enlazador.enlazar(Path.of("principal.dobby"),
            "Floo f desde \"funciones.dobby\"; Hogwarts() {}", null));
        assertEquals("Error lexico", error.getTipo());
        assertTrue(error.getMessage().contains("[funciones.dobby]"));
        assertTrue(error.getMessage().contains("linea 2"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2147483648", "-2147483649", "9999999999999999999999999"})
    void rechazaEnterosLiteralesFueraDeRango(String numero) {
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> compilar("Hogwarts() {\nRevelio " + numero + ";\n}"));
        assertEquals("Error de sintaxis", error.getTipo());
        assertTrue(error.getMessage().contains("Literal numerico fuera de rango"));
        assertTrue(error.getMessage().contains("linea 2"));
    }

    @Test
    void rechazaDecimalLiteralInfinito() {
        ErrorEnlace error = assertThrows(ErrorEnlace.class,
            () -> compilar("Hogwarts() { Revelio " + "9".repeat(400) + ".0; }"));
        assertTrue(error.getMessage().contains("Literal numerico fuera de rango"));
    }

    @Test
    void permiteExtremosEnterosEIgualdadNumericaMixta() {
        comprobarSalida("""
            Revelio -2147483648;
            Revelio 2147483647;
            Revelio 1 == 1.0;
            Revelio 2 != 2.0;
            Revelio -0.0 == 0;
            Revelio -0.0 < 0;
            Revelio -0.0 >= 0;
            """, "-2147483648\n2147483647\nLumos\nNox\nLumos\nNox\nLumos");
    }

    static Stream<Arguments> erroresDeEjecucion() {
        return Stream.of(
            Arguments.of("Revelio 2147483647 + 1;", "Entero fuera de rango"),
            Arguments.of("Revelio -2147483648 - 1;", "Entero fuera de rango"),
            Arguments.of("Revelio 100000 * 100000;", "Entero fuera de rango"),
            Arguments.of("Revelio --2147483648;", "Entero fuera de rango"),
            Arguments.of("Revelio -2147483648 / -1;", "Entero fuera de rango"),
            Arguments.of("Revelio 1 / 0;", "Division entre cero"),
            Arguments.of("Revelio 1.0 / -0.0;", "Division entre cero"),
            Arguments.of("Alohomora x: Entero; Revelio x;", "no ha sido inicializada"),
            Arguments.of("Alohomora x: Booleano; Revelio !x;", "no ha sido inicializada"),
            Arguments.of("Alohomora x: Texto; Revelio x + \"hola\";", "no ha sido inicializada"),
            Arguments.of("Gringotts<Entero> a; Revelio a;", "no ha sido inicializado")
        );
    }

    @ParameterizedTest
    @MethodSource("erroresDeEjecucion")
    void reportaErroresSinPerderSalidaNiUbicacion(String cuerpo, String esperado) {
        var resultado = ejecutar("Revelio \"Antes\";\n" + cuerpo);
        assertFalse(resultado.isExito());
        assertEquals("Antes", resultado.getSalida().trim());
        String error = resultado.getErrores().getFirst();
        assertTrue(error.contains(esperado), error);
        assertTrue(error.contains("[principal.dobby]"), error);
        assertTrue(error.contains("linea 3"), error);
    }

    @ParameterizedTest
    @ValueSource(strings = {"NaN", "Infinity", "-Infinity", "1e309"})
    void rechazaEntradasDecimalesNoFinitas(String entrada) {
        var programa = compilar("Hogwarts() {\nAlohomora x: Decimal=Legilimens();\n}").programa();
        var resultado = new Interprete(m -> entrada).ejecutar(programa);
        assertFalse(resultado.isExito());
        assertTrue(resultado.getErrores().getFirst().contains("Decimal fuera de rango"));
        assertTrue(resultado.getErrores().getFirst().contains("linea 2"));
    }

    @Test
    void rechazaDesbordamientoDecimalEnOperaciones() {
        var programa = compilar("Hogwarts() { Alohomora x: Decimal=Legilimens(); Revelio x * 2; }").programa();
        var resultado = new Interprete(m -> "1e308").ejecutar(programa);
        assertFalse(resultado.isExito());
        assertTrue(resultado.getErrores().getFirst().contains("Decimal fuera de rango"));
    }

    @Test
    void cancelarEntradaNoSeConvierteEnNulo() {
        var programa = compilar("Hogwarts() {\nRevelio \"Antes\";\nRevelio Legilimens();\n}").programa();
        var resultado = new Interprete(m -> null).ejecutar(programa);
        assertFalse(resultado.isExito());
        assertEquals("Antes", resultado.getSalida().trim());
        assertTrue(resultado.getErrores().getFirst().contains("cancelada"));
        assertTrue(resultado.getErrores().getFirst().contains("linea 3"));
    }

    @Test
    void reiniciaDeclaracionesEnCadaIteracion() {
        comprobarSalida("""
            Wingardium(i=0;i<3;i=i+1) {
                Alohomora doble: Entero=i*2;
                Revelio doble;
            }
            """, "0\n2\n4");
    }

    @Test
    void unaDeclaracionSinValorNoReutilizaElDeLaVueltaAnterior() {
        var resultado = ejecutar("""
            Wingardium(i=0;i<2;i=i+1) {
                Alohomora x: Entero;
                Protego(i==0) { x=7; }
                Revelio x;
            }
            """);
        assertFalse(resultado.isExito());
        assertEquals("7", resultado.getSalida().trim());
        assertTrue(resultado.getErrores().getFirst().contains("variable 'x' no ha sido inicializada"));
    }

    @Test
    void nuloSigueSiendoUnValorValidoDeObliviate() {
        comprobarSalida("""
            Alohomora n: Obliviate;
            Revelio n;
            Wingardium(v=Obliviate;Nox;v=Obliviate) {}
            Revelio v;
            """, "Obliviate\nObliviate");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Imperio(Lumos) {}",
        "Imperio(Lumos) { Reparo; }",
        "Wingardium(i=0;Lumos;i=i+1) {}"
    })
    void detieneCiclosInfinitos(String cuerpo) {
        var resultado = assertTimeoutPreemptively(Duration.ofSeconds(3), () -> ejecutar(cuerpo));
        assertFalse(resultado.isExito());
        assertTrue(resultado.getErrores().getFirst().contains("pasos de ejecucion"));
        assertTrue(resultado.getErrores().getFirst().contains("linea 2"));
    }

    @Test
    void detieneRecursionMutuaYSePuedeVolverAEjecutar() {
        var interprete = new Interprete();
        var programa = compilar("""
            Expecto a(): Obliviate { Accio b(); }
            Expecto b(): Obliviate { Accio a(); }
            Hogwarts() { Accio a(); }
            """).programa();
        var resultado = assertTimeoutPreemptively(Duration.ofSeconds(3), () -> interprete.ejecutar(programa));
        assertFalse(resultado.isExito());
        assertTrue(resultado.getErrores().getFirst().contains("llamadas simultaneas"));
        var siguiente = interprete.ejecutar(compilar("Hogwarts() { Revelio 7; }").programa());
        assertTrue(siguiente.isExito());
        assertEquals("7", siguiente.getSalida().trim());
    }

    @Test
    void conservaUbicacionDeLimiteEnFuncionImportada() {
        var programa = new Enlazador(r -> "Expecto infinito(): Obliviate {\nImperio(Lumos) {}\n}")
            .enlazar(Path.of("principal.dobby"),
                "Floo infinito desde \"ciclos.dobby\"; Hogwarts() { Accio infinito(); }", null).programa();
        var resultado = new Interprete().ejecutar(programa);
        assertFalse(resultado.isExito());
        assertTrue(resultado.getErrores().getFirst().contains("[ciclos.dobby]"));
        assertTrue(resultado.getErrores().getFirst().contains("linea 2"));
    }

    @Test
    void limitaSalidaYTextoSinAgotarMemoria() {
        var salida = ejecutar("Imperio(Lumos) { Revelio \"abcdefghij\"; }");
        assertFalse(salida.isExito());
        assertTrue(salida.getSalida().length() <= 20_000);
        assertTrue(salida.getErrores().getFirst().contains("caracteres de salida"));
        var texto = ejecutar("Alohomora t: Texto=\"ab\"; Imperio(Lumos) { t=t+t; }");
        assertFalse(texto.isExito());
        assertTrue(texto.getErrores().getFirst().contains("caracteres por texto"));
    }

    @Test
    void limitaExpansionDeReferenciasCompartidasAlImprimir() {
        var programa = compilar("""
            Varita Rama { hijos: Gringotts<Rama>; }
            Hogwarts() {
                Alohomora r: Rama=Rama {hijos: []};
                Wingardium(i=0;i<30;i=i+1) { r=Rama {hijos: [r,r]}; }
                Revelio r;
            }
            """).programa();
        var resultado = assertTimeoutPreemptively(Duration.ofSeconds(3), () -> new Interprete().ejecutar(programa));
        assertFalse(resultado.isExito());
        assertTrue(resultado.getErrores().getFirst().contains("limite"));
        assertEquals("", resultado.getSalida());
    }

    @Test
    void limitaProfundidadDeCompilacionYSigueFuncionando() {
        String profundo = "Hogwarts() { Revelio " + "(".repeat(100) + "1" + ")".repeat(100) + "; }";
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> compilar(profundo));
        assertTrue(error.getMessage().contains("anidamiento"));
        ErrorEnlace expresion = assertThrows(ErrorEnlace.class,
            () -> compilar("Hogwarts() { Revelio " + "1+".repeat(200) + "1; }"));
        assertTrue(expresion.getMessage().contains("niveles de expresion"));
        Parser parser = new Parser();
        assertThrows(RuntimeException.class, () -> parser.parsear(new Lexer().tokenizar(profundo)));
        assertEquals(1, parser.parsear(new Lexer().tokenizar("Hogwarts() {}")).getSentencias().size());
    }

    @Test
    void limitaTamanoDeFuenteYNumeroDeTokens() {
        assertThrows(ErrorLexico.class, () -> new Lexer().tokenizar(" ".repeat(1_000_001)));
        assertThrows(ErrorLexico.class, () -> new Lexer().tokenizar(";".repeat(100_001)));
    }

    @Test
    void limitaBloquesUnariosTiposYAccesosProfundos() {
        String[] codigos = {
            "Hogwarts() {" + "Protego(Lumos) {".repeat(100) + "}".repeat(100) + "}",
            "Hogwarts() { Revelio " + "!".repeat(100) + "Lumos; }",
            "Hogwarts() { Alohomora a:" + "Gringotts<".repeat(100) + "Entero" + ">".repeat(100) + "; }",
            "Varita R { siguiente: R; } Hogwarts() { Alohomora r: R; Revelio r" + ".siguiente".repeat(100) + "; }"
        };
        for (String codigo : codigos) {
            ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> compilar(codigo));
            assertTrue(error.getMessage().contains("limite"), error.getMessage());
        }
    }

    @Test
    void declaracionesDeRamasSeReinicianDentroDelCiclo() {
        comprobarSalida("""
            Wingardium(i=0;i<2;i=i+1) {
                Protego(i==0) { Alohomora x: Entero=1; Revelio x; }
                Finite { Alohomora x: Texto="fin"; Revelio x; }
            }
            """, "1\nfin");
        assertThrows(ErrorEnlace.class, () -> compilar(
            "Hogwarts() { Alohomora x: Entero; Alohomora x: Entero; }"));
    }

    @Test
    void cortocircuitoNoLeeVariablesSinInicializar() {
        comprobarSalida("""
            Alohomora x: Booleano;
            Revelio Nox && x;
            Revelio Lumos || x;
            """, "Nox\nLumos");
    }

    @Test
    void limiteDeTextoEnEntradaYLiteralesConservaUbicacion() {
        String grande = "a".repeat(65_537);
        var resultado = ejecutar("Revelio \"" + grande + "\";");
        assertFalse(resultado.isExito());
        assertTrue(resultado.getErrores().getFirst().contains("caracteres por texto"));
        var programa = compilar("Hogwarts() {\nRevelio Legilimens();\n}").programa();
        var entrada = new Interprete(m -> grande).ejecutar(programa);
        assertFalse(entrada.isExito());
        assertTrue(entrada.getErrores().getFirst().contains("linea 2"));
    }

    @Test
    void limitaCadenaDeImportacionesSinDesbordarLaPila() {
        Enlazador enlazador = new Enlazador(ruta -> {
            int i = Integer.parseInt(ruta.getFileName().toString().replace(".dobby", ""));
            return "Floo T" + (i+1) + " desde \"" + (i+1) + ".dobby\"; Varita T" + i + " {}";
        });
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> enlazador.enlazar(Path.of("principal.dobby"),
            "Floo T0 desde \"0.dobby\"; Hogwarts() {}", null));
        assertTrue(error.getMessage().contains("niveles de importaciones"));
    }

    @Test
    void respetaInterrupcionSinBorrarla() {
        var programa = compilar("Hogwarts() { Revelio 1; }").programa();
        Thread.currentThread().interrupt();
        try {
            var resultado = new Interprete().ejecutar(programa);
            assertFalse(resultado.isExito());
            assertTrue(resultado.getErrores().getFirst().contains("interrumpida"));
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
    }
}
