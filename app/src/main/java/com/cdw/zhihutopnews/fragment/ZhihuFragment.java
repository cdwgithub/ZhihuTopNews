package com.cdw.zhihutopnews.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;
import com.cdw.zhihutopnews.R;
import com.cdw.zhihutopnews.activity.ZhihuDetailActivity;
import com.cdw.zhihutopnews.adapter.ZhihuAdapter;
import com.cdw.zhihutopnews.bean.TopStoryItem;
import com.cdw.zhihutopnews.bean.ZhihuDaily;
import com.cdw.zhihutopnews.config.Config;
import com.cdw.zhihutopnews.presenter.implePresenter.ZhihuPresenterImpl;
import com.cdw.zhihutopnews.presenter.impleView.IZhihuFragment;
import com.cdw.zhihutopnews.view.GridItemDividerDecoration;
import com.cdw.zhihutopnews.widget.MainBanner;
import com.cdw.zhihutopnews.widget.WrapContentLinearLayoutManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by CDW on 2016/10/31.
 */

public class ZhihuFragment extends BaseFragment implements IZhihuFragment {
    TextView noConnectionText;

    @BindView(R.id.recycle_zhihu)
    RecyclerView recycleZhihu;
    @BindView(R.id.prograss)
    ProgressBar progress;
    @BindView(R.id.recycle_header)
    MainBanner recycleHeader;
    @BindView(R.id.header)
    RecyclerViewHeader header;
    @BindView(R.id.fragmeng_zhihu)
    FrameLayout fragmengZhihu;


    private ZhihuPresenterImpl zhihuPresenter;
    private ZhihuAdapter zhihuAdapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.OnScrollListener loadingMoreListener;
    private MainBanner.OnItemClickListener mainBanneronClickListener;
    private View view = null;
    private boolean connected = false;
    private boolean loading;
    private boolean monitoringConnectivity;
    private String currentLoadDate;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //使用setRetainInstance方法. 设置该方法为true后，可以让fragment在activity被重建时保持实例不变
        setRetainInstance(true);
        view = inflater.inflate(R.layout.fragment_zhihu, container, false);
        checkConnectivity(view);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialDate();
        initialView();
    }

    /**
     * 初始化视图
     */
    private void initialView() {
        initialListener();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            linearLayoutManager = new WrapContentLinearLayoutManager(getContext());

        } else {
            linearLayoutManager = new LinearLayoutManager(getContext());
        }
        recycleZhihu.setLayoutManager(linearLayoutManager);
        recycleZhihu.setHasFixedSize(true);//设定固定大小
        recycleZhihu.addItemDecoration(new GridItemDividerDecoration(getContext(), R.dimen.divider_height, R.color.divider));//设置item分割线
        // TODO: 16/8/13 add  animation
        recycleZhihu.setItemAnimator(new DefaultItemAnimator());
        recycleZhihu.setAdapter(zhihuAdapter);
        recycleZhihu.addOnScrollListener(loadingMoreListener);
        header.attachTo(recycleZhihu, true);
        recycleHeader.setOnItemClickListener(mainBanneronClickListener);


        loadLatestStory();

    }

    /**
     * 初始化数据
     */
    private void initialDate() {
        zhihuPresenter = new ZhihuPresenterImpl(getContext(), this);
        zhihuAdapter = new ZhihuAdapter(getContext());
    }

    /**
     * 加载最新新闻
     */
    private void loadLatestStory() {
        if (zhihuAdapter.getItemCount() > 0) {
            zhihuAdapter.clearData();
        }
        currentLoadDate = "0";

        if (connected) {
            zhihuPresenter.getLastZhihuNews();
        } else {
            zhihuPresenter.getLastFromCache();
        }


    }

    /**
     * 加载更多新闻
     */
    private void loadMoreStory() {
        zhihuAdapter.loadingStart();
        zhihuPresenter.getTheDaily(currentLoadDate);
    }


    private void initialListener() {
        loadingMoreListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) //向下滚动
                {
                    int visibleItemCount = linearLayoutManager.getChildCount();//获取可见的Item的数量
                    int totalItemCount = linearLayoutManager.getItemCount();//返回Adapter当前持有的Item的数量,等于List数据源的数目
                    int pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
                    if (!loading && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        loading = true;
                        loadMoreStory();
                    }
                }
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    connected = true;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            noConnectionText.setVisibility(View.GONE);
                            loadLatestStory();
                        }
                    });
                }

                @Override
                public void onLost(Network network) {
                    connected = false;
                }
            };

        }

        mainBanneronClickListener = new MainBanner.OnItemClickListener() {
            @Override
            public void onClick(View v, TopStoryItem topStoryItem) {
                Intent intent = new Intent(getActivity(), ZhihuDetailActivity.class);
                intent.putExtra("id", topStoryItem.getId());
                intent.putExtra("title", topStoryItem.getTitle());
                intent.putExtra("image", topStoryItem.getImage());
                getActivity().startActivity(intent);
            }
        };


    }


    @Override
    public void updateList(ZhihuDaily zhihuDaily) {
        if (loading) {
            loading = false;
            zhihuAdapter.loadingFinish();
        }
        currentLoadDate = zhihuDaily.getDate();
        zhihuAdapter.addItems(zhihuDaily.getStories());
        // if the new data is not full of the screen, need load more data
        if (!recycleZhihu.canScrollVertically(View.SCROLL_INDICATOR_BOTTOM)) {
            loadMoreStory();
        }
    }

    @Override
    public void getTopStory(ZhihuDaily zhihuDaily) {
        recycleHeader.showTopStory(zhihuDaily);
    }

    private ConnectivityManager.NetworkCallback connectivityCallback;

    /**
     * 检查网络连接
     *
     * @param view
     */
    private void checkConnectivity(View view) {
        final ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        //  Log.d("ZhihuFragment", "activeNetworkInfo.isConnected():"+activeNetworkInfo.isConnected());

        connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!connected && progress != null) {//不判断容易抛出空指针异常
            progress.setVisibility(View.INVISIBLE);
            if (noConnectionText == null) {
                ViewStub stub_text = (ViewStub) view.findViewById(R.id.stub_no_connection_text);
                noConnectionText = (TextView) stub_text.inflate();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                connectivityManager.registerNetworkCallback(
                        new NetworkRequest.Builder()
                                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(),
                        connectivityCallback);
                monitoringConnectivity = true;
            }

        }

    }


    @Override
    public void showProgressDialog() {
        if (progress != null) {
            progress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hidProgressDialog() {
        if (progress != null) {
            progress.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showError(String error) {
        if (recycleZhihu != null) {
            Snackbar.make(recycleZhihu, getString(R.string.snack_infor), Snackbar.LENGTH_SHORT).setAction("重试", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentLoadDate.equals("0")) {
                        zhihuPresenter.getLastZhihuNews();
                    } else {
                        zhihuPresenter.getTheDaily(currentLoadDate);
                    }
                }
            }).show();

        }
    }

    /**
     * 日间夜间模式切换时更新UI
     */
    public void refreshUI() {
        fragmengZhihu.setBackgroundColor(getResources().getColor(Config.isNight ? R.color.background_dark : R.color.background_light));
        zhihuAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
