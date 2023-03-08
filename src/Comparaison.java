import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class Comparaison {
    protected int rand;
    boolean[][] listePreference;

    public Comparaison(int seed, ArrayList<Integer> listeElement) {
        Random rand = new Random(seed);
        listePreference = new boolean[listeElement.size()][listeElement.size()];
        for(int i =0 ; i < listeElement.size() ; i++)
        {
            for(int j = 0 ; j < listeElement.size()-1 ; j++)
            {
                boolean compare =rand.nextBoolean();
                listePreference[i][j]=compare;
                listePreference[j][i]=!compare;
            }
        }
    }
    public boolean compare(int individu1,int individu2) // return the best individu
    {
        if(individu2==-1)
        {
            return true;
        }
        return listePreference[individu1][individu2];
    }

    public int getRand() {
        return rand;
    }

    public void setRand(int rand) {
        this.rand = rand;
    }

    public boolean[][] getListePreference() {
        return listePreference;
    }

    public void setListePreference(boolean[][] listePreference) {
        this.listePreference = listePreference;
    }
}
