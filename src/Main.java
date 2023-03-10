import java.util.ArrayList;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        int seed=3;
        int playset=8;
        ArrayList<Disposant> disposants=new ArrayList<>();
        ArrayList<Proposant> proposants=new ArrayList<>();
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
        preferencePartielDisposant(proposants,disposants,true);


    }
    public static void preferencePartielDisposant(ArrayList<Proposant> proposants,ArrayList<Disposant> disposants, boolean sens)
    {
        int playset=proposants.size();
        ArrayList<Proposant> pasMarie = new ArrayList<>(proposants);
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
