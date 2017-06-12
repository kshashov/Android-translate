package shashov.translate.mvp.views;

import com.arellomobile.mvp.MvpView;
import shashov.translate.dao.Language;
import shashov.translate.dao.Translate;

import java.util.List;

/**
 * Created by kirill on 11.06.17.
 */
public interface TranslateView extends MvpView {
    void showLoading();

    void showNoData();

    void showTranslate(Translate translate);

    void populateLangs(List<Language> langs);
}
