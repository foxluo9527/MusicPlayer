package com.example.musicplayerdome.ui.activity;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.AsyncTask;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.adapter.CommentAdapter;
import com.example.musicplayerdome.adapter.HotCommentAdapter;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.bean.MusicCommentsResultBean;
import com.example.musicplayerdome.ui.base.BaseActivity;
import com.example.musicplayerdome.ui.component.MaxRecyclerView;
import com.example.musicplayerdome.utils.HttpRequest;

import java.util.ArrayList;
import java.util.HashSet;

public class SongCommentsActivity extends BaseActivity {
    Music music;
    TextView commentsCount,songName,songSinger;
    ImageView songCover;
    MaxRecyclerView hotCommentList,commentList;
    ArrayList<MusicCommentsResultBean.HotCommentsBean> hotCommentsBeans=new ArrayList<>();
    ArrayList<MusicCommentsResultBean.CommentsBean> commentsBeans=new ArrayList<>();
    CommentAdapter commentAdapter;
    HotCommentAdapter hotCommentAdapter;
    ScrollView scrollView;
    int nowPage=0;
    @Override
    protected int getResId() {
        return R.layout.activity_song_comments;
    }

    @Override
    protected void onConfigView() {
        commentsCount=findViewById(R.id.comment_count);
        songName=findViewById(R.id.song_name);
        songSinger=findViewById(R.id.song_singer);
        songCover=findViewById(R.id.song_cover);
        hotCommentList=findViewById(R.id.hot_comment_view);
        commentList=findViewById(R.id.comments_view);
        scrollView=findViewById(R.id.comment_scroll);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initData() {
        music=getIntent().getParcelableExtra("music");
        if (music==null||music.getMId()==-1){
            finish();
            return;
        }
        Glide.with(this)
                .load(music.getAlbumImg())
                .placeholder(R.drawable.music)
                .error(R.drawable.music)
                .into(songCover);
        songName.setText(music.getName());
        songSinger.setText(music.getSinger());
        StaggeredGridLayoutManager commentGridLayoutManager=new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        StaggeredGridLayoutManager hotCommentGridLayoutManager=new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        commentList.setLayoutManager(commentGridLayoutManager);
        hotCommentList.setLayoutManager(hotCommentGridLayoutManager);
        hotCommentAdapter=new HotCommentAdapter(hotCommentsBeans,SongCommentsActivity.this);
        commentAdapter=new CommentAdapter(commentsBeans,SongCommentsActivity.this);
        commentList.setAdapter(commentAdapter);
        hotCommentList.setAdapter(hotCommentAdapter);
        commentList.setHasFixedSize(true);
        commentList.setNestedScrollingEnabled(false);
        hotCommentList.setHasFixedSize(true);
        hotCommentList.setNestedScrollingEnabled(false);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 判断 scrollView 当前滚动位置在顶部
                if(scrollView.getScrollY() == 0){
                }

                // 判断scrollview 滑动到底部
                // scrollY 的值和子view的高度一样，这人物滑动到了底部
                if (scrollView.getChildAt(0).getHeight() - scrollView.getHeight()
                        == scrollView.getScrollY()){
                    getComments();
                }
                return false;
            }
        });

        getComments();
    }
    MusicCommentsResultBean musicCommentsResultBean;
    boolean onLoading=false;
    private void getComments(){
        if (onLoading){
            return;
        }
        showLoadingDialog();
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                onLoading=true;
                musicCommentsResultBean= HttpRequest.getComments(music.getMId(),30,nowPage);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                onLoading=false;
                dismissLoadingDialog();
                if (musicCommentsResultBean!=null){
                    if (hotCommentsBeans.size()==0&&musicCommentsResultBean.getHotComments()!=null){
                        commentsCount.setText("("+musicCommentsResultBean.getTotal()+")");
                        hotCommentsBeans.addAll(musicCommentsResultBean.getHotComments());
                        hotCommentAdapter.notifyDataSetChanged();
                    }
                    if (musicCommentsResultBean.getComments().size()==30){
                        nowPage++;
                    }else {
                        Toast.makeText(SongCommentsActivity.this,"人家也是有底线的...",Toast.LENGTH_SHORT).show();
                    }
                    commentsBeans.addAll(musicCommentsResultBean.getComments());
                    commentsBeans=new ArrayList<>(new HashSet<>(commentsBeans));
                    commentAdapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(SongCommentsActivity.this,"网络好像开小差了...",Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
    @Override
    protected void setLocalClassName() {
        this.lockAppName+=this.getLocalClassName();
    }
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    public void doClick(View view) {
        switch (view.getId()){
            case R.id.btn_return:
                finish();
                break;
        }
    }
}
