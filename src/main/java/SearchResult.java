import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SearchResult {

    private String uri;
    private String title;
    private String snippet;
    private double absRelevance;
    private double relRelevance;

    public SearchResult(String uri, String title, String snippet, double absRelevance, double relRelevance) {
        this.uri = uri;
        setTitle(title);
        this.snippet = snippet;
        this.absRelevance = absRelevance;
        this.relRelevance = relRelevance;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String html) {
        Document document = Jsoup.parse(html);
        this.title = document.title();
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public double getAbsRelevance() {
        return absRelevance;
    }

    public void setAbsRelevance(double absRelevance) {
        this.absRelevance = absRelevance;
    }

    public double getRelRelevance() {
        return relRelevance;
    }

    public void setRelRelevance(double relRelevance) {
        this.relRelevance = relRelevance;
    }

    @Override
    public String toString() {
        return uri + "\n\t" + title + "\n\t" + snippet + "\n\trelRel: " + relRelevance + "\n\tbsRel: " + absRelevance;
    }
}
