import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class Disposant {
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

    Disposant(int id, int playset, int seed,double bruit,double note,double moyenneNbrVoeux){
        this.bloc=(int)note;
        this.id=id;
        this.listeAccepte=new ArrayList<>();
        this.listeAttente=new ArrayList<>();
        this.listeRefus=new ArrayList<>();
        Random rand = new Random(seed);
        this.playset=playset;
        //this.nbrSouhait=Math.abs(rand.nextInt(1,playset+1));
        double moyenne =moyenneNbrVoeux;
        double ecartType = 1.5;
        double valeur = rand.nextGaussian() * ecartType + moyenne;
        // Arrondir la valeur à l'entier le plus proche entre 0 et 10 inclus
        this.nbrSouhait = Math.min(Math.max((int) Math.round(valeur), 0), 10);
        this.marie=-1;
        double aleatoire = rand.nextDouble();
        double y = note;
        double x = note+1.0;
        // Calculer la note aléatoire entre x et y
       this.note= aleatoire * (y - x) + x;
        this.bruit = bruit;
    }
    Disposant(int id, int playset, int seed,double bruit){
        this.id=id;
        this.listeAccepte=new ArrayList<>();
        this.listeAttente=new ArrayList<>();
        this.listeRefus=new ArrayList<>();
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
        this.bruit = bruit;
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
   public List<Integer> genererListeSouhait(Map<Integer,List<Proposant>> proposants,ArrayList<Proposant> proposantList)
   {
       int length=proposants.size();
        Set<Integer> formationSet = new HashSet<>();
        Random rand = new Random(seed);
       double moyenne = this.note;
       double ecartType = 1.5;
       // Arrondir la valeur à l'entier le plus proche entre 0 et 10 inclus
       while (formationSet.size() < this.nbrSouhait) {
           int valeur = (int) (rand.nextGaussian() * ecartType + moyenne); // un nombre aléatoire avec une moyenne de la note de l'étudiant
           System.out.println("id : "+this.id+" choix bloc :"+valeur);
           List<Proposant> blocI = new ArrayList<>(proposants.get(valeur));
           boolean done = true;
           while(done ) {
               int choixRandom = rand.nextInt(0, blocI.size());
               System.out.println(choixRandom + " dans " + valeur);
               if (!(formationSet.contains(blocI.get(choixRandom).getId()))) {
                   if(blocI.size()>0) {
                       blocI.remove(choixRandom);
                       done=true;
                   }else{
                       done = false;
                   }
                    // retire un nbr
               } else {
                   if (blocI.get(choixRandom).nouvelleDemande(this)) {
                       formationSet.add(blocI.get(choixRandom).getId());
                       done = false; // c fait
                   }
                 else{
                   if (proposants.get(valeur).size() == 0) {
                       done = false;
                   } else {
                       proposants.get(valeur).remove(choixRandom);
                       done = true; // c pas fait
                   }
               }
           }

           }
       }
       ArrayList<Proposant> formation = new ArrayList<>();
       List<Integer> tmp = formationSet.stream().toList();
        for(int i = 0 ; i<formationSet.size();i++)
        {
            formation.add(proposantList.get(tmp.get(i)));
        }
       Function<Proposant,Pair<Proposant,Double>> noteBruiter =  (proposant -> new Pair(proposant,proposant.getReputation()+rand.nextDouble(-this.bruit*length,this.bruit*length)));
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
