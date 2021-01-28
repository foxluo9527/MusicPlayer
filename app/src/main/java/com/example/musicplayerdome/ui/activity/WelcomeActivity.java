package com.example.musicplayerdome.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.bean.OneBean;
import com.example.musicplayerdome.ui.base.BaseActivity;
import com.example.musicplayerdome.utils.ActivityManager;
import com.example.musicplayerdome.utils.HttpRequest;
import com.example.musicplayerdome.utils.ImgHelper;
import com.github.florent37.glidepalette.BitmapPalette;
import com.github.florent37.glidepalette.GlidePalette;

public class WelcomeActivity extends BaseActivity {
    View oneImgView,oneTextView;
    EditText oneText;
    ImageView oneImg;
    OneBean oneBean;
    boolean continueJump=true;
    int oneIndex;
    @Override
    protected int getResId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void onConfigView() {
        oneImgView=findViewById(R.id.one_img_view);
        oneTextView=findViewById(R.id.one_view);
        oneText=findViewById(R.id.one_text);
        oneImg=findViewById(R.id.one_img);
    }

    @Override
    protected void setLocalClassName() {
        this.lockAppName+=this.getLocalClassName();
    }
    @Override
    protected void initData() {
        if (ActivityManager.activities.size()>1){
            startActivity(new Intent(WelcomeActivity.this,HomeActivity.class));
            finish();
            return;
        }
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                oneBean=HttpRequest.getOne();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (oneBean!=null){
                    oneIndex=(int)(Math.random()*oneBean.getData().size());
                    oneText.setText(oneBean.getData().get(0).getText());
                    Glide.with(WelcomeActivity.this).load(oneBean.getData().get(0).getSrc())
                            .listener(GlidePalette.with(oneBean.getData().get(0).getSrc())
                                    .use(GlidePalette.Profile.VIBRANT_DARK)
                                    .intoBackground(oneTextView)
                                    .intoBackground(oneImgView)
                            )
                            .into(oneImg);
                }
            }
        }.execute();
        jumpToMainThread.start();
    }
    Thread jumpToMainThread=new Thread(){
        @Override
        public void run() {
            try {
                sleep(7000);
                if (continueJump){
                    startActivity(new Intent(WelcomeActivity.this,HomeActivity.class));
                    finish();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
    public void doClick(View view) {
        switch (view.getId()){
            case R.id.jump:
                continueJump=false;
                startActivity(new Intent(WelcomeActivity.this,HomeActivity.class));
                finish();
                break;
            case R.id.one_img_view:
                continueJump=false;
                startActivity(new Intent(WelcomeActivity.this,HomeActivity.class));
                Intent intent= new Intent(WelcomeActivity.this,WebActivity.class);
                finish();
                intent.putExtra("URLString","http://wufazhuce.com/");
                startActivity(intent);
                break;
        }
    }
}
