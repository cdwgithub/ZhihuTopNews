package com.cdw.zhihutopnews.presenter.impleView;

import com.cdw.zhihutopnews.bean.ZhihuDaily;

/**
 * Created by CDW on 2016/11/20.
 */

public interface ITopStories {
    void showError(String error);
    void showTopStory(ZhihuDaily zhihuDaily);
}
