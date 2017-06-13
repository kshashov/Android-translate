package shashov.translate.di;

import com.google.gson.Gson;
import dagger.Component;
import io.realm.Realm;
import shashov.translate.TranslateApp;
import shashov.translate.common.NetworkManager;
import shashov.translate.common.YandexAPI;
import shashov.translate.di.modules.AppModule;
import shashov.translate.di.modules.DataModule;
import shashov.translate.di.modules.NetworkModule;
import shashov.translate.di.modules.TranslateModule;
import shashov.translate.mvp.presenters.HistoryPresenter;
import shashov.translate.mvp.presenters.SplashPresenter;
import shashov.translate.mvp.presenters.TranslatePresenter;
import shashov.translate.ui.activities.MainActivity;
import shashov.translate.ui.fragments.HistoryFragment;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class, DataModule.class, TranslateModule.class})
public interface AppComponent {

    void inject(HistoryPresenter historyPresenter);
    void inject(TranslateApp translateApp);

    void inject(MainActivity mainActivity);

    void inject(HistoryFragment historyFragment);

    void inject(SplashPresenter splashPresenter);

    void inject(TranslatePresenter translatePresenter);

    Gson gson();

    Realm realm();

    YandexAPI yandexApi();

    NetworkManager networkManager();


}
