package dobby.controlador;

import dobby.flow.FlowController;
import dobby.modelo.ArchivoDobby;
import dobby.motor.interprete.Interprete;
import dobby.motor.interprete.ResultadoEjecucion;
import dobby.motor.lexer.Lexer;
import dobby.motor.lexer.Token;
import dobby.motor.parser.NodoPrograma;
import dobby.motor.parser.Parser;
import dobby.util.FileUtil;
import dobby.vista.MainView;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Controlador {
    private final MainView vista;
    private final FlowController flowController;
    private final Lexer lexer;
    private final Parser parser;
    private final Interprete interprete;
    private NodoPrograma ultimoPrograma;

    public Controlador(MainView vista, FlowController flowController) {
        this.vista = vista;
        this.flowController = flowController;
        this.lexer = new Lexer();
        this.parser = new Parser();
        this.interprete = new Interprete(mensaje -> JOptionPane.showInputDialog(vista, mensaje, "Legilimens", JOptionPane.QUESTION_MESSAGE));
        inicializarListeners();
    }

    private void inicializarListeners() {
        vista.setControlador(this);
    }

    public void manejarNuevoArchivo() {
        String ejemplo = """
                Hogwarts() {
                    Alohomora edad: Entero;
                    edad = 11;
                    Revelio "Puede entrar a Hogwarts";
                }
                """;
        flowController.setArchivoActivo(null);
        ultimoPrograma = null;
        vista.getPanelEditor().setTexto(ejemplo);
        vista.getPanelSalida().limpiar();
        vista.getPanelSalida().agregarMensaje("Archivo nuevo creado en memoria.");
        vista.getPanelSalida().agregarMensaje("Ya puedes modificarlo y compilarlo.");
        vista.setTitle("Dobby");
    }

    public void manejarAbrir() {
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter("Archivos Dobby (*.dobby)", "dobby"));
        if (selector.showOpenDialog(vista) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path ruta = selector.getSelectedFile().toPath();
        try {
            String contenido = FileUtil.leerArchivo(ruta);
            ArchivoDobby archivo = new ArchivoDobby(ruta);
            archivo.setNombre(ruta.getFileName().toString());
            archivo.setContenido(contenido);
            archivo.setModificado(false);
            flowController.setArchivoActivo(archivo);
            ultimoPrograma = null;

            vista.getPanelEditor().setTexto(contenido);
            vista.getPanelSalida().limpiar();
            vista.getPanelSalida().agregarMensaje("Archivo abierto: " + archivo.getNombre());
            vista.setTitle("Dobby - " + archivo.getNombre());
        } catch (IOException e) {
            vista.getPanelSalida().agregarError("No se pudo abrir el archivo: " + e.getMessage());
        }
    }

    public void manejarGuardar() {
        ArchivoDobby archivoActivo = flowController.getArchivoActivo();
        Path ruta = archivoActivo != null ? archivoActivo.getRuta() : null;

        if (ruta == null) {
            JFileChooser selector = new JFileChooser();
            selector.setFileFilter(new FileNameExtensionFilter("Archivos Dobby (*.dobby)", "dobby"));
            if (selector.showSaveDialog(vista) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            ruta = selector.getSelectedFile().toPath();
            if (!FileUtil.esArchivoDobby(ruta)) {
                ruta = ruta.resolveSibling(ruta.getFileName() + ".dobby");
            }
        }

        try {
            String contenido = vista.getPanelEditor().getTexto();
            FileUtil.escribirArchivo(ruta, contenido);

            ArchivoDobby archivo = archivoActivo != null ? archivoActivo : new ArchivoDobby(ruta);
            archivo.setRuta(ruta);
            archivo.setNombre(ruta.getFileName().toString());
            archivo.setContenido(contenido);
            archivo.setModificado(false);
            flowController.setArchivoActivo(archivo);

            vista.getPanelSalida().agregarMensaje("Archivo guardado: " + archivo.getNombre());
            vista.setTitle("Dobby - " + archivo.getNombre());
        } catch (IOException e) {
            vista.getPanelSalida().agregarError("No se pudo guardar el archivo: " + e.getMessage());
        }
    }

    public void manejarCompilar() {
        vista.getPanelSalida().limpiar();
        String codigo = vista.getPanelEditor().getTexto();

        if (codigo == null || codigo.trim().isEmpty()) {
            registrarError("No hay codigo para compilar.");
            return;
        }

        try {
            List<Token> tokens = lexer.tokenizar(codigo);
            NodoPrograma programa = parser.parsear(tokens);
            ultimoPrograma = programa;

            ResultadoEjecucion resultado = new ResultadoEjecucion();
            resultado.setExito(true);
            resultado.setSalida("Compilacion exitosa.");
            resultado.setErrores(new ArrayList<>());
            flowController.setUltimoResultado(resultado);

            vista.getPanelSalida().agregarMensaje("Compilacion exitosa.");
            vista.getPanelSalida().agregarMensaje("Tokens encontrados: " + Math.max(0, tokens.size() - 1));
            vista.getPanelSalida().agregarMensaje("Declaraciones principales: " + programa.getSentencias().size());
        } catch (RuntimeException e) {
            registrarError(e.getMessage());
        }
    }

    public void manejarEjecutar() {
        ResultadoEjecucion ultimoResultado = flowController.getUltimoResultado();
        if (ultimoResultado == null || !ultimoResultado.isExito() || ultimoPrograma == null) {
            vista.getPanelSalida().agregarError("Primero debes compilar sin errores.");
            return;
        }

        ResultadoEjecucion resultado = interprete.ejecutar(ultimoPrograma);
        flowController.setUltimoResultado(resultado);

        if (resultado.isExito()) {
            vista.getPanelSalida().agregarMensaje("Ejecucion exitosa.");
            if (resultado.getSalida() != null && !resultado.getSalida().isEmpty()) {
                vista.getPanelSalida().agregarMensaje(resultado.getSalida().stripTrailing());
            }
        } else {
            for (String error : resultado.getErrores()) {
                vista.getPanelSalida().agregarError(error);
            }
        }
    }

    private void registrarError(String mensaje) {
        ultimoPrograma = null;
        ResultadoEjecucion resultado = new ResultadoEjecucion();
        resultado.setExito(false);
        resultado.setSalida("");
        resultado.setErrores(List.of(mensaje));
        flowController.setUltimoResultado(resultado);
        vista.getPanelSalida().agregarError(mensaje);
    }
}
