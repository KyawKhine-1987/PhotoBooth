package com.freelance.android.photoblog.models;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

/**
 * Created by Kyaw Khine on 04/11/2018.
 */

public class BlogPostId {

    @Exclude
    public String BlogPostId;

    public <T extends BlogPostId> T withId(@NonNull final String id) {
        this.BlogPostId = id;
        return (T) this;
    }
}
