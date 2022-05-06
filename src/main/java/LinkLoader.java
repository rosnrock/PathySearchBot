import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RecursiveTask;

public class LinkLoader extends RecursiveTask<Link> {

    private final Link loadLink;
    private final Link rootLink;
    private static final Set<String> checkedLinks = new HashSet<>();
    public static final List<Link> loadedLinks = new ArrayList<>();

    public LinkLoader(Link loadLink, Link rootLink) {
        this.loadLink = loadLink;
        this.rootLink = rootLink;
    }

    @Override
    protected Link compute() {

        List<LinkLoader> tasks = new ArrayList<>();
        try {
            List<Link> children = loadChildren();
            loadedLinks.addAll(children);
            for (Link child : children) {
                loadLink.addChild(child);
                LinkLoader childLinkLoader = new LinkLoader(child, rootLink);
                childLinkLoader.fork();
                tasks.add(childLinkLoader);
            }
            for (LinkLoader task : tasks) {
                task.join();
            }
            System.out.println("Loaded link: " + loadLink.getLinkName());
        } catch (IOException e) {
            System.err.println("Error loading link children: " + loadLink.toString().trim());
        }

        return loadLink;
    }

    private List<Link> loadChildren() throws IOException {

        List<Link> children = new ArrayList<>();

        Connection.Response response = Jsoup.connect(loadLink.getLinkName()).execute(); // выгружаем страницу по ссылке
        loadLink.setStatusCode(response.statusCode());
        Document siteHTML = response.parse();
        loadLink.setLinkBody(siteHTML.outerHtml());
        Elements linkElements = siteHTML.select("a[href]"); // выгружаем все ссылки со страницы
        linkElements.forEach(element -> {
            String link = element.attr("href");
            link = link.endsWith("/") ? link.substring(0, link.length() - 1) : link; // если ссылка заканчивается на /, убираем его
            if (!checkedLinks.contains(link) && !link.contains("#")) { // если ссылка уже проверена была выгружена или внутренняя
                checkedLinks.add(link);
                if (link.startsWith(loadLink.getLinkName())) {
                    children.add(new Link(link, "", 0));
                } else if (link.startsWith("/") || link.isEmpty()) { // если начинается со /, добавляем корневую ссылку для загрузки дальше
                    String formedLink = rootLink.getLinkName() + link;
                    if (!checkedLinks.contains(formedLink) && formedLink.startsWith(loadLink.getLinkName())) {
                        checkedLinks.add(formedLink);
                        Link newLink = new Link(formedLink, "", 0);
                        newLink.setShortLinkName(link.isEmpty() ? formedLink : link); // если ссылка корневая
                        children.add(newLink);
                    }
                }
            }
        });

        SystemUtils.interrupt(500L);

        return children;
    }
}
