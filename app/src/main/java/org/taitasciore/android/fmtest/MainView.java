package org.taitasciore.android.fmtest;

import android.database.Cursor;

import java.util.List;

/**
 * Created by roberto on 17/03/17.
 */

/**
 * Interface to serve as communication between presenter and view
 */
public interface MainView {

    void showNetworkProgress();
    void hideProgress();
    void showSorryText();
    void showSuggestions(Cursor c);
    void showStationInfo(ApiResponse.Metro station);
    void showNetworkError();
}
