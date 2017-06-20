package shashov.translate.mvp.presenters;

import com.squareup.otto.Bus;
import io.realm.OrderedRealmCollection;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;
import ru.terrakok.cicerone.Router;
import shashov.translate.RxTestUtils;
import shashov.translate.dao.Translate;
import shashov.translate.di.AppComponent;
import shashov.translate.mvp.models.HistoryModel;
import shashov.translate.mvp.views.HistoryView;
import shashov.translate.test.TestComponent;
import shashov.translate.test.TestComponentRule;

import static org.mockito.Mockito.*;

@Config(manifest = Config.NONE)
public class HistoryPresenterTest {

    @Rule
    public TestComponentRule testComponentRule = new TestComponentRule(testAppComponent());
    @Mock
    HistoryView historyView;
    @Mock
    Bus eventBus;
    @Mock
    Router router;
    @Mock
    HistoryModel historyModel;
    @Mock
    OrderedRealmCollection<Translate> all;
    @Mock
    OrderedRealmCollection<Translate> favs;
    private HistoryPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        RxTestUtils.before();
        doAnswer(invocation -> {
            ((HistoryModel.HistoryDataAction) invocation.getArgument(0)).onSuccess(all, favs);
            return null;
        }).when(historyModel).getHistory(any(HistoryModel.HistoryDataAction.class));
        presenter = new HistoryPresenter();
        presenter.getAttachedViews().add(historyView);
    }

    @Test
    public void showDataWhenLoading() {
        presenter.attachView(historyView);
        verify(historyView).showContent(anyBoolean(), any());
        verify(historyView).search(anyString());
        verify(historyView).setRVState(any());
    }

    @Test
    public void searchWhenTextChanged() {
        presenter.onChangeSearchText("text");
        verify(historyView).search(contains("text"));
    }

    @Test
    public void showDataWhenTabChanged() {
        presenter.onChangeTab(true);
        verify(historyView).showContent(anyBoolean(), any());
        verify(historyView).search(anyString());
    }

    @Test
    public void showDialogWhenDeleted() {
        presenter.onDeleteHistory();
        verify(historyView).showDeleteDialog();
    }

    @Test
    public void closeDialogWhenClose() {
        presenter.onNegativeCloseDeleteDialog();
        presenter.onPositiveCloseDeleteDialog(true);
        presenter.onPositiveCloseDeleteDialog(false);
        verify(historyView, times(3)).closeDeleteDialog();
    }

    @After
    public void tearDown() throws Exception {
        RxTestUtils.after();
    }

    private AppComponent testAppComponent() {
        return new TestComponent() {

            @Override
            public void inject(HistoryPresenter presenter) {
                presenter.eventBus = eventBus;
                presenter.historyModel = historyModel;
            }
        };
    }
}

