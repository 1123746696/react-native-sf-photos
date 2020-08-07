package com.sf.sfimagepicker;

import android.graphics.Bitmap;

public class Image2 {
    private int id;
    private String path;
    private String bucket;
    private String Date;
    private boolean isSelect;
    private boolean isVideo;
    private Bitmap thumb;

    public Image2(int id,String path, String bucket,String date) {
        this.id = id;
        this.path = path;
        this.bucket = bucket;
        this.Date = date;
        isSelect = false;
        isVideo = false;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }
}
