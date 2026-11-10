# Dobby

**Un lenguaje de programación con temática de Harry Potter, pensado para que aprender a programar se sienta menos como memorizar palabras clave en inglés y más como aprender hechizos.**

Dobby tiene tres piezas: un lenguaje de programación propio, un motor que lo compila y lo ejecuta de verdad (no es una simulación), y un pequeño entorno de desarrollo gráfico, construido todo en Java con Swing. Es un proyecto del curso Paradigmas de Programación, Universidad Nacional, Sede Regional Brunca, Campus Pérez Zeledón, a cargo del profesor Pablo A. Venegas Elizondo.

**Integrantes:** José Andrés Mora Mora y Darien Arroyo Castro.

---

## Cómo nació la idea

Aprender a programar en los primeros cursos es complicado, y no es solo por la lógica detrás de cada problema: también porque la sintaxis misma no tiene nada que atrape, nada que se quede en la cabeza.

Harry Potter no se eligió al azar. Es, probablemente, el universo de fantasía más reconocido del mundo, cruza generaciones y formatos (libros, películas, fandom), y ya trae consigo un vocabulario mágico memorable por diseño: `Accio`, `Alohomora`, `Expelliarmus` ya suenan a instrucciones antes de que nadie las explique. El universo también se presta solo: un hechizo es, literalmente, una instrucción; una función que retorna algo bien podría invocar su propio patronus. Así nació `Wingardium` en vez de `for`, `Imperio` en vez de `while`.

La segunda pregunta llegó después, pensando en quién se queda afuera de un editor de código tradicional: alguien con discapacidad visual. Un editor es, por diseño, una herramienta 100% visual. Ningún curso introductorio pensado solo para verse en pantalla incluye a esa persona. Esa fue la motivación real detrás de la innovación del proyecto: si ya se estaba construyendo un lenguaje pensado para bajar una barrera de entrada, tenía sentido que también bajara esa otra.

Así es como Dobby terminó siendo dos cosas a la vez: un lenguaje con identidad propia, memorable porque no se parece a los demás, y un entorno que se puede usar completo por voz y por teclado, sin depender de ver la pantalla.

---

## ¿Qué es Dobby, exactamente?

- Un **lenguaje de programación real**: tiene su propia sintaxis, su propia semántica, tipos simples y compuestos, funciones, condicionales, ciclos, y un sistema de proyectos con varios archivos que se importan entre sí.
- Un **motor de lenguaje** construido como lo haría un compilador de verdad: un *Lexer* que tokeniza el código, un *Parser* que arma el árbol de sintaxis por descenso recursivo, un *Enlazador* que resuelve las importaciones entre archivos y valida la semántica, y un *Intérprete* que recorre ese árbol y lo ejecuta.
- Un **mini-IDE en Swing**: editor con pestañas y resaltado de errores, árbol de archivos, panel de salida, y un sistema de ayuda integrado para consultar el lenguaje sin salir de la aplicación.
- Pensado para **estudiantes principiantes** (secundaria, un curso CS1, o autodidactas) y, de forma transversal, para **personas con discapacidad visual**, gracias a la accesibilidad por voz y teclado.

No está pensado para reemplazar un lenguaje de propósito general. Su objetivo es educativo: que alguien entienda cómo funciona un lenguaje de programación por dentro, construyendo uno desde cero.

---

## El lenguaje

### Identidad

- **Nombre:** Dobby
- **Extensión de archivo:** `.dobby`
- **Filosofía de diseño:** cada palabra reservada es un hechizo o un lugar reconocible del universo de Harry Potter, elegido para que su significado como instrucción sea fácil de recordar y no una palabra arbitraria más que memorizar.

### Palabras reservadas

| Palabra | Propósito | Forma de uso | Ejemplo |
|---|---|---|---|
| `Alohomora` | Declarar variable | `Alohomora nombre: Tipo;` | `Alohomora edad: Entero;` |
| `Expecto` | Declarar función | `Expecto nombre(params): Tipo { ... }` | `Expecto sumar(a: Entero, b: Entero): Entero { ... }` |
| `Patronum` | Retornar un valor (`return`) | `Patronum expresion;` | `Patronum a + b;` |
| `Revelio` | Salida de datos (imprimir) | `Revelio expresion;` | `Revelio "Hola";` |
| `Legilimens` | Entrada de datos (`input`) | `variable = Legilimens();` | `edad = Legilimens();` |
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
| `Floo` | Importar función o estructura de otro archivo | `Floo nombre desde "archivo.dobby";` | `Floo sumar desde "operaciones.dobby";` |
| `desde` | Conector de la importación | se usa junto a `Floo` | `Floo sumar desde "operaciones.dobby";` |
| `Obliviate` | Tipo de función sin retorno / valor nulo | literal / tipo | `Expecto f(): Obliviate { ... }` |

En total son **25 palabras reservadas**: las 17 filas de esta tabla (contando `Protego`/`Finite` y `Lumos`/`Nox` como dos palabras cada una, 20 en total) más los 5 nombres de tipo simple (`Entero`, `Decimal`, `Booleano`, `Texto`, `Caracter`), documentados abajo en Tipos de datos.

> Dobby no tiene una palabra reservada para la asignación en sí (`=`), solo para la declaración con tipo (`Alohomora`).

### Sintaxis, en un vistazo

**Condicionales anidados.** El condicional múltiple no tiene una palabra reservada propia (no hay un `switch`); se resuelve anidando `Protego` dentro de `Finite`:

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

**Función con parámetros y retorno, y la función principal:**

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

### Operaciones

- **Aritméticas:** `+`, `-`, `*`, `/`
- **Relacionales:** `==`, `!=`, `>`, `<`, `>=`, `<=`
- **Lógicas:** `&&` (y), `||` (o), `!` (no)

### Semántica

- Todo programa inicia en `Hogwarts() { }`, el único punto de entrada permitido por proyecto.
- Los bloques se delimitan con llaves `{ }`; la indentación no es obligatoria, solo estética.
- Toda variable debe declararse con `Alohomora` (o la forma abreviada de arreglo, `Gringotts<Tipo> nombre;`) antes de usarse. Leer una variable sin inicializar es un error.
- Dobby **no tiene alcance por bloque**: una variable declarada dentro de un `Protego` o de un `Imperio`/`Wingardium` sigue siendo visible en el resto de la función, no solo dentro de ese bloque. La regla real es más fina de lo que parece a primera vista: si se declara solo en una de las dos ramas de un `Protego`/`Finite`, el compilador no la considera declarada después (tiene que aparecer en ambas ramas); y si se declara dentro de un ciclo, no queda disponible una vez que el ciclo termina, porque no hay garantía de que el ciclo se haya ejecutado al menos una vez.
- Toda función `Expecto` que no sea de tipo `Obliviate` debe retornar un valor con `Patronum` en todos sus caminos posibles, o el compilador la rechaza.
- No se puede llamar (`Accio`) una función que no haya sido declarada ni importada.
- Las importaciones (`Floo`) se resuelven en tiempo de compilación, validando existencia del archivo, existencia de la función o estructura, cantidad de argumentos, compatibilidad de tipos, declaraciones duplicadas, importaciones circulares, y errores dentro del propio archivo importado.

### Tipos de datos

**Simples (5, más un tipo especial para valores nulos):**

| Tipo Dobby | Equivalente |
|---|---|
| `Entero` | int |
| `Decimal` | float |
| `Booleano` | bool |
| `Texto` | string |
| `Caracter` | char |
| `Obliviate` | tipo nulo / sin retorno |

**Compuestos (2):**

| Tipo Dobby | Equivalente |
|---|---|
| `Gringotts<Tipo>` | arreglo |
| `Varita` | struct |

#### Arreglos (`Gringotts`)

```text
Hogwarts() {
    Alohomora notas: Gringotts<Entero> = [80, 90, 100];
    notas[1] = 95;
    Alohomora total: Entero = 0;
    Wingardium (i = 0; i < notas.longitud; i = i + 1) {
        total = total + notas[i];
    }
    Revelio notas;
    Revelio total;
}
```

Salida: `[80, 95, 100]` y `275`.

- `longitud` es de solo lectura; un índice fuera de rango se reporta al ejecutar.
- Todos los elementos respetan el tipo declarado (`Decimal` admite literales enteros, `Texto` admite caracteres).
- Las asignaciones y los parámetros comparten referencia: modificar una posición desde una función también cambia el arreglo del llamador.
- Se admiten arreglos anidados (`Gringotts<Gringotts<Entero>> tabla = [[1, 2], [3, 4]];`) y arreglos de estructuras (`Gringotts<Punto>`).

#### Estructuras (`Varita`)

```text
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
```

Salida: `Harry: 85.0` y `Hermione: 95.0`. Este programa está disponible en **Ejemplos > Estructuras (Varita)**, listo para insertar en un archivo vacío.

- La creación usa `Nombre {campo: valor, otro: valor}`; todos los campos son obligatorios.
- Los tipos son nominales: dos estructuras con los mismos campos pero nombres distintos no son intercambiables.
- Se pueden encadenar accesos (`grupo.alumnos[0].nota`) y anidar structs dentro de structs.
- Para compartir un tipo entre archivos: `Floo Alumno desde "tipos.dobby";`. Importar una función o estructura también incorpora los tipos de los que depende.

---

## Accesibilidad: la innovación del proyecto

La característica adicional del proyecto es accesibilidad para personas con discapacidad visual, construida en dos piezas que funcionan juntas:

**Texto a voz.** Al compilar o ejecutar, el resultado se lee en voz alta además de mostrarse en pantalla, invocando el sintetizador de voz del propio sistema operativo (`System.Speech` vía PowerShell en Windows con selección automática de voz en español, `espeak` en Linux, `say` en macOS), sin agregar ninguna dependencia externa al proyecto. Está apagada por defecto y se activa con Ctrl+M. También se puede pedir que lea la línea actual (Ctrl+L) o el archivo completo (Ctrl+Shift+L). El código no se lee tal cual: se tokeniza con el mismo *Lexer* del lenguaje y cada símbolo (`{`, `;`, `==`, `&&`...) se traduce a su nombre hablado, para que una línea con símbolos se entienda escuchada y no solo leída. La misma traducción se aplica a los mensajes de error. Una lectura nueva siempre corta a la anterior en vez de acumularse, se puede repetir la última (Ctrl+R) o detener (menú Proyecto), y los diálogos de la aplicación (confirmaciones, `Legilimens`) anuncian su contenido apenas se abren y se cierran con Escape.

**Navegación completa por teclado.** Los tres menús de la barra (Archivo, Proyecto, Ayuda) y cada una de sus opciones tienen su propia letra mnemotécnica: toda la aplicación se puede operar con Alt y una letra, sin el mouse. Las pestañas del editor se cambian con las flechas ← → una vez que tienen el foco, y el árbol de archivos se abre con Enter y despliega su menú (renombrar, eliminar, nuevo archivo) con Shift+F10, igual que cualquier aplicación de escritorio convencional.

La disponibilidad real del sintetizador de voz se comprueba al iniciar (no se asume); si el sistema no tiene uno disponible, la aplicación lo avisa en Ajustes y sigue funcionando con normalidad, solo sin audio.

---

## El entorno gráfico

### Identidad visual

La idea fue transmitir el universo de Harry Potter usando directamente su estética, no solo su vocabulario: la mascota es el propio Dobby, el elfo doméstico, con una varita como accesorio, y la tipografía de los títulos está inspirada en el logo oficial de los libros.

- **Pantalla de bienvenida:** un cielo nocturno animado con estrellas, una luna con resplandor, la silueta de un castillo, chispas flotando del color de acento activo, y una snitch dorada que se mueve sola por la pantalla. La mascota queda centrada, con un aura que pulsa suavemente detrás.
- **Transición de andén:** entre la bienvenida y el entorno principal hay una animación propia con una frase aleatoria del universo de Harry Potter, con su atribución.
- **Selector de casa:** desde Ajustes se elige una de las cuatro casas de Hogwarts, lo que cambia el color de acento de toda la interfaz (bordes, archivo activo en el árbol, resaltados).
- **Cursor de varita:** al pasar sobre el editor o la bienvenida, el cursor del mouse se reemplaza por un ícono de varita.

Todo esto se construyó en Swing puro, sin ninguna librería gráfica externa: `javax.swing.Timer` para las animaciones (nunca `Thread.sleep()`, que bloquearía la interfaz), `Graphics2D` con antialiasing para las formas propias, y una clase `TemaManager` que centraliza el color de acento para que cambiar de casa actualice toda la interfaz desde un solo lugar.

### El editor en uso

El entorno principal tiene el árbol de archivos a la izquierda, el editor con pestañas y numeración de líneas arriba a la derecha, y el panel de salida abajo. Compilar y Ejecutar son pasos separados, con sus propios botones y atajos (F6 y F5).

Cuando algo falla, el error se muestra en el panel de salida con archivo, línea, descripción y una posible causa, **y además se resalta visualmente**: la línea exacta del error aparece marcada en rojo en el editor, con su número también en rojo en el margen, siempre que el error pertenezca al archivo que está abierto en la pestaña activa. La marca desaparece sola en cuanto se edita, se cambia de pestaña, o se vuelve a compilar sin ese error.

Además de errores (que detienen la compilación), Dobby detecta **advertencias** que no la detienen pero sí avisan en el panel de salida con el prefijo `[ADVERTENCIA]`: una variable declarada que nunca se lee, una función que nunca se llama desde ningún archivo del proyecto, y una importación con `Floo` que nunca se usa.

### Proyectos y archivo principal

Un proyecto es simplemente una carpeta con archivos `.dobby`. **Archivo > Nuevo proyecto...** crea una carpeta con un `principal.dobby` ya ejecutable; **Archivo > Abrir carpeta...** abre uno existente. El árbol permite crear, abrir, renombrar y eliminar archivos, y marca con `(principal)` el que contiene `Hogwarts()`, sin importar cómo se llame el archivo. Debe existir exactamente un `Hogwarts()` en todo el proyecto; si no hay ninguno, o hay más de uno, se avisa antes de intentar compilar nada. Compilar y Ejecutar siempre parten del archivo principal, incluso si la pestaña activa es otra.

### Un proyecto de ejemplo, listo para abrir

La carpeta [`ejemplos/calificaciones`](ejemplos/calificaciones) tiene un proyecto real de tres archivos, listo para abrir con **Archivo > Abrir carpeta...** y ejecutar con F5: importa un `Varita` y varias funciones con `Floo`, arma un `Gringotts<Alumno>`, recorre sus campos con `Wingardium`, decide aprobado o reprobado con `Protego`/`Finite` llamando a una función directo en la condición, y calcula un factorial por recursión. Un programa corto, pero que toca casi todas las piezas del lenguaje a la vez.

---

## Arquitectura

Dobby sigue una arquitectura MVC ampliada con un `FlowController` (el estado de la aplicación) y un motor de lenguaje completamente separado de la interfaz:

```
Vista (Swing)  <-->  Controlador  <-->  FlowController (estado de la app)
                           |
                           v
                Motor (Lexer -> Parser -> Enlazador -> Intérprete)
```

La Vista solo presenta información y captura eventos, sin lógica propia. El Controlador escucha esos eventos y coordina la Vista, el `FlowController` y el Motor, sin conocer los detalles internos del lenguaje. El Motor convierte texto fuente en ejecución real, en fases independientes, igual que un compilador de verdad: el *Lexer* tokeniza, el *Parser* construye el árbol de sintaxis por descenso recursivo con precedencia de operadores, el *Enlazador* resuelve las importaciones entre archivos y corre la validación semántica, y el *Intérprete* recorre el árbol para ejecutarlo.

```
Dobby/
├── pom.xml
├── README.md
├── ejemplos/
│   └── calificaciones/              # Proyecto de ejemplo multiarchivo
├── src/main/resources/assets/
│   ├── imagenes/                    # Mascota, casas, cursor de varita
│   └── fuentes/HARRYP__.TTF         # Tipografía de títulos
└── src/main/java/dobby/
    ├── Main.java
    ├── vista/                       # MainView, editor, árbol, diálogos, ayuda
    ├── controlador/                 # Controlador.java
    ├── flow/                        # FlowController.java
    ├── modelo/                      # Proyecto.java, ArchivoDobby.java
    ├── util/                        # Errores, voz, traductor de símbolos, temas
    └── motor/
        ├── lexer/                   # Token, Lexer
        ├── parser/                  # Nodo* (AST), Parser
        ├── enlazador/                # Enlazador, ValidadorSemantico
        └── interprete/              # Interprete, ResultadoEjecucion
```

---

## Robustez

El compilador distingue errores léxicos, de sintaxis, semánticos y de importación; el intérprete, errores de ejecución. Un error detiene esa compilación o ejecución, conserva lo que ya se había mostrado, y permite corregir el programa y volver a intentarlo. Algunos detalles de robustez que vale la pena conocer:

- Los símbolos desconocidos y los operadores incompletos (`&` o `|` sueltos) ya no se ignoran en silencio: se reportan como error, con una sugerencia (`usa &&`).
- Se admiten comentarios `// de una línea` y `/* de varias */` (sin anidar), y escapes dentro de texto (`\"`, `\\`, `\n`, `\r`, `\t`).
- `Entero` admite el rango de `-2147483648` a `2147483647`; salirse de ahí es un error, no un desbordamiento silencioso. `Decimal` no admite `NaN` ni infinito.
- Los números se comparan por su valor (`1 == 1.0` es verdadero); los arreglos y estructuras se comparan por referencia.
- La ejecución tiene límites de protección fijos (cantidad de pasos, llamadas simultáneas, profundidad de expresiones, tamaño de texto y de salida), pensados para que un programa con un error de lógica, como un ciclo infinito, no deje la aplicación sin responder.

---

## Pruebas

El proyecto tiene una suite de pruebas automatizadas con JUnit que cubre el Lexer, el Parser, el Enlazador (importaciones, ciclos circulares, advertencias), `Gringotts`, `Varita`, los límites de ejecución y la integración completa del controlador y los proyectos. Se ejecuta con:

```
mvn test
```

---

## Cómo correrlo

**Requisitos:** JDK 21 o superior, Apache Maven 3.9 o superior.

```bash
# Compilar
mvn compile

# Correr directo (abre la ventana principal)
mvn compile exec:java

# Generar el .jar ejecutable
mvn package
java -jar target/dobby.jar
```

No hay ninguna dependencia fuera del JDK estándar (JUnit se usa solo para las pruebas), así que el `.jar` generado corre en cualquier máquina con Java 21, sin instalación adicional.

---

## Integrantes

- José Andrés Mora Mora
- Darien Arroyo Castro
