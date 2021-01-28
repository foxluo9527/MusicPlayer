package com.example.musicplayerdome.bean;

import java.util.List;

public class BannerBean {

    /**
     * banners : [{"pic":"http://p3.music.126.net/s25q2x5QyqsAzilCurD-2w==/7973658325212564.jpg","targetType":10,"adLocation":null,"url":"","targetId":3165446,"exclusive":false,"adid":null,"monitorClick":null,"monitorImpress":null,"monitorType":null,"monitorBlackList":null,"adSource":null,"extMonitor":null,"extMonitorInfo":null,"typeTitle":"新碟首发","monitorClickList":[],"monitorImpressList":[],"encodeId":"3165446","titleColor":"red"},{"pic":"http://p4.music.126.net/V9-MXz6b2MNhEKjutoDWIg==/7937374441542745.jpg","targetType":1005,"adLocation":null,"url":"http://music.163.com/m/topic/170001","targetId":170001,"exclusive":false,"adid":null,"monitorClick":null,"monitorImpress":null,"monitorType":null,"monitorBlackList":null,"adSource":null,"extMonitor":null,"extMonitorInfo":null,"typeTitle":"专栏","monitorClickList":[],"monitorImpressList":[],"encodeId":"170001","titleColor":"blue"},{"pic":"http://p4.music.126.net/CTU5B9R9y3XyYBZXJUXzTg==/2897213141428023.jpg","targetType":1005,"adLocation":null,"url":"http://music.163.com/m/topic/169001","targetId":169001,"exclusive":false,"adid":null,"monitorClick":null,"monitorImpress":null,"monitorType":null,"monitorBlackList":null,"adSource":null,"extMonitor":null,"extMonitorInfo":null,"typeTitle":"专栏","monitorClickList":[],"monitorImpressList":[],"encodeId":"169001","titleColor":"blue"},{"pic":"http://p4.music.126.net/tGPljf-IMOCyPvumoWLOTg==/7987951976374270.jpg","targetType":1000,"adLocation":null,"url":"","targetId":81662699,"exclusive":false,"adid":null,"monitorClick":null,"monitorImpress":null,"monitorType":null,"monitorBlackList":null,"adSource":null,"extMonitor":null,"extMonitorInfo":null,"typeTitle":"歌单","monitorClickList":[],"monitorImpressList":[],"encodeId":"81662699","titleColor":"red"},{"pic":"http://p4.music.126.net/mp2Y2n4ueZzIj6JSnUOdtw==/7875801790676538.jpg","targetType":3000,"adLocation":null,"url":"http://wlj2015.qq.com/","targetId":0,"exclusive":false,"adid":null,"monitorClick":null,"monitorImpress":null,"monitorType":null,"monitorBlackList":null,"adSource":null,"extMonitor":null,"extMonitorInfo":null,"typeTitle":"广告","monitorClickList":[],"monitorImpressList":[],"encodeId":"0","titleColor":"blue"},{"pic":"http://p3.music.126.net/e0gGadEhjur2UuUpDF9hPg==/7788940372125389.jpg","targetType":10,"adLocation":null,"url":"","targetId":3165337,"exclusive":false,"adid":null,"monitorClick":null,"monitorImpress":null,"monitorType":null,"monitorBlackList":null,"adSource":null,"extMonitor":null,"extMonitorInfo":null,"typeTitle":"新碟首发","monitorClickList":[],"monitorImpressList":[],"encodeId":"3165337","titleColor":"red"}]
     * code : 200
     */

    private int code;
    private List<BannersBean> banners;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<BannersBean> getBanners() {
        return banners;
    }

    public void setBanners(List<BannersBean> banners) {
        this.banners = banners;
    }

    public static class BannersBean {
        /**
         * pic : http://p3.music.126.net/s25q2x5QyqsAzilCurD-2w==/7973658325212564.jpg
         * targetType : 10
         * adLocation : null
         * url :
         * targetId : 3165446
         * exclusive : false
         * adid : null
         * monitorClick : null
         * monitorImpress : null
         * monitorType : null
         * monitorBlackList : null
         * adSource : null
         * extMonitor : null
         * extMonitorInfo : null
         * typeTitle : 新碟首发
         * monitorClickList : []
         * monitorImpressList : []
         * encodeId : 3165446
         * titleColor : red
         */

        private String pic;
        private int targetType;
        private Object adLocation;
        private String url;
        private int targetId;
        private boolean exclusive;
        private Object adid;
        private Object monitorClick;
        private Object monitorImpress;
        private Object monitorType;
        private Object monitorBlackList;
        private Object adSource;
        private Object extMonitor;
        private Object extMonitorInfo;
        private String typeTitle;
        private String encodeId;
        private String titleColor;
        private List<?> monitorClickList;
        private List<?> monitorImpressList;

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public int getTargetType() {
            return targetType;
        }

        public void setTargetType(int targetType) {
            this.targetType = targetType;
        }

        public Object getAdLocation() {
            return adLocation;
        }

        public void setAdLocation(Object adLocation) {
            this.adLocation = adLocation;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getTargetId() {
            return targetId;
        }

        public void setTargetId(int targetId) {
            this.targetId = targetId;
        }

        public boolean isExclusive() {
            return exclusive;
        }

        public void setExclusive(boolean exclusive) {
            this.exclusive = exclusive;
        }

        public Object getAdid() {
            return adid;
        }

        public void setAdid(Object adid) {
            this.adid = adid;
        }

        public Object getMonitorClick() {
            return monitorClick;
        }

        public void setMonitorClick(Object monitorClick) {
            this.monitorClick = monitorClick;
        }

        public Object getMonitorImpress() {
            return monitorImpress;
        }

        public void setMonitorImpress(Object monitorImpress) {
            this.monitorImpress = monitorImpress;
        }

        public Object getMonitorType() {
            return monitorType;
        }

        public void setMonitorType(Object monitorType) {
            this.monitorType = monitorType;
        }

        public Object getMonitorBlackList() {
            return monitorBlackList;
        }

        public void setMonitorBlackList(Object monitorBlackList) {
            this.monitorBlackList = monitorBlackList;
        }

        public Object getAdSource() {
            return adSource;
        }

        public void setAdSource(Object adSource) {
            this.adSource = adSource;
        }

        public Object getExtMonitor() {
            return extMonitor;
        }

        public void setExtMonitor(Object extMonitor) {
            this.extMonitor = extMonitor;
        }

        public Object getExtMonitorInfo() {
            return extMonitorInfo;
        }

        public void setExtMonitorInfo(Object extMonitorInfo) {
            this.extMonitorInfo = extMonitorInfo;
        }

        public String getTypeTitle() {
            return typeTitle;
        }

        public void setTypeTitle(String typeTitle) {
            this.typeTitle = typeTitle;
        }

        public String getEncodeId() {
            return encodeId;
        }

        public void setEncodeId(String encodeId) {
            this.encodeId = encodeId;
        }

        public String getTitleColor() {
            return titleColor;
        }

        public void setTitleColor(String titleColor) {
            this.titleColor = titleColor;
        }

        public List<?> getMonitorClickList() {
            return monitorClickList;
        }

        public void setMonitorClickList(List<?> monitorClickList) {
            this.monitorClickList = monitorClickList;
        }

        public List<?> getMonitorImpressList() {
            return monitorImpressList;
        }

        public void setMonitorImpressList(List<?> monitorImpressList) {
            this.monitorImpressList = monitorImpressList;
        }
    }
}
