import org.jsoup.nodes.Document;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;

public class Main {

    public static void main(String[] args) throws SQLException {

        String url = "https://skillbox.ru";
        Link link = new Link(url, "", 0);

        DBConnection connection = DBConnection.getInstance();
//        LinkLoader loadLink = new LinkLoader(link, link);
        connection.downloadPages();

        // выгрузка страниц в БД
//        new ForkJoinPool().invoke(loadLink);
//        connection.uploadLinks(LinkLoader.loadedLinks);

        // выгрузка страниц из БД после обработки и лемматизация каждой



//        String text = "Повторное появление леопарда в Осетии позволяет предположить, " +
//                "что леопард постоянно обитает в некоторых районах Северного Кавказа.";
//        for (Map.Entry<TreeSet<String>, Integer> treeSetIntegerEntry : Lemmatizer.getCountWords(text).entrySet()) {
//            System.out.println(treeSetIntegerEntry.getKey().first() + " - " + treeSetIntegerEntry.getValue());
//        }
    }
}
