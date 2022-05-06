import java.util.ArrayList;

public class Link {

    private String linkName;
    private String shortLinkName;
    private String linkBody;
    private int statusCode;
    private ArrayList<Link> children;
    private int level;

    public Link(String linkName, String linkBody, int statusCode) {
        this.linkName = linkName.endsWith("/") ? linkName.substring(0, linkName.length() - 1) : linkName;
        shortLinkName = this.linkName;
        this.linkBody = linkBody;
        this.statusCode = statusCode;
        this.children = new ArrayList<>();
    }

    public void addChild(Link link) {
        link.setLevel(level + 1);
        children.add(link);
    }

    public String getLinkName() {
        return linkName;
    }

    public String getShortLinkName() {
        return shortLinkName;
    }

    public void setShortLinkName(String shortLinkName) {
        this.shortLinkName = shortLinkName;
    }

    public String getLinkBody() {
        return linkBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setLinkBody(String linkBody) {
        this.linkBody = linkBody;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(linkName).append("\n");
        for (Link childLink : children) {
            builder.append("\t".repeat(childLink.getLevel())).append(childLink);
        }
        return builder.toString();
    }
}
