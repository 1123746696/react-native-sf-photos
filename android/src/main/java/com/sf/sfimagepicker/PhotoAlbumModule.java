package com.sf.sfimagepicker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.imageutils.BitmapUtil;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.sf.sfimagepicker.utils.CheckPermission;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018-05-24.
 */

public class PhotoAlbumModule extends ReactContextBaseJavaModule {
    Callback callback;
    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener(){
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            super.onActivityResult(activity, requestCode, resultCode, data);
            if(requestCode == 200) {
                if (resultCode == 200) {
                    if (data != null) {
                        WritableArray array = new WritableNativeArray();
                        WritableMap map = new WritableNativeMap();
                        Uri uri = data.getExtras().getParcelable("data");
                        map.putString("type", "img");
                        map.putString("imageUrl", uri.toString());
                        map.putString("videoUrl", "");
                        array.pushMap(map);
                        if (callback != null) {
                            callback.invoke(1, array);
                        }//13238052753
                    } else {
                        List<Image2> image = new ArrayList<>();
                        for (Image2 img : MediaUtils2.imgList
                                ) {
                            if (img.isSelect()) {
                                image.add(img);
                            }
                        }
                        ImageToJson(image);
                    }

                }
            }
        }
    };
    public PhotoAlbumModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(mActivityEventListener);
    }

    @Override
    public String getName() {
        return "SFImagePicker";
    }
    @ReactMethod
    public void initPhotos(final Promise promise){
        promise.resolve(0);
    }
    @ReactMethod
    public void getPhotos(final ReadableMap readableMap, final Callback callback){
        final Activity activity = getCurrentActivity();
        final PhotoAlbumModule module = this;
        this.callback = callback;
        if (activity == null) {
            this.callback.invoke(-100,"Activity doesn't exist");
            return;
        }
        CheckPermission.CheckPermission(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new CheckPermission.CheckPermissionCallback() {
            @Override
            public void onResult() {
                int type = 2;
                int number = 9;
                boolean isCrop = false;
                if(readableMap!=null){
                    if(readableMap.getString("type").equals("photos")){
                        type = 0;
                    }else if(readableMap.getString("type").equals("videos")){
                        type = 1;
                    }else{
                        type = 2;
                    }
                    if(readableMap.hasKey("number")){
                        number = readableMap.getInt("number");
                    }
                    if(number==1 && readableMap.hasKey("isCrop")){
                        isCrop = readableMap.getBoolean("isCrop");
                        if(isCrop){
                            type = 0;
                        }
                    }
                }
                final Intent intent = new Intent(activity, SFPhotosActivity.class);
                intent.putExtra("type",type);
                intent.putExtra("number",number);
                intent.putExtra("isCrop",isCrop);
                activity.startActivityForResult(intent, 200);
            }
        });

    }
    public void ImageToJson(List<Image2> list){
        WritableArray array = new WritableNativeArray();
        for(int i=0;i<list.size();i++){
            Image2 img = list.get(i);
            WritableMap map = new WritableNativeMap();
            if(img.isVideo()){
                map.putString("type","video");
                map.putString("imageUrl","data:image/png;base64,"+ bitmapToBase64(img.getThumb()));
                map.putString("videoUrl",Uri.fromFile(new File(img.getPath())).toString());
            }else{
                map.putString("type","img");
                map.putString("imageUrl",Uri.fromFile(new File(img.getPath())).toString());
                map.putString("videoUrl","");
            }
            array.pushMap(map);
        }
        if(callback!=null){
            callback.invoke(0,array);
        }
    }
    public String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
