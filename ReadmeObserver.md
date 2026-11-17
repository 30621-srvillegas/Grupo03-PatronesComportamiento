# Investigación del Patrón Observer

Lo primero es conocer que existen tres clasificaciones principales de patrones de diseño de software:

1. **Creacionales**

   Se centran en la creación de objetos de una manera segura y flexible, evitando la acentuación de antipatrones y garantizando que el proceso de instanciación no interfiera directamente con la lógica principal de la aplicación.

2. **Estructurales**

   Consisten en definir cómo las clases y objetos se componen para formar estructuras más grandes y complejas, asegurando que funcionen de manera coordinada y eficiente.

3. **De comportamiento**

   Este grupo de patrones se centra en la distribución de responsabilidades y en cómo los objetos interactúan y se comunican entre sí. Dentro de este grupo encontramos al patrón Observer (Observador).

## El Patrón Observer

El patrón Observer hace honor a su nombre, ya que su propósito principal consiste en vigilar los cambios de estado en un objeto central denominado **Sujeto** (o **Observable**). Al detectar cualquier modificación en dicho objeto, los componentes suscritos, conocidos como **Observers** (o **Observadores**), reaccionan y actualizan su estado rápidamente.

### Diferencia detallada entre el Sujeto (Observable) y el Observador (Observer)

Para comprender de manera adecuada este patrón, es fundamental distinguir la responsabilidad de cada uno de sus actores principales:

- **El Sujeto (Subject / Observable):** Es la entidad que posee o custodia los datos principales y mantiene el estado relevante de la aplicación. Posee una lista de observadores registrados y proporciona métodos explícitos para agregar (suscribir), remover (desuscribir) y notificar automáticamente a todos sus suscritos cuando ocurre un cambio significativo en su estado internal.

- **El Observador (Observer):** Es el objeto dependiente que necesita enterarse de las actualizaciones del Sujeto. Implementa una interfaz común de actualización (generalmente con un método *update()*) que el Sujeto invoca al momento de notificar los cambios. De este modo, los Observadores pueden responder ejecutando su propia lógica interna sin modificar al Sujeto.

### Analogía: El canal de YouTube y las notificaciones

Una forma intuitiva de entender esta relación es compararla con un canal de YouTube:

- **El canal de YouTube** actúa como el **Sujeto (Observable)**. Es quien genera el contenido original y gestiona su lista de suscriptores.

- **Los usuarios que hacen clic en "Suscribirse" y activan la campanita** son los **Observadores (Observers)**.

- Cuando el creador publica un nuevo video (cambio de estado), no se dedica a llamar a cada usuario individualmente; en su lugar, la plataforma envía de forma automática una **notificación** a todos los suscriptores activos. Cada suscriptor, al recibir el aviso, decide libremente cuándo y cómo reaccionar (ver el video inmediatamente, guardarlo para más tarde o ignorarlo).

### ¿Cuándo se debe usar el patrón Observer?

Este patrón debe implementarse en los siguientes escenarios concretos de desarrollo:

- Cuando una modificación en el estado de un objeto requiere actualizar automáticamente otros objetos, y no se conoce de antemano cuántos objetos necesitan ser actualizados.

- Cuando un objeto debe ser capaz de notificar a otros sin asumir ningún conocimiento estricto sobre la clase concreta de dichos objetos (desacoplamiento).

- En arquitecturas orientadas a eventos, interfaces gráficas de usuario (GUI) y sistemas de procesamiento reactivo de datos en tiempo real.

### Comportamiento asincrónico y servicios de suscripción

Esto también resalta la capacidad de este patrón para el comportamiento asincrónico y el manejo de hilos, ya que su funcionamiento se basa en un servicio de suscripción, permitiendo que el Observer reciba notificaciones automáticas ante cualquier modificación.

Este mecanismo evita que los componentes de la aplicación tengan que realizar consultas constantes (polling) sobre el estado del objeto principal. Así se logra una arquitectura limpia, desacoplada y una comunicación más fluida entre las partes del sistema.

Según Berrio Huamani (2025), el patrón Observer destaca por su capacidad en la programación concurrente y asincrónica, sobre todo en aplicaciones que manejan varias tareas al mismo tiempo. En su artículo explica cómo ciertas interfaces usan flujos observables apoyándose en los patrones Observer e Iterator junto a la programación funcional. Un caso claro es **ReactiveX**, tecnología que ayuda al manejo de hilos y a notificar sobre errores asincrónicos, manteniendo la información al día durante la ejecución de múltiples procesos.

También este patrón es bastante útil cuando varios objetos deben reaccionar al detectar un cambio en la información. Por ejemplo, en un sistema de compras o ventas, al cambiar el estado del pedido se notifica rápidamente al inventario y a los avisos del cliente. Esto evita que los objetos interfieran entre sí y permite ampliar el sistema sin complicaciones. En la programación en paralelo expuesta por el autor, sirve como base para organizar eventos y flujos de datos, aunque por sí solo no asegura el manejo de hilos de forma automática.

## References

Miranda, E. D., & Urrego, J. D. (2023). *Análisis práctico del desempeño de los patrones de diseño de software: Observer, Strategy y Command en una aplicación* [Trabajo de grado, Universidad Antonio Nariño]. Repositorio Institucional UAN. http://hdl.handle.net/10823/7552

Berrio Huamani, M. A. (2025). Implementación de hilos virtuales para mejorar la velocidad de procesamiento usando JAVA 21. *Revista Científica: BIOTECH AND ENGINEERING*, *4*(3), 1-15. https://doi.org/10.52248/eb.vol4iss3.118
