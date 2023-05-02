import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StrategieDefault implements Strategie{
    @Override
    public Integer choixParmiAcceptation(Disposant etudiant)
    {
        if(etudiant.getListeAccepte().size()==0)
        {
            return null;
        }
        Function<Integer, Integer> indexPref = (formation -> etudiant.getListeSouhait().indexOf(formation));
        Stream<Integer> listIndexPref = etudiant.getListeAccepte().stream().map(indexPref);
        int minIndex = listIndexPref.min(Integer::compare).get();
        return etudiant.getListeSouhait().get(minIndex);
    }
    @Override
    public List<Integer> choixParmiAttente(Disposant etudiant)
    {
        if(etudiant.getListeAttente().size()==0)
        {
            ArrayList<Integer> retourNull = new ArrayList<>();
            return retourNull;
        }
        Integer formationAccepte = this.choixParmiAcceptation(etudiant);
        if(formationAccepte == null)
        {
            return etudiant.getListeAttente(); // si je suis accepter nulle part, je garde tout
        }

        int indiceMeilleurFormationActuel = etudiant.getListeSouhait().indexOf(formationAccepte);
        return new ArrayList<>(etudiant.getListeAttente().stream().filter(c-> etudiant.getListeSouhait().indexOf(c) < indiceMeilleurFormationActuel).collect(Collectors.toList()));
    } // return new ArrayList<>(etudiant.getListeAttente());
}
