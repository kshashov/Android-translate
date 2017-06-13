package shashov.translate.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import shashov.translate.R;
import shashov.translate.TranslateApp;
import shashov.translate.common.events.OpenTranslateEvent;
import shashov.translate.mvp.presenters.MainPresenter;
import shashov.translate.mvp.views.MainView;
import shashov.translate.ui.fragments.HistoryFragment;
import shashov.translate.ui.fragments.TranslateFragment;

import javax.inject.Inject;

public class MainActivity extends MvpAppCompatActivity implements MainView, BottomNavigationView.OnNavigationItemSelectedListener {

    @Inject
    Bus eventBus;
    @InjectPresenter
    MainPresenter mainPresenter;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bnv;

    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        TranslateApp.getAppComponent().inject(this);
        eventBus.register(this);

        fm = getSupportFragmentManager();
        bnv.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_translate:
                mainPresenter.onTranslate();
                break;
            case R.id.action_history:
                mainPresenter.onHistory();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (fm.findFragmentById(R.id.container_fragment) instanceof TranslateFragment) {
            finish();
        } else {
            showTranslate(); //todo go back
        }
    }

    @Override
    public void showTranslate() {
        String tag = TranslateFragment.TAG;
        selectMenu(0);
        Fragment fragment = fm.findFragmentByTag(tag);
        showFragment(fragment == null ? new TranslateFragment() : fragment, tag);
    }

    @Override
    public void showHistory() {
        String tag = HistoryFragment.TAG;
        selectMenu(1);
        Fragment fragment = fm.findFragmentByTag(tag);
        showFragment(fragment == null ? new HistoryFragment() : fragment, tag);
    }

    private void selectMenu(int index) {
        bnv.getMenu().getItem(index).setChecked(true);
    }

    private void showFragment(@NonNull Fragment fragment, String tag) {
        fm.beginTransaction()
                .replace(R.id.container_fragment, fragment == null ? new HistoryFragment() : fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    @Subscribe
    public void onOpenTranslate(OpenTranslateEvent event) {
        //TODO set translate to translatepresenter
        showTranslate();
    }
}
