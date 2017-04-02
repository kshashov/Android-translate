package shashov.translate.internals.mvp.presenters;

import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import shashov.translate.internals.mvp.MVP;

import java.util.Map;

/**
 * Created by envoy on 24.03.2017.
 */
public class PresenterCache {
    /**
     * Map that contains the cached presenters.
     */
    private final Map<String, MVP.Presenter<? extends MVP.View>> cachedPresenters = new ArrayMap<>();

    /**
     * Returns a {@code presenter} with the given key.
     *
     * @param activityName The key a presenter is supposed to have.
     * @return The {@code presenter} or {@code null} if none was found.
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getPresenter(String activityName) {
        return (T) cachedPresenters.get(activityName);
    }

    /**
     * Stores the given {@code presenter}.
     *
     * @param activityName The key to store the presenter.
     * @param presenter    Presenter to store.
     */
    public void putPresenter(String activityName, MVP.Presenter<? extends MVP.View> presenter) {
        cachedPresenters.put(activityName, presenter);
    }

    /**
     * Removes the given {@code presenter} from this cache.
     *
     * @param presenterToRemove Presenter to remove.
     */
    public void removePresenter(MVP.Presenter<? extends MVP.View> presenterToRemove) {
        for (Map.Entry<String, MVP.Presenter<? extends MVP.View>> entry : cachedPresenters.entrySet()) {
            if (presenterToRemove.equals(entry.getValue())) {
                cachedPresenters.remove(entry.getKey());
                break;
            }
        }
    }
}
