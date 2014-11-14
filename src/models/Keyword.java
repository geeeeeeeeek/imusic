package models;

/**
 * Created by Tong on 7/12/2014.
 * Keyword.
 */
public class Keyword {

    private String q;
    private String[] tags;
    private String start;
    private String count = "1";

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tag) {
        this.tags = tag;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }
}
