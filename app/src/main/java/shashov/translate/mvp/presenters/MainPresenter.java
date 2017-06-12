package shashov.translate.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import shashov.translate.mvp.views.MainView;

/**
 * Created by kirill on 11.06.17.
 */
@InjectViewState
public class MainPresenter extends BasePresenter<MainView> {

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().showTranslate();
    }


    public void onTranslate() {
        getViewState().showTranslate();
    }

    public void onHistory() {
        getViewState().showHistory();
    }
}
