package dobby.controlador;

import dobby.flow.FlowController;
import dobby.modelo.ArchivoDobby;
import dobby.modelo.Proyecto;
import dobby.motor.enlazador.Enlace;
import dobby.motor.enlazador.ErrorEnlace;
import dobby.motor.enlazador.Enlazador;
import dobby.motor.interprete.Interprete;
import dobby.motor.interprete.ResultadoEjecucion;
import dobby.util.FileUtil;
import dobby.vista.DialogoNombre;
import dobby.vista.DialogoOpciones;
import dobby.vista.MainView;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Controlador {
    private static final String PLANTILLA_NUEVO = """
            Hogwarts() {
                Alohomora edad: Entero;
                edad = 11;
                Revelio "Puede entrar a Hogwarts";
            }
            """;
    private static final int PROFUNDIDAD_PROYECTO = 6;

    private final MainView vista;
    private final FlowController flowController;
    private final Interprete interprete;
    private int contadorSinTitulo;

    public Controlador(MainView vista, FlowController flowController) {
        this.vista = vista;
        this.flowController = flowController;
        this.interprete = new Interprete(mensaje -> JOptionPane.showInputDialog(vista, mensaje, "Legilimens", JOptionPane.QUESTION_MESSAGE));
        inicializarListeners();
        crearSinTitulo("");
    }

    private void inicializarListeners() {
        vista.setControlador(this);
        vista.getPanelPestanas().alSeleccionar(this::activar);
        vista.getPanelPestanas().alCerrar(this::cerrarArchivo);
        vista.getPanelArbolArchivos().alAbrirArchivo(this::abrirRuta);
        vista.getPanelArbolArchivos().alNuevoArchivo(this::crearArchivoEn);
        vista.getPanelArbolArchivos().alRenombrar(this::renombrarArchivo);
        vista.getPanelArbolArchivos().alEliminar(this::eliminarArchivo);
        vista.getPanelEditor().alCambiarTexto(this::alEditar);
    }

    public void manejarNuevoArchivo() {
        vista.getPanelSalida().limpiar();
        ArchivoDobby archivo = crearSinTitulo(PLANTILLA_NUEVO);
        vista.getPanelSalida().agregarMensaje("Archivo nuevo: " + archivo.getNombre());
        vista.getPanelSalida().agregarMensaje("Ya puedes modificarlo y compilarlo.");
    }

    public void manejarAbrir() {
        JFileChooser selector = new JFileChooser(directorioInicial());
        selector.setDialogTitle("Abrir archivos Dobby");
        selector.setMultiSelectionEnabled(true);
        selector.setFileFilter(new FileNameExtensionFilter("Archivos Dobby (*.dobby)", "dobby"));
        if (selector.showOpenDialog(vista) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        for (File archivo : selector.getSelectedFiles()) {
            abrirRuta(archivo.toPath());
        }
    }

    public void manejarAbrirCarpeta() {
        JFileChooser selector = new JFileChooser(directorioInicial());
        selector.setDialogTitle("Abrir carpeta de proyecto");
        selector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (selector.showOpenDialog(vista) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Proyecto proyecto = cargarProyecto(selector.getSelectedFile().toPath());
        if (proyecto == null) {
            return;
        }
        vista.getPanelSalida().limpiar();
        int total = proyecto.getArchivos().size();
        vista.getPanelSalida().agregarMensaje("Proyecto abierto: " + proyecto.getNombre());
        vista.getPanelSalida().agregarMensaje(total == 0
            ? "La carpeta todavia no tiene archivos .dobby."
            : "Archivos .dobby encontrados: " + total);
    }

    public void manejarGuardar() {
        ArchivoDobby activo = flowController.getArchivoActivo();
        if (activo != null) {
            guardar(activo, false);
        }
    }

    public void manejarGuardarComo() {
        ArchivoDobby activo = flowController.getArchivoActivo();
        if (activo != null) {
            guardar(activo, true);
        }
    }

    public void manejarGuardarTodo() {
        guardarTodo();
    }

    public void manejarCerrarPestana() {
        ArchivoDobby activo = flowController.getArchivoActivo();
        if (activo != null) {
            cerrarArchivo(activo);
        }
    }

    public void manejarSalir() {
        sincronizarBuffer();
        List<ArchivoDobby> pendientes = archivosAbiertos().stream().filter(ArchivoDobby::isModificado).toList();
        if (!pendientes.isEmpty()) {
            List<String> lineas = new ArrayList<>();
            lineas.add(pendientes.size() == 1
                ? "Hay 1 archivo con cambios sin guardar:"
                : "Hay " + pendientes.size() + " archivos con cambios sin guardar:");
            for (int i = 0; i < Math.min(4, pendientes.size()); i++) {
                lineas.add("• " + pendientes.get(i).getNombre());
            }
            if (pendientes.size() > 4) {
                lineas.add("... y " + (pendientes.size() - 4) + " mas");
            }
            int eleccion = DialogoOpciones.elegir(vista, "Cambios sin guardar", lineas.toArray(new String[0]),
                "Guardar todo", "Salir sin guardar", "Cancelar");
            if (eleccion == 0) {
                if (!guardarTodo()) {
                    return;
                }
            } else if (eleccion != 1) {
                return;
            }
        }
        vista.dispose();
        System.exit(0);
    }

    public void manejarCompilar() {
        vista.getPanelSalida().limpiar();
        try {
            Enlace enlace = enlazarActivo();
            ResultadoEjecucion resultado = new ResultadoEjecucion();
            resultado.setExito(true);
            resultado.setSalida("Compilacion exitosa.");
            resultado.setErrores(new ArrayList<>());
            flowController.setUltimoResultado(resultado);

            vista.getPanelSalida().agregarMensaje("Compilacion exitosa: " + flowController.getArchivoActivo().getNombre());
            vista.getPanelSalida().agregarMensaje("Tokens encontrados: " + enlace.tokens());
            vista.getPanelSalida().agregarMensaje("Declaraciones principales: " + enlace.programa().getSentencias().size());
            if (enlace.archivosImportados() > 0) {
                vista.getPanelSalida().agregarMensaje("Archivos importados: " + enlace.archivosImportados());
            }
        } catch (RuntimeException e) {
            registrarError(e.getMessage());
        }
    }

    public void manejarEjecutar() {
        vista.getPanelSalida().limpiar();
        Enlace enlace;
        try {
            enlace = enlazarActivo();
        } catch (RuntimeException e) {
            registrarError(e.getMessage());
            return;
        }

        ResultadoEjecucion resultado = interprete.ejecutar(enlace.programa());
        flowController.setUltimoResultado(resultado);

        boolean haySalida = resultado.getSalida() != null && !resultado.getSalida().isEmpty();
        if (resultado.isExito()) {
            vista.getPanelSalida().agregarMensaje("Ejecucion exitosa.");
        }
        if (haySalida) {
            vista.getPanelSalida().agregarMensaje(resultado.getSalida().stripTrailing());
        }
        for (String error : resultado.getErrores()) {
            vista.getPanelSalida().agregarError(error);
        }
    }

    private Enlace enlazarActivo() {
        sincronizarBuffer();
        ArchivoDobby activo = flowController.getArchivoActivo();
        String codigo = activo.getContenido();
        if (codigo == null || codigo.isBlank()) {
            throw new ErrorEnlace("No hay codigo para compilar.");
        }
        Proyecto proyecto = flowController.getProyectoActivo();
        Path carpetaBase = proyecto != null ? proyecto.getCarpeta() : null;
        return new Enlazador(this::leerCodigo).enlazar(activo.getRuta(), codigo, carpetaBase);
    }

    private String leerCodigo(Path ruta) throws IOException {
        ArchivoDobby abierto = flowController.buscarAbierto(ruta);
        if (abierto != null) {
            return textoDe(abierto);
        }
        return FileUtil.leerArchivo(ruta);
    }

    private void registrarError(String mensaje) {
        ResultadoEjecucion resultado = new ResultadoEjecucion();
        resultado.setExito(false);
        resultado.setSalida("");
        resultado.setErrores(List.of(mensaje));
        flowController.setUltimoResultado(resultado);
        vista.getPanelSalida().agregarError(mensaje);
    }

    private List<ArchivoDobby> archivosAbiertos() {
        return flowController.getArchivosAbiertos();
    }

    private String textoDe(ArchivoDobby archivo) {
        if (archivo == flowController.getArchivoActivo()) {
            return vista.getPanelEditor().getTexto();
        }
        return archivo.getContenido() != null ? archivo.getContenido() : "";
    }

    private void sincronizarBuffer() {
        ArchivoDobby activo = flowController.getArchivoActivo();
        if (activo != null) {
            activo.setContenido(vista.getPanelEditor().getTexto());
            activo.setPosicionCursor(vista.getPanelEditor().getPosicionCursor());
        }
    }

    private ArchivoDobby crearSinTitulo(String contenido) {
        contadorSinTitulo++;
        ArchivoDobby archivo = new ArchivoDobby(null);
        archivo.setNombre("sin_titulo_" + contadorSinTitulo);
        archivo.setContenido(contenido);
        archivo.setModificado(false);
        archivosAbiertos().add(archivo);
        activar(archivo);
        return archivo;
    }

    private void activar(ArchivoDobby archivo) {
        if (archivo == flowController.getArchivoActivo()) {
            vista.getPanelEditor().enfocar();
            return;
        }
        sincronizarBuffer();
        flowController.setArchivoActivo(archivo);
        vista.getPanelEditor().setTexto(archivo.getContenido() != null ? archivo.getContenido() : "");
        vista.getPanelEditor().setPosicionCursor(archivo.getPosicionCursor());
        vista.getPanelEditor().enfocar();
        refrescarVista();
    }

    private void alEditar() {
        ArchivoDobby activo = flowController.getArchivoActivo();
        if (activo != null && !activo.isModificado()) {
            activo.setModificado(true);
            refrescarVista();
        }
    }

    private void refrescarVista() {
        ArchivoDobby activo = flowController.getArchivoActivo();
        vista.getPanelPestanas().refrescar(archivosAbiertos(), activo);

        boolean conRuta = activo != null && activo.getRuta() != null;
        vista.setTitle(conRuta ? "Dobby - " + activo.getNombre() + (activo.isModificado() ? " *" : "") : "Dobby");

        Set<Path> modificadas = new HashSet<>();
        for (ArchivoDobby archivo : archivosAbiertos()) {
            if (archivo.isModificado() && archivo.getRuta() != null) {
                modificadas.add(archivo.getRuta());
            }
        }
        vista.getPanelArbolArchivos().actualizarEstado(conRuta ? activo.getRuta() : null, modificadas);
    }

    private void abrirRuta(Path rutaOriginal) {
        Path ruta = normalizar(rutaOriginal);
        ArchivoDobby existente = flowController.buscarAbierto(ruta);
        if (existente != null) {
            activar(existente);
            return;
        }

        String contenido;
        try {
            contenido = FileUtil.leerArchivo(ruta);
        } catch (IOException e) {
            vista.getPanelSalida().limpiar();
            vista.getPanelSalida().agregarError("No se pudo abrir el archivo: " + e.getMessage());
            return;
        }

        ArchivoDobby descartable = sinTituloVacioUnico();
        ArchivoDobby archivo = new ArchivoDobby(ruta);
        archivo.setNombre(ruta.getFileName().toString());
        archivo.setContenido(contenido);
        archivo.setModificado(false);
        archivosAbiertos().add(archivo);
        if (descartable != null) {
            archivosAbiertos().remove(descartable);
        }
        activar(archivo);
        asegurarProyecto(ruta);

        vista.getPanelSalida().limpiar();
        vista.getPanelSalida().agregarMensaje("Archivo abierto: " + archivo.getNombre());
    }

    private String pedirNombreArchivo(String titulo, String etiqueta, String inicial) {
        String texto = DialogoNombre.pedir(vista, titulo, etiqueta, inicial);
        if (texto == null) {
            return null;
        }
        String nombre = texto.trim();
        if (nombre.isEmpty()) {
            return null;
        }
        if (nombre.matches(".*[\\\\/:*?\"<>|].*")) {
            vista.getPanelSalida().limpiar();
            vista.getPanelSalida().agregarError("El nombre no puede contener \\ / : * ? \" < > |");
            return null;
        }
        return FileUtil.esArchivoDobby(Path.of(nombre)) ? nombre : nombre + ".dobby";
    }

    private void crearArchivoEn(Path carpeta) {
        String nombre = pedirNombreArchivo("Nuevo archivo", "Nombre del archivo", "nuevo");
        if (nombre == null) {
            return;
        }
        Path destino = normalizar(carpeta).resolve(nombre);
        if (Files.exists(destino)) {
            vista.getPanelSalida().limpiar();
            vista.getPanelSalida().agregarError("Ya existe un archivo llamado " + nombre);
            return;
        }
        try {
            FileUtil.escribirArchivo(destino, PLANTILLA_NUEVO);
        } catch (IOException e) {
            vista.getPanelSalida().limpiar();
            vista.getPanelSalida().agregarError("No se pudo crear el archivo: " + e.getMessage());
            return;
        }
        abrirRuta(destino);
        vista.getPanelSalida().agregarMensaje("Archivo creado: " + nombre);
    }

    private void renombrarArchivo(Path rutaOriginal) {
        Path ruta = normalizar(rutaOriginal);
        String nombre = pedirNombreArchivo("Renombrar archivo", "Nuevo nombre", ruta.getFileName().toString());
        if (nombre == null || nombre.equals(ruta.getFileName().toString())) {
            return;
        }
        Path destino = ruta.resolveSibling(nombre);
        if (Files.exists(destino)) {
            vista.getPanelSalida().limpiar();
            vista.getPanelSalida().agregarError("Ya existe un archivo llamado " + nombre);
            return;
        }
        try {
            Files.move(ruta, destino);
        } catch (IOException e) {
            vista.getPanelSalida().limpiar();
            vista.getPanelSalida().agregarError("No se pudo renombrar el archivo: " + e.getMessage());
            return;
        }
        ArchivoDobby abierto = flowController.buscarAbierto(ruta);
        if (abierto != null) {
            abierto.setRuta(destino);
            abierto.setNombre(nombre);
        }
        refrescarProyectoActual();
        vista.getPanelSalida().limpiar();
        vista.getPanelSalida().agregarMensaje("Archivo renombrado: " + nombre);
        vista.getPanelSalida().agregarMensaje("Si otros archivos lo importan con Floo, actualiza la ruta.");
    }

    private void eliminarArchivo(Path rutaOriginal) {
        Path ruta = normalizar(rutaOriginal);
        String nombre = ruta.getFileName().toString();
        int eleccion = DialogoOpciones.elegir(vista, "Eliminar archivo",
            new String[]{"¿Eliminar «" + nombre + "»?", "Esta accion no se puede deshacer."},
            "Eliminar", "Cancelar");
        if (eleccion != 0) {
            return;
        }
        try {
            Files.deleteIfExists(ruta);
        } catch (IOException e) {
            vista.getPanelSalida().limpiar();
            vista.getPanelSalida().agregarError("No se pudo eliminar el archivo: " + e.getMessage());
            return;
        }
        ArchivoDobby abierto = flowController.buscarAbierto(ruta);
        if (abierto != null) {
            abierto.setModificado(false);
            quitarAbierto(abierto);
        }
        refrescarProyectoActual();
        vista.getPanelSalida().limpiar();
        vista.getPanelSalida().agregarMensaje("Archivo eliminado: " + nombre);
    }

    private void refrescarProyectoActual() {
        Proyecto proyecto = flowController.getProyectoActivo();
        if (proyecto != null) {
            cargarProyecto(proyecto.getCarpeta());
        } else {
            refrescarVista();
        }
    }

    private ArchivoDobby sinTituloVacioUnico() {
        if (archivosAbiertos().size() != 1) {
            return null;
        }
        ArchivoDobby unico = archivosAbiertos().get(0);
        boolean vacio = unico.getRuta() == null && !unico.isModificado() && textoDe(unico).isBlank();
        return vacio ? unico : null;
    }

    private void asegurarProyecto(Path ruta) {
        Proyecto proyecto = flowController.getProyectoActivo();
        if (proyecto == null) {
            cargarProyecto(ruta.getParent());
        } else if (ruta.startsWith(proyecto.getCarpeta())) {
            cargarProyecto(proyecto.getCarpeta());
        }
    }

    private Proyecto cargarProyecto(Path carpetaOriginal) {
        Path carpeta = normalizar(carpetaOriginal);
        List<Path> rutas;
        try {
            rutas = FileUtil.listarArchivosDobbyRecursivo(carpeta, PROFUNDIDAD_PROYECTO);
        } catch (IOException | UncheckedIOException e) {
            vista.getPanelSalida().limpiar();
            vista.getPanelSalida().agregarError("No se pudo leer la carpeta: " + carpeta);
            return null;
        }

        Path nombreCarpeta = carpeta.getFileName();
        Proyecto proyecto = new Proyecto(nombreCarpeta != null ? nombreCarpeta.toString() : carpeta.toString(), carpeta);
        for (Path ruta : rutas) {
            ArchivoDobby archivo = new ArchivoDobby(ruta);
            archivo.setNombre(ruta.getFileName().toString());
            proyecto.agregarArchivo(archivo);
        }
        flowController.setProyectoActivo(proyecto);
        vista.getPanelArbolArchivos().cargarProyecto(proyecto);
        refrescarVista();
        return proyecto;
    }

    private boolean guardar(ArchivoDobby archivo, boolean forzarDialogo) {
        if (archivo == flowController.getArchivoActivo()) {
            sincronizarBuffer();
        }
        Path destino = archivo.getRuta();
        if (destino == null || forzarDialogo) {
            destino = elegirDestino(archivo);
            if (destino == null) {
                return false;
            }
        }

        try {
            FileUtil.escribirArchivo(destino, archivo.getContenido() != null ? archivo.getContenido() : "");
        } catch (IOException e) {
            vista.getPanelSalida().agregarError("No se pudo guardar el archivo: " + e.getMessage());
            return false;
        }

        ArchivoDobby otro = flowController.buscarAbierto(destino);
        if (otro != null && otro != archivo) {
            quitarAbierto(otro);
        }
        archivo.setRuta(destino);
        archivo.setNombre(destino.getFileName().toString());
        archivo.setModificado(false);
        asegurarProyecto(destino);
        refrescarVista();
        vista.getPanelSalida().agregarMensaje("Archivo guardado: " + archivo.getNombre());
        return true;
    }

    private boolean guardarTodo() {
        sincronizarBuffer();
        for (ArchivoDobby archivo : new ArrayList<>(archivosAbiertos())) {
            if (archivo.isModificado() && !guardar(archivo, false)) {
                return false;
            }
        }
        return true;
    }

    private Path elegirDestino(ArchivoDobby archivo) {
        JFileChooser selector = new JFileChooser(directorioInicial());
        selector.setDialogTitle("Guardar archivo Dobby");
        selector.setFileFilter(new FileNameExtensionFilter("Archivos Dobby (*.dobby)", "dobby"));
        String sugerido = archivo.getNombre();
        if (!sugerido.endsWith(".dobby")) {
            sugerido += ".dobby";
        }
        selector.setSelectedFile(new File(selector.getCurrentDirectory(), sugerido));
        if (selector.showSaveDialog(vista) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        Path ruta = normalizar(selector.getSelectedFile().toPath());
        if (!FileUtil.esArchivoDobby(ruta)) {
            ruta = ruta.resolveSibling(ruta.getFileName() + ".dobby");
        }
        if (Files.exists(ruta) && !ruta.equals(archivo.getRuta())) {
            int eleccion = DialogoOpciones.elegir(vista, "Sobrescribir archivo",
                new String[]{"El archivo «" + ruta.getFileName() + "» ya existe.", "¿Quieres reemplazarlo?"},
                "Sobrescribir", "Cancelar");
            if (eleccion != 0) {
                return null;
            }
        }
        return ruta;
    }

    private void cerrarArchivo(ArchivoDobby archivo) {
        if (archivo == flowController.getArchivoActivo()) {
            sincronizarBuffer();
        }
        if (archivo.isModificado()) {
            int eleccion = DialogoOpciones.elegir(vista, "Cambios sin guardar",
                new String[]{"El archivo «" + archivo.getNombre() + "» tiene cambios sin guardar.",
                    "¿Quieres guardarlos antes de cerrar?"},
                "Guardar", "No guardar", "Cancelar");
            if (eleccion == 0) {
                if (!guardar(archivo, false)) {
                    return;
                }
            } else if (eleccion != 1) {
                return;
            }
        }
        quitarAbierto(archivo);
    }

    private void quitarAbierto(ArchivoDobby archivo) {
        List<ArchivoDobby> abiertos = archivosAbiertos();
        int indice = abiertos.indexOf(archivo);
        if (indice < 0) {
            return;
        }
        abiertos.remove(indice);
        if (flowController.getArchivoActivo() != archivo) {
            refrescarVista();
            return;
        }
        flowController.setArchivoActivo(null);
        if (abiertos.isEmpty()) {
            crearSinTitulo("");
        } else {
            activar(abiertos.get(Math.min(indice, abiertos.size() - 1)));
        }
    }

    private File directorioInicial() {
        Proyecto proyecto = flowController.getProyectoActivo();
        if (proyecto != null) {
            return proyecto.getCarpeta().toFile();
        }
        ArchivoDobby activo = flowController.getArchivoActivo();
        if (activo != null && activo.getRuta() != null) {
            return activo.getRuta().getParent().toFile();
        }
        return null;
    }

    private Path normalizar(Path ruta) {
        return ruta.toAbsolutePath().normalize();
    }
}
