package com.example.musicplayerdome.utils;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.musicplayerdome.bean.AlbumResultBean;
import com.example.musicplayerdome.bean.AlbumSearchBean;
import com.example.musicplayerdome.bean.AppVersionBean;
import com.example.musicplayerdome.bean.BannerAlbumResultBean;
import com.example.musicplayerdome.bean.BannerBean;
import com.example.musicplayerdome.bean.HotListResultBean;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.bean.MusicCommentsResultBean;
import com.example.musicplayerdome.bean.MusicCommentsTempBean;
import com.example.musicplayerdome.bean.MusicResultBean;
import com.example.musicplayerdome.bean.MyAlbumBean;
import com.example.musicplayerdome.bean.MyMusicSearchBean;
import com.example.musicplayerdome.bean.MyPlayListBean;
import com.example.musicplayerdome.bean.NewAlbumResultBean;
import com.example.musicplayerdome.bean.OneBean;
import com.example.musicplayerdome.bean.PlayListResultBean;
import com.example.musicplayerdome.bean.PlayListResultTempBean;
import com.example.musicplayerdome.bean.PlayListSearchBean;
import com.example.musicplayerdome.bean.SongSearchFormatBean;
import com.example.musicplayerdome.bean.TopListBean;
import com.example.musicplayerdome.bean.TopListResultBean;
import com.example.musicplayerdome.bean.YunSearchResult;
import com.example.musicplayerdome.bean.YunSongLrcBean;
import com.example.musicplayerdome.bean.YunSongLrcFormatBean;
import com.example.musicplayerdome.dao.SearchDao;
import com.example.musicplayerdome.service.MusicService;
import com.example.musicplayerdome.ui.activity.DetailsActivity;
import com.example.musicplayerdome.ui.activity.HomeActivity;
import com.lauzy.freedom.library.Lrc;
import com.lauzy.freedom.library.LrcHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.musicplayerdome.utils.DownloadLrcUtils.downloadLrc;

public class HttpRequest {
    private static final String TAG = "HttpRequest";
    public static OneBean getOne(){
        OneBean oneBean=null;
        JSONObject resultJson=OkHttpGet(Strings.ONE_API);
        if (resultJson!=null){
            oneBean=JSON.parseObject(resultJson.toString(),OneBean.class);
        }
        return oneBean;
    }
    /**
     * 网络获取网易云搜索结果（同步），需异步操作
     * @param name  搜索歌曲名
     * @param singer 搜索作者名
     * @param limit 搜索条数(单页条数)
     * @param offset 搜索偏移(搜索页数)
     */
    public static ArrayList<MyMusicSearchBean.SongsBean> getYunFormatSearchSimple(String name, String singer, int limit, int offset){
        ArrayList<MyMusicSearchBean.SongsBean> songs=new ArrayList<>();
        String baseUrl=Strings.YUN_SEARCH_SONG_API+name+" "+singer+"&limit="+limit+"&offset="+offset;
        String testBaseUrl=Strings.TEST_YUN_SEARCH_SONG_API+name+" "+singer+"&limit="+limit+"&offset="+offset;
        try {
            JSONObject resultJson=OkHttpGet(baseUrl);
            if (resultJson!=null&&resultJson.optInt("code")==200&&((JSONObject) resultJson.get("result")).optInt("songCount")>0) {
                YunSearchResult searchResultBean=(YunSearchResult) JSON.parseObject(resultJson.toString(), YunSearchResult.class);
                for (YunSearchResult.ResultBean.SongsBean yunSearchSongBean : searchResultBean.getResult().getSongs()) {
                    MyMusicSearchBean.SongsBean song=new MyMusicSearchBean.SongsBean();
                    song.setAlbum(yunSearchSongBean.getAl().getName());
                    song.setPic(yunSearchSongBean.getAl().getPicUrl());
                    song.setId(yunSearchSongBean.getId());
                    song.setName(yunSearchSongBean.getName());
                    String singer1="";
                    int singerNum=1;
                    for (YunSearchResult.ResultBean.SongsBean.ArBean singers : yunSearchSongBean.getAr()) {
                        singer1+=singers.getName();
                        if (singerNum<yunSearchSongBean.getAr().size()){
                            singer1+="/";
                        }
                        singerNum++;
                    }
                    song.setSinger(singer1);
                    song.setUrl("https://music.163.com/song/media/outer/url?&id="+yunSearchSongBean.getId());
                    song.setTime(yunSearchSongBean.getDt());
                    songs.add(song);
                }
            }else {
                songs=testFormatSearch(testBaseUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
            songs=testFormatSearch(testBaseUrl);
        }
        return songs;
    }
    private static ArrayList<MyMusicSearchBean.SongsBean> testFormatSearch(String baseUrl){
        ArrayList<MyMusicSearchBean.SongsBean> songs=new ArrayList<>();
        try {
            String jsonString=HttpConnGetString(baseUrl);
            com.alibaba.fastjson.JSONObject resultJson= com.alibaba.fastjson.JSONObject.parseObject(jsonString);
            if (resultJson!=null&&resultJson.getInteger("code")==200){
                SongSearchFormatBean searchResultBean=com.alibaba.fastjson.JSON.parseObject(resultJson.toString(), SongSearchFormatBean.class);
                for (SongSearchFormatBean.ResultBean.SongsBean yunSearchSongBean : searchResultBean.getResult().getSongs()) {
                    MyMusicSearchBean.SongsBean song=new MyMusicSearchBean.SongsBean();
                    song.setAlbum(yunSearchSongBean.getAlbum().getName());
                    song.setPic("");
                    song.setId((int) yunSearchSongBean.getId());
                    song.setName(yunSearchSongBean.getName());
                    String singer1="";
                    int singerNum=1;
                    for (SongSearchFormatBean.ResultBean.SongsBean.ArtistsBean singers : yunSearchSongBean.getArtists()) {
                        singer1+=singers.getName();
                        if (singerNum<yunSearchSongBean.getArtists().size()){
                            singer1+="/";
                        }
                        singerNum++;
                    }
                    song.setSinger(singer1);
                    song.setUrl("https://music.163.com/song/media/outer/url?&id="+yunSearchSongBean.getId());
                    song.setTime(yunSearchSongBean.getDuration());
                    songs.add(song);
                }
            }
        }catch (Exception e){

        }
        return songs;
    }
    public static ArrayList<AlbumSearchBean.ResultBean.AlbumsBean> getYunSearchAlbum(String s,int limit,int offset){
        String baseUrl=Strings.YUN_SEARCH_ALBUM_API+s+"&limit="+limit+"&offset="+offset;
        ArrayList<AlbumSearchBean.ResultBean.AlbumsBean> albums=null;
        try {
            JSONObject resultJson=HttpConnGet(baseUrl);
            if (resultJson.optInt("code")==200){
                AlbumSearchBean albumSearchBean=JSON.parseObject(resultJson.toString(), AlbumSearchBean.class);
                albums=new ArrayList<>();
                albums.addAll(albumSearchBean.getResult().getAlbums());
            }
        }catch (Exception e){
            albums=null;
        }
        return albums;
    }
    public static ArrayList<PlayListSearchBean.ResultBean.PlaylistsBean> getYunSearchPlayList(String s, int limit, int offset){
        String baseUrl=Strings.YUN_SEARCH_LIST_API+s+"&limit="+limit+"&offset="+offset;
        ArrayList<PlayListSearchBean.ResultBean.PlaylistsBean> lists=null;
        try {
            JSONObject resultJson=HttpConnGet(baseUrl);
            if (resultJson.optInt("code")==200){
                PlayListSearchBean playListSearchBean=JSON.parseObject(resultJson.toString(), PlayListSearchBean.class);
                lists=new ArrayList<>();
                lists.addAll(playListSearchBean.getResult().getPlaylists());
            }
        }catch (Exception e){
            lists=null;
        }
        return lists;
    }
    /**
     * 搜索歌词列表
     * @param name
     * @param singer
     * @param limit
     * @param offset
     * @return
     */
    public static ArrayList<String[]> getYunFormatSearchLrc(String name, String singer,int limit,int offset){
        ArrayList<MyMusicSearchBean.SongsBean> songs=getYunFormatSearchSimple(name,singer,limit,offset);
        ArrayList<String[]> lrcs=new ArrayList<String[]>();
        for (MyMusicSearchBean.SongsBean song : songs) {
            try {
                String[] lrc=getYunSongLrc(String.valueOf(song.getId()),"temp","",-1,false);//只获取歌词字符串
                lrcs.add(lrc);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return lrcs;
    }
    /**
     * 网易云获取歌词信息
     * @param id 歌曲id
     * @param name 歌曲名
     * @param singer 歌手名
     * @return  两个歌词文件地址 (普通歌词，翻译歌词)
     */
    public static String[] getYunSongLrc(String id,String name,String singer,long songId,boolean setNowLrc){
        String[] resultStrings=new String[2];
        resultStrings[0]=null;
        resultStrings[1]=null;
        String url= Strings.YUN_GET_LRC_API+id;
        try {
            JSONObject resultJson=OkHttpGet(url);
            if (resultJson!=null&&resultJson.optInt("code")==200){
                YunSongLrcBean yunSongLrcBean=(YunSongLrcBean)JSON.parseObject(resultJson.toString(),YunSongLrcBean.class);
                if (yunSongLrcBean!=null){
                    String normalLrc=null,extraLrc=null;
                    if (yunSongLrcBean.getLrc()!=null)
                        normalLrc=yunSongLrcBean.getLrc().getFormatLyric();  //普通歌词
                    if (yunSongLrcBean.getTlyric()!=null)
                        extraLrc=yunSongLrcBean.getTlyric().getFormatLyric();//翻译歌词
                    Log.e(TAG,normalLrc);
                    String fileName=name+"-"+singer;
                    Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\/]");
                    Matcher matcher = pattern.matcher(fileName);
                    fileName= matcher.replaceAll("");
                    if (normalLrc!=null&&!normalLrc.isEmpty()&&normalLrc.length()>0){
                        String normalLrcFileName=fileName+".lrc";
                        Strings.writeOcrStrtoFile(normalLrc,DetailsActivity.LrcPath,normalLrcFileName);
                        if (setNowLrc&&HomeActivity.musicControl.getMusic().getId()==songId)
                            MusicService.nowLrcs= (ArrayList<Lrc>) LrcHelper.parseLrcFromFile(new File(DetailsActivity.LrcPath+"/"+normalLrcFileName));
                        resultStrings[0]=normalLrc;
                    }
                    if (extraLrc!=null&&!extraLrc.isEmpty()&&extraLrc.length()>0){
                        String extraLrcFileName=fileName+"-trans.lrc";
                        Strings.writeOcrStrtoFile(extraLrc,DetailsActivity.LrcPath,extraLrcFileName);
                        if (setNowLrc&&HomeActivity.musicControl.getMusic().getId()==songId)
                            MusicService.nowTransLrcs= (ArrayList<Lrc>) LrcHelper.parseLrcFromFile(new File(DetailsActivity.LrcPath+"/"+extraLrcFileName));
                        resultStrings[1]=extraLrc;
                    }else {
                        MusicService.nowTransLrcs=null;
                    }
                }
            }else {
                return testFormatYunSongLrc(id, name, singer, songId, setNowLrc);
            }
        }catch (Exception e) {
            e.printStackTrace();
            return testFormatYunSongLrc(id, name, singer, songId, setNowLrc);
        }
        return resultStrings;
    }
    public static String[] testFormatYunSongLrc(String id,String name,String singer,long songId,boolean setNowLrc){
        String[] resultStrings=new String[2];
        resultStrings[0]=null;
        resultStrings[1]=null;
        String url= Strings.YUN_GET_FORMAT_LRC_API+id;
        try {
            JSONObject resultJson=OkHttpGet(url);
            if (resultJson!=null&&resultJson.optInt("code")==200){
                YunSongLrcFormatBean yunSongLrcBean=JSON.parseObject(resultJson.toString(),YunSongLrcFormatBean.class);
                if (yunSongLrcBean!=null){
                    String normalLrc=null,extraLrc=null;
                    if (yunSongLrcBean.getLrc()!=null)
                        normalLrc=yunSongLrcBean.getLrc().getFormatLyric();  //普通歌词
                    if (yunSongLrcBean.getTlyric()!=null)
                        extraLrc=yunSongLrcBean.getTlyric().getFormatLyric();//翻译歌词
                    Log.e(TAG,normalLrc);
                    String fileName=name+"-"+singer;
                    Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\/]");
                    Matcher matcher = pattern.matcher(fileName);
                    fileName= matcher.replaceAll("");
                    if (normalLrc!=null&&!normalLrc.isEmpty()&&normalLrc.length()>0){
                        String normalLrcFileName=fileName+".lrc";
                        Strings.writeOcrStrtoFile(normalLrc,DetailsActivity.LrcPath,normalLrcFileName);
                        if (setNowLrc&&HomeActivity.musicControl.getMusic().getId()==songId)
                            MusicService.nowLrcs= (ArrayList<Lrc>) LrcHelper.parseLrcFromFile(new File(DetailsActivity.LrcPath+"/"+normalLrcFileName));
                        resultStrings[0]=normalLrc;
                    }
                    if (extraLrc!=null&&!extraLrc.isEmpty()&&extraLrc.length()>0){
                        String extraLrcFileName=fileName+"-trans.lrc";
                        Strings.writeOcrStrtoFile(extraLrc,DetailsActivity.LrcPath,extraLrcFileName);
                        if (setNowLrc&&HomeActivity.musicControl.getMusic().getId()==songId)
                            MusicService.nowTransLrcs= (ArrayList<Lrc>) LrcHelper.parseLrcFromFile(new File(DetailsActivity.LrcPath+"/"+extraLrcFileName));
                        resultStrings[1]=extraLrc;
                    }else {
                        MusicService.nowTransLrcs=null;
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return resultStrings;
    }
    private static String[] getTransLrc(String lyric){
        String[] lrcs=new String[2];
        lrcs[0]=null;
        lrcs[1]=null;

        return lrcs;
    }
    private static String getFormatLyric(String lyric){
        String[] lineLyrics=lyric.split("\n"); //每行一个字符串
        String newString="";
        int lineLength=lineLyrics.length; //获取歌词行数
        for (int i=0;i<lineLength;i++){
            int lastIndex=lineLyrics[i].indexOf("]");   //】的索引
            String lineString=lineLyrics[i];
            StringBuilder sb=new StringBuilder(lineString);
            if (lastIndex==10){            //不合规范
                sb.replace(9,10,"");
            }
            newString+=sb.toString()+"\n";
        }
        return newString;
    }

    /**
     * 歌词迷api搜索歌词信息
     * @param name 歌名
     * @param singer 作者
     * @return 歌词地址
     */
    public static String getMiSongLrc(Context context,String name, String singer,long songId){
        String resultString=null;
        String url= Strings.GET_SONG_LRC_API+name+"/"+singer;
        try {
            JSONObject resultJson=OkHttpGet(url);
            if (resultJson!=null&&resultJson.optInt("code")==0){
                JSONArray lrcArray=resultJson.optJSONArray("result");
                if (lrcArray.length()>0) {
                    JSONObject lrcJson= (JSONObject) lrcArray.get(0);
                    resultString=lrcJson.optString("lrc");
                    resultString=MusicService.proxy.getProxyUrl(resultString);
                    String file =downloadLrc(context,resultString);
                    if (HomeActivity.musicControl.getMusic().getId()==songId)
                        MusicService.nowLrcs=(ArrayList<Lrc>) LrcHelper.parseLrcFromFile(new File(file));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return resultString;
    }
    public static ArrayList<SearchDao.Record> getSearchSuggestion(String searchString){
        ArrayList<SearchDao.Record> searchResult=new ArrayList<>();
        String baseUrl= Strings.BAIDU_SEARCH_SUGGESTION_API+searchString;
        try {
            JSONObject resultJson = HttpConnGet(baseUrl);
            if (resultJson != null) {
                JSONArray suggestionArray=resultJson.optJSONArray("suggestion_list");
                if (suggestionArray!=null){
                    for (int i=0;i<suggestionArray.length();i++){
                        String suggestionString= null;
                        suggestionString = (String) suggestionArray.get(i);
                        SearchDao.Record record=new SearchDao.Record();
                        record.setRecord(suggestionString);
                        searchResult.add(record);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return searchResult;
    }
    public static ArrayList<BannerBean.BannersBean> getBanners(){
        ArrayList<BannerBean.BannersBean> banners=null;
        try {
            JSONObject resultJson = HttpConnGet(Strings.YUN_BANNER_API);
            banners=new ArrayList<>();
            if (resultJson!=null&&resultJson.optInt("code")==200){
                BannerBean bannerBean=JSON.parseObject(resultJson.toString(),BannerBean.class);
                banners= (ArrayList<BannerBean.BannersBean>) bannerBean.getBanners();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return banners;
    }

    //获取专辑内所以歌曲
    public static MyAlbumBean getAlbumDetails(long id){
        MyAlbumBean myAlbumBean=null;
        ArrayList<Music> musics=null;
        JSONObject resultJson = null;
        try {
            resultJson = HttpConnGet(Strings.YUN_ALBUM_DETAILS_API+id);
            if (resultJson!=null&&resultJson.optInt("code")==200){
                myAlbumBean=new MyAlbumBean();
                musics=new ArrayList<>();
                AlbumResultBean albumBean=JSON.parseObject(resultJson.toString(), AlbumResultBean.class);
                myAlbumBean.setCover(albumBean.getAlbum().getPicUrl());
                myAlbumBean.setCreateTime(toDate(albumBean.getAlbum().getPublishTime()));
                if (albumBean.getAlbum().getDescription()!=null)
                    myAlbumBean.setIntroduce(albumBean.getAlbum().getDescription());
                else
                    myAlbumBean.setIntroduce("暂无简介");
                myAlbumBean.setName(albumBean.getAlbum().getName());
                myAlbumBean.setSinger(albumBean.getAlbum().getArtist().getName());
                ArrayList<AlbumResultBean.AlbumBeanX.SongsBean> songs= (ArrayList<AlbumResultBean.AlbumBeanX.SongsBean>) albumBean.getAlbum().getSongs();
                for (AlbumResultBean.AlbumBeanX.SongsBean song:
                        songs) {
                    Music music=new Music();
                    music.setSId((long)-1);
                    music.setMId(Long.valueOf(song.getId()));
                    music.setId(Long.valueOf(song.getId()));
                    music.setUrl("https://music.163.com/song/media/outer/url?&id="+song.getId());
                    String singer="";
                    int singerNum=1;
                    for (AlbumResultBean.AlbumBeanX.SongsBean.ArtistsBeanX ar:
                         song.getArtists()) {
                        singer+=ar.getName();
                        if (singerNum<song.getArtists().size()){
                            singer+="/";
                        }
                        singerNum++;
                    }
                    music.setSinger(singer);
                    music.setProgress(0);
                    music.setTime(song.getDuration());
                    music.setName(song.getName());
                    music.setTitle(singer+"-"+song.getName());
                    music.setAlbum(song.getAlbum().getName());
                    music.setAlbumImg(song.getAlbum().getPicUrl());
                    music.setSize(0);
                    musics.add(music);
                }
                myAlbumBean.setMusics(musics);
            }
        } catch (Exception e) {
            e.printStackTrace();
            myAlbumBean=testBannerAlbumBean(resultJson);
        }
        return myAlbumBean;
    }
    private static MyAlbumBean testBannerAlbumBean(JSONObject resultJson){
        MyAlbumBean myAlbumBean=null;
        try {
            if (resultJson!=null&&resultJson.optInt("code")==200){
                myAlbumBean=new MyAlbumBean();
                ArrayList<Music> musics=new ArrayList<>();
                BannerAlbumResultBean albumBean=JSON.parseObject(resultJson.toString(), BannerAlbumResultBean.class);
                myAlbumBean.setCover(albumBean.getAlbum().getPicUrl());
                myAlbumBean.setCreateTime(toDate(albumBean.getAlbum().getPublishTime()));
                if (albumBean.getAlbum().getDescription()!=null)
                    myAlbumBean.setIntroduce(albumBean.getAlbum().getDescription());
                else
                    myAlbumBean.setIntroduce("暂无简介");
                myAlbumBean.setName(albumBean.getAlbum().getName());
                myAlbumBean.setSinger(albumBean.getAlbum().getArtist().getName());
                ArrayList<BannerAlbumResultBean.AlbumBeanX.SongsBean> songs= (ArrayList<BannerAlbumResultBean.AlbumBeanX.SongsBean>) albumBean.getAlbum().getSongs();
                for (BannerAlbumResultBean.AlbumBeanX.SongsBean song:
                        songs) {
                    Music music=new Music();
                    music.setSId((long)-1);
                    music.setMId(Long.valueOf(song.getId()));
                    music.setId(Long.valueOf(song.getId()));
                    music.setUrl("https://music.163.com/song/media/outer/url?&id="+song.getId());
                    String singer="";
                    int singerNum=1;
                    for (BannerAlbumResultBean.AlbumBeanX.SongsBean.ArtistsBeanX ar:
                            song.getArtists()) {
                        singer+=ar.getName();
                        if (singerNum<song.getArtists().size()){
                            singer+="/";
                        }
                        singerNum++;
                    }
                    music.setSinger(singer);
                    music.setProgress(0);
                    music.setTime(song.getDuration());
                    music.setName(song.getName());
                    music.setTitle(singer+"-"+song.getName());
                    music.setAlbum(song.getAlbum().getName());
                    music.setAlbumImg(song.getAlbum().getPicUrl());
                    music.setSize(0);
                    musics.add(music);
                }
                myAlbumBean.setMusics(musics);
            }
        }catch (Exception e){
            return null;
        }
        return myAlbumBean;
    }
    //获取歌单详细信息
    public static MyPlayListBean getPlayListDetails(long id){
        MyPlayListBean myPlayListBean=null;
        JSONObject resultJson=null;
        try {
            resultJson = HttpConnGet(Strings.YUN_PLAYLIST_DETAILS_API+id);
            if (resultJson!=null&&resultJson.optInt("code")==200){
                myPlayListBean=new MyPlayListBean();
                PlayListResultBean listBean=JSON.parseObject(resultJson.toString(), PlayListResultBean.class);
                myPlayListBean.setName(listBean.getResult().getName());
                myPlayListBean.setCover(listBean.getResult().getCoverImgUrl());
                myPlayListBean.setCreater(listBean.getResult().getCreator().getNickname());
                if (listBean.getResult().getDescription()!=null)
                    myPlayListBean.setIntroduce(listBean.getResult().getDescription());
                else
                    myPlayListBean.setIntroduce("暂无简介");
                myPlayListBean.setListenCount(listBean.getResult().getPlayCount());
                ArrayList<PlayListResultBean.ResultBean.TracksBean> songs= (ArrayList<PlayListResultBean.ResultBean.TracksBean>) listBean.getResult().getTracks();
                ArrayList<Music> musics=new ArrayList<>();
                for (PlayListResultBean.ResultBean.TracksBean song:
                     songs) {
                    Music music=new Music();
                    music.setSId((long)-1);
                    music.setMId(Long.valueOf(song.getId()));
                    music.setId(Long.valueOf(song.getId()));
                    music.setUrl("https://music.163.com/song/media/outer/url?&id="+song.getId());
                    String singer="";
                    int singerNum=1;
                    for (PlayListResultBean.ResultBean.TracksBean.ArtistsBeanX artist:
                         song.getArtists()) {
                        singer+=artist.getName();
                        if (singerNum<song.getArtists().size()){
                            singer+="/";
                        }
                        singerNum++;
                    }
                    music.setSinger(singer);
                    music.setProgress(0);
                    music.setTime(song.getDuration());
                    music.setName(song.getName());
                    music.setTitle(singer+"-"+song.getName());
                    music.setAlbum(song.getAlbum().getName());
                    music.setAlbumImg(song.getAlbum().getPicUrl());
                    music.setSize(0);
                    musics.add(music);
                }
                myPlayListBean.setMusics(musics);
            }
        } catch (Exception e) {
            e.printStackTrace();
            myPlayListBean=testTopListParse(resultJson);
        }
        return myPlayListBean;
    }
    private static MyPlayListBean testTopListParse(JSONObject resultJson){
        MyPlayListBean myPlayListBean=null;
        try {
            if (resultJson!=null&&resultJson.optInt("code")==200) {
                myPlayListBean = new MyPlayListBean();
                TopListResultBean listBean = JSON.parseObject(resultJson.toString(), TopListResultBean.class);
                myPlayListBean.setName(listBean.getResult().getName());
                myPlayListBean.setCover(listBean.getResult().getCoverImgUrl());
                myPlayListBean.setCreater(listBean.getResult().getCreator().getNickname());
                if (listBean.getResult().getDescription() != null)
                    myPlayListBean.setIntroduce(listBean.getResult().getDescription());
                else
                    myPlayListBean.setIntroduce("暂无简介");
                myPlayListBean.setListenCount((int) listBean.getResult().getPlayCount());
                ArrayList<TopListResultBean.ResultBean.TracksBean> songs = (ArrayList<TopListResultBean.ResultBean.TracksBean>) listBean.getResult().getTracks();
                ArrayList<Music> musics = new ArrayList<>();
                for (TopListResultBean.ResultBean.TracksBean song :
                        songs) {
                    Music music = new Music();
                    music.setSId((long) -1);
                    music.setMId(Long.valueOf(song.getId()));
                    music.setId(Long.valueOf(song.getId()));
                    music.setUrl("https://music.163.com/song/media/outer/url?&id=" + song.getId());
                    String singer = "";
                    int singerNum=1;
                    for (TopListResultBean.ResultBean.TracksBean.ArtistsBeanX artist :
                            song.getArtists()) {
                        singer += artist.getName();
                        if (singerNum<song.getArtists().size()){
                            singer+="/";
                        }
                        singerNum++;
                    }
                    music.setSinger(singer);
                    music.setProgress(0);
                    music.setTime(song.getDuration());
                    music.setName(song.getName());
                    music.setTitle(singer + "-" + song.getName());
                    music.setAlbum(song.getAlbum().getName());
                    music.setAlbumImg(song.getAlbum().getPicUrl());
                    music.setSize(0);
                    musics.add(music);
                }
                myPlayListBean.setMusics(musics);
            }
        }catch (Exception e){
            return testWeekListParse(resultJson);
        }
        return myPlayListBean;
    }
    private static MyPlayListBean testWeekListParse(JSONObject resultJson){
        MyPlayListBean myPlayListBean=null;
        try {
            if (resultJson!=null&&resultJson.optInt("code")==200) {
                myPlayListBean = new MyPlayListBean();
                PlayListResultTempBean listBean = JSON.parseObject(resultJson.toString(), PlayListResultTempBean.class);
                myPlayListBean.setName(listBean.getResult().getName());
                myPlayListBean.setCover(listBean.getResult().getCoverImgUrl());
                myPlayListBean.setCreater(listBean.getResult().getCreator().getNickname());
                if (listBean.getResult().getDescription() != null)
                    myPlayListBean.setIntroduce(listBean.getResult().getDescription());
                else
                    myPlayListBean.setIntroduce("暂无简介");
                myPlayListBean.setListenCount((int) listBean.getResult().getPlayCount());
                ArrayList<PlayListResultTempBean.ResultBean.TracksBean> songs = (ArrayList<PlayListResultTempBean.ResultBean.TracksBean>) listBean.getResult().getTracks();
                ArrayList<Music> musics = new ArrayList<>();
                for (PlayListResultTempBean.ResultBean.TracksBean song :
                        songs) {
                    Music music = new Music();
                    music.setSId((long) -1);
                    music.setMId(Long.valueOf(song.getId()));
                    music.setId(Long.valueOf(song.getId()));
                    music.setUrl("https://music.163.com/song/media/outer/url?&id=" + song.getId());
                    String singer = "";
                    int singerNum=1;
                    for (PlayListResultTempBean.ResultBean.TracksBean.ArtistsBeanX artist :
                            song.getArtists()) {
                        singer += artist.getName();
                        if (singerNum<song.getArtists().size()){
                            singer+="/";
                        }
                        singerNum++;
                    }
                    music.setSinger(singer);
                    music.setProgress(0);
                    music.setTime(song.getDuration());
                    music.setName(song.getName());
                    music.setTitle(singer + "-" + song.getName());
                    music.setAlbum(song.getAlbum().getName());
                    music.setAlbumImg(song.getAlbum().getPicUrl());
                    music.setSize(0);
                    musics.add(music);
                }
                myPlayListBean.setMusics(musics);
            }
        }catch (Exception e){
            return null;
        }
        return myPlayListBean;
    }
    public static Music getMusicDetail(int id){
        Music music=new Music();
        try {
            JSONObject resultJson=HttpConnGet(Strings.YUN_MUSIC_DETAILS_API+id+"&ids=["+id+"]");
            if (resultJson!=null&&resultJson.optInt("code")==200){
                MusicResultBean musicResultBean=JSON.parseObject(resultJson.toString(),MusicResultBean.class);
                MusicResultBean.SongsBean song=musicResultBean.getSongs().get(0);
                music.setId((long) song.getId());
                music.setMId(Long.valueOf(song.getId()));
                music.setSId((long)-1);
                music.setAlbum(song.getAlbum().getName());
                String singerName="";
                int singerNum=1;
                for (int i=0;i<song.getArtists().size();i++){
                    singerName+=song.getArtists().get(i).getName();
                    if (singerNum<song.getArtists().size()){
                        singerName+="/";
                    }
                    singerNum++;
                }
                music.setSinger(singerName);
                music.setSize(0);
                music.setAlbumImg(song.getAlbum().getPicUrl());
                music.setTitle(song.getName()+"-"+singerName);
                music.setTime(song.getDuration());
                music.setProgress(0);
                music.setName(song.getName());
                music.setUrl("https://music.163.com/song/media/outer/url?&id="+song.getId());
            }
        }catch (Exception e){
            e.printStackTrace();
            music=null;
        }
        return music;
    }
    //获取推荐歌单
    public static ArrayList<MyPlayListBean> getRecommendList(int limit,int offset){
        ArrayList<MyPlayListBean> myPlayListBeans=null;
        try {
            JSONObject resultJson=OkHttpGet(Strings.YUN_RECOMMEND_LIST_API+limit+"&offset="+offset);
            if (resultJson!=null&&resultJson.optInt("code")==200){
                myPlayListBeans=new ArrayList<>();
                HotListResultBean hotListResultBean=JSON.parseObject(resultJson.toString(),HotListResultBean.class);
                ArrayList<HotListResultBean.PlaylistsBean> playlistsBeans= (ArrayList<HotListResultBean.PlaylistsBean>) hotListResultBean.getPlaylists();
                for (HotListResultBean.PlaylistsBean playList:
                        playlistsBeans) {
                    MyPlayListBean myPlayListBean=new MyPlayListBean();
                    myPlayListBean.setId(playList.getId());
                    myPlayListBean.setListenCount(playList.getPlayCount());
                    myPlayListBean.setIntroduce(playList.getDescription());
                    myPlayListBean.setName(playList.getName());
                    myPlayListBean.setCover(playList.getCoverImgUrl());
                    myPlayListBean.setCreater("");
                    myPlayListBean.setMusics(new ArrayList<Music>());
                    myPlayListBeans.add(myPlayListBean);
                    //仅获取封面信息，详细信息点击进入获取
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            myPlayListBeans=null;
        }
        return myPlayListBeans;
    }
    public static ArrayList<MyAlbumBean> getNewAlbumList(int limit,int offset){
        ArrayList<MyAlbumBean> myAlbumBeans=null;
        try {
            JSONObject resultJson=HttpConnGet(Strings.NEW_ALBUM_API+limit+"&offset="+offset);
            if (resultJson!=null&&resultJson.optInt("code")==200){
                myAlbumBeans=new ArrayList<>();
                NewAlbumResultBean newAlbumResultBean=JSON.parseObject(resultJson.toString(),NewAlbumResultBean.class);
                ArrayList<NewAlbumResultBean.AlbumsBean> playlistsBeans= (ArrayList<NewAlbumResultBean.AlbumsBean>) newAlbumResultBean.getAlbums();
                for (NewAlbumResultBean.AlbumsBean playList:
                        playlistsBeans) {
                    MyAlbumBean myAlbumBean=new MyAlbumBean();
                    myAlbumBean.setId(playList.getId());
                    String singer="";
                    int singerNum=1;
                    for (NewAlbumResultBean.AlbumsBean.ArtistsBean ar:
                         playList.getArtists()) {
                        singer+=ar.getName();
                        if (singerNum<playList.getArtists().size()){
                            singer+="/";
                        }
                        singerNum++;
                    }
                    myAlbumBean.setSinger(singer);
                    myAlbumBean.setIntroduce(playList.getDescription());
                    myAlbumBean.setName(playList.getName());
                    myAlbumBean.setCover(playList.getPicUrl());
                    myAlbumBean.setMusics(new ArrayList<Music>());
                    myAlbumBeans.add(myAlbumBean);
                    //仅获取封面信息，详细信息点击进入获取
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            myAlbumBeans=null;
        }
        return myAlbumBeans;
    }
    public static ArrayList<TopListBean> getTopList(){
        ArrayList<TopListBean> topListBeans=null;
        try {
            com.alibaba.fastjson.JSONObject resultJson;
            resultJson = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(HttpConnGetString(Strings.YUN_RANK_LIST_API));
            if (resultJson!=null&&resultJson.getInteger("code")==200){
                topListBeans=new ArrayList<>();
                com.alibaba.fastjson.JSONArray topList=resultJson.getJSONArray("list");
                for(int i=0;i<topList.size();i++){
                    com.alibaba.fastjson.JSONObject topListJson=topList.getJSONObject(i);
                    TopListBean topListBean=new TopListBean();
                    topListBean.setId(topListJson.getLong("id"));
                    topListBean.setName(topListJson.getString("name"));
                    topListBean.setPicUrl(topListJson.getString("coverImgUrl"));
                    topListBean.setPlayCount(topListJson.getInteger("playCount"));
                    topListBean.setUpdateString(topListJson.getString("updateFrequency"));
                    com.alibaba.fastjson.JSONArray arListJsonArray=topListJson.getJSONArray("tracks");
                    ArrayList<TopListBean.TopBean> arBeans=new ArrayList<>();
                    for (int j=0;j<arListJsonArray.size();j++){
                        com.alibaba.fastjson.JSONObject arJson=(com.alibaba.fastjson.JSONObject)arListJsonArray.get(j);
                        arBeans.add(new TopListBean.TopBean(arJson.getString("first"),arJson.getString("second")));
                    }
                    topListBean.setTaskBeans(arBeans);
                    topListBeans.add(topListBean);
                }
                Log.e(TAG,topListBeans.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return topListBeans;
    }
    public static AppVersionBean checkUpdate(String version){
        AppVersionBean versionBean=null;
        try {
            String baseUrl=Strings.APP_UPDATE_API+version;
            JSONObject resultJson=OkHttpGet(baseUrl);
            if (resultJson.optInt("code")==200){
                versionBean=JSON.parseObject(resultJson.toString(),AppVersionBean.class);
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return versionBean;
    }
    public static MusicCommentsResultBean getComments(long id,int limit,int offset){
        MusicCommentsResultBean commentsBean=null;
        try {
            String baseUrl=Strings.YUN_SONG_COMMENTS_API+id+"&limit="+limit+"&offset="+offset;
            JSONObject resultJson=HttpConnGet(baseUrl);
            if (resultJson.optInt("code")==200){
                commentsBean=JSON.parseObject(resultJson.toString(),MusicCommentsResultBean.class);
            }else{
                return getTempComments(id, limit, offset);
            }
        }catch (Exception e){
            e.printStackTrace();
            return getTempComments(id, limit, offset);
        }
        return commentsBean;
    }
    public static MusicCommentsResultBean getTempComments(long id, int limit, int offset){
        MusicCommentsResultBean commentsBean=null;
        try {
            String baseUrl=Strings.YUN_SONG_FORMAT_COMMENTS_API+id+"&limit="+limit+"&offset="+offset;
            JSONObject resultJson=OkHttpGet(baseUrl);
            if (resultJson.optInt("code")==200){
                commentsBean=JSON.parseObject(resultJson.toString(),MusicCommentsResultBean.class);
            }else{
                commentsBean=null;
            }
        }catch (Exception e){
            e.printStackTrace();
            commentsBean=null;
        }
        return commentsBean;
    }

    private static JSONObject HttpConnGet(String baseUrl){
        JSONObject resultJson=null;
        try {
            URL url = new URL(baseUrl);// 新建一个URL对象
            // 打开一个HttpURLConnection连接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            // 设置连接超时时间
            urlConn.setConnectTimeout(30 * 1000);
            //设置从主机读取数据超时
            urlConn.setReadTimeout(30 * 1000);
            // Post请求必须设置允许输出 默认false
            urlConn.setDoOutput(true);
            //设置请求允许输入 默认是true
            urlConn.setDoInput(true);
            // Post请求不能使用缓存
            urlConn.setUseCaches(false);
            // 设置为Post请求
            urlConn.setRequestMethod("GET");
            //设置本次连接是否自动处理重定向
            urlConn.setInstanceFollowRedirects(true);
            // 配置请求Content-Type
            urlConn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            // 开始连接
            urlConn.connect();
            // 发送请求参数
            DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
            dos.flush();
            dos.close();
            // 判断请求是否成功
            if (urlConn.getResponseCode() == 200) {
                String result = streamToString(urlConn.getInputStream());
                resultJson = new JSONObject(result);
                System.out.println(result);
            }
            // 关闭连接
            urlConn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            resultJson=null;
        }
        return resultJson;
    }
    private static String HttpConnGetString(String baseUrl){
        try {
            URL url = new URL(baseUrl);// 新建一个URL对象
            // 打开一个HttpURLConnection连接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            // 设置连接超时时间
            urlConn.setConnectTimeout(30 * 1000);
            //设置从主机读取数据超时
            urlConn.setReadTimeout(30 * 1000);
            // Post请求必须设置允许输出 默认false
            urlConn.setDoOutput(true);
            //设置请求允许输入 默认是true
            urlConn.setDoInput(true);
            // Post请求不能使用缓存
            urlConn.setUseCaches(false);
            // 设置为Post请求
            urlConn.setRequestMethod("GET");
            //设置本次连接是否自动处理重定向
            urlConn.setInstanceFollowRedirects(true);
            // 配置请求Content-Type
            urlConn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            // 开始连接
            urlConn.connect();
            // 发送请求参数
            DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
            dos.flush();
            dos.close();
            // 判断请求是否成功
            if (urlConn.getResponseCode() == 200) {
                String result = streamToString(urlConn.getInputStream());
                System.out.println(result);
                return result;
            }
            // 关闭连接
            urlConn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private static JSONObject OkHttpGet(String url){
        JSONObject resultJson=null;
        OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(50, TimeUnit.SECONDS).build();   //50毫秒内响应
        Request request = new Request.Builder().url(url).get().build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            String result = response.body().string();
            Log.e(TAG,"response: " + result);
            try {
                resultJson=new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultJson;
    }
    private static String OkHttpGetString(String url){
        String result=null;
        OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(50, TimeUnit.SECONDS).build();   //50毫秒内响应
        Request request = new Request.Builder().url(url).get().build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            result = response.body().string();
            Log.e(TAG,"response: " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public static String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();
            byte[] byteArray = baos.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
            return "";
        }
    }
    public static String toDate(long time) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd");
        Date date = new Date(time);
        return sdf.format(date);
    }
}
