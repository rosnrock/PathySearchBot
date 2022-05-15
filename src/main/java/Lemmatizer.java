import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.*;

public class Lemmatizer {

    private static volatile LuceneMorphology instanceRussian;
    private static List<HashMap<String, Integer>> listLemmas;

    static {
        try {
            instanceRussian = new RussianLuceneMorphology();
        } catch (IOException e) {
            e.printStackTrace();
        }
        listLemmas = new ArrayList<>();
    }

    public static Map<TreeSet<String>, Integer> getCountWords(String text) {

        Map<TreeSet<String>, Integer> words = new HashMap<>(); // мапа со словами и количеством
        String[] wordsArray = text.toLowerCase().replaceAll("[-.?!)(,:;0-9]", "").split("\\s+"); // текст

        for (String s : wordsArray) {
            try {
                String morphInfo = instanceRussian.getMorphInfo(s).toString();
                if (!morphInfo.contains("СОЮЗ") && !morphInfo.contains("ПРЕДЛ")
                        && !morphInfo.contains("МЕЖД") && !morphInfo.contains("ЧАСТ")) {
                    TreeSet<String> set = new TreeSet<>(instanceRussian.getNormalForms(s)); // леммы слов, TreeSet нужен чтобы не было расхождений
                    words.put(set,
                            words.getOrDefault(set, 0) + 1);
                }
            } catch (Exception ignored) {
                // ignored
            }
        }

        return words;
    }
}
