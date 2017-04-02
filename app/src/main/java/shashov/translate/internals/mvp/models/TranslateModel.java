package shashov.translate.internals.mvp.models;

import android.util.Log;
import com.google.gson.annotations.SerializedName;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import shashov.translate.internals.mvp.MVP;
import shashov.translate.networking.YandexAPI;
import shashov.translate.realm.Translate;
import shashov.translate.support.NetworkManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by envoy on 25.03.2017.
 */
public class TranslateModel implements MVP.Model {

    private static final String TAG = TranslateModel.class.getSimpleName();
    private YandexAPI yandexAPI;
    private NetworkManager networkManager;
    private Subscription observable;
    private final Realm realm;

    public TranslateModel(YandexAPI yandexAPI, Realm realm, NetworkManager networkManager) {
        this.yandexAPI = yandexAPI;
        this.realm = realm;
        this.networkManager = networkManager;
    }

    public void translate(final Translate translate, final OnDataLoaded<Translate> onDataLoaded) {
        if (!networkManager.isConnected()) {
            onDataLoaded.onFail("No cached data and not Internet");
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
                                       onDataLoaded.onFail(e.toString());
                                   }

                                   @Override
                                   public void onNext(TranslateModel.TranslateResponse translateResponse) {
                                       Log.d(TAG, "onNext() called with: " + "translateResponse = ["
                                               + translateResponse + "]");

                                       Translate data = translate;
                                       if (!translateResponse.getTextList().isEmpty()) {
                                           translate.setOutput(translateResponse.getTextList().get(0));
                                       }

                                       List<Translate> saved = new ArrayList();
                                       saved.add(data);
                                       cache(saved, new Date());
                                       onDataLoaded.onSuccess(data);
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

    @Override
    public void cache(List<?> data, Date date) {
        //TODO save to realm
        List<Translate> translates = new ArrayList<>((Collection<? extends Translate>) data);
        realm.beginTransaction();
        for (Translate translate : translates) {
            translate.setTime(date.getTime());
            realm.copyToRealm(translate);
        }
        realm.commitTransaction();
    }

    @Override
    public void clearCache() {

    }

    public Translate getLastTranslate() {
        RealmResults<Translate> result = realm.where(Translate.class).findAllSorted("time", Sort.DESCENDING);
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public void getAll(final OnDataLoaded<List<Translate>> onDataLoaded) {
        RealmResults<Translate> result = realm.where(Translate.class).findAllSorted("time", Sort.DESCENDING);
        onDataLoaded.onSuccess(realm.copyFromRealm(result));

    }

    public class TranslateResponse {
        //TODO check code response
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
