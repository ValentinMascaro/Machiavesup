import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
      // int seed =67;

        int nbrEtudiant=20;
        int nbrFormation=4;
        int capaciteMin=2;
        int capaciteMax=4;
        int nbrCycle=0;
        int nbrTest=1000;
        double bruitEtudiant=3.0;
        double bruitFormation=3.0;
        int nbrTestBruit=20;
        ArrayList<Disposant> disposants=new ArrayList<>();
        ArrayList<Proposant> proposants=new ArrayList<>();
        for(int b = 0;b<nbrTestBruit;b++) {
            int seed=10;
            bruitFormation++;
            nbrCycle=0;
            List<Integer> listSeed = new ArrayList<>();
            for (int i = 0; i < nbrTest; i++) {
                disposants = new ArrayList<>();
                proposants = new ArrayList<>();
                Parcoursup(proposants, disposants, 10, seed, nbrEtudiant, nbrFormation, capaciteMin, capaciteMax, bruitEtudiant, bruitFormation);
                //printListeFormation(proposants);
               // printListeMini(proposants, disposants);
                //  printListe(proposants,disposants);
                for (int j = 0; j < disposants.size(); j++) {
                    if (disposants.get(j).getMarie() != -1 && disposants.get(j).getListeSouhait().indexOf(disposants.get(j).getMarie()) != 0) {
                        //  System.out.println("Que se passe-t-il si " + j + " libere " + (char) (disposants.get(j).getMarie() + 'A') + " ?");
                        Couple dispoPropo = prochainEtat(disposants, proposants, j, seed);
                        if (dispoPropo.getDisposant() == j) {
                            nbrCycle++;
                            //System.out.println(seed);
                            listSeed.add(seed);
                            // System.out.println("/!\\ " + j + " pourrai obtenir " + (char) (dispoPropo.getProposant() + 'A') + " en liberant " + (char) (disposants.get(j).getMarie() + 'A'));
                        }
                    }
                }
                seed++;
            }
            printListeMini(proposants,disposants);
            System.out.println("BruitEtudiant : " + bruitEtudiant + "\nBruitFormation : " + bruitFormation);
            System.out.println(nbrCycle + " cycle trouvée sur " + nbrEtudiant + " avec " + nbrFormation + "formation pour " + nbrTest + " test");
            System.out.println(listSeed);
            System.out.println("FIN");
        }
    }

    public static Couple prochainEtat(ArrayList<Disposant> disposants,ArrayList<Proposant> proposants,int disposantCible,int seed)
    {
        ArrayList<Couple> listeMariage = new ArrayList<>();
        for(int i=0;i<disposants.size();i++){
            listeMariage.add(i,new Couple(i,disposants.get(i).getMarie()));
        }
        int MarieLibere = disposants.get(disposantCible).getMarie();
        Couple tmp = libereProposant(disposants,proposants,MarieLibere,listeMariage); // je libere un marie
        if(tmp.getDisposant()==-1 || tmp.getProposant()==-1){
            return tmp; // pas d'amélioration possible
        }

        int stop=0;
        while(tmp.getDisposant()!=-1 && tmp.getDisposant()!=disposantCible)
        {
            listeMariage.get(tmp.getDisposant()).setProposant(MarieLibere);
            if(tmp.getProposant()==-1)
            {
                return tmp;
            }
            MarieLibere= tmp.getProposant();
         //   System.out.println(tmp.getDisposant() + " liberera "+(char) (tmp.getProposant()+'A'));
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
    public static Couple libereProposant(ArrayList<Disposant> disposants,ArrayList<Proposant> proposants,int libere, ArrayList<Couple> mariagePossible) // return le prochain marie liberer par quel disposant
    {
        Proposant proposantLibere = proposants.get(libere);
        int prochainDisposant=proposantLibere.getProchainListeAttente();
        if(prochainDisposant==-1)
        {
            return new Couple(-1,libere);
        }
        int etape=0;
        int prochainLiberer=disposants.get(prochainDisposant).reponse(libere,mariagePossible.get(prochainDisposant).getProposant());
     //   System.out.println("Prochaine proposition de "+(char)(libere+'A')+" : "+prochainDisposant);
        while(prochainLiberer==libere && prochainDisposant!=-1)
        {
            //System.out.println(prochainDisposant+" refuse");
            etape++;
            prochainDisposant=proposantLibere.getProchainListeAttente(etape);
           // System.out.println("Prochaine proposition de "+(char)(libere+'A')+" : "+prochainDisposant);
            if(prochainDisposant==-1)
            {
                return new Couple(-1,libere);
            }
            prochainLiberer=disposants.get(prochainDisposant).reponse(libere,mariagePossible.get(prochainDisposant).getProposant());
        }

        return new Couple(prochainDisposant,prochainLiberer); // return l'individu qui prendrai le proposant, et le prochain proposant liberer
    }



    public static void init(ArrayList<Disposant> disposants,ArrayList<Proposant> proposants,int playsetEtudiant,int playsetFormation,int seed,int capaciteMin,int capaciteMax,double bruitEtudiant,double bruitFormation)
    {
        for(int i =0;i<playsetFormation;i++)
        {
            Proposant proposant=new Proposant(i,seed,capaciteMin,capaciteMax,bruitFormation);
            proposants.add(proposant);
            seed++;
        }

        for(int i = 0 ; i<playsetEtudiant;i++)
        {
            Disposant disposant = new Disposant(i,playsetFormation,seed,bruitEtudiant);
            disposant.genererListeSouhait(proposants);
            disposant.genererComparaison();
            List<Integer> res = disposant.getListeSouhait();
            for(int j = 0; j <res.size();j++)
            {
                proposants.get(res.get(j)).addDossierIndividu(disposant);
            }
            seed++;
            disposants.add(disposant);
        }

        for(int i = 0 ; i<playsetFormation;i++)
        {
            proposants.get(i).generateAllInnerListe(seed);
            seed++;
        }
    }
    public static void printListe(ArrayList<Proposant> proposant,ArrayList<Disposant> disposant)
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
    public static void printListeFormation(ArrayList<Proposant> proposant)
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
    public static void printListeMini(ArrayList<Proposant> proposant,ArrayList<Disposant> disposant)
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
            System.out.print("Formation : "+(char)('A'+p.getId())+" "+ p.getNbrIndividu()+" places");
            System.out.println(" Souhait : "+souhait+ " "+ accepte );
        }
    }
    public static void Appel(ArrayList<Proposant> proposants,ArrayList<Disposant> disposants)
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
    public static void Reponse(Strategie strategie,ArrayList<Proposant> proposants,ArrayList<Disposant> disposants)
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
    public static void Parcoursup(ArrayList<Proposant> proposants,ArrayList<Disposant> disposants,int nbrJour,int seed,int nbrEtudiant,int nbrFormation,int capaciteMin,int capaciteMax,double bruitEtudiant,double bruitFormation)
    {
        Strategie strategie = new StrategieDefault();
        init(disposants,proposants,nbrEtudiant,nbrFormation,seed,capaciteMin,capaciteMax,bruitEtudiant,bruitFormation);
       /* disposants.get(2).setListeSouhait(new Integer[]{2,4,0,1,3});
        proposants.get(2).setListeSouhait(new Integer[]{3,4,2,6,7,1});
        disposants.get(4).setListeSouhait(new Integer[]{4,0,2,1,3});
        proposants.get(0).setListeSouhait(new Integer[]{9,5,2,0,6,4,7,1});
        proposants.get(4).setListeSouhait(new Integer[]{5,2,6,4,1,8});*/
       for(int jour = 0; jour < nbrJour ; jour++)
       {
        //   System.out.println("Jour : "+jour+" / "+nbrJour);
         //  System.out.println("---------------------------");
         //  printListe(proposants,disposants);
           Appel(proposants,disposants);
         //  printListe(proposants,disposants);
           Reponse(strategie,proposants,disposants);
         //  printListe(proposants,disposants);
       }
    }
}
