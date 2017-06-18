package shashov.translate.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import shashov.translate.R;
import shashov.translate.dao.Language;

import java.util.ArrayList;
import java.util.List;

public class LanguageSpinnerAdapter extends ArrayAdapter<String> {
    private List<Language> langs = new ArrayList<>();

    public LanguageSpinnerAdapter(@NonNull Context context) {
        super(context, R.layout.lang_spinner_item);
    }

    public void addItems(@NonNull List<Language> items) {
        langs.addAll(items);
        String[] languages = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            languages[i] = items.get(i).getTitle();
        }

        addAll(languages);
        notifyDataSetChanged();
    }

    public Language getLanguage(int position) {
        if ((langs != null) && (langs.size() > position)) {
            return langs.get(position);
        }

        return null;
    }

    public int getPosition(@NonNull String langCode) {
        for (int i = 0; i < langs.size(); i++) {
            if (langs.get(i).getCode().equals(langCode)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lang_spinner_item, null);
        }

        TextView textView = (TextView) view.findViewById(R.id.standard_spinner_format);
        textView.setText(langs.get(position).getCode().toUpperCase());
        return view;
    }

    @Override
    public String getItem(int position) {
        return langs.get(position).getTitle();
    }
}
