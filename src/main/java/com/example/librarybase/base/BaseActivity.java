package com.example.librarybase.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.librarybase.R;
import com.gyf.immersionbar.ImmersionBar;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {
    //    @Nullable
//    @BindView(R2.id.tv_title)
    TextView mTvTitle;
    private Unbinder mUnBinder;

    public Toolbar mToolbar;
    private long mLastClick = 0;

    private WeakReference<Activity> weakReference = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (showTitleView()) {
            initTitleView();
        } else {
            setContentView(getContentViewId());
        }
        baseInit(savedInstanceState);
        if (weakReference == null) {
            weakReference = new WeakReference<Activity>(this);
        }
        ActivityStack.getInstance().pushActivity(weakReference.get());
        initImmersionBar();
        initView(savedInstanceState);
        loadData();
    }

    protected abstract void loadData();

    private void initTitleView() {
        View rootView = View.inflate(this, R.layout.activity_base, null);
        mTvTitle = rootView.findViewById(R.id.tv_title);
        mToolbar = rootView.findViewById(R.id.toolbar);
//        Drawable backIcon = getResources().getDrawable(R.mipmap.ic_back);
//        backIcon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
//        mToolbar.setNavigationIcon(backIcon);
        mToolbar.setNavigationOnClickListener(v -> finish());
        FrameLayout flContent = rootView.findViewById(R.id.fl_content);
        View content = View.inflate(this, getContentViewId(), null);
        if (content != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            flContent.addView(content, params);
        }
        setContentView(rootView);
    }

    @Override
    public void setTitle(int titleId) {
        if (mTvTitle != null) {
            mTvTitle.setText(titleId);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        if (mTvTitle != null) {
            mTvTitle.setText(title);
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 初始化沉浸式
     * Init immersion bar.
     */
    protected void initImmersionBar() {
        //设置共同沉浸式样式
        ImmersionBar.with(this).statusBarColor(R.color.colorPrimary).fitsSystemWindows(true).navigationBarColor(R.color.colorPrimary).init();
    }


    /**
     * [防止快速点击]
     *
     * @return
     */
    private boolean fastClick() {

        if (System.currentTimeMillis() - mLastClick <= 1000) {
            return false;
        }
        mLastClick = System.currentTimeMillis();
        return true;
    }


    /**
     * 是不是显示标题栏
     *
     * @return
     */
    public boolean showTitleView() {
        return true;
    }

    protected void baseInit(@Nullable Bundle savedInstanceState) {
        //ButterKnife注解视图
        mUnBinder = ButterKnife.bind(this);
        //EventBus注入
        if (this.getClass().isAnnotationPresent(BindEventBus.class)) {
            EventBusHelper.register(this);
        }
    }

    @Override
    protected void onDestroy() {
        //销毁注解操作
        if (mUnBinder != null) {
            mUnBinder.unbind();
            mUnBinder = null;
        }
        //
        EventBusHelper.unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            ActivityStack.getInstance().removeActivity(weakReference.get());
        }
    }

    protected abstract int getContentViewId();

    protected abstract void initView(@Nullable Bundle savedInstanceState);

//    protected boolean isLogin() {
//        return MMKV.defaultMMKV().decodeBool(Constant.IS_LOGIN, false);
//    }


    public String getTag() {
        return getClass().getSimpleName();
    }
}
