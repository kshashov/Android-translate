package shashov.translate.support;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by envoy on 28.03.2017.
 */
public class TranslateRealmMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        RealmSchema schema = realm.getSchema();

        if (oldVersion < 10) {
            schema.create("Translate")
                    .addField("input", String.class)
                    .addField("output", String.class)
                    .addField("fromLang", String.class)
                    .addField("toLang", String.class);
            oldVersion++;
        }

        if (oldVersion == 10) {
            schema.get("Translate")
                    .addField("time", Long.class);
            oldVersion++;
        }
    }
}