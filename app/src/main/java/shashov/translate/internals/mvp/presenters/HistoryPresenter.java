package shashov.translate.internals.mvp.presenters;

import android.os.Bundle;
import shashov.translate.TranslateApp;
import shashov.translate.eventbus.RxEventBus;
import shashov.translate.eventbus.events.OpenTranslateEvent;
import shashov.translate.internals.mvp.MVP;
import shashov.translate.internals.mvp.models.TranslateModel;
import shashov.translate.internals.mvp.views.HistoryView;
import shashov.translate.realm.Translate;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by Aksiom on 6/29/2016.
 */
public class HistoryPresenter extends MVP.Presenter<HistoryView> {

    @Inject
    TranslateModel translateModel;

    @Inject
    RxEventBus eventBus;

    public HistoryPresenter() {
        TranslateApp.getAppComponent().inject(this);
    }

    public void loadData() {
        getView().showLoading();
        translateModel.getAll(new MVP.Model.OnDataLoaded<List<Translate>>() {
            @Override
            public void onSuccess(List<Translate> data) {
                getView().showContent();
                getView().populateList(data);
            }

            @Override
            public void onFail(String error) {
                getView().showEmpty();
                getView().showError(error);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

    }

    public void onClickTranslate(Translate translate) {
        eventBus.post(new OpenTranslateEvent(translate));
    }
}
