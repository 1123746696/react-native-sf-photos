package com.sf.sfimagepicker.fragment;

import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.sf.sfimagepicker.Image2;
import com.sf.sfimagepicker.R;
import com.sf.sfimagepicker.view.MatrixImageView;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Administrator on 2018-04-02.
 */

public class PreviewImageFragment extends Fragment {
    MatrixImageView imageView;
    Image2 image;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_photo_preview,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageView = (MatrixImageView)view.findViewById(R.id.imageview);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(image.getPath());
        Picasso.get().load(Uri.fromFile(new File(image.getPath()))).resize(1200,1200).centerInside().into(imageView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    public void setData(Image2 image){
        this.image = image;
    }


}
