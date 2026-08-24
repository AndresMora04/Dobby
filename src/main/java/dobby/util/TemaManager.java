package dobby.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public final class TemaManager {

    public enum Casa {
        NEUTRA("Neutro", new Color(214, 176, 96), null),
        GRYFFINDOR("Gryffindor", new Color(174, 54, 54), "/assets/imagenes/Gryffindor.png"),
        SLYTHERIN("Slytherin", new Color(42, 110, 66), "/assets/imagenes/Slytherin.png"),
        RAVENCLAW("Ravenclaw", new Color(48, 84, 158), "/assets/imagenes/Ravenclaw.png"),
        HUFFLEPUFF("Hufflepuff", new Color(196, 158, 40), "/assets/imagenes/Hufflepuff.png");

        private final String etiqueta;
        private final Color color;
        private final String archivoEscudo;

        Casa(String etiqueta, Color color, String archivoEscudo) {
            this.etiqueta = etiqueta;
            this.color = color;
            this.archivoEscudo = archivoEscudo;
        }

        public String getEtiqueta() {
            return etiqueta;
        }

        public Color getColor() {
            return color;
        }

        public String getArchivoEscudo() {
            return archivoEscudo;
        }
    }

    private static final TemaManager INSTANCIA = new TemaManager();

    private Casa casaActual = Casa.NEUTRA;
    private final List<Runnable> oyentes = new ArrayList<>();

    private TemaManager() {
    }

    public static TemaManager getInstancia() {
        return INSTANCIA;
    }

    public Casa getCasaActual() {
        return casaActual;
    }

    public Color getColorAcento() {
        return casaActual.getColor();
    }

    public Color getColorAcentoClaro() {
        return casaActual.getColor().brighter();
    }

    public void setCasaActual(Casa casa) {
        this.casaActual = casa;
        for (Runnable oyente : oyentes) {
            oyente.run();
        }
    }

    public void agregarOyente(Runnable oyente) {
        oyentes.add(oyente);
    }
}
