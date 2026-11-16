# EST-07 — Diagramas UML definitivos de Strategy y Observer

**Responsable:** Leonardo Figueroa (`@30621-lmfigueroa1`).  
**Rol:** Teoría y análisis de arquitectura; no se modifica código Java.

## Entregables

| Patrón | Imagen para README/presentación | Fuente editable |
| --- | --- | --- |
| Strategy | [strategy.svg](strategy.svg) | [strategy.puml](strategy.puml) |
| Observer | [observer.svg](observer.svg) | [observer.puml](observer.puml) |

### Strategy

![Diagrama UML de Strategy en el RPG](strategy.svg)

- `Personaje` es el contexto y guarda `arma: IEstrategiaAtaque`.
- `IEstrategiaAtaque` define `atacar(String nombreAtacante)`.
- `AtaqueEspada`, `AtaqueFuego` y `AtaqueHielo` realizan la interfaz.
- `setArma()` sustituye la estrategia activa sin modificar la implementación de `Personaje.atacar()`.

### Observer

![Diagrama UML de Observer en el RPG](observer.svg)

- `TorreDeVigilancia` realiza `ISujeto`, administra `List<IObservador>` y notifica a suscriptores.
- `Jugador` realiza `IObservador`: `actualizar()` invoca `activarEscudo()`.
- La agregación con multiplicidad `0..*` muestra que la torre conserva referencias a observadores, sin controlar su ciclo de vida.
- Cadena de ejecución: `detectarEnemigo()` → `notificar()` → `actualizar()` → `activarEscudo()`.

## Trazabilidad y coordinación

Los nombres de clases, campos y firmas se contrastaron con los archivos Java presentes en las ramas de desarrollo:

- Strategy: [`feature/strategy-ataque`](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/tree/b3ab472c2fefe40179c546db3d127d46af649c75/src/strategy), revisión `b3ab472`.
- Observer: [`feature/observer-alerta`](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/tree/1d5631204732fba1354d06c834f53717a2bc4dc6/src/observer), revisión `1d56312`.

