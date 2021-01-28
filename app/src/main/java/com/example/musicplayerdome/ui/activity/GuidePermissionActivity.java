package com.example.musicplayerdome.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.utils.ActivityManager;
import com.example.musicplayerdome.utils.ImgHelper;

public class GuidePermissionActivity extends AppCompatActivity {
    ImageView guideImg;
    Button enterBtn;
    public static final int GUIDE_LOCK_PERMISSION=1;
    public static final int GUIDE_FLOAT_PERMISSION=2;
    int guideModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
        setContentView(R.layout.activity_guide_permission);
        if (getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        initView();
        initData();
    }

    private void initView(){
        guideImg=findViewById(R.id.guide_img);
        enterBtn=findViewById(R.id.enter_btn);
    }
    Bitmap srcBitmap;
    private void initData() {
        guideModel=getIntent().getIntExtra("GUIDE_MODEL",-1);
        if (guideModel==-1){
            finish();
            return;
        }
        if (guideModel==GUIDE_FLOAT_PERMISSION){
            Glide.with(this)
                    .load(R.drawable.guide_open_float_permission)
                    .into(new SimpleTarget<Drawable>() {
                        @Override public void onResourceReady(
                                @NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            srcBitmap= ImgHelper.drawableToBitmap(resource);
                            guideImg.setImageBitmap(srcBitmap);
                            WindowManager windowManager = (WindowManager) GuidePermissionActivity.this.getSystemService(Context.WINDOW_SERVICE);
                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
                            //屏幕宽度
                            float width = displayMetrics.widthPixels;
                            //缩放比例
                            float scale = width / srcBitmap.getWidth();
                            //缩放后的宽度和高度
                            int afterWidth = (int) (srcBitmap.getWidth() * scale);
                            int afterHeight = (int) (srcBitmap.getHeight() * scale);
                            ViewGroup.LayoutParams lp = guideImg.getLayoutParams();
                            lp.width = afterWidth;
                            lp.height = afterHeight;
                            guideImg.setLayoutParams(lp);
                        }
                    });
        }else if(guideModel==GUIDE_LOCK_PERMISSION){
            Glide.with(this)
                    .load(R.drawable.guide_open_lock_permission)
                    .into(new SimpleTarget<Drawable>() {
                        @Override public void onResourceReady(
                                @NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            srcBitmap= ImgHelper.drawableToBitmap(resource);
                            guideImg.setImageBitmap(srcBitmap);
                            WindowManager windowManager = (WindowManager) GuidePermissionActivity.this.getSystemService(Context.WINDOW_SERVICE);
                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
                            //屏幕宽度
                            float width = displayMetrics.widthPixels;
                            //缩放比例
                            float scale = width / srcBitmap.getWidth();
                            //缩放后的宽度和高度
                            int afterWidth = (int) (srcBitmap.getWidth() * scale);
                            int afterHeight = (int) (srcBitmap.getHeight() * scale);
                            ViewGroup.LayoutParams lp = guideImg.getLayoutParams();
                            lp.width = afterWidth;
                            lp.height = afterHeight;
                            guideImg.setLayoutParams(lp);
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (srcBitmap!=null&&!srcBitmap.isRecycled()){
            srcBitmap.recycle();
        }
    }

    public void doClick(View view) {
        if (view.getId()==R.id.enter_btn){
            if (guideModel==GUIDE_FLOAT_PERMISSION){
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1010);
            }else if(guideModel==GUIDE_LOCK_PERMISSION){
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.fromParts("package", this.getPackageName(), null));
                if (intent.resolveActivity(this.getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        }
        finish();
    }
}
