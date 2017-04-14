package shashov.translate.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import io.realm.Case;
import io.realm.OrderedRealmCollection;
import shashov.translate.R;
import shashov.translate.dao.Translate;
import shashov.translate.support.TranslateRealmMigration;
import xyz.projectplay.realmsearchadapter.RealmSearchAdapter;

public class HistorySearchAdapter extends RealmSearchAdapter {
    private HistoryListener historyListener;

    public HistorySearchAdapter(@NonNull Context context, @Nullable OrderedRealmCollection data, HistoryListener historyListener) {
        super(context,
                data,
                TranslateRealmMigration.TranslateColumns.INPUT,
                true,
                Case.INSENSITIVE,
                null,
                null,
                null);
        this.historyListener = historyListener;
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_lang_code)
        public TextView tvLangsCode;
        @BindView(R.id.tv_output)
        public TextView tvOutput;
        @BindView(R.id.tv_input)
        public TextView tvInput;
        @BindView(R.id.translate_favorite)
        public MaterialFavoriteButton mfbFav;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final HistoryViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View vComplex = inflater.inflate(R.layout.history_item, parent, false);
        viewHolder = new HistorySearchAdapter.HistoryViewHolder(vComplex);

        viewHolder.mfbFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                historyListener.onChangeFavorite((Translate) getItem(viewHolder.getLayoutPosition()));
            }
        });
        viewHolder.tvInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                historyListener.onClickItem((Translate) getItem(viewHolder.getLayoutPosition()));
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        HistorySearchAdapter.HistoryViewHolder vhc = (HistorySearchAdapter.HistoryViewHolder) holder;
        final Translate translate = (Translate) getItem(position);
        vhc.tvLangsCode.setText(translate.getFromLang() + "-" + translate.getToLang());
        vhc.tvOutput.setText(translate.getOutput());
        vhc.tvInput.setText(translate.getInput());
        vhc.mfbFav.setFavorite(translate.isFavorite(), false);
    }

    public interface HistoryListener {
        void onClickItem(Translate translate);

        void onChangeFavorite(Translate translate);
    }
}
