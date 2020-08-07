package com.sf.sfimagepicker.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;


import com.sf.sfimagepicker.R;

import java.io.IOException;


/**
 * Created by Administrator on 2017-03-03.
 */

public class SFVideoPlayerView extends SurfaceView implements
		SurfaceHolder.Callback {
	private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;
    private String url;
    private Context context;
    private boolean isAutoPlay;
    private PrepareCallback callback;
    private PlayCallback playCallback;
    int screen_width;
    int screen_height;

    public SFVideoPlayerView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setKeepScreenOn(true);
        this.context = context;
        isAutoPlay = true;
        WindowManager wm = (WindowManager)context
                .getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        screen_width = size.x;
        screen_height = size.y;
    }

    public SFVideoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public SFVideoPlayerView(Context context, AttributeSet attrs,
                             int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context ctx, AttributeSet attrs, int defStyle) {
        TypedArray typeArray = ctx.obtainStyledAttributes(attrs,
                R.styleable.SFVideoPlayerView, defStyle, defStyle);

        this.context = ctx;
        WindowManager wm = (WindowManager)context
                .getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        screen_width = size.x;
        screen_height = size.y;
        isAutoPlay = typeArray.getBoolean(
                R.styleable.SFVideoPlayerView_is_auto_play, true);
        initplay();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceHolder = holder;
        startplay();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                stop();
            }
        }
    }

    private void initMedia() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.setDisplay(surfaceHolder);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

    }


    public void initplay() {
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setKeepScreenOn(true);
    }

    private void startplay() {
        Log.e("sf","loadvideo");
        initMedia();
        if (mediaPlayer != null && url != null) {
            try {
                mediaPlayer.setDataSource(getUrl());
                mediaPlayer.prepareAsync();
                mediaPlayer
                        .setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                    if (mediaPlayer.getVideoWidth() > mediaPlayer.getVideoHeight()) {
                                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                                        layoutParams.width = screen_width;
                                        layoutParams.height = mediaPlayer.getVideoHeight()
                                                * screen_width / mediaPlayer.getVideoWidth();
                                        checkWidthHeight(layoutParams);
                                        setLayoutParams(layoutParams);
                                    } else {
                                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                                        layoutParams.width = mediaPlayer.getVideoWidth()
                                                * screen_height / mediaPlayer.getVideoHeight();
                                        layoutParams.height = screen_height;
                                        checkWidthHeight(layoutParams);
                                        setLayoutParams(layoutParams);
                                    }
                                } else if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                    if (mediaPlayer.getVideoWidth() > mediaPlayer.getVideoHeight()){
                                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                                        layoutParams.height = screen_width;
                                        layoutParams.width = mediaPlayer.getVideoWidth() * screen_width
                                                / mediaPlayer.getVideoHeight();
                                        checkWidthHeight(layoutParams);
                                        setLayoutParams(layoutParams);
                                    }else{
                                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                                        layoutParams.height = screen_width;
                                        layoutParams.width = mediaPlayer.getVideoWidth()*screen_width/mediaPlayer.getVideoHeight();
                                        checkWidthHeight(layoutParams);
                                        setLayoutParams(layoutParams);
                                    }

                                }
                                if(isAutoPlay){
                                    mediaPlayer.start();
                                }
                                if (callback!=null){
                                    callback.onPrepared();
                                }
                            }
                        });
                mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer arg0) {
                        // TODO Auto-generated method stub
                        mediaPlayer.reset();
                        try {
                            mediaPlayer.setDataSource(getUrl());
                            mediaPlayer.prepare();
                            if(playCallback!=null){
                                playCallback.onComplete();
                            }
                        } catch (IllegalArgumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Log.e("zq",getWidth()+","+getHeight());
            // Log.e("zq_media",mediaPlayer.getVideoWidth()+","+mediaPlayer.getVideoHeight());
            // 1440,2560
            // 768,576
        }
    }
    private ViewGroup.LayoutParams checkWidthHeight(ViewGroup.LayoutParams layoutParams){
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (layoutParams.width > screen_width){
                layoutParams.width = screen_width;
                layoutParams.height = mediaPlayer.getVideoHeight()*screen_width/mediaPlayer.getVideoWidth();
                return layoutParams;
            }else if (layoutParams.height > screen_height){
                layoutParams.height = screen_height;
                layoutParams.width = mediaPlayer.getVideoWidth()*screen_height/mediaPlayer.getVideoHeight();
                return layoutParams;
            }
        }else if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            if (layoutParams.width > screen_height){
                layoutParams.width = screen_height;
                layoutParams.height = mediaPlayer.getVideoHeight()*screen_height/mediaPlayer.getVideoWidth();
                return layoutParams;
            }else if (layoutParams.height > screen_width){
                layoutParams.height = screen_width;
                layoutParams.width = mediaPlayer.getVideoWidth()*screen_width/mediaPlayer.getVideoHeight();
                return layoutParams;
            }
        }
        return layoutParams;
    }
    public void play(){
        if (mediaPlayer!=null){
            mediaPlayer.start();
        }
    }

    private void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    public void pause(){
        if (mediaPlayer!=null){
            mediaPlayer.pause();
        }
    }
    public void resume(){
        if (mediaPlayer!=null){
            mediaPlayer.start();
        }
    }
    public int getDuration(){
        if (mediaPlayer!=null){
            return mediaPlayer.getDuration();
        }
        return 0;
    }
    public void setSeek(int msec){
        if (mediaPlayer!=null){
            mediaPlayer.seekTo(msec);
        }
    }
    public int getCurrentPosition(){
        if (mediaPlayer!=null){
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }
    public boolean isPlaying(){
        if (mediaPlayer!=null){
            return mediaPlayer.isPlaying();
        }else{
            return false;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public void reSize(){
        if (mediaPlayer.getVideoWidth()!=0 && mediaPlayer.getVideoHeight()!=0){
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (mediaPlayer.getVideoWidth() > mediaPlayer.getVideoHeight()) {
                    ViewGroup.LayoutParams layoutParams = getLayoutParams();
                    layoutParams.width = screen_width;
                    layoutParams.height = mediaPlayer.getVideoHeight()
                            * screen_width / mediaPlayer.getVideoWidth();
                    checkWidthHeight(layoutParams);
                    setLayoutParams(layoutParams);
                } else {
                    ViewGroup.LayoutParams layoutParams = getLayoutParams();
                    layoutParams.width = mediaPlayer.getVideoWidth()
                            * screen_height / mediaPlayer.getVideoHeight();
                    layoutParams.height = screen_height;
                    checkWidthHeight(layoutParams);
                    setLayoutParams(layoutParams);
                }
            } else if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (mediaPlayer.getVideoWidth() > mediaPlayer.getVideoHeight()){
                    ViewGroup.LayoutParams layoutParams = getLayoutParams();
                    layoutParams.height = screen_width;
                    layoutParams.width = mediaPlayer.getVideoWidth() * screen_width
                            / mediaPlayer.getVideoHeight();
                    checkWidthHeight(layoutParams);
                    setLayoutParams(layoutParams);
                }else{
                    ViewGroup.LayoutParams layoutParams = getLayoutParams();
                    layoutParams.height = screen_width;
                    layoutParams.width = mediaPlayer.getVideoWidth()*screen_width/mediaPlayer.getVideoHeight();
                    checkWidthHeight(layoutParams);
                    setLayoutParams(layoutParams);
                }

            }
        }
    }

    public boolean isAutoPlay() {
        return isAutoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        isAutoPlay = autoPlay;
    }

    public void setPrepareCallback(PrepareCallback callback){
        this.callback = callback;
    }

    public void setPlayCallback(PlayCallback callback){
        this.playCallback = callback;
    }

    interface PrepareCallback {
        void onPrepared();
    }
    public interface PlayCallback{
        void onComplete();
    }
}
