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
    protected int nbrIndividuAttente;
    protected List<Integer> listeSouhait; // ordonné pour les proposant
    protected  ArrayList<Integer> listeChoixPossible;
    protected ArrayList<Disposant> listeDossier;
    protected List<Integer> listeAcceptation;
    protected List<Integer> listeRefus;
    protected List<Integer> listeAttente;
    protected int seed;
    protected double reputation;

    protected int indiceProposition;
    protected boolean isMarie;
    protected int demande;
    protected int nbrDemandeRecu;
    private double nbrBloc;
    private Random rand;
    private int nbrPropRefus;

    public Proposant(int id, int seed, int demande, double note, int capacite,double nbrBloc) {
        this.nbrPropRefus=capacite;
        this.nbrBloc=nbrBloc;
        this.id = id;
        this.listeChoixPossible=new ArrayList<Integer>();
        this.indiceProposition=0;
        this.listeDossier=new ArrayList<>();
        this.rand = new Random(seed);
        this.reputation=note;//rand.nextGaussian();
        this.nbrIndividu = capacite ;//rand.nextInt(capaciteMin,capaciteMax);
        this.nbrIndividuAttente=1000;
        this.demande=demande;
        this.nbrDemandeRecu=0;
    }
    public int getDemande() {
        return demande;
    }
    public int nouvelleDemande(Disposant etudiant)
    {
        //return true; // test
       if(nbrDemandeRecu+1>demande)
        {

            return -1;
        }
        if(listeDossier.contains(etudiant))
        {

            return -1;
        }
        this.nbrDemandeRecu++;
        this.addDossierIndividu(etudiant);
        return this.nbrDemandeRecu;
    }
    public void generateAllInnerListe(double bruit)
    {
        this.generateListePreference(bruit);
        this.generateListeAcceptation();
        this.generateListeAttente();
        this.generateListeRefus(); // pas encore implementé
    }
    public void addEtudiantRefus(Integer e){
        if (listeAcceptation.contains(e))
        {

            this.listeAcceptation.remove(e);
            if(!this.listeAttente.isEmpty()) {
                Integer etu = this.listeAttente.remove(0);
                this.listeAcceptation.add(etu);
            }
            this.nbrPropRefus++;
        }
        else if(listeAttente.contains(e))
        {
            this.listeAttente.remove(e);
        }
        if(!this.listeRefus.contains(e)) {
            this.listeRefus.add(e);
        }
    }
    public void generateListeAcceptation()
    {
        List<Integer> accepte=new ArrayList<>();
        if(this.listeSouhait.size()==0)
        {
            this.listeAcceptation=accepte;
            //return null;
        }
        int max_capacite_liste;
        if(this.nbrIndividu>listeSouhait.size())
        {
            max_capacite_liste=listeSouhait.size();
        }else
        {
            max_capacite_liste=this.nbrIndividu;
        }
        for(int i = 0;i<max_capacite_liste;i++)
        {
            accepte.add(this.listeSouhait.get(i));
        }
        this.listeAcceptation=accepte;
       // return this.listeAcceptation;
    }
    public void generateListeAttente() {
        ArrayList<Integer> attente = new ArrayList<>();
        if (listeSouhait.size() == 0) {
            this.listeAttente = attente;
            //return null;
        }
        for (int i = this.nbrIndividu; i < Math.min(this.listeSouhait.size(), this.nbrIndividu + this.nbrIndividuAttente); i++) {
            attente.add(this.listeSouhait.get(i));
        }
        this.listeAttente = attente;
        //this.capacite_attente-=this.listeAttente.size();
        //return this.listeAttente;
    }
    public void generateListeRefus() { // pas implementer
        if(true)
        {
            this.listeRefus=new ArrayList<>();
        }
        List<Integer> refus = new ArrayList<>();
        if (listeSouhait.size() == 0) {
            this.listeRefus = refus;
           // return null;
        }
        for (int i = this.nbrIndividu + this.nbrIndividuAttente; i < this.listeSouhait.size(); i++) {
            refus.add(this.listeSouhait.get(i));
        }

        this.listeRefus = refus;
        //return this.listeRefus;
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
    public void generateListePreference(double bruit) {

        Function<Disposant,Pair<Disposant,Double>> noteBruiter =  (disposant -> new Pair(disposant,disposant.getNote()+this.rand.nextDouble(-bruit*this.nbrBloc,bruit*nbrBloc)));
        Stream<Pair<Disposant, Double>> listeNoteBruiter = listeDossier.stream().map(noteBruiter);
        this.listeSouhait= listeNoteBruiter.sorted( (a,b) -> (int)Math.signum(a.second() - b.second())).map(Pair::first).map(Disposant::getId).toList();

    }
    public void addDossierIndividu(Disposant disposant)
    {
        this.listeDossier.add(disposant);
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
        this.generateListeAcceptation();
        this.generateListeAttente();
        this.generateListeRefus();
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
        if(this.listeAttente.size()!=0)
        {
            return this.listeAttente.get(0);
        }
        return -1;
    }
    public int getProchainListeAttente(int indiceDecale){
        if(this.listeAttente.size()>indiceDecale)
        {
            return this.listeAttente.get(indiceDecale);
        }
        return -1;
    }

    public double getReputation() {
        return reputation;
    }

    public int getNbrIndividuAttente() {
        return nbrIndividuAttente;
    }

    public ArrayList<Disposant> getListeDossier() {
        return listeDossier;
    }

    public List<Integer> getListeAcceptation() {
        return listeAcceptation;
    }

    public List<Integer> getListeRefus() {
        return listeRefus;
    }

    public List<Integer> getListeAttente() {
        return listeAttente;
    }

    public int getNbrDemandeRecu() {
        return nbrDemandeRecu;
    }

    public int getNbrPropRefus() {
        return nbrPropRefus;
    }
}
