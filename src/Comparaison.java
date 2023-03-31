import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Comparaison {
    protected int rand;

    protected List<Integer> listePreference;

    public Comparaison(int seed, List<Integer> listeElement) {
        Random rand = new Random(seed);
        this.listePreference = listeElement;
    }
    public boolean compare(int individu1,int individu2) // return true si individu1 meilleur
    {
        if(listePreference.indexOf(individu1)==-1)
        {
            return false;
        }
        if(individu2==-1)
        {
            return true;
        }
        // System.out.println(listePreference);
        //   System.out.println(listePreference.indexOf(individu1)+" "+listePreference.indexOf(individu2));
        return listePreference.indexOf(individu1)==Math.min(listePreference.indexOf(individu1),listePreference.indexOf(individu2));
    }
    public int getRand() {
        return rand;
    }
    public void setRand(int rand) {
        this.rand = rand;

    }
}
