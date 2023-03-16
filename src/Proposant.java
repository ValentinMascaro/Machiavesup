import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Proposant {
    //protected Comparaison logic;
    protected int id;
    protected int nbrIndividu;
    protected ArrayList<Integer> listeSouhait; // ordonné pour les proposant
    protected  ArrayList<Integer> listeChoixPossible;
    protected int seed;

    protected int indiceProposition;
    protected boolean isMarie;
    public Proposant(int id, int nbrIndividu) {
        this.id = id;
        this.nbrIndividu = this.nbrIndividu;
        this.listeChoixPossible=new ArrayList<Integer>();
        this.indiceProposition=0;

    }
    public int getMarie()
    {
        return this.listeSouhait.get(indiceProposition);
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

    public ArrayList<Integer> getListeSouhait() {
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
}
