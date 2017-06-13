package shashov.translate.common;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import shashov.translate.mvp.models.LangsModel;
import shashov.translate.mvp.models.TranslateModel;

public interface YandexAPI {
    String API_CODE = "trnsl.1.1.20170323T143109Z.c8f429d90accb2d0.572caaaaaa40c3400aa6e4ec5d5ebc95364c7987";
    String API_URL = "https://translate.yandex.net/api/v1.5/tr.json/";

    @GET("getLangs")
    Observable<LangsModel.LangsResponse> getLangs(@Query("key") String apiKey, @Query("ui") String lang);

    @GET("translate")
    Observable<TranslateModel.TranslateResponse> translate(@Query("key") String apiKey, @Query("lang") String langDirection, @Query("text") String text);

}
