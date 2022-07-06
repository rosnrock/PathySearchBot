import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class Main {

    public static void main(String[] args) throws SQLException {

        /* Процесс индексации сайта */
        // Задание сайта для индексации
//        List<String> urls = Arrays.asList("https://skillfactory.ru", "https://gb.ru");
//        for (String url : urls) {
//            Link link = new Link(url, "", 0);
//            // Подключение к БД
            DBConnection connection = DBConnection.getInstance();
//            // Выгрузка дерева страниц во внутреннюю коллекцию
//            LinkLoader loadLink = new LinkLoader(link, link);
//            new ForkJoinPool().invoke(loadLink);
//            // Выгрузка дерева страниц в БД
//            connection.uploadLinks(LinkLoader.loadedLinks);
//            // Индексация выгруженных страниц
//            connection.indexPages();
//            /* --- */
//        }

        /* Процесс поиска по строке */
        String findQuery = "аналитика данных разработка";
        Map<TreeSet<String>, Integer> words = Lemmatizer.getCountWords(findQuery);
        LinkedList<Lemma> lemmaList = new LinkedList<>(DBConnection.getLemmaFrequency(words));
        List<Integer> idPages = DBConnection.getPages(new LinkedList<>(DBConnection.getLemmaFrequency(words)), new ArrayList<>(), true);
        List<SearchResult> searchResults = DBConnection.getSearchResult(idPages, lemmaList);
        searchResults.forEach(System.out::println);
    }
}
