package dobby.util;

import java.util.HashMap;
import java.util.Map;

public final class PalabrasReservadas {
    public static final Map<String, String> MAPA = construirMapa();

    private PalabrasReservadas() {
    }

    private static Map<String, String> construirMapa() {
        Map<String, String> mapa = new HashMap<>();
        mapa.put("Alohomora", "DECLARAR_VARIABLE");
        mapa.put("Expecto", "DECLARAR_FUNCION");
        mapa.put("Patronum", "RETORNO");
        mapa.put("Revelio", "IMPRIMIR");
        mapa.put("Legilimens", "ENTRADA");
        mapa.put("Accio", "LLAMAR_FUNCION");
        mapa.put("Wingardium", "PARA");
        mapa.put("Imperio", "MIENTRAS");
        mapa.put("Protego", "SI");
        mapa.put("Finite", "SINO");
        mapa.put("Expelliarmus", "ROMPER");
        mapa.put("Reparo", "CONTINUAR");
        mapa.put("Lumos", "VERDADERO");
        mapa.put("Nox", "FALSO");
        mapa.put("Hogwarts", "PRINCIPAL");
        mapa.put("Gringotts", "TIPO_ARREGLO");
        mapa.put("Varita", "TIPO_STRUCT");
        mapa.put("Floo", "IMPORTAR");
        mapa.put("desde", "DESDE");
        mapa.put("Obliviate", "TIPO_NULO");
        mapa.put("Entero", "TIPO_ENTERO");
        mapa.put("Decimal", "TIPO_DECIMAL");
        mapa.put("Booleano", "TIPO_BOOLEANO");
        mapa.put("Texto", "TIPO_TEXTO");
        mapa.put("Caracter", "TIPO_CARACTER");
        return mapa;
    }

    public static boolean esPalabraReservada(String palabra) {
        return MAPA.containsKey(palabra);
    }
}
