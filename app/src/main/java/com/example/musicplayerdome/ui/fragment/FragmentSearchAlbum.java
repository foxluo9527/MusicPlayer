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
import com.example.musicplayerdome.adapter.SearchAlbumAdapter;
import com.example.musicplayerdome.bean.AlbumSearchBean;
import com.example.musicplayerdome.ui.activity.PlayOnlineAlbumActivity;
import com.example.musicplayerdome.ui.base.BaseFragment;
import com.example.musicplayerdome.utils.HttpRequest;

import java.util.ArrayList;

public class FragmentSearchAlbum extends BaseFragment {
    String searchString;
    int nowPage=0;
    SearchAlbumAdapter adapter;
    ListView listView;
    boolean searchDone=false;
    ArrayList<AlbumSearchBean.ResultBean.AlbumsBean> albumsBeans=new ArrayList<>();
    Context context;

    public FragmentSearchAlbum(String searchString) {
        super();
        this.searchString = searchString;
    }

    @Override
    protected int getResId() {
        return R.layout.search_album_view;
    }

    @Override
    protected void onConfigView() {
        listView=getActivity().findViewById(R.id.album_view);
    }

    @Override
    protected void initData() {
        context=getContext();
        adapter=new SearchAlbumAdapter(albumsBeans,context);
        listView.setAdapter(adapter);
        getSearchList();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(), PlayOnlineAlbumActivity.class);
                intent.putExtra("albumId",albumsBeans.get(position).getId());
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
    ArrayList<AlbumSearchBean.ResultBean.AlbumsBean> tempalbums;
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
                tempalbums= HttpRequest.getYunSearchAlbum(searchString,20,nowPage*20);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                onLoading=false;
                dismissLoadingDialog();
                if (tempalbums!=null){
                    if (tempalbums.size()==20){
                        nowPage++;
                    }else {
                        searchDone=true;
                    }
                    albumsBeans.addAll(tempalbums);
                    adapter.notifyDataSetChanged();
                }else {
                    nowPage++;
                    Toast.makeText(context,"网络好像开小差了...",Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
