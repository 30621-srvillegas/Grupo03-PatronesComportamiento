package strategy;

import java.util.Objects;

/**
 * Contexto del patron Strategy.
 * Recibe su estrategia de ataque por inyeccion de dependencias y puede
 * cambiarla en tiempo de ejecucion sin modificar esta clase (OCP).
 */

public class Personaje {

    private final String nombre;
    private IEstrategiaAtaque arma;

    public Personaje(String nombre, IEstrategiaAtaque armaInicial) {
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.arma = Objects.requireNonNull(armaInicial, "El arma no puede ser nula");
    }

    public void setArma(IEstrategiaAtaque nuevaArma) {
        this.arma = Objects.requireNonNull(nuevaArma, "El arma no puede ser nula");
        System.out.println(">> " + nombre + " cambia de arma a: " + nuevaArma.getClass().getSimpleName());
    }

    public void atacar() {
        arma.atacar(nombre);
    }

    public String getNombre() {
        return nombre;
    }
}