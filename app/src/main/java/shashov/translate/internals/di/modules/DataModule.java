package shashov.translate.internals.di.modules;

import android.app.Application;
import com.google.gson.Gson;
import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

import javax.inject.Singleton;

@Module
public class DataModule {

    private Application application;

    public DataModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    Realm provideRealm() {
        return Realm.getDefaultInstance();
    }

}
