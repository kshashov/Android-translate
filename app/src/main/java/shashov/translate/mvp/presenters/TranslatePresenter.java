package shashov.translate.mvp.presenters;

import android.support.annotation.NonNull;
import com.arellomobile.mvp.InjectViewState;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
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

@InjectViewState
public class TranslatePresenter extends BasePresenter<TranslateView> {

    @Inject
    LangsModel langsModel;
    @Inject
    TranslateModel translateModel;
    @Inject
    HistoryModel historyModel;
    @Inject
    Bus eventBus;

    private Translate currentTranslate;
    private Subscription subscriptionChangeInput;
    private Subscription subscription;

    public TranslatePresenter() {
        TranslateApp.getAppComponent().inject(this);
        eventBus.register(this);
        //init current translate state
        currentTranslate = historyModel.getLast();
        if (currentTranslate == null) {
            currentTranslate = new Translate();
        }
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        //load languages list
        langsModel.getLangs(
                langs -> getViewState().populateLangs(langs),
                error -> {
                }
        );

        loadData();
    }

    @Override
    public void attachView(TranslateView view) {
        super.attachView(view);
        //update state for saved translate (for example, fav status)
        Translate translate = historyModel.findTranslate(currentTranslate);
        if (translate != null) {
            currentTranslate = new Translate(translate);
        }

        if ((subscription == null) || subscription.isUnsubscribed()) {
            loadData();
        }
    }

    /**
     * load translate from model for current translate state
     */
    private void loadData() {
        if (subscription != null) {
            subscription.unsubscribe();
        }

        getViewState().showLoading();

        //empty request
        if (currentTranslate.getInput().isEmpty()) {
            currentTranslate.setOutput("");
            getViewState().showTranslate(currentTranslate);
            return;
        }

        //load translate
        subscription = translateModel.translate(currentTranslate,
                (data) -> getViewState().showTranslate(currentTranslate = new Translate(data)),
                (error) -> getViewState().showNoData());

        if (subscription != null) {
            unsubscribeOnDestroy(subscription);
        }
    }

    @Subscribe
    public void openTranslate(OpenTranslateEvent setTranslateEvent) {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        currentTranslate = setTranslateEvent.translate;
        eventBus.post(new MainPresenter.OpenTranslateEvent());
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

        if (!isDelay) {
            loadData();
        } else {
            subscriptionChangeInput = Observable
                    .just(input)
                    .delay(1250, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(inputText -> loadData());
            unsubscribeOnDestroy(subscriptionChangeInput);
        }
    }

    static class OpenTranslateEvent {
        public Translate translate;

        OpenTranslateEvent(@NonNull Translate translate) {
            this.translate = translate;
        }
    }
}
