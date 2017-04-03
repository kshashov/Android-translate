package shashov.translate.internals.mvp.presenters;

import android.os.Bundle;
import io.realm.OrderedRealmCollection;
import shashov.translate.TranslateApp;
import shashov.translate.eventbus.RxEventBus;
import shashov.translate.eventbus.events.OpenTranslateEvent;
import shashov.translate.internals.mvp.MVP;
import shashov.translate.internals.mvp.models.TranslateModel;
import shashov.translate.internals.mvp.views.HistoryView;
import shashov.translate.realm.Translate;

import javax.inject.Inject;

public class HistoryPresenter extends MVP.Presenter<HistoryView> {

    @Inject
    TranslateModel translateModel;

    @Inject
    RxEventBus eventBus;

    private OrderedRealmCollection<Translate> data;

    public HistoryPresenter() {
        TranslateApp.getAppComponent().inject(this);
    }

    public void loadData() {
        getView().showLoading();
        if (data != null) {
            //already loaded
            showContent();
            return;
        }

        translateModel.getAll(new MVP.Model.OnDataLoaded<OrderedRealmCollection<Translate>>() {
            @Override
            public void onSuccess(OrderedRealmCollection<Translate> data) {
                HistoryPresenter.this.data = data;
                showContent();
            }

            @Override
            public void onFail(String error) {
                getView().showEmpty();
                getView().showError(error);
            }
        });
    }

    private void showContent() {
        if (data != null) {
            getView().showContent();
            getView().populateList(data);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

    }

    public void onClickTranslate(Translate translate) {
        eventBus.post(new OpenTranslateEvent(translate));
    }
}
