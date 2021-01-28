package com.example.musicplayerdome.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicplayerdome.R;

/**
 * 创建日期：2018/11/23 on 10:38
 * 描述: glide工具类
 */
public class GlideUtil {


    private static GlideUtil instance;
    RequestOptions options;
    Context mContext;

    private GlideUtil(Context context){
        options = new RequestOptions();
        options.skipMemoryCache(false);
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        options.priority(Priority.HIGH);
        options.error(R.drawable.music);
        //设置占位符,默认
        options.placeholder(R.drawable.music);
        //设置错误符,默认
        options.error(R.drawable.music);
        mContext=context;
    }

    public static GlideUtil getInstance(Context context){
        if (instance==null){
            synchronized (GlideUtil.class){
                if (instance==null){
                    instance=new GlideUtil(context);
                }
            }
        }
        return instance;
    }

    //设置占位符
    public void setPlaceholder(int id){
        options.placeholder(id);
    }
    public void setPlaceholder(Drawable drawable){
        options.placeholder(drawable);
    }

    //设置错误符
    public void setError(int id){
        options.error(id);
    }

    public void setError(Drawable drawable){
        options.error(drawable);
    }

    public void showImage(String url, ImageView imageView){

        Glide .with(mContext)
                .load(url)
                .apply(options)
                .into(imageView);

    }

    //以图片宽度为基准
    public void showImageWidthRatio(String url, final ImageView imageView, final int width){
        Glide.with(mContext)
                .asBitmap()
                .apply(options)
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        int imageWidth=resource.getWidth();
                        int imageHeight=resource.getHeight();
                        int height = width * imageHeight / imageWidth;
                        ViewGroup.LayoutParams params=imageView.getLayoutParams();
                        params.height=height;
                        params.width=width;
                        imageView.setImageBitmap(resource);
                    }
                });
    }

    //以图片高度为基准
    public void showImageHeightRatio(String url, final ImageView imageView, final int height){
        Glide.with(mContext)
                .asBitmap()
                .apply(options)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        int imageWidth=resource.getWidth();
                        int imageHeight=resource.getHeight();
                        int width = height * imageHeight / imageWidth;
                        ViewGroup.LayoutParams params=imageView.getLayoutParams();
                        params.height=height;
                        params.width=width;
                        imageView.setImageBitmap(resource);
                    }
                });
    }

    //设置图片固定的大小尺寸
    public void showImageWH(String url, final ImageView imageView, int height,int width){

        options.override(width,height);
        Glide.with(mContext)
                .load(url)
                .apply(options)
                .into(imageView);
    }

    //设置图片圆角，以及弧度
    public void showImageRound(String url, final ImageView imageView,int radius){

        options.transform(new CornersTranform(radius));
//        options.transform(new GlideCircleTransform());
        Glide.with(mContext)
                .load(url)
                .apply(options)
                .into(imageView);

    }

    public void showImageRound(String url, final ImageView imageView,int radius, int height,int width){
        //不一定有效，当原始图片为长方形时设置无效
        options.override(width,height);
        options.transform(new CornersTranform(radius));
//        options.centerCrop(); //不能与圆角共存
        Glide.with(mContext)
                .load(url)
                .apply(options)
                .into(imageView);

    }


    public void showImageRound(String url, final ImageView imageView){
        //自带圆角方法，显示圆形
        options.circleCrop();
        Glide.with(mContext)
                .load(url)
                .apply(options)
                .into(imageView);
    }
}