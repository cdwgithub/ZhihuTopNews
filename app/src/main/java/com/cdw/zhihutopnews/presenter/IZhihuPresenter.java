package com.cdw.zhihutopnews.presenter;

/**
 * Created by CDW on 2016/11/5.
 */

public interface IZhihuPresenter extends BasePresenter {
    void getLastZhihuNews();
    void getTheDaily(String date);
    void getLastFromCache();
}
