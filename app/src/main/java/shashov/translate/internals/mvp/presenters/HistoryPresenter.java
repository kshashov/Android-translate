package shashov.translate.internals.mvp.presenters;

import com.squareup.otto.Bus;
import io.realm.OrderedRealmCollection;
import shashov.translate.R;
import shashov.translate.TranslateApp;
import shashov.translate.dao.Translate;
import shashov.translate.eventbus.events.OpenTranslateEvent;
import shashov.translate.internals.mvp.MVP;
import shashov.translate.internals.mvp.models.HistoryModel;
import shashov.translate.internals.mvp.views.HistoryView;

import javax.inject.Inject;
import java.util.HashMap;

public class HistoryPresenter extends MVP.Presenter<HistoryView> {

    @Inject
    HistoryModel historyModel;

    @Inject
    Bus eventBus;

    private HashMap<String, OrderedRealmCollection<Translate>> data = new HashMap<>();
    private static final String FAV = "fav";
    private static final String ALL = "all";

    public HistoryPresenter() {
        TranslateApp.getAppComponent().inject(this);
    }

    public void loadData(boolean isAll) {
        getView().showLoading();
        final String key = isAll ? ALL : FAV;

        if (data.containsKey(key)) {
            //already loaded
            showContent(data.get(key));
            return;
        }

        MVP.Model.OnDataLoaded<OrderedRealmCollection<Translate>> onDataLoaded = new MVP.Model.OnDataLoaded<OrderedRealmCollection<Translate>>() {
            @Override
            public void onSuccess(OrderedRealmCollection<Translate> data) {
                HistoryPresenter.this.data.put(key, data);
                showContent(data);
            }

            @Override
            public void onFail(String error) {
                getView().showEmpty();
                getView().showError(error);
            }
        };

        if (isAll) {
            loadAll(onDataLoaded);
        } else {
            loadFavorites(onDataLoaded);
        }
    }

    private void loadAll(MVP.Model.OnDataLoaded<OrderedRealmCollection<Translate>> onDataLoaded) {
        historyModel.getAll(onDataLoaded);
    }

    private void loadFavorites(MVP.Model.OnDataLoaded<OrderedRealmCollection<Translate>> onDataLoaded) {
        historyModel.getFav(onDataLoaded);
    }

    private void showContent(OrderedRealmCollection<Translate> translates) {
        if (data != null) {
            getView().showContent();
            getView().populateList(translates);
        }
    }

    public void onClickTranslate(Translate translate) {
        eventBus.post(new OpenTranslateEvent(translate));
    }

    public void onChangeFavorite(Translate translate) {
        historyModel.changeFavorite(translate);
    }

    public void onDeleteHistory(boolean isAll) {
        String text = getView().getActivity().getString(isAll ? R.string.deleteAll : R.string.deleteFav);
        getView().showDeleteDialog(text);
    }

    public void deleteHistory(boolean isAll) {
        final String key = isAll ? ALL : FAV;
        if (data.containsKey(key)) {
            historyModel.delete(data.get(key), isAll);
        }
    }
}
