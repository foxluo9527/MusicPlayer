package com.example.musicplayerdome.ui.activity;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.musicplayerdome.R;
import com.example.musicplayerdome.adapter.RankListAdapter;
import com.example.musicplayerdome.bean.TopListBean;
import com.example.musicplayerdome.ui.base.BaseActivity;
import com.example.musicplayerdome.ui.fragment.FragmentMine;
import com.example.musicplayerdome.ui.fragment.FramentMusicbar;
import com.example.musicplayerdome.utils.HttpRequest;

import java.util.ArrayList;

public class TopListActivity extends BaseActivity {
    FramentMusicbar musicBar;
    private FragmentManager manager;
    ArrayList<TopListBean> topListBeans=new ArrayList<>();
    ListView rankListView;
    RankListAdapter rankListAdapter;
    Context context;
    @Override
    protected int getResId() {
        return R.layout.activity_top_list;
    }

    @Override
    protected void onConfigView() {
        context=this;
        rankListView=findViewById(R.id.new_album_view);
    }

    @Override
    protected void setLocalClassName() {
        this.lockAppName+=this.getLocalClassName();
    }
    @Override
    protected void initData() {
        musicBar=new FramentMusicbar();
        manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.music_bar,musicBar);
        transaction.show(musicBar).commitAllowingStateLoss();
        getRankList();
        rankListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(context, PlayOnlineListActivity.class);
                intent.putExtra("playListId",topListBeans.get(position).getId());
                startActivity(intent);
            }
        });
    }

    public void doClick(View view) {
        if (view.getId()==R.id.btn_return){
            finish();
        }
    }
    private void getRankList(){
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ArrayList<TopListBean> tempList= HttpRequest.getTopList();
                topListBeans.clear();
                if (tempList!=null)
                    topListBeans.addAll(tempList);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (topListBeans.size()>0){
                    rankListAdapter=new RankListAdapter(topListBeans,context);
                    rankListView.setAdapter(rankListAdapter);
                    FragmentMine.setListViewHeightBasedOnChildren(rankListView);
                }
            }
        }.execute();
    }
}
