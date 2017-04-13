package shashov.translate.internals.mvp.models;

import android.util.Log;
import com.google.gson.annotations.SerializedName;
import io.realm.Realm;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import shashov.translate.dao.Translate;
import shashov.translate.internals.mvp.MVP;
import shashov.translate.networking.YandexAPI;
import shashov.translate.support.NetworkManager;

import java.util.Date;
import java.util.List;

public class TranslateModel implements MVP.Model {
    private static final String TAG = TranslateModel.class.getSimpleName();
    private YandexAPI yandexAPI;
    private NetworkManager networkManager;
    private Subscription observable;
    private HistoryModel historyModel;
    private final Realm realm;

    public TranslateModel(HistoryModel historyModel, YandexAPI yandexAPI, Realm realm, NetworkManager networkManager) {
        this.yandexAPI = yandexAPI;
        this.realm = realm;
        this.networkManager = networkManager;
        this.historyModel = historyModel;
    }

    public void translate(final Translate translate, final OnDataLoaded<Translate> onDataLoaded) {
        if (!networkManager.isConnected()) {
            onDataLoaded.onFail("No cached data and not Internet"); //TODO text from constant
        } else {
            Translate savedTranslate = historyModel.findTranslate(translate);
            if (savedTranslate != null) {
                realm.beginTransaction();
                savedTranslate.setTime((new Date()).getTime());
                realm.commitTransaction();
                onDataLoaded.onSuccess(savedTranslate);
                return;
            }
            observable = yandexAPI.translate(YandexAPI.API_CODE, translate.getFromLang() + "-" + translate.getToLang(), translate.getInput())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<TranslateModel.TranslateResponse>() {
                                   @Override
                                   public void onCompleted() {
                                   }

                                   @Override
                                   public void onError(Throwable e) {
                                       Log.d(TAG, "onError() called with: " + "e = [" + e + "]");
                                       onDataLoaded.onFail(e.toString());
                                   }

                                   @Override
                                   public void onNext(TranslateModel.TranslateResponse translateResponse) {
                                       Log.d(TAG, "onNext() called with: " + "translateResponse = ["
                                               + translateResponse + "]");

                                       if (!translateResponse.getTextList().isEmpty()) {
                                           translate.setOutput(translateResponse.getTextList().get(0));
                                       } else {
                                           translate.setOutput("");
                                       }

                                       cache(translate);
                                       onDataLoaded.onSuccess(translate);
                                   }
                               }
                    );
        }
    }

    public void unsubscribe() {
        if (observable != null) {
            observable.unsubscribe();
        }
    }

    public void cache(Translate translate) {
        realm.beginTransaction();
        translate.setTime((new Date()).getTime());
        translate.setFavTime(0);
        realm.copyToRealm(translate);
        realm.commitTransaction();
    }

    public class TranslateResponse {

        @SerializedName("code")
        String code; //TODO use code for print error message
        @SerializedName("text")
        List<String> textList;

        public List<String> getTextList() {
            return textList;
        }

        public void setTextList(List<String> textList) {
            this.textList = textList;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
