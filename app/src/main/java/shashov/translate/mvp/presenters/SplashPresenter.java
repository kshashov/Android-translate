package shashov.translate.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import rx.Subscription;
import shashov.translate.TranslateApp;
import shashov.translate.mvp.models.LangsModel;
import shashov.translate.mvp.views.SplashView;

import javax.inject.Inject;

@InjectViewState
public class SplashPresenter extends BasePresenter<SplashView> {
    @Inject
    LangsModel langsModel;
    private Subscription subscription;

    public SplashPresenter() {
        TranslateApp.getAppComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        loadData();
    }

    public void loadData() {
        if (subscription != null) {
            subscription.unsubscribe();
        }

        //load
        getViewState().showLoading();
        subscription = langsModel.getLangs(
                langs -> getViewState().showApp(),
                throwable -> getViewState().showNoData()
        );

        if (subscription != null) {
            unsubscribeOnDestroy(subscription);
        }
    }
}
