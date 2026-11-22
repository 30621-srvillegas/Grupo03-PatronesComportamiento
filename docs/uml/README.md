# Diagramas UML del videojuego RPG

## Diagrama general del código

![Diagrama UML general](general.svg)

El UML general representa los diez tipos Java declarados en `src`: siete clases y tres interfaces. `Principal` aparece una sola vez con todos sus atributos y métodos explícitos, conectando ambos paquetes. Los archivos `package-info.java` documentan paquetes y no declaran tipos adicionales. Las clases de biblioteca estándar (`Scanner`, `Map`, `List`, `Runnable`, etc.) se muestran en los tipos de los miembros, no como clases propias del proyecto.

| Vista | Imagen SVG | Fuente editable |
|---|---|---|
| General | [general.svg](general.svg) | [general.puml](general.puml) |
| Strategy | [strategy.svg](strategy.svg) | [strategy.puml](strategy.puml) |
| Observer | [observer.svg](observer.svg) | [observer.puml](observer.puml) |

**Legibilidad:** texto de miembros de 24 px y encabezados de 30 px en las imágenes SVG; separación amplia entre cajas, conectores diferenciados y lienzo vectorial ampliable. Las fuentes PlantUML conservan la estructura y ajustes de espaciado, aunque su distribución automática puede variar respecto a los SVG.

**Notación:** `+` público, `-` privado, `{static}` miembro estático, `{readOnly}` campo final. Las multiplicidades se indican en el extremo destino: una estrategia activa, tres estrategias disponibles en el mapa inicial, una torre y cero o más jugadores/observadores. Las dependencias `«create»` representan la creación de las estrategias concretas en `Principal`. No se impone una multiplicidad inversa que el código no garantiza.

**Versión contrastada:** `main`, commit `d9749cbbe9a783c0ddd7fb43623f965bfb311fe1`. Las vistas específicas muestran los miembros de `Principal` relevantes a cada patrón, mientras que la vista general incluye todos sus miembros declarados.

## Strategy: diagrama final integrado

![Diagrama UML de Strategy](strategy.svg)

Se representan las clases implementadas: `Principal`, `Personaje`, `IEstrategiaAtaque`, `AtaqueEspada`, `AtaqueFuego` y `AtaqueHielo`.

- `Personaje` es el **contexto**; mantiene `arma: IEstrategiaAtaque` y delega `atacar()` mediante `arma.atacar(nombre)`.
- Las tres estrategias concretas **realizan** la interfaz `IEstrategiaAtaque`; la flecha de realización es una línea discontinua con triángulo hueco hacia la interfaz.
- `Principal` contiene `personaje: Personaje`, `armasDisponibles: Map<Integer, IEstrategiaAtaque>` y `acciones: Map<Integer, Runnable>`. Inicializa las tres estrategias concretas en su constructor y cambia el arma a través de `cambiarArma()` y `personaje.setArma(armaElegida)`.
- Las líneas sólidas con navegabilidad representan las **asociaciones** de los campos, no herencia: `Principal → Personaje`, `Principal → IEstrategiaAtaque` a través de los valores del mapa y `Personaje → IEstrategiaAtaque`.


## Observer: diagrama final integrado

![Diagrama UML de Observer](observer.svg)

Se representan `Principal`, `ISujeto`, `IObservador`, `TorreDeVigilancia` y `Jugador`.

- `TorreDeVigilancia` **realiza** `ISujeto`, mantiene `observadores: List<IObservador>` y conserva una **agregación compartida con 0..* observadores**: guarda referencias sin controlar el ciclo de vida de los observadores.
- `Jugador` **realiza** `IObservador`, define `actualizar()` y en esa operación invoca `activarEscudo()`.
- `ISujeto` **depende de** `IObservador` porque sus métodos de suscripción y desuscripción reciben esa interfaz como parámetro.
- `Principal` mantiene `torre: TorreDeVigilancia` y `jugadoresSuscritos: List<Jugador>`, crea los jugadores, los suscribe o desuscribe, y llama `torre.detectarEnemigo()`.
- Flujo comprobado: `detectarEnemigo() → notificar() → actualizar() → activarEscudo()`. La torre itera sobre una copia temporal de la lista de observadores.

## Verificación frente al código

| Archivo Java en main | Comprobación en los UML |
|---|---|
| [`Principal.java`](../../src/Principal.java) | Instancia `Personaje` y `TorreDeVigilancia`; administra `Map<Integer, IEstrategiaAtaque>`, `Map<Integer, Runnable>` y `List<Jugador>`; métodos de interacción por menú. |
| [`strategy/Personaje.java`](../../src/strategy/Personaje.java) | Constructor, `setArma`, `atacar`, `getNombre` y relación con `IEstrategiaAtaque`. |
| [`strategy/IEstrategiaAtaque.java`](../../src/strategy/IEstrategiaAtaque.java) | Método `atacar(String nombreAtacante)`. |
| [`strategy/AtaqueEspada.java`](../../src/strategy/AtaqueEspada.java), [`AtaqueFuego.java`](../../src/strategy/AtaqueFuego.java) y [`AtaqueHielo.java`](../../src/strategy/AtaqueHielo.java) | Implementaciones concretas y firmas correctas. |
| [`observer/ISujeto.java`](../../src/observer/ISujeto.java) y [`IObservador.java`](../../src/observer/IObservador.java) | Contratos de suscripción/notificación y actualización. |
| [`observer/TorreDeVigilancia.java`](../../src/observer/TorreDeVigilancia.java) | `List<IObservador>`, suscripción, desuscripción, notificación y detección. |
| [`observer/Jugador.java`](../../src/observer/Jugador.java) | `nombre`, constructor, `getNombre()`, `actualizar()` y `activarEscudo()`. |

