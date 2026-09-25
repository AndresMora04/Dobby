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

class VaritaTest {
    private static final String PUNTO = "Varita Punto { x: Entero; y: Decimal; } ";

    private Enlace compilar(String codigo, Map<String, String> archivos) {
        return new Enlazador(ruta -> {
            String texto = archivos.get(ruta.getFileName().toString());
            if (texto == null) throw new NoSuchFileException(ruta.toString());
            return texto;
        }).enlazar(Path.of("principal.dobby"), codigo, null);
    }

    private ResultadoEjecucion ejecutar(String codigo, Map<String, String> archivos) {
        return new Interprete(m -> "12").ejecutar(compilar(codigo, archivos).programa());
    }

    private void comprobarSalida(String codigo, String esperado) {
        comprobarSalida(codigo, Map.of(), esperado);
    }

    private void comprobarSalida(String codigo, Map<String, String> archivos, String esperado) {
        ResultadoEjecucion resultado = ejecutar(codigo, archivos);
        assertTrue(resultado.isExito(), resultado.getErrores().toString());
        assertEquals(esperado, resultado.getSalida().replace("\r\n", "\n").stripTrailing());
    }

    static Stream<Arguments> validos() {
        return Stream.of(
            Arguments.of("crear y modificar",
                "Alohomora p: Punto=Punto {x: 1,y: 2}; p.x=7; Revelio p; Revelio p.y/2;",
                "Punto {x: 7, y: 2.0}\n1.0"),
            Arguments.of("orden de campos",
                "Alohomora p: Punto=Punto {y: 3,x: 2}; Revelio p;", "Punto {x: 2, y: 3.0}"),
            Arguments.of("asignacion posterior",
                "Alohomora p: Punto; p=Punto {x: 1,y: 2}; Revelio p.x;", "1"),
            Arguments.of("acceso directo",
                "Revelio Punto {x: 2,y: 3}.x*4; Revelio -Punto {x: 2,y: 3}.y;", "8\n-3.0"),
            Arguments.of("referencia",
                "Alohomora p: Punto=Punto {x: 1,y: 2}; Alohomora q: Punto=p; q.x=9; Revelio p.x; Revelio p==q;",
                "9\nLumos"),
            Arguments.of("reemplazo",
                "Alohomora p: Punto=Punto {x: 1,y: 2}; Alohomora q: Punto=p; p=Punto {x: 3,y: 4}; Revelio q.x; Revelio p==q;",
                "1\nNox"),
            Arguments.of("instancias independientes",
                "Alohomora p: Punto=Punto {x: 1,y: 2}; Alohomora q: Punto=Punto {x: 1,y: 2}; p.x=9; Revelio q.x; Revelio p!=q;",
                "1\nLumos"),
            Arguments.of("arreglo de estructuras",
                "Gringotts<Punto> a=[Punto {x: 1,y: 2}]; a[0].x=5; a[0]=Punto {x: 7,y: 8}; Revelio a; Revelio a.longitud;",
                "[Punto {x: 7, y: 8.0}]\n1"),
            Arguments.of("arreglo vacio",
                "Gringotts<Punto> a=[]; Revelio a.longitud;", "0"),
            Arguments.of("inferencia",
                "Revelio [Punto {x: 1,y: 2}][0].y/2;", "1.0"),
            Arguments.of("entrada en campo",
                "Alohomora p: Punto=Punto {x: Legilimens(),y: 2}; p.y=Legilimens(); Revelio p;",
                "Punto {x: 12, y: 12.0}"),
            Arguments.of("campo como contador",
                "Alohomora p: Punto=Punto {x: 8,y: 0}; Wingardium(p.x=0;p.x<2;p.x=p.x+1) { Revelio p.x; }",
                "0\n1"),
            Arguments.of("tipo inferido en ciclo",
                "Wingardium(p=Punto {x: 0,y: 0};p.x<2;p.x=p.x+1) { Revelio p.x; }",
                "0\n1"),
            Arguments.of("concatenacion",
                "Revelio \"Dato: \"+Punto {x: 1,y: 2};", "Dato: Punto {x: 1, y: 2.0}")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validos")
    void ejecutaEstructuras(String nombre, String cuerpo, String esperado) {
        comprobarSalida(PUNTO + "Hogwarts() {" + cuerpo + "}", esperado);
    }

    @Test
    void entradaPuedeSerNombreDeEstructuraSinConfundirseConLegilimens() {
        comprobarSalida("""
            Varita Entrada { valor: Entero; }
            Hogwarts() {
                Alohomora e: Entrada=Entrada {valor: 2};
                Revelio [e][0].valor;
            }
            """, "2");
        assertThrows(ErrorEnlace.class, () -> compilar(
            "Varita Entrada { valor: Entero; } Hogwarts() { Alohomora e: Entrada=Legilimens(); }", Map.of()));
        assertThrows(ErrorEnlace.class, () -> compilar(
            "Varita Entrada { valor: Entero; } Hogwarts() { Alohomora t: Texto=Entrada {valor: 1}; }", Map.of()));
    }

    @Test
    void indicaLaLineaDelCampoConTipoDesconocido() {
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> compilar("""
            Varita Malo {
                x: Entero;
                y: Fantasma;
            }
            Hogwarts() {}
            """, Map.of()));
        assertTrue(error.getMessage().contains("linea 3"), error.getMessage());
    }

    @Test
    void combinaCamposAnidadosYArreglos() {
        comprobarSalida(PUNTO + """
            Varita Grupo { centro: Punto; notas: Gringotts<Decimal>; otros: Gringotts<Punto>; }
            Hogwarts() {
                Alohomora g: Grupo=Grupo {centro: Punto {x: 1,y: 2},notas: [1,2],otros: []};
                g.centro.x=4;
                g.notas[1]=7;
                g.otros=[g.centro];
                g.otros[0].y=8;
                Revelio g.centro.y;
                Revelio g.notas;
                Revelio g.otros.longitud;
                g.notas=[];
                Revelio g.notas.longitud;
            }
            """, "8.0\n[1.0, 7.0]\n1\n0");
    }

    @Test
    void permiteCampoLlamadoLongitud() {
        comprobarSalida("""
            Varita Medida { longitud: Entero; }
            Hogwarts() {
                Gringotts<Medida> a=[Medida {longitud: 4}];
                a[0].longitud=8;
                Revelio a.longitud;
                Revelio a[0].longitud;
            }
            """, "1\n8");
    }

    @Test
    void admiteDeclaracionesAdelantadasTiposSimplesYEstructuraVacia() {
        comprobarSalida("""
            Varita Ficha { datos: Datos; nada: Obliviate; }
            Varita Datos { texto: Texto; letra: Caracter; activo: Booleano; }
            Varita Vacia {}
            Hogwarts() {
                Alohomora ficha: Ficha=Ficha {datos: Datos {texto: "a",letra: "b",activo: Lumos},nada: Obliviate};
                Revelio ficha;
                Revelio Vacia {};
            }
            """, "Ficha {datos: Datos {texto: a, letra: b, activo: Lumos}, nada: Obliviate}\nVacia {}");
    }

    @Test
    void funcionesRecibenModificanYRetornanEstructuras() {
        comprobarSalida(PUNTO + """
            Expecto crear(x: Entero): Punto { Patronum Punto {x: x,y: 2}; }
            Expecto mover(p: Punto): Punto { p.x=p.x+1; Patronum p; }
            Expecto lista(): Gringotts<Punto> { Patronum [Accio crear(8)]; }
            Hogwarts() {
                Alohomora p: Punto=Accio crear(4);
                Alohomora q: Punto=Accio mover(p);
                Revelio p.x;
                Revelio p==q;
                Revelio Accio mover(Punto {x: 1,y: 2}).x;
                Revelio Accio lista()[0].x;
            }
            """, "5\nLumos\n2\n8");
    }

    @Test
    void evaluaUnaVezElDestinoYRespetaElOrdenEscritoDeLosCampos() {
        comprobarSalida(PUNTO + """
            Expecto siguiente(contador: Gringotts<Entero>): Entero {
                contador[0]=contador[0]+1;
                Patronum contador[0];
            }
            Expecto indice(contador: Gringotts<Entero>): Entero {
                contador[0]=contador[0]+1;
                Patronum 0;
            }
            Hogwarts() {
                Gringotts<Entero> contador=[0];
                Gringotts<Punto> puntos=[Punto {y: Accio siguiente(contador),x: Accio siguiente(contador)}];
                puntos[Accio indice(contador)].x=Accio siguiente(contador);
                Revelio contador[0];
                Revelio puntos[0];
            }
            """, "4\nPunto {x: 4, y: 1.0}");
    }

    @Test
    void imprimeReferenciasCiclicasSinDesbordarLaPila() {
        comprobarSalida("""
            Varita Rama { hijos: Gringotts<Rama>; }
            Hogwarts() {
                Alohomora raiz: Rama=Rama {hijos: []};
                raiz.hijos=[raiz];
                Revelio raiz;
                Revelio raiz.hijos;
            }
            """, "Rama {hijos: [<ciclo>]}\n[Rama {hijos: <ciclo>}]");
    }

    @Test
    void noConfundeReferenciasRepetidasConCiclos() {
        comprobarSalida(PUNTO + """
            Hogwarts() {
                Alohomora p: Punto=Punto {x: 1,y: 2};
                Revelio [p,p];
            }
            """, "[Punto {x: 1, y: 2.0}, Punto {x: 1, y: 2.0}]");
    }

    @Test
    void importaDefinicionesYFuncionesCompartiendoElMismoTipo() {
        String biblioteca = PUNTO + "Expecto mover(p: Punto): Punto { p.x=p.x+1; Patronum p; }";
        comprobarSalida("""
            Floo Punto desde "puntos.dobby";
            Floo mover desde "puntos.dobby";
            Hogwarts() {
                Alohomora p: Punto=Punto {x: 3,y: 4};
                Revelio Accio mover(p).x;
                Revelio p.x;
            }
            """, Map.of("puntos.dobby", biblioteca), "4\n4");
    }

    @Test
    void funcionImportadaTraeSusTiposYDependenciasTransitivas() {
        Map<String, String> archivos = Map.of(
            "tipos.dobby", PUNTO,
            "crear.dobby", """
                Floo Punto desde "tipos.dobby";
                Varita Caja { punto: Punto; }
                Expecto crear(): Caja { Patronum Caja {punto: Punto {x: 7,y: 2}}; }
                """);
        comprobarSalida("""
            Floo crear desde "crear.dobby";
            Floo Punto desde "tipos.dobby";
            Hogwarts() {
                Alohomora caja: Caja=Accio crear();
                caja.punto.x=9;
                Revelio caja.punto.x;
            }
            """, archivos, "9");
    }

    static Stream<Arguments> invalidos() {
        return Stream.of(
            Arguments.of("tipo generico Varita", "Hogwarts() { Alohomora p: Varita; }", "Tipo de dato desconocido"),
            Arguments.of("tipo inexistente", "Varita Malo { x: Fantasma; } Hogwarts() {}", "Tipo de dato desconocido"),
            Arguments.of("tipo anidado inexistente", "Varita Malo { x: Gringotts<Fantasma>; } Hogwarts() {}", "Tipo de dato desconocido"),
            Arguments.of("declaracion duplicada", "Varita Punto {} Hogwarts() {}", "mas de una vez"),
            Arguments.of("conflicto con funcion", "Expecto Punto(): Obliviate {} Hogwarts() {}", "funcion y estructura"),
            Arguments.of("constructor desconocido", "Hogwarts() { Revelio Fantasma {}; }", "desconocida"),
            Arguments.of("campo faltante", "Hogwarts() { Revelio Punto {x: 1}; }", "Falta el campo 'y'"),
            Arguments.of("campo extra", "Hogwarts() { Revelio Punto {x: 1,y: 2,z: 3}; }", "no tiene el campo 'z'"),
            Arguments.of("tipo incorrecto", "Hogwarts() { Revelio Punto {x: Nox,y: 2}; }", "tipo Entero"),
            Arguments.of("lectura desconocida", "Hogwarts() { Revelio Punto {x: 1,y: 2}.z; }", "no tiene el campo 'z'"),
            Arguments.of("escritura desconocida", "Hogwarts() { Alohomora p: Punto=Punto {x: 1,y: 2}; p.z=3; }", "no tiene el campo 'z'"),
            Arguments.of("escritura incompatible", "Hogwarts() { Alohomora p: Punto=Punto {x: 1,y: 2}; p.x=2.5; }", "tipo Entero"),
            Arguments.of("campo sobre escalar", "Hogwarts() { Revelio 1.x; }", "estructura Varita"),
            Arguments.of("variable inexistente", "Hogwarts() { p.x=1; }", "no ha sido declarada"),
            Arguments.of("longitud inexistente", "Hogwarts() { Revelio Punto {x: 1,y: 2}.longitud; }", "no tiene el campo"),
            Arguments.of("tipo nominal", "Varita Otro { x: Entero; y: Decimal; } Hogwarts() { Alohomora p: Punto=Otro {x: 1,y: 2}; }", "tipo Punto"),
            Arguments.of("campo de arreglo incompatible", "Varita Caja { puntos: Gringotts<Punto>; } Hogwarts() { Revelio Caja {puntos: [1]}; }", "tipo Punto"),
            Arguments.of("campo anidado incompatible", "Varita Caja { punto: Punto; } Hogwarts() { Revelio Caja {punto: 1}; }", "tipo Punto"),
            Arguments.of("argumento incompatible", "Expecto ver(p: Punto): Obliviate {} Hogwarts() { Accio ver(1); }", "argumento"),
            Arguments.of("retorno incompatible", "Expecto crear(): Punto { Patronum 1; } Hogwarts() {}", "tipo Punto"),
            Arguments.of("aritmetica", "Hogwarts() { Revelio Punto {x: 1,y: 2}+1; }", "requiere un numero"),
            Arguments.of("condicion", "Hogwarts() { Protego(Punto {x: 1,y: 2}) {} }", "requiere Booleano")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidos")
    void rechazaErroresSemanticos(String nombre, String codigo, String esperado) {
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> compilar(PUNTO + codigo, Map.of()));
        assertEquals("Error semantico", error.getTipo());
        assertTrue(error.getMessage().contains(esperado), error.getMessage());
    }

    static Stream<String> sintaxisInvalida() {
        return Stream.of(
            "Varita Otra { x: Entero; x: Decimal; } Hogwarts() {}",
            "Varita Otra { x: Entero } Hogwarts() {}",
            "Varita Otra { x: Entero;",
            "Hogwarts() { Revelio Punto {x: 1,x: 2,y: 3}; }",
            "Hogwarts() { Revelio Punto {x: 1 y: 2}; }",
            "Hogwarts() { Revelio Punto {x: 1,y: 2,}; }",
            "Hogwarts() { Revelio Punto {x: 1,y: 2; }",
            "Hogwarts() { Varita Local {} }",
            "Hogwarts() { Alohomora p: Punto; p.=1; }"
        );
    }

    @ParameterizedTest
    @MethodSource("sintaxisInvalida")
    void rechazaSintaxisInvalida(String codigo) {
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> compilar(PUNTO + codigo, Map.of()));
        assertEquals("Error de sintaxis", error.getTipo());
    }

    static Stream<Arguments> erroresDeEjecucion() {
        return Stream.of(
            Arguments.of("Alohomora p: Punto; Revelio p.x;", "no ha sido inicializada"),
            Arguments.of("Alohomora p: Punto; p.x=1;", "no ha sido inicializada"),
            Arguments.of("Alohomora p: Punto; Alohomora q: Punto=p;", "No se recibio un valor"),
            Arguments.of("Gringotts<Punto> a=[]; a[0].x=1;", "fuera de rango"),
            Arguments.of("Alohomora x: Entero; Revelio Punto {x: x,y: 2};", "No se recibio un valor")
        );
    }

    @ParameterizedTest
    @MethodSource("erroresDeEjecucion")
    void reportaErroresDeEjecucionConUbicacion(String cuerpo, String esperado) {
        var resultado = ejecutar(PUNTO + "Hogwarts() {\n" + cuerpo + "\n}", Map.of());
        assertFalse(resultado.isExito());
        String mensaje = resultado.getErrores().getFirst();
        assertTrue(mensaje.contains(esperado), mensaje);
        assertTrue(mensaje.contains("[principal.dobby]"));
        assertTrue(mensaje.contains("linea 2"));
    }

    @Test
    void conservaArchivoYLineaEnErroresDeFuncionesImportadas() {
        String biblioteca = PUNTO + """
            Expecto consultar(): Entero {
                Alohomora p: Punto;
                Patronum p.x;
            }
            """;
        var resultado = ejecutar("Floo consultar desde \"puntos.dobby\"; Hogwarts() { Revelio Accio consultar(); }",
            Map.of("puntos.dobby", biblioteca));
        assertFalse(resultado.isExito());
        String mensaje = ErrorFormatter.formatearMensaje("Error de ejecucion", "principal.dobby",
            resultado.getErrores().getFirst());
        assertTrue(mensaje.contains("Archivo: puntos.dobby"), mensaje);
        assertTrue(mensaje.contains("Linea: 3"), mensaje);
    }

    @Test
    void rechazaConflictosEntreDefinicionesDeArchivosDistintos() {
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> compilar("""
            Floo crear desde "puntos.dobby";
            Varita Punto { nombre: Texto; }
            Hogwarts() {}
            """, Map.of("puntos.dobby", PUNTO + "Expecto crear(): Punto { Patronum Punto {x: 1,y: 2}; }")));
        assertTrue(error.getMessage().contains("estructuras distintas"), error.getMessage());
    }

    @Test
    void rechazaImportacionDuplicadaDeTipo() {
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> compilar("""
            Floo Punto desde "puntos.dobby";
            Floo Punto desde "puntos.dobby";
            Hogwarts() {}
            """, Map.of("puntos.dobby", PUNTO)));
        assertTrue(error.getMessage().contains("ya esta declarada o importada"));
    }

    @Test
    void validaEstructurasAunqueNoSeUsenEnFunciones() {
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> compilar("""
            Floo Malo desde "tipos.dobby";
            Hogwarts() {}
            """, Map.of("tipos.dobby", "Varita Malo { x: Fantasma; }")));
        assertTrue(error.getMessage().contains("[tipos.dobby]"));
        assertTrue(error.getMessage().contains("Tipo de dato desconocido"));
    }
}
