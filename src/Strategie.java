import java.util.ArrayList;
import java.util.List;
public interface Strategie {
    Integer choixParmiAcceptation(Disposant etudiant);
    List<Integer> choixParmiAttente(Disposant etudiant);
}
