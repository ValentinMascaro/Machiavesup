import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
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
        System.out.println(isItTrichable(disposants,proposants,0,disposants.get(0).getIndexMarie(),1));
        proposants.get(disposant1.getMarie()).refus();
        disposant1.setMarie(-1);
        galeShapley(proposants,disposants,true);
    }
    public static boolean isItTrichable(ArrayList<Disposant> disposants,ArrayList<Proposant> proposants,int idDisposant,int idProposantActuel,int idProposantSouhaite)
    {
        ArrayList<Integer> listeSouhaitProposantSouhaite = proposants.get(idProposantSouhaite).getListeSouhait();
        Integer suivanteListeAttente = listeSouhaitProposantSouhaite.get(proposants.get(idProposantSouhaite).getIndiceProposition()+1);
        if(disposants.get(suivanteListeAttente).reponse(idProposantActuel)!=idProposantActuel)
        {
            return true;
        }
        return false;
    }
   public static void init(ArrayList<Disposant> disposants,ArrayList<Proposant> proposants,int playset,int seed)
   {
       for(int i =0;i<playset;i++)
       {
           Proposant proposant=new Proposant(i,playset);
           proposants.add(proposant);
       }
       System.out.println("Disposants :");
       for(int i = 0 ; i<playset;i++)
       {
           Disposant disposant = new Disposant(i,playset,seed);
           disposant.genererListeSouhait();
           disposant.genererComparaison();
           ArrayList<Integer> res = disposant.getListeSouhait();
           System.out.println(i+" : "+res);
           for(int j = 0; j <res.size();j++)
           {
               proposants.get(res.get(j)).addIndividu(i);
           }
           seed++;
           disposants.add(disposant);
       }

       System.out.println("------------\nProposants");
       for(int i = 0 ; i<playset;i++)
       {
           proposants.get(i).generateListePreference(seed);
           System.out.println(i+" : "+proposants.get(i).getListeSouhait());
           seed++;
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
