package com.example.musicplayerdome.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.musicplayerdome.R;
import com.example.musicplayerdome.adapter.LrcMatchAdapter;
import com.example.musicplayerdome.adapter.LrcViewAdapter;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.service.MusicService;
import com.example.musicplayerdome.ui.base.BaseActivity;
import com.example.musicplayerdome.ui.component.HorizontalListView;
import com.example.musicplayerdome.utils.HttpRequest;
import com.example.musicplayerdome.utils.Strings;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchLrcActivity extends BaseActivity implements View.OnClickListener,LrcMatchAdapter.OnRecyclerViewItemListener{
    LrcMatchAdapter adapter=null;
    RecyclerView lrcsView;
    EditText editName,editSinger;
    ArrayList<String[]> lrcs;
    public static boolean choice[];
    String musicName,musicSinger;
    View searchLoading;
    long musicId;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    @Override
    protected int getResId() {
        return R.layout.activity_search_lrc;
    }

    @Override
    protected void onConfigView() {
        lrcsView=findViewById(R.id.lrcs_view);
        editName=findViewById(R.id.edit_name);
        editSinger=findViewById(R.id.edit_singer);
        searchLoading=findViewById(R.id.search_loading_view);
    }

    @Override
    protected void setLocalClassName() {
        this.lockAppName+=this.getLocalClassName();
    }
    @Override
    protected void initData() {
        staggeredGridLayoutManager=new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        lrcsView.setLayoutManager(staggeredGridLayoutManager);
        musicName=getIntent().getStringExtra("musicName");
        musicSinger=getIntent().getStringExtra("musicSinger");
        musicId=getIntent().getLongExtra("musicId",-1);
        editName.setHint(musicName);
        editSinger.setHint(musicSinger);
        editName.setText(musicName);
        editSinger.setText(musicSinger);
        getSongLrcs(musicName,musicSinger);
    }

    public void doClick(View view) {
        switch (view.getId()){
            case R.id.btn_return:
                finish();
                break;
            case R.id.search_btn:
                if (lrcsView.getVisibility()==View.GONE){
                    return;
                }
                String name=editName.getText().toString();
                String singer=editSinger.getText().toString();
                if (name.isEmpty()||name.length()==0){
                    name=musicName;
                }
                if (singer.isEmpty()||singer.length()==0){
                    singer=musicSinger;
                }
                getSongLrcs(name,singer);
                break;
            case R.id.save:
                saveLrc();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int position= (int) v.getTag();
        switch (v.getId()){
            case R.id.radio_choice:
                for (int i=0;i<choice.length;i++){
                    choice[i]=false;
                }
                choice[position]=true;
                adapter.notifyDataSetChanged();
                break;
        }
    }
    private void getSongLrcs(String name,String singer){
        searchLoading.setVisibility(View.VISIBLE);
        lrcsView.setVisibility(View.GONE);
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                lrcs=HttpRequest.getYunFormatSearchLrc(name,singer,5,0);//获取前5首歌歌词
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (lrcs!=null){
                    choice=new boolean[lrcs.size()];
                    for (int i=0;i<choice.length;i++)
                        choice[i]=false;
                    if (adapter==null||LrcMatchAdapter.lrcs==null||LrcMatchAdapter.lrcs.size()==0){
                        adapter=new LrcMatchAdapter(lrcs,SearchLrcActivity.this::onClick,SearchLrcActivity.this);
                        lrcsView.setAdapter(adapter);
                        adapter.setOnRecyclerViewItemListener(SearchLrcActivity.this);
                    }else {
                        LrcMatchAdapter.lrcs.clear();
                        LrcMatchAdapter.lrcs.addAll(lrcs);
                        adapter.notifyDataSetChanged();
                    }
                    searchLoading.setVisibility(View.GONE);
                    lrcsView.setVisibility(View.VISIBLE);
                }
            }
        }.execute();
    }

    @Override
    public void onItemClickListener(View view, int position) {
        for (int i=0;i<choice.length;i++){
            choice[i]=false;
        }
        choice[position]=true;
        adapter.notifyDataSetChanged();
    }
    private void saveLrc(){
        if (choice==null||choice.length==0){
            finish();
            return;
        }
        String fileName=musicName+"-"+musicSinger;
        Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");
        Matcher matcher = pattern.matcher(fileName);
        fileName= matcher.replaceAll("");
        for (int i=0;i<choice.length;i++){
            if (choice[i]){
                if (lrcs.get(i)[0]!=null&&!lrcs.get(i)[0].isEmpty()){
                    String normalLrcFileName=fileName+".lrc";
                    Strings.writeOcrStrtoFile(lrcs.get(i)[0],DetailsActivity.LrcPath,normalLrcFileName);
                }
                File transFile=new File(DetailsActivity.LrcPath+fileName+"-trans.lrc");
                if (transFile.exists()){
                    transFile.delete();
                }
                if (lrcs.get(i)[1]!=null&&!lrcs.get(i)[1].isEmpty()){
                    String extraLrcFileName=fileName+"-trans.lrc";
                    Strings.writeOcrStrtoFile(lrcs.get(i)[1],DetailsActivity.LrcPath,extraLrcFileName);
                }
                break;
            }
        }
        Intent intent=new Intent();
        intent.putExtra("musicId",musicId);
        setResult(2,intent);
        finish();
    }
}
