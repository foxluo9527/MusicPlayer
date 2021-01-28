package com.example.musicplayerdome.ui.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.musicplayerdome.R;
import com.example.musicplayerdome.adapter.HotListAdapter;
import com.example.musicplayerdome.adapter.NewAlbumListAdapter;
import com.example.musicplayerdome.bean.MyAlbumBean;
import com.example.musicplayerdome.bean.MyPlayListBean;
import com.example.musicplayerdome.ui.base.BaseActivity;
import com.example.musicplayerdome.ui.fragment.FragmentRecommend;
import com.example.musicplayerdome.ui.fragment.FramentMusicbar;
import com.example.musicplayerdome.utils.HttpRequest;

import java.util.ArrayList;

public class NewAlbumActivity extends BaseActivity implements NewAlbumListAdapter.OnRecyclerViewItemListener{
    FramentMusicbar musicBar;
    private FragmentManager manager;
    Context context;
    ArrayList<MyAlbumBean> myAlbumBeans=new ArrayList<>();
    NewAlbumListAdapter newAlbumListAdapter;
    RecyclerView newAlbumRecyclerView;
    int nowPage=0;
    boolean loadDone=false;
    @Override
    protected int getResId() {
        return R.layout.activity_new_album;
    }

    @Override
    protected void onConfigView() {
        context=this;
        newAlbumRecyclerView=findViewById(R.id.new_album_view);
    }

    @Override
    protected void setLocalClassName() {
        this.lockAppName+=this.getLocalClassName();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initData() {
        musicBar=new FramentMusicbar();
        manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.music_bar,musicBar);
        transaction.show(musicBar).commitAllowingStateLoss();
        StaggeredGridLayoutManager hotGridLayoutManager=new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        newAlbumRecyclerView.setLayoutManager(hotGridLayoutManager);
        newAlbumListAdapter = new NewAlbumListAdapter(myAlbumBeans, context);
        newAlbumRecyclerView.setAdapter(newAlbumListAdapter);
        newAlbumListAdapter.setOnRecyclerViewItemListener(NewAlbumActivity.this::onItemClickListener);
        getNewAlbum();

        newAlbumRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (HotListActivity.isSlideToBottom(newAlbumRecyclerView)){
                    if (loadDone){
                        Toast.makeText(context,"人家也是有底线的",Toast.LENGTH_SHORT).show();
                    }else {
                        getNewAlbum();
                    }
                }
            }
        });
    }

    public void doClick(View view) {
        if (view.getId()==R.id.btn_return){
            finish();
        }
    }

    @Override
    public void onItemClickListener(View view, int position) {
        Intent intent=new Intent(context, PlayOnlineAlbumActivity.class);
        intent.putExtra("albumId",myAlbumBeans.get(position).getId());
        startActivity(intent);
    }
    private void getNewAlbum(){
        showLoadingDialog();
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ArrayList<MyAlbumBean> tempList= HttpRequest.getNewAlbumList(21,nowPage*21);
                if (tempList!=null){
                    myAlbumBeans.addAll(tempList);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                dismissLoadingDialog();
                if (myAlbumBeans.size()>0) {
                    if (myAlbumBeans.size()%21==0){
                        nowPage++;
                    }else {
                        loadDone=true;
                    }
                    newAlbumListAdapter.notifyDataSetChanged();
                }
            }
        }.execute();
    }
}
