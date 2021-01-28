package com.example.musicplayerdome.bean;

import java.util.List;

public class OneBean {

    /**
     * code : 200
     * msg : 成功
     * data : [{"src":"http://image.wufazhuce.com/FrLWJZi6cIbDVrNOQjvsz6wMW8wY","text":"我疯狂收集每一个快乐的瞬间，用他们回击每一个糟糕的日子。","day":"VOL.2735\n                                2\n                                Apr 2020"},{"src":"http://image.wufazhuce.com/FvCEfFvwk6BXc7s2AKIxSzHlxlkl","text":"所谓成熟，就是明明该哭该闹，却不言不语地微笑。","day":"VOL.2734\n                                1\n                                Apr 2020"},{"src":"http://image.wufazhuce.com/Fs-2TBEosmmC7OyEi5obCKUHB4-X","text":"不要为明天忧虑。早上醒来，充分地好好活这一天，最近我只留心这件事。现在我不说谎了，读书也逐渐不是为了虚荣与算计。以前老爱仰赖明天、敷衍当下，现在也不会了。只是一天一天，非常珍惜地过日子。","day":"VOL.2733\n                                31\n                                Mar 2020"},{"src":"http://image.wufazhuce.com/Fus8yYXS3aaXw7nKCi-ELpyQrHJ5","text":"美，多少要包含一点偶然。","day":"VOL.2732\n                                30\n                                Mar 2020"},{"src":"http://image.wufazhuce.com/Fv_3N71Fv5Fj8xv1Ba3ni1YhOHAp","text":"我的不幸, 恰恰在于我缺乏拒绝的能力。我害怕一旦拒绝别人, 便会在彼此心里留下永远无法愈合的裂痕。","day":"VOL.2731\n                                29\n                                Mar 2020"},{"src":"http://image.wufazhuce.com/FnRFVe_ALFnzp4FzOVH6UgN3PJQ-","text":"使人疲惫的不是远方的高山，而是鞋子里的一粒沙子。","day":"VOL.2730\n                                28\n                                Mar 2020"},{"src":"http://image.wufazhuce.com/Fl3aMNXxpilxgnlzcVTdTHUi0h3J","text":"星河在上，波光在下\n我在你身边，\n等着你的回答。","day":"VOL.2729\n                                27\n                                Mar 2020"}]
     */

    private int code;
    private String msg;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * src : http://image.wufazhuce.com/FrLWJZi6cIbDVrNOQjvsz6wMW8wY
         * text : 我疯狂收集每一个快乐的瞬间，用他们回击每一个糟糕的日子。
         * day : VOL.2735
         2
         Apr 2020
         */

        private String src;
        private String text;
        private String day;

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }
    }
}
