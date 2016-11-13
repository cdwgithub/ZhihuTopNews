package com.cdw.zhihutopnews.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

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
import com.cdw.zhihutopnews.uitls.AnimUtils;
import com.cdw.zhihutopnews.uitls.ColorUtils;
import com.cdw.zhihutopnews.uitls.DensityUtil;
import com.cdw.zhihutopnews.uitls.GlideUtils;
import com.cdw.zhihutopnews.uitls.ViewUtils;
import com.cdw.zhihutopnews.uitls.WebUtil;
import com.cdw.zhihutopnews.widget.ElasticDragDismissFrameLayout;
import com.cdw.zhihutopnews.widget.ParallaxScrimageView;
import com.cdw.zhihutopnews.widget.TranslateYTextView;

import java.lang.reflect.InvocationTargetException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ZhihuDetailActivity extends BaseActivity implements IZhihuStory {

   @BindView(R.id.shot)
   ParallaxScrimageView parallaxScrimageView;
   // @BindView(R.id.title)
   // TranslateYTextView translateYTextView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.wv_zhihu)
    WebView wvZhihu;
    @BindView(R.id.nest)
    NestedScrollView nest;
 //   @BindView(R.id.draggable_frame)
  //  ElasticDragDismissFrameLayout draggableFrame;//随意拖拽退出当前界面的交互的开源框架


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
    private static final float SCRIM_ADJUSTMENT = 0.075f;
    private IZhihuStoryPresenter iZhihuStoryPresenter;
   // private Transition.TransitionListener zhihuReturnHomeListener;//从转变过渡的监听器接收通知
    private NestedScrollView.OnScrollChangeListener scrollListener;
   // private ElasticDragDismissFrameLayout.SystemChromeFader chromeFader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhihu_detail);
        ButterKnife.bind(this);
        DevicesInfo = DensityUtil.getDeviceInfo(this);//获取屏幕分辨率信息
        width = DevicesInfo[0];
        height = 3 * width / 4;
        setSupportActionBar(toolbar);
      //  initListener();
        initData();
        initView();
        getData();


     //   chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(this);
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().getSharedElementReturnTransition().addListener(zhihuReturnHomeListener);
            getWindow().setSharedElementEnterTransition(new ChangeBounds());
        }*/

        enterAnimation();

    }


    private void enterAnimation() {
        float offSet = toolbar.getHeight();
        LinearInterpolator interpolator = new LinearInterpolator();
        viewEnterAnimation(parallaxScrimageView, offSet, interpolator);
        viewEnterAnimationNest(nest, 0f, interpolator);
    }

    private void viewEnterAnimation(View view, float offset, Interpolator interp) {
        view.setTranslationY(-offset);
        view.setAlpha(0f);
        view.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(500L)
                .setInterpolator(interp)
                .setListener(null)
                .start();
    }

    private void viewEnterAnimationNest(View view, float offset, Interpolator interp) {
        view.setTranslationY(-offset);
        view.setAlpha(0.3f);
        view.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(500L)
                .setInterpolator(interp)
                .setListener(null)
                .start();
    }

    private void getData() {
        iZhihuStoryPresenter.getZhihuStory(id);
    }

    private void initView() {
        toolbar.setTitleMargin(20, 20, 0, 10);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nest.smoothScrollTo(0, 0);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                expandImageAndFinish();
            }


        });
        //translateYTextView.setText(title);

        WebSettings settings = wvZhihu.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        //settings.setUseWideViewPort(true);造成文字太小
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
        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        imageUrl = getIntent().getStringExtra("image");
        iZhihuStoryPresenter = new ZhihuStoryPresenterImpl(this);
        nest.setOnScrollChangeListener(scrollListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
            parallaxScrimageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    parallaxScrimageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startPostponedEnterTransition();
                    }
                    return true;
                }
            });
        }
    }

   /* private void initListener() {
        zhihuReturnHomeListener = new AnimUtils.TransitionListenerAdapter() {
            @Override
            public void onTransitionStart(Transition transition) {
                super.onTransitionStart(transition);

                Log.d("ZhihuDetailActivity", "toobar->transitiolistener////////");
                toolbar.animate()
                        .alpha(0f)
                        .setDuration(100)
                        .setInterpolator(new AccelerateInterpolator());
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    parallaxScrimageView.setElevation(1f);
                    toolbar.setElevation(0f);
                }

                nest.animate()
                        .alpha(0f)
                        .setDuration(50)
                        .setInterpolator(new AccelerateInterpolator());

            }
        };
        scrollListener = new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (oldScrollY < 168) {
                    Log.d("ZhihuDetailActivity", "toobar->onScrollChange////////");
                    parallaxScrimageView.setOffset(-oldScrollY);
                   // translateYTextView.setOffset(-oldScrollY);
                }
            }
        };
    }*/


    @Override
    protected void onResume() {
        super.onResume();
       // draggableFrame.addListener(chromeFader);
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
       // draggableFrame.removeListener(chromeFader);
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
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().getSharedElementReturnTransition().removeListener(zhihuReturnHomeListener);
        }*/
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
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


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
                            }

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
