package shashov.translate.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import rx.Subscription;
import shashov.translate.TranslateApp;
import shashov.translate.mvp.models.LangsModel;
import shashov.translate.mvp.views.SplashView;

import javax.inject.Inject;

/**
 * Created by kirill on 10.06.17.
 */
@InjectViewState
public class SplashPresenter extends BasePresenter<SplashView> {
    @Inject
    LangsModel langsModel;
    private State state;

    public SplashPresenter() {
        TranslateApp.getAppComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        loadData();
    }

    @Override
    public void attachView(SplashView view) {
        super.attachView(view);
        if (isInRestoreState(view)) {
            updateUI(state);
        }
    }

    private void updateUI(State state) {
        if ((this.state = state) == State.LOADING) {
            getViewState().showLoading();
        } else {
            getViewState().showNoData();
        }
    }

    public void loadData() {
        updateUI(State.LOADING);

        //load
        Subscription subscription = langsModel.getLangs(
                langs -> {
                    getViewState().showApp();
                },
                throwable -> updateUI(State.NO_DATA)
        );

        if (subscription != null) {
            unsubscribeOnDestroy(subscription);
        }
    }

    enum State {
        LOADING,
        NO_DATA
    }
}
