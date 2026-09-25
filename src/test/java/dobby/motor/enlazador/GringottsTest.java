package dobby.motor.enlazador;

import dobby.motor.interprete.Interprete;
import dobby.motor.interprete.ResultadoEjecucion;
import dobby.util.ErrorFormatter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GringottsTest {
    private Enlace compilar(String codigo, Map<String, String> archivos) {
        return new Enlazador(ruta -> {
            String texto = archivos.get(ruta.getFileName().toString());
            if (texto == null) throw new NoSuchFileException(ruta.toString());
            return texto;
        }).enlazar(Path.of("principal.dobby"), codigo, null);
    }

    private ResultadoEjecucion ejecutar(String codigo) {
        return new Interprete(m -> "12").ejecutar(compilar(codigo, Map.of()).programa());
    }

    private void comprobarSalida(String codigo, String esperado) {
        ResultadoEjecucion resultado = ejecutar(codigo);
        assertTrue(resultado.isExito(), resultado.getErrores().toString());
        assertEquals(esperado, resultado.getSalida().replace("\r\n", "\n").stripTrailing());
    }

    @Test
    void funcionesRecibenYRetornanArreglosPorReferencia() {
        comprobarSalida("""
            Expecto crear(): Gringotts<Entero> { Patronum [1,2]; }
            Expecto modificar(a: Gringotts<Entero>): Gringotts<Entero> {
                a[0] = 10;
                Patronum a;
            }
            Hogwarts() {
                Gringotts<Entero> a = Accio crear();
                Gringotts<Entero> b = Accio modificar(a);
                Revelio a;
                Revelio b == a;
                Revelio Accio crear()[1];
                Revelio Accio crear().longitud;
            }
            """, "[10, 2]\nLumos\n2\n2");
    }

    @Test
    void convierteLiteralesAlTipoDeParametrosYRetornos() {
        comprobarSalida("""
            Expecto mostrar(a: Gringotts<Decimal>): Obliviate { Revelio a; }
            Expecto vacio(): Gringotts<Texto> { Patronum []; }
            Expecto decimales(): Gringotts<Decimal> { Patronum [1,2]; }
            Hogwarts() {
                Accio mostrar([1,2]);
                Accio mostrar([]);
                Revelio Accio vacio();
                Revelio Accio decimales()[0] / 2;
            }
            """, "[1.0, 2.0]\n[]\n[]\n0.5");
    }

    @Test
    void evaluaElIndiceUnaSolaVez() {
        comprobarSalida("""
            Expecto indice(contador: Gringotts<Entero>): Entero {
                contador[0] = contador[0] + 1;
                Patronum 0;
            }
            Hogwarts() {
                Gringotts<Entero> contador = [0];
                Gringotts<Entero> a = [1];
                a[Accio indice(contador)] = 9;
                Revelio contador[0];
                Revelio a[0];
            }
            """, "1\n9");
    }

    @Test
    void validaArreglosEnFuncionesImportadas() {
        String biblioteca = "Expecto primero(a: Gringotts<Entero>): Entero { Patronum a[0]; }";
        String principal = "Floo primero desde \"operaciones.dobby\"; Hogwarts() { Revelio Accio primero([7]); }";
        var enlace = compilar(principal, Map.of("operaciones.dobby", biblioteca));
        assertEquals("7", new Interprete().ejecutar(enlace.programa()).getSalida().trim());
        assertThrows(ErrorEnlace.class, () -> compilar(principal.replace("[7]", "[Lumos]"),
            Map.of("operaciones.dobby", biblioteca)));
    }

    @Test
    void unIndiceInvalidoEnImportacionConservaLaUbicacion() {
        String biblioteca = "Expecto primero(a: Gringotts<Entero>): Entero {\n Patronum a[0];\n}";
        String principal = "Floo primero desde \"operaciones.dobby\"; Hogwarts() { Revelio Accio primero([]); }";
        var resultado = new Interprete().ejecutar(compilar(principal, Map.of("operaciones.dobby", biblioteca)).programa());
        assertFalse(resultado.isExito());
        String error = ErrorFormatter.formatearMensaje("Error de ejecucion", "principal.dobby",
            resultado.getErrores().getFirst());
        assertTrue(error.contains("Archivo: operaciones.dobby"));
        assertTrue(error.contains("Linea: 2"));
        assertTrue(error.contains("fuera de rango"));
    }

    @Test
    void reportaEntradaInvalidaAlAsignarUnElemento() {
        var programa = compilar("Hogwarts() { Gringotts<Entero> a=[1]; a[0]=Legilimens(); }", Map.of()).programa();
        var resultado = new Interprete(m -> "hola").ejecutar(programa);
        assertFalse(resultado.isExito());
        assertTrue(resultado.getErrores().getFirst().contains("tipo Entero"));
    }

    static Stream<Arguments> validos() {
        return Stream.of(
            Arguments.of("declaracion", "Alohomora a: Gringotts<Entero>; a=[10,20]; Revelio a[0]; Revelio a.longitud;", "10\n2"),
            Arguments.of("abreviada", "Gringotts<Entero> a=[1,2]; a[1]=7; Revelio a;", "[1, 7]"),
            Arguments.of("sin espacios", "Alohomora a:Gringotts<Entero>=[1,2]; Revelio a;", "[1, 2]"),
            Arguments.of("vacio", "Gringotts<Entero> a=[]; Revelio a; Revelio a.longitud;", "[]\n0"),
            Arguments.of("literal directo", "Revelio [1,2,3][1]; Revelio [].longitud;", "2\n0"),
            Arguments.of("precedencia", "Gringotts<Entero> a=[1,2,3]; Revelio -a[1+1]*2;", "-6"),
            Arguments.of("decimales", "Gringotts<Decimal> a=[1,2.5]; a[0]=3; Revelio a; Revelio a[0]/2;", "[3.0, 2.5]\n1.5"),
            Arguments.of("inferencia numerica", "Revelio [1,2.5]; Revelio [1,2.5][0]/2;", "[1.0, 2.5]\n0.5"),
            Arguments.of("textos", "Gringotts<Texto> a=[\"a\",\"hola\"]; a[1]=\"mundo\"; Revelio a;", "[a, mundo]"),
            Arguments.of("caracteres", "Gringotts<Caracter> a=[\"a\",\"b\"]; Revelio a[0]+a[1];", "ab"),
            Arguments.of("booleanos", "Gringotts<Booleano> a=[Lumos,Nox]; a[1]=!a[0]; Revelio a;", "[Lumos, Nox]"),
            Arguments.of("nulos", "Gringotts<Obliviate> a=[Obliviate]; Revelio a;", "[Obliviate]"),
            Arguments.of("entrada", "Gringotts<Entero> a=[Legilimens()]; a[0]=Legilimens(); Revelio a;", "[12]"),
            Arguments.of("anidados", "Gringotts<Gringotts<Entero>> a=[[1,2],[]]; a[0][1]=8; a[1]=[3]; Revelio a; Revelio a[0].longitud;", "[[1, 8], [3]]\n2"),
            Arguments.of("anidados sin espacios", "Alohomora a:Gringotts<Gringotts<Decimal>>=[[1],[2.5]]; Revelio a;", "[[1.0], [2.5]]"),
            Arguments.of("referencia", "Gringotts<Entero> a=[1]; Gringotts<Entero> b=a; b[0]=9; Revelio a; Revelio a==b;", "[9]\nLumos"),
            Arguments.of("reemplazo", "Gringotts<Entero> a=[1]; Gringotts<Entero> b=a; a=[2,3]; Revelio a; Revelio b;", "[2, 3]\n[1]"),
            Arguments.of("for", "Gringotts<Entero> a=[1,2,3]; Alohomora total: Entero=0; Wingardium(i=0;i<a.longitud;i=i+1) { total=total+a[i]; } Revelio total;", "6"),
            Arguments.of("while", "Gringotts<Entero> a=[1,2]; Alohomora i: Entero=0; Imperio(i<a.longitud) { a[i]=a[i]*2; i=i+1; } Revelio a;", "[2, 4]"),
            Arguments.of("posicion como contador", "Gringotts<Entero> a=[9]; Wingardium(a[0]=0;a[0]<3;a[0]=a[0]+1) { Revelio a[0]; }", "0\n1\n2"),
            Arguments.of("arreglo inferido en for", "Wingardium(a=[1];a[0]<2;a[0]=a[0]+1) { Revelio a; }", "[1]"),
            Arguments.of("literales independientes", "Gringotts<Entero> a=[1]; Gringotts<Entero> b=[1]; a[0]=2; Revelio b;", "[1]"),
            Arguments.of("concatenacion", "Gringotts<Entero> a=[1,2]; Revelio \"Valores: \"+a;", "Valores: [1, 2]")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validos")
    void ejecutaArreglos(String nombre, String cuerpo, String esperado) {
        comprobarSalida("Hogwarts() {" + cuerpo + "}", esperado);
    }

    static Stream<Arguments> invalidos() {
        return Stream.of(
            Arguments.of("mezcla", "Gringotts<Entero> a=[1,Lumos];", "tipo Entero"),
            Arguments.of("mezcla sin contexto", "Revelio [1,\"hola\"];", "tipos compatibles"),
            Arguments.of("asignacion escalar", "Gringotts<Entero> a; a=1;", "Gringotts<Entero>"),
            Arguments.of("elemento incompatible", "Gringotts<Entero> a=[1]; a[0]=\"hola\";", "tipo Entero"),
            Arguments.of("caracter largo", "Gringotts<Caracter> a=[\"hola\"];", "tipo Caracter"),
            Arguments.of("indice decimal", "Gringotts<Entero> a=[1]; Revelio a[0.5];", "indice"),
            Arguments.of("indice booleano", "Gringotts<Entero> a=[1]; a[Lumos]=3;", "indice"),
            Arguments.of("indice sin convertir", "Gringotts<Entero> a=[1]; Revelio a[Legilimens()];", "indice"),
            Arguments.of("escalar indexado", "Alohomora a: Entero=1; Revelio a[0];", "Se requiere un arreglo"),
            Arguments.of("longitud de texto", "Revelio \"hola\".longitud;", "Se requiere un arreglo"),
            Arguments.of("longitud de solo lectura", "Gringotts<Entero> a=[1]; a.longitud=3;", "solo lectura"),
            Arguments.of("variable inexistente", "a[0]=1;", "no ha sido declarada"),
            Arguments.of("indice inexistente", "Gringotts<Entero> a=[1]; Revelio a[i];", "no ha sido declarada"),
            Arguments.of("sin covarianza", "Gringotts<Entero> a=[1]; Gringotts<Decimal> b=a;", "Gringotts<Decimal>"),
            Arguments.of("arreglo en escalar", "Alohomora a: Entero=[1];", "tipo Entero"),
            Arguments.of("aritmetica", "Revelio [1]+[2];", "requiere un numero"),
            Arguments.of("condicion", "Protego([Lumos]) {}", "requiere Booleano"),
            Arguments.of("anidado incompatible", "Gringotts<Gringotts<Entero>> a=[[1],[Nox]];", "tipo Entero"),
            Arguments.of("tipo inexistente", "Gringotts<FalsoTipo> a=[];", "Tipo de dato desconocido")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidos")
    void rechazaTiposInvalidos(String nombre, String cuerpo, String esperado) {
        ErrorEnlace error = assertThrows(ErrorEnlace.class,
            () -> compilar("Hogwarts() {" + cuerpo + "}", Map.of()));
        assertEquals("Error semantico", error.getTipo());
        assertTrue(error.getMessage().contains(esperado), error.getMessage());
    }

    static Stream<String> sintaxisInvalida() {
        return Stream.of("Revelio [1 2];", "Revelio [1,2;", "Revelio [1,];",
            "Gringotts<Entero> a=[1]; Revelio a[];");
    }

    @ParameterizedTest
    @MethodSource("sintaxisInvalida")
    void rechazaSintaxisInvalida(String cuerpo) {
        ErrorEnlace error = assertThrows(ErrorEnlace.class,
            () -> compilar("Hogwarts() {" + cuerpo + "}", Map.of()));
        assertEquals("Error de sintaxis", error.getTipo());
    }

    static Stream<Arguments> erroresDeEjecucion() {
        return Stream.of(
            Arguments.of("Gringotts<Entero> a=[1]; Revelio a[-1];", "fuera de rango"),
            Arguments.of("Gringotts<Entero> a=[1]; Revelio a[a.longitud];", "fuera de rango"),
            Arguments.of("Gringotts<Entero> a=[1]; a[1]=2;", "fuera de rango"),
            Arguments.of("Gringotts<Entero> a=[]; Revelio a[0];", "fuera de rango"),
            Arguments.of("Gringotts<Entero> a; Revelio a.longitud;", "no ha sido inicializado"),
            Arguments.of("Gringotts<Entero> a; a[0]=1;", "no ha sido inicializado"),
            Arguments.of("Gringotts<Entero> a=[1]; Alohomora i: Entero; Revelio a[i];", "variable 'i' no ha sido inicializada")
        );
    }

    @ParameterizedTest
    @MethodSource("erroresDeEjecucion")
    void reportaErroresSinExcepcionesDeJava(String cuerpo, String esperado) {
        ResultadoEjecucion resultado = ejecutar("Hogwarts() {\n" + cuerpo + "\n}");
        assertFalse(resultado.isExito());
        String error = resultado.getErrores().getFirst();
        assertTrue(error.contains(esperado), error);
        assertTrue(error.contains("linea 2"));
        assertTrue(error.contains("[principal.dobby]"));
    }
}
