package shashov.translate.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import shashov.translate.R;
import shashov.translate.TranslateApp;
import shashov.translate.eventbus.RxEventBus;
import shashov.translate.eventbus.events.OpenTranslateEvent;
import shashov.translate.ui.fragments.BaseFragment;
import shashov.translate.ui.fragments.HistoryFragment;
import shashov.translate.ui.fragments.TranslateFragment;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Inject
    RxEventBus eventBus;

    @BindView(R.id.container_fragment)
    FrameLayout flFragmentContainer;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bnv;

    private FragmentManager fm;
    private MainSubscriber mainBusSubscriber = new MainSubscriber();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        TranslateApp.getAppComponent().inject(this);
        fm = getSupportFragmentManager();
        bnv.setOnNavigationItemSelectedListener(this);

        if (fm.findFragmentById(R.id.container_fragment) == null) {
            openFragment(null);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        BaseFragment fragment = null;
        switch (item.getItemId()) {
            case R.id.action_translate:
                fragment = TranslateFragment.newInstance(null);
                break;

            case R.id.action_history:
                fragment = new HistoryFragment();
                break;
        }

        openFragment(fragment);
        return true;
    }

    private void openFragment(BaseFragment fragment) {
        if (fragment == null) {
            fragment = getDefaultFragment();
        }

        final FragmentTransaction transaction = fm.beginTransaction();
        String tag = "";
        if (fragment instanceof TranslateFragment) {
            tag = TranslateFragment.TAG;
            bnv.getMenu().getItem(0).setChecked(true);
        } else if (fragment instanceof HistoryFragment) {
            tag = HistoryFragment.TAG;
            bnv.getMenu().getItem(1).setChecked(true);
        }

        if (fm.findFragmentById(R.id.container_fragment) != null) {
            transaction.replace(R.id.container_fragment, fragment, tag);
        } else {
            transaction.add(R.id.container_fragment, fragment, tag);
        }
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (fm.findFragmentById(R.id.container_fragment) instanceof TranslateFragment) {
            finish();
        }
        if (fm.findFragmentByTag(TranslateFragment.TAG) != null) {
            openFragment((BaseFragment) fm.findFragmentByTag(TranslateFragment.TAG));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventBus.getBusObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mainBusSubscriber);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainBusSubscriber.unsubscribe();
    }

    private BaseFragment getDefaultFragment() {
        return TranslateFragment.newInstance(null);
    }

    private class MainSubscriber extends Subscriber<Object> {

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onNext(Object o) {
            if (o instanceof OpenTranslateEvent) {
                OpenTranslateEvent event = (OpenTranslateEvent) o;
                Fragment translateFragment = fm.findFragmentByTag(TranslateFragment.TAG);
                if (translateFragment == null || !(translateFragment instanceof TranslateFragment)) {
                    translateFragment = TranslateFragment.newInstance(event.getTranslate());
                } else {
                    ((TranslateFragment) translateFragment).setTranslate(((OpenTranslateEvent) o).getTranslate());
                }
                openFragment((BaseFragment) translateFragment);

            }
        }
    }
}
