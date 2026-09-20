package dobby.vista;

public final class EjemplosDobby {
    private EjemplosDobby() {
    }

    public record Ejemplo(String titulo, String codigo) {
        @Override
        public String toString() {
            return titulo;
        }
    }

    public static final Ejemplo[] EJEMPLOS = {
        new Ejemplo("Función principal", "Hogwarts() {\n    Revelio \"Hola, mundo\";\n}\n"),
        new Ejemplo("Declarar variable", "Alohomora edad: Entero;\nedad = 11;\n"),
        new Ejemplo("Imprimir", "Revelio \"Hola\";\n"),
        new Ejemplo("Entrada de datos", "Alohomora edad: Entero;\nedad = Legilimens();\n"),
        new Ejemplo("Función con retorno", "Expecto sumar(a: Entero, b: Entero): Entero {\n    Patronum a + b;\n}\n"),
        new Ejemplo("Llamar función", "Accio sumar(2, 3);\n"),
        new Ejemplo("Condicional if / else", "Protego (edad >= 11) {\n    Revelio \"Puede entrar a Hogwarts\";\n} Finite {\n    Revelio \"Muy joven para Hogwarts\";\n}\n"),
        new Ejemplo("Ciclo for", "Wingardium (i = 0; i < 10; i = i + 1) {\n    Revelio i;\n}\n"),
        new Ejemplo("Ciclo while", "Imperio (mana < 100) {\n    mana = mana + 1;\n}\n"),
        new Ejemplo("Romper ciclo", "Wingardium (i = 0; i < 10; i = i + 1) {\n    Protego (i == 5) {\n        Expelliarmus;\n    }\n}\n"),
        new Ejemplo("Continuar ciclo", "Wingardium (i = 0; i < 10; i = i + 1) {\n    Protego (i == 5) {\n        Reparo;\n    }\n    Revelio i;\n}\n"),
        new Ejemplo("Operadores lógicos", "Protego (vida > 0 && mana >= 10) {\n    Revelio \"Listo para el hechizo\";\n}\n"),
    };
}
