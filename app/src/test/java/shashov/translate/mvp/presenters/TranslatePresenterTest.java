package shashov.translate.mvp.presenters;

import android.content.res.Resources;
import com.squareup.otto.Bus;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;
import rx.functions.Action1;
import shashov.translate.R;
import shashov.translate.RxTestUtils;
import shashov.translate.dao.Translate;
import shashov.translate.di.AppComponent;
import shashov.translate.mvp.models.HistoryModel;
import shashov.translate.mvp.models.LangsModel;
import shashov.translate.mvp.models.TranslateModel;
import shashov.translate.mvp.models.TranslateModelTest;
import shashov.translate.mvp.views.TranslateView;
import shashov.translate.test.TestComponent;
import shashov.translate.test.TestComponentRule;

import static org.mockito.Mockito.*;

@Config(manifest = Config.NONE)
public class TranslatePresenterTest {

    @Rule
    public TestComponentRule testComponentRule = new TestComponentRule(testAppComponent());
    @Mock
    TranslateView translateView;
    @Mock
    Bus eventBus;
    @Mock
    TranslatePresenter presenter;
    @Mock
    Resources resources;
    @Mock
    LangsModel langsModel;
    @Mock
    HistoryModel historyModel;
    @Mock
    TranslateModel translateModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        RxTestUtils.before();
        when(historyModel.getLast()).thenReturn(TranslateModelTest.getTranslate());
        when(historyModel.findTranslate(any(Translate.class))).then(AdditionalAnswers.returnsFirstArg());
        when(resources.getString(R.string.lang_code)).thenReturn("en");
        presenter = new TranslatePresenter();
        presenter.getAttachedViews().add(translateView);
    }

    @Test
    public void showTranslateWhenFirstAttach() {
        when(translateModel.translate(any(Translate.class), any(Action1.class), any(Action1.class))).
                thenAnswer(invocation -> {
                    ((Action1<Translate>) invocation.getArgument(1)).call(TranslateModelTest.getTranslate());
                    return null;
                });

        presenter.onFirstViewAttach();
        verify(translateView).showLoading();
        verify(translateView).showTranslate(any());
    }

    @Test
    public void showNoDataWhenError() {
        //mock invalid model
        when(translateModel.translate(any(Translate.class), any(Action1.class), any(Action1.class))).
                thenAnswer(invocation -> {
                    ((Action1<String>) invocation.getArgument(2)).call("");
                    return null;
                });

        presenter.attachView(translateView);
        verify(translateView, times(2)).showLoading();
        verify(translateView, times(2)).showNoData();
    }

    @Test
    public void showFullScreen() {
        presenter.onOpenTranslateFullScreen();
        verify(translateView).showTranslateFullScreen(any(Translate.class));
    }

    @Test
    public void closeFullScreen() {
        presenter.onCloseTranslateFullScreen();
        verify(translateView).showTranslate(any(Translate.class));
    }

    @Test
    public void showTranslateWhenDataChanged() {
        when(translateModel.translate(any(), any(Action1.class), any(Action1.class))).
                thenAnswer(invocation -> {
                    ((Action1<Translate>) invocation.getArgument(1)).call(TranslateModelTest.getTranslate());
                    return null;
                });

        presenter.onChangeLang(true, "en");
        presenter.onSwapLangs();
        presenter.onReloadTranslate();

        verify(translateView, times(3)).showLoading();
        verify(translateView, times(3)).showTranslate(any());
    }

    @After
    public void tearDown() throws Exception {
        RxTestUtils.after();
    }

    private AppComponent testAppComponent() {
        return new TestComponent() {

            @Override
            public void inject(TranslatePresenter presenter) {
                presenter.eventBus = eventBus;
                presenter.resources = resources;
                presenter.langsModel = langsModel;
                presenter.historyModel = historyModel;
                presenter.translateModel = translateModel;
            }
        };
    }
}

