package shashov.translate.di.modules;

import android.content.res.Resources;
import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import shashov.translate.common.NetworkManager;
import shashov.translate.common.YandexAPI;
import shashov.translate.mvp.models.HistoryModel;
import shashov.translate.mvp.models.LangsModel;
import shashov.translate.mvp.models.TranslateModel;

import javax.inject.Singleton;

@Module
public class TranslateModule {

    @Provides
    @Singleton
    LangsModel provideLangsModel(Resources resources, YandexAPI yandexAPI, NetworkManager networkManager) {
        return new LangsModel(resources, yandexAPI, networkManager);
    }

    @Provides
    @Singleton
    TranslateModel provideTranslateModel(Resources resources, HistoryModel historyModel, YandexAPI yandexAPI, Realm realm, NetworkManager networkManager) {
        return new TranslateModel(resources, historyModel, yandexAPI, realm, networkManager);
    }

    @Provides
    @Singleton
    HistoryModel provideHistoryModel(Realm realm) {
        return new HistoryModel(realm);
    }
}
