package shashov.translate.mvp.models;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.gson.annotations.SerializedName;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import shashov.translate.R;
import shashov.translate.common.YandexAPI;
import shashov.translate.dao.Translate;
import shashov.translate.di.modules.NetworkModule;

import java.util.List;

public class TranslateModel {
    private static final String TAG = TranslateModel.class.getSimpleName();
    private final YandexAPI yandexAPI;
    private final NetworkModule.NetworkManager networkManager;
    private Subscription subscription;
    private final HistoryModel historyModel;
    private final Resources resources;

    public TranslateModel(HistoryModel historyModel, Resources resources, YandexAPI yandexAPI, NetworkModule.NetworkManager networkManager) {
        this.yandexAPI = yandexAPI;
        this.networkManager = networkManager;
        this.historyModel = historyModel;
        this.resources = resources;
    }

    public Subscription translate(@NonNull final Translate translate, @NonNull final Action1<? super Translate> onSuccess, @NonNull final Action1<String> onError) {
        //search translate in realm
        Translate savedTranslate = historyModel.findTranslate(translate);
        if (savedTranslate != null) {
            historyModel.saveTranslate(savedTranslate);
            onSuccess.call(savedTranslate);
            return null;
        }
        //get translate from api
        if (!networkManager.isConnected()) {
            onError.call(resources.getString(R.string.no_internet));
        } else {
            subscription = yandexAPI
                    .translate(YandexAPI.API_CODE, translate.getFromLang() + "-" + translate.getToLang(), translate.getInput())
                    //.delay(10, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            (translateResponse) -> {
                                Log.d(TAG, "onNext() called with: " + "translateResponse = [" + translateResponse + "]");
                                if ((translateResponse.code == null) || !translateResponse.code.equals("200")) {
                                    onError.call(resources.getString(R.string.wtf_error));
                                    return;
                                }
                                if (!translateResponse.textList.isEmpty()) {
                                    translate.setOutput(translateResponse.textList.get(0));
                                } else {
                                    translate.setOutput("");
                                }

                                onSuccess.call(historyModel.saveTranslate(translate));
                            }, (throwable) -> {
                                Log.d(TAG, "onError() called with: " + translate.getFromLang() + "-" + translate.getToLang() + " e = [" + throwable + "]");
                                onError.call(resources.getString(R.string.wtf_error));
                            }
                    );
        }

        return subscription;
    }

    public static class TranslateResponse {

        @SerializedName("code")
        String code;
        @SerializedName("text")
        List<String> textList;
    }
}
