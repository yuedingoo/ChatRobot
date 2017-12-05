package com.yueding.chatrobot.chat;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import static java.text.DateFormat.getDateTimeInstance;

/**
 * Created by yueding on 2017/11/30.
 */

public class ChatItem implements MultiItemEntity {
    private String time;
    private int icon;
    private String name;
    private String message;
    private int msgCode;
    private int itemType;
    private String image;

    public static final int LEFT = 100;
    public static final int RIGHT = 200;
    public static final int IMAGE = 300;

    public ChatItem(int itemType) {
        this.itemType = itemType;
        setDate(new Date());
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public String getTime() {
        return time;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private void setDate(Date date) {
        Date date1 = date;
        DateFormat df = getDateTimeInstance();
        this.time = df.format(date);
    }

    public int getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(int msgCode) {
        this.msgCode = msgCode;
    }
}
