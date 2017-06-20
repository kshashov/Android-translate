package shashov.translate.mvp.models;

import android.content.res.Resources;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import rx.Observable;
import rx.functions.Action1;
import shashov.translate.R;
import shashov.translate.RxTestUtils;
import shashov.translate.common.YandexAPI;
import shashov.translate.dao.Translate;
import shashov.translate.di.modules.NetworkModule;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TranslateModelTest {
    private TranslateModel translateModel;
    @Mock
    private Resources resources;
    @Mock
    private YandexAPI yandexAPI;
    @Mock
    private NetworkModule.NetworkManager networkManager;
    @Mock
    private HistoryModel historyModel;
    @Mock
    private Action1<Translate> onSuccess;
    @Mock
    private Action1<String> onError;
    @Captor
    private ArgumentCaptor<String> errorArg;
    @Captor
    private ArgumentCaptor<Translate> successArg;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        //mock history
        when(historyModel.saveTranslate(any())).then(AdditionalAnswers.returnsFirstArg());

        //mock resources
        when(resources.getString(R.string.wtf_error)).thenReturn("wtf");
        when(resources.getString(R.string.no_internet)).thenReturn("net");

        //mock api
        List<String> output = new ArrayList<>();
        output.add("4");
        TranslateModel.TranslateResponse response = new TranslateModel.TranslateResponse("200", output);
        when(yandexAPI.translate(any(), any(), any())).thenReturn(Observable.just(response));

        RxTestUtils.before();
    }

    @Test
    public void loadTranslateWithoutInternet() {
        translateModel = new TranslateModel(historyModel, resources, yandexAPI, networkManager);

        //disable cache and internet
        when(historyModel.findTranslate(any())).thenReturn(null);
        when(networkManager.isConnected()).thenReturn(false);
        translateModel.translate(new Translate(), onSuccess, onError);

        //catch error when loading langs
        verify(onError).call(errorArg.capture());
        assertEquals("net", errorArg.getValue());
    }

    @Test
    public void loadTranslateFromCache() {
        translateModel = new TranslateModel(historyModel, resources, yandexAPI, networkManager);

        //enable cache
        Translate translate = getTranslate();
        translate.setOutput("5");
        when(historyModel.findTranslate(any())).thenReturn(translate);

        //disable internet
        when(networkManager.isConnected()).thenReturn(false);
        translateModel.translate(getTranslate(), onSuccess, onError);

        //enable internet
        when(networkManager.isConnected()).thenReturn(true);
        translateModel.translate(getTranslate(), onSuccess, onError);

        verify(onSuccess, times(2)).call(successArg.capture());
        assertEquals("5", successArg.getValue().getOutput());
    }

    @Test
    public void loadTranslateFromAPI() {
        translateModel = new TranslateModel(historyModel, resources, yandexAPI, networkManager);

        //disable cache and enable internet
        when(historyModel.findTranslate(any())).thenReturn(null);
        when(networkManager.isConnected()).thenReturn(true);
        translateModel.translate(getTranslate(), onSuccess, onError);

        verify(onSuccess).call(successArg.capture());
        assertEquals("4", successArg.getValue().getOutput());

    }

    @Test
    public void loadTranslateFromAPIError() {
        translateModel = new TranslateModel(historyModel, resources, yandexAPI, networkManager);

        //return error from api
        when(yandexAPI.translate(any(), any(), any())).thenReturn(Observable.just(new TranslateModel.TranslateResponse("403", null)));

        //disable cache and enable internet
        when(historyModel.findTranslate(any())).thenReturn(null);
        when(networkManager.isConnected()).thenReturn(true);
        translateModel.translate(getTranslate(), onSuccess, onError);

        verify(onError).call(errorArg.capture());
        assertEquals("wtf", errorArg.getValue());

    }

    public static Translate getTranslate() {
        Translate translate = new Translate();
        translate.setInput("1");
        translate.setToLang("2");
        translate.setFromLang("3");
        return translate;
    }

    @After
    public void tearDown() throws Exception {
        RxTestUtils.after();
    }
}
