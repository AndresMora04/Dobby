# Manual de usuario de Dobby

Esta guía está pensada para cualquier persona, incluso si nunca has escrito una línea de código en tu vida. Vas a aprender a abrir Dobby, crear tu primer programa, y entender lo que la aplicación te muestra en pantalla.

## 1. ¿Qué es Dobby?

Dobby es un programa de computadora en el que vos escribís instrucciones (a eso se le llama "código" o "programa"), y la aplicación las lee y las ejecuta de verdad, como si fuera una calculadora que sigue tus órdenes paso a paso.

Lo particular de Dobby es que, en vez de usar palabras en inglés como `for` o `while` (como casi todos los demás lenguajes de programación), usa palabras del universo de Harry Potter. Por ejemplo, para repetir algo varias veces se usa `Wingardium`, y para mostrar un mensaje en pantalla se usa `Revelio`.

## 2. Cómo abrir Dobby

Hay dos formas de abrir la aplicación. Elegí la que te resulte más cómoda.

### Opción A: con doble clic (la más fácil)

Si ya tenés la carpeta `Dobby` (la que contiene `Dobby.exe`), simplemente hacé doble clic en `Dobby.exe`, igual que abrirías cualquier otro programa. Podés copiar esa carpeta a tu Escritorio para tenerla siempre a mano. No necesitás tener Java instalado para esto.

Si todavía no tenés esa carpeta generada, ejecutá `packaging\build-exe.bat` dentro del proyecto (esto sí necesita el JDK 21 instalado) y la va a crear en `packaging/output/Dobby`.

### Opción B: desde la terminal

Necesitás tener instalado Java 21. Abrí una terminal dentro de la carpeta del proyecto y escribí:

```
mvn exec:java
```

Esto compila y abre la aplicación directamente. O, si ya generaste el archivo `.jar`:

```
java -jar target/dobby.jar
```

Cualquiera de las dos opciones abre la misma ventana principal de Dobby.

## 3. La pantalla de bienvenida

Lo primero que vas a ver es una pantalla con el cielo nocturno y la mascota del proyecto (Dobby, el elfo doméstico). Ahí vas a encontrar:

- Un botón grande que dice **"Alohomora"**, en la parte de abajo. Hacé clic ahí para entrar a la aplicación.
- Cuatro íconos pequeños arriba a la derecha:
  - **⚙ (Ajustes):** para cambiar el tema visual y otras preferencias.
  - **✦ (Créditos):** quién hizo el proyecto.
  - **? (Ayuda):** abre la referencia completa del lenguaje (lo mismo que el menú Ayuda que vemos más adelante).
  - **i (Acerca de):** información general sobre Dobby.

## 4. Crear tu primer proyecto

Un "proyecto" en Dobby es simplemente una carpeta en tu computadora donde vas a guardar tus archivos de código.

1. Andá al menú **Archivo** (arriba a la izquierda) y elegí **"Nuevo proyecto..."**.
2. Se va a abrir una ventana para que elijas (o crees) la carpeta donde querés guardar el proyecto.
3. Después te va a pedir un nombre para el proyecto (por defecto sugiere "MiProyecto"). Escribí el que quieras y aceptá.

Con eso ya tenés un proyecto vacío, listo para empezar a escribir código.

## 5. Escribir tu primer programa

1. En el menú **Archivo**, elegí **"Nuevo"** para crear un archivo nuevo dentro del proyecto (o hacé clic derecho sobre la carpeta del proyecto, en el panel de la izquierda, y elegí **"Nuevo archivo aquí"**).
2. En el editor de texto que aparece, escribí esto exactamente:

```
Hogwarts() {
    Revelio "Hola, mundo";
}
```

¿Qué significa esto?
- `Hogwarts()` es obligatorio en todo programa Dobby: es el punto donde el programa empieza a correr. Solo puede haber uno por proyecto.
- `Revelio "Hola, mundo";` le dice a Dobby que muestre ese texto en pantalla.
- Las llaves `{ }` marcan dónde empieza y dónde termina un bloque de instrucciones.
- Cada instrucción termina con punto y coma `;`.

3. Guardá el archivo con **Ctrl+S**, o desde el menú Archivo → **"Guardar"**.

## 6. Compilar: revisar que el código esté bien escrito

"Compilar" quiere decir que la aplicación revisa tu código en busca de errores **antes** de correrlo, igual que alguien que relee un texto antes de publicarlo.

Para compilar: andá al menú **Proyecto → Compilar**, o apretá la tecla **F6**.

Si todo está bien escrito, en el panel de salida (la zona de abajo) vas a ver un resumen diciendo que la compilación fue exitosa. Si hay algún error, te va a decir exactamente qué está mal y en qué línea (ver la sección 8).

## 7. Ejecutar: correr tu programa de verdad

Para que el programa realmente se ejecute y veas el resultado: andá al menú **Proyecto → Ejecutar**, o apretá **F5**.

Con el ejemplo de arriba, en el panel de salida vas a ver:

```
Hola, mundo
```

Si tu código tiene errores, Dobby no te va a dejar ejecutarlo hasta que los arregles — así evitás confusiones entre "esto no corrió porque está mal escrito" y "esto no corrió por otra razón".

## 8. Entender un mensaje de error

Si algo está mal, Dobby no solo te dice "hay un error": te dice exactamente dónde y por qué. Por ejemplo:

```
Error de importacion
Archivo: principal.dobby
Linea: 1
Descripcion: El archivo 'falta.dobby' no existe
Posible causa: Revisa la ruta y el nombre del archivo importado.
```

Siempre vas a encontrar estas partes:
- **Archivo:** en qué archivo pasó el problema (importante si tu proyecto tiene varios).
- **Linea:** en qué línea exacta.
- **Descripcion:** qué salió mal, en palabras simples.
- **Posible causa:** una pista de cómo arreglarlo.

Además, la línea donde ocurrió el error se marca en **rojo** directamente en el editor, para que la encuentres sin tener que buscarla.

También podés ver mensajes que empiezan con `[ADVERTENCIA]`: esos no son errores (tu programa sí corre), pero te avisan de cosas como una variable que declaraste y nunca usaste.

## 9. El árbol de archivos (panel izquierdo)

Ahí ves todos los archivos de tu proyecto. Podés:
- **Abrir un archivo:** hacé doble clic, o seleccionalo con las flechas del teclado y apretá **Enter**.
- **Crear un archivo nuevo:** clic derecho sobre la carpeta → "Nuevo archivo aquí".
- **Renombrar o eliminar:** clic derecho sobre el archivo → la opción que necesites. También podés abrir ese mismo menú con el teclado usando **Shift+F10** sobre el archivo seleccionado.
- El archivo que tiene la función `Hogwarts()` (el punto de entrada) se marca de forma distinta en el árbol, para que sepas cuál es el principal.

## 10. Trabajar con varios archivos (pestañas)

Cada archivo que abrís aparece como una pestaña en la parte de arriba del editor. Para cambiar entre pestañas:
- Hacé clic directamente sobre la que querés.
- O, si ya hiciste clic una vez en el área de pestañas, usá las flechas **← →** del teclado.

Un proyecto puede tener más de un archivo, y uno puede usar código de otro con `Floo`, por ejemplo:

```
Floo sumar desde "operaciones.dobby";
```

Esto le dice a Dobby: "la función `sumar` está definida en el archivo `operaciones.dobby`, quiero usarla acá".

## 11. El menú Ayuda: consultar el lenguaje sin salir de la app

Si no te acordás de una palabra o cómo se escribe algo, no hace falta que cierres la aplicación ni busques en internet. Andá al menú **Ayuda → "Referencia de Dobby"** (o apretá **F1**), y vas a encontrar tablas con:

- Todas las palabras reservadas (qué hacen y cómo se usan).
- La sintaxis completa del lenguaje.
- Estructuras de control, funciones, operaciones, tipos de datos, importaciones, y ejemplos listos para copiar.

## 12. Accesibilidad: usar Dobby sin mouse o con lectura de voz

Dobby fue diseñado para que una persona con discapacidad visual también pueda usarlo completo, sin depender de ver la pantalla:

- **Leer resultados en voz alta:** menú Proyecto → "Leer resultados en voz alta", o apretá **Ctrl+M**. Con esto activado, cada vez que compiles o ejecutes, Dobby lee el resultado en voz alta.
- **Repetir la última lectura:** Ctrl+R.
- **Leer la línea actual del código:** Ctrl+L.
- **Leer todo el archivo:** Ctrl+Shift+L.
- **Navegación completa por teclado:** todos los menús se abren con `Alt` + la letra subrayada (por ejemplo, `Alt+A` abre Archivo). Las pestañas se mueven con las flechas, y el árbol de archivos se usa con Enter y Shift+F10, como se explicó arriba.

Si tu computadora no tiene un lector de voz disponible, Dobby lo detecta automáticamente y simplemente no reproduce audio, sin romper nada más.

## 13. Guardar tu trabajo

- **Guardar:** Ctrl+S (guarda el archivo que tenés abierto).
- **Guardar como...:** Ctrl+Shift+S (para guardarlo con otro nombre o en otro lugar).
- **Guardar todo:** desde el menú Archivo, guarda todos los archivos abiertos de una vez.

## 14. Volver al inicio o salir

Desde el menú **Archivo**, tenés dos opciones al final:
- **"Inicio":** te regresa a la pantalla de bienvenida sin cerrar la aplicación.
- **"Salir":** cierra Dobby.

## 15. Resumen rápido de atajos de teclado

| Acción | Atajo |
| :-: | :-: |
| Compilar | F6 |
| Ejecutar | F5 |
| Guardar | Ctrl+S |
| Guardar como | Ctrl+Shift+S |
| Abrir archivo | Ctrl+O |
| Abrir carpeta | Ctrl+Shift+O |
| Nuevo proyecto | Ctrl+Shift+N |
| Nuevo archivo | Ctrl+N |
| Cerrar pestaña | Ctrl+W |
| Ayuda / Referencia de Dobby | F1 |
| Activar/desactivar voz | Ctrl+M |
| Repetir última lectura | Ctrl+R |
| Leer línea actual | Ctrl+L |
| Leer archivo completo | Ctrl+Shift+L |
| Cambiar de pestaña | ← → (con foco en las pestañas) |
| Abrir archivo seleccionado en el árbol | Enter |
| Menú contextual del árbol | Shift+F10 |

Con esto ya tenés todo lo necesario para abrir Dobby, escribir tu primer programa, entender los errores, y moverte por toda la aplicación, con o sin mouse.
