package com.example.musicplayerdome.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.adapter.MusicListAdapter;
import com.example.musicplayerdome.adapter.PlayListAdapter;
import com.example.musicplayerdome.adapter.SearchMusicListAdapter;
import com.example.musicplayerdome.adapter.TabPageAdapter;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.bean.MyMusicSearchBean;
import com.example.musicplayerdome.bean.PlayMusicBean;
import com.example.musicplayerdome.bean.SongList;
import com.example.musicplayerdome.dao.MusicDaoImpl;
import com.example.musicplayerdome.service.MusicService;
import com.example.musicplayerdome.ui.base.BaseActivity;
import com.example.musicplayerdome.ui.component.CircleImageView;
import com.example.musicplayerdome.ui.fragment.FragmentSearchAlbum;
import com.example.musicplayerdome.ui.fragment.FragmentSearchList;
import com.example.musicplayerdome.ui.fragment.FragmentSearchSong;
import com.example.musicplayerdome.ui.fragment.FramentMusicbar;
import com.example.musicplayerdome.utils.HttpRequest;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import static com.example.musicplayerdome.service.MusicService.VAL_UPDATE_UI_PLAY;
import static com.example.musicplayerdome.service.MusicService.position;
import static com.example.musicplayerdome.ui.activity.DetailsActivity.changeRuleImg;
import static com.example.musicplayerdome.ui.activity.DetailsActivity.setRuleText;

public class SearchResultActivity extends BaseActivity{
    TextView searchText;
    String searchString;
    FragmentSearchSong fragmentSearchSong;
    FragmentSearchAlbum fragmentSearchAlbum;
    FragmentSearchList fragmentSearchList;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    List<Fragment> fragmentList;
    FramentMusicbar musicBar;
    private FragmentManager manager;
    @Override
    protected int getResId() {
        return R.layout.activity_search_result;
    }

    @Override
    protected void onConfigView() {
        searchText=findViewById(R.id.search_text);
        viewPager=findViewById(R.id.main_view);
        viewPager.setOffscreenPageLimit(2);
        tabLayout=findViewById(R.id.search_tab_view);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initData() {
        searchString=getIntent().getStringExtra("searchString");
        searchText.setText(searchString);
        musicBar=new FramentMusicbar();
        manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.music_bar,musicBar);
        transaction.show(musicBar).commitAllowingStateLoss();
        initFragment();
        String[] mTabNames=new String[]{"单曲","专辑","歌单"};
        TabPageAdapter pageAdapter = new TabPageAdapter(getSupportFragmentManager(), fragmentList,mTabNames){
            @Override
            public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                super.setPrimaryItem(container, position, object);
            }
        };
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void setLocalClassName() {
        this.lockAppName+=this.getLocalClassName();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void doClick(View view) {
        switch (view.getId()){
            case R.id.cancel:
                finish();
                break;
            case R.id.search_lineLayout:
                startActivity(new Intent(SearchResultActivity.this,SearchActivity.class));
                finish();
                break;
        }
    }
    private void initFragment(){
        fragmentList=new ArrayList<Fragment>();
        fragmentSearchSong=new FragmentSearchSong(searchString);
        fragmentSearchAlbum=new FragmentSearchAlbum(searchString);
        fragmentSearchList=new FragmentSearchList(searchString);

        fragmentList.add(fragmentSearchSong);
        fragmentList.add(fragmentSearchAlbum);
        fragmentList.add(fragmentSearchList);
    }
}
