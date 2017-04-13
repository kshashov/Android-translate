package shashov.translate.internals.mvp.models;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import shashov.translate.dao.Translate;
import shashov.translate.internals.mvp.MVP;
import shashov.translate.support.TranslateRealmMigration;

import java.util.Date;

/**
 * Created by envoy on 13.04.2017.
 */
public class HistoryModel implements MVP.Model {
    private static final String TAG = HistoryModel.class.getSimpleName();
    private final Realm realm;

    public HistoryModel(Realm realm) {
        this.realm = realm;
    }

    public Translate getLast() {
        RealmResults<Translate> result = realm
                .where(Translate.class)
                .findAllSorted(TranslateRealmMigration.TranslateColumns.TIME, Sort.DESCENDING);

        if (!result.isEmpty()) {
            return result.first();
        }
        return null;
    }


    public void getAll(final OnDataLoaded<OrderedRealmCollection<Translate>> onDataLoaded) {
        OrderedRealmCollection<Translate> translates = realm.where(Translate.class).findAllSorted(TranslateRealmMigration.TranslateColumns.TIME, Sort.DESCENDING);
        onDataLoaded.onSuccess(translates);
    }

    public void getFav(final OnDataLoaded<OrderedRealmCollection<Translate>> onDataLoaded) {
        OrderedRealmCollection<Translate> translates = realm
                .where(Translate.class)
                .notEqualTo(TranslateRealmMigration.TranslateColumns.FAV_TIME, 0)
                .findAllSorted(TranslateRealmMigration.TranslateColumns.FAV_TIME, Sort.DESCENDING);
        onDataLoaded.onSuccess(translates);
    }

    public void changeFavorite(Translate translate) {
        realm.beginTransaction();
        translate.setFavTime(translate.isFavorite() ? 0 : (new Date()).getTime());
        realm.commitTransaction();
    }

    public Translate findTranslate(Translate translate) {
        RealmResults<Translate> result = realm
                .where(Translate.class)
                .equalTo(TranslateRealmMigration.TranslateColumns.INPUT, translate.getInput())
                .equalTo(TranslateRealmMigration.TranslateColumns.FROM_LANG, translate.getFromLang())
                .equalTo(TranslateRealmMigration.TranslateColumns.TO_LANG, translate.getToLang())
                .findAll();
        if (!result.isEmpty()) {
            return result.first();
        }
        return null;
    }
}
