# EST-08 — Análisis de arquitectura, condicionales y principios SOLID

**Proyecto:** Grupo 3 — Patrones de comportamiento en un videojuego RPG  
**Responsable:** Leonardo Figueroa (`@30621-lmfigueroa1`)  
**Rol:** teoría y análisis de arquitectura; no implica implementación en Java  
**Tarea GitHub:** [EST-08, issue #8](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/issues/8)

> **Alcance de la verificación:** este documento analiza el código existente en las ramas `feature/strategy-ataque` (revisión `b3ab472`) y `feature/observer-alerta` (revisión `1d56312`).

## 1. Problema arquitectónico y decisión de diseño

Los patrones de comportamiento distribuyen responsabilidades y controlan cómo colaboran los objetos. En este RPG se resuelven **dos problemas diferentes**:

- **Strategy (variación de algoritmos):** el personaje debe cambiar su modo de atacar sin contener una cadena creciente de decisiones sobre espada, fuego o hielo. Cada tipo de ataque se encapsula tras `IEstrategiaAtaque`.
- **Observer (notificación a varios receptores):** cuando una torre detecta un enemigo, debe avisar a los jugadores suscritos sin conocer sus clases concretas. La torre trabaja con `IObservador`, no directamente con `Jugador`.

No se debe afirmar que Observer elimina por sí mismo todo `if-else`: su principal aporte es el **desacoplamiento entre emisor y receptores**. Strategy sí evita que el contexto contenga una selección condicional de la implementación de cada algoritmo. En ambos casos pueden existir condicionales legítimos en otros componentes de la aplicación.

## 2. Strategy: de un personaje rígido a estrategias intercambiables

### 2.1 Alternativa rígida, solo para comparación (no implementada)

Supongamos que `Personaje` guardara un texto `tipoArma` y que su método `atacar()` tuviera esta lógica:

```text
Personaje.atacar():
  si tipoArma = "espada": ejecutar lógica de espada
  si tipoArma = "fuego":  ejecutar lógica de fuego
  si tipoArma = "hielo":  ejecutar lógica de hielo
```

**Consecuencias:** cada nuevo ataque obliga a editar el método del personaje; una clase concentra la identidad del personaje y todas las variantes de combate; probar o modificar un tipo de ataque exige intervenir sobre una unidad más grande. El problema no es la sintaxis de `if` o `switch`, sino el **acoplamiento del contexto a cada variante**.

### 2.2 Diseño implementado en el RPG

| Componente existente | Tipo / rol | Responsabilidad comprobada |
| --- | --- | --- |
| `IEstrategiaAtaque` | Interfaz de estrategia | Declara `atacar(String nombreAtacante)`. |
| `AtaqueEspada`, `AtaqueFuego`, `AtaqueHielo` | Estrategias concretas | Implementan la interfaz y muestran comportamientos distintos de ataque en consola. |
| `Personaje` | Contexto | Guarda `arma: IEstrategiaAtaque`, recibe una estrategia inicial por constructor, permite sustituirla con `setArma(IEstrategiaAtaque)` y delega `atacar()`. |

La interacción efectiva es:

```text
Cliente elige y entrega un objeto de estrategia
  → Personaje.setArma(nuevaArma)
  → Personaje.atacar()
  → arma.atacar(nombre)
  → se ejecuta la implementación concreta activa
```

En `Personaje.atacar()`, el código real es `arma.atacar(nombre);`. La decisión sobre **qué objeto inyectar** está fuera de este método. Por ejemplo, un cliente puede utilizar `new AtaqueFuego()` y luego `new AtaqueHielo()` con el mismo personaje, sin reescribir `Personaje.atacar()`.

### 2.3 Comparación arquitectónica

| Aspecto | Diseño rígido hipotético | Strategy implementado |
| --- | --- | --- |
| Selección de ataques | Rama condicional dentro de `Personaje` | Sustitución de un objeto de tipo `IEstrategiaAtaque` |
| Dependencia del contexto | Conoce todos los tipos concretos | Conoce el contrato de la interfaz |
| Agregar un ataque | Editar `atacar()` y volver a revisar sus casos | Crear otra implementación de la interfaz; conectar su selección desde el cliente |
| Pruebas | Variantes reunidas en un mismo método | Cada estrategia puede probarse aisladamente; el contexto admite una estrategia de prueba |
| Cambio durante la ejecución | Requiere reinterpretar `tipoArma` y sus ramas | `setArma()` sustituye la referencia activa |

### 2.4 Justificación SOLID

**OCP — Abierto/Cerrado.** `Personaje` está abierto a **extensión del comportamiento** mediante nuevas implementaciones de `IEstrategiaAtaque` y puede permanecer sin modificaciones al incorporarlas. No significa que toda la aplicación sea inmutable: el menú, la fábrica o la composición que decide qué estrategia crear podría requerir un cambio.

**DIP — Inversión de Dependencias.** La política de delegación del personaje no depende de `AtaqueFuego`, `AtaqueHielo` ni `AtaqueEspada`, sino de `IEstrategiaAtaque`. Las clases concretas también dependen del contrato al implementarlo. La inyección de estrategia por constructor y por `setArma()` facilita sustituir implementaciones; **la inyección es un mecanismo, mientras DIP es un principio de diseño**.

**Mantenimiento:** un cambio en el texto o efecto de `AtaqueFuego` debe localizarse en esa estrategia. La incorporación de otra variante reduce la necesidad de tocar el contexto estable. Cada estrategia nueva, sin embargo, incorpora una clase que debe probarse y mantenerse.

### 2.5 Ejemplo empresarial realista: motor de tarifas de envíos

Una plataforma de comercio electrónico puede calcular un coste de envío con distintas estrategias (entrega estándar, urgente o retiro en tienda). En lugar de que `Pedido` implemente todas las reglas en un `switch`, puede delegar el cálculo a una interfaz `IEstrategiaEnvio` y recibir una implementación seleccionada. **Equivalencia con el RPG:** `Pedido` es el contexto como `Personaje`; la modalidad de envío representa la estrategia concreta como `AtaqueFuego`; cambiar la modalidad equivale a `setArma()`. Es un **escenario de aplicación**, no una funcionalidad ya implementada en el repositorio.

## 3. Observer: de alertas directas a suscriptores desacoplados

### 3.1 Alternativa rígida, solo para comparación (no implementada)

Si la torre almacenara referencias específicas a `Jugador` y al detectar un enemigo ejecutara `jugador1.activarEscudo()`, `jugador2.activarEscudo()`, etc., el emisor quedaría ligado al tipo de receptor y a la cantidad de jugadores. Agregar una clase de receptor diferente (p. ej., una alarma o un registro de eventos) requeriría introducir nuevas llamadas y posiblemente condicionales en la torre.

Este enfoque puede funcionar en un caso mínimo, pero se vuelve difícil de extender cuando los receptores se suscriben o se retiran durante la ejecución.

### 3.2 Diseño implementado en el RPG

| Componente existente | Tipo / rol | Responsabilidad comprobada |
| --- | --- | --- |
| `ISujeto` | Interfaz del sujeto | Declara `suscribir(IObservador)`, `desuscribir(IObservador)` y `notificar()`. |
| `IObservador` | Interfaz del observador | Declara `actualizar()`. |
| `TorreDeVigilancia` | Sujeto concreto | Implementa `ISujeto`, mantiene `List<IObservador>`, añade y retira observadores, ejecuta `notificar()`. |
| `Jugador` | Observador concreto | Implementa `IObservador`; `actualizar()` llama a `activarEscudo()`. |

La secuencia del código existente es:

```text
Se registran jugadores mediante torre.suscribir(jugador)
  → torre.detectarEnemigo()
  → torre.notificar()
  → por cada IObservador: observador.actualizar()
  → en Jugador: activarEscudo()
```

**Relación estructural:** una `TorreDeVigilancia` mantiene referencias a **cero o más** instancias de `IObservador` (colección de suscriptores). `Jugador` realiza la interfaz; la torre no necesita conocer su clase. El registro puede cambiar durante la ejecución. La implementación evita duplicados en `suscribir()` y itera sobre una copia de la lista al notificar.

### 3.3 Comparación arquitectónica

| Aspecto | Diseño rígido hipotético | Observer implementado |
| --- | --- | --- |
| Destinatarios | La torre conoce cada `Jugador` concreto | La torre depende de `IObservador` |
| Incorporar un receptor nuevo | Modificar a la torre para llamarlo | Implementar `IObservador` y suscribirlo |
| Cancelar alertas | Requiere lógica específica | `desuscribir(observador)` |
| Número de receptores | Puede estar fijado por referencias explícitas | Lista dinámica `List<IObservador>` |
| Propagación | Invocaciones directas a métodos concretos | `notificar()` llama `actualizar()` a los inscritos |
| Acoplamiento | Emisor conoce acciones de cada receptor | Cada observador decide qué hacer en `actualizar()` |

### 3.4 Justificación SOLID

**OCP — Abierto/Cerrado.** Para registrar un nuevo tipo de destinatario que implemente `IObservador`, la lógica de `TorreDeVigilancia.notificar()` no necesita cambiar. El nuevo observador define su propia reacción. El evento que actualmente se notifica es una alerta genérica sin datos adicionales; **si se necesita información nueva sobre el enemigo**, puede ser necesario evolucionar el contrato y sus consumidores.

**DIP — Inversión de Dependencias.** `TorreDeVigilancia` gestiona una lista de la abstracción `IObservador`, no una lista de `Jugador`. `ISujeto` utiliza también el contrato `IObservador` para administrar suscripciones. Así el emisor no depende de las implementaciones de respuesta.

**Mantenimiento:** las reacciones de los observadores quedan localizadas en sus clases; se pueden ensayar la suscripción, desuscripción y fan-out sin mezclar las responsabilidades de la torre con la activación concreta del escudo.

### 3.5 Ejemplo empresarial realista: seguimiento de pedidos

En un sistema comercial, un módulo de pedidos puede publicar cambios de estado a componentes suscritos (correo, panel de seguimiento o auditoría). Cada receptor decide su respuesta y el emisor no tiene que incorporar la lógica específica de todos ellos. **Equivalencia con el RPG:** el módulo que detecta el cambio equivale a `TorreDeVigilancia`; los servicios interesados equivalen a los observadores; el cambio de estado equivale a `detectarEnemigo()`.

Se trata de una **analogía arquitectónica**. El proyecto Java del grupo implementa notificaciones **en memoria, síncronas y locales al mismo proceso**; no demuestra mensajería distribuida, entrega remota, persistencia, colas ni tolerancia a fallos. Un sistema entre servicios necesita otros mecanismos adicionales.

## 4. OCP y DIP: síntesis transversal

| Principio | Strategy: evidencia del RPG | Observer: evidencia del RPG | Beneficio |
| --- | --- | --- | --- |
| **OCP**: extender comportamiento sin cambiar el núcleo estable | Nuevos `IEstrategiaAtaque` sin modificar `Personaje.atacar()` | Nuevos `IObservador` sin modificar `TorreDeVigilancia.notificar()` | Menor superficie de modificación y de regresión |
| **DIP**: módulos de alto nivel se relacionan con abstracciones, no con detalles concretos | `Personaje` declara `arma: IEstrategiaAtaque` | La torre declara `List<IObservador>` | Sustitución, pruebas y desacoplamiento |

**Relación con otros SOLID, de forma complementaria:** SRP se favorece al separar el ataque, la gestión del personaje, la detección de eventos y la reacción del jugador. LSP requiere que cada estrategia u observador cumpla el contrato que ofrece; implementar una interfaz no prueba por sí solo toda la sustitución conductual. ISP resulta coherente con interfaces pequeñas, pero la evidencia principal solicitada para esta tarea es OCP y DIP.

## 5. Consecuencias de mantenimiento y límites

1. **Cambio localizado:** añadir un nuevo ataque se concentra en una clase de estrategia; añadir otro receptor de alertas se concentra en un observador nuevo y su registro.
2. **Menor acoplamiento:** los contextos y emisores interactúan con contratos estables y no invocan detalles de todas las clases concretas.
3. **Pruebas independientes:** se pueden sustituir estrategias u observadores por implementaciones de prueba para verificar delegación y notificación.
4. **Composición externa:** alguna parte del sistema sigue siendo responsable de seleccionar estrategias concretas y registrar observadores. Los patrones no eliminan esas decisiones.
5. **Costes:** las interfaces y clases adicionales aumentan la cantidad de componentes a comprender; convienen cuando hay variación real o destinatarios dinámicos.
6. **Límite de Observer implementado:** `actualizar()` no recibe datos del evento; `notificar()` invoca secuencialmente a los observadores en el mismo proceso. No equivale a publicación/suscripción distribuida.

## 6. Evidencia de trazabilidad y control antes de integrar

El informe se basa en estas clases del repositorio, consultadas en sus ramas de desarrollo:

- [`Personaje.java`](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/blob/b3ab472c2fefe40179c546db3d127d46af649c75/src/strategy/Personaje.java), [`IEstrategiaAtaque.java`](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/blob/b3ab472c2fefe40179c546db3d127d46af649c75/src/strategy/IEstrategiaAtaque.java), [estrategias concretas](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/tree/b3ab472c2fefe40179c546db3d127d46af649c75/src/strategy).
- [`TorreDeVigilancia.java`](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/blob/1d5631204732fba1354d06c834f53717a2bc4dc6/src/observer/TorreDeVigilancia.java), [`Jugador.java`](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/blob/1d5631204732fba1354d06c834f53717a2bc4dc6/src/observer/Jugador.java), [interfaces del paquete](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/tree/1d5631204732fba1354d06c834f53717a2bc4dc6/src/observer).
- [Diagramas de EST-07, PR #34](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/pull/34), usados como referencia arquitectónica; la integración a `main` aún debe coordinarse con el equipo.


## 7. Fuentes consultadas

1. Refactoring.Guru. (s. f.). [*Strategy*](https://refactoring.guru/es/design-patterns/strategy). Definición, colaboración entre contexto y estrategias, usos y limitaciones.
2. Refactoring.Guru. (s. f.). [*Observer*](https://refactoring.guru/es/design-patterns/observer). Suscripción, notificación y dependencia de interfaces.
3. Refactoring.Guru. (s. f.). [*Replace Conditional with Polymorphism*](https://refactoring.guru/replace-conditional-with-polymorphism). Contexto de refactorización de condicionales.
4. Shvets, A. (s. f.). [*Dive Into Design Patterns*, muestra en español](https://refactoring.guru/files/design-patterns-es-demo.pdf). Secciones de abierto/cerrado e inversión de dependencias.
5. Grupo 3. (2026). *Código Java del videojuego RPG*, revisiones de ramas mencionadas en la sección 6. Repositorio GitHub del grupo.

*Los contrastes rígidos, ejemplos empresariales y conclusiones de mantenimiento son análisis originales elaborados para el ejercicio; no se presentan como funcionalidades existentes del proyecto.*
