package shashov.translate.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import shashov.translate.dao.Language;
import shashov.translate.dao.Translate;

import java.util.List;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface TranslateView extends MvpView {
    void showLoading();
    void showNoData();

    @StateStrategyType(SkipStrategy.class)
    void showTranslate(Translate translate);
    void populateLangs(List<Language> langs);
}
