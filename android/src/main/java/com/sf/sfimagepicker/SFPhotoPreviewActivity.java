package com.sf.sfimagepicker;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.sf.sfimagepicker.fragment.PreviewImageFragment;
import com.sf.sfimagepicker.fragment.PreviewVideoFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-05-25.
 */

public class SFPhotoPreviewActivity extends AppCompatActivity {
    int maxCount = 9;
    ViewPager viewPager;
    PreviewImageItemAdapter adapter;
    List<Fragment> fragmentList;
    CheckBox cb_choose;
    Button btn_send;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_photos);
        init();
    }

    public void init() {
        maxCount = getIntent().getExtras().getInt("number",9);
        viewPager = findViewById(R.id.viewpager);
        cb_choose = findViewById(R.id.cb_choose);
        btn_send = findViewById(R.id.btn_send);
        fragmentList = new ArrayList<>();
        for (int i = 0; i < MediaUtils2.imgList.size(); i++) {
            if(MediaUtils2.imgList.get(i).isVideo()){
                PreviewVideoFragment fragment = new PreviewVideoFragment();
                fragment.setData(MediaUtils2.imgList.get(i));
                fragmentList.add(fragment);
            }else{
                PreviewImageFragment fragment = new PreviewImageFragment();
                fragment.setData(MediaUtils2.imgList.get(i));
                fragmentList.add(fragment);
            }

        }
        adapter = new PreviewImageItemAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                cb_choose.setChecked(MediaUtils2.imgList.get(position).isSelect());
                pausePlay(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(getIntent().getIntExtra("position", 0));
        viewPager.setOffscreenPageLimit(2);
        cb_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!cb_choose.isChecked() || !checkMax()) {
                    MediaUtils2.imgList.get(viewPager.getCurrentItem()).setSelect(cb_choose.isChecked());
                    refresh();
                } else {
                    cb_choose.setChecked(false);
                    Toast.makeText(SFPhotoPreviewActivity.this, "最多只能选择"+maxCount+"张图片或视频", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(200);
                finish();
            }
        });
        refresh();
    }

    public void goback(View v) {
        finish();
    }
    public void pausePlay(int index){
        for(int i=0;i<fragmentList.size();i++){
            if(MediaUtils2.imgList.get(i).isVideo()){
                PreviewVideoFragment fr = (PreviewVideoFragment) fragmentList.get(i);
                if(i!=index){
                    fr.pause();
                }else{
                    fr.play();
                }
            }
        }
    }

    public boolean checkMax() {
        int selectCount = 0;
        for (Image2 image : MediaUtils2.imgList
                ) {
            if (image.isSelect()) selectCount++;
        }
        if (selectCount >= maxCount) {
            return true;
        } else {
            return false;
        }
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
}
