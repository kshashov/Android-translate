package shashov.translate.internals.mvp.presenters;

import io.realm.OrderedRealmCollection;
import shashov.translate.TranslateApp;
import shashov.translate.eventbus.RxEventBus;
import shashov.translate.eventbus.events.OpenTranslateEvent;
import shashov.translate.internals.mvp.MVP;
import shashov.translate.internals.mvp.models.TranslateModel;
import shashov.translate.internals.mvp.views.HistoryView;
import shashov.translate.realm.Translate;

import javax.inject.Inject;
import java.util.HashMap;

public class HistoryPresenter extends MVP.Presenter<HistoryView> {

    @Inject
    TranslateModel translateModel;

    @Inject
    RxEventBus eventBus;
    private HashMap<String, OrderedRealmCollection<Translate>> data = new HashMap<>();
    public static final String FAV = "fav";
    public static final String ALL = "all";

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
        translateModel.getAll(onDataLoaded);
    }

    private void loadFavorites(MVP.Model.OnDataLoaded<OrderedRealmCollection<Translate>> onDataLoaded) {
        translateModel.getAll(onDataLoaded); //TODO
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
}
