package com.example.musicplayerdome.bean;

public class YunSongLrcFormatBean {
    /**
     * sgc : false
     * sfy : false
     * qfy : false
     * transUser : {"id":28012031,"status":99,"demand":1,"userid":84211810,"nickname":"Amami-Haruka","uptime":1571333402271}
     * lrc : {"version":19,"lyric":"[00:10.090]泣いたってもうなにも戻ってこないって\n[00:15.590]何度言っても私の心 理解しない\n[00:22.030]描いた未来の二人が邪魔してくるんだ\n[00:28.900]痛いほどに\n[00:30.710]\n[00:41.900]忘れたい答え あがいたって\n[00:46.900]君はもういない 日々だった\n[00:51.910]何もできなくてありがとうも\n[00:56.840]言えず堪えた涙は落ちた\n[01:02.660]「あとほんの少し\u2026」つけた指輪\n[01:07.720]このままでいてお願い\n[01:11.160]\n[01:12.220]泣いたってもうなにも戻ってこないって\n[01:18.090]何度言っても私の心 理解しない\n[01:24.650]描いた未来の二人が邪魔してくるんだ\n[01:31.280]痛いほどに\n[01:33.340]\n[01:44.220]思い出数え大切に 一つ一つ消してく\n[01:54.350]少し先行く足早な君は\n[01:59.970]\n[02:00.760]二度と振り向かないの?\n[02:04.570]\n[02:05.700]「あとほんの少し\u2026」想っていたい\n[02:10.260]どんなに辛くても\n[02:13.570]\n[02:14.670]そばにいたい 君の笑う顔はもう全部\n[02:21.540]記憶の中で抜け殻のように\n[02:27.170]いつも笑っているんだ\n[02:31.360]あの日からずっと進めないよ\n[02:35.980]\n[02:39.050]二人出会ったこと後悔なんてしてない\n[02:47.360]\n[02:48.050]幸せ沢山くれた君へ強がりのさよなら\n[03:00.670]\n[03:01.610]泣いたってもうなにも戻ってこないって\n[03:07.670]何度言っても私の心 理解しない\n[03:14.050]描いた未来の二人が邪魔してくるんだ\n[03:20.920]痛いほど鮮明に\n[03:24.480]君の笑う顔はもう\n[03:29.350]記憶の中で抜け殻のように\n[03:34.920]いつも笑っているん\n[03:38.980]あの日からずっと進めないよ\n"}
     * klyric : {"version":6,"lyric":null}
     * tlyric : {"version":4,"lyric":"[00:10.090]突然感到泣不成声 也没办法再去改变什么了\n[00:15.590]如此无数次地说服自己 内心却无法理解这句话\n[00:22.030]曾经想象于未来的彼此 如梦魇般对我穷追不舍\n[00:28.900]如今痛苦不堪\n[00:41.900]希望能忘记的答案 无论如何去挣扎\n[00:46.900]如今已是 没有你陪在身边的日子\n[00:51.910]变得无能为力 连说出一句谢谢也是\n[00:56.840]不堪忍受欲言又止 不禁已声泪俱下\n[01:02.660]\u201c还差一点点\u201d戴上的戒指\n[01:07.720]希望就这样戴着不要拆取下来\n[01:12.220]突然感到泣不成声 也没办法再去改变什么了\n[01:18.090]如此无数次地说服自己 内心却无法理解这句话\n[01:24.650]曾经想象于未来的彼此 如梦魇般对我穷追不舍\n[01:31.280]如今痛苦不堪\n[01:44.220]细数回忆 将之视为珍宝 却一个一个地消逝不见\n[01:54.350]渐行渐远的身影迅速的步伐\n[02:00.760]你已经不会再次转身回头了吗？\n[02:05.700]「还差一点点...」想抱持这种念头\n[02:10.260]尽管是多么地痛不欲生\n[02:14.670]想呆在你身边 你开怀大笑的笑容 如今全部\n[02:21.540]于记忆之中 像是一层蜕变后的外衣\n[02:27.170]始终维持着笑容的样貌\n[02:31.360]从那天开始就一直受困在记忆中的原地\n[02:39.050]两个人邂逅的机遇 对此从来没有感到后悔\n[02:48.050]曾感受到无比幸福 故作坚强地向你说出一句再见\n[03:01.610]纵然哭到泣不成声 也没办法再去改变什么了\n[03:07.670]如此无数次的说服自己 内心却无法理解这句话\n[03:14.050]曾经想象于未来的彼此 如梦魇般对我穷追不舍\n[03:20.920]如今因痛苦而铭心刻骨\n[03:24.480]你开怀大笑的面容 如今全部\n[03:29.350]于记忆之中 像是一层蜕变后的外衣\n[03:34.920]始终维持着笑容的样貌\n[03:38.980]从那天开始就一直 受困在记忆中的原地"}
     * code : 200
     */

    private boolean sgc;
    private boolean sfy;
    private boolean qfy;
    private TransUserBean transUser;
    private LrcBean lrc;
    private KlyricBean klyric;
    private TlyricBean tlyric;
    private int code;

    public boolean isSgc() {
        return sgc;
    }

    public void setSgc(boolean sgc) {
        this.sgc = sgc;
    }

    public boolean isSfy() {
        return sfy;
    }

    public void setSfy(boolean sfy) {
        this.sfy = sfy;
    }

    public boolean isQfy() {
        return qfy;
    }

    public void setQfy(boolean qfy) {
        this.qfy = qfy;
    }

    public TransUserBean getTransUser() {
        return transUser;
    }

    public void setTransUser(TransUserBean transUser) {
        this.transUser = transUser;
    }

    public LrcBean getLrc() {
        return lrc;
    }

    public void setLrc(LrcBean lrc) {
        this.lrc = lrc;
    }

    public KlyricBean getKlyric() {
        return klyric;
    }

    public void setKlyric(KlyricBean klyric) {
        this.klyric = klyric;
    }

    public TlyricBean getTlyric() {
        return tlyric;
    }

    public void setTlyric(TlyricBean tlyric) {
        this.tlyric = tlyric;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class TransUserBean {
        /**
         * id : 28012031
         * status : 99
         * demand : 1
         * userid : 84211810
         * nickname : Amami-Haruka
         * uptime : 1571333402271
         */

        private int id;
        private int status;
        private int demand;
        private int userid;
        private String nickname;
        private long uptime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getDemand() {
            return demand;
        }

        public void setDemand(int demand) {
            this.demand = demand;
        }

        public int getUserid() {
            return userid;
        }

        public void setUserid(int userid) {
            this.userid = userid;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public long getUptime() {
            return uptime;
        }

        public void setUptime(long uptime) {
            this.uptime = uptime;
        }
    }

    public static class LrcBean {
        /**
         * version : 19
         * lyric : [00:10.090]泣いたってもうなにも戻ってこないって
         [00:15.590]何度言っても私の心 理解しない
         [00:22.030]描いた未来の二人が邪魔してくるんだ
         [00:28.900]痛いほどに
         [00:30.710]
         [00:41.900]忘れたい答え あがいたって
         [00:46.900]君はもういない 日々だった
         [00:51.910]何もできなくてありがとうも
         [00:56.840]言えず堪えた涙は落ちた
         [01:02.660]「あとほんの少し…」つけた指輪
         [01:07.720]このままでいてお願い
         [01:11.160]
         [01:12.220]泣いたってもうなにも戻ってこないって
         [01:18.090]何度言っても私の心 理解しない
         [01:24.650]描いた未来の二人が邪魔してくるんだ
         [01:31.280]痛いほどに
         [01:33.340]
         [01:44.220]思い出数え大切に 一つ一つ消してく
         [01:54.350]少し先行く足早な君は
         [01:59.970]
         [02:00.760]二度と振り向かないの?
         [02:04.570]
         [02:05.700]「あとほんの少し…」想っていたい
         [02:10.260]どんなに辛くても
         [02:13.570]
         [02:14.670]そばにいたい 君の笑う顔はもう全部
         [02:21.540]記憶の中で抜け殻のように
         [02:27.170]いつも笑っているんだ
         [02:31.360]あの日からずっと進めないよ
         [02:35.980]
         [02:39.050]二人出会ったこと後悔なんてしてない
         [02:47.360]
         [02:48.050]幸せ沢山くれた君へ強がりのさよなら
         [03:00.670]
         [03:01.610]泣いたってもうなにも戻ってこないって
         [03:07.670]何度言っても私の心 理解しない
         [03:14.050]描いた未来の二人が邪魔してくるんだ
         [03:20.920]痛いほど鮮明に
         [03:24.480]君の笑う顔はもう
         [03:29.350]記憶の中で抜け殻のように
         [03:34.920]いつも笑っているん
         [03:38.980]あの日からずっと進めないよ

         */

        private int version;
        private String lyric;

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getLyric() {
            return lyric;
        }

        public void setLyric(String lyric) {
            this.lyric = lyric;
        }
        /**
         *  歌词毫秒数忽略最后一位 [dd:dd:ddd]==>[dd:dd:dd]
         * @return
         */
        public String getFormatLyric(){
            String[] lineLyrics=lyric.split("\n"); //切割每行成一个字符串
            String newString="";
            int lineLength=lineLyrics.length; //获取歌词行数
            for (int i=0;i<lineLength;i++){
                int lastIndex=lineLyrics[i].indexOf("]");   //第一个']'的位置
                String lineString=lineLyrics[i];
                StringBuilder sb=new StringBuilder(lineString);
                if (lastIndex==10){            //不合规范,去掉最后一位
                    sb.replace(9,10,"");
                }
                newString+=sb.toString()+"\n";
            }
            return newString;
        }
    }

    public static class KlyricBean {
        /**
         * version : 6
         * lyric : null
         */

        private int version;
        private Object lyric;

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public Object getLyric() {
            return lyric;
        }

        public void setLyric(Object lyric) {
            this.lyric = lyric;
        }
    }

    public static class TlyricBean {
        /**
         * version : 4
         * lyric : [00:10.090]突然感到泣不成声 也没办法再去改变什么了
         [00:15.590]如此无数次地说服自己 内心却无法理解这句话
         [00:22.030]曾经想象于未来的彼此 如梦魇般对我穷追不舍
         [00:28.900]如今痛苦不堪
         [00:41.900]希望能忘记的答案 无论如何去挣扎
         [00:46.900]如今已是 没有你陪在身边的日子
         [00:51.910]变得无能为力 连说出一句谢谢也是
         [00:56.840]不堪忍受欲言又止 不禁已声泪俱下
         [01:02.660]“还差一点点”戴上的戒指
         [01:07.720]希望就这样戴着不要拆取下来
         [01:12.220]突然感到泣不成声 也没办法再去改变什么了
         [01:18.090]如此无数次地说服自己 内心却无法理解这句话
         [01:24.650]曾经想象于未来的彼此 如梦魇般对我穷追不舍
         [01:31.280]如今痛苦不堪
         [01:44.220]细数回忆 将之视为珍宝 却一个一个地消逝不见
         [01:54.350]渐行渐远的身影迅速的步伐
         [02:00.760]你已经不会再次转身回头了吗？
         [02:05.700]「还差一点点...」想抱持这种念头
         [02:10.260]尽管是多么地痛不欲生
         [02:14.670]想呆在你身边 你开怀大笑的笑容 如今全部
         [02:21.540]于记忆之中 像是一层蜕变后的外衣
         [02:27.170]始终维持着笑容的样貌
         [02:31.360]从那天开始就一直受困在记忆中的原地
         [02:39.050]两个人邂逅的机遇 对此从来没有感到后悔
         [02:48.050]曾感受到无比幸福 故作坚强地向你说出一句再见
         [03:01.610]纵然哭到泣不成声 也没办法再去改变什么了
         [03:07.670]如此无数次的说服自己 内心却无法理解这句话
         [03:14.050]曾经想象于未来的彼此 如梦魇般对我穷追不舍
         [03:20.920]如今因痛苦而铭心刻骨
         [03:24.480]你开怀大笑的面容 如今全部
         [03:29.350]于记忆之中 像是一层蜕变后的外衣
         [03:34.920]始终维持着笑容的样貌
         [03:38.980]从那天开始就一直 受困在记忆中的原地
         */

        private int version;
        private String lyric;

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getLyric() {
            return lyric;
        }

        public void setLyric(String lyric) {
            this.lyric = lyric;
        }
        /**
         *  歌词毫秒数忽略最后一位 [dd:dd:ddd]==>[dd:dd:dd]
         * @return
         */
        public String getFormatLyric(){
            if (lyric==null||lyric.isEmpty()){
                return null;
            }
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
    }
}
