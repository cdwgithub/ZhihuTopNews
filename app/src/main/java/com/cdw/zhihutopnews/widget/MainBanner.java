package com.cdw.zhihutopnews.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cdw.zhihutopnews.R;
import com.cdw.zhihutopnews.bean.TopStoryItem;
import com.cdw.zhihutopnews.bean.ZhihuDaily;
import com.cdw.zhihutopnews.uitls.ObservableColorMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CDW on 2016/11/20.
 */

public class MainBanner extends FrameLayout implements View.OnClickListener {

    private ViewPager mainbannerVp;
    private LinearLayout mainbannerLlDot;
    private ImageView ivTitle;
    private TextView tvTitle;


    private Context context;
    private ArrayList<TopStoryItem> topStoryList = new ArrayList<>();
    private List<View> views;
    private List<ImageView> iv_dots;
    private boolean isAutoPlay;
    private int currentItem;
    private Handler handler = new Handler();
    private OnItemClickListener itemClickListener;

    public MainBanner(Context context) {
        this(context, null);
    }

    public MainBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        views = new ArrayList<View>();
        iv_dots = new ArrayList<ImageView>();
        View view = LayoutInflater.from(context).inflate(R.layout.mainbanner_layout, this, true);
        mainbannerVp = (ViewPager) view.findViewById(R.id.mainbanner_vp);
        mainbannerLlDot = (LinearLayout) view.findViewById(R.id.mainbanner_ll_dot);
        mainbannerLlDot.removeAllViews();
    }

    /**
     * 加载图片
     * @param picUrl
     */
    void loadPic(String picUrl) {
        Glide.with(context)
                .load(picUrl)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {


                        ivTitle.setHasTransientState(true);//告诉系统这个 View 应该尽可能的被保留，直到setHasTransientState(false)被呼叫
                        final ObservableColorMatrix cm = new ObservableColorMatrix();
                        final ObjectAnimator animator = ObjectAnimator.ofFloat(cm, ObservableColorMatrix.SATURATION, 0f, 1f);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                ivTitle.setColorFilter(new ColorMatrixColorFilter(cm));
                            }
                        });
                        animator.setDuration(2000L);
                        animator.setInterpolator(new AccelerateInterpolator());
                        animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                ivTitle.clearColorFilter();
                                ivTitle.setHasTransientState(false);

                                animator.start();


                            }
                        });


                        return false;
                    }
                }).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(ivTitle);
    }


    class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return views.size();
        }
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    public void showTopStory(ZhihuDaily zhihuDaily) {
        topStoryList = zhihuDaily.getTopstories();
        int len = topStoryList.size();
        for (int i = 0; i < len; i++) {
            ImageView iv_dot = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 5;
            params.rightMargin = 5;
            params.gravity = Gravity.CENTER;
            mainbannerLlDot.addView(iv_dot, params);
            iv_dots.add(iv_dot);

        }
        for (int i = 0; i <= len + 1; i++) {
            View fm = LayoutInflater.from(context).inflate(
                    R.layout.mainbanner_content_layout, null);
            ivTitle = (ImageView) fm.findViewById(R.id.iv_title);
            tvTitle = (TextView) fm.findViewById(R.id.tv_title);
            ivTitle.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (i == 0) {
                loadPic(topStoryList.get(len - 1).getImage());
                tvTitle.setText(topStoryList.get(len - 1).getTitle());
            } else if (i == len + 1) {
                loadPic(topStoryList.get(0).getImage());
                tvTitle.setText(topStoryList.get(0).getTitle());
            } else {
                loadPic(topStoryList.get(i - 1).getImage());
                tvTitle.setText(topStoryList.get(i - 1).getTitle());
            }
            fm.setOnClickListener(this);
            views.add(fm);
        }
        mainbannerVp.setAdapter(new MyPagerAdapter());
        mainbannerVp.setFocusable(true);
        mainbannerVp.setCurrentItem(1);
        mainbannerVp.addOnPageChangeListener(new BannerOnPageChangeListener());
        startPlay();//开始自动轮播
    }

    private void startPlay() {
        isAutoPlay = true;
        handler.postDelayed(task, 3000);
    }

    private final Runnable task = new Runnable() {

        @Override
        public void run() {
            if (isAutoPlay) {
                currentItem = currentItem % (topStoryList.size() + 1) + 1;
                if (currentItem == 1) {
                    mainbannerVp.setCurrentItem(currentItem, false);
                    handler.post(task);
                } else {
                    mainbannerVp.setCurrentItem(currentItem);
                    handler.postDelayed(task, 5000);
                }
            } else {
                handler.postDelayed(task, 5000);
            }
        }
    };

    class BannerOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                case 0:
                    if (mainbannerVp.getCurrentItem() == 0) {
                        mainbannerVp.setCurrentItem(topStoryList.size(), false);
                    } else if (mainbannerVp.getCurrentItem() == topStoryList.size() + 1) {
                        mainbannerVp.setCurrentItem(1, false);
                    }
                    currentItem = mainbannerVp.getCurrentItem();
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < iv_dots.size(); i++) {
                if (i == arg0 - 1) {
                    iv_dots.get(i).setImageResource(R.drawable.dot_focus);
                } else {
                    iv_dots.get(i).setImageResource(R.drawable.dot_blur);
                }
            }

        }

    }

    @Override
    public void onClick(View view) {
        if (itemClickListener != null) {
            TopStoryItem topStoryItem = topStoryList.get(mainbannerVp.getCurrentItem() - 1);
            itemClickListener.onClick(view, topStoryItem);
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
       void onClick(View v, TopStoryItem topStoryItem);
    }
}
