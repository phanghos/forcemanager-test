package org.taitasciore.android.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by roberto on 17/03/17.
 */

/**
 * Class responsible for creating the local database
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "stations.db";
    private static final int DB_VERSION = 1;
    private static final String SQL_CREATE = String.format("CREATE TABLE %s (" +
            "%s INTEGER PRIMARY KEY," +
            "%s TEXT NOT NULL," +
            "%s TEXT NOT NULL," +
            "%s TEXT NOT NULL," +
            "%s TEXT NOT NULL," +
            "%s TEXT NOT NULL," +
            "%s REAL NOT NULL," +
            "%s REAL NOT NULL)",
            StationsContract.StationEntry.TABLE_NAME,
            StationsContract.StationEntry._ID,
            StationsContract.StationEntry.LINE,
            StationsContract.StationEntry.NAME,
            StationsContract.StationEntry.ACCESIBILITY,
            StationsContract.StationEntry.ZONE,
            StationsContract.StationEntry.CONNECTIONS,
            StationsContract.StationEntry.LAT,
            StationsContract.StationEntry.LON);
    private static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + StationsContract.StationEntry.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE);
        db.execSQL(SQL_CREATE);
    }
}
