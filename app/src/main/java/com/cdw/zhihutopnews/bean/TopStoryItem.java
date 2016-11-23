package com.cdw.zhihutopnews.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by CDW on 2016/11/3.
 */

public class TopStoryItem {
    /**
     * image : http://pic3.zhimg.com/ce435b27cf810d7c0cebc4dce87cc34a.jpg
     * type : 0
     * id : 8998520
     * ga_prefix : 112117
     * title : 知乎好问题 · 有氧运动要超过一定时间才有减脂作用吗？
     */
    @SerializedName("image")
    private String image;
    @SerializedName("type")
    private int type;
    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;

    public void setImage(String image) {
        this.image = image;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public int getType() {
        return type;
    }

    public String getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }


}
