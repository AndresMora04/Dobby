package dobby.motor.enlazador;

import dobby.motor.interprete.Interprete;
import dobby.motor.interprete.ResultadoEjecucion;
import dobby.vista.EjemplosDobby;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EnlazadorTest {
    private static final Path PRINCIPAL = Path.of("principal.dobby").toAbsolutePath();
    private static final String SUMAR = "Expecto sumar(a: Entero, b: Entero): Entero { Patronum a + b; }";
    private static final String SALUDAR = "Expecto saludar(nombre: Texto): Obliviate { Revelio nombre; }";

    private Enlace compilar(String codigo, Map<String, String> archivos) {
        return new Enlazador(ruta -> {
            String contenido = archivos.get(ruta.getFileName().toString());
            if (contenido == null) throw new NoSuchFileException(ruta.toString());
            return contenido;
        }).enlazar(PRINCIPAL, codigo, null);
    }

    static Stream<Arguments> invalidos() {
        return Stream.of(
            Arguments.of("variable sin declarar", "Hogwarts() { Revelio x; }", "no ha sido declarada"),
            Arguments.of("asignacion sin declarar", "Hogwarts() { x = 1; }", "no ha sido declarada"),
            Arguments.of("variable duplicada", "Hogwarts() { Alohomora x: Entero; Alohomora x: Entero; }", "ya habia sido declarada"),
            Arguments.of("parametro duplicado", "Expecto f(x: Entero, x: Entero): Obliviate {} Hogwarts() {}", "parametro 'x'"),
            Arguments.of("parametro redeclarado", "Expecto f(x: Entero): Obliviate { Alohomora x: Entero; } Hogwarts() {}", "ya habia sido declarada"),
            Arguments.of("tipo desconocido", "Hogwarts() { Alohomora x: Fantasma; }", "Tipo de dato desconocido"),
            Arguments.of("tipo compuesto invalido", "Hogwarts() { Alohomora x: Gringotts<Fantasma>; }", "Tipo de dato desconocido"),
            Arguments.of("asignacion incompatible", "Hogwarts() { Alohomora x: Entero; x = Lumos; }", "tipo Entero"),
            Arguments.of("decimal a entero", "Hogwarts() { Alohomora x: Entero; x = 1.5; }", "tipo Entero"),
            Arguments.of("caracter largo", "Hogwarts() { Alohomora x: Caracter; x = \"hola\"; }", "tipo Caracter"),
            Arguments.of("condicion numerica", "Hogwarts() { Protego (1) {} }", "requiere Booleano"),
            Arguments.of("while con texto", "Hogwarts() { Imperio (\"hola\") {} }", "requiere Booleano"),
            Arguments.of("for con numero", "Hogwarts() { Wingardium(i = 0; 3; i = i + 1) {} }", "requiere Booleano"),
            Arguments.of("logica incompatible", "Hogwarts() { Revelio Lumos && 1; }", "requiere Booleano"),
            Arguments.of("negacion incompatible", "Hogwarts() { Revelio !1; }", "requiere Booleano"),
            Arguments.of("aritmetica incompatible", "Hogwarts() { Revelio \"hola\" - 1; }", "requiere un numero"),
            Arguments.of("comparacion incompatible", "Hogwarts() { Revelio Lumos > 1; }", "requiere un numero"),
            Arguments.of("break fuera de ciclo", "Hogwarts() { Expelliarmus; }", "dentro de un ciclo"),
            Arguments.of("continue fuera de ciclo", "Hogwarts() { Reparo; }", "dentro de un ciclo"),
            Arguments.of("ciclo no se hereda al llamar", "Expecto f(): Obliviate { Reparo; } Hogwarts() { Imperio(Lumos) { Accio f(); } }", "dentro de un ciclo"),
            Arguments.of("funcion inexistente", "Hogwarts() { Accio falta(); }", "no esta declarada ni importada"),
            Arguments.of("cantidad de argumentos", SUMAR + "Hogwarts() { Accio sumar(1); }", "espera 2 argumento"),
            Arguments.of("tipo de variable argumento", SUMAR + "Hogwarts() { Alohomora x: Texto; x = \"hola\"; Accio sumar(x, 1); }", "argumento 1"),
            Arguments.of("tipo de expresion argumento", SUMAR + "Hogwarts() { Accio sumar(1 < 2, 1); }", "argumento 1"),
            Arguments.of("tipo de llamada anidada", SUMAR + "Expecto f(): Texto { Patronum \"hola\"; } Hogwarts() { Accio sumar(Accio f(), 1); }", "argumento 1"),
            Arguments.of("retorno incorrecto", "Expecto f(): Entero { Patronum Lumos; } Hogwarts() {}", "tipo Entero"),
            Arguments.of("retorno faltante", "Expecto f(): Entero {} Hogwarts() {}", "debe retornar"),
            Arguments.of("retorno parcial", "Expecto f(x: Booleano): Entero { Protego(x) { Patronum 1; } } Hogwarts() {}", "todos sus caminos"),
            Arguments.of("retorno vacio incompatible", "Expecto f(): Obliviate { Patronum 1; } Hogwarts() {}", "tipo Obliviate"),
            Arguments.of("retorno principal", "Hogwarts() { Patronum 1; }", "no puede usar Patronum"),
            Arguments.of("sin principal", SUMAR, "principal Hogwarts"),
            Arguments.of("principal duplicada", "Hogwarts() {} Hogwarts() {}", "mas de una vez"),
            Arguments.of("principal con parametros", "Hogwarts(x: Entero) {}", "no puede recibir parametros"),
            Arguments.of("declaracion condicional", "Hogwarts() { Protego(Nox) { Alohomora x: Entero; } Revelio x; }", "no ha sido declarada"),
            Arguments.of("ramas independientes", "Hogwarts() { Protego(Lumos) { Alohomora x: Entero; } Finite { Revelio x; } }", "no ha sido declarada"),
            Arguments.of("declaracion tras rama", "Hogwarts() { Protego(Lumos) { Alohomora x: Entero; } Alohomora x: Entero; }", "ya habia sido declarada"),
            Arguments.of("declaracion dentro de while", "Hogwarts() { Imperio(Nox) { Alohomora x: Entero; } Revelio x; }", "no ha sido declarada"),
            Arguments.of("entrada sin convertir en condicion", "Hogwarts() { Protego(Legilimens()) {} }", "requiere Booleano")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidos")
    void rechazaProgramasInvalidos(String nombre, String codigo, String mensaje) {
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> compilar(codigo, Map.of()));
        assertTrue(error.getMessage().contains(mensaje), error.getMessage());
        assertTrue(error.getMessage().startsWith("[principal.dobby]"), error.getMessage());
    }

    static Stream<Arguments> ejemplos() {
        return Arrays.stream(EjemplosDobby.EJEMPLOS).map(e -> Arguments.of(e.titulo(), e.codigo()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ejemplos")
    void conservaEjemplosDelEditor(String titulo, String fragmento) {
        String codigo;
        if (fragmento.startsWith("Hogwarts")) codigo = fragmento;
        else if (fragmento.startsWith("Expecto")) codigo = fragmento + "Hogwarts() {}";
        else if (fragmento.startsWith("Floo")) codigo = fragmento + "Hogwarts() { Revelio Accio sumar(2, 3); }";
        else codigo = SUMAR + SALUDAR + "Hogwarts() {" + fragmento + "}";
        ResultadoEjecucion resultado = new Interprete(m -> "11")
            .ejecutar(compilar(codigo, Map.of("operaciones.dobby", SUMAR)).programa());
        assertTrue(resultado.isExito(), resultado.getErrores().toString());
    }

    @Test
    void aceptaRetornosCompletosRecursionYConversiones() {
        String codigo = """
            Expecto factorial(n: Entero): Entero {
                Protego(n <= 1) { Patronum 1; }
                Patronum n * Accio factorial(n - 1);
            }
            Expecto elegir(b: Booleano): Entero {
                Protego(b) { Patronum 2; } Finite { Patronum 3; }
            }
            Hogwarts() {
                Alohomora decimal: Decimal;
                decimal = Accio factorial(4);
                Revelio decimal;
                Revelio Accio elegir(Nox);
                Alohomora letra: Caracter;
                letra = "a";
                Alohomora texto: Texto;
                texto = letra;
                Revelio texto;
            }
            """;
        ResultadoEjecucion resultado = new Interprete().ejecutar(compilar(codigo, Map.of()).programa());
        assertTrue(resultado.isExito(), resultado.getErrores().toString());
        assertEquals("24.0\n3\na", resultado.getSalida().replace("\r\n", "\n").stripTrailing());
    }

    @Test
    void infiereTextoEnLaInicializacionAutomaticaDelCiclo() {
        String codigo = "Hogwarts() { Wingardium(texto = Legilimens(); texto == \"a\"; texto = \"fin\")"
            + " { Revelio texto; } Revelio texto; }";
        ResultadoEjecucion resultado = new Interprete(m -> "a").ejecutar(compilar(codigo, Map.of()).programa());
        assertTrue(resultado.isExito(), resultado.getErrores().toString());
        assertEquals("a\nfin", resultado.getSalida().replace("\r\n", "\n").stripTrailing());
    }

    @Test
    void admiteDeclaracionesComunesSinConfundirLasRamas() {
        String codigo = "Hogwarts() { Protego(Nox) { Alohomora x: Entero; x = 1; }"
            + "Finite { Alohomora x: Entero; x = 2; } Revelio x; }";
        ResultadoEjecucion resultado = new Interprete().ejecutar(compilar(codigo, Map.of()).programa());
        assertTrue(resultado.isExito());
        assertEquals("2", resultado.getSalida().trim());
    }

    @Test
    void validaFuncionesImportadasYConservaSuArchivo() {
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> compilar(
            "Floo f desde \"biblioteca.dobby\"; Hogwarts() { Accio f(); }",
            Map.of("biblioteca.dobby", "Expecto f(): Entero {\n Patronum Lumos;\n}")));
        assertEquals("Error semantico", error.getTipo());
        assertTrue(error.getMessage().contains("[biblioteca.dobby]"));
        assertTrue(error.getMessage().contains("linea 2"));
    }

    @Test
    void validaArgumentosDeImportaciones() {
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> compilar(
            "Floo sumar desde \"operaciones.dobby\"; Hogwarts() { Accio sumar(Lumos, 2); }",
            Map.of("operaciones.dobby", SUMAR)));
        assertTrue(error.getMessage().contains("argumento 1"));
    }

    @Test
    void conservaDeteccionDeImportacionesCirculares() {
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> compilar(
            "Floo f desde \"biblioteca.dobby\"; Hogwarts() {}",
            Map.of("biblioteca.dobby", "Floo g desde \"principal.dobby\"; Expecto f(): Obliviate {}")));
        assertEquals("Error de importacion", error.getTipo());
        assertTrue(error.getMessage().contains("Importacion circular"));
    }

    @Test
    void clasificaArchivoFaltanteYSintaxis() {
        ErrorEnlace importacion = assertThrows(ErrorEnlace.class,
            () -> compilar("Floo f desde \"falta.dobby\"; Hogwarts() {}", Map.of()));
        assertEquals("Error de importacion", importacion.getTipo());
        ErrorEnlace sintaxis = assertThrows(ErrorEnlace.class,
            () -> compilar("Hogwarts() { Revelio 1 }", Map.of()));
        assertEquals("Error de sintaxis", sintaxis.getTipo());
    }
}
