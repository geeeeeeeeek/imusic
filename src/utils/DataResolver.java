package utils;


import models.NetSong;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tong on 7/9/2014.
 * Last modified by Tong on 7/20.2014.
 * The DataResolver class handles all request from MusicSearchEngine in a data level way,
 * mainly involves XML/JSON document, etc.
 * The last modification optimized possible exceptions and resolved singles from albums.
 */

public class DataResolver {

    private String documentString;
    private URL source;

    public DataResolver(String urlPath) {
        /**
         * Constructor. Take urlPath as the unique identifier of one document.
         */
        // Cast ' ' characters into '%20', or URL encoding will run into problem
        for (int i = 0; i < urlPath.length(); i++) {
            if (urlPath.charAt(i) == ' ') {
                urlPath = urlPath.substring(0, i) + "%20" + urlPath.substring(i + 1, urlPath.length());
            }
        }
        try {
            this.source = new URL(urlPath);
        } catch (MalformedURLException e) {
            ErrorHandler.deal(5, "urlPath = " + urlPath + "\n");
//            e.printStackTrace();
        }
    }


    private Document getXMLDocFromString(String str) {
        /**
         * Cast XML document to string.
         */
        try {
            return DocumentHelper.parseText(str);
        } catch (DocumentException e) {
            ErrorHandler.deal(3, "The document str = " + str);
            return null;
        }
    }

    public boolean get(String encoding) {
        /**
         * Establish connection to the source, and fetch the xml document.
         * */
        try {
            HttpURLConnection connection = (HttpURLConnection) source.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            // Convert code
            Reader reader = new InputStreamReader(inputStream, encoding);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String str;
            StringBuilder sb = new StringBuilder();
            while ((str = bufferedReader.readLine()) != null) {
                sb.append(str);
            }
            reader.close();
            connection.disconnect();
            documentString = sb.toString();
        } catch (Exception e) {
            ErrorHandler.deal(2, "Try again? source = " + source.toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Deprecated
    public String resolveAsAudio() {
        /**
         * Streaming media at Baidu Music follows the rule:
         * "http://box.zhangmen.baidu.com/x?op=12&count=1&title=[Song]$$[Singer]$$$$"
         * @rtn the url of audio if successful, or null.
         * */

        Document xmlDocument = getXMLDocFromString(documentString);
        String encode, decode;
        try {
            Element root = xmlDocument.getRootElement();

            int count = Integer.parseInt(root.elementText("count"));
            // If audio not found, return.
            if (count == 0) {
                ErrorHandler.deal(8);
                return null;
            }

            Element element = (Element) root.elementIterator("url").next();
            encode = element.elementText("encode");
            decode = element.elementText("decode");
        } catch (Exception e) {
            // Make all exceptions user invisible
            ErrorHandler.deal(6);
            return null;
        }
        // If audio not found, return.
        if (encode == null || decode == null) {
            ErrorHandler.deal(6);
            return null;
        }
        int maxPtr = encode.length() - 1;
        while (true) {
            if (encode.charAt(maxPtr) == '/') break;
            maxPtr--;
        }
        return encode.substring(0, maxPtr + 1) + decode;

    }

    @Deprecated
    public String resolveAsLyric() {
        /**
         * Lyrics at Baidu Music follows the rule:
         * "http://box.zhangmen.baidu.com/bdlrc/[lrcId / 100]/[lrcId].lrc"
         * @rtn the url of lyric if successful, or null.
         * */

        Document xmlDocument = getXMLDocFromString(documentString);

        int lrcId;
        try {
            Element root = xmlDocument.getRootElement();
            int count = Integer.parseInt(root.elementText("count"));
            // If lyric not found, return.
            if (count == 0) {
                ErrorHandler.deal(8);
                return null;
            }
            Element element = (Element) root.elementIterator("url").next();

            lrcId = Integer.parseInt(element.elementText("lrcid"));
        } catch (Exception e) {
            // Make all exceptions user invisible.
            ErrorHandler.deal(7);
            return null;
        }

        return lrcId == 0 ? null : "http://box.zhangmen.baidu.com/bdlrc/" + (lrcId / 100) + "/" + (lrcId) + ".lrc";

    }

    public String[] resolveAsAduioAndLyric() {
        /**
         * Combine audio and lyric resolution in one method,
         * which only needs to resolve XML document one time,
         * with an improvement of efficiency
         * @rtn the url of audio and lyric if successful, or null.
         * */

        String[] out = new String[2];
        Document xmlDocument = getXMLDocFromString(documentString);
        String encode, decode;
        int lrcId;
        try {
            Element root = xmlDocument.getRootElement();

            int count = Integer.parseInt(root.elementText("count"));
            // If lyric not found, return.
            if (count == 0) {
                return null;
            }
            // Changed from url to url in version 3.3
            Element element = (Element) root.elementIterator("url").next();

            encode = element.elementText("encode");
            decode = element.elementText("decode");

            element = (Element) root.elementIterator("url").next();

            lrcId = Integer.parseInt(element.elementText("lrcid"));
        } catch (Exception e) {
            // Make all exceptions user invisible.
            ErrorHandler.deal(6);
            return null;
        }
        // If audio not found, return.
        if (encode == null || decode == null) {
            ErrorHandler.deal(6);
            return null;
        }
        int maxPtr = encode.length() - 1;
        while (encode.charAt(maxPtr) != '/') maxPtr--;

        out[0] = encode.substring(0, maxPtr + 1) + decode;
        out[1] = lrcId == 0 ? null : "http://box.zhangmen.baidu.com/bdlrc/" + (lrcId / 100) + "/" + (lrcId) + ".lrc";
        return out;
    }

    public List<NetSong> resolveAsHitList(int num) {
        /**
         * Hit list(including hit_all and hit_newsongs) at QQ Music can be fetched from
         * "http://music.qq.com/musicbox/shop/v3/data/hit/hit_all.js"
         * and "http://music.qq.com/musicbox/shop/v3/data/hit/hit_newsong.js"
         * @rtn a list of objects Song or null if failed to resolve list.
         * */
        String entry = documentString;
        String[] words = entry.split("\"");
        List<NetSong> songs = new ArrayList<NetSong>(num * 2);
        for (int index = 0; index < num * 2; index++) {
            songs.add(new NetSong("Unset", "Unset"));
        }
        /*
        Iterate the whole string and fetch songs with the limit of num.
        Each Song object is assigned with songName, singerName, and albumName only.
        Other properties remain null.
          */
        int index = 0;
        for (int ptr = 0; ptr < (words.length - 1) && index < num * 2;
             ptr++) {
            if (", songName:".equals(words[ptr])) {
                songs.get(index).setTitle(words[ptr + 1]);
            }
            if (", singerName:".equals(words[ptr])) {
                songs.get(index).setSinger(words[ptr + 1]);
            }
            if (", albumName:".equals(words[ptr])) {
                songs.get(index).setAlbum(words[ptr + 1]);
                index++;
            }
            //UNFINISHED: Needs to exclude songs that do not exist in Baidu's database
        }
        return songs;
    }

    public List<NetSong> resolveAsSongs(int num) {
        /**
         * Information to construct a Song object can be fetched using Douban Music API as:
         * https://api.douban.com/v2/music/search
         *
         * A sample song is represented as follows:
         * {"count":1,"start":0,"total":1,
         * "musics":[
         * {"rating":{"max":10,"average":"6.0","numRaters":1170,"min":0},
         *  "author":[{"name":"筷子兄弟"}],
         *  "alt_title":"",
         *  "image":"http:\/\/img3.douban.com\/spic\/s27297383.jpg",
         *  "tags":[{"count":241,"name":"筷子兄弟"},
         *      {"count":130,"name":"神曲"},
         *      {"count":93,"name":"中国"},
         *      {"count":78,"name":"2014"},
         *      {"count":69,"name":"Pop"},
         *      {"count":56,"name":"大陆"},
         *      {"count":51,"name":"内地"},
         *      {"count":47,"name":"单曲"}],
         *  "mobile_link":"http:\/\/m.douban.com\/music\/subject\/25900422\/",
         *  "attrs":{"publisher":["儒意欣欣 \/ 乐视影业"],
         *      "singer":["筷子兄弟"],"pubdate":["2014-05-29"],
         *      "title":["小苹果"],
         *      "media":["数字(Digital)"],
         *      "tracks":["1.小苹果    电影《老男孩之猛龙过江》宣传曲"],
         *      "version":["单曲"]},
         *  "title":"小苹果",
         *  "alt":"http:\/\/music.douban.com\/subject\/25900422\/",
         *  "id":"25900422"}]}
         *
         * @rtn a list of objects Song or null if failed to resolve list.
         * */

        // Search 5 times of the requested number of results.
        final int LIMIT = num * 5;
        List<NetSong> allSongs = new ArrayList<NetSong>();
        // Initialize allSongs
        for (int index = 0; index < allSongs.size(); index++)
            allSongs.add(new NetSong("Unset", "Unset"));

        JSONObject document;
        try {
            document = JSONObject.fromObject(documentString);
        } catch (Exception e) {
            // Exception is most possibly occurred when documentString is null
            ErrorHandler.deal(4, "documentString = " + documentString);
            e.printStackTrace();
            return null;
        }

        int songIndex = 0;
        try {
            // Normally, there is only one "musics" attribute in the document.
            JSONArray musics = document.getJSONArray("musics");

            /*
            Iterate the whole string and fetch songs with the limit of num.
            Each Song object is assigned with all properties
            Other properties remain null.
              */

            for (int entryIndex = 0; songIndex < LIMIT; entryIndex++) {
                // Each entry.
                JSONObject item;
                try {
                    item = musics.getJSONObject(entryIndex);
                } catch (Exception e) {
                    // Exception is most possibly occurred when there is no more entry. Then return
                    return allSongs;
                }
                String rating, author, img_url, id;
                String[] tag = new String[100];
                JSONArray titleAttr, tracksArr, publisherAttr, pubdateAttr;
                try {
                    /* 0.Set rating */
                    rating = item.getJSONObject("rating").getString("average");

                    /* 1.Set author */
                    JSONArray authors = item.getJSONArray("author");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < authors.size(); i++) {
                        sb.append(authors.getJSONObject(i).getString("name"));
                    }
                    author = sb.toString();

                    /* 2.Set img_url */
                    img_url = Arrays.toString(item.getString("image").split("\\\\"));
                    img_url = img_url.substring(1, img_url.length() - 1);

                    /* 3.Set tags */
                    JSONArray tags = item.getJSONArray("tags");

                    for (int i = 0; i < tags.size() && i < 100; i++)
                        tag[i] = tags.getJSONObject(i).getString("name");

                    /* 4. Set id */
                    id = item.getString("id");

                    /* 5. Set title */
                    JSONObject attrs = item.getJSONObject("attrs");
                    publisherAttr = attrs.getJSONArray("publisher");
                    pubdateAttr = attrs.getJSONArray("pubdate");
                    titleAttr = attrs.getJSONArray("title");
                    tracksArr = attrs.getJSONArray("tracks");
                } catch (Exception e) {
                    // If current song entry has some format fault, continue to the next.
                    continue;
                }
                String title = titleAttr.getString(0);
                title = reformatTrack(title);
                if (title != null) {
                    allSongs.add(new NetSong(title, author));
                    allSongs.get(songIndex).setRating(rating);
                    allSongs.get(songIndex).setImg_url(img_url);
                    allSongs.get(songIndex).setTag(tag);
                    allSongs.get(songIndex).setId(id);
                    songIndex++;
                }
                String[] tracks = null;
                for (int i = 0; i < tracksArr.size(); i++) {
                    String[] temp = tracksArr.getString(i).split("[\\\\\",\\.\\\\[0-9]()]+");
                    tracks = (String[]) ArrayUtils.addAll(tracks, temp);
                }
                if (tracks == null) {
                    // If there is "tracks" in this entry
                    ErrorHandler.deal(8);
                    continue;
                }


                for (String track : tracks) {
                    try {
                        track = reformatTrack(track);
                        if (track == null)
                            continue;
                        allSongs.add(new NetSong(track, author));
                        allSongs.get(songIndex).setRating(rating);
                        allSongs.get(songIndex).setImg_url(img_url);
                        allSongs.get(songIndex).setTag(tag);
                        allSongs.get(songIndex).setId(id);
                        allSongs.get(songIndex).setTracks(titleAttr.getString(0));
                        allSongs.get(songIndex).setAlbum(title);
                        allSongs.get(songIndex).setPublisher(publisherAttr.getString(0));
                        allSongs.get(songIndex).setPubdate(pubdateAttr.getString(0));
                    } catch (Exception e) {
                        // Ignore
                        continue;
                    }
                    if (songIndex++ == LIMIT)
                        break;
                }
                // Give up the rest attrs.
            }
        } catch (Exception e) {
            ErrorHandler.deal(9, "Is your request song number too large?");
            e.printStackTrace();
        }
        return allSongs;
    }

    private String reformatTrack(String track) {
        String[] forbiddenString = {"-", "", " ", "  ", "   ", "\n"};
        for (String each : forbiddenString) {
            if (track.equals(each))
                return null;
        }

        int i = 0;
        while (track.charAt(i) == ' ' && i < track.length())
            i++;
        track = track.substring(i);

        for (i = 0; i < track.length(); i++)
            if (track.charAt(i) == '\n') {
                track = track.substring(0, i) + track.substring(i + 1);
                i--;
            }
        return track;
    }
}
