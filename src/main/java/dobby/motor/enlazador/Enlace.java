package dobby.motor.enlazador;

import dobby.motor.parser.NodoPrograma;

public record Enlace(NodoPrograma programa, int archivosImportados, int tokens) {
}
