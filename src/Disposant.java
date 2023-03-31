import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class Disposant {
    protected Comparaison logic;
    protected int id;
    protected int nbrSouhait;
    protected int playset;
    protected List<Integer> listeSouhait;
    protected  ArrayList<Integer> listeChoixPossible;
    protected int seed;
    protected int marie;
    protected double note;
    Disposant(int id,int playset,int seed){
        this.id=id;
        Random rand = new Random(seed);
        this.playset=playset;
        this.nbrSouhait=Math.abs(rand.nextInt(1,playset+1));
       // this.nbrSouhait=playset;
        ArrayList<Integer> listeInteger=new ArrayList<>();
        this.seed=seed;
        for(int i =0;i<nbrSouhait;i++)
        {
            listeInteger.add(i);
        }
        this.listeChoixPossible=listeInteger;
        this.marie=-1;
        this.note = rand.nextGaussian(10,4);
    }
    public void clean()
    {
        this.marie=-1;
    }
   public List<Integer> genererListeSouhait(List<Proposant> proposants)
   {
       ArrayList<Proposant> formation = new ArrayList<>();
       Set<Integer> usedNumbers = new HashSet<>();
       Random rand = new Random(seed);
       while (formation.size() < this.nbrSouhait) {
           int number = rand.nextInt(playset-1+1);
           if (!usedNumbers.contains(number)) {
               formation.add(proposants.get(number));
               usedNumbers.add(number);
           }
       }
       Function<Proposant,Pair<Proposant,Double>> noteBruiter =  (proposant -> new Pair(proposant,proposant.getReputation()+rand.nextDouble()));
       Stream<Pair<Proposant, Double>> listeReputationBruiter = formation.stream().map(noteBruiter);
       this.listeSouhait= listeReputationBruiter.sorted( (a,b) -> (int)Math.signum(a.second() - b.second())).map(Pair::first).map(Proposant::getId).toList();
        return this.listeSouhait;
   }
    public ArrayList<Integer> genererListeSouhait( ) {
        ArrayList<Integer> numbers = new ArrayList<>();
        Set<Integer> usedNumbers = new HashSet<>();
        Random rand = new Random(seed);
        while (numbers.size() < this.nbrSouhait) {
            int number = rand.nextInt(playset-1+1);
            if (!usedNumbers.contains(number)) {
                numbers.add(number);
                usedNumbers.add(number);
            }
        }

        this.listeSouhait=numbers;
        return numbers;
    }
    public int reponse(int proposant, int marieActuel)
    {
        if(logic.compare(proposant, marieActuel))
        {
            return marieActuel;
        }
        return proposant;
    }
    public int reponse(int proposants) // return a free husband or -1 if none is free
    {
        if(proposants==marie)
        {
            System.out.println("j'avais raison c'était nécessaire :p"); // si ce print arrive c'est que j'avais raison :p
            return -1; // n'arrive jamais en situation normal, mais est nécessaire pour .prochainEtatStablePour(), je crois, pas sur, on verra
        } // finalement sa arrive jamais, jcrois, donc à supprimer
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

    public List<Integer> getListeSouhait() {
        return listeSouhait;
    }

    public void setListeSouhait(Integer[] listeSouhait) {
        ArrayList<Integer> tmp = new ArrayList<>();
        Collections.addAll(tmp,listeSouhait);
        this.listeSouhait = tmp;
        this.logic=new Comparaison(seed,this.listeSouhait);
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
    public int getIndexMarie()
    {
        return listeSouhait.indexOf(marie);
    }
    public void setMarie(int marie) {
        this.marie = marie;
    }
    public double getNote() {
        return note;
    }

    public void setNote(double note) {
        this.note = note;
    }
}
