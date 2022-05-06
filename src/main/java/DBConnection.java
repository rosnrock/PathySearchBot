import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class DBConnection {

    private static volatile DBConnection instance;
    private static Connection connection;

    public static DBConnection getInstance() {
        DBConnection localInstance = instance;
        if (localInstance == null) {
            synchronized (DBConnection.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = new DBConnection();
                    connection = getConnection();
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
            // TODO: временно очищаем таблицу для этого этапа, далее все будет статично
            connection.createStatement().execute("DROP TABLE IF EXISTS links");
            connection.createStatement().execute(
                    "CREATE TABLE links(" +
                            "id INT NOT NULL AUTO_INCREMENT, " +
                            "path VARCHAR(255) NOT NULL, " +
                            "code INT NOT NULL, " +
                            "content MEDIUMTEXT NOT NULL, " +
                            "PRIMARY KEY(id), KEY(path))");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    public void loadLinks(List<Link> links) {

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


            // вставляем пачками по 5 записей
            if (count > 5) {
                try {
                    String sql = "INSERT INTO links(path, code, content) " +
                            "VALUES" + values;
                    connection.createStatement().execute(sql);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                count = 0;
                values = new StringBuilder();
            }
        }

        try {
            String sql = "INSERT INTO links(path, code, content) " +
                    "VALUES" + values;
            connection.createStatement().execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
