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
lenguaje + estructura del proyecto de software + diseño visual/UX); no
sustituye la documentación formal en PDF que exige el curso.

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
| `Floo` | Importar función o estructura de otro archivo | `Floo nombre desde "archivo.dobby";` | `Floo sumar desde "operaciones.dobby";` |
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
- Toda variable debe declararse antes de usarse, con `Alohomora` o con
  la forma abreviada `Gringotts<Tipo> nombre;` para arreglos.
- Toda función `Expecto` que no sea de tipo `Obliviate` (vacío) debe incluir
  un `Patronum` antes de cerrar su cuerpo.
- Los bloques de código se delimitan con llaves `{ }`; la indentación no es
  obligatoria (solo estética).
- No se puede llamar (`Accio`) una función que no haya sido declarada
  previamente con `Expecto`, ni usar una variable no declarada con
  `Alohomora`.
- Las importaciones (`Floo`) se resuelven en tiempo de compilación; el
  compilador debe validar: existencia del archivo, existencia de la
  función o estructura, cantidad de argumentos, compatibilidad de tipos, declaraciones
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
| Gringotts | arreglo de tamaño fijo |
| Varita | struct |

### Arreglos Gringotts

`Gringotts` ya permite crear, consultar, modificar y recorrer arreglos,
además de recibirlos o retornarlos en funciones e importaciones.

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

- También se acepta `Gringotts<Entero> notas = [80, 90, 100];`.
- Los índices son enteros desde cero hasta `longitud - 1`.
  `longitud` es de solo lectura. Un índice fuera de rango se reporta al ejecutar.
- `[]` crea un arreglo vacío. Si se declara sin inicializar, consultar
  sus elementos o su longitud produce un error de ejecución.
- Todos los elementos respetan el tipo declarado; al crear un literal,
  `Decimal` admite enteros y `Texto` admite caracteres.
- Los arreglos ya creados requieren tipos idénticos para asignaciones y
  parámetros. Un `Gringotts<Entero>` no se convierte a `Gringotts<Decimal>`.
- Las asignaciones y los parámetros comparten referencias: cambiar una
  posición desde una función también cambia el arreglo del llamador.
  Asignar otro literal reemplaza la referencia, sin modificar el arreglo anterior.
- El tamaño de cada arreglo es fijo; no se agregan ni eliminan posiciones.
  `==` y `!=` comparan referencias. No se admite aritmética entre arreglos.
- Se admiten arreglos anidados: `Gringotts<Gringotts<Entero>> tabla = [[1, 2], [3, 4]];`,
  con acceso como `tabla[0][1]`, y arreglos de estructuras como `Gringotts<Punto>`.

Los ejemplos del editor incluyen arreglos, recorridos y una función que suma
sus elementos. Las pruebas están en `GringottsTest` y se ejecutan con `mvn test`.

### Estructuras Varita

`Varita` define un tipo con campos. Se declara fuera de las funciones;
las variables usan su nombre como tipo, no la palabra `Varita`.

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

Salida: `Harry: 85.0` y `Hermione: 95.0`. Este programa completo aparece
en **Ejemplos > Estructuras (Varita)** para insertarlo en un archivo vacío.

- La creación usa `Nombre {campo: valor, otro: valor}`. Todos los campos
  son obligatorios; se rechazan faltantes, duplicados y nombres desconocidos.
- Se valida el tipo de cada campo. `Decimal` admite valores enteros;
  los arreglos literales de un campo reciben también su tipo declarado.
- Los valores se evalúan en el orden escrito, aunque los campos pueden
  proporcionarse en un orden diferente al de la declaración.
- Se admiten campos simples, arreglos y otras estructuras, incluidos tipos
  declarados después. Se pueden encadenar accesos: `grupo.alumnos[0].nota`.
- Los tipos son nominales: dos estructuras con nombres diferentes no son
  intercambiables aunque tengan los mismos campos.
- Las asignaciones, parámetros y retornos comparten referencias.
  Modificar un campo afecta a todas las referencias; asignar otra instancia
  reemplaza solo esa referencia. `==` y `!=` comparan identidad.
- Una variable declarada sin valor, como `Alohomora a: Alumno;`, debe recibir
  una instancia completa antes de consultar o modificar sus campos.
- `Revelio` muestra el nombre y los campos en orden de declaración.
  Las referencias circulares se muestran como `<ciclo>`, sin recursión infinita.
- Se permiten estructuras vacías (`Varita Vacia {}` y `Vacia {}`) y campos
  llamados `longitud`. La longitud de los arreglos sigue siendo de solo lectura.

Para compartir tipos entre archivos, se puede escribir
`Floo Alumno desde "tipos.dobby";`. El nombre importado debe declararse
directamente en ese archivo. Importar una función o estructura incorpora
las estructuras visibles del módulo y de sus dependencias; las funciones
siguen requiriendo su propio `Floo`. Una misma definición puede llegar por
varias importaciones, pero dos definiciones distintas con el mismo nombre
se rechazan para evitar mezclar tipos incompatibles.

Pruebas: `VaritaTest`, los ejemplos del editor y la integración de proyectos,
ejecutables con `mvn test`.

## Robustez y errores

El compilador distingue errores léxicos, de sintaxis, semánticos y de
importación. La salida indica archivo, línea, descripción y posible causa.
Un error detiene esa ejecución, conserva lo impreso antes del fallo y permite
corregir el programa y volver a ejecutarlo.

- Los símbolos desconocidos y operadores incompletos, como `&` o `|`, ya
  no se ignoran. Los operadores lógicos completos son `&&` y `||`.
- Se admiten comentarios `// hasta fin de línea` y `/* de varias líneas */`.
  Los comentarios de bloque no se anidan. Los textos y comentarios sin cerrar
  reportan la línea donde comenzaron.
- Dentro de textos se admiten `\"`, `\\`, `\n`, `\r` y `\t`.
  Para rutas de `Floo`, se recomienda `"lib/tipos.dobby"`; una barra
  invertida literal debe escribirse como `\\`.
- `Entero` admite desde `-2147483648` hasta `2147483647`.
  Los literales y operaciones fuera de rango producen un error, sin dar la
  vuelta al rango numérico. `Decimal` no admite `NaN` ni infinito.
- Los números se comparan por su valor: `1 == 1.0` produce `Lumos`.
  Los arreglos y estructuras siguen comparándose por referencia.
- Leer una variable sin inicializar produce un error. `Obliviate` mantiene
  su valor nulo. Una declaración dentro de un ciclo se reinicia en cada vuelta;
  no conserva el valor de la vuelta anterior.
- Cancelar `Legilimens` detiene la ejecución. Una entrada inválida para el
  tipo esperado se reporta como error, sin mostrar una excepción de Java.

### Límites de protección

Estos límites son fijos y están pensados para programas educativos:

| Recurso | Límite |
|---|---|
| Fuente por archivo | 1 000 000 caracteres y 100 000 tokens |
| Anidamiento del parser | 64 niveles, contando bloques y expresiones |
| Profundidad de expresiones en validación | 64 niveles |
| Cadena de importaciones | 64 archivos con ruta en la cadena activa |
| Ejecución | 100 000 pasos, contando sentencias, expresiones y recorrido de salida |
| Llamadas simultáneas | 64, incluyendo `Hogwarts` |
| Expresiones activas en ejecución | 128 niveles |
| Recorrido de arreglos y estructuras al imprimir | 64 niveles |
| Texto individual | 65 536 caracteres |
| Salida acumulada | 20 000 caracteres, incluidos saltos de línea |

Superar un límite produce un error controlado. El límite de pasos puede
detener también un programa finito muy grande; no es una detección matemática
de ciclos infinitos. Los contadores se reinician al ejecutar de nuevo.
La ejecución sigue siendo síncrona; estos límites no sustituyen un entorno
aislado ni una ejecución en segundo plano.

Las salidas de más de 200 caracteres se muestran completas, sin esperar la
animación carácter por carácter.

Prueba manual, en un archivo independiente o como único principal del proyecto:

```text
Hogwarts() {
    Revelio "Antes del ciclo";
    Imperio (Lumos) {}
}
```

Se conserva `Antes del ciclo` y aparece el error del límite de pasos en la
línea del `Imperio`. Reemplaza el ciclo por `Revelio "Terminado";` y vuelve a
ejecutar para comprobar la recuperación.

Pruebas automatizadas: `RobustezTest` y pruebas de integración del controlador,
proyectos, formato de errores y panel de salida. Ejecutar con `mvn clean test`.

## Proyectos y archivo principal

- **Archivo > Nuevo proyecto...** crea una carpeta nueva con un
  `principal.dobby` ejecutable. No sobrescribe carpetas existentes.
- **Archivo > Abrir carpeta...** abre un proyecto existente.
- El punto de entrada es el archivo que declara `Hogwarts()`, sin depender
  de su nombre. El árbol lo identifica con `(principal)`.
- Debe existir un único bloque `Hogwarts()` entre los archivos `.dobby`
  del proyecto, incluidas las subcarpetas visibles. Las carpetas cuyo nombre
  comienza con punto se excluyen del árbol y de esta búsqueda.
- **Compilar** y **Ejecutar** parten de ese archivo aunque esté activa una
  pestaña auxiliar. Se revisa la sintaxis de los archivos del proyecto y
  se enlaza el principal con sus dependencias; las importaciones deben
  permanecer dentro de la carpeta del proyecto.
- Los archivos abiertos aportan su contenido actual, incluso sin guardar.
  Una pestaña sin ruta todavía no pertenece al proyecto.
- Los archivos nuevos dentro del proyecto empiezan vacíos para no agregar
  otro punto de entrada por accidente.
- **Proyecto > Actualizar archivos** actualiza el árbol después de cambios
  externos. La ejecución también vuelve a buscar el principal.
- **Proyecto > Cerrar proyecto** conserva las pestañas y sus cambios;
  sin proyecto abierto se compila o ejecuta el archivo activo.

Prueba manual: crear un proyecto, agregar `operaciones.dobby` con una
función `Expecto`, importarla desde el principal y ejecutar mientras
`operaciones.dobby` está activo. Luego agregar un segundo `Hogwarts()`
y comprobar que la ejecución se detiene con un error de proyecto.

Pruebas automatizadas: `mvn test`.

## Menú de opciones (requisito del entorno gráfico)

El entorno debe permitir consultar, sin salir de la aplicación: palabras
reservadas, sintaxis, estructuras de control, funciones, operaciones,
semántica, tipos de datos, importaciones y ejemplos — todo el contenido de
este README debe quedar accesible también desde ahí.

## Diseño gráfico (entorno / mini-IDE)

Concepto general: transmitir la magia de Harry Potter usando directamente
la estética del universo — la mascota es el propio Dobby (el elfo
doméstico), con una varita como accesorio y tipografía inspirada en el
logo oficial de los libros, para que la identidad visual se sienta lo más
fiel posible al material original.

**Assets usados** (en `src/main/resources/assets/`):
- `imagenes/Dobby.png` — mascota (elfo con laptop, audífonos y taza de
  café con código, estilo "programador").
- `imagenes/Varita.png` — ícono de varita (para el cursor del editor y el
  botón "Alohomora — Entrar").
- `fuentes/HARRYP__.TTF` — tipografía inspirada en el logo de los libros,
  para el tagline y títulos. Se carga con `Font.createFont(...)` desde el
  classpath (no depende de que esté instalada en el sistema).

**Pantalla de bienvenida:**
- Fondo: cielo nocturno con degradado oscuro, estrellas parpadeando, luna
  sutil, silueta genérica de castillo abajo (formas geométricas simples,
  sin escudos). Chispas doradas decorativas flotando alrededor.
- Mascota elfo centrada y grande, con sombrero puntiagudo y varita.
- Snitch dorada volando de forma aleatoria por toda la pantalla, cambiando
  de posición cada par de segundos con transición suave.
- Tagline en cursiva ("un lenguaje con un poco de magia") arriba, nombre
  "dobby" pequeño y discreto debajo.
- Botón principal: "Alohomora — Entrar", con ícono de varita.
- Esquina superior derecha: 4 botones solo-ícono — Ajustes ⚙, Créditos,
  Ayuda, Acerca de.

**Pantalla de proyecto vacío** (justo después de la bienvenida si no hay
proyecto abierto): árbol de archivos vacío con "Sin proyecto abierto", un
botón para crear uno nuevo, e ícono/mensaje central invitando a escribir
(ej. "Crea o abre un proyecto para empezar a escribir hechizos").

**Pantalla principal (editor en uso):** árbol de archivos a la izquierda
con el archivo activo resaltado, editor con resaltado de sintaxis
(palabras reservadas en el color de acento del tema activo), botones
Compilar/Ejecutar arriba, panel de salida abajo.

**Estado de error de compilación:** el archivo con problemas se marca en
rojo en el árbol, la línea del error se resalta en el editor, el mensaje
aparece en el panel de salida con tipo/archivo/línea/causa (conecta
directo con la sección "Segmento de salida" del enunciado), y "Ejecutar"
se ve atenuado/deshabilitado mientras el error no se resuelva.

**Detalles interactivos:**
- **Cursor de varita**: al pasar sobre el editor, el cursor cambia a un
  ícono de varita en vez de la flecha/manita normal.
- **Selector de casa**: en Ajustes, 4 colores de acento (roja, verde, azul,
  amarilla — nombres neutros, sin nombres/escudos oficiales) que cambian
  el color de acento de toda la interfaz (bordes de botones, archivo
  activo en el árbol, palabras reservadas en el editor).
- **Sello de cera para guardado**: ícono junto al archivo activo, "sellado"
  (guardado) o "abierto/roto" (cambios sin guardar).
- **Mascota que se teletransporta**: al compilar o ejecutar, la mascota
  aparece con efecto "poof" (chispas que se expanden, crece de tamaño 0 a
  1 con rebote), entrega el resultado (el sello se "rompe" y el mensaje
  aparece), y tras un par de segundos se desvanece con el efecto inverso.
  Se eligió sobre una lechuza mensajera porque: los elfos domésticos
  canónicamente se teletransportan, reutiliza la misma mascota (menos
  trabajo de arte), y es más simple de animar en Swing que una
  trayectoria de vuelo.
- **A futuro (fuera de alcance de esta entrega)**: "compañero mágico" — la
  mascota crece/evoluciona según logros del usuario (pruebas superadas,
  funcionalidades completadas).

**Implementación técnica** (Swing puro, sin librerías externas de UI):
- `javax.swing.Timer` para toda animación — nunca `Thread.sleep()` (bloquea
  el hilo de eventos/EDT).
- `Graphics2D` con antialiasing para formas propias (castillo, chispas,
  sello de cera).
- `ImageIcon` / `Toolkit.createCustomCursor()` para cargar la mascota y el
  cursor de varita como PNG con transparencia.
- Una clase `TemaManager` que centraliza los colores de casa, para que
  cambiar el tema actualice toda la interfaz desde un solo lugar.

**Pendiente**: guardar en el repo (por ejemplo en `docs/mockups/`) las
capturas/mockups de referencia de las pantallas.

## Estructura del proyecto

```
Dobby/
├── pom.xml
├── README.md
├── src/main/resources/assets/
│   ├── imagenes/
│   │   ├── Dobby.png        # Mascota
│   │   └── Varita.png       # Cursor / ícono de varita
│   └── fuentes/
│       └── HARRYP__.TTF     # Tipografía de titulos/tagline
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

El proyecto incluye lexer, parser, intérprete, validación semántica,
importaciones y gestión de proyectos con detección del archivo principal.
`Gringotts` está implementado con acceso por índice, modificación,
longitud, funciones y pruebas automatizadas.

`Varita` está implementado con creación de valores, lectura y escritura de
campos, tipos anidados, arreglos, funciones, importaciones y pruebas.

Siguen pendientes la lectura de voz y los demás ajustes de la entrega final.

El compilador y el intérprete incluyen validación léxica, comentarios y escapes,
controles numéricos y de inicialización, límites de ejecución y pruebas de
recuperación tras errores.
