package shashov.translate.ui.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import shashov.translate.R;
import shashov.translate.dao.Language;

import java.util.ArrayList;
import java.util.List;

public class LanguageSpinnerAdapter extends ArrayAdapter<String> {
    private List<Language> langs = new ArrayList<>();

    public LanguageSpinnerAdapter(Context context) {
        super(context, R.layout.lang_spinner_item);
    }

    public void addItems(List<Language> items) {
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

    public int getPosition(String langCode) {
        for (int i = 0; i < langs.size(); i++) {
            if (langs.get(i).getCode().equals(langCode)) {
                return i;
            }
        }
        return 0;
    }
}
