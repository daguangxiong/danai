package com.example.librarybase.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    private Unbinder mUnBinder;
    private View mContentView;
    protected Context mContext;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContentViewId() != 0) {
            mContentView = inflater.inflate(getContentViewId(), container, false);
            return mContentView;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnBinder = ButterKnife.bind(this, view);
        if (this.getClass().isAnnotationPresent(BindEventBus.class)) {
            EventBusHelper.register(this);
        }
        //EventBus.getDefault().register(this);
        initView(view, savedInstanceState);
        loadData();

    }

    protected abstract void loadData();


    @Override
    public void onDestroyView() {
        if (mUnBinder != null) {
            mUnBinder.unbind();
            mUnBinder = null;
        }
        if (this.getClass().isAnnotationPresent(BindEventBus.class)) {
            EventBusHelper.unregister(this);
        }
        super.onDestroyView();
    }


    protected abstract void initView(View view, Bundle savedInstanceState);

    protected abstract @LayoutRes
    int getContentViewId();
}
