package shashov.translate.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import shashov.translate.R;
import shashov.translate.realm.Translate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aksiom on 6/30/2016.
 */
public class HistoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private HistoryListener historyListener;
    private List<Translate> translates = new ArrayList<>();

    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_lang_code)
        public TextView tvLangsCode;
        @BindView(R.id.tv_output)
        public TextView tvOutput;
        @BindView(R.id.tv_input)
        public TextView tvInput;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public HistoryRecyclerAdapter(List<Translate> translates, HistoryListener historyListener) {
        this.historyListener = historyListener;
        this.translates = translates;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View vComplex = inflater.inflate(R.layout.history_item, parent, false);
        viewHolder = new HistoryViewHolder(vComplex);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        HistoryViewHolder vhc = (HistoryViewHolder) holder;
        vhc.tvLangsCode.setText(translates.get(position).getFromLang() + "-" + translates.get(position).getToLang());

        vhc.tvOutput.setText(translates.get(position).getOutput());
        vhc.tvInput.setText(translates.get(position).getInput());
        vhc.tvLangsCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                historyListener.onClickItem(translates.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return translates.size();
    }

    public interface HistoryListener {
        void onClickItem(Translate translate);
    }
}
