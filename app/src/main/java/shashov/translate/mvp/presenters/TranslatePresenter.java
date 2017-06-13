package shashov.translate.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import shashov.translate.TranslateApp;
import shashov.translate.dao.Translate;
import shashov.translate.mvp.models.HistoryModel;
import shashov.translate.mvp.models.LangsModel;
import shashov.translate.mvp.models.TranslateModel;
import shashov.translate.mvp.views.TranslateView;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Created by kirill on 11.06.17.
 */
@InjectViewState
public class TranslatePresenter extends BasePresenter<TranslateView> {

    @Inject
    LangsModel langsModel;
    @Inject
    TranslateModel translateModel;
    @Inject
    HistoryModel historyModel;

    private State state;
    private Translate currentTranslate;
    private Subscription subscriptionChangeInput;

    public TranslatePresenter() {
        TranslateApp.getAppComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        langsModel.getLangs(
                langs -> getViewState().populateLangs(langs),
                error -> {
                }
        );

        currentTranslate = historyModel.getLast();
        if (currentTranslate == null) {
            currentTranslate = new Translate();
        }

        loadData();
    }

    private void loadData() {
        translateModel.unsubscribe();

        onLoading();

        //empty request
        if (currentTranslate.getInput().isEmpty()) {
            currentTranslate.setOutput("");
            onTranslated();
            return;
        }

        //load translate
        translateModel.translate(currentTranslate,
                (data) -> {
                    currentTranslate = new Translate(data);
                    onTranslated();
                }, (error) -> {
                    onNoData();
                });
    }

    private void onTranslated() {
        getViewState().showTranslate(currentTranslate);
        state = State.TRANSLATED;
    }

    private void onLoading() {
        state = State.LOADING;
        getViewState().showLoading();
    }

    private void onNoData() {
        state = State.NO_DATA;
        getViewState().showNoData();
    }

    @Override
    public void attachView(TranslateView view) {
        super.attachView(view);

        //update state for saved translate (for example, fav status)
        Translate translate = historyModel.findTranslate(currentTranslate);
        if (translate != null) {
            currentTranslate = new Translate(translate);
        }

        //restore view state
        if (state == State.LOADING) {
            onLoading();
        } else if (state == State.NO_DATA) {
            onNoData();
        } else if (state == State.TRANSLATED) {
            onTranslated();
        }
    }

    public void onChangeFavorite() {
        historyModel.changeFavorite(currentTranslate);
    }

    public void onChangeLang(boolean isInput, String langCode) {
        if (isInput) {
            currentTranslate.setFromLang(langCode);
        } else {
            currentTranslate.setToLang(langCode);
        }
        loadData();
    }

    public void onSwapLangs() {
        if (currentTranslate.getToLang().equals(currentTranslate.getFromLang())) {
            return;
        }

        String temp = currentTranslate.getToLang();
        currentTranslate.setToLang(currentTranslate.getFromLang());
        currentTranslate.setFromLang(temp);

        loadData();
    }

    public void onReloadTranslate() {
        loadData();
    }

    public void onChangeInput(String input) {
        onChangeInput(input, true);
    }

    public void onClearInput() {
        onChangeInput("", false);
    }

    private void onChangeInput(String input, boolean isDelay) {
        if (currentTranslate.getInput().equals(input)) {
            return;
        }

        if (subscriptionChangeInput != null) {
            subscriptionChangeInput.unsubscribe();
        }

        currentTranslate.setInput(input);

        if (isDelay) {
            subscriptionChangeInput = Observable
                    .just(input)
                    .delay(1250, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(inputText -> loadData());
            unsubscribeOnDestroy(subscriptionChangeInput);
        } else {
            loadData();
        }
    }


    enum State {
        LOADING,
        NO_DATA,
        TRANSLATED
    }
}
