package shashov.translate.mvp.presenters;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;
import rx.functions.Action1;
import shashov.translate.RxTestUtils;
import shashov.translate.dao.Language;
import shashov.translate.di.AppComponent;
import shashov.translate.mvp.models.LangsModel;
import shashov.translate.mvp.views.SplashView;
import shashov.translate.test.TestComponent;
import shashov.translate.test.TestComponentRule;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@Config(manifest = Config.NONE)
public class SplashPresenterTest {

    @Rule
    public TestComponentRule testComponentRule = new TestComponentRule(testAppComponent());
    @Mock
    SplashView splashView;
    @Mock
    LangsModel langsModel;

    private SplashPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        RxTestUtils.before();
        presenter = new SplashPresenter();
        presenter.getAttachedViews().add(splashView);

    }

    @Test
    public void loadingOnAttach() {
        presenter.attachView(splashView);
        verify(splashView).showLoading();
    }

    @Test
    public void showAppWhenLoaded() {
        //mock model
        List<Language> langs = new ArrayList<>();
        langs.add(new Language("en", "en"));
        when(langsModel.getLangs(any(Action1.class), any(Action1.class))).
                thenAnswer(invocation -> {
                    ((Action1<List<Language>>) invocation.getArgument(0)).call(langs);
                    return null;
                });

        presenter.attachView(splashView);
        verify(splashView).showApp();
    }

    @Test
    public void showNoDataWhenError() {
        //mock model
        when(langsModel.getLangs(any(Action1.class), any(Action1.class))).
                thenAnswer(invocation -> {
                    ((Action1<String>) invocation.getArgument(1)).call("error");
                    return null;
                });

        presenter.attachView(splashView);
        verify(splashView).showNoData();
    }

    @Test
    public void showNLoadingWhenReload() {
        //mock model
        when(langsModel.getLangs(any(Action1.class), any(Action1.class))).
                thenAnswer(invocation -> {
                    ((Action1<String>) invocation.getArgument(1)).call("error");
                    return null;
                });

        presenter.attachView(splashView);
        verify(splashView).showNoData();

        presenter.loadData();
        verify(splashView, times(2)).showLoading();
    }

    @After
    public void tearDown() throws Exception {
        RxTestUtils.after();
    }


    private AppComponent testAppComponent() {
        return new TestComponent() {

            @Override
            public void inject(SplashPresenter presenter) {
                presenter.langsModel = langsModel;
            }
        };
    }

}
