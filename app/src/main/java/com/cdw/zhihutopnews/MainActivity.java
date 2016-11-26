package com.cdw.zhihutopnews;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.util.SimpleArrayMap;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cdw.zhihutopnews.activity.BaseActivity;

import com.cdw.zhihutopnews.config.Config;
import com.cdw.zhihutopnews.fragment.ZhihuFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    SimpleArrayMap<Integer, String> titleArrayMap = new SimpleArrayMap<>();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer)
    DrawerLayout drawer;
    @BindView(R.id.sr)
    SwipeRefreshLayout sr;
    private MenuItem currentMenuItem;
    private Fragment currentFragment;
    private ZhihuFragment zhihuFragment;
    private long exitTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTheme(Config.isNight);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(navigationOnClickListener);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);//去掉默认显示的Title
        initView();
        initLisneter();
        if (savedInstanceState == null) {
            if (currentMenuItem == null) {
                currentMenuItem = navView.getMenu().findItem(R.id.zhihuitem);//默认选择知乎界面

            }
            if (currentMenuItem != null) {
                currentMenuItem.setChecked(true);
                Fragment fragment = getFragmentById(currentMenuItem.getItemId());
                if (fragment != null) {
                    switchFragment(fragment);
                }
            }
        } else {
            if (currentMenuItem != null) {
                Fragment fragment = getFragmentById(currentMenuItem.getItemId());
                if (fragment != null) {
                    switchFragment(fragment);
                }
            } else {
                switchFragment(new ZhihuFragment());
                currentMenuItem = navView.getMenu().findItem(R.id.zhihuitem);
            }
        }

    }

    private void initView() {
        addFragmentAndTitle();
        int[][] state = new int[][]{
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_checked}  // pressed
        };
        int[] color = new int[]{
                Color.BLACK, Color.BLACK};
        int[] iconcolor = new int[]{
                Color.GRAY, Color.BLACK};
        navView.setItemTextColor(new ColorStateList(state, color));
        navView.setItemIconTintList(new ColorStateList(state, iconcolor));
        /**
         * 暂未实现下拉刷新功能
         */
        sr.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void initLisneter() {
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (currentMenuItem != item && currentMenuItem != null) {
                    currentMenuItem.setChecked(false);
                    currentMenuItem = item;
                    currentMenuItem.setChecked(true);
                    switchFragment(getFragmentById(currentMenuItem.getItemId()));
                }
                drawer.closeDrawer(GravityCompat.START, true);
                return true;
            }
        });
        sr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sr.setRefreshing(false);
            }
        });
    }

    /**
     * 切换白天黑夜主题模式,此种方式需要重启Activity
     * MainActivity.this.recreate();
     */
    private void changeTheme(boolean display_model) {
        if (display_model) {
            setTheme(R.style.AppTheme_Night);//夜间模式
        } else {
            setTheme(R.style.AppTheme_Light);//白天模式
        }

    }

    private void refreshUI() {
        toolbar.setBackgroundColor(getResources().getColor(Config.isNight ? R.color.toolbar_background_dark : R.color.toolbar_background_light));
        navView.setBackgroundColor(getResources().getColor(Config.isNight ? R.color.background_dark : R.color.background_light));
        ((ZhihuFragment) currentFragment).refreshUI();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.getItem(0).setTitle(Config.isNight ? getResources().getString(R.string.display_model_light) : getResources().getString(R.string.display_model_night));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_display_model:
                Config.isNight = !Config.isNight;
                showAnimation();
                refreshUI();
                item.setTitle(Config.isNight ? getResources().getString(R.string.display_model_light) : getResources().getString(R.string.display_model_night));

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 切换不同的Fragment，当前只有一个Fragment，后续扩展
     *
     * @param fragment
     */
    private void switchFragment(Fragment fragment) {
        if (currentFragment == null || !currentFragment.getClass().getName().equals(fragment.getClass().getName()))
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
                    .commit();
        currentFragment = fragment;
    }

    /**
     * 根据ID加载不同的Fragment，当前只有一个Fragment，后续扩展
     *
     * @param itemId
     * @return
     */
    private Fragment getFragmentById(int itemId) {
        Fragment fragment = null;
        switch (itemId) {
            case R.id.zhihuitem:
                fragment = new ZhihuFragment();
                break;
        }
        return fragment;

    }

    private void addFragmentAndTitle() {
        titleArrayMap.put(R.id.zhihuitem, getResources().getString(R.string.zhihu));

    }

    private View.OnClickListener navigationOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            drawer.openDrawer(GravityCompat.START);
        }
    };


    //当Recycle滑动到底部时，加载更多新闻
    public interface LoadingMore {
        void loadingStart();

        void loadingFinish();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, R.string.app_exit_tip, Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
        }
    }


    /**
     * 展示一个切换动画
     */
    private void showAnimation() {
        final View decorView = getWindow().getDecorView();
        Bitmap cacheBitmap = getCacheBitmapFromView(decorView);
        if (decorView instanceof ViewGroup && cacheBitmap != null) {
            final View view = new View(this);
            view.setBackgroundDrawable(new BitmapDrawable(getResources(), cacheBitmap));
            ViewGroup.LayoutParams layoutParam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            ((ViewGroup) decorView).addView(view, layoutParam);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
            objectAnimator.setDuration(300);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    ((ViewGroup) decorView).removeView(view);
                }
            });
            objectAnimator.start();
        }
    }

    /**
     * 获取一个 View 的缓存视图
     *
     * @param view
     * @return
     */
    private Bitmap getCacheBitmapFromView(View view) {
        final boolean drawingCacheEnabled = true;
        view.setDrawingCacheEnabled(drawingCacheEnabled);
        view.buildDrawingCache(drawingCacheEnabled);
        final Bitmap drawingCache = view.getDrawingCache();
        Bitmap bitmap;
        if (drawingCache != null) {
            bitmap = Bitmap.createBitmap(drawingCache);
            view.setDrawingCacheEnabled(false);
        } else {
            bitmap = null;
        }
        return bitmap;
    }

}
