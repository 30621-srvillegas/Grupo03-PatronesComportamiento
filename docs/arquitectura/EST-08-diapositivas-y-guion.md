# EST-08 — Material para diapositivas y exposición

**Responsable:** Leonardo Figueroa | **Grupo 3** | **Duración orientativa:** 3–4 minutos, a coordinar con el equipo  
**Material base:** [Análisis arquitectónico completo](EST-08-analisis-arquitectonico.md)  
**Diagramas:** [EST-07, PR #34](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/pull/34)  
**Versión del código contrastada:** `main` en `b66dbd3` (19-11-2026), incluida `Principal.java`.


## Diapositiva 1 — ¿Qué problema arquitectónico resolvemos?

**En pantalla:**

- Strategy: cambiar comportamientos sin reescribir el contexto.
- Observer: notificar a varios receptores sin acoplarse a sus clases.
- Objetivo compartido: extensibilidad y menor acoplamiento.

**Guion sugerido:** «En el RPG tenemos dos responsabilidades diferentes: un personaje necesita cambiar su manera de atacar; una torre necesita enviar alertas a los jugadores interesados. Strategy aborda la variación del algoritmo. Observer permite que el emisor y los receptores colaboren mediante interfaces».

**Visual sugerido:** dos bloques paralelos, `Personaje → IEstrategiaAtaque` y `TorreDeVigilancia → IObservador`, ambos coordinados desde `Principal`.

## Diapositiva 2 — Strategy: condicionales frente a polimorfismo

**En pantalla:**

| Solución rígida hipotética | Strategy en el RPG |
| --- | --- |
| `atacar()` concentra casos de espada/fuego/hielo | `atacar()` delega a `arma.atacar(nombre)` |
| Añadir ataques exige editar el método | Añadir una estrategia no exige editar ese método |
| El contexto conoce cada variante | El contexto conoce `IEstrategiaAtaque` |

**Guion sugerido:** «El problema no es utilizar un `if` aislado: es que el personaje termine con una selección de algoritmos que crece cada vez que incorporamos un arma. Nuestra clase `Personaje` recibe `IEstrategiaAtaque` y podemos cambiar su implementación con `setArma()`».

**Visual sugerido:** insertar `docs/uml/strategy.svg` del PR de EST-07, **actualizado para incluir `Principal` y el mapa `armasDisponibles`**. El menú elige entre tres objetos concretos y usa `setArma()`.

## Diapositiva 3 — Observer: alertas sin acoplar receptores

**En pantalla:**

- Rígido hipotético: `torre` invoca directamente `jugador1.activarEscudo()`, `jugador2.activarEscudo()`…
- Implementado: `List<IObservador>` + `suscribir()`, `desuscribir()`, `notificar()`.
- Flujo: `detectarEnemigo() → notificar() → actualizar() → activarEscudo()`.

**Guion sugerido:** «La torre no necesita saber qué clase concreta recibe la alerta. Al detectar un enemigo, recorre una lista de observadores y llama a su contrato `actualizar()`. El jugador decide que su respuesta es activar el escudo. También podemos desuscribir un receptor».

**Visual sugerido:** insertar `docs/uml/observer.svg` del PR de EST-07, ahora con `Principal`, `torre: TorreDeVigilancia` y `jugadoresSuscritos: List<Jugador>`; no confundir esta lista con `List<IObservador>` de la torre.

## Diapositiva 4 — ¿Cómo aplicamos OCP y DIP?

**En pantalla:**

| SOLID | Strategy | Observer |
| --- | --- | --- |
| **OCP**: extender sin alterar el núcleo estable | Nueva clase de ataque | Nuevo tipo de observador |
| **DIP**: depender de abstracciones | `arma: IEstrategiaAtaque` | `List<IObservador>` |

**Guion sugerido:** «En Strategy no cambiamos `Personaje.atacar()` para incorporar otra implementación. En Observer no cambiamos `TorreDeVigilancia.notificar()` para registrar otro tipo de receptor. Ambos componentes centrales usan interfaces; pero `Principal` sí conoce y crea `AtaqueEspada`, `AtaqueFuego`, `AtaqueHielo` y `Jugador`. Por eso OCP y DIP se justifican en los núcleos de los patrones, no en cada línea del sistema».

## Diapositiva 5 — Uso empresarial y relación con RPG

**En pantalla:**

- **Strategy → comercio electrónico:** estrategias de precio o modalidad de envío; `Pedido` delega el cálculo.
- **Observer → seguimiento de pedidos:** al cambiar el estado, notifica a correo, seguimiento o auditoría.
- **RPG:** ataques sustituibles y alerta de torre a jugadores.

**Guion sugerido:** «Estos patrones no son exclusivos de juegos. Una tienda electrónica puede calcular tarifas usando distintas estrategias. También puede notificar cambios de un pedido a varios componentes. La estructura conceptual es semejante al personaje y a la torre de nuestro RPG».

**Precisión técnica:** el Observer del proyecto es local, síncrono y en memoria; no constituye por sí mismo un sistema de mensajería distribuida.

## Diapositiva 6 — Integración final, condicionales y mantenimiento

**En pantalla:**

- Strategy organiza variantes de algoritmos; Observer organiza suscriptores.
- Menos dependencia de detalles concretos y modificaciones más localizadas.
- Contratos estables favorecen pruebas y extensibilidad.
- `Principal` despacha opciones con `Map<Integer, Runnable>` y `getOrDefault`.
- Siguen existiendo `if` legítimos para validar armas, jugadores y entradas.
- Los patrones tienen costes: más tipos y necesidad de coordinar la composición.

**Guion sugerido:** «En la clase `Principal`, ya integrada, un mapa vincula números del menú con funciones `Runnable`; así no se requiere un gran `switch` de opciones. Esto complementa, pero no reemplaza, Strategy ni Observer. El código conserva validaciones `if` necesarias. Cada patrón debe evaluarse por su responsabilidad: cambiar algoritmos o comunicar eventos a suscriptores».

## Preguntas probables del docente

**¿Strategy elimina todos los `if`?** No. `Personaje.atacar()` evita ramas por tipo de arma, pero `Principal.cambiarArma()` usa `if (armaElegida == null)` para validar. Además el programa selecciona la estrategia a través de `Map<Integer, IEstrategiaAtaque>`.

**¿Qué diferencia hay entre Strategy y Observer?** Strategy selecciona el comportamiento que ejecuta un objeto. Observer notifica un evento a múltiples receptores suscritos.

**¿Qué evidencia concreta hay de DIP?** Los campos `Personaje.arma` y `TorreDeVigilancia.observadores` usan interfaces. `Principal`, en cambio, crea clases concretas, como corresponde al componente de integración.

**¿Se demuestra arquitectura distribuida?** No. La simulación es Java en un único proceso; el patrón Observer se podría adaptar a mensajería entre servicios, pero eso requeriría infraestructura adicional.

**¿Puede modificarse todo sin tocar ninguna clase existente?** No. OCP se aplica al núcleo diseñado para ser extensible bajo cambios previstos; ampliar contratos o cambiar requisitos puede exigir modificar código.


## Contraste con el menú interactivo definitivo

| Opción de `Principal` | Acción real | Patrón demostrado |
|---|---|---|
| 1. Atacar | `personaje::atacar` | Strategy con la estrategia activa |
| 2. Cambiar arma | `this::cambiarArma` → `personaje.setArma(armaElegida)` | Strategy con sustitución durante la ejecución |
| 3. Suscribir jugador | `this::suscribirJugador` → `new Jugador(nombre)` → `torre.suscribir(jugador)` | Observer / registro |
| 4. Desuscribir jugador | `this::desuscribirJugador` → `torre.desuscribir(jugador)` | Observer / baja |
| 5. Detectar enemigo | `this::detectarEnemigo` → `torre.detectarEnemigo()` | Observer / notificación |

**Fuente concreta:** [`src/Principal.java` de `main` (`b66dbd3`)](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/blob/b66dbd3c9659dc4188bf502ef1b2c149dfb15541/src/Principal.java). Las relaciones y firmas del resto de clases corresponden a esa misma revisión. La exposición debe usar los UML actualizados de EST-07.
