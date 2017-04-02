package shashov.translate.internals.mvp.views;

import shashov.translate.internals.mvp.MVP;
import shashov.translate.realm.Language;

import java.util.List;

/**
 * Created by Aksiom on 6/29/2016.
 */
public interface TranslateView extends MVP.View {

    void populateList(List<Language> data);

    void changeLang(int langCode, boolean isInput);

    void showLoadingTranslate(boolean isLoading);

    void showTranslate();

    void showInput(String input);

    void showOutput(String output);

    void showTranslateReload();

}
