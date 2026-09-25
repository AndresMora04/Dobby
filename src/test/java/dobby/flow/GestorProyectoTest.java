package dobby.flow;

import dobby.modelo.Proyecto;
import dobby.motor.enlazador.ErrorEnlace;
import dobby.motor.interprete.Interprete;
import dobby.util.FileUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GestorProyectoTest {
    @TempDir
    Path carpeta;

    private final Map<Path, String> abiertos = new HashMap<>();
    private final GestorProyecto gestor = new GestorProyecto(ruta ->
        abiertos.containsKey(ruta) ? abiertos.get(ruta) : FileUtil.leerArchivo(ruta));

    private Path escribir(String nombre, String codigo) throws Exception {
        Path ruta = carpeta.resolve(nombre);
        Files.createDirectories(ruta.getParent());
        Files.writeString(ruta, codigo);
        return ruta;
    }

    @Test
    void ejecutaEstructurasImportadasConCambiosSinGuardar() throws Exception {
        Path tipos = escribir("lib/tipos.dobby", "Varita Dato { valor: Entero; }");
        Path entrada = escribir("inicio.dobby", """
            Floo Dato desde "lib/tipos.dobby";
            Hogwarts() {
                Gringotts<Dato> datos=[Dato {valor: 7}];
                datos[0].valor=9;
                Revelio datos[0].valor/2;
            }
            """);
        abiertos.put(tipos, "Varita Dato { valor: Decimal; }");
        Proyecto proyecto = gestor.cargar(carpeta);
        var resultado = new Interprete().ejecutar(gestor.enlazar(proyecto).programa());
        assertTrue(resultado.isExito(), resultado.getErrores().toString());
        assertEquals(entrada, proyecto.getArchivoPrincipal().getRuta());
        assertEquals("4.5", resultado.getSalida().trim());
        assertTrue(Files.readString(tipos).contains("Entero"));
    }

    @Test
    void creaProyectoEjecutableSinSobrescribir() throws Exception {
        Proyecto proyecto = gestor.crear(carpeta, "MiProyecto");
        assertTrue(Files.isRegularFile(proyecto.getCarpeta().resolve("principal.dobby")));
        var resultado = new Interprete().ejecutar(gestor.enlazar(proyecto).programa());
        assertTrue(resultado.isExito());
        assertEquals("Hola, mundo", resultado.getSalida().trim());
        assertThrows(FileAlreadyExistsException.class, () -> gestor.crear(carpeta, "MiProyecto"));
        assertEquals("principal.dobby", proyecto.getArchivoPrincipal().getNombre());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", ".", "..", "../afuera", "sub/carpeta", "sub\\carpeta", "invalido:", "nombre."})
    void rechazaNombresInvalidos(String nombre) {
        assertThrows(IllegalArgumentException.class, () -> gestor.crear(carpeta, nombre));
    }

    @Test
    void detectaPrincipalPorContenidoEnSubcarpetas() throws Exception {
        escribir("principal.dobby", "Expecto f(): Obliviate { Revelio \"Hogwarts\"; }");
        Path entrada = escribir("src/arranque.dobby", "Hogwarts() { Revelio 7; }");
        Proyecto proyecto = gestor.cargar(carpeta);
        assertEquals(entrada, gestor.buscarPrincipal(proyecto).getRuta());
        assertEquals("7", new Interprete().ejecutar(gestor.enlazar(proyecto).programa()).getSalida().trim());
    }

    @Test
    void noConfundeTextoConDeclaraciones() throws Exception {
        escribir("biblioteca.dobby", "Expecto f(): Texto { Patronum \"Hogwarts() {}\"; }");
        Proyecto proyecto = gestor.cargar(carpeta);
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> gestor.enlazar(proyecto));
        assertEquals("Error de proyecto", error.getTipo());
        assertTrue(error.getMessage().contains("No se encontro Hogwarts"));
        assertNull(proyecto.getArchivoPrincipal());
    }

    @Test
    void rechazaProyectoVacio() throws Exception {
        Proyecto proyecto = gestor.cargar(carpeta);
        assertThrows(ErrorEnlace.class, () -> gestor.enlazar(proyecto));
    }

    @Test
    void rechazaDosArchivosPrincipalesInclusoSinImportacion() throws Exception {
        escribir("uno.dobby", "Hogwarts() {}");
        escribir("sub/dos.dobby", "Hogwarts() {}");
        Proyecto proyecto = gestor.cargar(carpeta);
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> gestor.enlazar(proyecto));
        assertTrue(error.getMessage().contains("uno.dobby"));
        assertTrue(error.getMessage().contains("dos.dobby"));
        assertNull(proyecto.getArchivoPrincipal());
    }

    @Test
    void rechazaDosBloquesEnUnMismoArchivo() throws Exception {
        escribir("principal.dobby", "Hogwarts() {}\nHogwarts() {}");
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> gestor.enlazar(gestor.cargar(carpeta)));
        assertTrue(error.getMessage().contains("linea 1"));
        assertTrue(error.getMessage().contains("linea 2"));
    }

    @Test
    void ejecutaImportacionesConBuffersSinGuardar() throws Exception {
        Path entrada = escribir("principal.dobby", "Hogwarts() { Revelio 0; }");
        Path funciones = escribir("operaciones.dobby", "Expecto valor(): Entero { Patronum 1; }");
        abiertos.put(entrada, "Floo valor desde \"operaciones.dobby\"; Hogwarts() { Revelio Accio valor(); }");
        abiertos.put(funciones, "Expecto valor(): Entero { Patronum 9; }");
        var resultado = new Interprete().ejecutar(gestor.enlazar(gestor.cargar(carpeta)).programa());
        assertTrue(resultado.isExito());
        assertEquals("9", resultado.getSalida().trim());
        assertTrue(Files.readString(entrada).contains("Revelio 0"));
    }

    @Test
    void detectaPrincipalAgregadoYEliminadoSinGuardar() throws Exception {
        Path entrada = escribir("principal.dobby", "Hogwarts() {}");
        Path auxiliar = escribir("auxiliar.dobby", "");
        Proyecto proyecto = gestor.cargar(carpeta);
        assertEquals(entrada, gestor.buscarPrincipal(proyecto).getRuta());
        abiertos.put(auxiliar, "Hogwarts() {}");
        assertThrows(ErrorEnlace.class, () -> gestor.buscarPrincipal(proyecto));
        assertNull(proyecto.getArchivoPrincipal());
        abiertos.put(entrada, "");
        assertEquals(auxiliar, gestor.buscarPrincipal(proyecto).getRuta());
        abiertos.clear();
        assertEquals(entrada, gestor.buscarPrincipal(proyecto).getRuta());
    }

    @Test
    void vuelveADetectarTrasRenombrarYEliminar() throws Exception {
        Path entrada = escribir("principal.dobby", "Hogwarts() {}");
        Proyecto proyecto = gestor.cargar(carpeta);
        gestor.buscarPrincipal(proyecto);
        Path renombrado = carpeta.resolve("inicio.dobby");
        Files.move(entrada, renombrado);
        assertEquals(renombrado, gestor.buscarPrincipal(proyecto).getRuta());
        Files.delete(renombrado);
        assertThrows(ErrorEnlace.class, () -> gestor.buscarPrincipal(proyecto));
        assertNull(proyecto.getArchivoPrincipal());
    }

    @Test
    void incluyeArchivosNuevosYSubcarpetasProfundas() throws Exception {
        escribir("principal.dobby", "Hogwarts() {}");
        Proyecto proyecto = gestor.cargar(carpeta);
        gestor.buscarPrincipal(proyecto);
        escribir("a/b/c/d/e/f/g/otro.dobby", "Hogwarts() {}");
        assertThrows(ErrorEnlace.class, () -> gestor.buscarPrincipal(proyecto));
    }

    @Test
    void ignoraCarpetasOcultasYArchivosDeOtraExtension() throws Exception {
        escribir("principal.dobby", "Hogwarts() {}");
        escribir(".historial/copia.dobby", "Hogwarts() {}");
        escribir("notas.txt", "Hogwarts() {}");
        Proyecto proyecto = gestor.cargar(carpeta);
        assertEquals("principal.dobby", gestor.buscarPrincipal(proyecto).getNombre());
        assertEquals(1, proyecto.getArchivos().size());
    }

    @Test
    void conservaErroresDeSintaxisConArchivoYLinea() throws Exception {
        escribir("principal.dobby", "Hogwarts() {}");
        escribir("auxiliar.dobby", "Expecto f(): Obliviate {\n Revelio 1\n}");
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> gestor.enlazar(gestor.cargar(carpeta)));
        assertEquals("Error de sintaxis", error.getTipo());
        assertTrue(error.getMessage().contains("[auxiliar.dobby]"));
        assertTrue(error.getMessage().contains("linea 3"));
    }

    @Test
    void validaParametrosDelPrincipal() throws Exception {
        escribir("principal.dobby", "Hogwarts(x: Entero) {}");
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> gestor.enlazar(gestor.cargar(carpeta)));
        assertTrue(error.getMessage().contains("no puede recibir parametros"));
    }

    @Test
    void validaDependenciasAntesDeEjecutar() throws Exception {
        escribir("principal.dobby", "Floo f desde \"falta.dobby\"; Hogwarts() { Accio f(); }");
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> gestor.enlazar(gestor.cargar(carpeta)));
        assertEquals("Error de importacion", error.getTipo());
    }

    @Test
    void noUsaBufferDeUnaDependenciaEliminada() throws Exception {
        escribir("principal.dobby", "Floo f desde \"auxiliar.dobby\"; Hogwarts() { Accio f(); }");
        abiertos.put(carpeta.resolve("auxiliar.dobby"), "Expecto f(): Obliviate {}");
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> gestor.enlazar(gestor.cargar(carpeta)));
        assertTrue(error.getMessage().contains("no existe"));
    }

    @Test
    void noImportaFuncionesDeOtroProyecto() throws Exception {
        escribir("uno/principal.dobby", "Floo f desde \"../dos/funciones.dobby\"; Hogwarts() { Accio f(); }");
        escribir("dos/funciones.dobby", "Expecto f(): Obliviate {}");
        Proyecto proyecto = gestor.cargar(carpeta.resolve("uno"));
        ErrorEnlace error = assertThrows(ErrorEnlace.class, () -> gestor.enlazar(proyecto));
        assertEquals("Error de importacion", error.getTipo());
    }
}
