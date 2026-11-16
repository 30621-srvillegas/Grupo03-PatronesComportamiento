package observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TorreDeVigilancia implements ISujeto {
    private final List<IObservador> observadores = new ArrayList<>();

    @Override
    public void suscribir(IObservador observador) {
        Objects.requireNonNull(observador, "El observador no puede ser null");
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    @Override
    public void desuscribir(IObservador observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificar() {
        for (IObservador o : new ArrayList<>(observadores)) {
            o.actualizar();
        }
    }

    public void detectarEnemigo() {
        System.out.println("Torre: enemigo detectado");
        notificar();
    }
}