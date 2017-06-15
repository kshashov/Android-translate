package shashov.translate.di;

import dagger.Component;
import io.realm.Realm;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.Router;
import shashov.translate.TranslateApp;
import shashov.translate.common.YandexAPI;
import shashov.translate.di.modules.AppModule;
import shashov.translate.di.modules.DataModule;
import shashov.translate.di.modules.NetworkModule;
import shashov.translate.di.modules.TranslateModule;
import shashov.translate.mvp.presenters.HistoryPresenter;
import shashov.translate.mvp.presenters.MainPresenter;
import shashov.translate.mvp.presenters.SplashPresenter;
import shashov.translate.mvp.presenters.TranslatePresenter;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class, DataModule.class, TranslateModule.class})
public interface AppComponent {

    void inject(TranslateApp translateApp);

    void inject(MainPresenter mainPresenter);
    void inject(SplashPresenter splashPresenter);

    void inject(HistoryPresenter historyPresenter);
    void inject(TranslatePresenter translatePresenter);

    Realm realm();
    YandexAPI yandexApi();

    NetworkModule.NetworkManager networkManager();

    NavigatorHolder navigatorHolder();

    Router router();
}
