package shashov.translate.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import butterknife.*;
import com.lb.auto_fit_textview.AutoResizeTextView;
import shashov.translate.R;
import shashov.translate.adapters.LanguageSpinnerAdapter;
import shashov.translate.internals.di.components.AppComponent;
import shashov.translate.internals.mvp.presenters.TranslatePresenter;
import shashov.translate.internals.mvp.views.TranslateView;
import shashov.translate.realm.Language;
import shashov.translate.realm.Translate;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TranslateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TranslateFragment extends BaseFragment<TranslatePresenter> implements TranslateView {
    // TODO: Rename parameter arguments, choose names that match
    public static final String TAG = "TranslateFragment";

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

    private static final String TRANSLATE = "translate";
    private Translate translate;
    private Unbinder unbinder;
    private Timer timer = new Timer();
    private final long DELAY = 500; // milliseconds

    public TranslateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param translate Parameter 1.
     * @return A new instance of fragment TranslateFragment.
     */
    // TODO: Rename and change types and number of parameters
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
            }
            getArguments().clear();
        }
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
    public void onStop() {
        timer.cancel();
        Translate translate = new Translate();
        translate.setInput(etInputText.getText().toString());
        translate.setToLang(((LanguageSpinnerAdapter) spOutputLang.getAdapter()).getLanguage(spOutputLang.getSelectedItemPosition()).getCode());
        translate.setFromLang(((LanguageSpinnerAdapter) spInputLang.getAdapter()).getLanguage(spInputLang.getSelectedItemPosition()).getCode());
        getPresenter().saveCurrentTranslate(translate);
        super.onStop();
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
    public void populateList(List<Language> data) {
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
        if (isLoading) {
            tv_outputText.setTextColor(getResources().getColor(R.color.textTranslatedLoading));
        } else {
            tv_outputText.setTextColor(getResources().getColor(R.color.textTranslated));
        }

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
    public void showOutput(String output) {
        tv_outputText.setText(output);
    }

    @Override
    public void showTranslateReload() {
        tv_outputText.setVisibility(View.GONE);
        llNoTranslate.setVisibility(View.VISIBLE);
    }

    void onTextChanged(CharSequence text) {
        setClearButtonVisible(text.length() > 0);
        sendToConvert(text.toString());
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
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (fragment == null || (fragment.getActivity() == null)) {
                                            return;
                                        }
                                        fragment.getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                fragment.onTextChanged(s.toString());
                                            }
                                        });
                                    }
                                },
                                DELAY
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
