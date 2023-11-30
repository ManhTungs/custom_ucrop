package com.example.testcameravieww;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.view.CropImageView;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class CropImageActivity extends AppCompatActivity {

    CropImageView cropImageView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(com.example.ucrop.R.layout.ucrop_activity_photobox);
//
//        initView();
    }

    private void initView() throws UnsupportedEncodingException {


        Intent intent=getIntent();

        byte[] data=intent.getExtras().getByteArray("data");
        String s=new String(data,"UTF-8");
        Uri uri=Uri.parse(s);
        UCrop uCrop=UCrop.of(uri, Uri.fromFile(new File(getCacheDir(),s+".jpeg")));

        uCrop=basicConfig(uCrop);
        uCrop=advancedConfig(uCrop);

        uCrop.start(CropImageActivity.this);
    }



    private UCrop basicConfig(UCrop uCrop) {
        uCrop=uCrop.useSourceImageAspectRatio();
        return uCrop;
    }

    private UCrop advancedConfig(UCrop uCrop) {
        UCrop.Options options=new UCrop.Options();

        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(100);
        options.setFreeStyleCropEnabled(true);

        return uCrop.withOptions(options);
    }
}
