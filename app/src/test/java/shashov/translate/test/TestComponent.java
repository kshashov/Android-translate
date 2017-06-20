package shashov.translate.test;

import io.realm.Realm;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.Router;
import shashov.translate.TranslateApp;
import shashov.translate.common.YandexAPI;
import shashov.translate.di.AppComponent;
import shashov.translate.di.modules.NetworkModule;
import shashov.translate.mvp.presenters.HistoryPresenter;
import shashov.translate.mvp.presenters.MainPresenter;
import shashov.translate.mvp.presenters.SplashPresenter;
import shashov.translate.mvp.presenters.TranslatePresenter;

public class TestComponent implements AppComponent {

    @Override
    public Realm realm() {
        return null;
    }

    @Override
    public YandexAPI yandexApi() {
        return null;
    }

    @Override
    public NetworkModule.NetworkManager networkManager() {
        return null;
    }

    @Override
    public NavigatorHolder navigatorHolder() {
        return null;
    }

    @Override
    public Router router() {
        return null;
    }

    @Override
    public void inject(TranslateApp translateApp) {

    }

    @Override
    public void inject(MainPresenter mainPresenter) {

    }

    @Override
    public void inject(SplashPresenter splashPresenter) {

    }

    @Override
    public void inject(HistoryPresenter historyPresenter) {

    }

    @Override
    public void inject(TranslatePresenter translatePresenter) {

    }
}
