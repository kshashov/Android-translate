package shashov.translate.di.modules;

import android.app.Application;
import android.content.res.Resources;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import dagger.Module;
import dagger.Provides;
import ru.terrakok.cicerone.Cicerone;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.Router;

import javax.inject.Singleton;

@Module
public class AppModule {
    private Cicerone<Router> cicerone;
    Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
        cicerone = Cicerone.create(new Router());
    }

    @Provides
    @Singleton
    Router providesRouter() {
        return cicerone.getRouter();
    }

    @Provides
    @Singleton
    NavigatorHolder providesNavigatorHolder() {
        return cicerone.getNavigatorHolder();
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    Bus providesEventBus() {
        return new Bus(ThreadEnforcer.MAIN);
    }

    @Provides
    @Singleton
    Resources providesResources() {
        return this.mApplication.getResources();
    }
}
