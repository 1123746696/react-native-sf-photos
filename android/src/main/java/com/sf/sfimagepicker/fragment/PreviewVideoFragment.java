package com.sf.sfimagepicker.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sf.sfimagepicker.Image2;
import com.sf.sfimagepicker.R;
import com.sf.sfimagepicker.view.SFVideoPlayerView;

/**
 * Created by Administrator on 2018-04-02.
 */

public class PreviewVideoFragment extends Fragment {
    SFVideoPlayerView videoplayerview;
    Image2 image;
    Button btn_play;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_video_preview,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        videoplayerview = view.findViewById(R.id.videoplayerview);
        btn_play = view.findViewById(R.id.btn_play);
        videoplayerview.setUrl(image.getPath());
        videoplayerview.setPlayCallback(new SFVideoPlayerView.PlayCallback() {
            @Override
            public void onComplete() {
                btn_play.setVisibility(View.VISIBLE);
            }
        });
        btn_play.setVisibility(View.VISIBLE);
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_play.setVisibility(View.INVISIBLE);
                play();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    public void setData(Image2 image){
        this.image = image;
    }

    public void play(){
        if(videoplayerview!=null){
            btn_play.setVisibility(View.GONE);
            videoplayerview.play();
        }
    }
    public void pause(){
        if(videoplayerview!=null){
            btn_play.setVisibility(View.VISIBLE);
            videoplayerview.pause();
        }
    }
}
