package shashov.translate.internals.mvp.views;

import io.realm.OrderedRealmCollection;
import shashov.translate.internals.mvp.MVP;
import shashov.translate.realm.Translate;

public interface HistoryView extends MVP.View {

    void populateList(OrderedRealmCollection<Translate> data);
}
