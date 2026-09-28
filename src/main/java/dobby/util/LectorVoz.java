package dobby.util;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public final class LectorVoz {
    private static final int MAX_CARACTERES = 8000;
    private static final String VARIABLE_TEXTO = "DOBBY_TTS_TEXTO";
    private static final LinkedBlockingDeque<String> COLA = new LinkedBlockingDeque<>();

    private static volatile boolean habilitado = false;
    private static volatile Boolean disponibleCache;
    private static volatile Process procesoActual;
    private static volatile String ultimoTexto;

    static {
        Thread hilo = new Thread(LectorVoz::procesarCola, "LectorVoz");
        hilo.setDaemon(true);
        hilo.start();
    }

    private LectorVoz() {
    }

    public static void setHabilitado(boolean valor) {
        habilitado = valor;
        if (!valor) {
            detener();
        }
    }

    public static boolean isHabilitado() {
        return habilitado;
    }

    public static boolean disponible() {
        if (disponibleCache == null) {
            disponibleCache = calcularDisponibilidad();
        }
        return disponibleCache;
    }

    public static void leer(String texto) {
        if (!habilitado || texto == null || texto.isBlank() || !disponible()) {
            return;
        }
        ultimoTexto = texto;
        detener();
        COLA.add(recortar(TraductorCodigo.mensajeATextoHablado(texto)));
    }

    public static void repetir() {
        String texto = ultimoTexto;
        if (texto != null) {
            leer(texto);
        }
    }

    public static void detener() {
        COLA.clear();
        Process proceso = procesoActual;
        if (proceso != null) {
            proceso.destroyForcibly();
        }
    }

    private static void procesarCola() {
        while (true) {
            try {
                String texto = COLA.take();
                if (!habilitado) {
                    continue;
                }
                ProcessBuilder builder = new ProcessBuilder(construirComando(texto));
                builder.environment().put(VARIABLE_TEXTO, texto);
                builder.redirectErrorStream(true);
                Process proceso = builder.start();
                procesoActual = proceso;
                proceso.waitFor();
                procesoActual = null;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (IOException e) {
                procesoActual = null;
            }
        }
    }

    private static String recortar(String texto) {
        String limpio = texto.replaceAll("\\s+", " ").trim();
        return limpio.length() > MAX_CARACTERES ? limpio.substring(0, MAX_CARACTERES) + "..." : limpio;
    }

    private static List<String> construirComando(String texto) {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) {
            String script = "Add-Type -AssemblyName System.Speech; "
                + "$s = New-Object System.Speech.Synthesis.SpeechSynthesizer; "
                + "$voz = $s.GetInstalledVoices() | Where-Object { $_.Enabled -and "
                + "$_.VoiceInfo.Culture.TwoLetterISOLanguageName -eq 'es' } | Select-Object -First 1; "
                + "if ($voz) { $s.SelectVoice($voz.VoiceInfo.Name) }; "
                + "$s.Speak($env:" + VARIABLE_TEXTO + ")";
            return List.of("powershell", "-NoProfile", "-NonInteractive", "-WindowStyle", "Hidden", "-Command", script);
        }
        if (os.contains("mac")) {
            return List.of("say", texto);
        }
        return List.of("espeak", "-v", "es", texto);
    }

    private static boolean calcularDisponibilidad() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) {
            return true;
        }
        String comando = os.contains("mac") ? "say" : "espeak";
        try {
            Process proceso = new ProcessBuilder("which", comando).redirectErrorStream(true).start();
            boolean termino = proceso.waitFor(2, TimeUnit.SECONDS);
            return termino && proceso.exitValue() == 0;
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
