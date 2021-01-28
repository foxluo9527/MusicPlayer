package com.example.musicplayerdome.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.musicplayerdome.R;
import com.example.musicplayerdome.adapter.HotSearchListAdapter;
import com.example.musicplayerdome.adapter.SearchRecordListAdapter;
import com.example.musicplayerdome.ui.base.BaseActivity;
import com.example.musicplayerdome.dao.SearchDao;
import com.example.musicplayerdome.dao.SearchDao.Record;
import com.example.musicplayerdome.ui.fragment.FramentMusicbar;
import com.example.musicplayerdome.utils.DBOpenHelper;
import com.example.musicplayerdome.utils.HttpRequest;

import java.util.ArrayList;

public class SearchActivity extends BaseActivity {
    ImageView cancel;
    Button deleteBtn;
    EditText searchText;
    String searchString;
    ListView listView;
    HotSearchListAdapter searchAdapter;
    SearchRecordListAdapter recordAdapter;
    ArrayList<Record> searchStrings=new ArrayList<Record>();
    ArrayList<Record> recordStrings=new ArrayList<Record>();
    FramentMusicbar musicBar;
    private FragmentManager manager;
    SearchDao dao;
    boolean onRecord=true;
    @Override
    protected int getResId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onConfigView() {
        cancel=findViewById(R.id.cancel);
        deleteBtn=findViewById(R.id.deleteButton);
        deleteBtn.setVisibility(View.GONE);
        searchText=findViewById(R.id.search_editText);
        searchText.setHint(searchString);
        listView=findViewById(R.id.search_record_list);
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
        getRecordData();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (onRecord){
                    if (i==recordStrings.size()-1){
                        dao.delAllRecord();
                        recordStrings.clear();
                        recordAdapter.notifyDataSetChanged();
                    }else {//跳转结果
                        searchString=recordStrings.get(i).getRecord();
                        dao.delRecord(recordStrings.get(i).getId());
                        dao.addRecord(searchString);
                        jumpToSearchResult();
                    }
                }else{//跳转结果
                    searchString= searchStrings.get(i).getRecord();
                    dao.addRecord(searchString);
                    jumpToSearchResult();
                }
            }
        });
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchString=searchText.getText()+"";
                if (searchString.length()==0){
                    getRecordData();
                    deleteBtn.setVisibility(View.GONE);
                    onRecord=true;
                    recordAdapter=new SearchRecordListAdapter(recordStrings,SearchActivity.this);
                    listView.setAdapter(recordAdapter);
                }else {//搜索框输入
                    getSearchData(searchText.getText().toString());
                    onRecord=false;
                }
            }
        });
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //跳转到搜索详情页
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                   search();
                }
                return true;
            }
        });
        searchAdapter=new HotSearchListAdapter(searchStrings,this);
        recordAdapter=new SearchRecordListAdapter(recordStrings,this);
        listView.setAdapter(recordAdapter);
    }
    private void search(){
        searchString=searchText.getText().toString();
        dao.addRecord(searchString);
        jumpToSearchResult();
    }
    private void jumpToSearchResult(){
        Intent intent =new Intent(SearchActivity.this,SearchResultActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("searchString", searchString);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
    private void getRecordData() {
        recordStrings.clear();
        DBOpenHelper helper=new DBOpenHelper(SearchActivity.this,"search_record_tb",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        dao=new SearchDao(db);
        recordStrings=dao.getRecords();
        if (recordStrings.size()>0)
        recordStrings.add(new Record());
    }

    private void getSearchData(String s) {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                searchStrings=HttpRequest.getSearchSuggestion(s);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                searchAdapter=new HotSearchListAdapter(searchStrings,SearchActivity.this);
                deleteBtn.setVisibility(View.VISIBLE);
                listView.setAdapter(searchAdapter);
            }
        }.execute();
    }

    public void doClick(View v){
        switch (v.getId()){
            case R.id.cancel:
                finish();
                break;
            case R.id.sure:
                search();
                break;
            case R.id.deleteButton:
                searchText.setText("");
                deleteBtn.setVisibility(View.GONE);
                break;
            case R.id.search_record_list:
                break;
            case R.id.delete_item_btn:
                final int position = (int) v.getTag();
                dao.delRecord(recordStrings.get(position).getId());
                recordStrings.remove(position);
                if (recordStrings.size()==1){
                    recordStrings.clear();
                }
                recordAdapter.notifyDataSetChanged();
                break;
        }
        if (v.getId()!=R.id.search_lineLayout){
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
