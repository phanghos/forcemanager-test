package org.taitasciore.android.storage;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by roberto on 17/03/17.
 */

/**
 * Constants to allow a better reference to columns in the database and interaction with
 * the {@link android.content.ContentProvider}
 */
public final class StationsContract {

    public static final String AUTHORITY = "org.taitasciore.android.fmtest.provider";
    public static final String STATIONS = "stations";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + STATIONS);

    /**
     * Table and column names for the table 'stations'
     */
    public static class StationEntry implements BaseColumns {

        public static final String TABLE_NAME = "stations";
        public static final String LINE = "line";
        public static final String NAME = "name";
        public static final String ACCESIBILITY = "accesibility";
        public static final String ZONE = "zone";
        public static final String CONNECTIONS = "connections";
        public static final String LAT = "lat";
        public static final String LON = "lon";
    }
}
