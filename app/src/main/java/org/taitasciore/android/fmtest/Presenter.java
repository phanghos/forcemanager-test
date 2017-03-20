package org.taitasciore.android.fmtest;

/**
 * Created by roberto on 18/03/17.
 */

/**
 * Interface which every presenter must implement
 */
public interface Presenter {

    void onViewAttached(MainView view);
    void onViewDetached();
}
