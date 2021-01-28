package com.example.musicplayerdome.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.bean.SongList;
import com.example.musicplayerdome.dao.MusicDaoImpl;
import com.example.musicplayerdome.ui.base.BaseActivity;
import com.example.musicplayerdome.utils.FileUtils;
import com.example.musicplayerdome.utils.ImgHelper;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.example.musicplayerdome.utils.ImgHelper.getRoundedCornerBitmap;

public class EditSongListActivity extends BaseActivity {
    int position;
    ImageView coverImg;
    EditText nameEt;
    TextView introduceTv;
    MultiAutoCompleteTextView introduceEt;
    View eidtIntroduceView;
    Boolean isChanged=false;
    String coverString;
    @Override
    protected int getResId() {
        return R.layout.activity_edit_song_list;
    }

    @Override
    protected void onConfigView() {
        coverImg=findViewById(R.id.cover_img);
        nameEt=findViewById(R.id.edit_name);
        introduceEt=findViewById(R.id.edit_introduce);
        introduceTv=findViewById(R.id.introduce_text);
        eidtIntroduceView=findViewById(R.id.show_edit_view);
    }

    @Override
    protected void setLocalClassName() {
        this.lockAppName+=this.getLocalClassName();
    }
    @Override
    protected void initData() {
        position=getIntent().getIntExtra("position",-1);
        if (position==-1){
            finish();
        }
        coverString=HomeActivity.songLists.get(position).getCoverImg();
        Glide.with(this)
                .load(coverString)
                .placeholder(R.drawable.music)
                .error(R.drawable.music)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(
                            @NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Bitmap srcBitmap = ImgHelper.drawableToBitmap(resource);
                        srcBitmap=getRoundedCornerBitmap(srcBitmap,20f);
                        coverImg.setImageBitmap(srcBitmap);
                    }
                });
        nameEt.setText(HomeActivity.songLists.get(position).getName());
        introduceTv.setText(HomeActivity.songLists.get(position).getIntroduce());
        nameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isChanged=true;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        introduceEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isChanged=true;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void doClick(View view) {
        switch (view.getId()){
            case R.id.btn_return:
                if (isChanged){
                    showChangedPopup();
                }else
                finish();
                break;
            case R.id.more:
                saveSongListMsg();
                break;
            case R.id.change_cover_view:
                changeCoverImg();
                break;
            case R.id.edit_introduce_view:
                if (eidtIntroduceView.getVisibility()!=View.VISIBLE){
                    eidtIntroduceView.setVisibility(View.VISIBLE);
                    introduceEt.setText(introduceTv.getText());
                }
                break;
            case R.id.cancel_btn:
                eidtIntroduceView.setVisibility(View.GONE);
                break;
            case R.id.sure_btn:
                isChanged=true;
                introduceTv.setText(introduceEt.getText().toString());
                eidtIntroduceView.setVisibility(View.GONE);
                break;
        }
    }
    private static final int PICTURE = 10086; //requestcode
    public static final int NEW_PICTURE = 10010; //requestcode
    private void changeCoverImg(){
        isChanged=true;
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT < 19) {//因为Android SDK在4.4版本后图片action变化了 所以在这里先判断一下
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICTURE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data==null){
            return;
        }
        switch (requestCode) {
            case PICTURE:
                Uri uri = data.getData();
                String image = FileUtils.getUriPath(this, uri); //（因为4.4以后图片uri发生了变化）通过文件工具类 对uri进行解析得到图片路径
                File file=new File(Environment.getExternalStorageDirectory()+"/MusicPlayer/cover/");
                if(!file.exists()){
                    file.mkdirs();
                }
                file=new File(Environment.getExternalStorageDirectory()+"/MusicPlayer/cover/"+ UUID.randomUUID()+".jpg");
                if(file.exists()){    //如果目标文件已经存在
                    file.delete();    //则删除旧文件
                }else {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                UCrop.of(uri, Uri.fromFile(file))
                        .withAspectRatio(1, 1)
                        .start(this);
                break;
            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK){
                    final Uri resultUri = UCrop.getOutput(data);
                    coverString=resultUri.toString();
                    Glide.with(this)
                            .load(coverString)
                            .placeholder(R.drawable.music)
                            .error(R.drawable.music)
                            .into(coverImg);
                }else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                    cropError.printStackTrace();
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showChangedPopup(){
        final AlertDialog show;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        View view=View.inflate(this, R.layout.sure_del_list, null);
        View sure,cancel;
        TextView issueText=view.findViewById(R.id.issue_text);
        issueText.setText(" 您还没保存修改的内容\n是否确认退出");
        final CheckBox checkBox;
        sure=view.findViewById(R.id.sure);
        cancel=view.findViewById(R.id.cancel);

        alertDialog.setView(view);
        show=alertDialog.create();
        show.show();
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });
    }
    private void saveSongListMsg(){
        isChanged=false;
        String name=nameEt.getText().toString();
        String introduce=introduceTv.getText().toString();
        SongList songList=HomeActivity.songLists.get(position);
        songList.setCoverImg(coverString);
        songList.setName(name);
        songList.setIntroduce(introduce);
        MusicDaoImpl.updateSongListMsg(songList);
        finish();
    }
}
