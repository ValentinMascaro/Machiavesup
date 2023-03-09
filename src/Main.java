import java.util.ArrayList;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        int seed=10;
        int playset=10;
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
            System.out.println(res);
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
            System.out.println(proposants.get(i).getListeSouhait());
            seed++;
        }
        preferencePartielDisposant(proposants,disposants,true);
        for(int i =0;i<playset;i++)
        {
            proposants.get(i).clean();
            disposants.get(i).clean();
        }
        preferencePartielDisposant(proposants,disposants,false);
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
