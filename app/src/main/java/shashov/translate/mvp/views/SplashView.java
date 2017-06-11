package shashov.translate.mvp.views;

import com.arellomobile.mvp.MvpView;

/**
 * Created by kirill on 10.06.17.
 */
public interface SplashView extends MvpView {

    void showLoading();

    void showNoData();

    void showApp();
}
