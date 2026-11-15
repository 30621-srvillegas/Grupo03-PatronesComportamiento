package strategy;

public class AtaqueHielo implements IEstrategiaAtaque {

    @Override
    public void atacar(String nombreAtacante) {
        System.out.println(nombreAtacante + " lanza una ASTA DE HIELO y congela al enemigo por 2 turnos.");
    }
}