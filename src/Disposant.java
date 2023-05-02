import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class Disposant {
    Random rand;
    List<Proposant> listeFormation;
    protected Comparaison logic;
    protected int id;
    protected int nbrSouhait;
    protected int playset;
    protected List<Integer> listeSouhait;
    protected List<Integer> listeAccepte;
    protected List<Integer> listeAttente;
    protected List<Integer> listeRefus;
    protected  ArrayList<Integer> listeChoixPossible;
    protected int seed;
    protected int marie;
    protected double note;
    protected double bruit;
    protected int bloc;
    private int nbrBloc;

    public int getPlayset() {
        return playset;
    }

    public List<Integer> getListeAccepte() {
        return listeAccepte;
    }

    public List<Integer> getListeAttente() {
        return listeAttente;
    }

    public List<Integer> getListeRefus() {
        return listeRefus;
    }
    public int generateRandomNumber(double mean) {
        //double mean = 8.5;
        double sd = 1.5; // grand écart-type
        double randomNumber = this.rand.nextGaussian() * sd + mean;
        randomNumber = Math.max(1, Math.min(10, randomNumber));
        return (int)Math.round(randomNumber);
    }
    Disposant(int id, int seed,double bruit,double note,double moyenneNbrVoeux,int nbrBloc){
        this.listeFormation = new ArrayList<>();
        this.nbrBloc=nbrBloc;
        this.bloc=(int)note;
        this.id=id;
        this.listeSouhait=new ArrayList<>();
        this.listeAccepte=new ArrayList<>();
        this.listeAttente=new ArrayList<>();
        this.listeRefus=new ArrayList<>();
        this.rand = new Random(seed);
        this.seed=seed;
        //this.nbrSouhait=Math.abs(rand.nextInt(1,playset+1));
        // Arrondir la valeur à l'entier le plus proche entre 0 et 10 inclus
        this.nbrSouhait = 10;//this.generateRandomNumber(moyenneNbrVoeux);
        this.marie=-1;
        double aleatoire = rand.nextDouble();
        double y = note;
        double x = note+1.0;
        // Calculer la note aléatoire entre x et y
       this.note= aleatoire * (y - x) + x;
      //  this.note = note+rand.nextDouble();
        this.bruit = bruit;
    }
    public boolean canAffect(Proposant proposant)
    {
        return this.listeSouhait.size() < this.nbrSouhait  && !this.listeSouhait.contains(proposant.getId()) ;

    }
    public void affectSouhait(Proposant proposant)
    {
        this.listeFormation.add(proposant);
        this.listeSouhait.add(proposant.getId());
        proposant.nouvelleDemande(this);
    }
    public void add_accepte(Integer formation)
    {
        if(this.listeAttente.contains(formation))
        {
            this.listeAttente.remove(formation);
        }
        if(this.listeAccepte.contains(formation))
        {return ;}
        this.listeAccepte.add(formation);
    }
    public void add_refus(Integer formation)
    {
        if (this.listeRefus.contains(formation)) {
            return;
        }
        this.listeRefus.add(formation);
    }
    public void add_attente(Integer formation, int position)
    {
        if(this.listeAttente.contains(formation))
        {
            return;
        }
        //this.listePositionListeAttente.add(position);
        this.listeAttente.add(formation);
    }
    public void clean()
    {
        this.marie=-1;
    }
    public int nombreAleatoireEntre(int min, int max, double moyenne,double ecartType) // x min , y max, z moyenne
    {
        double valeur = this.rand.nextGaussian() * ecartType + moyenne;
        // Arrondir la valeur à l'entier le plus proche entre 0 et 10 inclus
        int entier = Math.min(Math.max((int) Math.round(valeur), min), max);
        return entier;
    }
    public <K,V> K getRandomKey(Map<K,V> map) {
        if(map.size()==1)
        {
            return map.keySet().iterator().next();
        }
        K[] keys = map.keySet().toArray((K[]) new Object[map.size()]);
        return keys[this.rand.nextInt(keys.length)];
    }
   public void genererListeSouhait(Map<Integer,List<Proposant>> proposants,List<Proposant> proposantList,int demande)
   {
       int nbrsouhaitTmp = this.nbrSouhait;
       if(proposants.size()<nbrsouhaitTmp)
       {
           nbrsouhaitTmp=proposants.size();
       }
       Set<Integer> interdit = new HashSet<>();
       List<Proposant> formation = new ArrayList<>();
       Map<Integer,List<Proposant>> propCopy = new HashMap<>();
       for (Map.Entry<Integer, List<Proposant>> entry : proposants.entrySet()) {
           Integer key = entry.getKey();
           List<Proposant> value = new ArrayList<>(entry.getValue()); // on clone la liste
           propCopy.put(key, value);
       }
       this.listeSouhait=new ArrayList<>();
       while(this.listeSouhait.size()<nbrsouhaitTmp )
       {
           int numeroBloc = this.nombreAleatoireEntre(0, this.nbrBloc, this.note,0.5);
           while( propCopy.size()!=0 && (propCopy.get(numeroBloc)==null || propCopy.get(numeroBloc).size()==0) ) {
               propCopy.remove(numeroBloc);
               interdit.add(numeroBloc);
               while(interdit.contains(numeroBloc))
               {
                   if(propCopy.size()==0)
                   {
                       break; // dans l'eventualité où il n'est pas possible de tirer un souhait, on break.
                   }
                  numeroBloc=getRandomKey(propCopy);
               }
           }
           if(propCopy.size()==0)
           {
               break;
           }
           int randomNumber = this.rand.nextInt(demande);
           int sum=0;
           int numeroFormation=0;
           while(sum<randomNumber && numeroFormation<propCopy.get(numeroBloc).size())
           {
               sum+=propCopy.get(numeroBloc).get(numeroFormation).getDemande();
           }
           int nouveauNumeroDemande = propCopy.get(numeroBloc).get(numeroFormation).nouvelleDemande(this);
           this.listeSouhait.add(propCopy.get(numeroBloc).get(numeroFormation).getId());
           formation.add(proposantList.get(propCopy.get(numeroBloc).get(numeroFormation).getId()));
           demande-=propCopy.get(numeroBloc).get(numeroFormation).getDemande();
           if(nouveauNumeroDemande>=propCopy.get(numeroBloc).get(numeroFormation).getDemande())
           {
               proposants.get(numeroBloc).remove(propCopy.get(numeroBloc).get(numeroFormation));
           }
           propCopy.get(numeroBloc).remove(numeroFormation);
       }
       Function<Proposant,Pair<Proposant,Double>> noteBruiter =  (proposant -> new Pair(proposant,proposant.getReputation()+rand.nextDouble(-this.bruit*this.nbrBloc,this.bruit*this.nbrBloc)));
       Stream<Pair<Proposant, Double>> listeReputationBruiter = formation.stream().map(noteBruiter);
       this.listeSouhait= listeReputationBruiter.sorted( (a,b) -> (int)Math.signum(a.second() - b.second())).map(Pair::first).map(Proposant::getId).toList();
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
        Function<Proposant,Pair<Proposant,Double>> noteBruiter =  (proposant -> new Pair(proposant,proposant.getReputation()+rand.nextDouble(-this.bruit*this.nbrBloc,this.bruit*this.nbrBloc)));
        Stream<Pair<Proposant, Double>> listeReputationBruiter = this.listeFormation.stream().map(noteBruiter);
        this.listeSouhait= listeReputationBruiter.sorted( (a,b) -> (int)Math.signum(a.second() - b.second())).map(Pair::first).map(Proposant::getId).toList();
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
        if(listeAccepte.size()!=0)
        {
            return this.listeAccepte.get(0);
        }
        return -1;

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

    public void setListeSouhait(List<Integer> listeSouhait) {
        this.listeSouhait = listeSouhait;

    }

    public void setListeAccepte(List<Integer> listeAccepte) {
        this.listeAccepte = listeAccepte;
    }

    public void setListeAttente(List<Integer> listeAttente) {
        this.listeAttente = listeAttente;
    }

    public void setListeRefus(List<Integer> listeRefus) {
        this.listeRefus = listeRefus;
    }
}
