import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.*;

public class Lemmatizer {

    private static volatile LuceneMorphology instanceRussian;

    static {
        try {
            instanceRussian = new RussianLuceneMorphology();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LinkedHashMap<TreeSet<String>, Integer> getCountWords(String text) {

        LinkedHashMap<TreeSet<String>, Integer> words = new LinkedHashMap<>(); // мапа со словами и количеством
        String[] wordsArray = text.toLowerCase().replaceAll("[-.?!)(,:;0-9]", " ").split("\\s+"); // текст

        for (String s : wordsArray) {
            try {
                String morphInfo = instanceRussian.getMorphInfo(s).toString();
                if (!morphInfo.contains("СОЮЗ") && !morphInfo.contains("ПРЕДЛ")
                        && !morphInfo.contains("МЕЖД") && !morphInfo.contains("ЧАСТ")) {
                    TreeSet<String> set = new TreeSet<>(instanceRussian.getNormalForms(s)); // леммы слов, TreeSet нужен чтобы не было расхождений
                    words.put(set,words.getOrDefault(set, 0) + 1);
                }
            } catch (Exception ignored) {
                // ignored
            }
        }

        return words;
    }
}
