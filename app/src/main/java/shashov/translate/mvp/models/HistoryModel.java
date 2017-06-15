package shashov.translate.mvp.models;

import android.support.annotation.NonNull;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import shashov.translate.common.TranslateRealmMigration;
import shashov.translate.dao.Translate;

import java.util.Date;

public class HistoryModel {
    private static final String TAG = HistoryModel.class.getSimpleName();
    private final Realm realm;

    public HistoryModel(Realm realm) {
        this.realm = realm;
    }

    /**
     * Find translate which max time
     */
    public Translate getLast() {
        RealmResults<Translate> result = realm
                .where(Translate.class)
                .findAllSorted(TranslateRealmMigration.TranslateColumns.TIME, Sort.DESCENDING);

        if (!result.isEmpty()) {
            return result.first();
        }
        return null;
    }

    /**
     * Find translates in realm
     */
    public void getHistory(@NonNull HistoryDataAction onSuccess) {
        OrderedRealmCollection<Translate> all = realm.where(Translate.class).findAllSorted(TranslateRealmMigration.TranslateColumns.TIME, Sort.DESCENDING);

        OrderedRealmCollection<Translate> favs = realm
                .where(Translate.class)
                .notEqualTo(TranslateRealmMigration.TranslateColumns.FAV_TIME, 0)
                .findAllSorted(TranslateRealmMigration.TranslateColumns.FAV_TIME, Sort.DESCENDING);
        onSuccess.onSuccess(all, favs);
    }

    /**
     * Change fav attribute for translate
     */
    public void changeFavorite(@NonNull Translate translate) {
        Translate savedTranslate = findTranslate(translate);
        if (savedTranslate == null) {
            return;
        }

        realm.beginTransaction();
        savedTranslate.setFavTime(savedTranslate.isFavorite() ? 0 : (new Date()).getTime());
        realm.commitTransaction();
    }

    /**
     * Find same translate in realm
     */
    public Translate findTranslate(@NonNull Translate translate) {
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

    /**
     * Delete Translate items from history all favorite list
     * @param isAll false = delete only favorite status
     */
    public void delete(@NonNull OrderedRealmCollection<Translate> items, boolean isAll) {
        if (isAll) {
            realm.executeTransaction((Realm realm) -> items.deleteAllFromRealm());
        } else {
            realm.executeTransaction((Realm realm) -> {
                for (Translate translate : items) {
                    translate.setFavTime(0);
                }
            });
        }
    }

    /**
     * Add translate to realm or update time attr
     */
    public Translate saveTranslate(@NonNull Translate translate) {
        Translate result = findTranslate(translate);
        realm.beginTransaction();

        //for new translate
        if (result == null) {
            result = translate;
            result.setFavTime(0);
            result.setTime((new Date()).getTime());
            realm.copyToRealm(result);
        }

        //update time
        result.setTime((new Date()).getTime());
        realm.commitTransaction();
        return result;
    }

    public interface HistoryDataAction {
        void onSuccess(OrderedRealmCollection<Translate> all, OrderedRealmCollection<Translate> favs);
    }
}
