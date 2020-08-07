package com.sf.sfimagepicker;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2017-03-02.
 */

public class MediaUtils2 {
    public static List<Image2> imgList = new ArrayList<>();
    public static List<Image2> getSelectPhotos(){
        return imgList;
    }

    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
    public static void initImageAndVideoData(Context context,int type, CursorCallback callback) {
        new ImageAndVideoTase().execute(context, type,callback);
    }
    public static void getAll(int type){
//        photoList.clear();
//        if(type == 2){
//            photoList.addAll(imgList);
//            photoList.addAll(videoList);
//        }else if(type ==0){
//            photoList.addAll(imgList);
//        }else if(type == 1){
//            photoList.addAll(videoList);
//        }
//        Collections.sort(photoList, new sortByDate());
    }
    public static void getAllImages(){
//        photoList.clear();
//        photoList.addAll(imgList);
//        Collections.sort(photoList, new sortByDate());
    }
    public static void getAllVideos(){
//        photoList.clear();
//        photoList.addAll(videoList);
//        Collections.sort(photoList, new sortByDate());
    }


    static class ImageAndVideoTase extends AsyncTask<Object, Object,String> {
        Context context;
        int type;
        CursorCallback callback;
        ArrayList<Image2> images = new ArrayList<>();
        String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATE_ADDED};

        @Override
        protected String doInBackground(Object... params) {
            context = (Context) params[0];
            type = (int)params[1];
            callback = (CursorCallback) params[2];
            imgList.clear();
            images.clear();
            if(type == 0){
                getAllImages();
            }else if(type == 1){
                getAllVideo();
            }else{
                getAllImages();
                getAllVideo();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    images.sort(new Comparator<Image2>() {
                        @Override
                        public int compare(Image2 o1, Image2 o2) {
                            Image2 img1 = o1;
                            Image2 img2 = o2;
                            if(Long.parseLong(img1.getDate()) < Long.parseLong(img2.getDate())){
                                return 1;
                            }
                            return 0;
                        }
                    });
                }else{
                    Collections.sort(images, new sortByDate());
                }
                imgList = images;
                callback.onFinishCursor();
            }
        }

        public void getAllImages() {
            ContentResolver contentResolver = context.getContentResolver();
            if (context != null) {
                Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                        null, MediaStore.Images.Media.DATE_TAKEN + " DESC ");
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                        String bucketName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
                        String date = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED));
                        Image2 image = new Image2(id,path,bucketName,date);
                        images.add(image);
                    }
                }
            }
        }

        public void getAllVideo() {
            ContentResolver contentResolver = context.getContentResolver();
            if (context != null) {
                Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.DATE_ADDED + " DESC");
                if (cursor != null) {
                    int a = 0;
                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                        String bucketName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
                        String date = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED));
                        Image2 video = new Image2(id,path,bucketName,date);
                        video.setVideo(true);
                        images.add(video);
                    }
                }
            }
        }
    }
    static class sortByDate implements Comparator{

        @Override
        public int compare(Object o, Object t1) {
            Image2 img1 = (Image2)o;
            Image2 img2 = (Image2)t1;
            if(Long.parseLong(img1.getDate()) < Long.parseLong(img2.getDate())){
                return 1;
            }

            return -1;
        }
    }
}
