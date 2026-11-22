# Integración de exposición, documentación y ensayo

**Responsable de coordinación:** Leonardo Figueroa (`@30621-lmfigueroa1`)  
**Issue:** [EST-09 (#9)](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/issues/9)  
**Base técnica verificada:** rama `main`, commit `b66dbd3c9659dc4188bf502ef1b2c149dfb15541`.

## Validación de coherencia frente a Java

| Presentación / documento | Elemento del código real | Estado |
|---|---|---|
| Strategy: interfaz de ataque | `IEstrategiaAtaque.atacar(String nombreAtacante)` | Verificado |
| Strategy: contexto | `Personaje.arma`, `Personaje.setArma()`, `Personaje.atacar()` | Verificado |
| Tres implementaciones | `AtaqueEspada`, `AtaqueFuego`, `AtaqueHielo` | Verificado |
| Observer: dos contratos | `ISujeto`, `IObservador.actualizar()` | Verificado |
| Sujeto/observador concreto | `TorreDeVigilancia`, `Jugador` | Verificado |
| Registro / notificación | `suscribir()`, `desuscribir()`, `detectarEnemigo()` | Verificado |
| Integración | `Principal` usa `Map<Integer, IEstrategiaAtaque>`, `Map<Integer, Runnable>` y `List<Jugador>` | Verificado |
| README, imágenes UML | Rutas `docs/uml/strategy.svg` y `docs/uml/observer.svg` | Pendiente de fusionar PR #34 |

**Precisión de arquitectura:** `Personaje` se desacopla de las variantes; `TorreDeVigilancia` se desacopla de las clases concretas de los receptores. `Principal` crea y conoce implementaciones concretas, lo cual es propio de la composición de la aplicación. Los patrones no eliminan los condicionales de validación ni convierten las notificaciones locales en un sistema distribuido.

## Comandos de ejecución acordes al README y Principal

Desde la raíz del repositorio, con JDK 21:

```bash
javac -encoding UTF-8 -d out -sourcepath src src/Principal.java
java -cp out Principal
```

`src/Principal.java` no declara paquete; por eso la clase de arranque se invoca como `Principal`. `-sourcepath src` permite que `javac` encuentre los paquetes `strategy` y `observer`. `out/` está excluido por `.gitignore`.

**Secuencia única de la demostración:** `1 → 2 (fuego) → 1 → 3 (Jugador A) → 3 (Jugador B) → 5 → 4 (desuscribir uno) → 5 → 0`.

No se modificó código Java como parte de EST-09; la compilación y la ejecución deben probarse además en el equipo desde el que se realizará el Live Coding.

## Plan de ensayo con los seis integrantes

| Participante | Enfoque y diapositivas | Prueba antes del ensayo |
|---|---|---|
| **Steven Villegas** | Concepto, analogía y aplicación Strategy (3–4 y parte de 13) | Explicar la estrategia sin leer; enlazarla con `Personaje`. |
| **Miguel Flores** | Concepto, analogía y secuencia Observer (5–6 y parte de 13) | Diferenciar sujeto y observador; evitar atribuir asincronía automática. |
| **Leonardo Figueroa** | UML y OCP/DIP, diferencia de condicionales (7–9) | Señalar realización, asociación y agregación; firmas reales de clases. |
| **Jorge Yugsi** | Live Coding Strategy (10) | Preparar JDK e IntelliJ/terminal; cambio espada → fuego. |
| **Francisco Jiménez** | Live Coding Observer (11) | Suscribir, notificar, desuscribir, notificar de nuevo. |
| **Wendy Cueva** | Introducción/organización, integración y cierre (1–2, 12 y 14) | Ejecutar desde `Principal`, verificar README e iniciar recuperación del demo si surge un problema. |

