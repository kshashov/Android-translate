package shashov.translate.di.modules;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import shashov.translate.common.NetworkManager;
import shashov.translate.common.YandexAPI;

import javax.inject.Singleton;

@Module
public class NetworkModule {

    private Context context;

    public NetworkModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public NetworkManager networkManager() {
        return new NetworkManager(context);
    }

    @Provides
    @Singleton
    public YandexAPI provideHnbAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(YandexAPI.API_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(YandexAPI.class);
    }
}
