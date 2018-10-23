package com.rashmi.rrp.blogspot;

import java.util.Date;

public class BlogPost extends BlogPostId {

    public String userId, imageUrl, desc, thumbUrl;
    public Date timestamp;

    public BlogPost() {}

    public BlogPost(String userId, String imageUrl, String desc, String thumbUrl, Date timestamp) {
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.desc = desc;
        this.thumbUrl = thumbUrl;
        this.timestamp = timestamp;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
