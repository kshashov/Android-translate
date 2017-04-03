package shashov.translate.internals.di.modules;

import android.app.Application;
import dagger.Module;
import dagger.Provides;
import shashov.translate.eventbus.RxEventBus;
import shashov.translate.internals.mvp.presenters.PresenterCache;

import javax.inject.Singleton;

@Module
public class AppModule {

    Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    PresenterCache providePresenterCacheProvider() {
        return new PresenterCache();
    }

    @Provides
    @Singleton
    RxEventBus provideEventBus() {
        return new RxEventBus();
    }
}
