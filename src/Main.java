import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        int seed=67;
        int playset=2;
        // test();
        for(int i=0;i<1;i++) {
            ArrayList<Disposant> disposants=new ArrayList<>();
            ArrayList<Proposant> proposants=new ArrayList<>();
            System.out.println(seed);
            init(disposants, proposants, 4, seed);
            disposants.get(3).setListeSouhait(new Integer[]{2,0,1,3});
            proposants.get(3).setListeSouhait(new Integer[]{1,3,0,2});

            galeShapley(proposants, disposants, true);
            Couple libererObtiendrai=libereProposant(disposants,proposants,0);
            System.out.println(libererObtiendrai.getDisposant()+" libererai "+libererObtiendrai.getProposant());
            printListeR(proposants,disposants);
            for(int j = 0;j<playset;j++) {
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
            }
            seed++;
        }

    }
    public static void test()
    {
        int seed=1;
        int playset=2;
        ArrayList<Disposant> disposants=new ArrayList<>();
        ArrayList<Proposant> proposants=new ArrayList<>();
        //init(disposants,proposants,playset,seed);
        Proposant proposant1=new Proposant(0,2);
        Proposant proposant2=new Proposant(1,2);
        proposants.add(proposant1);proposants.add(proposant2);
        Disposant disposant1=new Disposant(0,2,1);
        Disposant disposant2=new Disposant(0,2,1);
        disposants.add(disposant1);disposants.add(disposant2);

        proposant1.setListeSouhait(new Integer[]{0,1});
        proposant2.setListeSouhait(new Integer[]{1,0});

        disposant1.setListeSouhait(new Integer[]{1,0});
        disposant2.setListeSouhait(new Integer[]{0,1});
        galeShapley(proposants,disposants,true);
        System.out.println("Disposants");
        System.out.println(disposant1.getListeSouhait());
        System.out.println(disposant2.getListeSouhait());
        System.out.println("Proposants");
        System.out.println(proposant1.getListeSouhait());
        System.out.println(proposant2.getListeSouhait());
        System.out.println(isItTrichable(disposants,proposants,0,1));
        proposants.get(disposant1.getMarie()).refus();
        disposant1.setMarie(-1);
        galeShapley(proposants,disposants,true);
    }//
    public static Couple prochainEtatStablePour(ArrayList<Disposant> disposants,ArrayList<Proposant> proposants,int disposantCible)
    {

    }
    public static Couple libereProposant(ArrayList<Disposant> disposants,ArrayList<Proposant> proposants,int libere) // return le prochain marie liberer
    {
        Proposant proposantLibere = proposants.get(libere);
        int prochainDisposant=proposantLibere.getProchainListeAttente();
        int etape=0;
        int prochainLiberer=disposants.get(prochainDisposant).reponse(libere);
        while(prochainLiberer==libere && prochainDisposant!=-1)
        {
            etape++;
            prochainDisposant=proposantLibere.getProchainListeAttente(etape);
            prochainLiberer=disposants.get(prochainDisposant).reponse(libere);
        }
        return new Couple(prochainDisposant,prochainLiberer);
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
        ArrayList<Integer> listeProposantActuel = proposants.get(idProposantActuel).getListeSouhait();
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
    public static void printListeR(ArrayList<Proposant> proposant,ArrayList<Disposant> disposant)
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
    public static void printListe(ArrayList<Proposant> proposant,ArrayList<Disposant> disposant)
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
