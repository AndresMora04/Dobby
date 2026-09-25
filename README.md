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
  con acceso como `tabla[0][1]`. `Varita` permanece pendiente.

Los ejemplos del editor incluyen arreglos, recorridos y una función que suma
sus elementos. Las pruebas están en `GringottsTest` y se ejecutan con `mvn test`.

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

Siguen pendientes las estructuras `Varita` y la lectura de voz, además
de los demás ajustes de la entrega final.
