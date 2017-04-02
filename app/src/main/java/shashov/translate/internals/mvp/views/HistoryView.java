package shashov.translate.internals.mvp.views;

import shashov.translate.internals.mvp.MVP;
import shashov.translate.realm.Translate;

import java.util.List;

/**
 * Created by Aksiom on 6/29/2016.
 */
public interface HistoryView extends MVP.View {

    void populateList(List<Translate> data);
}
