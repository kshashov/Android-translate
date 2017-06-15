package shashov.translate.ui.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import io.realm.OrderedRealmCollection;
import shashov.translate.R;
import shashov.translate.dao.Translate;
import shashov.translate.mvp.presenters.HistoryPresenter;
import shashov.translate.mvp.views.HistoryView;
import shashov.translate.ui.adapters.HistorySearchAdapter;

public class HistoryFragment extends MvpAppCompatFragment implements HistoryView, SearchView.OnQueryTextListener, TabLayout.OnTabSelectedListener {

    @BindView(R.id.tl_history)
    TabLayout tlHistory;
    @BindView(R.id.rv_history)
    RecyclerView rvHistory;
    @BindView(R.id.sv_history)
    SearchView svHistory;

    @InjectPresenter(type = PresenterType.GLOBAL)
    HistoryPresenter historyPresenter;
    private Parcelable rvState;
    private Unbinder unbinder;
    private HistorySearchAdapter adapter;
    private AlertDialog dialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void showContent(boolean isAll, @NonNull OrderedRealmCollection<Translate> data) {
        tlHistory.removeOnTabSelectedListener(this);
        tlHistory.getTabAt(isAll ? 0 : 1).select();
        tlHistory.addOnTabSelectedListener(this);

        adapter = new HistorySearchAdapter(getContext(), data, historyPresenter);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvHistory.setAdapter(adapter);
    }

    @Override
    public void showDeleteDialog() {
        dialog = new AlertDialog.Builder(getContext())
                .setMessage(getResources().getString(R.string.deleteAll)) //TODO pass message
                .setPositiveButton(android.R.string.yes,
                        (dialog1, which) -> historyPresenter.onPositiveCloseDeleteDialog(tlHistory.getSelectedTabPosition() == 0))
                .setNegativeButton(android.R.string.no, (dialog1, which) -> historyPresenter.onNegativeCloseDeleteDialog())
                // .setOnDismissListener((dialog1) -> historyPresenter.onNegativeCloseDeleteDialog())
                .setOnCancelListener((dialog1) -> historyPresenter.onNegativeCloseDeleteDialog())
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
        });
        dialog.show();
    }

    @Override
    public void setRVState(Parcelable rvState) {
        if (rvState != null) {
            rvHistory.getLayoutManager().onRestoreInstanceState(rvState);
        }
    }

    @Override
    public void closeSearchViewFocus() {
        svHistory.setIconified((svHistory.getQuery() == null) || svHistory.getQuery().toString().isEmpty());
        svHistory.clearFocus();
    }

    @Override
    public void closeDeleteDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void search(@NonNull String newText) {
        svHistory.setOnQueryTextListener(null);
        svHistory.setQuery(newText, true);
        svHistory.setOnQueryTextListener(this);

        if (adapter != null) {
            adapter.filter(newText);
        }
    }

    @OnClick(R.id.ib_delete)
    void onDeleteClick() {
        historyPresenter.onDeleteHistory();
    }

    @Override
    public boolean onQueryTextSubmit(String newText) {
        historyPresenter.onChangeSearchText(newText);
        svHistory.setIconified(false);
        svHistory.clearFocus();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        historyPresenter.onChangeSearchText(newText);
        return false;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        historyPresenter.onChangeTab(tab.getPosition() == 0);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    @Override
    public void onPause() {
        super.onPause();
        if (rvHistory.getLayoutManager() != null) {
            rvState = rvHistory.getLayoutManager().onSaveInstanceState();
        }
    }

    @Override
    public void onDestroyView() {
        if (dialog != null) {
            dialog.dismiss();
        }
        unbinder.unbind();
        historyPresenter.saveRVState(rvState);
        super.onDestroyView();
    }
}
