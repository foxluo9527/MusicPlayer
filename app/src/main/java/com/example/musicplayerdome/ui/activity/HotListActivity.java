package com.example.musicplayerdome.ui.activity;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import com.example.musicplayerdome.R;
import com.example.musicplayerdome.adapter.HotListAdapter;
import com.example.musicplayerdome.bean.MyPlayListBean;
import com.example.musicplayerdome.ui.base.BaseActivity;
import com.example.musicplayerdome.ui.fragment.FramentMusicbar;
import com.example.musicplayerdome.utils.HttpRequest;

import java.util.ArrayList;

public class HotListActivity extends BaseActivity implements HotListAdapter.OnRecyclerViewItemListener{
    FramentMusicbar musicBar;
    private FragmentManager manager;
    Context context;
    ArrayList<MyPlayListBean> myPlayListBeans=new ArrayList<>();
    HotListAdapter hotListAdapter;
    RecyclerView hotListRecyclerView;
    int nowPage=0;
    boolean loadDone=false;
    @Override
    protected int getResId() {
        return R.layout.activity_hot_list;
    }

    @Override
    protected void onConfigView() {
        context=this;
        hotListRecyclerView=findViewById(R.id.hot_list_view);
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
        hotListRecyclerView.setLayoutManager(hotGridLayoutManager);
        hotListAdapter = new HotListAdapter(myPlayListBeans, context);
        hotListRecyclerView.setAdapter(hotListAdapter);
        hotListAdapter.setOnRecyclerViewItemListener(HotListActivity.this::onItemClickListener);
        getHotList();

        hotListRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (isSlideToBottom(hotListRecyclerView)){
                    if (loadDone){
                        Toast.makeText(context,"人家也是有底线的",Toast.LENGTH_SHORT).show();
                    }else {
                        getHotList();
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
    private void getHotList(){
        showLoadingDialog();
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ArrayList<MyPlayListBean> tempList= HttpRequest.getRecommendList(21,nowPage*21);
                if (tempList!=null)
                    myPlayListBeans.addAll(tempList);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                dismissLoadingDialog();
                if (myPlayListBeans.size()>0) {
                    if (myPlayListBeans.size()%21==0){
                        nowPage++;
                    }else {
                        loadDone=true;
                    }
                    hotListAdapter.notifyDataSetChanged();
                }
            }
        }.execute();
    }
    public static boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()
                >= recyclerView.computeVerticalScrollRange())
            return true;
        return false;
    }
    @Override
    public void onItemClickListener(View view, int position) {
        Intent intent=new Intent(context, PlayOnlineListActivity.class);
        intent.putExtra("playListId",myPlayListBeans.get(position).getId());
        startActivity(intent);
    }
}
