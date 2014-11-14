package utils;

import models.Keyword;
import models.NetSong;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by Tong on 7/8/2014.
 * Last modified by Tong on 7/20/2014.
 */
public class MusicSearchEngine {

    public NetSong[] getAllSongs(Keyword keyword) {
        int num = Integer.parseInt(keyword.getCount());

        String path = keywordToPath(keyword);

        // NOTICE: Other parameters ignored.

        DataResolver dataResolver = new DataResolver(path);
        dataResolver.get("UTF-8");
        List<NetSong> allSongs = dataResolver.resolveAsSongs(num);
        if (allSongs.size() == 0)
            return null;

        List<NetSong> finalSongs = new ArrayList<NetSong>();

        int ptr;
        String[] urls;

        while (finalSongs.size() < num && allSongs.size() > 0) {
            ptr = (int) (Math.random() * allSongs.size());
            urls = getAudioAndLyricURL(allSongs.get(ptr));

            if (urls == null) {
                allSongs.remove(ptr);
                continue;
            }

            allSongs.get(ptr).setAudioURL(urls[0]);
            allSongs.get(ptr).setLrcURL(urls[1]);
            finalSongs.add(allSongs.get(ptr));
            allSongs.remove(ptr);
        }

        return finalSongs.toArray(new NetSong[finalSongs.size()]);
    }


    private String keywordToPath(Keyword keyword) {
        String path = "https://api.douban.com/v2/music/search?";
        String query;
        String[] tags;
        int num = Integer.parseInt(keyword.getCount());
        boolean isKeywordVaild = false;
        if ((query = keyword.getQ()) != null) {
            query = encode(query);
            path += "q=" + query;
            isKeywordVaild = true;
        }
        if ((tags = keyword.getTags()) != null) {
            for (String tag : tags) {
                tag = encode(tag);
                if (!isKeywordVaild)
                    path += "&&";
                path += "tag=" + tag;
                isKeywordVaild = true;
            }
        }
        path += "&&count=" + num;
        return path;
    }

    private String encode(String utf_8) {
        try {
            return new String(utf_8.getBytes("utf-8"), "gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getAduioURL(NetSong song) {
        /**
         * Streaming media at Baidu Music follows the rule:
         * http://box.zhangmen.baidu.com/x?op=12&count=1&title=[????]$$[????]$$$$
         * */

        String title = song.getTitle();
        String singer = song.getSinger();
        if (title == null || singer == null) {
            ErrorHandler.deal(10, "song = " + song.toString());
        }
        String path = "http://box.zhangmen.baidu.com/x?op=12&count=1&title=" + title + "$$" + singer + "$$$$";
        DataResolver dataResolver = new DataResolver(path);
        if (!dataResolver.get("GBK"))
            return null;
        String audio = dataResolver.resolveAsAudio();
        if (audio == null) ErrorHandler.deal(8, "song = " + song.toString());
        return audio;
    }

    public String getLyricURL(NetSong song) {
        /**
         * Lyrics at Baidu Music follows the rule:
         * http://box.zhangmen.baidu.com/bdlrc/[lrcid / 100]/[lrcid].lrc
         * */

        String title = song.getTitle();
        String singer = song.getSinger();
        if (title == null || singer == null) {
            ErrorHandler.deal(10, "song = " + song.toString());
        }
        String path = "http://box.zhangmen.baidu.com/x?op=12&count=1&title=" + song.getTitle() + "$$" + song.getSinger() + "$$$$";
        DataResolver dataResolver = new DataResolver(path);
        if (!dataResolver.get("GBK"))
            return null;

        String lyric = dataResolver.resolveAsLyric();
        if (lyric == null) ErrorHandler.deal(8, "song = " + song.toString());
        return lyric;
    }

    public String[] getAudioAndLyricURL(NetSong song) {
        /**
         * Lyrics at Baidu Music follows the rule:
         * http://box.zhangmen.baidu.com/bdlrc/[lrcid / 100]/[lrcid].lrc
         * */

        String title = song.getTitle();
        String singer = song.getSinger();
        if (title == null || singer == null) {
            ErrorHandler.deal(10, "song = " + song.toString());
        }
        String path = "http://box.zhangmen.baidu.com/x?op=12&count=1&title=" + song.getTitle() + "$$" + song.getSinger() + "$$$$";
        DataResolver dataResolver = new DataResolver(path);
        if (!dataResolver.get("GBK"))
            return null;

        String[] result = dataResolver.resolveAsAduioAndLyric();
        if (result == null) ErrorHandler.deal(8, "song = " + song.toString());
        return result;
    }

    public NetSong[] getHitAllList(int num) {
        /**

         */
        String path = "http://music.qq.com/musicbox/shop/v3/data/hit/hit_all.js";
        DataResolver dataResolver = new DataResolver(path);
        dataResolver.get("GBK");
        List<NetSong> allSongs = dataResolver.resolveAsHitList(num);
        String[] audioAndLyric;
        for (int index = 0, ptr = 0; index < num && ptr < num * 2;
             index++) {
            audioAndLyric = getAudioAndLyricURL(allSongs.get(index));
            if (audioAndLyric == null) {
                allSongs.remove(index);
                index--;
                continue;
            }
            allSongs.get(index).setAudioURL(audioAndLyric[0]);
            allSongs.get(index).setLrcURL(audioAndLyric[1]);
        }
        while (allSongs.size() > num)
            allSongs.remove(allSongs.size() - 1);
        return allSongs.toArray(new NetSong[num]);
    }

    public NetSong[] getHitNewSongList(int num) {
        String path = "http://music.qq.com/musicbox/shop/v3/data/hit/hit_newsong.js";
        DataResolver dataResolver = new DataResolver(path);
        dataResolver.get("GBK");
        List<NetSong> allSongs = dataResolver.resolveAsHitList(num);
        String[] audioAndLyric;
        for (int index = 0, ptr = 0; index < num && ptr < num * 2;
             index++) {
            audioAndLyric = getAudioAndLyricURL(allSongs.get(index));
            if (audioAndLyric == null) {
                allSongs.remove(index);
                index--;
                continue;
            }
            allSongs.get(index).setAudioURL(audioAndLyric[0]);
            allSongs.get(index).setLrcURL(audioAndLyric[1]);
        }
        while (allSongs.size() > num)
            allSongs.remove(allSongs.size() - 1);
        return allSongs.toArray(new NetSong[num]);
    }

    public NetSong[] getRec(NetSong[] origSongs, int num) {
        /**
         * Recommend music to user according to the favorite song list.
         */
        Map<String, Integer> tagMap = new HashMap<String, Integer>();
        for (NetSong song : origSongs) {
            String[] tags = song.getTag();
            for (String tag : tags) {
                if (tag == null)
                    break;
                if (tagMap.containsKey(tag))
                    tagMap.put(tag, tagMap.get(tag) + 1);
                else
                    tagMap.put(tag, 1);
            }
        }

        Set<String> key = tagMap.keySet();

        // Get three most frequently occurred tags.
        String[] recTags = tagMap.size() > 2 ? new String[2] : new String[tagMap.size()];
        int[] values = tagMap.size() > 2 ? new int[2] : new int[tagMap.size()];
        for (int i = 0; i < recTags.length; i++) {
            int maxValue = 0;
            String maxString = null;
            for (String tag : key) {
                int value = tagMap.get(tag);
                if (value > maxValue) {
                    maxValue = value;
                    maxString = tag;
                }
            }
            recTags[i] = maxString;
            values[i] = maxValue;
            tagMap.remove(maxString);
        }
        int valueSum = 0, tempSum = 0;
        for (int value : values) valueSum += value;
        for (int i = 1; i < values.length; i++) {
            tempSum += values[i] /= valueSum / num;
        }
        values[0] = num - tempSum;
        Keyword keyword = new Keyword();
        List<NetSong> recSongs = new ArrayList<NetSong>();
        recSongs.add(new NetSong("", ""));
        for (int i = 0; i < recTags.length; i++) {
            keyword.setQ(recTags[i]);
            keyword.setCount("" + values[i]);
            NetSong[] temp = getAllSongs(keyword);
            if (temp != null) recSongs.addAll(Arrays.asList(temp));
        }
        keyword.setQ("pop");
        keyword.setCount("" + (num - recSongs.size() + 1));
        NetSong[] temp = getAllSongs(keyword);
        if (temp != null) recSongs.addAll(Arrays.asList(temp));
        recSongs.remove(0);
        return recSongs.toArray(new NetSong[num]);
    }


}


