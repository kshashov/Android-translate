package shashov.translate.test;

import android.support.annotation.NonNull;
import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Method;

public class TranslateAppTestRunner extends RobolectricTestRunner {

    private static final int SDK_EMULATE_LEVEL = 23;

    public TranslateAppTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    public Config getConfig(@NonNull Method method) {
        final Config defaultConfig = super.getConfig(method);
        return defaultConfig;
    }
}
