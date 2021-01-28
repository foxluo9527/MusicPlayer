package com.example.musicplayerdome.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.musicplayerdome.R;
import com.example.musicplayerdome.adapter.MusicAdapter;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.service.MusicService;
import com.example.musicplayerdome.ui.base.BaseActivity;
import com.example.musicplayerdome.utils.MusicList;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private ListView listView;
    private LinearLayout cur_music;
    private TextView tv_main_title;
    private ArrayList<Music> listMusic;
    private String TAG = "MainActivityLog";
    private MyReceiver myReceiver;

    @Override
    protected int getResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onConfigView() {

    }
    @Override
    public void onNetChange(int netMobile) {
        // TODO Auto-generated method stub
        //在这个判断，根据需要做处理

    }
    @Override
    protected void initData() {
        myReceiver = new MyReceiver(new Handler());
        IntentFilter itFilter = new IntentFilter();
        itFilter.addAction(MusicService.MAIN_UPDATE_UI);
        registerReceiver(myReceiver, itFilter);
        requestPermission();
    }

    @Override
    protected void setLocalClassName() {
        this.lockAppName+=this.getLocalClassName();
    }
    private void initView() {
        listView = this.findViewById(R.id.listView1);
        listMusic = MusicList.getMusicData(getApplicationContext());
        Log.i(TAG, "listMusic.size()=="+listMusic.size());
        MusicAdapter adapter = new MusicAdapter(this, listMusic);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("listMusic",listMusic);
                bundle.putInt("position", position);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });
        cur_music = findViewById(R.id.cur_music);
        tv_main_title = findViewById(R.id.tv_main_title);
        if(MusicService.mlastPlayer != null&&MusicService.listMusic!=null){
            int index=0;
            if (MusicService.mPosition>0){
                index=MusicService.mPosition;
            }
            tv_main_title.setText(listMusic.get(index).getName());
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        private final Handler handler;
        // Handler used to execute code on the UI thread
        public MyReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            // Post the UI updating code to our Handler
            handler.post(new Runnable() {
                @Override
                public void run() {
                    initView();
                }
            });
        }
    }
    @Override
    protected void onResume() {
        initView();
        if (MusicService.mlastPlayer != null&&MusicService.listMusic!=null){
            cur_music.setVisibility(View.VISIBLE);
            tv_main_title = findViewById(R.id.tv_main_title);
            tv_main_title.setText(listMusic.get(MusicService.mPosition).getName());
            cur_music.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    int position = MusicService.mPosition;
                    bundle.putInt("position", position);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(MainActivity.this, DetailsActivity.class);
                    startActivity(intent);
                }
            });
        }else{
            cur_music.setVisibility(View.GONE);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0){
                    for (int i = 0; i < grantResults.length; i++) {

                        int grantResult = grantResults[i];
                        if (grantResult == PackageManager.PERMISSION_DENIED){
                            String s = permissions[i];
                            Toast.makeText(this,"请打开:"+s+"权限", Toast.LENGTH_SHORT).show();
                        }else{
                            initView();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }
    private void requestPermission(){

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()){
            ActivityCompat.requestPermissions(this,permissionList.toArray(new String[permissionList.size()]),1);
        }else {
            initView();
        }
    }
}
