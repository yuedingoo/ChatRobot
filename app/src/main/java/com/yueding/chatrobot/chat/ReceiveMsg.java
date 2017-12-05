package com.yueding.chatrobot.chat;

import java.util.List;

/**
 * Created by yueding on 2017/11/30.
 * 接收到的消息实体类
 */

public class ReceiveMsg {
    private int code;
    private String text;
    private String url;
    private List<NewsItem> list;

    public List<NewsItem> getList() {
        return list;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
