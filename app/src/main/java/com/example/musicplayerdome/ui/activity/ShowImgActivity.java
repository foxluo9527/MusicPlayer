package com.example.musicplayerdome.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.ui.base.BaseActivity;
import com.example.musicplayerdome.utils.ImgHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ShowImgActivity extends BaseActivity {
    String url;
    String path;
    ImageView img;
    Button button;
    Bitmap srcBitmap;
    @Override
    protected int getResId() {
        return R.layout.activity_show_img;
    }

    @Override
    protected void onConfigView() {
        img=findViewById(R.id.imageView);
        button=findViewById(R.id.button);
    }

    @Override
    protected void setLocalClassName() {
        this.lockAppName+=this.getLocalClassName();
    }
    @Override
    protected void initData() {
        url=getIntent().getStringExtra("url");
        path=getIntent().getStringExtra("filePath");
        findViewById(R.id.main_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Glide.with(ShowImgActivity.this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        srcBitmap= ImgHelper.drawableToBitmap(resource);
                        img.setImageBitmap(srcBitmap);
                    }
                });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ShowImgActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};
                    ActivityCompat.requestPermissions(ShowImgActivity.this,mPermissionList,1);
                }else {
                    compressBitmap();//Bitmap的保存
                    Toast.makeText(ShowImgActivity.this,"图片已保存至"+path+"文件夹",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (srcBitmap!=null&&!srcBitmap.isRecycled()){
            srcBitmap.recycle();
        }
    }

    public void compressBitmap() {
        new Thread(){
            @Override
            public void run() {
                Bitmap bitmap = srcBitmap;
                String filePath = path+"/"+ UUID.randomUUID() +".jpg";
                File file = new File(filePath);
                FileOutputStream fos = null;
                try {
                    if (!file.exists()) {
                        // 先得到文件的上级目录，并创建上级目录，在创建文件
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    }
                    fos = new FileOutputStream(filePath );
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},TAKE_PHOTO_REQUEST_CODE);
                Toast.makeText(ShowImgActivity.this, "请打开SD卡写入权限", Toast.LENGTH_SHORT).show();
            }else {
                compressBitmap();//Bitmap的保存
                Toast.makeText(ShowImgActivity.this,"图片已保存至"+path+"文件夹",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
