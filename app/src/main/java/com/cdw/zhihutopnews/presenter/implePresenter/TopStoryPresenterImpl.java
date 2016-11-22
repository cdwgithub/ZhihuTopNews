package com.cdw.zhihutopnews.presenter.implePresenter;

import android.content.Context;
import android.util.Log;

import com.cdw.zhihutopnews.api.ApiManage;
import com.cdw.zhihutopnews.bean.ZhihuDaily;
import com.cdw.zhihutopnews.config.Config;
import com.cdw.zhihutopnews.presenter.ITopStoryPresenter;
import com.cdw.zhihutopnews.presenter.impleView.ITopStories;
import com.cdw.zhihutopnews.uitls.CacheUtil;
import com.google.gson.Gson;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by CDW on 2016/11/20.
 */

public class TopStoryPresenterImpl extends BasePresenterImpl implements ITopStoryPresenter {

    private ITopStories iTopStories;
    private CacheUtil cacheUtil;
    private Gson gson = new Gson();

    public TopStoryPresenterImpl (Context context,ITopStories iTopStories){
        cacheUtil = CacheUtil.get(context);
        this.iTopStories=iTopStories;
    }

    @Override
    public void getTopStory() {
        Subscription subscription = ApiManage.getInstence().getZhihuApiService().getLastDaily()
                .map(new Func1<ZhihuDaily, ZhihuDaily>() {
                    @Override
                    public ZhihuDaily call(ZhihuDaily zhihuDaily) {
                        return zhihuDaily;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ZhihuDaily>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        iTopStories.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(ZhihuDaily zhihuDaily) {
                    //    Log.d("TopStoryPresenterImpl", gson.toJson(zhihuDaily));
                        cacheUtil.put(Config.ZHIHU, gson.toJson(zhihuDaily));
                        iTopStories.showTopStory(zhihuDaily);
                    }
                });
        addSubscription(subscription);
    }
}
