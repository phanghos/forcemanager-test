package org.taitasciore.android.fmtest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by roberto on 18/03/17.
 */

/**
 * Using a retained fragment to better handle configuration changes, in particular,
 * screen orientation changes which destroy ongoing background tasks such as
 * connections to the API, which would cause the app to crash. I was tempted to add
 * android:screenOrientation="portrait" to the AndroidManifest, which would have made
 * things easier since I would not have had to worry about configuration changes because
 * orientation would be fixed in portrait mode and the activity would not be destroyed.
 * But I wanted to do a good job and so decided to take on the 'challenge'
 */
public class WorkerFragment extends Fragment {

    private MainView view;
    private MainPresenter presenter;

    /**
     * By calling setRetainInstance(true), we let the Android OS know that this will be a
     * retained fragment, that is, a fragment which will survive through orientation changes,
     * which is what we want here
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        presenter = new MainPresenter(view);
    }

    /**
     * Attaches current view to the {@link MainPresenter} instance
     * @param context Current context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        view = (MainView) getActivity();
        if (presenter != null) presenter.onViewAttached(view);
    }

    /**
     * Detaches view from fragment and presenter
     */
    @Override
    public void onDetach() {
        super.onDetach();
        if (presenter != null) presenter.onViewDetached();
        view = null;
    }

    /**
     * Destroys {@link MainPresenter} instance
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter = null;
    }

    /**
     * Returns {@link MainPresenter} instance to make it available in the activity
     * @return {@link MainPresenter} instance
     */
    public MainPresenter getPresenter() {
        return presenter;
    }
}
