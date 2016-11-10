package com.cdw.zhihutopnews.presenter.impleView;

/**
 * Created by CDW on 2016/11/3.
 */

public interface IBaseFragment {
    void showProgressDialog();

    void hidProgressDialog();

    void showError(String error);
}
