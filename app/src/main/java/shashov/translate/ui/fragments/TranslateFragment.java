package shashov.translate.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import butterknife.*;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.lb.auto_fit_textview.AutoResizeTextView;
import shashov.translate.R;
import shashov.translate.adapters.LanguageSpinnerAdapter;
import shashov.translate.dao.Language;
import shashov.translate.dao.Translate;
import shashov.translate.internals.di.components.AppComponent;
import shashov.translate.internals.mvp.presenters.TranslatePresenter;
import shashov.translate.internals.mvp.views.TranslateView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TranslateFragment extends BaseFragment<TranslatePresenter> implements TranslateView {
    public static final String TAG = "TranslateFragment";
    private static final String TRANSLATE = "translate";
    private final long SUBMIT_TEXT_DELAY = 500; // milliseconds

    @BindView(R.id.pb)
    ProgressBar pb;

    @BindView(R.id.pb_output)
    ProgressBar pbOutput;

    @BindView(R.id.ll_no_data)
    LinearLayout llNoData;

    @BindView(R.id.btn_reload)
    Button btnReload;

    @BindView(R.id.ll_no_translate)
    LinearLayout llNoTranslate;

    @BindView(R.id.btn_reload_translate)
    Button btnReloadTranslate;

    @BindView(R.id.rl_translate_container)
    RelativeLayout rlTranslateContainer;

    @BindView(R.id.sp_lang_input)
    Spinner spInputLang;

    @BindView(R.id.sp_lang_output)
    Spinner spOutputLang;

    @BindView(R.id.tv_output)
    AutoResizeTextView tv_outputText;

    @BindView(R.id.et_input)
    EditText etInputText;

    @BindView(R.id.img_clear)
    ImageView imgClear;

    @BindView(R.id.translate_favorite)
    public MaterialFavoriteButton mfbFav;

    private Translate translate;
    private Unbinder unbinder;
    private Timer timer = new Timer();

    public TranslateFragment() {
        // Required empty public constructor
    }

    public static TranslateFragment newInstance(Translate translate) {
        TranslateFragment fragment = new TranslateFragment();
        Bundle args = new Bundle();
        args.putSerializable(TRANSLATE, translate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(TRANSLATE)) {
                translate = (Translate) getArguments().getSerializable(TRANSLATE);
                getArguments().remove(TRANSLATE);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mfbFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getPresenter() != null) {
                    getPresenter().changeFavorite();
                    mfbFav.toggleFavorite();
                }
            }
        });
    }

    @Override
    protected void setupComponent(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    protected TranslatePresenter createPresenter() {
        return new TranslatePresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
        timer = new Timer();
        initInputEditText();
        getPresenter().loadData(translate);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if ((savedInstanceState.containsKey(TRANSLATE))) {
                if (translate == null) {
                    translate = (Translate) savedInstanceState.getSerializable(TRANSLATE);
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(TRANSLATE, translate);
    }

    @Override
    public void onPause() {
        timer.cancel();
        super.onPause();
        translate = getCurrentState();
    }

    private Translate getCurrentState() {
        Translate translate = new Translate();
        translate.setInput(etInputText.getText().toString());
        translate.setToLang(((LanguageSpinnerAdapter) spOutputLang.getAdapter()).getLanguage(spOutputLang.getSelectedItemPosition()).getCode());
        translate.setFromLang(((LanguageSpinnerAdapter) spInputLang.getAdapter()).getLanguage(spInputLang.getSelectedItemPosition()).getCode());
        return translate;
    }

    @Override
    public void showLoading() {
        pb.setVisibility(View.VISIBLE);
        llNoData.setVisibility(View.INVISIBLE);
        rlTranslateContainer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(String error) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showContent() {
        pb.setVisibility(View.INVISIBLE);
        llNoData.setVisibility(View.INVISIBLE);
        rlTranslateContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmpty() {
        pb.setVisibility(View.INVISIBLE);
        llNoData.setVisibility(View.VISIBLE);
        rlTranslateContainer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void populateLangs(List<Language> data) {
        spInputLang.setAdapter(new LanguageSpinnerAdapter(getContext()));
        spOutputLang.setAdapter(new LanguageSpinnerAdapter(getContext()));
        ((LanguageSpinnerAdapter) spInputLang.getAdapter()).addItems(data);
        ((LanguageSpinnerAdapter) spOutputLang.getAdapter()).addItems(data);
    }

    @Override
    public void changeLang(int langCode, final boolean isInput) {
        Spinner spinner = isInput ? spInputLang : spOutputLang;
        spinner.setOnItemSelectedListener(null);
        spinner.setSelection(langCode, false);
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getPresenter().selectLang(i, isInput);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void showLoadingTranslate(boolean isLoading) {
        tv_outputText.setTextColor(getResources().getColor(isLoading ? R.color.textTranslatedLoading : R.color.textTranslated));
        mfbFav.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
        pbOutput.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void showTranslate() {
        sendToConvert(etInputText.getText().toString());
    }

    @Override
    public void showInput(String input) {
        setClearButtonVisible(!input.isEmpty());
        etInputText.setText(input);
    }

    @Override
    public void showOutput(Translate translate) {
        tv_outputText.setText(translate.getOutput());
        mfbFav.setFavorite(translate.isFavorite(), false);

        if (translate.getOutput().isEmpty()) {
            mfbFav.setVisibility(View.INVISIBLE);
        } else {
            mfbFav.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showTranslateReload() {
        tv_outputText.setVisibility(View.GONE);
        llNoTranslate.setVisibility(View.VISIBLE);
    }

    private void sendToConvert(String input) {
        getPresenter().translate(input.trim());
    }

    private void setClearButtonVisible(boolean isVisible) {
        imgClear.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    private void initInputEditText() {
        etInputText.setHorizontallyScrolling(false);
        etInputText.setLines(4);
        etInputText.setPadding(etInputText.getPaddingLeft(), etInputText.getPaddingTop(), imgClear.getDrawable().getIntrinsicWidth(), etInputText.getPaddingBottom());
        tv_outputText.setMinTextSize(etInputText.getTextSize());
        final TranslateFragment fragment = this;
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
                        setClearButtonVisible(s.toString().length() > 0);

                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (fragment.getActivity() == null) { //TODO it safe????
                                            return;
                                        }
                                        fragment.getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                fragment.sendToConvert(s.toString());
                                            }
                                        });
                                    }
                                },
                                SUBMIT_TEXT_DELAY
                        );
                    }
                }
        );
    }

    @OnClick(R.id.ib_swap)
    void onSwapLangsClick() {
        getPresenter().swapLangs();
    }

    @OnFocusChange(R.id.et_input)
    void onFocusChanged(boolean focused) {
        if (!focused) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etInputText.getWindowToken(), 0);
        }
    }

    @OnClick(R.id.img_clear)
    void OnClickButtonClear() {
        showInput("");
        showTranslate();
    }

    @OnClick(R.id.btn_reload_translate)
    void OnClickButtonReloadTranslate() {
        llNoTranslate.setVisibility(View.GONE);
        tv_outputText.setVisibility(View.VISIBLE);
        showTranslate();
    }

    @OnClick(R.id.btn_reload)
    void OnClickButtonReload() {
        getPresenter().loadData(translate);
    }

    public void setTranslate(Translate translate) {
        this.translate = translate;
    }
}
