package com.freelance.android.photoblog.models;

import android.util.Log;

import java.util.Date;

/**
 * Created by Kyaw Khine on 04/10/2018.
 */

public class BlogPost {

    private static final String LOG_TAG = BlogPost.class.getName();

    public String user_id, image_url, desc, image_thumb;
    public Date timestamp;

    public BlogPost() {
        Log.i(LOG_TAG, "Test : BlogPost() empty constructor is called...");
    }

    public BlogPost(String user_id, String image_url, String desc, String image_thumb, Date timestamp) {
        Log.i(LOG_TAG, "Test : BlogPost() constructor is called...");

        this.user_id = user_id;
        this.image_url = image_url;
        this.desc = desc;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
