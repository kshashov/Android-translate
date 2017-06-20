package shashov.translate.mvp.presenters;

import com.squareup.otto.Bus;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;
import ru.terrakok.cicerone.Router;
import shashov.translate.RxTestUtils;
import shashov.translate.di.AppComponent;
import shashov.translate.mvp.views.MainView;
import shashov.translate.test.TestComponent;
import shashov.translate.test.TestComponentRule;

import static org.mockito.Mockito.verify;

@Config(manifest = Config.NONE)
public class MainPresenterTest {

    @Rule
    public TestComponentRule testComponentRule = new TestComponentRule(testAppComponent());
    @Mock
    MainView mainView;
    @Mock
    Bus eventBus;
    @Mock
    Router router;

    private MainPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        RxTestUtils.before();
        presenter = new MainPresenter();
        presenter.getAttachedViews().add(mainView);
    }

    @Test
    public void showTranslate() {
        presenter.attachView(mainView);
        presenter.onClickTranslate();
        verify(mainView).showTranslate();
    }

    @Test
    public void showHistory() {
        presenter.attachView(mainView);
        presenter.onClickHistory();
        verify(mainView).showHistory();
    }

    @After
    public void tearDown() throws Exception {
        RxTestUtils.after();
    }

    private AppComponent testAppComponent() {
        return new TestComponent() {

            @Override
            public void inject(MainPresenter presenter) {
                presenter.eventBus = eventBus;
                presenter.router = router;
            }
        };
    }
}
