# Patrón Strategy

## Qué es y qué problema resuelve

El patrón Strategy, o Estrategia, es un patrón de diseño de comportamiento que permite definir distintas formas de realizar una misma tarea. Cada alternativa se implementa por separado y puede utilizarse en lugar de otra porque todas ofrecen la misma operación. De esta manera, el objeto que necesita realizar la tarea puede trabajar con la estrategia que corresponda sin conocer los detalles de su implementación (Gamma et al., 1995).

Pensemos en una aplicación que calcula rutas para auto, bus y bicicleta. Si una sola clase contiene las reglas de los tres casos, cada vez que agreguemos un medio de transporte tendremos que modificar esa clase. Además, será más difícil ubicar qué parte del código corresponde a cada recorrido. Con Strategy, las reglas de cada medio se pueden implementar de manera independiente y la aplicación puede seguir solicitando el cálculo de la ruta de la misma forma (Refactoring Guru, s. f.).

## Cómo funciona

El objeto que necesita realizar la tarea se conoce como **contexto**. Este guarda una referencia a la estrategia activa y le encarga la operación cuando la necesita. La **interfaz de estrategia** indica qué operación deben ofrecer todas las alternativas. Por ejemplo, cada **estrategia concreta** puede implementar `calcularRuta()` con reglas propias. La estrategia para auto puede tener en cuenta las vías habilitadas para vehículos y la de bus puede utilizar las paradas disponibles (Refactoring Guru, s. f.).

La elección de la estrategia puede depender del usuario o de una regla de la aplicación. Si el usuario selecciona primero el auto y luego cambia a bicicleta, la aplicación puede reemplazar la estrategia activa mientras está funcionando. En la siguiente solicitud, el contexto vuelve a llamar a `calcularRuta()`, y esta vez se aplican las reglas de la bicicleta. Esto es posible porque las dos estrategias ofrecen la misma operación (Refactoring Guru, s. f.).

## Analogía con los medios de transporte

Cuando queremos llegar a la universidad, podemos ir en auto, tomar un bus o usar la bicicleta. La elección depende de las condiciones del momento. Por ejemplo, si hay mucho tráfico y el recorrido es corto, podemos optar por la bicicleta. Si el trayecto es más largo y hay una línea directa, podemos tomar el bus. En ambos casos queremos llegar al mismo lugar y la decisión cambia la forma de hacerlo.

En la aplicación, el destino representa la parte de la solicitud que se conserva. El medio elegido determina qué estrategia calcula el recorrido. Por eso podemos usar el mismo destino y obtener rutas distintas al cambiar de medio de transporte. Esta analogía adapta el ejemplo de navegación presentado por Refactoring Guru (s. f.).

## Cuándo conviene usarlo

Strategy resulta útil cuando ya existen varias formas de realizar una tarea y necesitamos elegir entre ellas según las condiciones del momento. También facilita añadir una alternativa cuando el sistema crece. Por ejemplo, si más adelante incorporamos una ruta a pie, podemos implementar sus reglas en una nueva estrategia que respete la interfaz existente. La aplicación deberá ofrecer esa opción al usuario, pero el contexto podrá seguir solicitando el cálculo con la misma operación (Refactoring Guru, s. f.).

Cada estrategia agrega una implementación que debemos mantener. Si la tarea tiene una sola variante o las diferencias entre alternativas son pequeñas y estables, una solución directa puede ser suficiente (Refactoring Guru, s. f.).

## Referencias

- Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1995). *Design patterns: Elements of reusable object-oriented software*. Addison-Wesley. [Ficha editorial e índice](https://www.informit.com/store/design-patterns-elements-of-reusable-object-oriented-9780321770462)
- Refactoring Guru. (s. f.). *Strategy*. [https://refactoring.guru/es/design-patterns/strategy](https://refactoring.guru/es/design-patterns/strategy)
