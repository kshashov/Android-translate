package shashov.translate.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.github.ybq.android.spinkit.SpinKitView;
import shashov.translate.R;
import shashov.translate.mvp.presenters.SplashPresenter;
import shashov.translate.mvp.views.SplashView;

/**
 * Created by kirill on 10.06.17.
 */
public class SplashActivity extends MvpAppCompatActivity implements SplashView {
    public static final String LANGS_KEY = "langs";
    @BindView(R.id.progress)
    SpinKitView progress;
    @BindView(R.id.ll_loading)
    LinearLayout llLoading;
    @BindView(R.id.ll_no_data)
    LinearLayout llNoData;

    @InjectPresenter
    SplashPresenter splashPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
    }

    @Override
    public void showLoading() {
        progress.getIndeterminateDrawable().start();
        llNoData.setVisibility(View.INVISIBLE);
        llLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoData() {
        llNoData.setVisibility(View.VISIBLE);
        llLoading.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.btn_reload)
    public void onClickReload() {
        splashPresenter.loadData();
    }

    @Override
    public void showApp() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @OnClick(R.id.btn_reload)
    public void onReload() {
        splashPresenter.loadData();
    }
}