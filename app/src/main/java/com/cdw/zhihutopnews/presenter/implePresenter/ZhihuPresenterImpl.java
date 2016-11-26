package com.cdw.zhihutopnews.presenter.implePresenter;

import android.content.Context;
import android.util.Log;

import com.cdw.zhihutopnews.api.ApiManage;
import com.cdw.zhihutopnews.bean.ZhihuDaily;
import com.cdw.zhihutopnews.bean.ZhihuDailyItem;
import com.cdw.zhihutopnews.config.Config;
import com.cdw.zhihutopnews.presenter.IZhihuPresenter;
import com.cdw.zhihutopnews.presenter.impleView.IZhihuFragment;
import com.cdw.zhihutopnews.uitls.CacheUtil;
import com.google.gson.Gson;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by CDW on 2016/11/5.
 */

public class ZhihuPresenterImpl extends BasePresenterImpl implements IZhihuPresenter {
    private IZhihuFragment zhihuFragment;
    private CacheUtil cacheUtil;
    private Gson gson = new Gson();

    public ZhihuPresenterImpl(Context context, IZhihuFragment zhihuFragment) {
        this.zhihuFragment = zhihuFragment;
        cacheUtil = CacheUtil.get(context);
    }


    @Override
    public void getLastZhihuNews() {
        Log.d("ZhihuPresenterImpl", "getLastZhihuNews()");
        zhihuFragment.showProgressDialog();
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
                        zhihuFragment.hidProgressDialog();
                        zhihuFragment.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(ZhihuDaily zhihuDaily) {
                        zhihuFragment.hidProgressDialog();
                        //Log.d("ZhihuPresenterImpl", gson.toJson(zhihuDaily));
                        cacheUtil.put(Config.ZHIHU, gson.toJson(zhihuDaily));
                        zhihuFragment.updateList(zhihuDaily);
                        zhihuFragment.getTopStory(zhihuDaily);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void getTheDaily(String date) {
        Subscription subscription = ApiManage.getInstence().getZhihuApiService().getTheDaily(date)
                .map(new Func1<ZhihuDaily, ZhihuDaily>() {
                    @Override
                    public ZhihuDaily call(ZhihuDaily zhihuDaily) {
                        String date = zhihuDaily.getDate();
                        for (ZhihuDailyItem zhihuDailyItem : zhihuDaily.getStories()) {
                            zhihuDailyItem.setDate(date);
                        }
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
                        e.printStackTrace();
                        zhihuFragment.hidProgressDialog();
                        zhihuFragment.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(ZhihuDaily zhihuDaily) {
                        zhihuFragment.hidProgressDialog();
                        zhihuFragment.updateList(zhihuDaily);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void getLastFromCache() {
        Log.d("ZhihuPresenterImpl", "getLastFromCache()");
        if (cacheUtil.getAsJSONObject(Config.ZHIHU) != null) {
            ZhihuDaily zhihuDaily = gson.fromJson(cacheUtil.getAsJSONObject(Config.ZHIHU).toString(), ZhihuDaily.class);
            zhihuFragment.updateList(zhihuDaily);
            zhihuFragment.getTopStory(zhihuDaily);
        }
    }

    @Override
    public void unsubcrible() {

    }
}
