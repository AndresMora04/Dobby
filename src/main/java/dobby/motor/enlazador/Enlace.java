package dobby.motor.enlazador;

import dobby.motor.parser.NodoPrograma;

import java.util.List;

public record Enlace(NodoPrograma programa, int archivosImportados, int tokens, List<String> advertencias) {
}
