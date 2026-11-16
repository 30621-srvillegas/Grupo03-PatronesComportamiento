package observer;

public interface ISujeto {
    void suscribir(IObservador observador);
    void desuscribir(IObservador observador);
    void notificar();
}
