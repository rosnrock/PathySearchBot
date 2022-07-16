import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

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
                    words.put(set, words.getOrDefault(set, 0) + 1);
                }
            } catch (Exception ignored) {
                // ignored
            }
        }

        return words;
    }

    public static String getSnippet(List<Lemma> lemmaList, String text) {
        StringBuilder snippet = new StringBuilder("");
        for (Lemma lemma : lemmaList) {
            List<String> sentences = getSentences(lemma, text);
            sentences.forEach(s -> snippet.append(s).append("\n"));
        }

        return snippet.toString();
    }

    private static List<String> getSentences(Lemma lemma, String text) {
        List<String> snippets = new ArrayList<>();
        String[] sentences = text.split("\\p{Punct}\\s+");
        for (int i = 0; i < sentences.length; i++) {
            String[] split = sentences[i].split("\\s+");
            for (int j = 0; j < split.length; j++) {
                try {
                    TreeSet<String> normalFrom = new TreeSet<>(instanceRussian.getNormalForms(split[j].toLowerCase()));
                    if (lemma.getName().equals(normalFrom.first())) {
                        split[j] = "<b>" + split[j] + "</b>";
                        snippets.add(String.join(" ", split));
                        break;
                    }
                } catch (Exception e) {
                    //ignored
                }
            }
        }

        for (String sentence : snippets) {
            System.out.println(sentence);
        }

        return snippets;
    }
}
