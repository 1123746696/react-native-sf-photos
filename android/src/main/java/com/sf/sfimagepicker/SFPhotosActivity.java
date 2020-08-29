package com.sf.sfimagepicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sf.sfimagepicker.inter.OnItemClickListener;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2018-05-25.
 */

public class SFPhotosActivity extends SFBaseActivity implements OnItemClickListener{
    int maxCount = 9;
    int type = 2;
    boolean isCrop = false;
    RecyclerView recyclerView;
    PhotoAdapter adapter;
    Button btn_send;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        initView();
    }
    public void initView(){
        maxCount = getIntent().getExtras().getInt("number",9);
        type = getIntent().getExtras().getInt("type",2);
        isCrop = getIntent().getExtras().getBoolean("isCrop",false);
        if(isCrop){
            maxCount = 1;
            type = 0;
        }
        MediaUtils2.initImageAndVideoData(this,type, new CursorCallback() {
            @Override
            public void onFinishCursor() {
                adapter.notifyDataSetChanged();
            }
        });
        recyclerView = findViewById(R.id.recyclerview);
        btn_send = findViewById(R.id.btn_send);
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        adapter = new PhotoAdapter(this,maxCount);
        adapter.setOnItemClickLitener(this);
        recyclerView.setAdapter(adapter);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isCrop){
                    Image2 img = null;
                    for (Image2 image:MediaUtils2.imgList
                            ) {
                        if(image.isSelect()){
                            img = image;
                            break;
                        }
                    }
                    if(img!=null){
                        startCropping(SFPhotosActivity.this,Uri.fromFile(new File(img.getPath())));
                    }
                }else{
                    setResult(200);
                    finish();
                }
            }
        });
    }
    public void goback(View v){
        finish();
    }

    public void refresh() {
        int selectCount = 0;
        for (Image2 image : MediaUtils2.imgList
                ) {
            if (image.isSelect()) selectCount++;
        }
        if(selectCount == 0){
            btn_send.setEnabled(false);
        }else{
            btn_send.setEnabled(true);
        }
        btn_send.setText("完成" + (selectCount == 0 ? "" : "(" + selectCount + "/" + maxCount + ")"));

    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this,SFPhotoPreviewActivity.class);
        intent.putExtra("position",position);
        intent.putExtra("number",maxCount);
        startActivityForResult(intent,200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200){
            refresh();
            adapter.notifyDataSetChanged();
            if(resultCode == 200){
                if(isCrop){
                    Image2 img = null;
                    for (Image2 image:MediaUtils2.imgList
                         ) {
                        if(image.isSelect()){
                            img = image;
                            break;
                        }
                    }
                    if(img!=null){
                        startCropping(this,Uri.fromFile(new File(img.getPath())));
                    }
                }else{
                    setResult(200);
                    finish();
                }
            }
        }else if (requestCode == UCrop.REQUEST_CROP) {
            if (data != null) {
                final Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    Intent intent = new Intent();
                    intent.putExtra("data",resultUri);
                    intent.putExtra("type",1);
                    setResult(200,intent);
                    finish();
                } else {
                    finish();
                }
            } else {
                finish();
            }
        }
    }
    private void configureCropperColors(UCrop.Options options) {
        int activeWidgetColor = Color.parseColor("#424242");
        int toolbarColor = Color.parseColor("#424242");
        int statusBarColor = Color.parseColor("#424242");
        options.setToolbarColor(toolbarColor);
        options.setStatusBarColor(statusBarColor);
        if (activeWidgetColor == Color.parseColor("#424242")) {
            /*
            Default tint is grey => use a more flashy color that stands out more as the call to action
            Here we use 'Light Blue 500' from https://material.google.com/style/color.html#color-color-palette
            */
            options.setActiveWidgetColor(Color.parseColor("#03A9F4"));
        } else {
            //If they pass a custom tint color in, we use this for everything
            options.setActiveWidgetColor(activeWidgetColor);
        }
    }
    private String getTmpDir(Activity activity) {
        String tmpDir = activity.getCacheDir() + "/react-native-sf-photos";
        Boolean created = new File(tmpDir).mkdir();
        return tmpDir;
    }
    private void startCropping(Activity activity, Uri uri) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(100);
        options.setCircleDimmedLayer(false);
        options.setShowCropGrid(true);
        options.setHideBottomControls(false);
//        if (enableRotationGesture) {
        // UCropActivity.ALL = enable both rotation & scaling
        options.setAllowedGestures(
                UCropActivity.ALL, // When 'scale'-tab active
                UCropActivity.ALL, // When 'rotate'-tab active
                UCropActivity.ALL  // When 'aspect ratio'-tab active
        );
//        }
        configureCropperColors(options);

        UCrop.of(uri, Uri.fromFile(new File(this.getTmpDir(activity), UUID.randomUUID().toString() + ".jpg")))
                .withMaxResultSize(200, 200)
                .withAspectRatio(200, 200)
                .withOptions(options)
                .start(activity);
    }
}
