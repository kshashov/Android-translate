package shashov.translate.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.lb.auto_fit_textview.AutoResizeTextView;
import shashov.translate.R;
import shashov.translate.dao.Language;
import shashov.translate.dao.Translate;
import shashov.translate.mvp.presenters.TranslatePresenter;
import shashov.translate.mvp.views.TranslateView;
import shashov.translate.ui.adapters.LanguageSpinnerAdapter;

import java.util.List;

public class TranslateFragment extends MvpAppCompatFragment implements TranslateView, TextWatcher {
    public static final String TAG = "TranslateFragment";

    @BindView(R.id.ll_no_data)
    LinearLayout llNoTranslate;
    @BindView(R.id.ll_loading)
    LinearLayout llLoading;
    @BindView(R.id.ll_translated_actions)
    LinearLayout llTranslateActions;
    @BindView(R.id.rl_translate)
    RelativeLayout rlTranslate;
    @BindView(R.id.rl_translate_fullscreen)
    RelativeLayout rlTranslateFullScreen;
    @BindView(R.id.rl_translate_screen)
    RelativeLayout rlTranslateScreen;

    //translate
    @BindView(R.id.sp_lang_input)
    Spinner spInputLang;
    @BindView(R.id.sp_lang_output)
    Spinner spOutputLang;
    @BindView(R.id.tv_output)
    AutoResizeTextView tvOutputText;
    @BindView(R.id.et_input)
    EditText etInputText;
    @BindView(R.id.translate_favorite)
    MaterialFavoriteButton mfbFav;
    @BindView(R.id.tv_output_fullscreen)
    AutoResizeTextView tvOutputTextFullScreen;

    @InjectPresenter
    TranslatePresenter translatePresenter;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);
        unbinder = ButterKnife.bind(this, view);
        initInputField();
        tvOutputText.setMinTextSize(etInputText.getTextSize());
        tvOutputTextFullScreen.setMinTextSize(etInputText.getTextSize());
        return view;
    }

    @Override
    public void showLoading() {
        rlTranslateScreen.setVisibility(View.VISIBLE);
        llNoTranslate.setVisibility(View.INVISIBLE);
        rlTranslate.setVisibility(View.INVISIBLE);
        rlTranslateFullScreen.setVisibility(View.INVISIBLE);
        llLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoData() {
        rlTranslateScreen.setVisibility(View.VISIBLE);
        llNoTranslate.setVisibility(View.VISIBLE);
        rlTranslate.setVisibility(View.INVISIBLE);
        llLoading.setVisibility(View.INVISIBLE);
        rlTranslateFullScreen.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showTranslate(Translate translate) {
        rlTranslateScreen.setVisibility(View.VISIBLE);
        llNoTranslate.setVisibility(View.INVISIBLE);
        rlTranslate.setVisibility(View.VISIBLE);
        llLoading.setVisibility(View.INVISIBLE);
        rlTranslateFullScreen.setVisibility(View.INVISIBLE);

        setupTranslate(translate);
    }

    @Override
    public void showTranslateFullScreen(Translate translate) {
        rlTranslateScreen.setVisibility(View.INVISIBLE);
        rlTranslateFullScreen.setVisibility(View.VISIBLE);
        tvOutputTextFullScreen.setText(translate.getOutput());
    }

    @Override
    public void populateLangs(@NonNull List<Language> langs) {
        spInputLang.setAdapter(new LanguageSpinnerAdapter(getContext()));
        spOutputLang.setAdapter(new LanguageSpinnerAdapter(getContext()));
        ((LanguageSpinnerAdapter) spInputLang.getAdapter()).addItems(langs);
        ((LanguageSpinnerAdapter) spOutputLang.getAdapter()).addItems(langs);
    }

    @OnClick(R.id.translate_favorite)
    void onClickFav() {
        translatePresenter.onChangeFavorite();
        mfbFav.toggleFavorite();
    }

    @OnClick(R.id.btn_reload)
    void onClickReloadTranslate() {
        translatePresenter.onReloadTranslate();
    }

    @OnClick(R.id.ib_swap)
    void onClickSwapLangs() {
        if (spInputLang.getSelectedItemPosition() == spOutputLang.getSelectedItemPosition()) {
            return;
        }

        translatePresenter.onSwapLangs();
    }

    @OnClick(R.id.btn_fullscreen)
    void onOpenTranslateFullScreen() {
        translatePresenter.onOpenTranslateFullScreen();
    }

    @OnClick(R.id.btn_back_fullscreen)
    void onCloseTranslateFullScreen() {
        translatePresenter.onCloseTranslateFullScreen();
    }

    private void initInputField() {
        etInputText.setHorizontallyScrolling(false);
        etInputText.setMaxLines(4);
    }

    private void setupTranslate(@NonNull Translate translate) {
        tvOutputText.setText(translate.getOutput());
        mfbFav.setFavorite(translate.isFavorite(), false);
        llTranslateActions.setVisibility(translate.getOutput().isEmpty() ? View.INVISIBLE : View.VISIBLE);
        if (!translate.getInput().equals(etInputText.getText().toString().trim())) {
            etInputText.removeTextChangedListener(this);
            etInputText.setText(translate.getInput());
            etInputText.addTextChangedListener(this);
            Log.d(TAG, "input = [" + translate.getInput() + "," + etInputText.getText().toString().trim() + "]");
        }

        //langs
        setupLang(true, translate.getFromLang());
        setupLang(false, translate.getToLang());
    }

    private void setupLang(boolean isInput, @NonNull String langCode) {
        Spinner spinner = isInput ? spInputLang : spOutputLang;
        int langPosition = ((LanguageSpinnerAdapter) spinner.getAdapter()).getPosition(langCode);
        if (spinner.getSelectedItemPosition() == langPosition) {
            return;
        }
        spinner.setOnItemSelectedListener(null);
        spinner.setSelection(langPosition, false);
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                translatePresenter.onChangeLang(isInput, ((LanguageSpinnerAdapter) spinner.getAdapter()).getLanguage(i).getCode());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        translatePresenter.onChangeInput(s.toString().trim());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
