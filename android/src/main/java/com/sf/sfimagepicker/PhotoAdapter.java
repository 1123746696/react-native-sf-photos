package com.sf.sfimagepicker;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sf.sfimagepicker.inter.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by Joker on 2018-05-25.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.Holder> {
    Context context;
    final int mGridWidth;
    OnItemClickListener mOnItemClickListener;
    ContentResolver contentResolver;
    int maxCount;

    public PhotoAdapter(Context context,int maxCount) {
        this.context = context;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            width = size.x;
        }else{
            width = wm.getDefaultDisplay().getWidth();
        }
        mGridWidth = (width-50) / 4;
        this.maxCount = maxCount;
        contentResolver  = context.getContentResolver();
    }
    public void setOnItemClickLitener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        Holder holder = new Holder(LayoutInflater.from(context).inflate(R.layout.item_photos,null));
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.position = position;
        Image2 data = MediaUtils2.imgList.get(position);
        if(data.isVideo()){
            if(data.getThumb()==null){
                MediaMetadataRetriever media = new MediaMetadataRetriever();
                media.setDataSource(data.getPath());
                Bitmap bitmap = media.getFrameAtTime();
                MediaUtils2.imgList.get(position).setThumb(bitmap);
                data.setThumb(bitmap);
            }
            holder.iv_img.setImageBitmap(data.getThumb());
        }else{
            holder.setImage(data.getPath());
        }
        holder.cb_choose.setChecked(MediaUtils2.imgList.get(position).isSelect());
    }

    @Override
    public int getItemCount() {
        return MediaUtils2.imgList.size();
    }

    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
        int position;
        ImageView iv_img;
        CheckBox cb_choose;
        public Holder(View itemView) {
            super(itemView);
            iv_img = itemView.findViewById(R.id.iv_img);
            cb_choose = itemView.findViewById(R.id.cb_choose);
            iv_img.setLayoutParams(new RelativeLayout.LayoutParams(mGridWidth,mGridWidth));
            iv_img.setOnClickListener(this);
            cb_choose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!cb_choose.isChecked() || !checkMax()) {
                        MediaUtils2.imgList.get(position).setSelect(cb_choose.isChecked());
                        ((SFPhotosActivity)context).refresh();
                    } else {
                        cb_choose.setChecked(false);
                        Toast.makeText(context, "最多只能选择"+maxCount+"张图片或视频", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            if(mOnItemClickListener!=null){
                mOnItemClickListener.onItemClick(view,position);
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
        public void setImage(String path){
            Picasso.get().load(Uri.fromFile(new File(path))).resize(mGridWidth,mGridWidth).centerCrop().into(iv_img);
        }
    }
}
