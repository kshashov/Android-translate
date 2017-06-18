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
import shashov.translate.dao.Language;
import shashov.translate.di.modules.NetworkModule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LangsModel {
    private static final String TAG = LangsModel.class.getSimpleName();
    private final Resources resources;
    private final YandexAPI yandexAPI;
    private List<Language> langsCached = new ArrayList<>();
    private final NetworkModule.NetworkManager networkManager;

    public LangsModel(Resources resources, YandexAPI yandexAPI, NetworkModule.NetworkManager networkManager) {
        this.yandexAPI = yandexAPI;
        this.networkManager = networkManager;
        this.resources = resources;
    }

    public Subscription getLangs(@NonNull final Action1<? super List<Language>> onSuccess, @NonNull final Action1<String> onError) {
        //cannot load
        if (langsCached.isEmpty() && !networkManager.isConnected()) {
            onError.call(resources.getString(R.string.no_internet));
            return null;
        }

        //already loaded
        if (!langsCached.isEmpty()) {
            onSuccess.call(langsCached);
            return null;
        }

        //loading
        Subscription subscription = yandexAPI.getLangs(YandexAPI.API_CODE, resources.getString(R.string.lang_code))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        data -> {
                            Log.d(TAG, "onNext() called with: " + "langs = [" + data + "]");

                            if ((data.langsMap == null) || data.langsMap.isEmpty()) {
                                onError.call(resources.getString(R.string.wtf_error));
                                return;
                            }

                            List result = new ArrayList<Language>();
                            for (Map.Entry<String, String> entry : data.langsMap.entrySet()) {
                                result.add(new Language(entry.getKey(), entry.getValue()));
                            }
                            Collections.sort(result, (Language left, Language right) -> left.getTitle().compareToIgnoreCase(right.getTitle()));
                            langsCached.clear();
                            langsCached.addAll(result);
                            onSuccess.call(langsCached);
                        }, error -> {
                            Log.d(TAG, "onError() called with: " + "e = [" + error + "]");
                            onError.call(resources.getString(R.string.wtf_error));
                        }
                );

        return subscription;
    }

    public class LangsResponse {

        @SerializedName("langs")
        public Map<String, String> langsMap;
    }

}
