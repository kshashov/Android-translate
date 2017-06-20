package shashov.translate.models;

import android.content.res.Resources;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;
import rx.functions.Action1;
import shashov.translate.R;
import shashov.translate.RxTestUtils;
import shashov.translate.common.YandexAPI;
import shashov.translate.dao.Language;
import shashov.translate.di.modules.NetworkModule;
import shashov.translate.mvp.models.LangsModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class LangsModelTest {

    private LangsModel langsModel;
    @Mock
    private Resources resources;
    @Mock
    private YandexAPI yandexAPI;
    @Mock
    private NetworkModule.NetworkManager networkManager;
    @Mock
    private Action1<List<Language>> onSuccess;
    @Mock
    private Action1<String> onError;
    @Captor
    private ArgumentCaptor<String> errorArg;
    @Captor
    private ArgumentCaptor<List<Language>> successArg;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        //mock resources
        when(resources.getString(R.string.lang_code)).thenReturn("en");
        when(resources.getString(R.string.wtf_error)).thenReturn("wtf");
        when(resources.getString(R.string.no_internet)).thenReturn("net");

        //mock api
        Map<String, String> map = new HashMap<>();
        map.put("ru", "ru");
        LangsModel.LangsResponse response = new LangsModel.LangsResponse(map);
        when(yandexAPI.getLangs(any(), any())).thenReturn(Observable.just(response));

        RxTestUtils.before();
    }

    @Test
    public void loadLangsWithoutInternet() {
        langsModel = new LangsModel(resources, yandexAPI, networkManager);

        when(networkManager.isConnected()).thenReturn(false);
        langsModel.getLangs(onSuccess, onError);

        //catch error when loading langs
        verify(onError).call(errorArg.capture());
        assertEquals("net", errorArg.getValue());
    }

    @Test
    public void loadLangsFromAPI() {
        langsModel = new LangsModel(resources, yandexAPI, networkManager);

        //save to cache
        when(networkManager.isConnected()).thenReturn(true);
        langsModel.getLangs(onSuccess, onError);

        //success loading langs
        verify(onSuccess).call(successArg.capture());
        assertTrue(successArg.getValue().size() == 1);
    }

    @Test
    public void loadLangsWithCache() {
        langsModel = new LangsModel(resources, yandexAPI, networkManager);

        //save to cache
        when(networkManager.isConnected()).thenReturn(true);
        langsModel.getLangs(onSuccess, onError);

        //load from cache
        when(networkManager.isConnected()).thenReturn(false);
        langsModel.getLangs(onSuccess, onError);

        //success loading langs
        verify(onSuccess, times(2)).call(successArg.capture());
        assertTrue(successArg.getValue().size() == 1);
    }

    @After
    public void tearDown() throws Exception {
        RxTestUtils.after();
    }
}
