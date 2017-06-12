package shashov.translate.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
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

/**
 * Created by kirill on 11.06.17.
 */
public class TranslateFragment extends MvpAppCompatFragment implements TranslateView {
    public static final String TAG = "TranslateFragment";

    @BindView(R.id.pb_output)
    ProgressBar pbOutput;
    @BindView(R.id.ll_no_translate)
    LinearLayout llNoTranslate;

    //translate
    @BindView(R.id.sp_lang_input)
    Spinner spInputLang;
    @BindView(R.id.sp_lang_output)
    Spinner spOutputLang;
    @BindView(R.id.tv_output)
    AutoResizeTextView tv_outputText;
    @BindView(R.id.et_input)
    EditText etInputText;
    @BindView(R.id.translate_favorite)
    MaterialFavoriteButton mfbFav;
    @BindView(R.id.img_clear)
    ImageView imgClear;

    @InjectPresenter
    TranslatePresenter translatePresenter;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);
        unbinder = ButterKnife.bind(this, view);
        initInputField();
        tv_outputText.setMinTextSize(etInputText.getTextSize());
        return view;
    }

    @Override
    public void showLoading() {
        pbOutput.setVisibility(View.VISIBLE);
        mfbFav.setVisibility(View.INVISIBLE);
        llNoTranslate.setVisibility(View.INVISIBLE);
        tv_outputText.setVisibility(View.VISIBLE);
        tv_outputText.setTextColor(getResources().getColor(R.color.textTranslatedLoading));
    }

    @Override
    public void showNoData() {
        pbOutput.setVisibility(View.INVISIBLE);
        mfbFav.setVisibility(View.INVISIBLE);
        llNoTranslate.setVisibility(View.VISIBLE);
        tv_outputText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showTranslate(Translate translate) {
        pbOutput.setVisibility(View.INVISIBLE);
        mfbFav.setVisibility(View.VISIBLE);
        llNoTranslate.setVisibility(View.INVISIBLE);
        tv_outputText.setVisibility(View.VISIBLE);
        tv_outputText.setTextColor(getResources().getColor(R.color.textTranslated));

        setupTranslate(translate);
    }

    @Override
    public void populateLangs(List<Language> langs) {
        spInputLang.setAdapter(new LanguageSpinnerAdapter(getContext()));
        spOutputLang.setAdapter(new LanguageSpinnerAdapter(getContext()));
        ((LanguageSpinnerAdapter) spInputLang.getAdapter()).addItems(langs);
        ((LanguageSpinnerAdapter) spOutputLang.getAdapter()).addItems(langs);
    }

    @OnClick(R.id.translate_favorite)
    public void onClickFav() {
        translatePresenter.onChangeFavorite();
        mfbFav.toggleFavorite();
    }

    @OnClick(R.id.btn_reload_translate)
    public void onClickReloadTranslate() {
        translatePresenter.onReloadTranslate();
    }

    @OnClick(R.id.ib_swap)
    public void onClickSwapLangs() {
        if (spInputLang.getSelectedItemPosition() == spOutputLang.getSelectedItemPosition()) {
            return;
        }

        translatePresenter.onSwapLangs();
    }

    @OnClick(R.id.img_clear)
    public void onClickClearInput() {
        translatePresenter.onClearInput();
    }

    private void initInputField() {
        etInputText.setHorizontallyScrolling(false);
        etInputText.setLines(4);
        etInputText.setPadding(etInputText.getPaddingLeft(), etInputText.getPaddingTop(), imgClear.getDrawable().getIntrinsicWidth(), etInputText.getPaddingBottom());
        etInputText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(final Editable s) {
                        translatePresenter.onChangeInput(s.toString());
                    }
                }
        );
    }

    private void setupTranslate(Translate translate) {
        tv_outputText.setText(translate.getOutput());
        mfbFav.setFavorite(translate.isFavorite(), false);
        etInputText.setText(translate.getInput());

        mfbFav.setVisibility(translate.getOutput().isEmpty() ? View.INVISIBLE : View.VISIBLE);
        imgClear.setVisibility(translate.getInput().isEmpty() ? View.INVISIBLE : View.VISIBLE);

        //langs
        setupLang(true, translate.getFromLang());
        setupLang(false, translate.getToLang());
    }

    private void setupLang(boolean isInput, String langCode) {
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
