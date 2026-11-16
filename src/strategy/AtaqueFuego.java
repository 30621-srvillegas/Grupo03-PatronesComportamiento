package strategy;

public class AtaqueFuego implements IEstrategiaAtaque {

    @Override
    public void atacar(String nombreAtacante) {
        System.out.println(nombreAtacante + " lanza una BOLA DE FUEGO e inflige 30 damage por quemadura.");
    }
}