package shashov.translate;

import android.app.Application;
import android.util.Log;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import shashov.translate.internals.di.components.AppComponent;
import shashov.translate.internals.di.components.DaggerAppComponent;
import shashov.translate.internals.di.modules.AppModule;
import shashov.translate.internals.di.modules.DataModule;
import shashov.translate.internals.di.modules.NetworkModule;
import shashov.translate.internals.di.modules.TranslateModule;
import shashov.translate.support.TranslateRealmMigration;

public class TranslateApp extends Application {

    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("INIT", "initInjector() called with: " + System.currentTimeMillis());
        initInjector();
        Log.d("INIT", "initRealmConfiguration() called with: " + System.currentTimeMillis());
        initRealmConfiguration();
        Log.d("INIT", "DONE: " + System.currentTimeMillis());
    }

    /**
     * Initialise the injector and create the app graph
     */
    private void initInjector() {
        appComponent = DaggerAppComponent.builder()
                .networkModule(new NetworkModule(this))
                .dataModule(new DataModule(this))
                .appModule(new AppModule(this))
                .translateModule(new TranslateModule())
                .build();
    }

    /**
     * Initialise the realm configuration
     */
    private void initRealmConfiguration() {
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .schemaVersion(12)
                .migration(new TranslateRealmMigration())
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }


    /**
     * @return the AppComponent instance
     */
    public static AppComponent getAppComponent() {
        return appComponent;
    }

}
