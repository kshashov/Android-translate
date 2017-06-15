package shashov.translate.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.android.SupportFragmentNavigator;
import shashov.translate.R;
import shashov.translate.TranslateApp;
import shashov.translate.mvp.presenters.MainPresenter;
import shashov.translate.mvp.views.MainView;
import shashov.translate.ui.fragments.HistoryFragment;
import shashov.translate.ui.fragments.TranslateFragment;

import static shashov.translate.mvp.presenters.MainPresenter.MainScreen;

public class MainActivity extends MvpAppCompatActivity implements MainView, BottomNavigationView.OnNavigationItemSelectedListener {
    @InjectPresenter(type = PresenterType.WEAK)
    MainPresenter mainPresenter;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bnv;

    private Navigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        bnv.setOnNavigationItemSelectedListener(this);

        navigator = new SupportFragmentNavigator(getSupportFragmentManager(), R.id.container_fragment) {
            @Override
            protected Fragment createFragment(String screenKey, Object data) {
                if (screenKey.equals(MainScreen.TRANSLATE_SCREEN.name())) {
                    return new TranslateFragment();
                } else if (screenKey.equals(MainScreen.HISTORY_SCREEN.name())) {
                    return new HistoryFragment();
                } else {
                    throw new RuntimeException("Unknown screen key!");
                }
            }

            @Override
            protected void showSystemMessage(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void exit() {
                finish();
            }
        };
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_translate:
                mainPresenter.onClickTranslate();
                break;
            case R.id.action_history:
                mainPresenter.onClickHistory();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container_fragment);
        if ((fragment == null) || (fragment instanceof TranslateFragment)) {
            finish();
        } else {
            mainPresenter.onClickTranslate();
        }
    }

    @Override
    public void showTranslate() {
        selectMenu(0);
    }

    @Override
    public void showHistory() {
        selectMenu(1);
    }

    private void selectMenu(int index) {
        bnv.getMenu().getItem(index).setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TranslateApp.getAppComponent().navigatorHolder().setNavigator(navigator);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TranslateApp.getAppComponent().navigatorHolder().removeNavigator();
    }
}
