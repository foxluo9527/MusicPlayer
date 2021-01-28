package com.example.musicplayerdome.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.musicplayerdome.R;
import com.example.musicplayerdome.adapter.SearchPlayListAdapter;
import com.example.musicplayerdome.bean.PlayListSearchBean;
import com.example.musicplayerdome.ui.activity.PlayOnlineListActivity;
import com.example.musicplayerdome.ui.base.BaseFragment;
import com.example.musicplayerdome.utils.HttpRequest;

import java.util.ArrayList;

public class FragmentSearchList extends BaseFragment {
    String searchString;
    int nowPage=0;
    SearchPlayListAdapter adapter;
    ListView listView;
    boolean searchDone=false;
    ArrayList<PlayListSearchBean.ResultBean.PlaylistsBean> playlistsBeans=new ArrayList<>();
    Context context;

    public FragmentSearchList(String searchString) {
        super();
        this.searchString=searchString;
    }

    @Override
    protected int getResId() {
        return R.layout.search_list_view;
    }

    @Override
    protected void onConfigView() {
        listView=getActivity().findViewById(R.id.list_view);
    }

    @Override
    protected void initData() {
        context=getContext();
        adapter=new SearchPlayListAdapter(playlistsBeans,context);
        listView.setAdapter(adapter);
        getSearchList();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(), PlayOnlineListActivity.class);
                intent.putExtra("playListId",playlistsBeans.get(position).getId());
                startActivity(intent);
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    View lastVisibleItemView = listView.getChildAt(listView.getChildCount() - 1);
                    if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == listView.getHeight()) {
                       getSearchList();
                    }
                }
            }
        });
    }
    boolean onLoading=false;
    ArrayList<PlayListSearchBean.ResultBean.PlaylistsBean> tempLists;
    private void getSearchList(){
        if (onLoading){
            return;
        }
        if (searchDone){
            Toast.makeText(context,"人家也是有底线的",Toast.LENGTH_SHORT).show();
            return;
        }
        showLoadingDialog();
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                onLoading=true;
                tempLists= HttpRequest.getYunSearchPlayList(searchString,12,nowPage*12);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                onLoading=false;
                dismissLoadingDialog();
                if (tempLists!=null){
                    if (tempLists.size()==12){
                        nowPage++;
                    }else {
                        searchDone=true;
                    }
                    playlistsBeans.addAll(tempLists);
                    adapter.notifyDataSetChanged();
                }else {
                    nowPage++;
                    Toast.makeText(context,"网络好像开小差了...",Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
