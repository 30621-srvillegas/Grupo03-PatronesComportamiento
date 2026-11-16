# EST-08 — Material para diapositivas y exposición

**Responsable:** Leonardo Figueroa | **Grupo 3** | **Duración orientativa:** 3–4 minutos, a coordinar con el equipo  
**Material base:** [Análisis arquitectónico completo](EST-08-analisis-arquitectonico.md)  
**Diagramas:** [EST-07, PR #34](https://github.com/30621-srvillegas/Grupo03-PatronesComportamiento/pull/34)


## Diapositiva 1 — ¿Qué problema arquitectónico resolvemos?

**En pantalla:**

- Strategy: cambiar comportamientos sin reescribir el contexto.
- Observer: notificar a varios receptores sin acoplarse a sus clases.
- Objetivo compartido: extensibilidad y menor acoplamiento.

**Guion sugerido:** «En el RPG tenemos dos responsabilidades diferentes: un personaje necesita cambiar su manera de atacar; una torre necesita enviar alertas a los jugadores interesados. Strategy aborda la variación del algoritmo. Observer permite que el emisor y los receptores colaboren mediante interfaces».

**Visual sugerido:** dos bloques paralelos, `Personaje → IEstrategiaAtaque` y `TorreDeVigilancia → IObservador`.

## Diapositiva 2 — Strategy: condicionales frente a polimorfismo

**En pantalla:**

| Solución rígida hipotética | Strategy en el RPG |
| --- | --- |
| `atacar()` concentra casos de espada/fuego/hielo | `atacar()` delega a `arma.atacar(nombre)` |
| Añadir ataques exige editar el método | Añadir una estrategia no exige editar ese método |
| El contexto conoce cada variante | El contexto conoce `IEstrategiaAtaque` |

**Guion sugerido:** «El problema no es utilizar un `if` aislado: es que el personaje termine con una selección de algoritmos que crece cada vez que incorporamos un arma. Nuestra clase `Personaje` recibe `IEstrategiaAtaque` y podemos cambiar su implementación con `setArma()`».

**Visual sugerido:** insertar `strategy.svg` del PR de EST-07.

## Diapositiva 3 — Observer: alertas sin acoplar receptores

**En pantalla:**

- Rígido hipotético: `torre` invoca directamente `jugador1.activarEscudo()`, `jugador2.activarEscudo()`…
- Implementado: `List<IObservador>` + `suscribir()`, `desuscribir()`, `notificar()`.
- Flujo: `detectarEnemigo() → notificar() → actualizar() → activarEscudo()`.

**Guion sugerido:** «La torre no necesita saber qué clase concreta recibe la alerta. Al detectar un enemigo, recorre una lista de observadores y llama a su contrato `actualizar()`. El jugador decide que su respuesta es activar el escudo. También podemos desuscribir un receptor».

**Visual sugerido:** insertar `observer.svg` del PR de EST-07.

## Diapositiva 4 — ¿Cómo aplicamos OCP y DIP?

**En pantalla:**

| SOLID | Strategy | Observer |
| --- | --- | --- |
| **OCP**: extender sin alterar el núcleo estable | Nueva clase de ataque | Nuevo tipo de observador |
| **DIP**: depender de abstracciones | `arma: IEstrategiaAtaque` | `List<IObservador>` |

**Guion sugerido:** «En Strategy no cambiamos el método `Personaje.atacar()` para incorporar una nueva implementación. En Observer no cambiamos la lógica `notificar()` para registrar un observador de otro tipo. En ambos casos el componente que coordina depende de una interfaz. Esto aplica OCP y DIP, pero no significa que cualquier cambio futuro pueda hacerse sin modificar ninguna parte del programa».

## Diapositiva 5 — Uso empresarial y relación con RPG

**En pantalla:**

- **Strategy → comercio electrónico:** estrategias de precio o modalidad de envío; `Pedido` delega el cálculo.
- **Observer → seguimiento de pedidos:** al cambiar el estado, notifica a correo, seguimiento o auditoría.
- **RPG:** ataques sustituibles y alerta de torre a jugadores.

**Guion sugerido:** «Estos patrones no son exclusivos de juegos. Una tienda electrónica puede calcular tarifas usando distintas estrategias. También puede notificar cambios de un pedido a varios componentes. La estructura conceptual es semejante al personaje y a la torre de nuestro RPG».

**Precisión técnica:** el Observer del proyecto es local, síncrono y en memoria; no constituye por sí mismo un sistema de mensajería distribuida.

## Diapositiva 6 — Conclusiones y mantenimiento

**En pantalla:**

- Strategy organiza variantes de algoritmos; Observer organiza suscriptores.
- Menos dependencia de detalles concretos y modificaciones más localizadas.
- Contratos estables favorecen pruebas y extensibilidad.
- Los patrones tienen costes: más tipos y necesidad de coordinar la composición.

**Guion sugerido:** «Elegimos cada patrón por el problema que resuelve. Strategy evita concentrar todas las variantes del ataque en `Personaje`; Observer permite extender los receptores sin añadir llamadas concretas a la torre. Los beneficios dependen de utilizar correctamente las interfaces y de que exista una necesidad real de variación o suscripción».

## Preguntas probables del docente

**¿Strategy elimina todos los `if`?** No. Evita que el contexto concentre las condiciones asociadas a las variantes; el cliente aún puede elegir qué estrategia crear.

**¿Qué diferencia hay entre Strategy y Observer?** Strategy selecciona el comportamiento que ejecuta un objeto. Observer notifica un evento a múltiples receptores suscritos.

**¿Qué evidencia concreta hay de DIP?** Los tipos de los campos `Personaje.arma` y `TorreDeVigilancia.observadores` son interfaces, no clases concretas.

**¿Se demuestra arquitectura distribuida?** No. La simulación es Java en un único proceso; el patrón Observer se podría adaptar a mensajería entre servicios, pero eso requeriría infraestructura adicional.

**¿Puede modificarse todo sin tocar ninguna clase existente?** No. OCP se aplica al núcleo diseñado para ser extensible bajo cambios previstos; ampliar contratos o cambiar requisitos puede exigir modificar código.
