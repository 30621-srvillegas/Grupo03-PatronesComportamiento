import observer.Jugador;
import observer.TorreDeVigilancia;
import strategy.AtaqueEspada;
import strategy.AtaqueFuego;
import strategy.AtaqueHielo;
import strategy.IEstrategiaAtaque;
import strategy.Personaje;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Punto de entrada del Simulador RPG.
 * Menu interactivo por consola para demostrar en vivo
 * los patrones Strategy y Observer.
 */
public class Principal {

    private static final int OPCION_SALIR = 0;

    private final Scanner scanner = new Scanner(System.in);
    private final Map<Integer, IEstrategiaAtaque> armasDisponibles = new LinkedHashMap<>();
    private final Map<Integer, Runnable> acciones = new LinkedHashMap<>();
    private final List<Jugador> jugadoresSuscritos = new ArrayList<>();
    private final TorreDeVigilancia torre = new TorreDeVigilancia();
    private final Personaje personaje;

    public Principal() {
        armasDisponibles.put(1, new AtaqueEspada());
        armasDisponibles.put(2, new AtaqueFuego());
        armasDisponibles.put(3, new AtaqueHielo());

        personaje = new Personaje("Arthas", armasDisponibles.get(1));

        acciones.put(1, personaje::atacar);
        acciones.put(2, this::cambiarArma);
        acciones.put(3, this::suscribirJugador);
        acciones.put(4, this::desuscribirJugador);
        acciones.put(5, this::detectarEnemigo);
    }

    public static void main(String[] args) {
        new Principal().iniciar();
    }

    private void iniciar() {
        System.out.println("=== Simulador RPG: Patrones Strategy y Observer ===");
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Elige una opcion: ");
            if (opcion != OPCION_SALIR) {
                acciones.getOrDefault(opcion, () -> System.out.println("Opcion no valida.")).run();
            }
        } while (opcion != OPCION_SALIR);
        System.out.println("Fin de la simulacion.");
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("------ STRATEGY ------");
        System.out.println("1. Atacar");
        System.out.println("2. Cambiar arma");
        System.out.println("------ OBSERVER ------");
        System.out.println("3. Suscribir jugador a la torre");
        System.out.println("4. Desuscribir jugador");
        System.out.println("5. Detectar enemigo");
        System.out.println("0. Salir");
    }

    // ===== Strategy =====

    private void cambiarArma() {
        System.out.println("Armas disponibles:");
        armasDisponibles.forEach((numero, arma) ->
                System.out.println("  " + numero + ". " + arma.getClass().getSimpleName()));

        IEstrategiaAtaque armaElegida = armasDisponibles.get(leerEntero("Elige el arma: "));
        if (armaElegida == null) {
            System.out.println("Arma no valida.");
            return;
        }
        personaje.setArma(armaElegida);
    }

    // ===== Observer =====

    private void suscribirJugador() {
        String nombre = leerTexto("Nombre del jugador: ");
        Jugador jugador = new Jugador(nombre);
        torre.suscribir(jugador);
        jugadoresSuscritos.add(jugador);
        System.out.println(nombre + " se suscribio a la torre.");
    }

    private void desuscribirJugador() {
        if (jugadoresSuscritos.isEmpty()) {
            System.out.println("No hay jugadores suscritos.");
            return;
        }
        for (int i = 0; i < jugadoresSuscritos.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + jugadoresSuscritos.get(i).getNombre());
        }
        int indice = leerEntero("Elige el jugador: ") - 1;
        if (indice < 0 || indice >= jugadoresSuscritos.size()) {
            System.out.println("Jugador no valido.");
            return;
        }
        Jugador jugador = jugadoresSuscritos.remove(indice);
        torre.desuscribir(jugador);
        System.out.println(jugador.getNombre() + " ya no recibe alertas.");
    }

    private void detectarEnemigo() {
        torre.detectarEnemigo();
    }

    // ===== Lectura de consola =====

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = scanner.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un numero valido.");
            }
        }
    }

    private String leerTexto(String mensaje) {
        String texto;
        do {
            System.out.print(mensaje);
            texto = scanner.nextLine().trim();
        } while (texto.isEmpty());
        return texto;
    }
}