package shashov.translate.internals.mvp.models;

import android.content.res.Resources;
import android.util.Log;
import com.google.gson.annotations.SerializedName;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import shashov.translate.R;
import shashov.translate.dao.Language;
import shashov.translate.internals.mvp.MVP;
import shashov.translate.networking.YandexAPI;
import shashov.translate.support.NetworkManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LanguageModel implements MVP.Model {
    private static final String TAG = LanguageModel.class.getSimpleName();
    private final Resources resources;
    private Subscription observable;
    private final YandexAPI yandexAPI;
    private List<Language> langsCached = new ArrayList<>();
    private final NetworkManager networkManager;

    public LanguageModel(Resources resources, YandexAPI yandexAPI, NetworkManager networkManager) {
        this.yandexAPI = yandexAPI;
        this.networkManager = networkManager;
        this.resources = resources;
    }

    public void getLangs(String lang, final OnDataLoaded<List<Language>> onDataLoaded) {
        langsCached = checkIfCached();
        if (!langsCached.isEmpty()) {
            onDataLoaded.onSuccess(langsCached);
        } else if (!networkManager.isConnected()) {
            onDataLoaded.onFail(resources.getString(R.string.no_internet));
        } else {
            observable = yandexAPI.getLangs(YandexAPI.API_CODE, lang)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<LangsResponse>() {
                                   @Override
                                   public void onCompleted() {
                                   }

                                   @Override
                                   public void onError(Throwable e) {
                                       Log.d(TAG, "onError() called with: " + "e = [" + e + "]");
                                       langsCached = checkIfCached();
                                       if (langsCached.isEmpty()) {
                                           onDataLoaded.onFail(resources.getString(R.string.wtf_error));
                                       } else {
                                           onDataLoaded.onSuccess(langsCached);
                                       }
                                   }

                                   @Override
                                   public void onNext(LangsResponse langsResponse) {
                                       Log.d(TAG, "onNext() called with: " + "langs = ["
                                               + langsResponse + "]");
                                       List result = new ArrayList();
                                       if ((langsResponse.getLangs() == null) || langsResponse.getLangs().isEmpty()) {
                                           onDataLoaded.onFail(resources.getString(R.string.wtf_error));
                                           return;
                                       }
                                       for (Map.Entry<String, String> entry : langsResponse.getLangs().entrySet()) {
                                           Language language = new Language();
                                           language.setCode(entry.getKey());
                                           language.setTitle(entry.getValue());
                                           result.add(language);
                                       }

                                       cache(result);
                                       onDataLoaded.onSuccess(langsCached);
                                   }
                               }
                    );

        }
    }


    private List<Language> checkIfCached() {
        if (langsCached != null) {
            return langsCached;
        }

        return new ArrayList<>();
    }

    public void cache(List<Language> data) {
        langsCached = data;
    }

    public void unsubscribe() {
        if (observable != null) {
            observable.unsubscribe();
        }
    }

    public class LangsResponse {

        @SerializedName("langs")
        Map<String, String> langsMap;

        public Map<String, String> getLangs() {
            return langsMap;
        }

        public void setLangs(Map<String, String> mapLangs) {
            this.langsMap = mapLangs;
        }
    }
}
