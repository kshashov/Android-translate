package shashov.translate.internals.di.modules;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import shashov.translate.internals.mvp.models.LanguageModel;
import shashov.translate.internals.mvp.models.TranslateModel;
import shashov.translate.networking.YandexAPI;
import shashov.translate.support.NetworkManager;

import javax.inject.Singleton;

@Module
public class TranslateModule {

    @Provides
    @Singleton
    LanguageModel provideLanguageModel(YandexAPI yandexAPI, NetworkManager networkManager) {
        return new LanguageModel(yandexAPI, networkManager);
    }

    @Provides
    @Singleton
    TranslateModel provideTranslateModel(YandexAPI yandexAPI, Realm realm, NetworkManager networkManager) {
        return new TranslateModel(yandexAPI, realm, networkManager);
    }
}
