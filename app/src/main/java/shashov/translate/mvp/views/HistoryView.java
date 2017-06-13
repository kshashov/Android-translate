package shashov.translate.mvp.views;

import com.arellomobile.mvp.MvpView;
import io.realm.OrderedRealmCollection;
import shashov.translate.dao.Translate;

/**
 * Created by kirill on 13.06.17.
 */
public interface HistoryView extends MvpView {
    public static final String TAG = "HistoryFragment";

    void showContent(OrderedRealmCollection<Translate> data);

    void showLoadingContent();

    void showDeleteDialog();

    void restoreState(HistoryViewState viewState);

    class HistoryViewState {
        public String searchString = "";
        public boolean isAllTab = true;
    }
}
