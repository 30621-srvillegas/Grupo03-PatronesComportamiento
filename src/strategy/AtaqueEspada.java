package strategy;

public class AtaqueEspada implements IEstrategiaAtaque {

    @Override
    public void atacar(String nombreAtacante) {
        System.out.println(nombreAtacante + " ataca con su ESPADA e inflige 20 de daño fisico.");
    }
}