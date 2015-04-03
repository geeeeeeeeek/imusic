imusic - Core back-end codes. A Music Search Engine(mse.jar) that implements music APIs from other platforms.

MusicSearchEngine(mse_v3.4_20140728)接口说明书
==================
*Copyright @ TongZhongyi*

##I.	调用说明
1.	运行本库的JDK版本必须在1.7.0.25或之上，否则不能成功。
2.	调用搜索时中文字符串需通过UT-8传输。凡经豆瓣API调用之所有中文统一采用UTF-8编码，搜索时getAllNetSongs(Keyword)已包装了一层显式的UTF-8至GBK转换。
3.	调用本库中方法时会有一定的延迟，主要是由网络连接产生。尤其是调用搜索时延迟较为严重。控制台不断有调试信息输出表示运行正常。
##II.	模块说明
>####**Class: NetSong**
*业务领域之网络歌曲(Fileds说明略)*
*注意 : 在调用本类各域时请做好非空判断	* 
>####Method/ Note/ Sample
**Constructor: NetSong(String, String)**	
NetSong的构造函数需要两个字符串，分别为歌名和歌手	
>>NetSong song = new NetSong("小苹果", "筷子兄弟");
>
**toString() : String**	
返回该NetSong各属性的字符串，用于调试。只有非null字段会被打印出来
>>{[id = 23369319] [title = 汪峰创作辑精选(HQCD)/CDB] [img_url = http://img3.douban.com/spic/s26243593.jpg] [singer = 汪峰] [rating = 8.4] [tag = 23369319, 汪峰, 内地, 摇滚, 精选集, 中国, 2011, 精选]}

>**compareTo(Object) : int**	
>比较两个NetSong的哈希值的hashcode，用于调试。返回1为大于，0为等于，-1为小于。
>>略

>**Getters and Setters**	
>所有域已封装，只能通过Getters和Setters调用或设置
>>song.setTitle("小情歌"); song.setSinger("苏打绿");


.
>####**Class: Keyword**
*业务领域之搜索关键字*
>####Fields
**q**: 搜索关键字，如”小苹果”
**tags**: 搜索标签，如”神曲”、”单曲”
**count**: 返回搜索结果数量，默认为”1”
**start**: 搜索结果偏移量，建议忽略
>####Method/ Note/ Sample
**Constructor: Keyword()**	
空构造函数
>>	Keyword keyword = new Keyword();
>
**Getters and Setters**	
所有域已封装，只能通过Getters和Setters调用或设置	
>>keyword.setQ("小苹果");
>> keyword.setCount("1");

.
>####**Class: MusicSearchEngine**
*音乐搜索引擎，可以按关键字、标签搜索歌曲信息，获取音频、歌词、热歌榜、新歌榜。*
	  
>####Method/ Note/ Sample
**Constructor: MusicSearchEngine ()**	
空构造函数	
>>MusicSearchEngine mse = new MusicSearchEngine();
>
**getAllSongs(Keyword) : NetSong[]**	
按关键字搜索歌曲。
传入参数Keyword的q/tags不得全为null。返回NetSong[]，若没有相关歌曲则为null。注意 : 返回的NetSong只有img_url/id/tag/
singer/title/rating/pudate/publisher/audioURL/lrcURL/tracks/album会被设置，其他Fields仍保持null。	
>>Keyword keyword = new Keyword();
>>keyword.setQ("小苹果");
>>keyword.setCount("1");
>>NetSong[] songs = mse.getAllSongs(keyword);

>**getAudioURL(NetSong): String **
@Depreated. 推荐使用getAudioAndLyricURL	搜索音频。根据传入NetSong的title和singer匹配mp3格式的音频。返回音频地址，若没有匹配到则为null。	
>>String path = mse.getAduioURL(song); 

>**getLyricURL (NetSong): String**
>@Depreated. 推荐使用getAudioAndLyricURL	搜索歌词。根据传入NetSong的title和singer匹配lrc格式的歌词。返回歌词地址，若没有匹配到则为null。	
>>String path = mse.getLyricURL(song);
>
>**getAudioAndLyricURL (NetSong): String[]**
搜索音频和歌词。根据传入NetSong的title和singer匹配lrc格式的歌词。返回二维数组，[0]为音频地址，[1]为歌词地址，若没有匹配到则为null。	
>>String[] path = mse.getAudioAndLyricURL(song);

>**getHitAllList(int) : NetSong[]	**
>搜索热歌榜。传入int为期望得到歌曲的数量，应该在0至100之间。返回NetSong[]歌曲列表。
>注意 : 返回的NetSong只有500和singer/audioURL/lrcURL会被设置，其他Fields保持null。lycURL可能会为null。
>>NetSong[] songs = mse.getHitAllList(10);
>
>**getHitNewNetSongList(int) : NetSong[]	**
>搜索新歌榜。传入int为期望得到歌曲的数量，应该在0至500之间。返回NetSong[]歌曲列表。
>注意 : 返回的NetSong只有title和singer/audioURL/lrcURL会被设置，其他Fields保持null。lycURL可能会为null。	
>>NetSong[] songs = mse.getHitNewNetSongList(10);
>
>**getRec(NetSong[], int) : NetSong[]**
新增	推荐相似歌曲	见下例
##III.	测试

    public class TestMusicSearchEngine {
        static NetSong song = new NetSong("Unset", "Unset");
    
        public static void main(String[] args) {
            MusicSearchEngine mse = new MusicSearchEngine();
            TestGetAllSong(mse);
            song.setTitle("摇滚本事");
            song.setSinger("五月天");
            TestGetAduioURL(mse, song);
            TestGetLyricURL(mse, song);
            TestGetHitAllList(mse);
            TestGetHitNewSongList(mse);
            TestGetRec(mse);
        }
    
        private static boolean TestGetAllSong(MusicSearchEngine mse) {
            Keyword keyword = new Keyword();
            String s = "小清新";
    //String[] ss = {"摇滚"};
    //keyword.setTags(ss);
            keyword.setQ(s);
            keyword.setCount("2");
            NetSong[] songs = mse.getAllSongs(keyword);
            if (songs == null) return false;
            for (NetSong song1 : songs) System.out.println(song1.toString());
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
            NetSong[] songs = mse.getHitAllList(2);
            for (NetSong s : songs) {
                System.out.println(s);
            }
            return true;
        }
    
        private static boolean TestGetHitNewSongList(MusicSearchEngine mse) {
            NetSong[] songs = mse.getHitNewSongList(2);
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
            NetSong[] recSongs = mse.getRec(songs, 3);
            for (NetSong s : recSongs) {
                System.out.println(s);
            }
            return true;
        }
    }

 
**控制台返回结果（略去了打印出的调试信息）**

    // Output for TestGetLyricURL
    http://zhangmenshiting2.baidu.com/data2/music/951382/951382.mp3?xcode=96c7fc8bcefe15d2fd7565267e8a466cbeba610873cbe8d0&mid=0.16996915761368
    // Output for TestGetAduioURL
    http://box.zhangmen.baidu.com/bdlrc/468/46867.lrc
    
    // Output for TestGetHitAllList
    {[title = 江南style] [audioURL = http://cdn.y.baidu.com/yinyueren/data2/music/64572/64572.mp3?xcode=96c7fc8bcefe15d234b8fa511994db431e21768ed2b7855f] [lrcURL = http://box.zhangmen.baidu.com/bdlrc/10716/1071605.lrc] [singer = Psy] [album = PSY 6甲 Part.1] [tag = null]}
    {[title = 没有什么不同] [audioURL = http://zhangmenshiting2.baidu.com/data2/music/39464332/39464332.mp3?xcode=96c7fc8bcefe15d2dd4ac5f4dca4a977ce8272ab73cdf358&mid=0.65014954556240] [singer = 曲婉婷] [album = 我的歌声里] [tag = null]}
    // Output for TestGetHitNewSongList
    {[title = 想你的夜] [audioURL = http://zhangmenshiting2.baidu.com/data2/music/35419599/35419599.mp3?xcode=96c7fc8bcefe15d249ece7a8eff4f320d6c9148c371d3012&mid=0.33521767404577] [lrcURL = http://box.zhangmen.baidu.com/bdlrc/8727/872720.lrc] [singer = 关喆] [album = 身边的故事] [tag = null]}
    {[title = 怒放的生命] [audioURL = http://zhangmenshiting2.baidu.com/data2/music/35420302/35420302.mp3?xcode=96c7fc8bcefe15d249ece7a8eff4f320598f0faef8e9c637&mid=0.19723829955844] [lrcURL = http://box.zhangmen.baidu.com/bdlrc/256/25635.lrc] [singer = 汪峰] [album = 北京青年 电视原声带] [tag = null]}
    
    // Output for TestGetRec
    {[id = 1499821] [title = Gimme Danger] [img_url = http://img3.douban.com/spic/s2890304.jpg] [audioURL = http://zhangmenshiting2.baidu.com/data2/music/3701737/3701737.mp3?xcode=96c7fc8bcefe15d29297e2ed3cfbeda9057f6ad431c0188d&mid=0.31834795237455] [tracks = Raw Power] [singer = Iggy Pop & The Stooges] [publisher = Columbia/Legacy] [pubdate = 2002] [album = Raw Power] [rating = 8.9] [tag = 1499821, IggyPop, punk, TheStooges, Proto-Punk, Rock, 美国, Iggy_Pop, Punk]}
    {[id = 1499821] [title = Shake Appeal] [img_url = http://img3.douban.com/spic/s2890304.jpg] [audioURL = http://zhangmenshiting2.baidu.com/data2/music/8893832/8893832.mp3?xcode=96c7fc8bcefe15d2bf5a34818f136fd13f6ced07b70c3d5a&mid=0.73853346162066] [tracks = Raw Power] [singer = Iggy Pop & The Stooges] [publisher = Columbia/Legacy] [pubdate = 2002] [album = Raw Power] [rating = 8.9] [tag = 1499821, IggyPop, punk, TheStooges, Proto-Punk, Rock, 美国, Iggy_Pop, Punk]}
    {[id = 1499821] [title = Search And Destroy] [img_url = http://img3.douban.com/spic/s2890304.jpg] [audioURL = http://zhangmenshiting2.baidu.com/data2/music/15729215/15729215.mp3?xcode=96c7fc8bcefe15d22eceb8937c60d2a7c180368a8702da8b&mid=0.82079654681126] [lrcURL = http://box.zhangmen.baidu.com/bdlrc/8776/877695.lrc] [tracks = Raw Power] [singer = Iggy Pop & The Stooges] [publisher = Columbia/Legacy] [pubdate = 2002] [album = Raw Power] [rating = 8.9] [tag = 1499821, IggyPop, punk, TheStooges, Proto-Punk, Rock, 美国, Iggy_Pop, Punk]}

##IV.	调试信息
在MSE运行时，ErrorHandler封装了各种可能出现的Java Exceptions，并且提供了详细的调试信息，来帮助调用者理解库的运行过程。需要指出的是，控制台打印出的Error仅供调试时分析，无需处理。
这段输出表示ErrorHandler开始运行：

    |||  Welcome to ErrorHandler.
    |||  I'm here to help you understand the process of MusicSearchEngine.
    |||  It is necessary to let you know some errors are harmless. Just ignore them.
    |||  // And of course, you are not expected to understand this.
    |||  You cannot see the above comment, can you?
    |||  Okay, any problem, feel free to contact TongZhongyi.
    

Error overview指定了错误类型，Error details会打印出和Error相关的过程信息。最常见的Error是SONG_DOES_NOT_EXIST_IN_DATABASE_ERROR，说明该歌曲在数据库中不存在。

    [0] Oops, an error occurred during the use of MusicSearchEngine.
    Error overview: SONG_DOES_NOT_EXIST_IN_DATABASE_ERROR
    Error details: song = {[id = 25737993] [title = 彩虹的你] [img_url = http://img5.douban.com/spic/s27057077.jpg] [tracks = 我的小清新] [singer = 陈升陈昇] [publisher = 滚石唱片] [pubdate = 2013-10-18] [album = 我的小清新] [rating = 6.7] [tag = 25737993, 陈升, 台湾, 2013, 老清新, 陳昇, Pop, 男声, 小清新]}

其他调试信息略。
 

##V.	更新记录
**mse_v3.4_20140728**
1. NetSong增加了 getTagString() : String方法

**mse_v3.3_20140726**
1. Changed 'durl' to 'url' in mp3 fetching. Hope to solve the problem.

**mse_v3.2_20140725**
1. 新增了getRec方法 推荐相似歌曲 NetSong[] 和 requestReturnNumber进 NetSong[]出
2. 新增了*.lrc文件不存在的检测

**mse_v3.1_20140725**
1. Calc hashcaode to do compareTo

**mse_v3.0_20140724**
1. Nothing changed. Just for test.

**mse_v2.9_20140724**
1. 修复了新歌/热歌榜返回大于请求数目歌曲的bug
2. 修复了某些情况下新歌/热歌榜返回歌曲中audioURL为空的bug

**mse_v2.8_20140724**
1. NetSong implemented "CompareTo" Method. @Return 1 for a higher rating, 0 for a lower rating, and -1 for the rating of one of the song being null, which normally won't happen.

**mse_v2.7_20140724**
1. 在keyword searching的时候加了一层UTF-8 to GBK wrapper.

**mse_v2.6_20140724**
1. 调整了编码到GBK

**mse_v2.5_20140723**
1. Small modification

**mse_v2.4_20140723**
1. 优化了MSE,大幅降低了搜索时间

**mse_v2.3_20140723**
1. Small modification

**mse_v2.2_20140722**
1. 修复了url问题

**mse_v2.1_20140721**
1. Changed Song to NetSong

**mse_v2.0_20140720**
1. Milestone version. The library becomes more stable and robust.

**mse_v1.0_20140715**
1. Most viable version.
