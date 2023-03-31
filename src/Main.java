import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
      // int seed =67;
        int seed=166;
        int playset=20;            ;
        // test();
        Random rand = new Random(10);
        for(int i=0;i<100;i++) {
            ArrayList<Disposant> disposants=new ArrayList<>();
            ArrayList<Proposant> proposants=new ArrayList<>();
            System.out.println(seed);
            init(disposants, proposants, playset, seed);
          //  disposants.get(3).setListeSouhait(new Integer[]{2,0,1,3});
           // proposants.get(3).setListeSouhait(new Integer[]{1,3,0,2}); //seed 67

            galeShapley(proposants, disposants, true);
            printListeR(proposants,disposants);
            for(int j =0 ; j<playset;j++)
            {
                if(disposants.get(j).getMarie()!=-1 && disposants.get(j).getListeSouhait().indexOf(disposants.get(j).getMarie())!=0) {
                    System.out.println("Que se passe-t-il si " + j + " libere " + (char) (disposants.get(j).getMarie() + 'A') + " ?");
                    Couple dispoPropo = prochainEtat(disposants, proposants, j,seed);
                    if (dispoPropo.getDisposant() == j) {
                        System.out.println("/!\\ " + j + " pourrai obtenir " + (char) (dispoPropo.getProposant() + 'A') + " en liberant " + (char) (disposants.get(j).getMarie() + 'A'));
                    }
                }
            }
            System.out.println("FIN");
          /* for(int j = 0;j<playset;j++) {
                ArrayList<Integer> prop = disposants.get(j).getListeSouhait();
                for(int y = disposants.get(j).getIndexMarie()-1 ; y>=0;y--) {
                    int dispoMarie = echangePossible(j,disposants, proposants, disposants.get(j).getMarie(), prop.get(y));//int dispoMarie = isItTrichable(disposants, proposants, disposants.get(j).getMarie(), prop.get(y));
                    if(dispoMarie!=-1 )
                    {
                        if(echangePossible(dispoMarie,disposants,proposants, disposants.get(dispoMarie).getMarie(),disposants.get(j).getMarie())!=-1)
                        {
                            System.out.println(j + " obtiendrai : " + (char) (prop.get(y) + 'A') + " par " + dispoMarie);
                        }

                    }
                }
            }*/
            seed++;
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
            System.out.println(tmp.getDisposant() + " liberera "+(char) (tmp.getProposant()+'A'));
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
    public static Couple prochainEtatStablePour(ArrayList<Disposant> disposants,ArrayList<Proposant> proposants,int disposantCible,int seed)
    {
        int proposantLiberer = disposants.get(disposantCible).getMarie();
        Couple dispoPropo = libereProposant(disposants,proposants,proposantLiberer);
        int tmpRes=-1;
        int stop=0;
        while(dispoPropo.getDisposant()!=-1 && dispoPropo.getDisposant()!=disposantCible)
        {
            tmpRes=dispoPropo.getProposant();
            System.out.println(dispoPropo.getDisposant() + " liberera "+(char) (dispoPropo.getProposant()+'A'));
            dispoPropo = libereProposant(disposants,proposants,dispoPropo.getProposant());
            stop++;
            if(stop==10)
            {
                System.out.println("----BOUCLE INFINI--- "+seed);
                return new Couple(-1,-1);
            }
        }
        if(dispoPropo.getDisposant()==-1)
        {
            return dispoPropo;
        }
        else
        {
            return new Couple(dispoPropo.getDisposant(),tmpRes);
        }
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
        System.out.println("Prochaine proposition de "+(char)(libere+'A')+" : "+prochainDisposant);
        while(prochainLiberer==libere && prochainDisposant!=-1)
        {
            System.out.println(prochainDisposant+" refuse");
            etape++;
            prochainDisposant=proposantLibere.getProchainListeAttente(etape);
            System.out.println("Prochaine proposition de "+(char)(libere+'A')+" : "+prochainDisposant);
            if(prochainDisposant==-1)
            {
                return new Couple(-1,libere);
            }
            prochainLiberer=disposants.get(prochainDisposant).reponse(libere,mariagePossible.get(prochainDisposant).getProposant());
        }

        return new Couple(prochainDisposant,prochainLiberer); // return l'individu qui prendrai le proposant, et le prochain proposant liberer
    }
    public static Couple libereProposant(ArrayList<Disposant> disposants,ArrayList<Proposant> proposants,int libere) // return le prochain marie liberer par quel disposant
    {
        Proposant proposantLibere = proposants.get(libere);
        int prochainDisposant=proposantLibere.getProchainListeAttente();
        if(prochainDisposant==-1)
        {
           return new Couple(-1,libere);
        }
        int etape=0;
       int prochainLiberer=disposants.get(prochainDisposant).reponse(libere);
        System.out.println("Prochaine proposition de "+(char)(libere+'A')+" : "+prochainDisposant);
        while(prochainLiberer==libere && prochainDisposant!=-1)
        {
            System.out.println(prochainDisposant+" refuse");
            etape++;
            prochainDisposant=proposantLibere.getProchainListeAttente(etape);
            System.out.println("Prochaine proposition de "+(char)(libere+'A')+" : "+prochainDisposant);
            if(prochainDisposant==-1)
            {
                return new Couple(-1,libere);
            }
            prochainLiberer=disposants.get(prochainDisposant).reponse(libere);
        }

        return new Couple(prochainDisposant,prochainLiberer); // return l'individu qui prendrai le proposant, et le prochain proposant liberer
    }


    public static int echangePossible(int cible,ArrayList<Disposant> disposants,ArrayList<Proposant> proposants, int indiceMarieActuel,int indiceMarieSouhaite)
    {
        Proposant marieSouhaite=proposants.get(indiceMarieSouhaite); // 3
        Proposant marieActuel = proposants.get(indiceMarieActuel); // 0
        int FemmeMarieSouhaite=marieSouhaite.getMarie(); // 0

        // 0 préfére 3 à 0 ? oui
        if(disposants.get(FemmeMarieSouhaite).reponse(indiceMarieActuel)==indiceMarieSouhaite) {
            // est-ce que prochaine prop de notre marie est la femme du marie qu'on souhaite ? oui
            if(marieActuel.getProchainListeAttente()==FemmeMarieSouhaite)
            {
                System.out.println("échange direct");
                return FemmeMarieSouhaite;
            }
            else
            {
                int nbrEtape=1;
                // le prochain du prochain dans la liste d'attente
                int disposantListeAttente=marieActuel.getProchainListeAttente(1);
                while (disposantListeAttente != -1) {
                    if(disposantListeAttente==FemmeMarieSouhaite)
                    {
                        System.out.println("échange indirect en "+nbrEtape+" étape");
                        return FemmeMarieSouhaite;
                    }
                    if(disposants.get(disposantListeAttente).reponse(indiceMarieSouhaite) != indiceMarieSouhaite){
                        return -1;
                    }
                    nbrEtape++;
                    disposantListeAttente = marieSouhaite.getProchainListeAttente(nbrEtape);
                }
            }
        }
        return -1;
    }
    public static int isItTrichable(ArrayList<Disposant> disposants,ArrayList<Proposant> proposants,int idProposantActuel,int idProposantSouhaite)
    {
        int dispoMarie = proposants.get(idProposantSouhaite).getMarie();
        List<Integer> listeProposantActuel = proposants.get(idProposantActuel).getListeSouhait();
        if(disposants.get(dispoMarie).reponse(idProposantActuel)!=idProposantActuel)
        {
            if(listeProposantActuel.indexOf(dispoMarie)==proposants.get(idProposantActuel).getIndiceProposition()+1)
            {
                System.out.println("échange parfait");
            }
            else{
                System.out.println("échange imparfait");
            }
            return dispoMarie;
        }
        return -1;
    }
    public static void init(ArrayList<Disposant> disposants,ArrayList<Proposant> proposants,int playset,int seed)
    {
        for(int i =0;i<playset;i++)
        {
            Proposant proposant=new Proposant(i,seed);
            proposants.add(proposant);
            seed++; // seed 67 plus valable
        }

        for(int i = 0 ; i<playset;i++)
        {
            Disposant disposant = new Disposant(i,playset,seed);
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

        for(int i = 0 ; i<playset;i++)
        {
            proposants.get(i).generateListePreference(seed);
            seed++;
        }
    }
    public static void printListeR(ArrayList<Proposant> proposant,ArrayList<Disposant> disposant)
    {
        System.out.println("Disposant");
        for(int i =0;i<disposant.size();i++)
        {
            List<Integer> res = disposant.get(i).getListeSouhait();
            System.out.print(i+" "+res.stream().map( f -> (char)(f+'A')).toList());
            System.out.println("->"+(char) ( ('A'+disposant.get(i).getMarie()) ));
        }
        System.out.println("Proposant");
        for(int i=0;i<proposant.size();i++)
        {
            List<Integer> res =proposant.get(i).getListeSouhait();
            System.out.print( (char)('A'+i)+" "+res);
            System.out.println("->"+proposant.get(i).getMarie());
        }
    }
    public static void printListe(ArrayList<Proposant> proposant,ArrayList<Disposant> disposant)
    {
        System.out.println("Disposant");
        for(int i =0;i<disposant.size();i++)
        {
            List<Integer> res = disposant.get(i).getListeSouhait();
            System.out.println(res);
        }
        System.out.println("Proposant");
        for(int i=0;i<proposant.size();i++)
        {
            List<Integer> res =proposant.get(i).getListeSouhait();
            System.out.println(res);
        }
    }

    public static void galeShapley(ArrayList<Proposant> proposants,ArrayList<Disposant> disposants, boolean sens)
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
        System.out.println("Resultat mariage");
        for(int i =0 ; i<playset;i++)
        {
            System.out.println("id : "+i+" -> "+disposants.get(i).getMarie());
        }

    }
}
