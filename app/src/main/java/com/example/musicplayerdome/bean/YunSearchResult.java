package com.example.musicplayerdome.bean;

import java.util.List;

public class YunSearchResult {

    /**
     * result : {"songs":[{"name":"secret base ~君がくれたもの~ (10 years after Ver.)","id":33911781,"pst":0,"t":0,"ar":[{"id":16906,"name":"茅野愛衣","tns":[],"alias":[]},{"id":17905,"name":"戸松遥","tns":[],"alias":[]},{"id":16501,"name":"早見沙織","tns":[],"alias":[]}],"alia":["TV动画《我们仍未知道那天所看见的花的名字》片尾曲"],"pop":100,"st":0,"rt":null,"fee":0,"v":446,"crbt":null,"cf":"","al":{"id":3266177,"name":"secret base～君がくれたもの～(初回生産限定盤)","picUrl":"https://p1.music.126.net/daZcHVIJicL3wXJWMIjAng==/7926379325753633.jpg","tns":[],"pic":7926379325753633},"dt":352493,"h":{"br":320000,"fid":0,"size":14101986,"vd":-28500},"m":{"br":192000,"fid":0,"size":8461208,"vd":-25900},"l":{"br":128000,"fid":0,"size":5640820,"vd":-24400},"a":null,"cd":"1","no":1,"rtUrl":null,"ftype":0,"rtUrls":[],"djId":0,"copyright":0,"s_id":0,"mark":9007199255003136,"originCoverType":0,"rtype":0,"rurl":null,"mst":9,"cp":663018,"mv":10785182,"publishTime":1303833600007,"privilege":{"id":33911781,"fee":0,"payed":0,"st":0,"pl":320000,"dl":999000,"sp":7,"cp":1,"subp":1,"cs":false,"maxbr":999000,"fl":320000,"toast":false,"flag":0}}],"songCount":112}
     * code : 200
     */

    private ResultBean result;
    private int code;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class ResultBean {
        /**
         * songs : [{"name":"secret base ~君がくれたもの~ (10 years after Ver.)","id":33911781,"pst":0,"t":0,"ar":[{"id":16906,"name":"茅野愛衣","tns":[],"alias":[]},{"id":17905,"name":"戸松遥","tns":[],"alias":[]},{"id":16501,"name":"早見沙織","tns":[],"alias":[]}],"alia":["TV动画《我们仍未知道那天所看见的花的名字》片尾曲"],"pop":100,"st":0,"rt":null,"fee":0,"v":446,"crbt":null,"cf":"","al":{"id":3266177,"name":"secret base～君がくれたもの～(初回生産限定盤)","picUrl":"https://p1.music.126.net/daZcHVIJicL3wXJWMIjAng==/7926379325753633.jpg","tns":[],"pic":7926379325753633},"dt":352493,"h":{"br":320000,"fid":0,"size":14101986,"vd":-28500},"m":{"br":192000,"fid":0,"size":8461208,"vd":-25900},"l":{"br":128000,"fid":0,"size":5640820,"vd":-24400},"a":null,"cd":"1","no":1,"rtUrl":null,"ftype":0,"rtUrls":[],"djId":0,"copyright":0,"s_id":0,"mark":9007199255003136,"originCoverType":0,"rtype":0,"rurl":null,"mst":9,"cp":663018,"mv":10785182,"publishTime":1303833600007,"privilege":{"id":33911781,"fee":0,"payed":0,"st":0,"pl":320000,"dl":999000,"sp":7,"cp":1,"subp":1,"cs":false,"maxbr":999000,"fl":320000,"toast":false,"flag":0}}]
         * songCount : 112
         */

        private int songCount;
        private List<SongsBean> songs;

        public int getSongCount() {
            return songCount;
        }

        public void setSongCount(int songCount) {
            this.songCount = songCount;
        }

        public List<SongsBean> getSongs() {
            return songs;
        }

        public void setSongs(List<SongsBean> songs) {
            this.songs = songs;
        }

        public static class SongsBean {
            /**
             * name : secret base ~君がくれたもの~ (10 years after Ver.)
             * id : 33911781
             * pst : 0
             * t : 0
             * ar : [{"id":16906,"name":"茅野愛衣","tns":[],"alias":[]},{"id":17905,"name":"戸松遥","tns":[],"alias":[]},{"id":16501,"name":"早見沙織","tns":[],"alias":[]}]
             * alia : ["TV动画《我们仍未知道那天所看见的花的名字》片尾曲"]
             * pop : 100
             * st : 0
             * rt : null
             * fee : 0
             * v : 446
             * crbt : null
             * cf :
             * al : {"id":3266177,"name":"secret base～君がくれたもの～(初回生産限定盤)","picUrl":"https://p1.music.126.net/daZcHVIJicL3wXJWMIjAng==/7926379325753633.jpg","tns":[],"pic":7926379325753633}
             * dt : 352493
             * h : {"br":320000,"fid":0,"size":14101986,"vd":-28500}
             * m : {"br":192000,"fid":0,"size":8461208,"vd":-25900}
             * l : {"br":128000,"fid":0,"size":5640820,"vd":-24400}
             * a : null
             * cd : 1
             * no : 1
             * rtUrl : null
             * ftype : 0
             * rtUrls : []
             * djId : 0
             * copyright : 0
             * s_id : 0
             * mark : 9007199255003136
             * originCoverType : 0
             * rtype : 0
             * rurl : null
             * mst : 9
             * cp : 663018
             * mv : 10785182
             * publishTime : 1303833600007
             * privilege : {"id":33911781,"fee":0,"payed":0,"st":0,"pl":320000,"dl":999000,"sp":7,"cp":1,"subp":1,"cs":false,"maxbr":999000,"fl":320000,"toast":false,"flag":0}
             */

            private String name;
            private int id;
            private int pst;
            private int t;
            private int pop;
            private int st;
            private Object rt;
            private int fee;
            private int v;
            private Object crbt;
            private String cf;
            private AlBean al;
            private int dt;
            private HBean h;
            private MBean m;
            private LBean l;
            private Object a;
            private String cd;
            private int no;
            private Object rtUrl;
            private int ftype;
            private int djId;
            private int copyright;
            private int s_id;
            private long mark;
            private int originCoverType;
            private int rtype;
            private Object rurl;
            private int mst;
            private int cp;
            private int mv;
            private long publishTime;
            private PrivilegeBean privilege;
            private List<ArBean> ar;
            private List<String> alia;
            private List<?> rtUrls;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getPst() {
                return pst;
            }

            public void setPst(int pst) {
                this.pst = pst;
            }

            public int getT() {
                return t;
            }

            public void setT(int t) {
                this.t = t;
            }

            public int getPop() {
                return pop;
            }

            public void setPop(int pop) {
                this.pop = pop;
            }

            public int getSt() {
                return st;
            }

            public void setSt(int st) {
                this.st = st;
            }

            public Object getRt() {
                return rt;
            }

            public void setRt(Object rt) {
                this.rt = rt;
            }

            public int getFee() {
                return fee;
            }

            public void setFee(int fee) {
                this.fee = fee;
            }

            public int getV() {
                return v;
            }

            public void setV(int v) {
                this.v = v;
            }

            public Object getCrbt() {
                return crbt;
            }

            public void setCrbt(Object crbt) {
                this.crbt = crbt;
            }

            public String getCf() {
                return cf;
            }

            public void setCf(String cf) {
                this.cf = cf;
            }

            public AlBean getAl() {
                return al;
            }

            public void setAl(AlBean al) {
                this.al = al;
            }

            public int getDt() {
                return dt;
            }

            public void setDt(int dt) {
                this.dt = dt;
            }

            public HBean getH() {
                return h;
            }

            public void setH(HBean h) {
                this.h = h;
            }

            public MBean getM() {
                return m;
            }

            public void setM(MBean m) {
                this.m = m;
            }

            public LBean getL() {
                return l;
            }

            public void setL(LBean l) {
                this.l = l;
            }

            public Object getA() {
                return a;
            }

            public void setA(Object a) {
                this.a = a;
            }

            public String getCd() {
                return cd;
            }

            public void setCd(String cd) {
                this.cd = cd;
            }

            public int getNo() {
                return no;
            }

            public void setNo(int no) {
                this.no = no;
            }

            public Object getRtUrl() {
                return rtUrl;
            }

            public void setRtUrl(Object rtUrl) {
                this.rtUrl = rtUrl;
            }

            public int getFtype() {
                return ftype;
            }

            public void setFtype(int ftype) {
                this.ftype = ftype;
            }

            public int getDjId() {
                return djId;
            }

            public void setDjId(int djId) {
                this.djId = djId;
            }

            public int getCopyright() {
                return copyright;
            }

            public void setCopyright(int copyright) {
                this.copyright = copyright;
            }

            public int getS_id() {
                return s_id;
            }

            public void setS_id(int s_id) {
                this.s_id = s_id;
            }

            public long getMark() {
                return mark;
            }

            public void setMark(long mark) {
                this.mark = mark;
            }

            public int getOriginCoverType() {
                return originCoverType;
            }

            public void setOriginCoverType(int originCoverType) {
                this.originCoverType = originCoverType;
            }

            public int getRtype() {
                return rtype;
            }

            public void setRtype(int rtype) {
                this.rtype = rtype;
            }

            public Object getRurl() {
                return rurl;
            }

            public void setRurl(Object rurl) {
                this.rurl = rurl;
            }

            public int getMst() {
                return mst;
            }

            public void setMst(int mst) {
                this.mst = mst;
            }

            public int getCp() {
                return cp;
            }

            public void setCp(int cp) {
                this.cp = cp;
            }

            public int getMv() {
                return mv;
            }

            public void setMv(int mv) {
                this.mv = mv;
            }

            public long getPublishTime() {
                return publishTime;
            }

            public void setPublishTime(long publishTime) {
                this.publishTime = publishTime;
            }

            public PrivilegeBean getPrivilege() {
                return privilege;
            }

            public void setPrivilege(PrivilegeBean privilege) {
                this.privilege = privilege;
            }

            public List<ArBean> getAr() {
                return ar;
            }

            public void setAr(List<ArBean> ar) {
                this.ar = ar;
            }

            public List<String> getAlia() {
                return alia;
            }

            public void setAlia(List<String> alia) {
                this.alia = alia;
            }

            public List<?> getRtUrls() {
                return rtUrls;
            }

            public void setRtUrls(List<?> rtUrls) {
                this.rtUrls = rtUrls;
            }

            public static class AlBean {
                /**
                 * id : 3266177
                 * name : secret base～君がくれたもの～(初回生産限定盤)
                 * picUrl : https://p1.music.126.net/daZcHVIJicL3wXJWMIjAng==/7926379325753633.jpg
                 * tns : []
                 * pic : 7926379325753633
                 */

                private int id;
                private String name;
                private String picUrl;
                private long pic;
                private List<?> tns;

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

                public String getPicUrl() {
                    return picUrl;
                }

                public void setPicUrl(String picUrl) {
                    this.picUrl = picUrl;
                }

                public long getPic() {
                    return pic;
                }

                public void setPic(long pic) {
                    this.pic = pic;
                }

                public List<?> getTns() {
                    return tns;
                }

                public void setTns(List<?> tns) {
                    this.tns = tns;
                }
            }

            public static class HBean {
                /**
                 * br : 320000
                 * fid : 0
                 * size : 14101986
                 * vd : -28500
                 */

                private int br;
                private int fid;
                private int size;
                private int vd;

                public int getBr() {
                    return br;
                }

                public void setBr(int br) {
                    this.br = br;
                }

                public int getFid() {
                    return fid;
                }

                public void setFid(int fid) {
                    this.fid = fid;
                }

                public int getSize() {
                    return size;
                }

                public void setSize(int size) {
                    this.size = size;
                }

                public int getVd() {
                    return vd;
                }

                public void setVd(int vd) {
                    this.vd = vd;
                }
            }

            public static class MBean {
                /**
                 * br : 192000
                 * fid : 0
                 * size : 8461208
                 * vd : -25900
                 */

                private int br;
                private int fid;
                private int size;
                private int vd;

                public int getBr() {
                    return br;
                }

                public void setBr(int br) {
                    this.br = br;
                }

                public int getFid() {
                    return fid;
                }

                public void setFid(int fid) {
                    this.fid = fid;
                }

                public int getSize() {
                    return size;
                }

                public void setSize(int size) {
                    this.size = size;
                }

                public int getVd() {
                    return vd;
                }

                public void setVd(int vd) {
                    this.vd = vd;
                }
            }

            public static class LBean {
                /**
                 * br : 128000
                 * fid : 0
                 * size : 5640820
                 * vd : -24400
                 */

                private int br;
                private int fid;
                private int size;
                private int vd;

                public int getBr() {
                    return br;
                }

                public void setBr(int br) {
                    this.br = br;
                }

                public int getFid() {
                    return fid;
                }

                public void setFid(int fid) {
                    this.fid = fid;
                }

                public int getSize() {
                    return size;
                }

                public void setSize(int size) {
                    this.size = size;
                }

                public int getVd() {
                    return vd;
                }

                public void setVd(int vd) {
                    this.vd = vd;
                }
            }

            public static class PrivilegeBean {
                /**
                 * id : 33911781
                 * fee : 0
                 * payed : 0
                 * st : 0
                 * pl : 320000
                 * dl : 999000
                 * sp : 7
                 * cp : 1
                 * subp : 1
                 * cs : false
                 * maxbr : 999000
                 * fl : 320000
                 * toast : false
                 * flag : 0
                 */

                private int id;
                private int fee;
                private int payed;
                private int st;
                private int pl;
                private int dl;
                private int sp;
                private int cp;
                private int subp;
                private boolean cs;
                private int maxbr;
                private int fl;
                private boolean toast;
                private int flag;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public int getFee() {
                    return fee;
                }

                public void setFee(int fee) {
                    this.fee = fee;
                }

                public int getPayed() {
                    return payed;
                }

                public void setPayed(int payed) {
                    this.payed = payed;
                }

                public int getSt() {
                    return st;
                }

                public void setSt(int st) {
                    this.st = st;
                }

                public int getPl() {
                    return pl;
                }

                public void setPl(int pl) {
                    this.pl = pl;
                }

                public int getDl() {
                    return dl;
                }

                public void setDl(int dl) {
                    this.dl = dl;
                }

                public int getSp() {
                    return sp;
                }

                public void setSp(int sp) {
                    this.sp = sp;
                }

                public int getCp() {
                    return cp;
                }

                public void setCp(int cp) {
                    this.cp = cp;
                }

                public int getSubp() {
                    return subp;
                }

                public void setSubp(int subp) {
                    this.subp = subp;
                }

                public boolean isCs() {
                    return cs;
                }

                public void setCs(boolean cs) {
                    this.cs = cs;
                }

                public int getMaxbr() {
                    return maxbr;
                }

                public void setMaxbr(int maxbr) {
                    this.maxbr = maxbr;
                }

                public int getFl() {
                    return fl;
                }

                public void setFl(int fl) {
                    this.fl = fl;
                }

                public boolean isToast() {
                    return toast;
                }

                public void setToast(boolean toast) {
                    this.toast = toast;
                }

                public int getFlag() {
                    return flag;
                }

                public void setFlag(int flag) {
                    this.flag = flag;
                }
            }

            public static class ArBean {
                /**
                 * id : 16906
                 * name : 茅野愛衣
                 * tns : []
                 * alias : []
                 */

                private int id;
                private String name;
                private List<?> tns;
                private List<?> alias;

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

                public List<?> getTns() {
                    return tns;
                }

                public void setTns(List<?> tns) {
                    this.tns = tns;
                }

                public List<?> getAlias() {
                    return alias;
                }

                public void setAlias(List<?> alias) {
                    this.alias = alias;
                }
            }
        }
    }
}
