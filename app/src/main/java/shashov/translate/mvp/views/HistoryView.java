package shashov.translate.mvp.views;

import android.os.Parcelable;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import io.realm.OrderedRealmCollection;
import shashov.translate.dao.Translate;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface HistoryView extends MvpView {
    void showContent(boolean isAll, OrderedRealmCollection<Translate> data);

    void search(String newText);

    @StateStrategyType(SkipStrategy.class)
    void showDeleteDialog();

    @StateStrategyType(SkipStrategy.class)
    void closeDeleteDialog();

    @StateStrategyType(SkipStrategy.class)
    void setRVState(Parcelable rvState);

    @StateStrategyType(SkipStrategy.class)
    void closeSearchViewFocus();
}
