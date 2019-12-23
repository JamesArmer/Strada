package com.example.strada;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class StradaProvider extends ContentProvider {

    DBHelper dbHelper = null;

    private static final UriMatcher uriMatcher;

    static { //Uri matcher for the various switch statements
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(StradaProviderContract.AUTHORITY, "run", 1);
    }

    @Override
    public boolean onCreate() {
        Log.d("g53mdp", "contentprovider oncreate");
        this.dbHelper = new DBHelper(this.getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        if (uri.getLastPathSegment()==null)
        {
            return "vnd.android.cursor.dir/MyProvider.data.text";
        }
        else
        {
            return "vnd.android.cursor.item/MyProvider.data.text";
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName = "";
        switch(uriMatcher.match(uri)) { //select the correct table to insert into
            case 1:
                tableName = "run";
                break;
        }

        long id = db.insert(tableName, null, values);
        db.close();
        Uri nu = ContentUris.withAppendedId(uri, id);
        Log.d("g53mdp", nu.toString());
        getContext().getContentResolver().notifyChange(nu, null);
        return nu;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[]
            selectionArgs, String sortOrder) {

        Log.d("g53mdp", uri.toString() + " " + uriMatcher.match(uri));
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        switch(uriMatcher.match(uri)){
            case 1: //retrieve recipes
                return db.query("run", projection, selection, selectionArgs, null, null, sortOrder);
            default:
                return null;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[]
            selectionArgs) {
        int count;
        Log.d("g53mdp", uri.toString() + " " + uriMatcher.match(uri));
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        switch(uriMatcher.match(uri)){
            case 1:
                count = db.update("run", values, selection, selectionArgs);
                return count;
            default:
                return -1;
        }
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;
        Log.d("g53mdp", uri.toString() + " " + uriMatcher.match(uri));
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        switch(uriMatcher.match(uri)){ //select the correct table to delete from
            case 1:
                count = db.delete("run", selection, selectionArgs);
                return count;
            default:
                return -1;
        }
    }
}

