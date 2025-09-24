package utils;

import com.fasterxml.jackson.databind.ObjectMapper; // mapping/cast entre des fichiers JSON et des objets Java (POJOs).
import java.io.InputStream;
import java.util.List;

/**
 * Classe utilitaire pour charger des données JSON situées dans test/resources/testData.
 * - readList : lire une liste d'objets depuis un fichier JSON.
 * - readOne : lire un seul objet depuis un fichier JSON.
 */
public final class DataLoader {

    // ObjectMapper est la classe principale de Jackson qui fait la conversion JSON <-> Java.
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // Constructeur privé pour empêcher l’instanciation (car c’est une classe utilitaire).
    private DataLoader() { }

    /**
     * Lire une liste d'objets Java depuis un fichier JSON (ex: liste d'étudiants).
     *
     * @param resourcePath chemin relatif dans src/test/resources (ex: "testdata/students.json")
     * @param clazz        classe cible (ex: Student.class)
     * @return une liste d'objets de type T
     */
    public static <T> List<T> readList(String resourcePath, Class<T> clazz) {
        try (InputStream is = getResourceAsStreamOrThrow(resourcePath)) {
            var javaType = MAPPER.getTypeFactory()
                    .constructCollectionType(List.class, clazz);
            return MAPPER.readValue(is, javaType);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la lecture JSON (liste) : " + resourcePath, e);
        }
    }

    /**
     * Lire un seul objet Java depuis un fichier JSON (ex: un étudiant unique).
     *
     * @param resourcePath chemin relatif dans src/test/resources (ex: "testdata/singleStudent.json")
     * @param clazz        classe cible (ex: Student.class)
     * @return un objet de type T
     */
    public static <T> T readOne(String resourcePath, Class<T> clazz) {
        try (InputStream is = getResourceAsStreamOrThrow(resourcePath)) {
            return MAPPER.readValue(is, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la lecture JSON (objet unique) : " + resourcePath, e);
        }
    }

    /**
     * Méthode utilitaire pour ouvrir un fichier depuis /test/resources/testData.
     * Si le fichier n'existe pas, une exception claire est lancée.
     */
    private static InputStream getResourceAsStreamOrThrow(String resourcePath) {
        InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IllegalArgumentException("Fichier introuvable : " + resourcePath +
                    " (attendu sous src/test/resources)");
        }
        return is;
    }
}
