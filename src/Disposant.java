import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Disposant {
    protected Comparaison logic;
    protected int id;
    protected int nbrSouhait;
    protected int nbrSouhaitMax;
    protected ArrayList<Integer> listeSouhait;
    protected  ArrayList<Integer> listeChoixPossible;
    protected int seed;
    protected int marie;
    Disposant(int id,int nbrSouhaitMax,int seed){
        this.id=id;
        Random rand = new Random(seed);
        this.nbrSouhaitMax=nbrSouhaitMax;
        this.nbrSouhait=Math.abs(rand.nextInt(1,nbrSouhaitMax+1));
        ArrayList<Integer> listeInteger=new ArrayList<>();
        this.seed=seed;
        for(int i =0;i<nbrSouhait;i++)
        {
            listeInteger.add(i);
        }
        this.listeChoixPossible=listeInteger;
        this.marie=-1;
    }
    public void clean()
    {
        this.marie=-1;
    }
    public ArrayList<Integer> genererListeSouhait( ) {
        ArrayList<Integer> numbers = new ArrayList<>();
        Set<Integer> usedNumbers = new HashSet<>();
        Random rand = new Random(seed);
        while (numbers.size() < this.nbrSouhait) {
            int number = rand.nextInt(nbrSouhaitMax-1+1);
            if (!usedNumbers.contains(number)) {
                numbers.add(number);
                usedNumbers.add(number);
            }
        }
        this.listeSouhait=numbers;
        return numbers;
    }
    public int reponse(int proposants) // return a free husband or -1 if none is free
    {

        if(logic.compare(proposants,marie))
        {
            //System.out.println("Femme : "+id+" accepte "+proposants +" rejette "+marie);
            return marie;
        }
       // System.out.println("Femme : "+id+" refuse "+proposants+ " rejette "+proposants);
        return proposants;
    }
    public void genererComparaison()
    {
        this.logic=new Comparaison(this.seed,this.listeSouhait);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Integer> getListeSouhait() {
        return listeSouhait;
    }

    public void setListeSouhait(ArrayList<Integer> listeSouhait) {
        this.listeSouhait = listeSouhait;
    }

    public ArrayList<Integer> getListeChoixPossible() {
        return listeChoixPossible;
    }

    public void setListeChoixPossible(ArrayList<Integer> listeChoixPossible) {
        this.listeChoixPossible = listeChoixPossible;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public Comparaison getLogic() {
        return logic;
    }

    public void setLogic(Comparaison logic) {
        this.logic = logic;
    }

    public int getNbrSouhait() {
        return nbrSouhait;
    }

    public void setNbrSouhait(int nbrSouhait) {
        this.nbrSouhait = nbrSouhait;
    }

    public int getMarie() {
       return marie;
    }

    public void setMarie(int marie) {
        this.marie = marie;
    }
}
