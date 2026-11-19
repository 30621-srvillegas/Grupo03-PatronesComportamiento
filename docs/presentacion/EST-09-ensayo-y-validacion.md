# EST-09 — Integración de exposición, documentación y ensayo

**Responsable de coordinación:** Leonardo Figueroa (`@30621-lmfigueroa1`)  
**Issue:** [EST-09 (#9)](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/issues/9)  
**Base técnica verificada:** rama `main`, commit `b66dbd3c9659dc4188bf502ef1b2c149dfb15541`.

## Material integrado

- [Presentación conjunta (fuente editable en Markdown/Marp)](EST-09-presentacion-integrada.md). Incluye concepto, analogías, las dos vistas UML, análisis SOLID, comandos y secuencia de demostración.
- [UML Strategy y Observer (EST-07, PR #34)](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/pull/34): las imágenes definitivas se incorporan en `docs/uml/` tras la fusión.
- [Análisis de arquitectura y guion (EST-08, PR #35)](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/pull/35): explicación verificada con `Principal` y los patrones.
- [README principal](../../README.md): contiene participantes, funciones, escenario RPG, UML, compilación, ejecución y opciones del menú.
- [Investigación de Observer, PR #38](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/pull/38): fuente de teoría de Miguel; aún no está fusionada en `main`.
- [Fundamentos Strategy en main](../strategy.md): material de Steven.

El documento de diapositivas usa enlaces relativos `../uml/*.svg`. **Hasta fusionar PR #34, esos vínculos no se resolverán desde `main`**, aunque el PPTX de apoyo, entregado aparte, representa ambas estructuras.

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

### Guion del ensayo (propuesta, no cita confirmada)

1. **Revisión previa conjunta:** confirmar que las diapositivas 7–8 usen los UML actualizados y que los PR #34 y #35 no tengan cambios sin revisar.
2. **Primera pasada completa:** participación sucesiva de los seis; cronometrar cada intervención, transiciones y Live Coding.
3. **Preguntas cruzadas:** cada integrante explica una clase y una decisión SOLID que no haya presentado originalmente.
4. **Segunda pasada:** repetir la transición exposición → IntelliJ/terminal y los pasos de ejecución sin improvisación.
5. **Evidencias finales:** anotar participantes presentes, duración real, fallos, correcciones y acuerdo de versión final.

**Pendiente de coordinación real:** fecha, hora, disponibilidad de los seis y confirmación de que se celebró el ensayo. No se declara ejecutado ni programado hasta recibir esa confirmación.

## Lista de control de EST-09

- [x] Unificar el contenido de diapositivas con la teoría y los UML de los PR anteriores.
- [x] Aportar al README sin alterar la autoría ni reescribir innecesariamente la documentación de Wendy.
- [x] Contrastar las instrucciones del README con `Principal.java` y su paquete.
- [x] Preparar reparto de intervenciones, secuencia de demo, agenda y validaciones del ensayo.
- [ ] Fusionar PR #34 y #35 tras aprobación; comprobar que los enlaces de UML se muestran en `main`.
- [ ] Coordinar fecha/hora con todos, realizar ensayo y registrar el resultado.
- [ ] Aprobar e integrar este PR cuando el equipo valide la presentación.
