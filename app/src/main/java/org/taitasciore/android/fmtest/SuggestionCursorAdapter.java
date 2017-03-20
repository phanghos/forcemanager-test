package org.taitasciore.android.fmtest;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.taitasciore.android.storage.StationsContract;

/**
 * Created by roberto on 17/03/17.
 */

/**
 * Adapter that will be attached to {@link android.support.v7.widget.SearchView} in activity
 */
public class SuggestionCursorAdapter extends CursorAdapter {

    public SuggestionCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Using custom layout for rows
     * @param context
     * @param cursor
     * @param parent
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.suggestion_row_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        TextView tvSuggestion = (TextView) view.findViewById(R.id.tvSuggestion);

        String name = cursor.getString(
                cursor.getColumnIndex(StationsContract.StationEntry.NAME));
        String line = cursor.getString(
                cursor.getColumnIndex(StationsContract.StationEntry.LINE));
        tvSuggestion.setText(name + " (" + line + ")");
    }
}
