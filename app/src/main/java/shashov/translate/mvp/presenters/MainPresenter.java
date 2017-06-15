package shashov.translate.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import ru.terrakok.cicerone.Router;
import shashov.translate.TranslateApp;
import shashov.translate.mvp.views.MainView;

import javax.inject.Inject;

@InjectViewState
public class MainPresenter extends BasePresenter<MainView> {

    @Inject
    Router router;
    @Inject
    Bus eventBus;

    public MainPresenter() {
        TranslateApp.getAppComponent().inject(this);
        eventBus.register(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        router.newRootScreen(MainScreen.TRANSLATE_SCREEN.name());
    }

    @Subscribe
    public void onOpenTranslateEvent(OpenTranslateEvent event) {
        onClickTranslate();
    }

    public void onClickTranslate() {
        getViewState().showTranslate();
        router.backTo(null);
    }

    public void onClickHistory() {
        getViewState().showHistory();
        router.newScreenChain(MainScreen.HISTORY_SCREEN.name());
    }

    static class OpenTranslateEvent {
        OpenTranslateEvent() {
        }
    }

    public enum MainScreen {
        TRANSLATE_SCREEN,
        HISTORY_SCREEN
    }
}
