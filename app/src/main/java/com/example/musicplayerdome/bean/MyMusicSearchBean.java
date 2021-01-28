package com.example.musicplayerdome.bean;

import java.util.List;

public class MyMusicSearchBean {


    /**
     * msg : 获取信息成功
     * code : 200
     * songs : [{"album":"摩天动物园","id":1405283464,"name":"句号","pic":"https://p1.music.126.net/KTo5oSxH3CPA5PBTeFKDyA==/109951164581432409.jpg","singer":"G.E.M.邓紫棋 ","time":235632,"url":"http://www.foxluo.cn/music.html?id=1405283464"},{"album":"\u201c用奋斗点亮幸福\u201d抖音2020江苏卫视跨年演唱会","id":1421005481,"name":"Walk on Water + 句号 + 倒数 (Live)","pic":"https://p1.music.126.net/dKbHPttf83GgJf2brMlX6Q==/109951164678002659.jpg","singer":"G.E.M.邓紫棋 ","time":0,"url":"http://www.foxluo.cn/music.html?id=1421005481"},{"album":"2019KUGOU直播年度盛典","id":1416321657,"name":"差不多姑娘 ＋ 句号 + 光年之外 (Live)","pic":"https://p1.music.126.net/z5l3z-q7Z2AdJnvnB3FBUA==/109951164622409334.jpg","singer":"G.E.M.邓紫棋 ","time":735059,"url":"http://www.foxluo.cn/music.html?id=1416321657"}]
     * count : 3
     */

    private String msg;
    private int code;
    private int count;
    private List<SongsBean> songs;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<SongsBean> getSongs() {
        return songs;
    }

    public void setSongs(List<SongsBean> songs) {
        this.songs = songs;
    }

    public static class SongsBean {
        /**
         * album : 摩天动物园
         * id : 1405283464
         * name : 句号
         * pic : https://p1.music.126.net/KTo5oSxH3CPA5PBTeFKDyA==/109951164581432409.jpg
         * singer : G.E.M.邓紫棋
         * time : 235632
         * url : http://www.foxluo.cn/music.html?id=1405283464
         */

        private String album;
        private int id;
        private String name;
        private String pic;
        private String singer;
        private int time;
        private String url;

        public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getSinger() {
            return singer;
        }

        public void setSinger(String singer) {
            this.singer = singer;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
