package jsc.exam.jsckit.ui;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import jsc.exam.jsckit.R;
import jsc.kit.baseui.APhotoActivity;
import jsc.kit.entity.CropConfig;
import jsc.kit.utils.CustomPermissionChecker;

public class PhotoActivity extends APhotoActivity {

    final String TAG = getClass().getSimpleName();
    ImageView ivPhoto;
    boolean needCrop = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        setTitle(getClass().getSimpleName().replace("Activity", ""));

        ivPhoto = findViewById(R.id.iv_photo);
        checkPermissions(0, new CustomPermissionChecker.OnCheckListener() {
            @Override
            public void onResult(int requestCode, boolean isAllGranted, @NonNull List<String> grantedPermissions, @NonNull List<String> deniedPermissions, @NonNull List<String> shouldShowPermissions) {

            }

            @Override
            public void onFinally(int requestCode) {
                removePermissionChecker();
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
    }

    public void widgetClick(View view) {
        switch (view.getId()) {
            case R.id.btn_choose:
                needCrop = false;
                openAlbum();
                break;
            case R.id.btn_choose_crop:
                needCrop = true;
                openAlbum();
                break;
            case R.id.btn_take_photo:
                needCrop = false;
                File targetDirFile1 = new File(Environment.getExternalStorageDirectory(), "photoTake");
                openCamera(targetDirFile1);
                break;
            case R.id.btn_take_crop_photo:
                needCrop = true;
                File targetDirFile2 = new File(Environment.getExternalStorageDirectory(), "photoTake");
                openCamera(targetDirFile2);
                break;
        }
    }

    @Override
    public void onPickPhotoResult(Uri uri) {
        String imagePath = getRealImagePathFromUri(uri);
        Log.i(TAG, "onPickPhotoResult: " + imagePath);
        if (imagePath == null || imagePath.trim().length() == 0) {
            showToast("选取图片失败！");
            return;
        }

        if (needCrop) {
            File file = new File(Environment.getExternalStorageDirectory(), "photoCrop");
            CropConfig config = new CropConfig()
                    .setCrop(false)
                    .setCircleCrop(true)
//                    .setAspectX(4)
//                    .setAspectY(3)
//                    .setOutputX(480)
//                    .setOutputY(300)
                    .setScale(true)
                    .setDirectory(file);
            cropPhoto(uri, config);
        } else {
            showImage(imagePath);
        }
    }

    @Override
    public void onTakePhotoResult(Uri uri, @NonNull File tempFile) {
        String imagePath = getRealImagePathFromUri(uri);
        Log.i(TAG, "onPickPhotoResult: " + imagePath);
        Log.i(TAG, "onPickPhotoResult: " + tempFile.getAbsolutePath());
        if (needCrop) {
            File file = new File(Environment.getExternalStorageDirectory(), "photoCrop");
            CropConfig config = new CropConfig()
                    .setAspectX(2)
                    .setAspectY(1)
                    .setDirectory(file);
            cropPhoto(uri, config);
        } else {
            showImage(tempFile);
        }
    }

    @Override
    public void onCropPhotoResult(@Nullable Uri uri, @Nullable File tempFile) {
        if (uri != null){
            String imagePath = getRealImagePathFromUri(uri);
            Log.i(TAG, "onCropPhotoResult: " + imagePath);
        }
        if (tempFile != null){
            Log.i(TAG, "onCropPhotoResult: " + tempFile.getAbsolutePath());
            showImage(tempFile);
        }
    }

    private void showImage(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        ivPhoto.setImageBitmap(bitmap);
        Log.i(TAG, "showImage: " + options.outMimeType);
    }

    private void showImage(File file) {
        showImage(file.getAbsolutePath());
    }

    public void showToast(CharSequence txt) {
        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    }
}
