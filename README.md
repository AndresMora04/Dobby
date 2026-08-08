# Dobby

Lenguaje de programación educativo con temática de Harry Potter, desarrollado
como proyecto para el curso de Paradigmas de Programación. Incluye un
lenguaje propio, un compilador/intérprete y un entorno gráfico de escritura
de código (mini-IDE) construido en Java con Swing.

## Integrantes

- José Andrés Mora Mora
- Darien Arroyo Castro

## Documentación

La documentación completa del proyecto se lleva en Google Drive:
[Documento de Dobby](https://docs.google.com/document/d/19L3MUmLLpp1Ip35WHBakqJbFW2lt02fOkEJsLRYRW6Y/edit?usp=sharing)

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

## Palabras reservadas de Dobby

| Palabra Dobby   | Equivalente             |
|-----------------|--------------------------|
| Alohomora       | Definición de función    |
| Expecto         | Retorno de función       |
| Revelio         | Imprimir en pantalla     |
| Accio           | Asignación de variable   |
| Wingardium      | Ciclo (while)            |
| Imperio         | Ciclo (for)              |
| Protego         | Condicional (if)         |
| Finite          | Fin de bloque / else     |
| Expelliarmus    | Romper ciclo (break)     |
| Reparo          | Continuar ciclo (continue)|
| Lumos / Nox     | Booleanos verdadero/falso|
| Hogwarts        | Programa principal       |
| Gringotts       | Estructura de datos      |
| Varita          | Declaración de variable  |

## Estructura del proyecto

```
Dobby/
├── pom.xml
├── README.md
└── src/main/java/dobby/
    ├── Main.java                 # Punto de entrada de la aplicación
    ├── vista/                    # Interfaz gráfica (Swing)
    │   ├── MainView.java
    │   ├── FileTreePanel.java
    │   ├── EditorPanel.java
    │   └── OutputPanel.java
    ├── controlador/              # Orquestación entre vista, flow y motor
    │   └── Controlador.java
    ├── flow/                     # Estado global de la aplicación
    │   └── FlowController.java
    ├── util/                     # Utilidades transversales
    │   ├── FileUtil.java
    │   ├── ErrorFormatter.java
    │   └── PalabrasReservadas.java
    ├── modelo/                   # Modelo de datos del proyecto/archivos
    │   ├── Proyecto.java
    │   └── ArchivoDobby.java
    └── motor/                    # Motor del lenguaje Dobby
        ├── lexer/                # Análisis léxico
        │   ├── Token.java
        │   ├── TipoToken.java
        │   └── Lexer.java
        ├── parser/                # Análisis sintáctico (AST)
        │   ├── Nodo.java
        │   ├── NodoPrograma.java
        │   ├── NodoSi.java
        │   ├── NodoMientras.java
        │   ├── NodoFuncion.java
        │   ├── NodoAsignacion.java
        │   ├── NodoLlamada.java
        │   └── Parser.java
        └── interprete/            # Interpretación/ejecución
            ├── Interprete.java
            └── ResultadoEjecucion.java
```

## Estado actual

Esqueleto inicial del proyecto: estructura de paquetes, clases y firmas de
métodos documentadas con Javadoc. La lógica de tokenización, parseo e
interpretación aún no está implementada (marcada con `TODO` en el código).
