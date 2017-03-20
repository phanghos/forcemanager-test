package org.taitasciore.android.storage;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by roberto on 17/03/17.
 */

public class StationsContentProvider extends ContentProvider {

    private DbHelper mHelper;
    private SQLiteDatabase db;
    private static UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMatcher.addURI(StationsContract.AUTHORITY, StationsContract.STATIONS, 1);
    }

    @Override
    public boolean onCreate() {
        mHelper = new DbHelper(getContext());
        db = mHelper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (mUriMatcher.match(uri) == 1) {
            Cursor c = db.query(
                    StationsContract.StationEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
            if (c != null) c.setNotificationUri(getContext().getContentResolver(), uri);

            return c;
        } else {
            throw new IllegalArgumentException("Invalid URI");
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        if (mUriMatcher.match(uri) == 1) {
            long newRowId = db.insert(StationsContract.StationEntry.TABLE_NAME, null, contentValues);
            if (newRowId > 0) {
                Uri insertUri = insertUri(newRowId);
                Log.i("uri", insertUri.getPath() + " - " + insertUri+"");
                getContext().getContentResolver().notifyChange(uri, null);
                return insertUri(newRowId);
            }
            else {
                return null;
            }
        } else {
            throw new IllegalArgumentException("Invalid URI");
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    private Uri insertUri(long id) {
        return ContentUris.withAppendedId(StationsContract.CONTENT_URI, id);
    }
}
