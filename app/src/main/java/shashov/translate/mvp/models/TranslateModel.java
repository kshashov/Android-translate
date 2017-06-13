package shashov.translate.mvp.models;

import android.content.res.Resources;
import android.util.Log;
import com.google.gson.annotations.SerializedName;
import io.realm.Realm;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import shashov.translate.R;
import shashov.translate.common.NetworkManager;
import shashov.translate.common.YandexAPI;
import shashov.translate.dao.Translate;

import java.util.Date;
import java.util.List;

public class TranslateModel {
    private static final String TAG = TranslateModel.class.getSimpleName();
    private final YandexAPI yandexAPI;
    private final NetworkManager networkManager;
    private Subscription observable;
    private final HistoryModel historyModel;
    private final Realm realm;
    private final Resources resources;

    public TranslateModel(Resources resources, HistoryModel historyModel, YandexAPI yandexAPI, Realm realm, NetworkManager networkManager) {
        this.yandexAPI = yandexAPI;
        this.realm = realm;
        this.networkManager = networkManager;
        this.historyModel = historyModel;
        this.resources = resources;
    }

    public void translate(final Translate translate, final Action1<? super Translate> onSuccess, final Action1<String> onError) {
        //search translate in realm
        Translate savedTranslate = historyModel.findTranslate(translate);
        if (savedTranslate != null) {
            realm.beginTransaction();
            savedTranslate.setTime((new Date()).getTime());
            realm.commitTransaction();
            onSuccess.call(savedTranslate);
            return;
        }
        //get translate from api
        if (!networkManager.isConnected()) {
            onError.call(resources.getString(R.string.no_internet));
        } else {
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
                                       onError.call(resources.getString(R.string.wtf_error));
                                   }

                                   @Override
                                   public void onNext(TranslateModel.TranslateResponse translateResponse) {
                                       Log.d(TAG, "onNext() called with: " + "translateResponse = ["
                                               + translateResponse + "]");
                                       if ((translateResponse.getCode() == null) || !translateResponse.getCode().equals("200")) {
                                           onError.call(resources.getString(R.string.wtf_error));
                                           return;
                                       }
                                       if (!translateResponse.getTextList().isEmpty()) {
                                           translate.setOutput(translateResponse.getTextList().get(0));
                                       } else {
                                           translate.setOutput("");
                                       }
                                       cache(translate);
                                       onSuccess.call(historyModel.findTranslate(translate));
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

    /**
     * Save Translate object to to realm
     */
    public void cache(Translate translate) {
        realm.beginTransaction();
        translate.setTime((new Date()).getTime());
        translate.setFavTime(0);
        realm.copyToRealm(translate);
        realm.commitTransaction();
    }

    public class TranslateResponse {

        @SerializedName("code")
        String code;
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
