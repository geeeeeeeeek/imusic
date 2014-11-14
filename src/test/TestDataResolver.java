package test;

import models.NetSong;
import utils.DataResolver;

import java.util.List;

/**
 * Created by Tong on 7/15/2014.
 * Test DataResolver. Nothing important modified.
 */
public class TestDataResolver {

    public static void main(String[] args) {
        NetSong song = new NetSong("Unset", "Unset");
        song.setTitle("小情歌");
        song.setSinger("苏打绿");
        testFetchingAudio(song);
        testFetchingLyric(song);
        testFetchingHitList("all");
        testFetchingHitList("new");
        testFetchingMusicInfo();
    }

    private static boolean testFetchingAudio(NetSong song) {
        /**
         * Streaming media at Baidu Music follows the rule:
         * http://box.zhangmen.baidu.com/x?op=12&count=1&title=[歌名]$$[歌手名]$$$$
         * */

        String path = "http://box.zhangmen.baidu.com/x?op=12&count=1&title=" + song.getTitle() + "$$" + song.getSinger() + "$$$$";
        DataResolver dataResolver = new DataResolver(path);
        dataResolver.get("GBK");
        String audio = dataResolver.resolveAsAudio();
        System.out.println(audio);
        return true;
    }

    private static boolean testFetchingLyric(NetSong song) {
        /**
         * Lyrics at Baidu Music follows the rule:
         * http://box.zhangmen.baidu.com/bdlrc/[lrcid / 100]/[lrcid].lrc
         * */

        String path = "http://box.zhangmen.baidu.com/x?op=12&count=1&title=" + song.getTitle() + "$$" + song.getSinger() + "$$$$";
        DataResolver dataResolver = new DataResolver(path);
        dataResolver.get("GBK");
        String lyric = dataResolver.resolveAsLyric();
        System.out.println(lyric);
        return true;
    }

    private static boolean testFetchingHitList(String type) {
        String path = null;
        if (type.equals("all")) {
            path = "http://music.qq.com/musicbox/shop/v3/data/hit/hit_all.js";
        }
        if (type.equals("new")) {
            path = "http://music.qq.com/musicbox/shop/v3/data/hit/hit_newsong.js";
        }

        DataResolver dataResolver = new DataResolver(path);
        dataResolver.get("GBK");
        int NUM = 5;
        List<NetSong> songs = dataResolver.resolveAsHitList(NUM);
        for (NetSong song : songs) {
            System.out.println(song.getTitle() + " " + song.getSinger() + " " + song.getAlbum());
        }
        return true;
    }

    private static boolean testFetchingMusicInfo() {
        String path = "https://api.douban.com/v2/music/search?q=%E5%B0%8F%E8%8B%B9%E6%9E%9C";
        DataResolver dataResolver = new DataResolver(path);
        dataResolver.get("UTF-8");
        dataResolver.resolveAsSongs(1);
        return true;
    }
}
