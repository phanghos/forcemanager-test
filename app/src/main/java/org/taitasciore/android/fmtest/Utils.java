package org.taitasciore.android.fmtest;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.taitasciore.android.storage.StationsContract;

import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

/**
 * Created by roberto on 17/03/17.
 */

/**
 * Helper class with various utility/helper methods
 */
public final class Utils {

    /**
     * This method takes care of filling a {@link ContentValues} instance and getting it
     * ready for insertion in the {@link android.content.ContentProvider}
     * @param metro {@link org.taitasciore.android.fmtest.ApiResponse.Metro} instance containing
     * a station's information
     * @return The {@link android.content.ContentProvider} instance ready for insertion in the
     * {@link android.content.ContentProvider}
     */
    public static ContentValues fillValues(ApiResponse.Metro metro) {
        ContentValues values = new ContentValues();
        values.put(StationsContract.StationEntry.LINE, metro.getLine());
        values.put(StationsContract.StationEntry.NAME, metro.getName());
        values.put(StationsContract.StationEntry.ACCESIBILITY, metro.getAccessibility());
        values.put(StationsContract.StationEntry.ZONE, metro.getZone());
        values.put(StationsContract.StationEntry.CONNECTIONS, metro.getConnections());
        values.put(StationsContract.StationEntry.LAT, metro.getLat());
        values.put(StationsContract.StationEntry.LON, metro.getLon());

        return values;
    }

    /**
     * This method builds a {@link org.taitasciore.android.fmtest.ApiResponse.Metro} instance
     * from a {@link Cursor}
     * @param c The {@link Cursor} containing a row
     * @return {@link org.taitasciore.android.fmtest.ApiResponse.Metro} instance filled
     */
    public static ApiResponse.Metro buildStation(Cursor c) {
        ApiResponse.Metro metro = new ApiResponse.Metro();
        metro.setId(c.getInt(c.getColumnIndex(StationsContract.StationEntry._ID)));
        metro.setAccessibility(c.getString(c.getColumnIndex(StationsContract.StationEntry.ACCESIBILITY)));
        metro.setConnections(c.getString(c.getColumnIndex(StationsContract.StationEntry.CONNECTIONS)));
        metro.setLine(c.getString(c.getColumnIndex(StationsContract.StationEntry.LINE)));
        metro.setName(c.getString(c.getColumnIndex(StationsContract.StationEntry.NAME)));
        metro.setZone(c.getString(c.getColumnIndex(StationsContract.StationEntry.ZONE)));
        metro.setLat(c.getDouble(c.getColumnIndex(StationsContract.StationEntry.LAT)));
        metro.setLon(c.getDouble(c.getColumnIndex(StationsContract.StationEntry.LON)));

        return metro;
    }

    /**
     * This method closes/hides the keyboard
     * @param context Current context
     */
    public static void hideKeyboard(final Activity context) {
        final View v = context.getCurrentFocus();

        if (v != null) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager)
                            context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            };
            v.postDelayed(runnable, 50);
        }
    }

    /**
     * This method shows a {@link MaterialDialog} containing a station's information.
     * This is called when clicking on a marker on the map
     * @param context Current context
     * @param metro Object containing a station's information
     */
    public static void showStationInfoDialog(Activity context, ApiResponse.Metro metro) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_station, false)
                .title(metro.getName())
                .positiveText("close")
                .build();

        View v = dialog.getCustomView();
        TextView tvLine = (TextView) v.findViewById(R.id.tvLine);
        TextView tvAccessibility = (TextView) v.findViewById(R.id.tvAccesibility);
        TextView tvZone = (TextView) v.findViewById(R.id.tvZone);
        TextView tvConnections = (TextView) v.findViewById(R.id.tvConnections);

        setupSpans(context, tvLine, "Line: " + metro.getLine());
        setupSpans(context, tvAccessibility, "Accesibility: " + metro.getAccessibility());
        setupSpans(context, tvZone, "Zone: " + metro.getZone());
        if (!metro.getConnections().isEmpty())
            setupSpans(context, tvConnections, "Connections: " + metro.getConnections());

        dialog.show();
    }

    /**
     * This method sets spans on every {@link TextView} in the dialog
     * @param context Current context
     * @param tv {@link TextView} instance
     * @param text The string that will be shown in the {@link TextView}
     */
    private static void setupSpans(Activity context, TextView tv, String text) {
        if (text == null || text.isEmpty()) {
            tv.setVisibility(View.GONE);
            return;
        }

        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(
                TypefaceUtils.load(context.getAssets(), "fonts/OpenSans-Bold.ttf"));
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.1f);

        int end = ssb.toString().indexOf(" ");
        ssb.setSpan(typefaceSpan, 0, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ssb.setSpan(sizeSpan, 0, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        tv.setText(ssb, TextView.BufferType.SPANNABLE);
    }

    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * This method determines network status (connected/not connected)
     * @param context Current context
     * @return True if connection is active, false otherwise
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = cm.getActiveNetworkInfo();
        return mNetworkInfo != null && mNetworkInfo.isConnected();
    }
}
