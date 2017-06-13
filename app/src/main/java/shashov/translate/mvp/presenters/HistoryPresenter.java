package shashov.translate.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.squareup.otto.Bus;
import io.realm.OrderedRealmCollection;
import shashov.translate.TranslateApp;
import shashov.translate.common.events.OpenTranslateEvent;
import shashov.translate.dao.Translate;
import shashov.translate.mvp.models.HistoryModel;
import shashov.translate.mvp.views.HistoryView;
import shashov.translate.ui.adapters.HistorySearchAdapter;

import javax.inject.Inject;

/**
 * Created by kirill on 13.06.17.
 */
@InjectViewState
public class HistoryPresenter extends MvpPresenter<HistoryView> implements HistorySearchAdapter.HistoryListener {

    @Inject
    HistoryModel historyModel;
    @Inject
    Bus eventBus;

    private OrderedRealmCollection<Translate> all;
    private OrderedRealmCollection<Translate> favs;
    private HistoryView.HistoryViewState dataState;
    private State state = State.LOADED;

    public HistoryPresenter() {
        TranslateApp.getAppComponent().inject(this);

        historyModel.getHistory((all, favs) -> {
            this.all = all;
            this.favs = favs;
        });
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        dataState = new HistoryView.HistoryViewState();
    }

    @Override
    public void attachView(HistoryView view) {
        super.attachView(view);
        getViewState().restoreState(dataState);

        if (state == State.LOADING) {
            getViewState().showLoadingContent();
        } else if (state == State.DELETE_DIALOG) {
            onDeleteHistory();
        } else if (state == State.LOADED) {
            onShowContent();
        }
    }

    public void onChangeTab(boolean isAll) {
        if (dataState.isAllTab == isAll) {
            return;
        }
        dataState.isAllTab = isAll;
        onShowContent();
    }

    @Override
    public void onClickItem(Translate translate) {
        eventBus.post(new OpenTranslateEvent(translate));
    }

    @Override
    public void onChangeFavorite(Translate translate) {
        historyModel.changeFavorite(translate);
    }

    public void onDeleteHistory() {
        getViewState().showDeleteDialog();
    }

    private void onShowContent() {
        state = State.LOADED;
        getViewState().showContent(dataState.isAllTab ? all : favs);
    }

    public void saveSearchText(String newText) {
        dataState.searchString = newText;
    }

    public void deleteCloseDeleteDialog(boolean isDelete) {
        if (isDelete) {
            historyModel.delete(dataState.isAllTab ? all : favs, dataState.isAllTab);
        }

        state = State.LOADED;
    }

    public enum State {
        LOADING,
        DELETE_DIALOG,
        LOADED
    }
}
