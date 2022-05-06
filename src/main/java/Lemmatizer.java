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

    public static Map<TreeSet<String>, Integer> getCountWords(String text) {

        Map<TreeSet<String>, Integer> words = new HashMap<>(); // мапа со словами и количеством
        String[] wordsArray = text.replaceAll("[-.?!)(,:;]", "").split("\\s+"); // текст

        for (String s : wordsArray) {
            s = s.toLowerCase();
            String morphInfo = instanceRussian.getMorphInfo(s).toString();
            if (!morphInfo.contains("СОЮЗ") && !morphInfo.contains("ПРЕДЛ")
                    && !morphInfo.contains("МЕЖД") && !morphInfo.contains("ЧАСТ")) {
                TreeSet<String> set = new TreeSet<>(instanceRussian.getNormalForms(s)); // леммы слов, TreeSet нужен чтобы не было расхождений
                words.put(set,
                        words.getOrDefault(set, 0) + 1);
            }
        }

        return words;
    }
}
