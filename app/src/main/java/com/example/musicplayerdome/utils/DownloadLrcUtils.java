package com.example.musicplayerdome.utils;

import android.content.Context;
import android.os.Environment;

import com.example.musicplayerdome.service.MusicService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadLrcUtils {
    public static String downloadLrc(Context context, String docUrl)throws Exception{                           /***加载正文***/
        docUrl= MusicService.proxy.getProxyUrl(docUrl);
        //获取存储卡路径、构成保存文件的目标路径
        String dirName = "";
        //SD卡具有读写权限、指定附件存储路径为SD卡上指定的文件夹
        dirName = Environment.getExternalStorageDirectory()+"/MusicPlayer/lyric";
        File f = new File(dirName);
        if(!f.exists()){      //判断文件夹是否存在
            f.mkdir();        //如果不存在、则创建一个新的文件夹
        }
        //准备拼接新的文件名
        String[] list = docUrl.split("/");
        String fileName = list[list.length-1];
        fileName = dirName + fileName;
        File file = new File(fileName);
        if(file.exists()){    //如果目标文件已经存在
            file.delete();    //则删除旧文件
        }
        //1K的数据缓冲
        byte[] bs = new byte[1024];
        //读取到的数据长度
        int len;
        try{
            //通过文件地址构建url对象
            URL url = new URL(docUrl);
            //获取链接
            //URLConnection conn = url.openConnection();
            //创建输入流
            InputStream is = url.openStream();
            //获取文件的长度
            //int contextLength = conn.getContentLength();
            //输出的文件流
            OutputStream os = new FileOutputStream(file);
            //开始读取
            while((len = is.read(bs)) != -1){
                os.write(bs,0,len);
            }
            //完毕关闭所有连接
            os.close();
            is.close();
        }catch(MalformedURLException e){
            fileName = null;
            System.out.println("创建URL对象失败");
            throw e;
        }catch(FileNotFoundException e){
            fileName = null;
            System.out.println("无法加载文件");
            throw e;
        }catch(IOException e){
            fileName = null;
            System.out.println("获取连接失败");
            throw e;
        }
        return fileName;
    }
    public static String downloadCover(String docUrl)throws Exception{
        docUrl= MusicService.proxy.getProxyUrl(docUrl);
        /***加载正文***/
        //获取存储卡路径、构成保存文件的目标路径
        String dirName = "";
        //SD卡具有读写权限、指定附件存储路径为SD卡上指定的文件夹
        dirName = Environment.getExternalStorageDirectory()+"/MusicPlayer/coverImg";
        File f = new File(dirName);
        if(!f.exists()){      //判断文件夹是否存在
            f.mkdirs();        //如果不存在、则创建一个新的文件夹
        }
        String fileName = UUID.randomUUID()+".jpg";
        fileName = dirName +"/"+ fileName;
        File file = new File(fileName);
        if(file.exists()){    //如果目标文件已经存在
            file.delete();    //则删除旧文件
        }
        //1K的数据缓冲
        byte[] bs = new byte[1024];
        //读取到的数据长度
        int len;
        try{
            //通过文件地址构建url对象
            URL url = new URL(docUrl);
            //获取链接
            //URLConnection conn = url.openConnection();
            //创建输入流
            InputStream is = url.openStream();
            //获取文件的长度
            //int contextLength = conn.getContentLength();
            //输出的文件流
            OutputStream os = new FileOutputStream(file);
            //开始读取
            while((len = is.read(bs)) != -1){
                os.write(bs,0,len);
            }
            //完毕关闭所有连接
            os.close();
            is.close();
        }catch(MalformedURLException e){
            fileName = null;
            System.out.println("创建URL对象失败");
            throw e;
        }catch(FileNotFoundException e){
            fileName = null;
            System.out.println("无法加载文件");
            throw e;
        }catch(IOException e){
            fileName = null;
            System.out.println("获取连接失败");
            throw e;
        }
        return fileName;
    }
}
