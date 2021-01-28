package com.example.musicplayerdome.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.service.MusicService;
import com.example.musicplayerdome.ui.base.BaseActivity;
import com.example.musicplayerdome.utils.ImgHelper;
import com.github.florent37.glidepalette.BitmapPalette;
import com.github.florent37.glidepalette.GlidePalette;

import java.io.File;

import static com.example.musicplayerdome.utils.ImgHelper.getRoundedCornerBitmap;

public class ShowSongListActivity extends BaseActivity {
    MultiAutoCompleteTextView introduceTv;
    ImageView coverImg;
    TextView listName;
    View mainView;
    String introduce;
    String name;
    String url;
    int position;
    @Override
    protected int getResId() {
        return R.layout.activity_show_song_list;
    }

    @Override
    protected void onConfigView() {
        introduceTv=findViewById(R.id.list_introduce);
        coverImg=findViewById(R.id.list_cover_img);
        listName=findViewById(R.id.list_name);
        mainView=findViewById(R.id.main_view);
    }

    @Override
    protected void setLocalClassName() {
        this.lockAppName+=this.getLocalClassName();
    }
    @Override
    protected void initData() {
        if (!getIntent().getBooleanExtra("isLocalList",true)){
            findViewById(R.id.btn_edit).setVisibility(View.GONE);
        }else {
            position=getIntent().getIntExtra("position",-1);
            if (position==-1){
                finish();
                return;
            }
        }
        introduce=getIntent().getStringExtra("introduce");
        name=getIntent().getStringExtra("name");
        url=getIntent().getStringExtra("url");
        introduceTv.setText(introduce);
        listName.setText(name);
        Glide.with(this).load(url)
                .listener(GlidePalette.with(url)
                        .use(GlidePalette.Profile.VIBRANT_LIGHT)
                        .intoBackground(mainView)
                        .use(GlidePalette.Profile.MUTED_LIGHT)
                        .intoTextColor(introduceTv)
                        .intoTextColor(listName)
                )
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(
                            @NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Bitmap srcBitmap = ImgHelper.drawableToBitmap(resource);
                        srcBitmap=getRoundedCornerBitmap(srcBitmap,20f);
                        coverImg.setImageBitmap(srcBitmap);
                    }
                });
        coverImg.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                if (url.contains("http")){
                    url= MusicService.proxy.getProxyUrl(url);
                }
                File downloadDir;
                Intent intent=new Intent(ShowSongListActivity.this,ShowImgActivity.class);
                downloadDir = new File(Environment.getExternalStorageDirectory(), "MusicPlayer/downloadImg");
                intent.putExtra("filePath",downloadDir.getPath());
                intent.putExtra("url",url);
                startActivity(intent);
                return true;
            }
        });
    }

    public void doClick(View view) {
        switch (view.getId()){
            case R.id.btn_return:
                finish();
                break;
            case R.id.btn_edit:
                Intent intent=new Intent(ShowSongListActivity.this, EditSongListActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
                finish();
                break;
        }
    }
}
