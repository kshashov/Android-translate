package shashov.translate.internals.mvp.presenters;

import shashov.translate.R;
import shashov.translate.TranslateApp;
import shashov.translate.dao.Language;
import shashov.translate.dao.Translate;
import shashov.translate.internals.mvp.MVP;
import shashov.translate.internals.mvp.models.HistoryModel;
import shashov.translate.internals.mvp.models.LanguageModel;
import shashov.translate.internals.mvp.models.TranslateModel;
import shashov.translate.internals.mvp.views.TranslateView;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class TranslatePresenter extends MVP.Presenter<TranslateView> {
    @Inject
    LanguageModel langModel;
    @Inject
    TranslateModel translateModel;
    @Inject
    HistoryModel historyModel;

    private List<Language> langs = new ArrayList<>();
    private Translate lastTranslate; //last successful translate
    private int posOfFromLang = 0;
    private int posOfToLang = 0;
    private boolean isLoading = false;

    public TranslatePresenter() {
        TranslateApp.getAppComponent().inject(this);
    }

    public void loadData(Translate translate) {
        getView().showLoading();
        lastTranslate = historyModel.getLast();
        if (translate == null) {
            //try to open last translate record from data model (==realm)
            if (lastTranslate != null) {
                //save output in cache
                translate = lastTranslate;
            } else {
                //open empty translate request
                translate = new Translate();
                translate.setInput("");
                translate.setFromLang(getAppLang());
                translate.setToLang(getAppLang());
            }
        }

        if (!langs.isEmpty()) {
            //for example: if fragment recreate
            initView(translate);
        } else {
            //load langs list
            final Translate finalTranslateCopy = translate;
            langModel.getLangs(getAppLang(), new MVP.Model.OnDataLoaded<List<Language>>() {
                @Override
                public void onSuccess(List<Language> data) {
                    langs.clear();
                    langs.addAll(data);
                    initView(finalTranslateCopy);
                }

                @Override
                public void onFail(String error) {
                    getView().showEmpty();
                    getView().showError(error);
                }
            });
        }

    }

    private int getPositionOfLang(String langCode) {
        for (int i = 0; i < langs.size(); i++) {
            if (langs.get(i).getCode().equals(langCode)) {
                return i;
            }
        }
        return 0;
    }

    private void initView(Translate translate) {
        //open translate in view
        if (translate != null) {
            //open saved translate
            posOfFromLang = getPositionOfLang(translate.getFromLang());
            posOfToLang = getPositionOfLang(translate.getToLang());
            getView().showInput(translate.getInput());
        }

        getView().showContent();
        getView().populateLangs(langs);
        getView().changeLang(posOfFromLang, true);
        getView().changeLang(posOfToLang, false);

        getView().showLoadingTranslate(isLoading);
        if (!isLoading) {
            getView().showTranslate();
        }
    }

    public void swapLangs() {
        int pos = posOfFromLang;
        posOfFromLang = posOfToLang;
        posOfToLang = pos;

        getView().changeLang(posOfFromLang, true);
        getView().changeLang(posOfToLang, false);
        getView().showTranslate();
    }

    public void selectLang(int position, boolean isInput) {
        if (isInput) posOfFromLang = position;
        else posOfToLang = position;
        getView().showTranslate();
    }

    public void changeFavorite() {
        if (lastTranslate != null) {
            historyModel.changeFavorite(lastTranslate);
        }
    }

    public void translate(final String input) {
        if (langs.isEmpty()) {
            return;
        }
        if (getView() == null) {
            return;
        }
        if (isLoading) {
            //stop current task
            translateModel.unsubscribe();
        }

        showLoadingTranslate(true);
        if (lastTranslate != null
                && input.equals(lastTranslate.getInput())
                && langs.get(posOfFromLang).getCode().equals(lastTranslate.getFromLang())
                && langs.get(posOfToLang).getCode().equals(lastTranslate.getToLang())) {
            //result in cache
            showOutput(lastTranslate);
            return;
        }

        //empty request
        if (input.isEmpty()) {
            showOutput(new Translate());
            return;
        }

        //new translate request
        final Translate translate = new Translate();
        translate.setInput(input);
        translate.setFromLang(langs.get(posOfFromLang).getCode());
        translate.setToLang(langs.get(posOfToLang).getCode());

        translateModel.translate(translate, new MVP.Model.OnDataLoaded<Translate>() {
            @Override
            public void onSuccess(Translate data) {
                if (getView() != null) {
                    lastTranslate = data;
                    showOutput(lastTranslate);
                }
            }

            @Override
            public void onFail(String error) {
                if (getView() != null) {
                    showLoadingTranslate(false);
                    getView().showTranslateReload();
                    getView().showError(error);
                }
            }
        });
    }

    @Override
    public void onStop(boolean changingConfigurations) {
        super.onStop(changingConfigurations);
        if (!changingConfigurations) {
            translateModel.unsubscribe();
            langModel.unsubscribe();
            isLoading = false;
        }
    }

    private void showOutput(Translate output) {
        if (getView() != null) {
            showLoadingTranslate(false);
            getView().showOutput(output);
        }
    }

    private void showLoadingTranslate(boolean isLoading) {
        this.isLoading = isLoading;
        if (getView() != null) {
            getView().showLoadingTranslate(isLoading);
        }
    }

    private String getAppLang() {
        return getView().getActivity().getString(R.string.lang_code);
    }
}
