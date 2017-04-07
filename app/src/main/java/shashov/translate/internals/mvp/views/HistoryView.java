package shashov.translate.internals.mvp.views;

import io.realm.OrderedRealmCollection;
import shashov.translate.dao.Translate;
import shashov.translate.internals.mvp.MVP;

public interface HistoryView extends MVP.View {

    void populateList(OrderedRealmCollection<Translate> data);
}
