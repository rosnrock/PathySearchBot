import java.sql.*;
import java.util.*;

public class DBConnection {

    private static volatile DBConnection instance;
    private static Connection connection;
    private static HashMap<String, Double> selectors;
    private static final List<String> checkedLemmas = new ArrayList<>();

    public static DBConnection getInstance() throws SQLException {
        DBConnection localInstance = instance;
        if (localInstance == null) {
            synchronized (DBConnection.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = new DBConnection();
                    connection = getConnection();
                    selectors = getSelectors();
                }
            }
        }
        return instance;
    }

    private static Connection getConnection() {

        String dbName = "search_bot";
        String dbUser = "root";
        String dbPass = "root";

        try {
            String url = "jdbc:mysql://localhost:3306/" +
                    dbName +
                    "?user=" +
                    dbUser +
                    "&password=" +
                    dbPass +
                    "&allowPublicKeyRetrieval=true" +
                    "&useSSL=false";
            connection = DriverManager.getConnection(url);

            // TODO: временно очищаем таблицы для этого этапа, далее все будет статично
//            connection.createStatement().execute("DROP TABLE IF EXISTS field");
//            connection.createStatement().execute(
//                    "CREATE TABLE field(" +
//                            "id INT NOT NULL AUTO_INCREMENT, " +
//                            "name VARCHAR(255) NOT NULL, " +
//                            "selector VARCHAR(255) NOT NULL, " +
//                            "weight FLOAT NOT NULL, " +
//                            "PRIMARY KEY(id))");
//            connection.createStatement().execute("INSERT INTO field (name, selector, weight) " +
//                    "VALUES ('title', 'title', 1.0), ('body', 'body', 0.8)");
//            connection.createStatement().execute("DROP TABLE IF EXISTS page");
//            connection.createStatement().execute(
//                    "CREATE TABLE page(" +
//                            "id INT NOT NULL AUTO_INCREMENT, " +
//                            "path TEXT NOT NULL, " +
//                            "code INT NOT NULL, " +
//                            "content MEDIUMTEXT NOT NULL, " +
//                            "PRIMARY KEY(id))");
//            connection.createStatement().execute("DROP TABLE IF EXISTS lemma");
//            connection.createStatement().execute(
//                    "CREATE TABLE lemma(" +
//                            "id INT NOT NULL AUTO_INCREMENT, " +
//                            "lemma VARCHAR(255) NOT NULL, " +
//                            "frequency INT NOT NULL, " +
//                            "PRIMARY KEY(id))");
//            connection.createStatement().execute("DROP TABLE IF EXISTS index_t");
//            connection.createStatement().execute(
//                    "CREATE TABLE index_t(" +
//                            "id INT NOT NULL AUTO_INCREMENT, " +
//                            "page_id INT NOT NULL, " +
//                            "lemma_id INT NOT NULL, " +
//                            "rank_f FLOAT NOT NULL, " +
//                            "PRIMARY KEY(id))");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    // загрузить страницы в БД
    public void uploadLinks(List<Link> links) {

        int count = 0;
        StringBuilder values = new StringBuilder();
        for (Link link : links) {
            boolean isStart = values.length() == 0;
            count++;
            values.append(isStart ? "" : ",")
                    .append("('")
                    .append(link.getShortLinkName()).append("', ")
                    .append(link.getStatusCode()).append(", '")
                    .append(link.getLinkBody().replaceAll("'", "\"")).append("')");

            if (count > 5) { // вставляем пачками по 5 записей
                try {
                    String sql = "INSERT INTO page(path, code, content) " +
                            "VALUES" + values;
                    connection.createStatement().execute(sql);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                count = 0;
                values = new StringBuilder();
            }
        }

        if (count > 0) { // дозагрузка
            try {
                String sql = "INSERT INTO page(path, code, content) " +
                        "VALUES" + values;
                connection.createStatement().execute(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // выгрузка страниц и индексация
    public void downloadPages() throws SQLException {
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM page WHERE code = 200");
        // обработка одной страницы
        while (resultSet.next()) {
            String pageHtml = resultSet.getString("content");
            int pageId = resultSet.getInt("id");
            Map<String, Double> lemmaRank = new HashMap<>();
            for (Map.Entry<String, Double> selector : selectors.entrySet()) {
                Map<TreeSet<String>, Integer> lemmas = Lemmatizer.getCountWords(LinkLoader.getTextByTag(selector.getKey(), pageHtml));
                for (Map.Entry<TreeSet<String>, Integer> lemmaSet : lemmas.entrySet()) {
                    String lemma = lemmaSet.getKey().first();
                    // проверяем есть ли лемма в БД
                    processLemma(lemma); // TODO: узкое горло
                    // считаем ранг всех лемм для страницы
                    double rank = lemmaSet.getValue() * selector.getValue();
                    lemmaRank.put(lemma.trim(), selectors.getOrDefault(lemma.trim(), 0.0) + rank);
                }
            }
            // заполняем таблицу index_t
            StringBuilder values = new StringBuilder();
            for (Map.Entry<String, Double> lRank : lemmaRank.entrySet()) {
                ResultSet lemmaId = connection.createStatement().executeQuery("SELECT id FROM lemma WHERE lemma = '" + lRank.getKey() + "'");
                while (lemmaId.next()) {
                    int id = lemmaId.getInt("id");
                    boolean isStart = values.length() == 0;
                    values.append(isStart ? "" : ",")
                            .append("(")
                            .append(pageId).append(", ")
                            .append(id).append(", ")
                            .append(lRank.getValue()).append(")");
                }
            }
            String sql = "INSERT INTO index_t(page_id, lemma_id, rank_f) " +
                    "VALUES" + values;
            connection.createStatement().execute(sql);
        }
    }

    private void processLemma(String lemma) throws SQLException {
        // проверяем есть ли лемма в БД
        if (!checkedLemmas.contains(lemma.trim())) {
            connection.createStatement().execute("INSERT INTO lemma (lemma, frequency) " +
                    "VALUES ('" + lemma.trim() + "' , 1)");
            checkedLemmas.add(lemma.trim());
        }  else {
            connection.createStatement().execute("UPDATE lemma l " +
                    "SET l.frequency = (l.frequency + 1) WHERE l.lemma = '" + lemma.trim() + "'");
        }
    }

    // получить все текующие селекторы из БД
    private static HashMap<String, Double> getSelectors() throws SQLException {

        HashMap<String, Double> selectors = new HashMap<>();
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM field");

        while (resultSet.next()) {
            selectors.put(resultSet.getString("selector"), Double.parseDouble(resultSet.getString("weight")));
        }

        return selectors;
    }
}
