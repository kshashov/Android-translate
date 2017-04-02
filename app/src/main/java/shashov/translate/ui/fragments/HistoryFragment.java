package shashov.translate.ui.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import shashov.translate.R;
import shashov.translate.adapters.HistoryRecyclerAdapter;
import shashov.translate.internals.di.components.AppComponent;
import shashov.translate.internals.mvp.presenters.HistoryPresenter;
import shashov.translate.internals.mvp.views.HistoryView;
import shashov.translate.realm.Translate;

import java.util.List;

/**
 * Created by envoy on 30.03.2017.
 */
public class HistoryFragment extends BaseFragment<HistoryPresenter> implements HistoryView, HistoryRecyclerAdapter.HistoryListener {
    public static final String TAG = "HistoryFragment";
    private Unbinder unbinder;

    @BindView(R.id.pb)
    ProgressBar pb;

    @BindView(R.id.ll_no_data)
    TextView tvNoData;

    @BindView(R.id.rv_exchange_rates)
    RecyclerView rvExchangeRates;

    private HistoryRecyclerAdapter adapter;
    private LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static TranslateFragment newInstance() {
        TranslateFragment fragment = new TranslateFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().loadData();
    }

    @Override
    protected void setupComponent(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    protected HistoryPresenter createPresenter() {
        return new HistoryPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void showLoading() {
        pb.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.INVISIBLE);
        rvExchangeRates.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(String error) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showContent() {
        pb.setVisibility(View.INVISIBLE);
        tvNoData.setVisibility(View.INVISIBLE);
        rvExchangeRates.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmpty() {
        pb.setVisibility(View.INVISIBLE);
        tvNoData.setVisibility(View.VISIBLE);
        rvExchangeRates.setVisibility(View.INVISIBLE);
    }

    @Override
    public void populateList(List<Translate> data) {
        adapter = new HistoryRecyclerAdapter(data, this);
        rvExchangeRates.setLayoutManager(manager);
        rvExchangeRates.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClickItem(Translate translate) {
        getPresenter().onClickTranslate(translate);
        // Snackbar.make(getActivity().findViewById(android.R.id.content), translate.getInput(), Snackbar.LENGTH_LONG).show(); //TODO
    }
}
