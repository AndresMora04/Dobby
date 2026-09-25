package dobby.vista;

public final class EjemplosDobby {
    private EjemplosDobby() {
    }

    public record Ejemplo(String titulo, String descripcion, String codigo) {
        @Override
        public String toString() {
            return titulo;
        }
    }

    public static final Ejemplo[] EJEMPLOS = {
        new Ejemplo("Estructuras (Varita)",
            "Programa completo con campos, arreglos y una funcion. Va en un archivo vacio.",
            """
            Varita Alumno {
                nombre: Texto;
                nota: Decimal;
            }

            Expecto subirNota(alumno: Alumno, puntos: Decimal): Obliviate {
                alumno.nota = alumno.nota + puntos;
            }

            Hogwarts() {
                Gringotts<Alumno> alumnos = [
                    Alumno {nombre: "Harry", nota: 80},
                    Alumno {nombre: "Hermione", nota: 95}
                ];
                Accio subirNota(alumnos[0], 5);
                Wingardium (i = 0; i < alumnos.longitud; i = i + 1) {
                    Revelio alumnos[i].nombre + ": " + alumnos[i].nota;
                }
            }
            """),
        new Ejemplo("Arreglos (Gringotts)",
            "Crear, modificar y recorrer un arreglo de notas.",
            """
            Alohomora notas: Gringotts<Entero> = [80, 90, 100];
            notas[1] = 95;
            Alohomora total: Entero = 0;
            Wingardium (i = 0; i < notas.longitud; i = i + 1) {
                Revelio notas[i];
                total = total + notas[i];
            }
            Revelio "Total: " + total;
            """),
        new Ejemplo("Arreglos anidados",
            "Un Gringotts que contiene otros arreglos.",
            """
            Gringotts<Gringotts<Entero>> tabla = [[1, 2], [3, 4]];
            tabla[0][1] = 9;
            Revelio tabla;
            Revelio tabla[1].longitud;
            """),
        new Ejemplo("Funcion con arreglo",
            "Recibe un arreglo y devuelve la suma de sus elementos. Va fuera de Hogwarts.",
            """
            Expecto sumarNotas(notas: Gringotts<Entero>): Entero {
                Alohomora total: Entero = 0;
                Wingardium (i = 0; i < notas.longitud; i = i + 1) {
                    total = total + notas[i];
                }
                Patronum total;
            }
            """),
        new Ejemplo("Función principal (Hogwarts)",
            "Punto de entrada: todo programa empieza aquí.",
            "Hogwarts() {\n    Revelio \"Hola, mundo\";\n}\n"),
        new Ejemplo("Función con retorno (Expecto)",
            "Declara una función con parámetros que devuelve un valor. Va fuera de Hogwarts.",
            "Expecto sumar(a: Entero, b: Entero): Entero {\n    Patronum a + b;\n}\n"),
        new Ejemplo("Función sin retorno (Obliviate)",
            "Función que no devuelve nada: su tipo es Obliviate. Va fuera de Hogwarts.",
            "Expecto saludar(nombre: Texto): Obliviate {\n    Revelio \"Hola, \" + nombre;\n}\n"),
        new Ejemplo("Función recursiva",
            "Una función que se llama a sí misma. Va fuera de Hogwarts.",
            "Expecto factorial(n: Entero): Entero {\n    Protego (n <= 1) {\n        Patronum 1;\n    }\n    Patronum n * Accio factorial(n - 1);\n}\n"),
        new Ejemplo("Importar función (Floo)",
            "Usa una función de otro archivo. Va al inicio del archivo, una línea por función.",
            "Floo sumar desde \"operaciones.dobby\";\n"),
        new Ejemplo("Llamar función (Accio)",
            "Llama a una función y guarda su resultado. Requiere la función sumar.",
            "Alohomora resultado: Entero;\nresultado = Accio sumar(2, 3);\nRevelio resultado;\n"),
        new Ejemplo("Llamar sin usar el valor",
            "Accio como sentencia. Requiere la función saludar.",
            "Accio saludar(\"Harry\");\n"),
        new Ejemplo("Declarar variable (Alohomora)",
            "Declara con tipo y luego asigna un valor.",
            "Alohomora edad: Entero;\nedad = 11;\n"),
        new Ejemplo("Variables de cada tipo",
            "Entero, Decimal, Texto y Booleano (Lumos / Nox).",
            "Alohomora edad: Entero;\nAlohomora precio: Decimal;\nAlohomora nombre: Texto;\nAlohomora activo: Booleano;\nedad = 11;\nprecio = 9.5;\nnombre = \"Harry\";\nactivo = Lumos;\n"),
        new Ejemplo("Imprimir (Revelio)",
            "Muestra un valor en el panel de salida.",
            "Revelio \"Hola\";\n"),
        new Ejemplo("Unir textos",
            "El operador + une textos entre sí o con números.",
            "Alohomora nombre: Texto;\nnombre = \"Harry\";\nRevelio \"Hola, \" + nombre + \"!\";\n"),
        new Ejemplo("Entrada de datos (Legilimens)",
            "Pide un valor al usuario; se convierte al tipo de la variable.",
            "Alohomora edad: Entero;\nedad = Legilimens();\nRevelio edad;\n"),
        new Ejemplo("Operaciones aritméticas",
            "Suma, resta, multiplicación y división, con paréntesis.",
            "Alohomora total: Entero;\ntotal = (10 + 5) * 2;\nRevelio total;\n"),
        new Ejemplo("Condicional (Protego / Finite)",
            "Ejecuta un bloque u otro según una condición.",
            "Alohomora edad: Entero;\nedad = 11;\nProtego (edad >= 11) {\n    Revelio \"Puede entrar a Hogwarts\";\n} Finite {\n    Revelio \"Muy joven para Hogwarts\";\n}\n"),
        new Ejemplo("Condicional anidado (else if)",
            "Varias condiciones encadenadas anidando Protego dentro de Finite.",
            "Alohomora nota: Entero;\nnota = 85;\nProtego (nota >= 90) {\n    Revelio \"Excelente\";\n} Finite {\n    Protego (nota >= 70) {\n        Revelio \"Aprobado\";\n    } Finite {\n        Revelio \"Reprobado\";\n    }\n}\n"),
        new Ejemplo("Operadores lógicos",
            "&& (y), || (o) y ! (no) para combinar condiciones.",
            "Alohomora vida: Entero;\nAlohomora mana: Entero;\nvida = 50;\nmana = 20;\nProtego (vida > 0 && mana >= 10) {\n    Revelio \"Listo para el hechizo\";\n}\n"),
        new Ejemplo("Ciclo for (Wingardium)",
            "Repite un número conocido de veces. La variable del ciclo se declara sola.",
            "Wingardium (i = 0; i < 5; i = i + 1) {\n    Revelio i;\n}\n"),
        new Ejemplo("Ciclo while (Imperio)",
            "Repite mientras la condición sea verdadera.",
            "Alohomora mana: Entero;\nmana = 0;\nImperio (mana < 3) {\n    mana = mana + 1;\n    Revelio mana;\n}\n"),
        new Ejemplo("Romper ciclo (Expelliarmus)",
            "Sale del ciclo de inmediato.",
            "Wingardium (i = 0; i < 10; i = i + 1) {\n    Protego (i == 5) {\n        Expelliarmus;\n    }\n    Revelio i;\n}\n"),
        new Ejemplo("Continuar ciclo (Reparo)",
            "Salta a la siguiente vuelta del ciclo.",
            "Wingardium (i = 0; i < 5; i = i + 1) {\n    Protego (i == 2) {\n        Reparo;\n    }\n    Revelio i;\n}\n"),
    };
}
