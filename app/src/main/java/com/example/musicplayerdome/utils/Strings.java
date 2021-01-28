package com.example.musicplayerdome.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Strings {
    public static final String PLAY_MODE_KEY="myMusic.playModel";

    //欢迎界面资源获取（one一个）
    public static final String ONE_API="http://api.youngam.cn/api/one.php";

    //简易音乐播放地址+音乐id
    public static final String SIMPLE_PLAY_MUSIC_URL="http://www.foxluo.cn/music.html?id=";

    //歌词迷在线获取歌词信息+（歌名+歌手）
    public static final String GET_SONG_LRC_API="http://gecimi.com/api/lyric/";

    //检查更新
    public static final String APP_UPDATE_API="http://www.foxluo.cn/myapp/AppUpdate?version=";
    //+version

    //limit范围，offset翻页（limit*pageNum）

    //搜索歌曲
    public static final String YUN_SEARCH_SONG_API="https://api.imjad.cn/cloudmusic/?type=search&search_type=1&s=";
    public static final String TEST_YUN_SEARCH_SONG_API="http://music.163.com/api/search/get?type=1&s=";

    //搜索专辑
    public static final String YUN_SEARCH_ALBUM_API="http://music.163.com/api/search/get?type=10&s=";

    //搜索歌单
    public static final String YUN_SEARCH_LIST_API="http://music.163.com/api/search/get?type=1000&s=";

    //首页轮播图
    public static final String YUN_BANNER_API="http://music.163.com/api/banner/get";

    //歌曲详情
    public static final String YUN_MUSIC_DETAILS_API="http://music.163.com/api/song/detail/?id=";

    //排行榜获取
    public static final String YUN_RANK_LIST_API="http://music.163.com/api/toplist/detail";

    //推荐歌单
    public static final String YUN_RECOMMEND_LIST_API="http://music.163.com/api/playlist/list?cat=全部&order=hot&total=true&limit=";

    //新碟
    public static final String NEW_ALBUM_API="http://music.163.com/api/album/new?area=ALL&total=false&limit=";

    //专辑详情 +id
    public static final String YUN_ALBUM_DETAILS_API="http://music.163.com/api/album/";

    //歌单详情
    public static final String YUN_PLAYLIST_DETAILS_API="http://music.163.com/api/playlist/detail?quot&id=";

    //网易云获取歌词信息
    public static final String YUN_GET_LRC_API="https://api.imjad.cn/cloudmusic/?type=lyric&id=";
    public static final String YUN_GET_FORMAT_LRC_API="http://music.163.com/api/song/lyric?os=osx&lv=-1&kv=-1&tv=-1&id=";

    //获取评论信息
    public static final String YUN_SONG_COMMENTS_API="https://api.imjad.cn/cloudmusic/?type=comments&id=";
    public static final String YUN_SONG_FORMAT_COMMENTS_API="https://v1.hitokoto.cn/nm/comment/music/";
    //28391863?offset=0&limit=100

    //百度音乐搜索简易api
    public static final String BAIDU_SEARCH_SUGGESTION_API="http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.search.suggestion&format=json&from=ios&version=2.1.1&query=";

    /**
     * 功能描述: 去掉括号里面的内容
     * @param: [context]
     * @return: java.lang.String
     */
    public static String ClearBracket(String context) {
        int head = context.indexOf('['); // 标记第一个使用左括号的位置
        if (head == -1) {
            return context; // 如果context中不存在括号，什么也不做，直接跑到函数底端返回初值str
        } else {
            int next = head + 1; // 从head+1起检查每个字符
            int count = 1; // 记录括号情况
            do {
                if (context.charAt(next) == '[') {
                    count++;
                } else if (context.charAt(next) == ']') {
                    count--;
                }
                next++; // 更新即将读取的下一个字符的位置
                if (count == 0) { // 已经找到匹配的括号
                    String temp = context.substring(head, next);
                    context = context.replace(temp, ""); // 用空内容替换，复制给context
                    head = context.indexOf('['); // 找寻下一个左括号
                    next = head + 1; // 标记下一个左括号后的字符位置
                    count = 1; // count的值还原成1
                }
            } while (head != -1); // 如果在该段落中找不到左括号了，就终止循环
        }
        return context; // 返回更新后的context
    }
    /**
     * 保存文件到本地
     * @throws Exception
     */
    public static boolean writeOcrStrtoFile(String result, String outPath, String outFileName){
        FileOutputStream fos = null;
        try {
            File dir = new File(outPath);
            if(!dir.exists()) {
                dir.mkdirs();
            }
            Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");
            Matcher matcher = pattern.matcher(outFileName);
            outFileName= matcher.replaceAll("");
            File txt = new File(outPath + "/" + outFileName);
            if (!txt.exists()) {
                txt.createNewFile();
            }
            byte bytes[] =  result.getBytes();
            int b = bytes.length; // 是字节的长度，不是字符串的长度
            fos = new FileOutputStream(txt);
            fos.write(bytes);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            outFileName="temp.lrc";
        }finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ioe) {
                System.out.println("Error in closing the Stream");
            }
        }
        return true;
    }

    private static String unicodeToCn(String unicode) {
        /** 以 \ u 分割，因为java注释也能识别unicode，因此中间加了一个空格*/
        String[] strs = unicode.split("\\\\u");
        String returnStr = "";
        // 由于unicode字符串以 \ u 开头，因此分割出的第一个字符是""。
        for (int i = 1; i < strs.length; i++) {
            returnStr += (char) Integer.valueOf(strs[i], 16).intValue();
        }
        return returnStr;
    }
}
