package dobby.motor.enlazador;

import org.junit.jupiter.api.Test;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AdvertenciasTest {
    private Enlace compilar(String codigo, Map<String, String> archivos) {
        return new Enlazador(ruta -> {
            String texto = archivos.get(ruta.getFileName().toString());
            if (texto == null) throw new NoSuchFileException(ruta.toString());
            return texto;
        }).enlazar(Path.of("principal.dobby"), codigo, null);
    }

    private boolean tieneAdvertencia(Enlace enlace, String fragmento) {
        return enlace.advertencias().stream().anyMatch(a -> a.contains(fragmento));
    }

    @Test
    void advierteVariableDeclaradaYNuncaUsada() {
        Enlace enlace = compilar("""
            Hogwarts() {
                Alohomora x: Entero;
                x = 5;
            }
            """, Map.of());
        assertTrue(tieneAdvertencia(enlace, "La variable 'x' se declara pero nunca se usa"));
    }

    @Test
    void noAdviertePorVariableLeida() {
        Enlace enlace = compilar("""
            Hogwarts() {
                Alohomora x: Entero;
                x = 5;
                Revelio x;
            }
            """, Map.of());
        assertTrue(enlace.advertencias().isEmpty());
    }

    @Test
    void noAdviertePorElContadorDeUnFor() {
        Enlace enlace = compilar("""
            Hogwarts() {
                Wingardium (i = 0; i < 3; i = i + 1) {
                    Revelio "hola";
                }
            }
            """, Map.of());
        assertTrue(enlace.advertencias().isEmpty());
    }

    @Test
    void noAdviertePorParametroSinUsar() {
        Enlace enlace = compilar("""
            Expecto f(a: Entero): Obliviate {
                Revelio "hola";
            }
            Hogwarts() {
                Accio f(1);
            }
            """, Map.of());
        assertTrue(enlace.advertencias().isEmpty());
    }

    @Test
    void advierteFuncionDeclaradaYNuncaLlamada() {
        Enlace enlace = compilar("""
            Expecto ayuda(): Obliviate {
                Revelio "x";
            }
            Hogwarts() {
                Revelio "hola";
            }
            """, Map.of());
        assertTrue(tieneAdvertencia(enlace, "La funcion 'ayuda' se declara pero nunca se llama"));
    }

    @Test
    void noAdviertePorFuncionLlamada() {
        Enlace enlace = compilar("""
            Expecto ayuda(): Obliviate {
                Revelio "x";
            }
            Hogwarts() {
                Accio ayuda();
            }
            """, Map.of());
        assertTrue(enlace.advertencias().isEmpty());
    }

    @Test
    void noAdviertePorFuncionUsadaSoloDesdeOtroArchivo() {
        Enlace enlace = compilar("""
            Floo sumar desde "ops.dobby";
            Hogwarts() {
                Revelio Accio sumar(2, 3);
            }
            """, Map.of("ops.dobby", """
            Expecto sumar(a: Entero, b: Entero): Entero {
                Patronum a + b;
            }
            """));
        assertFalse(tieneAdvertencia(enlace, "'sumar'"));
    }

    @Test
    void advierteImportacionNuncaUsada() {
        Enlace enlace = compilar("""
            Floo sumar desde "ops.dobby";
            Hogwarts() {
                Revelio "no la uso";
            }
            """, Map.of("ops.dobby", """
            Expecto sumar(a: Entero, b: Entero): Entero {
                Patronum a + b;
            }
            """));
        assertTrue(tieneAdvertencia(enlace, "La funcion importada 'sumar' nunca se usa en este archivo"));
    }

    @Test
    void noAdviertePorHogwartsAunqueNadaLoLlame() {
        Enlace enlace = compilar("""
            Hogwarts() {
                Revelio "hola";
            }
            """, Map.of());
        assertFalse(tieneAdvertencia(enlace, "Hogwarts"));
    }
}
