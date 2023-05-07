import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        List<Pair<Integer, Integer>> cycle;
        double bruit = 0.005;

        double nbrdevoeuxmoyen = 8.5;
        int nbrbloc = 20;
        for(int testBruit = 0;testBruit<10;testBruit++) {
            int seed = 1;
            double nbrTest = 0;
            double preAvg=Double.MAX_VALUE;
            double avg=0.0;
            double epsilon = 0.001;
            double nbCycle = 0;
            int nbEtudiantTraverse = 0;
            int nbEtudiantAmeliorer = 0;
           do {
                csvSimuBny csv = new csvSimuBny("cpgeECGE.csv", seed);
                csv.setNbrdemande();
                //int playset = csv.getNbrFormation();
                csv.setFormation(nbrbloc);
                //Map<Integer, List<Proposant>> proposants = csv.getFormation();
                List<Proposant> proposantList = csv.getFormationList();
                int totalDemande = 0;
                for (int i = 0; i < csv.getNbrdemande().size(); i++) {
                    totalDemande += csv.getNbrdemande().get(i);
                }
                List<Disposant> disposantList = init(seed, proposantList, totalDemande, 0, proposantList.size(), bruit, nbrbloc/2.0, nbrdevoeuxmoyen);
           // System.out.println("fin init");
                Parcoursup(15, disposantList, proposantList);
           // System.out.println("Nombre demande :"+totalDemande+" Nombre étudiant :"+disposantList.size()+" Nombre formation :"+proposantList.size());
                cycle = searchCycle(disposantList, proposantList);
                if (cycle.size() > 0) {
                    nbCycle += cycle.size();
                    for (Pair<Integer, Integer> integer : cycle) {
                        nbEtudiantTraverse += integer.first();
                        nbEtudiantAmeliorer += integer.second();
                    }
                }
                preAvg = avg;
                avg = nbCycle / ++nbrTest;
                seed++;
                // System.out.println(avg+" "+preAvg+" "+nbrTest);
            proposantList=proposantList.stream().sorted((a,b)-> Double.compare(a.getReputation(),b.getReputation())).collect(Collectors.toList());
               /* for(Proposant p : proposantList.subList(0,20))
                {
                    System.out.println("Formation : "+ (p.getId() )+" Note :"+p.getReputation() +" nbr étudiant appelé "+ p.getNbrPropRefus()+" Capacité :"+p.nbrIndividu+p.getListeSouhait().subList(0,10)+" "+p.getListeAcceptation()); // id +2 pour le excel
                }*/
         //   disposantList=disposantList.stream().sorted((a,b)-> Double.compare(a.getNote(),b.getNote())).collect(Collectors.toList());
              /*  for(Disposant d : disposantList.subList(0,100))
                {
                    System.out.println(d.getId()+" "+d.getNote()+d.getListeSouhait()+" "+d.getListeAccepte());

                }*/
            } while (Math.abs(preAvg - avg) > epsilon || nbrTest < 100);
            System.out.println("Bruit :"+bruit);
            System.out.println("Nbr test : " + nbrTest);
            System.out.println("Total cycle : " + nbCycle);
            System.out.println("Nombre de cycle moyen :" + nbCycle / nbrTest + " par seed");
            System.out.println("Nombre d'étudiant traversé en moyenne : " + nbEtudiantTraverse / nbCycle);
            System.out.println("Nombre d'étudiant amélioré en moyenne : " + nbEtudiantAmeliorer / nbCycle);
            System.out.println("------");

        } // for
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


    public static List<Pair<Integer,Integer>> searchCycle(List<Disposant> disposants,List<Proposant> proposants){
        List<Pair<Integer,Integer>> nbrCycleTaille=new ArrayList<>();

        for (int j = 0; j < disposants.size(); j++) {
            if (disposants.get(j).getMarie() != -1 && disposants.get(j).getListeSouhait().indexOf(disposants.get(j).getMarie()) != 0) {
                // System.out.println(j);
                //  System.out.println("Que se passe-t-il si " + j + " libere " + (char) (disposants.get(j).getMarie() + 'A') + " ?");
                CompteurCycle count=new CompteurCycle();
                Couple dispoPropo = prochainEtat(disposants, proposants, j,count);
                if (dispoPropo.getDisposant() == j) {
                    //   System.out.println("sucess");
                    nbrCycleTaille.add(new Pair<>(count.getCount(),count.getCountUpgrade()));
                }
            }
        }
        return nbrCycleTaille;
    }
    public static Couple prochainEtat(List<Disposant> disposants,List<Proposant> proposants,int disposantCible,CompteurCycle count)
    {
        //ArrayList<Couple> listeMariage = new ArrayList<>();
        Map<Integer,Integer> listeMariage = new HashMap<Integer,Integer>(disposants.size());
      /*  for(int i=0;i<disposants.size();i++){
            listeMariage.add(i,new Couple(i,disposants.get(i).getMarie())); // un hash / dico / map
        }*/
        int MarieLibere = disposants.get(disposantCible).getMarie();
        Couple tmp = libereProposant(disposants,proposants,MarieLibere,listeMariage,count); // je libere un marie
        if(tmp.getDisposant()==-1 || tmp.getProposant()==-1){
            return tmp; // pas d'amélioration possible
        }

        int stop=0;
        while(tmp.getDisposant()!=-1 && tmp.getDisposant()!=disposantCible)
        {
            listeMariage.put(tmp.getDisposant(),MarieLibere);
            count.countPlusPlus();
            count.countPlusPlusUpgrade();
            // listeMariage.get(tmp.getDisposant()).setProposant(MarieLibere);
            if(tmp.getProposant()==-1)
            {
                return tmp;
            }
            MarieLibere= tmp.getProposant();
            //System.out.println(tmp.getDisposant() + " liberera "+(char) (tmp.getProposant()+'A'));
            tmp = libereProposant(disposants,proposants,tmp.getProposant(),listeMariage,count); // libere un marie et maj les mariage

            stop++;
            if(stop>200)
            {
                //  System.out.println("--- boucle infini---");
                return tmp;
            }
        }
        return new Couple(tmp.getDisposant(),MarieLibere);
    }

    public static Couple libereProposant(List<Disposant> disposants, List<Proposant> proposants, int libere, Map<Integer,Integer> mariagePossible,CompteurCycle count) // return le prochain marie liberer par quel disposant
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
            count.countPlusPlus();
            etape++;
            prochainDisposant=proposantLibere.getProchainListeAttente(etape);
            // System.out.println("Prochaine proposition de "+(char)(libere+'A')+" : "+prochainDisposant);
            if(prochainDisposant==-1)
            {
                return new Couple(-1,libere);
            }
            prochainLiberer=disposants.get(prochainDisposant).reponse(libere,mariagePossible.getOrDefault(prochainDisposant,disposants.get(prochainDisposant).getMarie()));
            // prochainLiberer=disposants.get(prochainDisposant).reponse(libere,mariagePossible.get(prochainDisposant).getProposant());
        }

        return new Couple(prochainDisposant,prochainLiberer); // return l'individu qui prendrai le proposant, et le prochain proposant liberer
    }



    public static List<Disposant> init(int seed,List<Proposant> proposantListOriginal,int totalDemande,int noteMin,int noteMax,double bruit,double noteMoyenne, double moyenneNbrVoeux)
    {
        List<Disposant> disposants = new ArrayList<>();
        int nbDemande=0;
        int id=0;
        Random rand = new Random(seed);
       /* for(Proposant p : proposantListOriginal)
        {
            int nbDemandeTmp=0;
            while(nbDemandeTmp<p.getDemande()+20) {
                Disposant d = new Disposant(id, seed, bruit, p.getReputation(), moyenneNbrVoeux, noteMax);
                disposants.add(d);
                nbDemandeTmp += d.getNbrSouhait();
                seed++;
                id++;
            }
        }*/
        while(nbDemande<totalDemande*1.05)
        {
            Disposant d = new Disposant(id,seed,bruit,moyenneNbrVoeux,moyenneNbrVoeux,20);
            disposants.add(d);
            nbDemande+=d.getNbrSouhait();
            seed++;
            id++;
        }
        proposantListOriginal = proposantListOriginal.stream().sorted((a, b) -> Double.compare(a.getReputation(), b.getReputation())).toList();
        int pos = 0;
        for(Proposant p : proposantListOriginal )
        {
            System.out.println(p.getId()+" -> "+pos);
           p.setId(pos);
            p.setPosition(pos);
            pos++;
        }

        disposants=disposants.stream().sorted((a,b)-> Double.compare(a.getNote(),b.getNote())).collect(Collectors.toList());
        int dispoNewId=0;
        for(Disposant d : disposants)
        {
            d.setId(dispoNewId);
            dispoNewId++;
        }
        List<Disposant> dispoCopy = new ArrayList<>(disposants);
        List<Proposant> proposantList = new ArrayList<>(proposantListOriginal);
       // proposantList.stream().sorted((a,b)-> (int)Math.signum(a.getReputation()-b.getReputation()));
        int i=0;
      // for(int i =0 ; i<proposantList.size();i++){
        while(proposantList.size()>0)
        {

            if(i>=proposantList.size())
            {
                i=0;
            }
            Proposant p = proposantList.get(i);
           // System.out.println(p.getId());
           // System.out.println("I "+i);
            if(p.getNbrDemandeRecu()<p.getDemande())//while(p.getNbrDemandeRecu()<p.getDemande())//
            {

                //System.out.println("Id :"+p.getId()+" Nb Demande recu :"+p.getNbrDemandeRecu());
                //double choix =  convertToRange(nombreAleatoireEntre(rand,noteMin,noteMax,noteMoyenne,1.0),noteMin,noteMax);
                //double choix = convertToRange(p.getReputation(),noteMin,noteMax);
               // double choix = convertToRange(p.getPosition(),noteMin,noteMax);
                //        System.out.println("Choix :"+choix);
                // (int) Math.round(x * 118.0 / 195.0);
               int index=(int)Math.round(p.getPosition()*Math.round((double)dispoCopy.size()/proposantListOriginal.size())/(double)proposantListOriginal.size());
                //System.out.println(index);
                int decalage=1;
                int signe=-1;
                while(index>=dispoCopy.size() || index<0 || !dispoCopy.get(index).canAffect(p))
                { // 0 1 2 3 4 5 6
                  index+=decalage*signe;
                  decalage++;
                  signe*=-1;
                }
                dispoCopy.get(index).affectSouhait(p);
               // if(disposants.size()>22000) {
             //       System.out.println(p.getReputation() + " " + disposants.get(index).getNote());
                //}
                if(dispoCopy.get(index).getNbrSouhait()==dispoCopy.get(index).getListeSouhait().size())
                {
                    dispoCopy.remove(dispoCopy.get(index));
                }
            }
           else {
                proposantList.remove(i);
            }
            i++;
        }
        for(Proposant p : proposantListOriginal)
        {
            System.out.println(p.getId());
            p.generateAllInnerListe(0.00001);
        }
        for(Disposant d : disposants)
        {
            d.genererComparaison();
        }
        return disposants;
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

        for(Proposant p:proposants)
        {
           // Proposant formationI = proposants.get(i);
            List<Integer> listAccepte = p.getListeAcceptation();
            List<Integer> listAttente = p.getListeAttente();
            List<Integer> listRefus = p.getListeRefus();
            for(int j =0 ; j < listAccepte.size();j++)
            {
                disposants.get(listAccepte.get(j)).add_accepte(p.getId());
            }
            for(int j =0 ; j < listAttente.size();j++)
            {
                disposants.get(listAttente.get(j)).add_attente(p.getId(),j);
            }
            for (int j = 0; j < listRefus.size(); j++) {
                disposants.get(listRefus.get(j)).add_refus(p.getId());
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
           System.out.println("Appel");
           Appel(proposantList,disposantsList);
         //  printListe(proposants,disposants);
           System.out.println("Reponse");
           Reponse(strategie,proposantList,disposantsList);
           //int x=9127;
           //System.out.println("Etudiant : "+disposantsList.get(x).getListeSouhait()+"\n"+disposantsList.get(x).getListeAccepte()+"\n"+disposantsList.get(x).getListeAttente()+"\n"+disposantsList.get(x).getListeRefus());
           //System.out.println("Formation :"+proposantList.get(140).getListeAcceptation());
           //System.out.println(searchCycle(disposantsList,proposantList));
         //  printListe(proposants,disposants);
       }
    }
}
