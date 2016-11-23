package com.cdw.zhihutopnews.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cdw.zhihutopnews.R;
import com.cdw.zhihutopnews.bean.ZhihuStory;
import com.cdw.zhihutopnews.config.Config;
import com.cdw.zhihutopnews.presenter.IZhihuStoryPresenter;
import com.cdw.zhihutopnews.presenter.implePresenter.ZhihuStoryPresenterImpl;
import com.cdw.zhihutopnews.presenter.impleView.IZhihuStory;
import com.cdw.zhihutopnews.uitls.DensityUtil;
import com.cdw.zhihutopnews.uitls.WebUtil;
import com.cdw.zhihutopnews.widget.ParallaxScrimageView;
import com.cdw.zhihutopnews.widget.TranslateYTextView;

import java.lang.reflect.InvocationTargetException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ZhihuDetailActivity extends BaseActivity implements IZhihuStory {

    @BindView(R.id.shot)
    ParallaxScrimageView parallaxScrimageView;//自定义ImageView，显示新闻标题图片
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.wv_zhihu)
    WebView wvZhihu;
    @BindView(R.id.nest)
    NestedScrollView nest;
    @BindView(R.id.title)
    TranslateYTextView translateYTextView;


    private int width;//图片的宽
    private int height;//图片的高
    private String id;//新闻ID
    private String title;//新闻标题
    private boolean isToolbarenable = true;//toolbar是否能点击
    private IZhihuStoryPresenter iZhihuStoryPresenter;
    private NestedScrollView.OnScrollChangeListener scrollListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTheme(Config.isNight);
        setContentView(R.layout.activity_zhihu_detail);
        ButterKnife.bind(this);
        int[] devicesInfo = DensityUtil.getDeviceInfo(this);
        width = devicesInfo[0];
        height = 3 * width / 4;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initlistenr();
        initData();
        initView();
        getData();


    }

    /**
     * 切换白天黑夜主题模式
     */
    private void changeTheme(boolean display_model) {
        if (display_model) {
            setTheme(R.style.AppTheme_Night);//夜间模式
        } else {
            setTheme(R.style.AppTheme_Light);//白天模式
        }

    }

    private void initlistenr() {
        scrollListener = new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (oldScrollY < parallaxScrimageView.getHeight()) {
                    parallaxScrimageView.setOffset(-oldScrollY);
                    translateYTextView.setOffset(-oldScrollY);
                }
                if (scrollY >= oldScrollY && (scrollY - oldScrollY) <= toolbar.getHeight()) {
                    float alpha = 1 - (float) scrollY / (toolbar.getHeight() * 2);
                    toolbar.setAlpha(alpha);//根据向下滑动的距离逐渐隐藏toolbar
                    if (alpha <= 0.0f) {
                        isToolbarenable = false;//完全透明是toolbar不可以点击
                    }
                } else if (scrollY < oldScrollY && (oldScrollY - scrollY) > 10) {
                    toolbar.setAlpha(1);//toolbar透明度为1，即正常显示toolbar
                    isToolbarenable = true;//toolbar可以点击
                }


            }

        };

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isToolbarenable) {
                    expandImageAndFinish();//返回主页
                }
            }
        });

    }


    private void getData() {
        iZhihuStoryPresenter.getZhihuStory(id);//根据ID获取新闻明细
    }

    private void initView() {
        translateYTextView.setText(title);//显示新闻标题

        WebSettings settings = wvZhihu.getSettings();
        settings.setJavaScriptEnabled(true);//支持js
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//如果cache中不存在，从网络中获取！
        settings.setLoadWithOverviewMode(true);//缩放至屏幕大小
        settings.setBuiltInZoomControls(true);////设置支持缩放
        settings.setDomStorageEnabled(true);//如果需要存储一些简单的用key/value对即可解决的数据，DOM Storage是非常完美的方案
        settings.setDatabaseEnabled(true);
        settings.setAppCachePath(getCacheDir().getAbsolutePath() + "/webViewCache");//缓存路径
        settings.setAppCacheEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//把所有内容放到WebView组件等宽的一列中,避免出现横向滚动条
        wvZhihu.setWebChromeClient(new WebChromeClient());//辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等
    }

    /**
     * 页面返回主页时的动画
     */
    private void expandImageAndFinish() {
        if (parallaxScrimageView.getOffset() != 0f) {
            Animator expandImage = ObjectAnimator.ofFloat(parallaxScrimageView, ParallaxScrimageView.OFFSET,
                    0f);
            expandImage.setDuration(80);
            expandImage.setInterpolator(new AccelerateInterpolator());
            expandImage.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        finishAfterTransition();
                    } else {
                        finish();
                    }
                }
            });
            expandImage.start();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            } else {
                finish();
            }
        }
    }

    private void initData() {
        id = getIntent().getStringExtra("id");//获取新闻id
        title = getIntent().getStringExtra("title");//获取新闻标题
        //String imageUrl = getIntent().getStringExtra("image");

        iZhihuStoryPresenter = new ZhihuStoryPresenterImpl(this);
        nest.setOnScrollChangeListener(scrollListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //调用postponeEnterTransition()方法来暂时阻止启动共享元素 Transition
            postponeEnterTransition();
            parallaxScrimageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    parallaxScrimageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        //在共享元素准备好后调用startPostponedEnterTransition来恢复过渡效果。
                        // 常见的模式是在一个OnPreDrawListener中启动延时 Transition，
                        //  它会在共享元素测量和布局完毕后被调用
                        startPostponedEnterTransition();
                    }
                    return true;
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        try {
            wvZhihu.getClass().getMethod("onResume").invoke(wvZhihu, (Object[]) null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {

        try {
            wvZhihu.getClass().getMethod("onPause").invoke(wvZhihu, (Object[]) null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        //防止webview内存泄露
        if (wvZhihu != null) {
            ((ViewGroup) wvZhihu.getParent()).removeView(wvZhihu);
            wvZhihu.destroy();
            wvZhihu = null;
        }
        iZhihuStoryPresenter.unsubcrible();
        super.onDestroy();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        expandImageAndFinish();
    }

    @Override
    public void showError(String error) {
        Snackbar.make(wvZhihu, getString(R.string.snack_infor), Snackbar.LENGTH_INDEFINITE).setAction("重试", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        }).show();
    }

    @Override
    public void showZhihuStory(ZhihuStory zhihuStory) {
        Glide.with(this)
                .load(zhihuStory.getImage())
                .centerCrop()
                .override(width, height)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存原始图片
                .into(parallaxScrimageView);
        String url = zhihuStory.getShareUrl();
        boolean isEmpty = TextUtils.isEmpty(zhihuStory.getBody());
        String body = zhihuStory.getBody();
        String[] scc = zhihuStory.getCss();
        if (isEmpty) {
            wvZhihu.loadUrl(url);
        } else {
            String data = WebUtil.buildHtmlWithCss(body, scc, Config.isNight);
            wvZhihu.loadDataWithBaseURL(WebUtil.BASE_URL, data, WebUtil.MIME_TYPE, WebUtil.ENCODING, WebUtil.FAIL_URL);
        }

    }



}
