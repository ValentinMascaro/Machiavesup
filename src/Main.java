import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        int seed = 1;
        for(int nbrTest = 0 ; nbrTest<10;nbrTest++) { //[89, 56, 83, 22, 69, 105, 104, 92, 116, 73]
            double bruit = 0.05;
            for(int testBruit =0;testBruit<10;testBruit++) {
                System.out.println("Seed pour ce test : " + seed);
                double nbrdevoeuxmoyen = 8.5;
                int nbrbloc = 20;
                csvSimuBny csv = new csvSimuBny("cpgePtsi.csv", seed);
                csv.setNbrdemande();
                int playset = csv.getNbrFormation();
                csv.setFormation(nbrbloc);
                Map<Integer, List<Proposant>> proposants = csv.getFormation();
                List<Proposant> proposantList = csv.getFormationList();
                int totalDemande = 0;
                for (int i = 0; i < csv.getNbrdemande().size(); i++) {
                    totalDemande += csv.getNbrdemande().get(i);
                }
                List<Disposant> disposantList = init(seed, proposantList, totalDemande, 0, nbrbloc, bruit, nbrbloc / 2, nbrdevoeuxmoyen);
                /*int sommeD = 0;
                for (Proposant p : proposantList) {
                    sommeD += p.nbrDemandeRecu;
                }
                double moySOuhait = 0.0;
                double moyObtenu = 0.0;
                for (Disposant d : disposantList) {
                    moySOuhait += d.getNbrSouhait();
                    moyObtenu += d.getListeSouhait().size();
                }
                // printListeFormationByReputation(proposants,true);
                System.out.println("Moyenne souhaité " + moySOuhait / disposantList.size());
                System.out.println("Moyenne obtenu " + moyObtenu / disposantList.size());
                System.out.println("Total demande reçu :" + sommeD + " / " + totalDemande);*/
                Parcoursup(15, disposantList, proposantList);
                //System.out.println(disposantList.get(500).getListeSouhait());
                System.out.println("Seed :" + seed+" Bruit : "+bruit);
                bruit+=0.1;
            }
            seed++;
        }
        System.out.println("fin");
    }

    private static void printListeFormationByReputation(Map<Integer, List<Proposant>> proposants,boolean allOrNo) { // allOrNo =true -> affiche toute les formations, en rouge les non complete , false affiche uniquement les non remplies
        for (Map.Entry<Integer, List<Proposant>> entry : proposants.entrySet()) {
            List<Proposant> list = entry.getValue();
            for(Proposant p : list)
            {
                List<Integer> souhait = p.getListeSouhait();
                List<Integer> accepte = p.getListeAcceptation();
                List<Integer> attente = p.getListeAttente();
                List<Integer> refus = p.getListeRefus();
                if(allOrNo) {
                    if (p.nbrDemandeRecu < p.getDemande()) {
                        System.out.print("\033[31m");
                    }
                    if (souhait != null) {
                        System.out.print("Formation : " + p.getId() + " Reputation : " + p.getReputation() + " | " + p.getNbrIndividu() + " places et " + p.nbrDemandeRecu + " demandes /" + p.demande);
                    } else {
                        System.out.print("Formation : " + p.getId() + " Reputation : " + p.getReputation() + " | " + p.getNbrIndividu() + " places et " + p.nbrDemandeRecu + " demandes /" + p.demande);
                    }
                    System.out.println(" Souhait : " + souhait + " " + accepte + "\033[0m");
                }
                else{if(p.nbrDemandeRecu<p.getDemande()) {
                    System.out.print("\033[31m");
                    if (souhait != null) {
                        System.out.print("Formation : " + p.getId() + " Reputation : " + p.getReputation() + " | " + p.getNbrIndividu() + " places et " + p.nbrDemandeRecu + " demandes /" + p.demande);
                    } else {
                        System.out.print("Formation : " + p.getId() + " Reputation : " + p.getReputation() + " | " + p.getNbrIndividu() + " places et " + p.nbrDemandeRecu + " demandes /" + p.demande);
                    }
                    System.out.println(" Souhait : " + souhait + " " + accepte + "\033[0m");
                }
                }

            }
        }
    }


    public static int searchCycle(List<Disposant> disposants,List<Proposant> proposants){
        int nbrCycle=0;
        for (int j = 0; j < disposants.size(); j++) {
            if (disposants.get(j).getMarie() != -1 && disposants.get(j).getListeSouhait().indexOf(disposants.get(j).getMarie()) != 0) {
                //System.out.println(j);
                //   System.out.println("Que se passe-t-il si " + j + " libere " + (char) (disposants.get(j).getMarie() + 'A') + " ?");
                Couple dispoPropo = prochainEtat(disposants, proposants, j);
                if (dispoPropo.getDisposant() == j) {
                    nbrCycle++;
                    //System.out.println(seed);
                   // listSeed.add(seed);
                    //System.out.println("/!\\ " + j + " pourrai obtenir " + (char) (dispoPropo.getProposant() + 'A') + " en liberant " + (char) (disposants.get(j).getMarie() + 'A'));
                }
            }
        }
        return nbrCycle;
    }
    public static Couple prochainEtat(List<Disposant> disposants,List<Proposant> proposants,int disposantCible)
    {
       //ArrayList<Couple> listeMariage = new ArrayList<>();
        Map<Integer,Integer> listeMariage = new HashMap<Integer,Integer>(disposants.size());
      /*  for(int i=0;i<disposants.size();i++){

            listeMariage.add(i,new Couple(i,disposants.get(i).getMarie())); // un hash / dico / map
        }*/
        int MarieLibere = disposants.get(disposantCible).getMarie();
        Couple tmp = libereProposant(disposants,proposants,MarieLibere,listeMariage); // je libere un marie
        if(tmp.getDisposant()==-1 || tmp.getProposant()==-1){
            return tmp; // pas d'amélioration possible
        }

        int stop=0;
        while(tmp.getDisposant()!=-1 && tmp.getDisposant()!=disposantCible)
        {
           listeMariage.put(tmp.getDisposant(),MarieLibere);
         // listeMariage.get(tmp.getDisposant()).setProposant(MarieLibere);
            if(tmp.getProposant()==-1)
            {
                return tmp;
            }
            MarieLibere= tmp.getProposant();
           //System.out.println(tmp.getDisposant() + " liberera "+(char) (tmp.getProposant()+'A'));
            tmp = libereProposant(disposants,proposants,tmp.getProposant(),listeMariage); // libere un marie et maj les mariage

            stop++;
            if(stop>200)
            {
                System.out.println("--- boucle infini---");
                return tmp;
            }
        }
        return new Couple(tmp.getDisposant(),MarieLibere);
    }
    public static Couple libereProposant(List<Disposant> disposants,List<Proposant> proposants,int libere, Map<Integer,Integer> mariagePossible) // return le prochain marie liberer par quel disposant
    {
        Proposant proposantLibere = proposants.get(libere);
        int prochainDisposant=proposantLibere.getProchainListeAttente();
        if(prochainDisposant==-1)
        {
            return new Couple(-1,libere);
        }
        int etape=0;
        int prochainLiberer=disposants.get(prochainDisposant).reponse(libere,mariagePossible.getOrDefault(prochainDisposant,disposants.get(prochainDisposant).getMarie()));
      //  int prochainLiberer=disposants.get(prochainDisposant).reponse(libere,mariagePossible.get(prochainDisposant).getProposant());

          // System.out.println("Prochaine proposition de "+(char)(libere+'A')+" : "+prochainDisposant);
        while(prochainLiberer==libere && prochainDisposant!=-1)
        {
           // System.out.println(prochainDisposant+" refuse");
            etape++;
            prochainDisposant=proposantLibere.getProchainListeAttente(etape);
            //System.out.println("Prochaine proposition de "+(char)(libere+'A')+" : "+prochainDisposant);
            if(prochainDisposant==-1)
            {
                return new Couple(-1,libere);
            }
            prochainLiberer=disposants.get(prochainDisposant).reponse(libere,mariagePossible.getOrDefault(prochainDisposant,disposants.get(prochainDisposant).getMarie()));
           // prochainLiberer=disposants.get(prochainDisposant).reponse(libere,mariagePossible.get(prochainDisposant).getProposant());
        }

        return new Couple(prochainDisposant,prochainLiberer); // return l'individu qui prendrai le proposant, et le prochain proposant liberer
    }


    public static List<Disposant> init(int seed,List<Proposant> proposantList,int totalDemande,int noteMin,int noteMax,double bruit,double noteMoyenne, double moyenneNbrVoeux)
    {
        List<Disposant> disposants = new ArrayList<>();
        int nbDemande=0;
        int id=0;
        Random rand = new Random(seed);
        while(nbDemande<totalDemande*1.05)
        {
            Disposant d = new Disposant(id,seed,bruit,noteMoyenne,moyenneNbrVoeux,noteMax);
            disposants.add(d);
            nbDemande+=d.getNbrSouhait();
            seed++;
            id++;
        }
        disposants.stream().sorted((a,b)-> (int)Math.signum(a.getNote()-b.getNote()));
        List<Disposant> dispoCopy = new ArrayList<>(disposants);


        for(int i=0;i<proposantList.size();i++)
        {
            Proposant p = proposantList.get(i);
            //System.out.println("I "+i);
            while(p.getNbrDemandeRecu()<p.getDemande())
            {

                //System.out.println("Id :"+p.getId()+" Nb Demande recu :"+p.getNbrDemandeRecu());
                double choix =  convertToRange(nombreAleatoireEntre(rand,noteMin,noteMax,p.getReputation(),1.5),noteMin,noteMax);
                //        System.out.println("Choix :"+choix);
                int index=(int)Math.round(disposants.size()*choix);
                int decalage=1;
                int signe=-1;
                while(index>=disposants.size() || index<0 || !disposants.get(index).canAffect(p))
                { // 0 1 2 3 4 5 6
                  index+=decalage*signe;
                  decalage++;
                  signe*=-1;
                }
                disposants.get(index).affectSouhait(p);
                if(disposants.get(index).getNbrSouhait()==disposants.get(index).getListeSouhait().size())
                {
                    disposants.remove(disposants.get(index));
                }
            }
        }
        for(int i = 0 ; i<proposantList.size();i++)
        {
            proposantList.get(i).generateAllInnerListe(seed);
            seed++;
        }
        for(Proposant p : proposantList)
        {
            p.generateAllInnerListe(bruit);
        }
        for(Disposant d : dispoCopy)
        {
            d.genererComparaison();
        }
        return dispoCopy;
    }
    public static double convertToRange(double value, double min, double max) {
        // Vérifier que la valeur se trouve dans la plage spécifiée
        if (value < min || value > max) {
            throw new IllegalArgumentException("La valeur doit être comprise entre " + min + " et " + max);
        }

        // Calculer la valeur normalisée
        double range = max - min;
        double normalizedValue = (value - min) / range;

        return normalizedValue;
    }

    public static double nombreAleatoireEntre(Random rand,int min, int max, double moyenne,double ecartType) // x min , y max, z moyenne
    {
        double valeur =-1.0;
        do {
            valeur = rand.nextGaussian() * ecartType + moyenne;
        }while(valeur>max || valeur<min);
        // Arrondir la valeur à l'entier le plus proche entre 0 et 10 inclus
        return valeur;
    }
    public static void init(List<Disposant> disposants,Map<Integer,List<Proposant>> proposants,int seed,List<Proposant> proposantList,int totalDemande)
    {
        for(int i = 0 ; i<disposants.size();i++)
        {
            disposants.get(i).genererListeSouhait(proposants,proposantList,totalDemande) ;
            disposants.get(i).genererComparaison();
        }
        for(int i = 0 ; i<proposantList.size();i++)
        {
            proposantList.get(i).generateAllInnerListe(seed);
            seed++;
        }
    }

    public static void printListe(List<Proposant> proposant,List<Disposant> disposant)
    {
        System.out.println("Disposant");
        for(Disposant e : disposant)
        {
            List<Integer> souhait = e.getListeSouhait();
            List<Integer> accepte = e.getListeAccepte();
            List<Integer> attente = e.getListeAttente();
            List<Integer> refus = e.getListeRefus();
            System.out.println("Etudiant : "+e.getId());
            System.out.println("Souhait : "+souhait.stream().map(f->(char)(f+'A')).toList());
            System.out.println("Accepte : "+accepte.stream().map(f->(char)(f+'A')).toList());
            System.out.println("Attente : "+attente.stream().map(f->(char)(f+'A')).toList());
            System.out.println("Refus : "+refus.stream().map(f->(char)(f+'A')).toList());
        }
        System.out.println("Proposant");
        for(Proposant p : proposant)
        {
            List<Integer> souhait = p.getListeSouhait();
            List<Integer> accepte = p.getListeAcceptation();
            List<Integer> attente = p.getListeAttente();
            List<Integer> refus = p.getListeRefus();
            System.out.println("Formation : "+(char)('A'+p.getId()));
            System.out.println("Souhait : "+souhait);
            System.out.println("Accepte "+accepte.size()+" / "+p.getNbrIndividu()+" : "+accepte);
            System.out.println("Attente : "+attente);
            System.out.println("Refus : "+refus);
        }
    }
    public static void printListeFormation(List<Proposant> proposant)
    {
        System.out.println("Proposant");
        for(Proposant p : proposant)
        {
            List<Integer> souhait = p.getListeSouhait();
            List<Integer> accepte = p.getListeAcceptation();
            List<Integer> attente = p.getListeAttente();
            List<Integer> refus = p.getListeRefus();
            System.out.println("Formation : "+(char)('A'+p.getId()));
            System.out.println("Souhait : "+souhait);
            System.out.println("Accepte "+accepte.size()+" / "+p.getNbrIndividu()+" : "+accepte);
            System.out.println("Attente : "+attente);
            System.out.println("Refus : "+refus);
        }
    }
    public static void printListeMini(List<Proposant> proposant,List<Disposant> disposant)
    {
        System.out.println("Disposant");
        for(Disposant e : disposant)
        {
            List<Integer> souhait = e.getListeSouhait();
            List<Integer> accepte = e.getListeAccepte();
            List<Integer> attente = e.getListeAttente();
            List<Integer> refus = e.getListeRefus();
            System.out.print("Etudiant : "+e.getId()+ " "+ (char)('A'+e.getMarie()));
            System.out.println(" Souhait : "+souhait.stream().map(f->(char)(f+'A')).toList());
        }
        System.out.println("Proposant");
        for(Proposant p : proposant)
        {
            List<Integer> souhait = p.getListeSouhait();
            List<Integer> accepte = p.getListeAcceptation();
            List<Integer> attente = p.getListeAttente();
            List<Integer> refus = p.getListeRefus();
            System.out.print("Formation : "+(char)('A'+p.getId())+" "+ p.getNbrIndividu()+" places et "+souhait.size()+" demandes");
            System.out.println(" Souhait : "+souhait+ " "+ accepte );
        }
    }
    public static void printListeMiniInt(List<Proposant> proposant,List<Disposant> disposant)
    {
        System.out.println("Disposant");
        /*for(Disposant e : disposant)
        {
            List<Integer> souhait = e.getListeSouhait();
            List<Integer> accepte = e.getListeAccepte();
            List<Integer> attente = e.getListeAttente();
            List<Integer> refus = e.getListeRefus();
            System.out.print("Etudiant : "+e.getId()+ " "+ e.getMarie());
            System.out.println(" Souhait : "+souhait);
        }*/
        System.out.println("Proposant");
        for(Proposant p : proposant)
        {
            List<Integer> souhait = p.getListeSouhait();
            List<Integer> accepte = p.getListeAcceptation();
            List<Integer> attente = p.getListeAttente();
            List<Integer> refus = p.getListeRefus();
            if(souhait!=null) {
                System.out.print("Formation : " + p.getId() + " Reputation : "+p.getReputation()+" | " + p.getNbrIndividu() + " places et " + p.nbrDemandeRecu + " demandes /" + p.demande);
            }else{
                System.out.print("Formation : " + p.getId() + " Reputation : "+p.getReputation()+" | " + p.getNbrIndividu() + " places et " + p.nbrDemandeRecu + " demandes /" + p.demande);
            }
            System.out.println(" Souhait : "+souhait+ " "+ accepte );
        }
    }
    public static void Appel(List<Proposant> proposants,List<Disposant> disposants)
    {

        for(int i = 0 ; i < proposants.size();i++)
        {
            Proposant formationI = proposants.get(i);
            List<Integer> listAccepte = formationI.getListeAcceptation();
            List<Integer> listAttente = formationI.getListeAttente();
            List<Integer> listRefus = formationI.getListeRefus();
            for(int j =0 ; j < listAccepte.size();j++)
            {
                disposants.get(listAccepte.get(j)).add_accepte(i);
            }
            for(int j =0 ; j < listAttente.size();j++)
            {
                disposants.get(listAttente.get(j)).add_attente(i,j);
            }
            for (int j = 0; j < listRefus.size(); j++) {
                disposants.get(listRefus.get(j)).add_refus(i);
            }
        }
    }
    public static void Reponse(Strategie strategie,List<Proposant> proposants,List<Disposant> disposants)
    {
        applicationStrategie(strategie,disposants);
        for(Disposant e : disposants)
        {
            List<Integer> listeRefus = e.getListeRefus();
            for(int j = 0 ; j < listeRefus.size();j++)
            {
                proposants.get(listeRefus.get(j)).addEtudiantRefus(e.getId());
            }
        }
    }
    public static void applicationStrategie(Strategie strategie, List<Disposant> etudiants)
    {
        //
        for(Disposant e : etudiants) // tout les etudiant
        {
            List<Integer> choixAttente = strategie.choixParmiAttente(e);
            List<Integer> formationRefuse = new ArrayList<>(e.getListeRefus());
            List<Integer> listeAttenteAvantStrat = new ArrayList<>(e.getListeAttente());

            //  Etudiant.get(i).setListePositionListeAttente(listePositionAttente);
            listeAttenteAvantStrat.removeAll(choixAttente); // fait la différence entre les attente avant et en attente après
            formationRefuse.addAll(listeAttenteAvantStrat); // les individu qui ne sont plus en attente sont refusé
            e.setListeRefus(formationRefuse);
            e.setListeAttente(choixAttente);
            ///// acceptation
            Integer formationAccepte = strategie.choixParmiAcceptation(e);
            ArrayList<Integer> formatioAccepteAvantStrat = new ArrayList<>(e.getListeAccepte());
            if(formationAccepte!=null)
            {
                formatioAccepteAvantStrat.remove(formationAccepte);
                formationRefuse.addAll(formatioAccepteAvantStrat);
                e.setListeRefus(formationRefuse);
                ArrayList<Integer> retour = new ArrayList<>();
                retour.add(formationAccepte);
                e.setListeAccepte(retour);
            }
            else
            {
                formationRefuse.addAll(formatioAccepteAvantStrat);
                e.setListeRefus(formationRefuse);
                e.setListeAccepte(new ArrayList<Integer>());
            }
        }
    }
    public static void Parcoursup(int nbrJour,List<Disposant> disposantsList,List<Proposant> proposantList)
    {
        Strategie strategie = new StrategieDefault();
       for(int jour = 0; jour < nbrJour ; jour++)
       {
           System.out.println("Jour :"+jour);
          // System.out.println("Jour "+jour);
        //   System.out.println("Jour : "+jour+" / "+nbrJour);
         //  System.out.println("---------------------------");
         //  printListe(proposants,disposants);
           Appel(proposantList,disposantsList);
         //  printListe(proposants,disposants);
           Reponse(strategie,proposantList,disposantsList);
           System.out.println(searchCycle(disposantsList,proposantList)+" Cycle trouvé");
         //  printListe(proposants,disposants);
       }
    }
}
