package shashov.translate.internals.mvp.views;

import shashov.translate.dao.Language;
import shashov.translate.internals.mvp.MVP;

import java.util.List;

public interface TranslateView extends MVP.View {

    void populateList(List<Language> data);

    void changeLang(int langCode, boolean isInput);

    void showLoadingTranslate(boolean isLoading);

    void showTranslate();

    void showInput(String input);

    void showOutput(String output);

    void showTranslateReload();

}
