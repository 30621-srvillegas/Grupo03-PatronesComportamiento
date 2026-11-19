# EST-08 — Análisis de arquitectura, condicionales y principios SOLID

**Proyecto:** Grupo 3 — Patrones de comportamiento en un videojuego RPG  
**Responsable:** Leonardo Figueroa (`@30621-lmfigueroa1`)  
**Rol:** teoría y análisis de arquitectura; no implica implementación en Java  
**Tarea GitHub:** [EST-08, issue #8](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/issues/8)

> **Alcance de la verificación actualizado (19-11-2026):** se revisó el proyecto **integrado en `main`, commit `b66dbd3c9659dc4188bf502ef1b2c149dfb15541`**: `Principal.java`, los cinco archivos Java de `strategy` y los cuatro de `observer`. Las implementaciones de los patrones mantienen las mismas firmas principales; el cambio arquitectónico relevante respecto al primer análisis es el **cliente integrador `Principal`**, con menú interactivo, registros y selección de estrategias. Los ejemplos rígidos siguen siendo hipotéticos.

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

En el programa final, `Principal` crea `AtaqueEspada`, `AtaqueFuego` y `AtaqueHielo`, las registra en `armasDisponibles: Map<Integer, IEstrategiaAtaque>` y crea `Personaje("Arthas", armasDisponibles.get(1))`. El menú se despacha mediante `acciones: Map<Integer, Runnable>` y `acciones.getOrDefault(opcion, ...).run()`; no utiliza una cadena de condicionales para ejecutar acciones del menú. El método `cambiarArma()` consulta el mapa, verifica `armaElegida == null` y llama `personaje.setArma(armaElegida)`. **La elección y creación de estrategias concretas sí está en `Principal`: el núcleo `Personaje` permanece desacoplado, pero el cliente integrador conoce los tres tipos de ataque.**

La interacción efectiva es:

```text
Principal consulta armasDisponibles y obtiene la IEstrategiaAtaque seleccionada
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

**OCP — Abierto/Cerrado.** `Personaje` está abierto a **extensión del comportamiento** mediante nuevas implementaciones de `IEstrategiaAtaque` y puede permanecer sin modificaciones al incorporarlas. Esto se verifica **en el núcleo Strategy**, no en todo el proyecto: para ofrecer una cuarta arma por el menú actual sí habría que incorporarla a la inicialización de `armasDisponibles` en `Principal` (u otro componente de composición). La extensión no modifica `Personaje.atacar()`, pero no es completamente automática.

**DIP — Inversión de Dependencias.** La política de delegación del personaje no depende de `AtaqueFuego`, `AtaqueHielo` ni `AtaqueEspada`, sino de `IEstrategiaAtaque`. Las clases concretas también dependen del contrato al implementarlo. La inyección de estrategia por constructor y por `setArma()` facilita sustituir implementaciones; **la inyección es un mecanismo, mientras DIP es un principio de diseño**. `Principal` depende también de las implementaciones concretas al crearlas: es un cliente de composición y no debe describirse como completamente desacoplado.

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

En la versión integrada, `Principal` mantiene `torre: TorreDeVigilancia` y `jugadoresSuscritos: List<Jugador>`. Su método `suscribirJugador()` lee un nombre, crea `new Jugador(nombre)`, ejecuta `torre.suscribir(jugador)` y agrega esa misma referencia a la lista local; `desuscribirJugador()` selecciona un índice, retira la referencia local y llama `torre.desuscribir(jugador)`; `detectarEnemigo()` delega a la torre. Esa **lista auxiliar de `Principal` no es la lista interna `List<IObservador>` de la torre**: son dos colecciones distintas con propósitos diferentes.

La secuencia del código existente es:

```text
Principal crea Jugador y lo registra mediante torre.suscribir(jugador)
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

**DIP — Inversión de Dependencias.** `TorreDeVigilancia` gestiona una lista de la abstracción `IObservador`, no una lista de `Jugador`. `ISujeto` utiliza también el contrato `IObservador` para administrar suscripciones. Así el emisor no depende de las implementaciones de respuesta. La capa de demostración `Principal`, sin embargo, **sí** usa el tipo concreto `Jugador` para crearlo y seleccionarlo por índice: no corresponde atribuirle el mismo nivel de desacoplamiento que a `TorreDeVigilancia`.

**Mantenimiento:** las reacciones de los observadores quedan localizadas en sus clases; se pueden ensayar la suscripción, desuscripción y fan-out sin mezclar las responsabilidades de la torre con la activación concreta del escudo.

### 3.5 Ejemplo empresarial realista: seguimiento de pedidos

En un sistema comercial, un módulo de pedidos puede publicar cambios de estado a componentes suscritos (correo, panel de seguimiento o auditoría). Cada receptor decide su respuesta y el emisor no tiene que incorporar la lógica específica de todos ellos. **Equivalencia con el RPG:** el módulo que detecta el cambio equivale a `TorreDeVigilancia`; los servicios interesados equivalen a los observadores; el cambio de estado equivale a `detectarEnemigo()`.

Se trata de una **analogía arquitectónica**. El proyecto Java del grupo implementa notificaciones **en memoria, síncronas y locales al mismo proceso**; no demuestra mensajería distribuida, entrega remota, persistencia, colas ni tolerancia a fallos. Un sistema entre servicios necesita otros mecanismos adicionales.

## 4. Arquitectura final integrada: responsabilidades de Principal y condicionales reales

La clase `Principal` completa las dos demostraciones en un único proceso Java por consola. Su estructura, ausente en el análisis inicial, explica cómo se **componen** los patrones:

| Campo o método de `Principal` | Función implementada | Consecuencia arquitectónica |
| --- | --- | --- |
| `Map<Integer, IEstrategiaAtaque> armasDisponibles` | Registra las tres instancias de ataque por número. | Permite selección por datos; `Principal` conoce los tipos concretos que instancia. |
| `Map<Integer, Runnable> acciones` | Relaciona opciones 1–5 con referencias a métodos y ejecuta `getOrDefault(...).run()`. | Sustituye un despacho central rígido por una tabla de acciones; **esto no es una tercera implementación del patrón Strategy**. |
| `Personaje personaje` | Recibe inicialmente `AtaqueEspada`; después admite `setArma()`. | Cliente → contexto → interfaz → estrategia concreta. |
| `TorreDeVigilancia torre` | Ejecuta `suscribir()`, `desuscribir()` y `detectarEnemigo()`. | Cliente concreto de la torre; la torre mantiene su propio contrato con observadores. |
| `List<Jugador> jugadoresSuscritos` | Permite mostrar y seleccionar jugadores por índice en el menú. | Acoplamiento del menú a `Jugador`; no sustituye `List<IObservador>` dentro de la torre. |
| `leerEntero()`, `leerTexto()` | Validan entradas de consola. | Mantienen condicionales y bucles **legítimos** ajenos a los algoritmos de ataque o al envío de notificaciones. |

### Condicionales y despacho: distinción verificable

- **No hay `switch` ni cadena de `if-else` por tipo de arma en `Personaje.atacar()`**: usa polimorfismo mediante `arma.atacar(nombre)`.
- **No hay un `switch` principal para las opciones del menú**: `Principal` usa `Map<Integer, Runnable>` y `getOrDefault`, registrando referencias a métodos como `personaje::atacar` y `this::detectarEnemigo`.
- **Sí hay condiciones reales**, por ejemplo `if (armaElegida == null)` para validar un arma, `if (jugadoresSuscritos.isEmpty())` para impedir desuscripciones imposibles, comprobación de índices, `if (opcion != OPCION_SALIR)` y `try/catch` al leer enteros.
- **Observer también contiene condiciones y un bucle intencionales:** la torre comprueba `!observadores.contains(observador)` antes de añadir y usa `for` para notificar a cada observador. El patrón no elimina la necesidad de control de flujo.
- `acciones` evita condicionales de despacho; es una elección adicional de diseño basada en funciones `Runnable`, no el objetivo principal del patrón Observer ni una afirmación de que todo el programa es libre de condiciones.

### Recorrido real demostrable

**Strategy:** opción **1** → `personaje.atacar()` con espada; opción **2** → consulta del mapa y `personaje.setArma(armaElegida)`; opción **1** nuevamente → misma operación del personaje, distinta estrategia activa.

**Observer:** opción **3** → `new Jugador(nombre)` y `torre.suscribir(jugador)`; opción **5** → `detectarEnemigo()` → `notificar()` → `Jugador.actualizar()` → `activarEscudo()`; opción **4** → `desuscribirJugador()` y `torre.desuscribir(jugador)`; siguiente detección → ese jugador ya no es notificado.

**Límites técnicos:** las notificaciones son síncronas, en memoria y del mismo proceso, no mensajes distribuidos. `actualizar()` no recibe información del enemigo; la torre notifica secuencialmente. La extensión OCP/DIP se verifica principalmente en `Personaje` y `TorreDeVigilancia`; `Principal` es un cliente que conoce clases concretas.

## 5. OCP y DIP: síntesis transversal

| Principio | Strategy: evidencia del RPG | Observer: evidencia del RPG | Beneficio |
| --- | --- | --- | --- |
| **OCP**: extender comportamiento sin cambiar el núcleo estable | Nuevos `IEstrategiaAtaque` sin modificar `Personaje.atacar()` | Nuevos `IObservador` sin modificar `TorreDeVigilancia.notificar()` | Menor superficie de modificación y de regresión |
| **DIP**: módulos de alto nivel se relacionan con abstracciones, no con detalles concretos | `Personaje` declara `arma: IEstrategiaAtaque` | La torre declara `List<IObservador>` | Sustitución, pruebas y desacoplamiento |

**Relación con otros SOLID, de forma complementaria:** SRP se favorece al separar el ataque, la gestión del personaje, la detección de eventos y la reacción del jugador. LSP requiere que cada estrategia u observador cumpla el contrato que ofrece; implementar una interfaz no prueba por sí solo toda la sustitución conductual. ISP resulta coherente con interfaces pequeñas, pero la evidencia principal solicitada para esta tarea es OCP y DIP.

## 6. Consecuencias de mantenimiento y límites

1. **Cambio localizado:** añadir un nuevo ataque se concentra en una clase de estrategia; añadir otro receptor de alertas se concentra en un observador nuevo y su registro.
2. **Menor acoplamiento:** los contextos y emisores interactúan con contratos estables y no invocan detalles de todas las clases concretas.
3. **Pruebas independientes:** se pueden sustituir estrategias u observadores por implementaciones de prueba para verificar delegación y notificación.
4. **Composición externa:** alguna parte del sistema sigue siendo responsable de seleccionar estrategias concretas y registrar observadores. Los patrones no eliminan esas decisiones.
5. **Costes:** las interfaces y clases adicionales aumentan la cantidad de componentes a comprender; convienen cuando hay variación real o destinatarios dinámicos.
6. **Límite de Observer implementado:** `actualizar()` no recibe datos del evento; `notificar()` invoca secuencialmente a los observadores en el mismo proceso. No equivale a publicación/suscripción distribuida.

## 7. Evidencia de trazabilidad del código integrado

El análisis se verificó contra la revisión `b66dbd3` de **`main`**, que contiene las interfaces, las implementaciones de ambas mecánicas y el menú interactivo. Los siguientes enlaces históricos muestran de dónde proceden originalmente las clases:

- [`Personaje.java`](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/blob/b3ab472c2fefe40179c546db3d127d46af649c75/src/strategy/Personaje.java), [`IEstrategiaAtaque.java`](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/blob/b3ab472c2fefe40179c546db3d127d46af649c75/src/strategy/IEstrategiaAtaque.java), [estrategias concretas](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/tree/b3ab472c2fefe40179c546db3d127d46af649c75/src/strategy).
- [`TorreDeVigilancia.java`](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/blob/1d5631204732fba1354d06c834f53717a2bc4dc6/src/observer/TorreDeVigilancia.java), [`Jugador.java`](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/blob/1d5631204732fba1354d06c834f53717a2bc4dc6/src/observer/Jugador.java), [interfaces del paquete](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/tree/1d5631204732fba1354d06c834f53717a2bc4dc6/src/observer).
- [`Principal.java` integrado](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/blob/b66dbd3c9659dc4188bf502ef1b2c149dfb15541/src/Principal.java), revisado para completar la arquitectura y las condiciones existentes.
- [Diagramas EST-07, PR #34](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/pull/34), actualizados para incluir `Principal` y sus asociaciones.


## 8. Fuentes consultadas

1. Refactoring.Guru. (s. f.). [*Strategy*](https://refactoring.guru/es/design-patterns/strategy). Definición, colaboración entre contexto y estrategias, usos y limitaciones.
2. Refactoring.Guru. (s. f.). [*Observer*](https://refactoring.guru/es/design-patterns/observer). Suscripción, notificación y dependencia de interfaces.
3. Refactoring.Guru. (s. f.). [*Replace Conditional with Polymorphism*](https://refactoring.guru/replace-conditional-with-polymorphism). Contexto de refactorización de condicionales.
4. Shvets, A. (s. f.). [*Dive Into Design Patterns*, muestra en español](https://refactoring.guru/files/design-patterns-es-demo.pdf). Secciones de abierto/cerrado e inversión de dependencias.
5. Grupo 3. (2026). *Código Java del videojuego RPG*, integración en `main` (commit `b66dbd3`), además de las revisiones de origen enumeradas en la sección 7. Repositorio GitHub del grupo.

*Los contrastes rígidos, ejemplos empresariales y conclusiones de mantenimiento son análisis originales elaborados para el ejercicio; no se presentan como funcionalidades existentes del proyecto.*
