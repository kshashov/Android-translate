package shashov.translate.support;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class TranslateRealmMigration implements RealmMigration {
    public static class TranslateColumns {
        public static final String INPUT = "input";
        public static final String OUTPUT = "output";
        public static final String FROM_LANG = "fromLang";
        public static final String TO_LANG = "toLang";
        public static final String TIME = "time";
        public static final String FAV_TIME = "favTime";

    }

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        RealmSchema schema = realm.getSchema();

        if (oldVersion < 10) {
            schema.create("Translate")
                    .addField(TranslateColumns.INPUT, String.class)
                    .addField(TranslateColumns.OUTPUT, String.class)
                    .addField(TranslateColumns.FROM_LANG, String.class)
                    .addField(TranslateColumns.TO_LANG, String.class);
            oldVersion++;
        }

        if (oldVersion == 10) {
            schema.get("Translate")
                    .addField(TranslateColumns.TIME, Long.class);
            oldVersion++;
        }

        if (oldVersion == 11) {
            schema.get("Translate")
                    .addField(TranslateColumns.FAV_TIME, long.class);
            oldVersion++;
        }
    }
}