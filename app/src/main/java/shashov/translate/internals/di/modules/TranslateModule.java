package shashov.translate.internals.di.modules;

import android.content.res.Resources;
import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import shashov.translate.internals.mvp.models.HistoryModel;
import shashov.translate.internals.mvp.models.LanguageModel;
import shashov.translate.internals.mvp.models.TranslateModel;
import shashov.translate.networking.YandexAPI;
import shashov.translate.support.NetworkManager;

import javax.inject.Singleton;

@Module
public class TranslateModule {

    @Provides
    @Singleton
    LanguageModel provideLanguageModel(Resources resources, YandexAPI yandexAPI, NetworkManager networkManager) {
        return new LanguageModel(resources, yandexAPI, networkManager);
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
