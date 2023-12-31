package com.example.testcameravieww;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.crop_picture.cropper.CropImage;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.gesture.Gesture;
import com.otaliastudios.cameraview.gesture.GestureAction;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    CameraView cameraView;
    CardView takenPicture;
    File cacheDir;
    ImageView img;
    LruCache<String, Bitmap> mMemoryCache;
    ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == Activity.RESULT_OK) {
                        Intent data = o.getData();
                        if (data == null) {
                            return;
                        }
                        Uri uri = data.getData();
                        File mediaFile = new File(uri.getPath());

                        Log.e("dfdf", "size image: " + mediaFile.length());

                        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), uri.getAuthority() + ".jpeg")));

                        uCrop = basicConfig(uCrop);
                        uCrop = advancedConfig(uCrop);


                        uCrop.start(MainActivity.this);

                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        pickFromGallery();
        img = findViewById(R.id.gif_download);
        cameraView = findViewById(R.id.camera);
        takenPicture = findViewById(R.id.action_taken);
        cameraView.setLifecycleOwner(this);


        Glide.with(this).load(R.drawable.gif_download2).into(img);

        cameraView.mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS);
        cameraView.mapGesture(Gesture.LONG_TAP, GestureAction.AUTO_FOCUS);

        takenPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.takePicture();
            }
        });


        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                try {
                    handleSaveToInternal(result);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            }
        });
    }

    private void handleSaveToInternal(PictureResult result) throws IOException {
        try {
            String NamePicture = "takenPicture.jpeg";
            File DirectoryInternal = new File(getFilesDir(), "picture_taken");
            File filePictureTaken = new File(DirectoryInternal, NamePicture);
            if (!DirectoryInternal.exists()) {
                DirectoryInternal.mkdirs();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(filePictureTaken);
            fileOutputStream.write(result.getData());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        startCrop();
    }

    private void startCrop() throws IOException {

        File directoryInternal = new File(getFilesDir(), "picture_taken");
        File directoryInternalCropped=new File(getFilesDir(),"picture_cropped");

        File fileDestinationFile=File.createTempFile("cropped_image",".jpeg",directoryInternalCropped);
        if (!directoryInternalCropped.exists()){
            directoryInternalCropped.mkdirs();
        }

        Uri destinationUri=Uri.fromFile(fileDestinationFile);

        File imageFile = new File(directoryInternal, "takenPicture.jpeg");

        Uri imageUri = null;
        if (imageFile.exists()) {
            imageUri = FileProvider.getUriForFile(this, "com.example.ucrop.provider", imageFile);
            UCrop uCrop = UCrop.of(imageUri, destinationUri);

            uCrop = basicConfig(uCrop);
            uCrop = advancedConfig(uCrop);

            uCrop.start(MainActivity.this);
        } else {
            Log.e("dfdf", "file not exists: ");
        }


    }
    private UCrop basicConfig(UCrop uCrop) {
        uCrop = uCrop.useSourceImageAspectRatio();
        return uCrop;
    }


    private UCrop advancedConfig(UCrop uCrop) {

        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setCompressionQuality(100);
        options.setFreeStyleCropEnabled(true);
        options.setHideBottomControls(true);

        return uCrop.withOptions(options);
    }
    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*")
                .addCategory(Intent.CATEGORY_OPENABLE);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

}