package models;

import java.util.Arrays;

/**
 * Created by Tong on 7/9/2014.
 * Song.
 */
public class NetSong implements Comparable {
    private String id;
    private String title;
    private String img_url;
    private String summary;
    private String tracks;
    private String singer;
    private String publisher;
    private String[] track;
    private String[] tag = new String[100];
    private String pubdate;
    private String album;
    private String rating;
    private String audioURL;
    private String lrcURL;
    private String tagString;

    public NetSong(String title, String singer) {
        this.setTitle(title);
        this.setSinger(singer);
    }

    public String getTagString() {
        return tagString;
    }

    public void setTagString(String tagString) {
        this.tagString = tagString;
    }

    public String getAudioURL() {
        return audioURL;
    }

    public void setAudioURL(String audioURL) {
        this.audioURL = audioURL;
    }

    public String getLrcURL() {
        return lrcURL;
    }

    public void setLrcURL(String lrcURL) {
        this.lrcURL = lrcURL;
    }

    @Override
    public String toString() {
        String songString = "{";
        if (id != null)
            songString += "[id = " + id + "] ";
        if (title != null)
            songString += "[title = " + title + "] ";
        if (img_url != null)
            songString += "[img_url = " + img_url + "] ";
        if (audioURL != null)
            songString += "[audioURL = " + audioURL + "] ";
        if (lrcURL != null)
            songString += "[lrcURL = " + lrcURL + "] ";
        if (summary != null)
            songString += "[summary = " + summary + "] ";
        if (tracks != null)
            songString += "[tracks = " + tracks + "] ";
        if (singer != null)
            songString += "[singer = " + singer + "] ";
        if (publisher != null)
            songString += "[publisher = " + publisher + "] ";
        if (pubdate != null)
            songString += "[pubdate = " + pubdate + "] ";
        if (album != null)
            songString += "[album = " + album + "] ";
        if (rating != null)
            songString += "[rating = " + rating + "] ";
        if (tag != null) {
            songString += "[tag = " + id;
            for (String tags : tag) {
                if (tags != null)
                    songString += ", " + tags;
            }
            songString += "]";
        }
        songString += "}";

        return songString;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTracks() {
        return tracks;
    }

    public void setTracks(String tracks) {
        this.tracks = tracks;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String[] getTrack() {
        return track;
    }

    public void setTrack(String[] track) {
        this.track = track;
    }

    public String[] getTag() {
        return tag;
    }

    public void setTag(String[] tag) {
        this.tag = tag;
        this.tagString = Arrays.toString(tag);
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }


    @Override
    public int compareTo(Object o) {
        return this.hashCode() > o.hashCode() ? 1 : this.hashCode() < o.hashCode() ? -1 : 0;
    }
}
