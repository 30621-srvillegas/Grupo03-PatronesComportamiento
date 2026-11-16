package observer;

public class Jugador implements IObservador {
    private final String nombre;

    public Jugador(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public void actualizar() {
        activarEscudo();
    }

    public void activarEscudo() {
        System.out.println(nombre + ": escudo activado");
    }
}