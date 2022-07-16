import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

public class SearchResult {

    private String uri;
    private String title;
    private String snippet;
    private double absRelevance;
    private double relRelevance;
    private List<Lemma> lemmaList;

    public SearchResult(String uri, String html, double absRelevance, double relRelevance, List<Lemma> lemmaList) {
        this.uri = uri;
        this.absRelevance = absRelevance;
        this.relRelevance = relRelevance;
        this.lemmaList = lemmaList;
        Document document = Jsoup.parse(html);
        setTitle(document);
        setSnippet(document);
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

    public void setTitle(Document document) {
        this.title = document.title();
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(Document document) {
        this.snippet = Lemmatizer.getSnippet(lemmaList, document.text());
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
