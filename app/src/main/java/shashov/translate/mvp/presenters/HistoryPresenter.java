package shashov.translate.mvp.presenters;

import android.os.Parcelable;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.squareup.otto.Bus;
import io.realm.OrderedRealmCollection;
import shashov.translate.TranslateApp;
import shashov.translate.dao.Translate;
import shashov.translate.mvp.models.HistoryModel;
import shashov.translate.mvp.views.HistoryView;
import shashov.translate.ui.adapters.HistorySearchAdapter;

import javax.inject.Inject;

@InjectViewState
public class HistoryPresenter extends MvpPresenter<HistoryView> implements HistorySearchAdapter.HistoryListener {

    @Inject
    HistoryModel historyModel;
    @Inject
    Bus eventBus;

    private OrderedRealmCollection<Translate> all;
    private OrderedRealmCollection<Translate> favs;
    private Parcelable rvState;
    private String searchText = "";

    public HistoryPresenter() {
        TranslateApp.getAppComponent().inject(this);
        //load data from model
        historyModel.getHistory((all, favs) -> {
            this.all = all;
            this.favs = favs;
        });

    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        //init view state
        getViewState().showContent(true, all);
        getViewState().search(searchText);
    }

    @Override
    public void attachView(HistoryView view) {
        super.attachView(view);
        getViewState().setRVState(rvState);
        getViewState().closeSearchViewFocus();
    }

    public void onChangeTab(boolean isAll) {
        getViewState().showContent(isAll, isAll ? all : favs);
        getViewState().search(searchText);
    }

    @Override
    public void onClickItem(Translate translate) {
        eventBus.post(new TranslatePresenter.OpenTranslateEvent(translate));
    }

    @Override
    public void onChangeFavorite(Translate translate) {
        historyModel.changeFavorite(translate);
    }

    public void onChangeSearchText(String newText) {
        searchText = newText;
        getViewState().search(searchText);
    }

    public void onDeleteHistory() {
        getViewState().showDeleteDialog();
    }

    public void onPositiveCloseDeleteDialog(boolean isAll) {
        historyModel.delete(isAll ? all : favs, isAll);
        getViewState().closeDeleteDialog();
    }

    public void onNegativeCloseDeleteDialog() {
        getViewState().closeDeleteDialog();
    }

    public void saveRVState(Parcelable rvState) {
        this.rvState = rvState;
    }
}
