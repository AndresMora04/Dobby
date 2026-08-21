# Dobby

Lenguaje de programación educativo con temática de Harry Potter, desarrollado
como proyecto para el curso de Paradigmas de Programación (UNA, Sede Regional
Brunca - Campus Pérez Zeledón, prof. Pablo A. Venegas Elizondo). Incluye un
lenguaje propio, un compilador/intérprete y un entorno gráfico de escritura
de código (mini-IDE) construido en Java con Swing.

## Integrantes

- José Andrés Mora Mora
- Darien Arroyo Castro

## Documentación

La documentación formal del proyecto (planteamiento del problema, propuesta
de solución, pruebas, etc., según el enunciado del curso) se lleva en Google
Drive:
[Documento de Dobby](https://docs.google.com/document/d/19L3MUmLLpp1Ip35WHBakqJbFW2lt02fOkEJsLRYRW6Y/edit?usp=sharing)

Este README es la referencia técnica de trabajo del equipo (diseño del
lenguaje + estructura del proyecto de software); no sustituye la
documentación formal en PDF que exige el curso.

## Problema y motivación

- La sintaxis técnica en inglés de los lenguajes de programación
  tradicionales es una barrera de entrada real para quienes empiezan a
  programar (estudiantes de secundaria, autodidactas). Dobby usa una
  temática familiar (Harry Potter) para bajar esa barrera cognitiva y
  emocional.
- Un editor de código es una herramienta 100% visual por naturaleza, lo que
  excluye a personas con discapacidad visual. Esta es la segunda motivación
  del proyecto y da pie a la innovación (ver abajo).

## Innovación

Característica adicional exigida por el enunciado (no puede ser solo
estética): **accesibilidad para personas con discapacidad visual**.

- **Texto-a-voz (TTS)**: al compilar o ejecutar, además de mostrarse en el
  panel de salida, el resultado (éxito, error de sintaxis/semántico/de
  ejecución) se lee en voz alta invocando el motor de voz del sistema
  operativo (`System.Speech` vía PowerShell en Windows, `espeak` en Linux),
  sin agregar dependencias externas al proyecto (JDK estándar + `ProcessBuilder`).
- **Navegación completa por teclado**: Compilar, Ejecutar, Nuevo, Abrir,
  Guardar y cambio de archivo activo deben poder invocarse sin usar el mouse
  (mnemonics/atajos).
- **Plus opcional** (solo si sobra tiempo): compatibilidad con lectores de
  pantalla externos vía `javax.accessibility` (Java Access Bridge).
- Advertencia técnica para la defensa: `espeak` no siempre viene instalado
  por defecto en Linux, y el primer llamado a `System.Speech` en Windows
  puede tardar un poco en cargar. Probar en la máquina de la presentación
  antes del día, y que la app siga funcionando (sin audio) si el motor de
  voz no está disponible.

## Requisitos

- JDK 21 o superior
- Apache Maven 3.9 o superior

## Cómo compilar y ejecutar

Compilar el proyecto:

```
mvn compile
```

Ejecutar la aplicación (abre la ventana principal del IDE):

```
mvn compile exec:java
```

Generar el `.jar` ejecutable:

```
mvn package
```

## Identidad del lenguaje

- **Nombre**: Dobby
- **Extensión de archivo**: `.dobby`
- **Propósito**: enseñar los fundamentos de la programación de forma
  memorable y accesible.
- **Usuarios**: estudiantes principiantes y personas con discapacidad
  visual.
- **Filosofía de diseño**: cada palabra reservada es un hechizo o lugar
  reconocible del universo de Harry Potter, elegido para que su significado
  como instrucción de programación sea intuitivo de recordar.

## Palabras reservadas

| Palabra reservada | Propósito | Forma de uso | Ejemplo |
|---|---|---|---|
| `Alohomora` | Declarar variable | `Alohomora nombre: Tipo;` | `Alohomora edad: Entero;` |
| `Expecto` | Declarar función | `Expecto nombre(params): Tipo { ... }` | `Expecto sumar(a: Entero, b: Entero): Entero { ... }` |
| `Patronum` | Retornar un valor (return) | `Patronum expresion;` | `Patronum a + b;` |
| `Revelio` | Salida de datos (imprimir) | `Revelio expresion;` | `Revelio "Hola";` |
| `Legilimens` | Entrada de datos (input) | `variable = Legilimens();` | `edad = Legilimens();` |
| `Accio` | Llamar función | `Accio nombre(args);` | `Accio sumar(2, 3);` |
| `Wingardium` | Ciclo `for` | `Wingardium (init; cond; paso) { ... }` | `Wingardium (i = 0; i < 10; i = i + 1) { ... }` |
| `Imperio` | Ciclo `while` | `Imperio (condicion) { ... }` | `Imperio (mana < 100) { mana = mana + 1; }` |
| `Protego` / `Finite` | Condicional (`if` / `else`) | `Protego (cond) { ... } Finite { ... }` | ver sección Sintaxis |
| `Expelliarmus` | Romper ciclo (`break`) | `Expelliarmus;` | `Expelliarmus;` |
| `Reparo` | Continuar ciclo (`continue`) | `Reparo;` | `Reparo;` |
| `Lumos` / `Nox` | Booleanos verdadero / falso | literal | `activo = Lumos;` |
| `Hogwarts` | Función principal (punto de entrada) | `Hogwarts() { ... }` | `Hogwarts() { ... }` |
| `Gringotts` | Tipo compuesto: arreglo | `Gringotts<Tipo> nombre;` | `Gringotts<Entero> numeros;` |
| `Varita` | Tipo compuesto: struct | `Varita Nombre { campo: Tipo; ... }` | `Varita Punto { x: Entero; y: Entero; }` |
| `Floo` | Importar función de otro archivo | `Floo funcion desde "archivo.dobby";` | `Floo sumar desde "operaciones.dobby";` |
| `Obliviate` | Tipo/valor nulo | literal / tipo | `Alohomora resultado: Obliviate;` |

> Nota de diseño: Dobby no usa una palabra reservada para la asignación en
> sí (`=`), solo para la declaración con tipo (`Alohomora`).

## Sintaxis (ejemplos)

**Condicionales anidados (if / else / else-if — el "condicional múltiple"
se resuelve anidando `Protego`/`Finite`, sin necesitar una palabra
reservada adicional tipo `switch`):**

```
Protego (vida > 0) {
    Imperio (mana < 100) {
        mana = mana + 1;
    }
} Finite {
    Protego (vida == 0) {
        Revelio "Has caído en batalla";
    } Finite {
        Revelio "Estado desconocido";
    }
}
```

**Función con parámetros y retorno, y función principal:**

```
Expecto sumar(a: Entero, b: Entero): Entero {
    Patronum a + b;
}

Hogwarts() {
    Alohomora edad: Entero;
    edad = Legilimens();
    Protego (edad >= 11) {
        Revelio "Puede entrar a Hogwarts";
    } Finite {
        Revelio "Muy joven para Hogwarts";
    }
}
```

**Importación entre archivos:**

```
Floo sumar desde "operaciones.dobby";

Hogwarts() {
    Revelio Accio sumar(2, 3);
}
```

## Operaciones

- **Aritméticas**: `+`, `-`, `*`, `/`
- **Relacionales**: `==`, `!=`, `>`, `<`, `>=`, `<=`
- **Lógicas**: `&&` (y), `||` (o), `!` (no)

## Semántica

- Todo programa inicia con el bloque `Hogwarts() { }`, la función principal.
- Toda variable debe declararse con `Alohomora` antes de usarse.
- Toda función `Expecto` que no sea de tipo `Obliviate` (vacío) debe incluir
  un `Patronum` antes de cerrar su cuerpo.
- Los bloques de código se delimitan con llaves `{ }`; la indentación no es
  obligatoria (solo estética).
- No se puede llamar (`Accio`) una función que no haya sido declarada
  previamente con `Expecto`, ni usar una variable no declarada con
  `Alohomora`.
- Las importaciones (`Floo`) se resuelven en tiempo de compilación; el
  compilador debe validar: existencia del archivo, existencia de la
  función, cantidad de argumentos, compatibilidad de tipos, declaraciones
  duplicadas e importaciones circulares.

## Tipos de datos

**Simples (6 — supera el mínimo de 5 exigido, incluyendo el tipo nulo):**

| Tipo Dobby | Equivalente |
|---|---|
| Entero | int |
| Decimal | float |
| Booleano | bool |
| Texto | string |
| Caracter | char |
| Obliviate | null |

**Compuestos (2):**

| Tipo Dobby | Equivalente |
|---|---|
| Gringotts | array/lista |
| Varita | struct |

## Menú de opciones (requisito del entorno gráfico)

El entorno debe permitir consultar, sin salir de la aplicación: palabras
reservadas, sintaxis, estructuras de control, funciones, operaciones,
semántica, tipos de datos, importaciones y ejemplos — todo el contenido de
este README debe quedar accesible también desde ahí.

## Estructura del proyecto

```
Dobby/
├── pom.xml
├── README.md
└── src/main/java/dobby/
    ├── Main.java
    ├── vista/
    │   ├── MainView.java
    │   ├── FileTreePanel.java
    │   ├── EditorPanel.java
    │   ├── OutputPanel.java
    │   └── DocumentacionDialog.java      # Menú de opciones / consulta del lenguaje
    ├── controlador/
    │   └── Controlador.java
    ├── flow/
    │   └── FlowController.java
    ├── util/
    │   ├── FileUtil.java
    │   ├── ErrorFormatter.java
    │   ├── PalabrasReservadas.java
    │   └── LectorVoz.java                # Innovación: accesibilidad por voz (TTS)
    ├── modelo/
    │   ├── Proyecto.java
    │   └── ArchivoDobby.java
    └── motor/
        ├── lexer/
        │   ├── Token.java
        │   ├── TipoToken.java
        │   └── Lexer.java
        ├── parser/
        │   ├── Nodo.java
        │   ├── NodoPrograma.java
        │   ├── NodoSi.java                    # Protego / Finite
        │   ├── NodoMientras.java              # Imperio (while)
        │   ├── NodoPara.java                  # Wingardium (for)
        │   ├── NodoFuncion.java               # Expecto / Hogwarts
        │   ├── NodoDeclaracionVariable.java    # Alohomora
        │   ├── NodoAsignacion.java
        │   ├── NodoLlamada.java               # Accio
        │   ├── NodoRetorno.java               # Patronum
        │   ├── NodoImpresion.java             # Revelio
        │   ├── NodoEntrada.java               # Legilimens
        │   ├── NodoImportacion.java           # Floo
        │   ├── NodoEstructura.java            # Varita
        │   └── Parser.java
        └── interprete/
            ├── Interprete.java
            └── ResultadoEjecucion.java
```

## Estado actual

Esqueleto del proyecto: estructura de paquetes y clases con firmas de
métodos (sin comentarios, por preferencia del equipo). El esqueleto ya
incluye un nodo de AST por cada construcción de la tabla de palabras
reservadas. `PalabrasReservadas.java` todavía no tiene cargado el mapa de
palabras (pendiente, es lógica). La lógica de tokenización, parseo e
interpretación aún no está implementada.
