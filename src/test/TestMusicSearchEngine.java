package test;


import models.Keyword;
import models.NetSong;
import utils.MusicSearchEngine;

/**
 * Created by Tong on 7/15/2014.
 * Test for mse. Nothing important modified.
 */
public class TestMusicSearchEngine {
    static NetSong song = new NetSong("Unset", "Unset");

    public static void main(String[] args) {
        MusicSearchEngine mse = new MusicSearchEngine();
        TestGetAllSong(mse);
//        song.setTitle("摇滚本事");
//        song.setSinger("五月�?);
//        TestGetAduioURL(mse, song);
//        TestGetLyricURL(mse, song);
//        TestGetHitAllList(mse);
//        TestGetHitNewSongList(mse);
//        TestGetRec(mse);
    }

    private static boolean TestGetAllSong(MusicSearchEngine mse) {
        Keyword keyword = new Keyword();
        String s = "小苹果";
//        String[] ss = {"摇滚"};
//        keyword.setTags(ss);
        keyword.setQ(s);
        keyword.setCount("20");
        NetSong[] songs = mse.getAllSongs(keyword);
        if (songs == null) return false;
        for (NetSong song1 : songs) System.out.println(song1.getTagString());
        return true;
    }

    private static boolean TestGetAduioURL(MusicSearchEngine mse, NetSong song) {
        String path = mse.getAduioURL(song);
        System.out.println(path);
        return true;
    }

    private static boolean TestGetLyricURL(MusicSearchEngine mse, NetSong song) {
        String path = mse.getLyricURL(song);
        System.out.println(path);
        return true;
    }

    private static boolean TestGetHitAllList(MusicSearchEngine mse) {
        NetSong[] songs = mse.getHitAllList(10);
        for (NetSong s : songs) {
            System.out.println(s);
        }
        return true;
    }

    private static boolean TestGetHitNewSongList(MusicSearchEngine mse) {
        NetSong[] songs = mse.getHitNewSongList(10);
        for (NetSong s : songs) {
            System.out.println(s);
        }
        return true;
    }

    private static boolean TestGetRec(MusicSearchEngine mse) {
        Keyword keyword = new Keyword();
        keyword.setQ("汪峰");
        keyword.setCount("5");
        NetSong[] songs = mse.getAllSongs(keyword);
        if (songs == null) return false;
        NetSong[] recSongs = mse.getRec(songs, 5);
        for (NetSong s : recSongs) {
            System.out.println(s);
        }
        return true;
    }
}

