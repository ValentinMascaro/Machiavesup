import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class Proposant {
    //protected Comparaison logic;
    protected int id;
    protected int nbrIndividu;
    protected List<Integer> listeSouhait; // ordonné pour les proposant
    protected  ArrayList<Integer> listeChoixPossible;
    protected ArrayList<Disposant> listeDossier;

    protected int seed;
    protected double reputation;

    protected int indiceProposition;
    protected boolean isMarie;
    public Proposant(int id,int seed) {
        this.id = id;

        this.listeChoixPossible=new ArrayList<Integer>();
        this.indiceProposition=0;
        this.listeDossier=new ArrayList<>();
        Random rand = new Random(seed);
        this.reputation=rand.nextGaussian();
        this.nbrIndividu = rand.nextInt(5,30);

    }
    public int getMarie()
    {
        if(indiceProposition!=listeSouhait.size()) {
            return this.listeSouhait.get(indiceProposition);
        }
        return -1;
    }
    public void clean()
    {
        this.isMarie=false;
        this.indiceProposition=0;
    }
    public int prochaineProposition(){
        if(indiceProposition>=listeSouhait.size())
        {
            this.isMarie=true;
            return -1;

        }
        return listeSouhait.get(this.indiceProposition);
    }
    public void setMariage() // on est marié a l'indice proposition jusqu'a preuve du contraire
    {
        isMarie=true;
    }
    public void refus()
    {
        isMarie=false;
        this.indiceProposition++;
    }
    public void generateListePreference(int seed) {
        Random rand = new Random(seed);
        Function<Disposant,Pair<Disposant,Double>> noteBruiter =  (disposant -> new Pair(disposant,disposant.getNote()+rand.nextDouble()));
        Stream<Pair<Disposant, Double>> listeNoteBruiter = listeDossier.stream().map(noteBruiter);
        this.listeSouhait= listeNoteBruiter.sorted( (a,b) -> (int)Math.signum(a.second() - b.second())).map(Pair::first).map(Disposant::getId).toList();

    }
    public void addDossierIndividu(Disposant disposant)
    {
        this.listeDossier.add(disposant);
    }
    public void generateListePreference(int seed, int deprecated) {
        ArrayList<Integer> pref = new ArrayList<>(this.listeChoixPossible);
        Random rand = new Random(seed);
        Collections.shuffle(pref, rand);
        this.listeSouhait = pref;
    }
    public void addIndividu(int individuToAdd)
    {
        this.listeChoixPossible.add(individuToAdd);
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNbrIndividu() {
        return nbrIndividu;
    }

    public void setNbrIndividu(int nbrIndividu) {
        this.nbrIndividu = nbrIndividu;
    }

    public List<Integer> getListeSouhait() {
        return listeSouhait;
    }

    public void setListeSouhait(Integer[] liste) {
        ArrayList<Integer> tmp = new ArrayList<>();
        Collections.addAll(tmp,liste);
        this.listeSouhait = tmp;

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

    public boolean isMarie() {
        return isMarie;
    }

    public int getIndiceProposition() {
        return indiceProposition;
    }
    public int getProchainListeAttente(){
        if(indiceProposition+1>=listeSouhait.size())
        {
            return -1;
        }
        return listeSouhait.get(indiceProposition+1);
    }
    public int getProchainListeAttente(int indiceDecale){
        if(indiceProposition+indiceDecale+1>=listeSouhait.size())
        {
            return -1;
        }
        return listeSouhait.get(indiceProposition+indiceDecale+1);
    }

    public double getReputation() {
        return reputation;
    }
}
