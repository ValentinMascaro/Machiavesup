import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class csvSimuBny {
    private List<Integer> nbrdemande;
    private List<Integer> note;
    private BufferedReader csvFile;
    public csvSimuBny(String cheminFichier)
    {
        try {
             this.csvFile= new BufferedReader(new FileReader("src/"+cheminFichier));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.nbrdemande=new ArrayList<>();
        this.note = new ArrayList<>();
    }
    public void setNbrdemande() {

        BufferedReader reader = this.csvFile;
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

    public List<Integer> getNbrProposition() {
        //String csvFile = "chemin/vers/votre/fichier.csv";
        BufferedReader reader = this.csvFile;
        List<Integer> columnEffectif = new ArrayList<>();

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
        return columnEffectif;
    }
    public List<Integer> getNbrAdmis()  {
       // String csvFile = "chemin/vers/votre/fichier.csv";
        BufferedReader reader = this.csvFile;
        List<Integer> columnEffectif = new ArrayList<>();

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
    }
