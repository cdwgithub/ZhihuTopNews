package com.cdw.zhihutopnews.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cdw.zhihutopnews.R;
import com.cdw.zhihutopnews.bean.ZhihuStory;
import com.cdw.zhihutopnews.config.Config;
import com.cdw.zhihutopnews.presenter.IZhihuStoryPresenter;
import com.cdw.zhihutopnews.presenter.implePresenter.ZhihuStoryPresenterImpl;
import com.cdw.zhihutopnews.presenter.impleView.IZhihuStory;
import com.cdw.zhihutopnews.uitls.ColorUtils;
import com.cdw.zhihutopnews.uitls.DensityUtil;
import com.cdw.zhihutopnews.uitls.GlideUtils;
import com.cdw.zhihutopnews.uitls.ViewUtils;
import com.cdw.zhihutopnews.uitls.WebUtil;
import com.cdw.zhihutopnews.widget.ParallaxScrimageView;
import com.cdw.zhihutopnews.widget.TranslateYTextView;

import java.lang.reflect.InvocationTargetException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ZhihuDetailActivity extends BaseActivity implements IZhihuStory {

    @BindView(R.id.shot)
    ParallaxScrimageView parallaxScrimageView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.wv_zhihu)
    WebView wvZhihu;
    @BindView(R.id.nest)
    NestedScrollView nest;
    @BindView(R.id.title)
    TranslateYTextView translateYTextView;


    private int[] DevicesInfo;
    private int width;
    private int height;
    private String id;
    private String title;
    private String url;
    private String imageUrl;
    private boolean isEmpty;
    private String body;
    private String[] scc;
    private boolean isToolbarenable = true;
    private IZhihuStoryPresenter iZhihuStoryPresenter;
    private NestedScrollView.OnScrollChangeListener scrollListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhihu_detail);
        ButterKnife.bind(this);
        DevicesInfo = DensityUtil.getDeviceInfo(this);//获取屏幕分辨率信息
        width = DevicesInfo[0];
        height = 3 * width / 4;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initlistenr();
        initData();
        initView();
        getData();


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
                    float alpha = 1 - (float) scrollY / toolbar.getHeight();
                    toolbar.setAlpha(alpha);//根据向下滑动的距离逐渐隐藏toolbar
                    if (alpha <= 0.0f) {
                        isToolbarenable = false;
                    }
                } else {
                    toolbar.setAlpha(1);//toolbar透明度为1，即正常显示toolbar
                    isToolbarenable = true;
                }


            }

        };
    }


    private void getData() {
        iZhihuStoryPresenter.getZhihuStory(id);//根据ID获取新闻明细
    }

    private void initView() {
        translateYTextView.setText(title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isToolbarenable) {
                    expandImageAndFinish();
                }

            }


        });


        WebSettings settings = wvZhihu.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);

        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAppCachePath(getCacheDir().getAbsolutePath() + "/webViewCache");
        settings.setAppCacheEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wvZhihu.setWebChromeClient(new WebChromeClient());
    }

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
        imageUrl = getIntent().getStringExtra("image");//获取新闻首张图片
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

        //webview内存泄露
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
                .load(zhihuStory.getImage()).centerCrop()
                .listener(loadListener).override(width, height)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(parallaxScrimageView);
        url = zhihuStory.getShareUrl();
        isEmpty = TextUtils.isEmpty(zhihuStory.getBody());
        body = zhihuStory.getBody();
        scc = zhihuStory.getCss();
        if (isEmpty) {
            wvZhihu.loadUrl(url);
        } else {
            String data = WebUtil.buildHtmlWithCss(body, scc, Config.isNight);
            wvZhihu.loadDataWithBaseURL(WebUtil.BASE_URL, data, WebUtil.MIME_TYPE, WebUtil.ENCODING, WebUtil.FAIL_URL);
        }

    }

    private RequestListener loadListener = new RequestListener<String, GlideDrawable>() {

        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            final Bitmap bitmap = GlideUtils.getBitmap(resource);
            final int twentyFourDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    24, ZhihuDetailActivity.this.getResources().getDisplayMetrics());
            Palette.from(bitmap)
                    .maximumColorCount(3)
                    .clearFilters() /* by default palette ignore certain hues
                        (e.g. pure black/white) but we don't want this. */
                    .setRegion(0, 0, bitmap.getWidth() - 1, twentyFourDip) /* - 1 to work around
                        https://code.google.com/p/android/issues/detail?id=191013 */
                    .generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            boolean isDark;
                            @ColorUtils.Lightness int lightness = ColorUtils.isDark(palette);
                            if (lightness == ColorUtils.LIGHTNESS_UNKNOWN) {
                                isDark = ColorUtils.isDark(bitmap, bitmap.getWidth() / 2, 0);
                            } else {
                                isDark = lightness == ColorUtils.IS_DARK;
                            }

                            // color the status bar. Set a complementary dark color on L,
                            // light or dark color on M (with matching status bar icons)
                           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


                                int statusBarColor = getWindow().getStatusBarColor();
                                final Palette.Swatch topColor =
                                        ColorUtils.getMostPopulousSwatch(palette);
                                if (topColor != null &&
                                        (isDark || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                                    statusBarColor = ColorUtils.scrimify(topColor.getRgb(),
                                            isDark, SCRIM_ADJUSTMENT);
                                    // set a light status bar on M+
                                    if (!isDark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        ViewUtils.setLightStatusBar(parallaxScrimageView);
                                    }
                                }

                                if (statusBarColor != getWindow().getStatusBarColor()) {
                                    parallaxScrimageView.setScrimColor(statusBarColor);
                                    ValueAnimator statusBarColorAnim = ValueAnimator.ofArgb(
                                            getWindow().getStatusBarColor(), statusBarColor);
                                    statusBarColorAnim.addUpdateListener(new ValueAnimator
                                            .AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            getWindow().setStatusBarColor(
                                                    (int) animation.getAnimatedValue());
                                        }
                                    });
                                    statusBarColorAnim.setDuration(1000L);
                                    statusBarColorAnim.setInterpolator(
                                            new AccelerateInterpolator());
                                    statusBarColorAnim.start();
                                }
                            }*/

                        }
                    });


            Palette.from(bitmap)
                    .clearFilters()
                    .generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {

                            // slightly more opaque ripple on the pinned image to compensate
                            // for the scrim
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                                parallaxScrimageView.setForeground(ViewUtils.createRipple(palette, 0.3f, 0.6f,
                                        ContextCompat.getColor(ZhihuDetailActivity.this, R.color.mid_grey),
                                        true));
                            }
                        }
                    });

            // TODO should keep the background if the image contains transparency?!
            parallaxScrimageView.setBackground(null);
            return false;
        }
    };

    @OnClick(R.id.shot)
    public void onClick() {
        nest.smoothScrollTo(0, 0);
    }
}
