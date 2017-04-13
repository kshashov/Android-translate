package shashov.translate.internals.mvp;

import android.app.Activity;

import java.lang.ref.WeakReference;

public interface MVP {

    interface Model {

        interface OnDataLoaded<D> {
            void onSuccess(D data);

            void onFail(String error);
        }
    }

    interface View {
        void showLoading();

        void showError(String error);

        void showContent();

        void showEmpty();

        Activity getActivity();
    }

    abstract class Presenter<V extends View> {
        private WeakReference<V> view = null;

        public final void setView(V view) {
            if (view == null) throw new NullPointerException("new view must not be null");

            if (this.view != null) dropView(this.view.get());

            this.view = new WeakReference<>(view);
        }

        public final void dropView(V view) {
            if (view == null) throw new NullPointerException("dropped view must not be null");
            this.view = null;
        }

        protected final V getView() {
            if (view == null) throw new NullPointerException("getView called when view is null. " +
                    "Ensure setView(View view) is called first.");
            return view.get();
        }

        public void onStart() {
        }

        public void onStop(boolean changingConfigurations) {
            view = null;
        }
    }
}
