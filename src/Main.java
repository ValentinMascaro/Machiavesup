import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // int seed =67;
        int seed = 1;
        int playset = 1000;

        for(;playset<=10000;playset+=1000) {
            int nbrTest = 0;
        double preAvg=Double.MAX_VALUE;
        double avg=0.0;
        double epsilon = 0.01;
        List<Pair<Integer, Integer>> cycle;
            double nbCycle = 0;
            int nbEtudiantTraverse = 0;
            int nbEtudiantAmeliorer = 0;
             do{
                List<Disposant> disposants = new ArrayList<>();
                List<Proposant> proposants = new ArrayList<>();
                // System.out.println(seed);
                init(disposants, proposants, playset, seed);
                galeShapley(proposants, disposants, true);
                cycle = searchCycle(disposants, proposants);
                nbCycle += cycle.size();
                if (cycle.size() > 0) {
                    for (Pair<Integer, Integer> integer : cycle) {
                        nbEtudiantTraverse += integer.first();
                        nbEtudiantAmeliorer += integer.second();
                    }
                }
                preAvg = avg;
                avg = nbEtudiantAmeliorer / ++nbrTest;
                seed++;

            }while(Math.abs(preAvg-avg)>epsilon || cycle.size()==0);

                // printListeR(proposants, disposants);
            System.out.println("Nbr test : "+nbrTest);
            System.out.println("Playset : " + playset);
            System.out.println("Total cycle : " + nbCycle);
            System.out.println("Nombre de cycle moyen :" + nbCycle / nbrTest + " par seed");
            System.out.println("Nombre d'étudiant traversé en moyenne : " + nbEtudiantTraverse / nbCycle);
            System.out.println("Nombre d'étudiant amélioré en moyenne : " + nbEtudiantAmeliorer / nbCycle);

        }
            System.out.println("Fin");
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

    public static void init(List<Disposant> disposants,List<Proposant> proposants,int playset,int seed)
    {
        for(int i =0;i<playset;i++)
        {
            Proposant proposant=new Proposant(i,playset);
            proposants.add(proposant);
        }

        for(int i = 0 ; i<playset;i++)
        {
            Disposant disposant = new Disposant(i,playset,seed);
            disposant.genererListeSouhait();
            disposant.genererComparaison();
            ArrayList<Integer> res = disposant.getListeSouhait();
            for(int j = 0; j <res.size();j++)
            {
                proposants.get(res.get(j)).addIndividu(i);
            }
            seed++;
            disposants.add(disposant);
        }

        for(int i = 0 ; i<playset;i++)
        {
            proposants.get(i).generateListePreference(seed);
            seed++;
        }
    }
    public static void printListeR(List<Proposant> proposant,List<Disposant> disposant)
    {
        System.out.println("Disposant");
        for(int i =0;i<disposant.size();i++)
        {
            ArrayList<Integer> res = disposant.get(i).getListeSouhait();
            System.out.print(i+" "+res.stream().map( f -> (char)(f+'A')).toList());
            System.out.println("->"+(char) ( ('A'+disposant.get(i).getMarie()) ));
        }
        System.out.println("Proposant");
        for(int i=0;i<proposant.size();i++)
        {
            ArrayList<Integer> res =proposant.get(i).getListeSouhait();
            System.out.print( (char)('A'+i)+" "+res);
            System.out.println("->"+proposant.get(i).getMarie());
        }
    }
    public static void printListe(List<Proposant> proposant,List<Disposant> disposant)
    {
        System.out.println("Disposant");
        for(int i =0;i<disposant.size();i++)
        {
            ArrayList<Integer> res = disposant.get(i).getListeSouhait();
            System.out.println(res);
        }
        System.out.println("Proposant");
        for(int i=0;i<proposant.size();i++)
        {
            ArrayList<Integer> res =proposant.get(i).getListeSouhait();
            System.out.println(res);
        }
    }
    public static void galeShapley(List<Proposant> proposants,List<Disposant> disposants, boolean sens)
    {
        int playset=proposants.size();
        ArrayList<Proposant> pasMarie =new ArrayList<>(proposants.stream().filter(f ->!f.isMarie()).collect(Collectors.toList()));;
        if(sens)
        {
            Collections.reverse(pasMarie);
        }
        while(!pasMarie.isEmpty())
        {
            Proposant proposantsActuel = pasMarie.get(0);
            pasMarie.remove(proposantsActuel);
            while(!proposantsActuel.isMarie())
            {
                int proposition = proposantsActuel.prochaineProposition();
                //  System.out.println("Homme : "+proposantsActuel.getId()+" propose à "+proposition);
                if(proposition!=-1)
                {
                    int reponse = disposants.get(proposition).reponse(proposantsActuel.getId());
                    if (reponse != proposantsActuel.getId()) {
                        proposantsActuel.setMariage();
                        disposants.get(proposition).setMarie(proposantsActuel.getId());
                        pasMarie.remove(proposantsActuel);
                        if(reponse!=-1)
                        {
                            proposants.get(reponse).refus();
                            pasMarie.add(proposants.get(reponse));
                        }
                    }
                    else{
                        proposantsActuel.refus();
                        pasMarie.add(proposants.get(reponse));
                    }

                }
                else {
                    pasMarie.remove(proposantsActuel);
                }

            }
        }
    }
}
