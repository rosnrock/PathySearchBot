import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;

public class Main {

    public static void main(String[] args) throws IOException {

//        String url = "https://skillbox.ru";
//        Link link = new Link(url, "", 0);
//        LinkLoader loadLink = new LinkLoader(link, link);
//        new ForkJoinPool().invoke(loadLink);
//        DBConnection connection = DBConnection.getInstance();
//        connection.loadLinks(LinkLoader.loadedLinks);

        String text = "Повторное появление леопарда в Осетии позволяет предположить, " +
                "что леопард постоянно обитает в некоторых районах Северного Кавказа.";
        for (Map.Entry<TreeSet<String>, Integer> treeSetIntegerEntry : Lemmatizer.getCountWords(text).entrySet()) {
            System.out.println(treeSetIntegerEntry.getKey().first() + " - " + treeSetIntegerEntry.getValue());
        }
    }
}
