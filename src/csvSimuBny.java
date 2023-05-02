import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class csvSimuBny {
    private List<Integer> nbrdemande;

    private List<Integer> note;
    private String csvFile;
    private int nbrFormation;
    private Map<Integer,List<Proposant>> formation;
    private List<Proposant> formationList;
    protected int seed;

    public int getNbrFormation() {
        return nbrFormation;
    }

    public csvSimuBny(String cheminFichier,int seed)
    {
        this.seed=seed;
        this.csvFile="src/"+cheminFichier;
        this.nbrdemande=new ArrayList<>();
        this.note = new ArrayList<>();
    }
    public void setFormation(int nbrBloc)
    {
       // List<Proposant> proposants=new ArrayList<>();
        List<Double> noteFormation = this.getReputation();
        List<Integer> demandeFormation = this.nbrdemande;
        List<Integer> capaciteFormation=this.getCapacite();
        this.nbrFormation=capaciteFormation.size();
        double max = noteFormation.stream().mapToDouble(d -> d).max().orElse(0.0);
        double min = Collections.min(noteFormation);
       // Map<Integer,List<Double>> proposants = new HashMap<>();
        this.formation=new HashMap<>();
        this.formationList=new ArrayList<>();
        for(int i =0;i<nbrBloc+1;i++)
        {
            formation.put( i,new ArrayList<>());
        }
        for(int i =0;i<demandeFormation.size();i++)
        {
           // System.out.println("Max : "+max+" Min "+min);
            double note =  (noteFormation.get(i) - min) / (max - min) * nbrBloc;
           // System.out.println(i+" "+noteFormation.get(i)+" "+note);
            int key = (int) note;
           // System.out.println(i+" | "+note+" | "+key);
            //proposants.get(key).add(note);
            Proposant prop = new Proposant(i,seed+i,demandeFormation.get(i),note,capaciteFormation.get(i),nbrBloc);
            formation.get(key).add(prop);
            formationList.add(prop);
        }
    }
    public List<Double> getReputation()
    {
        List<Double> liste2 = this.getNbrAdmis();
        List<Double> liste1 = this.getNbrProposition();
        if (liste1.size() != liste2.size()) {
            throw new IllegalArgumentException("Les deux listes doivent avoir la même taille.");
        }

        List<Double> resultat = IntStream.range(0, liste1.size())
                .mapToDouble(i ->((double)liste1.get(i) / (double)liste2.get(i)))
                .boxed()
                .collect(Collectors.toList());

        return resultat;
    }

    public void setNbrdemande() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(this.csvFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<Integer> columnEffectif = new ArrayList<>();
        try {
           // reader = new BufferedReader(new FileReader(csvFile));
            String header = reader.readLine(); // Lire la première ligne pour obtenir les noms des colonnes

            String[] headerColumns = header.split(";"); // Séparer les noms des colonnes par le séparateur ";"
            int colIndex = -1; // Indice de la colonne "Effectif total des candidats pour une formation"

            // Trouver l'indice de la colonne "Effectif total des candidats pour une formation"
            for (int i = 0; i < headerColumns.length; i++) {
                if (headerColumns[i].equals("Effectif total des candidats pour une formation")) {
                    colIndex = i;
                    break;
                }
            }

            if (colIndex == -1) {
                System.out.println("La colonne 'Effectif total des candidats pour une formation' n'a pas été trouvée.");
                return;
            }

            String nextLine;
            int i = 0;
            // Taille du tableau à ajuster selon la taille du fichier CSV
            while ((nextLine = reader.readLine()) != null) {
                String[] columns = nextLine.split(";"); // Séparer les colonnes par le séparateur ";"
                columnEffectif.add(Integer.valueOf(columns[colIndex])); // Stocker la valeur de la colonne "Effectif total des candidats pour une formation"
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Afficher les valeurs stockées dans le tableau
        this.nbrdemande=columnEffectif;
    }
    public List<Integer> getCapacite() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(this.csvFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<Integer> columnEffectif = new ArrayList<>();
        try {
            // reader = new BufferedReader(new FileReader(csvFile));
            String header = reader.readLine(); // Lire la première ligne pour obtenir les noms des colonnes

            String[] headerColumns = header.split(";"); // Séparer les noms des colonnes par le séparateur ";"
            int colIndex = -1; // Indice de la colonne "Effectif total des candidats pour une formation"

            // Trouver l'indice de la colonne "Effectif total des candidats pour une formation"
            for (int i = 0; i < headerColumns.length; i++) {
                if (headerColumns[i].equals("Capacité de l’établissement par formation")) {
                    colIndex = i;
                    break;
                }
            }

            if (colIndex == -1) {
                System.out.println("La colonne 'Capacité de l’établissement par formation' n'a pas été trouvée.");
                return null;
            }

            String nextLine;
            int i = 0;
            // Taille du tableau à ajuster selon la taille du fichier CSV
            while ((nextLine = reader.readLine()) != null) {
                String[] columns = nextLine.split(";"); // Séparer les colonnes par le séparateur ";"
                columnEffectif.add(Integer.valueOf(columns[colIndex])); // Stocker la valeur de la colonne "Effectif total des candidats pour une formation"
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Afficher les valeurs stockées dans le tableau
        return columnEffectif;
    }
    public List<Double> getNbrProposition() {
        //String csvFile = "chemin/vers/votre/fichier.csv";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(this.csvFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<Double> columnEffectif = new ArrayList<>();

        try {
           // reader = new BufferedReader(new FileReader(csvFile));
            String header = reader.readLine(); // Lire la première ligne pour obtenir les noms des colonnes

            String[] headerColumns = header.split(";"); // Séparer les noms des colonnes par le séparateur ";"
            int colIndex = -1; // Indice de la colonne "Effectif total des candidats pour une formation"

            // Trouver l'indice de la colonne "Effectif total des candidats pour une formation"
            for (int i = 0; i < headerColumns.length; i++) {
                if (headerColumns[i].equals("Effectif total des candidats ayant reçu une proposition d’admission de la part de l’établissement")) {
                    colIndex = i;
                    break;
                }
            }

            if (colIndex == -1) {
                System.out.println("La colonne 'Effectif total des candidats ayant reçu une proposition d’admission de la part de l’établissement' n'a pas été trouvée.");
                return null;
            }

            String nextLine;
            int i = 0;
            // Taille du tableau à ajuster selon la taille du fichier CSV
            while ((nextLine = reader.readLine()) != null) {
                String[] columns = nextLine.split(";"); // Séparer les colonnes par le séparateur ";"
                if(Double.valueOf(columns[colIndex])==0.0)
                {
                    columnEffectif.add(1.0);
                }
                else {
                    columnEffectif.add(Double.valueOf(columns[colIndex])); // Stocker la valeur de la colonne "Effectif total des candidats pour une formation"
                }
                    i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Afficher les valeurs stockées dans le tableau
        return columnEffectif;
    }
    public List<Double> getNbrAdmis()  {
       // String csvFile = "chemin/vers/votre/fichier.csv";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(this.csvFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<Double> columnEffectif = new ArrayList<>();

        try {
           // reader = new BufferedReader(new FileReader(csvFile));
            String header = reader.readLine(); // Lire la première ligne pour obtenir les noms des colonnes

            String[] headerColumns = header.split(";"); // Séparer les noms des colonnes par le séparateur ";"
            int colIndex = -1; // Indice de la colonne "Effectif total des candidats pour une formation"

            // Trouver l'indice de la colonne "Effectif total des candidats pour une formation"
            for (int i = 0; i < headerColumns.length; i++) {
                if (headerColumns[i].equals("Effectif total des candidats ayant accepté la proposition de l’établissement (admis)")) {
                    colIndex = i;
                    break;
                }
            }

            if (colIndex == -1) {
                System.out.println("La colonne 'Effectif total des candidats ayant accepté la proposition de l’établissement (admis)' n'a pas été trouvée.");
                return null;
            }

            String nextLine;
            int i = 0;
            // Taille du tableau à ajuster selon la taille du fichier CSV
            while ((nextLine = reader.readLine()) != null) {
                String[] columns = nextLine.split(";"); // Séparer les colonnes par le séparateur ";"
                if(Double.valueOf(columns[colIndex])==0.0)
                {
                    columnEffectif.add(1.0);
                }else {
                    columnEffectif.add(Double.valueOf(columns[colIndex]));
                }
                 // Stocker la valeur de la colonne "Effectif total des candidats pour une formation"
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Afficher les valeurs stockées dans le tableau
        //this.nbrdemande=columnEffectif;
        return columnEffectif;
    }

    public List<Integer> getNbrdemande() {
        return nbrdemande;
    }

    public Map<Integer, List<Proposant>> getFormation() {
        return formation;
    }

    public List<Proposant> getFormationList() {
        return formationList;
    }
}
