package shashov.translate.ui.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.OrderedRealmCollection;
import shashov.translate.R;
import shashov.translate.adapters.HistorySearchAdapter;
import shashov.translate.internals.di.components.AppComponent;
import shashov.translate.internals.mvp.presenters.HistoryPresenter;
import shashov.translate.internals.mvp.views.HistoryView;
import shashov.translate.realm.Translate;

public class HistoryFragment extends BaseFragment<HistoryPresenter> implements HistoryView, HistorySearchAdapter.HistoryListener, SearchView.OnQueryTextListener {
    public static final String TAG = "HistoryFragment";
    private static final String SV_TEXT = "svHistoryText";
    private static final String RV_STATE = "rvHistoryState";
    private Unbinder unbinder;

    @BindView(R.id.pb)
    ProgressBar pb;

    @BindView(R.id.ll_no_data)
    TextView tvNoData;

    @BindView(R.id.rv_history)
    RecyclerView rvHistory;

    @BindView(R.id.sv_history)
    SearchView svHistory;

    private HistorySearchAdapter adapter;
    private Parcelable rvState;
    private String svHistoryText = "";

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        if (savedInstanceState != null) {
            if ((savedInstanceState.containsKey(RV_STATE))) {
                rvState = savedInstanceState.getParcelable(RV_STATE);
            }
            if (savedInstanceState.containsKey(SV_TEXT)) {
                svHistoryText = savedInstanceState.getString(SV_TEXT);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().loadData(true);
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
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        rvState = rvHistory.getLayoutManager().onSaveInstanceState();
        svHistoryText = svHistory.getQuery().toString();
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
        rvHistory.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(String error) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showContent() {
        pb.setVisibility(View.INVISIBLE);
        tvNoData.setVisibility(View.INVISIBLE);
        rvHistory.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmpty() {
        pb.setVisibility(View.INVISIBLE);
        tvNoData.setVisibility(View.VISIBLE);
        rvHistory.setVisibility(View.INVISIBLE);
    }

    @Override
    public void populateList(OrderedRealmCollection<Translate> data) {
        if (rvHistory.getAdapter() != null) {
            // already loaded
            return;
        }

        //add loaded data and restore last state
        adapter = new HistorySearchAdapter(getContext(), data, this);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvHistory.setAdapter(adapter);
        svHistory.setOnQueryTextListener(this);
        svHistory.setQuery(svHistoryText, true);
        //adapter.filter(svHistoryText);
        if (rvState != null) {
            rvHistory.getLayoutManager().onRestoreInstanceState(rvState);
        }
    }

    @Override
    public void onClickItem(Translate translate) {
        getPresenter().onClickTranslate(translate);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (rvState != null) {
            outState.putParcelable(RV_STATE, rvState);
        }

        if (svHistoryText != null) {
            outState.putString(SV_TEXT, svHistoryText);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if ((savedInstanceState.containsKey(RV_STATE))) {
                if (rvState == null) {
                    rvState = savedInstanceState.getParcelable(RV_STATE);
                }
            }
            if (savedInstanceState.containsKey(SV_TEXT)) {
                if (svHistoryText == null) {
                    svHistoryText = savedInstanceState.getString(SV_TEXT);
                }
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.filter(query);
        svHistory.setIconified(false);
        svHistory.clearFocus();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return false;
    }

}
