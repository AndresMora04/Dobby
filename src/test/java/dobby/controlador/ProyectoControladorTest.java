package dobby.controlador;

import dobby.flow.FlowController;
import dobby.vista.MainView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class ProyectoControladorTest {
    @TempDir
    Path carpeta;
    private MainView vista;
    private FlowController flujo;
    private Controlador controlador;

    @BeforeEach
    void preparar() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless());
        Files.writeString(carpeta.resolve("principal.dobby"),
            "Floo valor desde \"operaciones.dobby\"; Hogwarts() { Revelio Accio valor(); }");
        Files.writeString(carpeta.resolve("operaciones.dobby"), "Expecto valor(): Entero { Patronum 5; }");
        SwingUtilities.invokeAndWait(() -> {
            vista = new MainView();
            flujo = new FlowController();
            controlador = new Controlador(vista, flujo);
            invocar("cargarProyecto", carpeta);
            invocar("abrirRuta", carpeta.resolve("operaciones.dobby"));
        });
    }

    @AfterEach
    void cerrar() throws Exception {
        if (vista != null) {
            SwingUtilities.invokeAndWait(() -> {
                controlador.manejarCerrarProyecto();
                vista.getPanelSalida().limpiar();
                vista.dispose();
            });
        }
    }

    private void invocar(String nombre, Path ruta) {
        try {
            Method metodo = Controlador.class.getDeclaredMethod(nombre, Path.class);
            metodo.setAccessible(true);
            metodo.invoke(controlador, ruta);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    @Test
    void compilaYEjecutaDesdeUnaPestanaAuxiliar() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            controlador.manejarCompilar();
            assertTrue(flujo.getUltimoResultado().isExito());
            controlador.manejarEjecutar();
            assertTrue(flujo.getUltimoResultado().isExito());
            assertEquals("5", flujo.getUltimoResultado().getSalida().trim());
            assertEquals("operaciones.dobby", flujo.getArchivoActivo().getNombre());
            assertEquals("principal.dobby", flujo.getProyectoActivo().getArchivoPrincipal().getNombre());
        });
    }

    @Test
    void ejecutaCambiosDelPrincipalTrasCambiarDePestana() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            invocar("abrirRuta", carpeta.resolve("principal.dobby"));
            vista.getPanelEditor().setTexto("Hogwarts() { Revelio 81; }");
            invocar("abrirRuta", carpeta.resolve("operaciones.dobby"));
            controlador.manejarEjecutar();
            assertTrue(flujo.getUltimoResultado().isExito());
            assertEquals("81", flujo.getUltimoResultado().getSalida().trim());
            assertEquals("operaciones.dobby", flujo.getArchivoActivo().getNombre());
        });
        assertFalse(Files.readString(carpeta.resolve("principal.dobby")).contains("Revelio 81"));
    }

    @Test
    void ejecutaLaVersionSinGuardarDeLaPestanaAuxiliar() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            vista.getPanelEditor().setTexto("Expecto valor(): Entero { Patronum 12; }");
            controlador.manejarEjecutar();
            assertTrue(flujo.getUltimoResultado().isExito());
            assertEquals("12", flujo.getUltimoResultado().getSalida().trim());
        });
        assertTrue(Files.readString(carpeta.resolve("operaciones.dobby")).contains("Patronum 5"));
    }

    @Test
    void ejecutaFuncionImportadaQueRetornaUnArreglo() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            vista.getPanelEditor().setTexto("Expecto valor(): Gringotts<Entero> { Patronum [2,4,6]; }");
            controlador.manejarEjecutar();
            assertTrue(flujo.getUltimoResultado().isExito());
            assertEquals("[2, 4, 6]", flujo.getUltimoResultado().getSalida().trim());
            assertEquals("operaciones.dobby", flujo.getArchivoActivo().getNombre());
        });
    }

    @Test
    void bloqueaEjecucionSiSeAgregaOtroPrincipalSinGuardar() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            vista.getPanelEditor().setTexto("Expecto valor(): Entero { Patronum 5; } Hogwarts() {}");
            controlador.manejarEjecutar();
            assertFalse(flujo.getUltimoResultado().isExito());
            assertEquals("", flujo.getUltimoResultado().getSalida());
            assertTrue(flujo.getUltimoResultado().getErrores().getFirst().contains("unico Hogwarts"));
            assertNull(flujo.getProyectoActivo().getArchivoPrincipal());
        });
    }

    @Test
    void cerrarProyectoConservaLosBuffersYPermiteEjecutarArchivoSuelto() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            vista.getPanelEditor().setTexto("Hogwarts() { Revelio 23; }");
            controlador.manejarCerrarProyecto();
            controlador.manejarEjecutar();
            assertNull(flujo.getProyectoActivo());
            assertEquals("23", flujo.getUltimoResultado().getSalida().trim());
            assertEquals("operaciones.dobby", flujo.getArchivoActivo().getNombre());
        });
    }

    @Test
    void refrescarDetectaUnPrincipalEliminadoAunqueSigaAbierto() throws Exception {
        SwingUtilities.invokeAndWait(() -> invocar("abrirRuta", carpeta.resolve("principal.dobby")));
        Files.delete(carpeta.resolve("principal.dobby"));
        SwingUtilities.invokeAndWait(() -> {
            controlador.manejarRefrescarProyecto();
            controlador.manejarEjecutar();
            assertFalse(flujo.getUltimoResultado().isExito());
            assertNull(flujo.getProyectoActivo().getArchivoPrincipal());
        });
    }
}
