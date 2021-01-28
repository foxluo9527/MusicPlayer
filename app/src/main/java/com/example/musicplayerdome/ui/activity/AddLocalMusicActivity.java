package com.example.musicplayerdome.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.musicplayerdome.R;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.ui.base.BaseActivity;
import com.example.musicplayerdome.utils.MusicList;
import com.zyao89.view.zloading.ZLoadingView;

import java.util.ArrayList;

public class AddLocalMusicActivity extends BaseActivity {
    ZLoadingView zLoadingView;
    Button cancelBtn,addBtn;
    View tryAgainView;
    TextView musicNum;
    ArrayList<Music> musicList=new ArrayList<>();
    @Override
    protected int getResId() {
        return R.layout.activity_add_local_music;
    }

    @Override
    protected void onConfigView() {
        zLoadingView = (ZLoadingView) findViewById(R.id.loadding);
        zLoadingView.setColorFilter(Color.BLACK);//设置颜色
        cancelBtn=findViewById(R.id.cancel);
        addBtn=findViewById(R.id.add);
        tryAgainView=findViewById(R.id.try_again_view);
        musicNum=findViewById(R.id.music_num);
    }

    @Override
    protected void initData() {
        new Thread(searchMusicRunnable).start();
    }

    @Override
    protected void setLocalClassName() {
        this.lockAppName+=this.getLocalClassName();
    }

    Runnable searchMusicRunnable=new Runnable() {
        @Override
        public void run() {
            try {
                zLoadingView.setVisibility(View.VISIBLE);
                musicList= MusicList.getLocalList(AddLocalMusicActivity.this);
                if (musicList!=null){
                    addHandler.sendEmptyMessage(1);
                }else {
                    addHandler.sendEmptyMessage(2);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    Handler addHandler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            zLoadingView.setVisibility(View.INVISIBLE);
            cancelBtn.setVisibility(View.GONE);
            switch (msg.what){
                case 1: //加载成功
                    addBtn.setVisibility(View.VISIBLE);
                    tryAgainView.setVisibility(View.GONE);
                    musicNum.setText(musicList.size()+"");
                    break;
                case 2://加载失败
                    tryAgainView.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
    public void doClick(View view) {
        switch (view.getId()){
            case R.id.btn_return:
            case R.id.cancel:
                finish();
                break;
            case R.id.try_again:
                new Thread(searchMusicRunnable).start();
                break;
            case R.id.add:
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("listMusic",musicList);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(2,intent);
                finish();
                break;
        }
    }
}
