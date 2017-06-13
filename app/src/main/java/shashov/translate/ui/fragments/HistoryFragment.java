package shashov.translate.ui.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import io.realm.OrderedRealmCollection;
import shashov.translate.R;
import shashov.translate.dao.Translate;
import shashov.translate.mvp.presenters.HistoryPresenter;
import shashov.translate.mvp.views.HistoryView;
import shashov.translate.ui.adapters.HistorySearchAdapter;

/**
 * Created by kirill on 13.06.17.
 */
public class HistoryFragment extends MvpAppCompatFragment implements HistoryView, SearchView.OnQueryTextListener {
    private static final String RV_STATE = "rvHistoryState";

    @BindView(R.id.tl_history)
    TabLayout tlHistory;
    @BindView(R.id.pb)
    ProgressBar pb;
    @BindView(R.id.rv_history)
    RecyclerView rvHistory;
    @BindView(R.id.sv_history)
    SearchView svHistory;
    @BindView(R.id.ib_delete)
    ImageButton ibDelete;

    @InjectPresenter
    HistoryPresenter historyPresenter;
    private boolean isAll = true;
    private Parcelable rvState;
    private Unbinder unbinder;
    private HistorySearchAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(RV_STATE)) {
            rvState = savedInstanceState.getParcelable(RV_STATE);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tlHistory.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                historyPresenter.onChangeTab(isAll = (tab.getPosition() == 0));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        svHistory.setOnQueryTextListener(this);
    }

    @Override
    public void showContent(OrderedRealmCollection<Translate> data) {
        pb.setVisibility(View.INVISIBLE);
        rvHistory.setVisibility(View.VISIBLE);


        adapter = new HistorySearchAdapter(getContext(), data, historyPresenter);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvHistory.setAdapter(adapter);

        if (rvState != null) {
            rvHistory.getLayoutManager().onRestoreInstanceState(rvState);
        }
        if (svHistory.getQuery() != null) {
            adapter.filter(svHistory.getQuery().toString());
        }
    }

    @Override
    public void showLoadingContent() {
        pb.setVisibility(View.VISIBLE); //TODO disable all
        rvHistory.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showDeleteDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setMessage("test") //TODO pass message
                .setPositiveButton(android.R.string.yes, (dialog1, which) -> historyPresenter.deleteCloseDeleteDialog(true))
                .setNegativeButton(android.R.string.no, (dialog1, which) -> historyPresenter.deleteCloseDeleteDialog(false))
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getActivity().getResources().getColor(R.color.black));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getActivity().getResources().getColor(R.color.black));
        });
        dialog.show();
    }

    @Override
    public void restoreState(HistoryViewState viewState) {
        isAll = viewState.isAllTab;
        tlHistory.getTabAt(viewState.isAllTab ? 0 : 1).select();
        svHistory.setQuery(viewState.searchString, true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (rvHistory.getLayoutManager() != null) {
            rvState = rvHistory.getLayoutManager().onSaveInstanceState();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (rvState != null) {
            outState.putParcelable(RV_STATE, rvState);
        }
    }

    @OnClick(R.id.ib_delete)
    public void onDeleteClick() {
        historyPresenter.onDeleteHistory();
    }

    @Override
    public boolean onQueryTextSubmit(String newText) {
        onQueryChanged(newText);
        svHistory.setIconified(false);
        svHistory.clearFocus();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        onQueryChanged(newText);
        return false;
    }

    private void onQueryChanged(String newText) {
        if (adapter != null) {
            adapter.filter(newText);
        }
        historyPresenter.saveSearchText(newText);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
