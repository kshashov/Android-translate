package shashov.translate.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import shashov.translate.TranslateApp;
import shashov.translate.internals.di.components.AppComponent;
import shashov.translate.internals.mvp.MVP;
import shashov.translate.internals.mvp.presenters.PresenterCache;

import javax.inject.Inject;

/**
 * Created by envoy on 30.03.2017.
 */
public abstract class BaseFragment<T extends MVP.Presenter> extends Fragment implements MVP.View {
    @Inject
    PresenterCache presenterCache;

    private T presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupComponent(TranslateApp.getAppComponent());
        restoreOrCreatePresenter();
        getPresenter().onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        presenterCache.putPresenter(getClass().getName(), presenter); //TODO ?
        presenter.setView(this);
        getPresenter().onStart();
    }

    private void restoreOrCreatePresenter() {
        // try to get a cached presenter
        presenter = presenterCache.getPresenter(getClass().getName());
        if (presenter == null) {
            // no cached one found, create a new one
            presenter = createPresenter();
            presenterCache.putPresenter(getClass().getName(), presenter);
        }
        getPresenter().setView(this);
    }

    @Override
    public void onStop() {
        if (!getActivity().isChangingConfigurations()) {
            // activity is stopped normally, remove the cached presenter so it's not cached
            // presenterCache.removePresenter(presenter); TODO ?
        }
        // onStop will clear view reference
        presenter.onStop(getActivity().isChangingConfigurations());
        super.onStop();
    }

    public T getPresenter() {
        return presenter;
    }

    protected abstract void setupComponent(AppComponent appComponent);

    protected abstract T createPresenter();
}
