package com.example.strada;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context){
        super(context, "mydb", null, 1);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE run ("+
                "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "+
                "name VARCHAR(128), "+
                "date VARCHAR(128), "+
                "distance VARCHAR(128), "+
                "duration VARCHAR(128), "+
                "pace VARCHAR(128), "+
                "comments VARCHAR(128));");

        //insert an initial run into the database
        db.execSQL("INSERT INTO run (name, date, distance, duration, pace, comments) VALUES ('Morning Run', '2019-12-23', '4.95km', '26m 39s', '5:23 /km', 'Hard. Sunny.');");
        db.execSQL("INSERT INTO run (name, date, distance, duration, pace, comments) VALUES ('Morning Run', '2020-01-02', '5.40km', '32m 39s', '5:23 /km', 'Easy. Wet.');");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

}
