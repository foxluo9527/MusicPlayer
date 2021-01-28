package com.example.musicplayerdome.ui.base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.musicplayerdome.R;

public abstract class BaseFragment extends Fragment {
    private MaterialDialog mLoadingDialog;
    private boolean isFirstLoad=true;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getResId(),null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onConfigView();
        initData();
        if (!isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(),"请检查网络连接",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(),"请检查网络连接",Toast.LENGTH_LONG).show();
        }
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    public void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new MaterialDialog.Builder(getActivity())
                    .widgetColorRes(R.color.black)
                    .progress(true, 0)
                    .cancelable(false)
                    .build();
        }
        mLoadingDialog.setContent("玩命加载中...");
        mLoadingDialog.show();
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }
    protected abstract int getResId();//加载布局
    protected abstract void onConfigView();//初始化View
    protected abstract void initData();//加载数据

}
