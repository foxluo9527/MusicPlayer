package com.example.musicplayerdome.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayerdome.R;
import com.example.musicplayerdome.adapter.SongListManageAdapter;
import com.example.musicplayerdome.dao.MusicDaoImpl;
import com.example.musicplayerdome.ui.base.BaseActivity;

public class ManageSongListActivity extends BaseActivity implements View.OnClickListener{
    private ListView listView;
    private TextView choiceNum;
    private TextView allChoiceText;
    public static boolean[] choices;
    public static boolean allChoice=false;
    private SongListManageAdapter adapter;
    private Context context;
    @Override
    protected int getResId() {
        return R.layout.activity_manage_song_list;
    }

    @Override
    protected void onConfigView() {
        context=ManageSongListActivity.this;
        listView=findViewById(R.id.song_list_view);
        choiceNum=findViewById(R.id.choice_list_num);
        allChoiceText=findViewById(R.id.all_choice_text);
    }

    @Override
    protected void setLocalClassName() {
        this.lockAppName+=this.getLocalClassName();
    }
    @Override
    protected void initData() {
        choices= new boolean[HomeActivity.songLists.size()];
        for(int i=0;i<choices.length;i++){
            choices[i]=false;
        }
        adapter=new SongListManageAdapter(HomeActivity.songLists,context,this::onClick);
        listView.setAdapter(adapter);
        choiceNum.setText("已选中(0)");
        allChoiceText.setText("全选");
    }

    public void doClick(View view) {
        switch (view.getId()){
            case R.id.btn_return:
                finish();
                break;
            case R.id.all_choice:
                if (choices.length==0){
                    return;
                }
                if (!allChoice){
                    allChoiceText.setText("取消全选");
                    choiceNum.setText("已选中("+choices.length+")");
                }else {
                    allChoiceText.setText("全选");
                    choiceNum.setText("已选中(0)");
                }
                allChoice=!allChoice;
                for(int i=0;i<choices.length;i++){
                    choices[i]=allChoice;
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.del_song_list:
                int choiceCount=0;
                for(int i=0;i<choices.length;i++){
                    if (choices[i])
                        choiceCount++;
                }
                if (choiceCount>0) {
                    showSureDelPopup();
                }else {
                    Toast.makeText(ManageSongListActivity.this,"未选择歌单",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private void showSureDelPopup(){
        final AlertDialog show;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        View view=View.inflate(context, R.layout.sure_del_list, null);
        View sure,cancel;
        TextView issueText=view.findViewById(R.id.issue_text);
        issueText.setText("确认删除选中的歌单及其内所有歌曲(不会删除本地下载的歌曲文件)");
        final CheckBox checkBox;
        sure=view.findViewById(R.id.sure);
        cancel=view.findViewById(R.id.cancel);

        alertDialog.setView(view);
        show=alertDialog.create();
        show.show();
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0;i<choices.length;i++){
                    if (choices[i]){
                        MusicDaoImpl.deleteSongList(HomeActivity.songLists.get(i));
                    }
                }
                HomeActivity.songLists=MusicDaoImpl.getSongLists();
                for (int i=0;i<4;i++){
                    HomeActivity.songLists.remove(0);
                }
                Toast.makeText(ManageSongListActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                initData();
                show.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int position= (int) v.getTag();
        switch (v.getId()){
            case R.id.list_item_view:
                v=v.findViewById(R.id.checkBox);
                ((CheckBox)v).setChecked(!((CheckBox)v).isChecked());
            case R.id.checkBox:
                choices[position]=((CheckBox)v).isChecked();
                int choiceCount=0;
                for(int i=0;i<choices.length;i++){
                    if (choices[i])
                        choiceCount++;
                }
                allChoice=(choiceCount==choices.length);
                if (!allChoice) allChoiceText.setText("全选");
                else allChoiceText.setText("取消全选");
                choiceNum.setText("已选中("+choiceCount+")");
                break;
        }
    }
}
