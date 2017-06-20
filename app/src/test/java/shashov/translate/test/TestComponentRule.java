package shashov.translate.test;

import android.support.annotation.NonNull;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import shashov.translate.TranslateApp;
import shashov.translate.di.AppComponent;

public class TestComponentRule implements TestRule {

    private AppComponent appComponent;

    public TestComponentRule() {
        appComponent = new TestComponent();
    }

    public TestComponentRule(@NonNull AppComponent component) {
        this.appComponent = component;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                TranslateApp.setAppComponent(appComponent);
                base.evaluate();
            }
        };
    }
}
