package com.example.strada;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

public class StradaObserver extends ContentObserver { //Observer class that allows all activities to access the content provider

    public StradaObserver(Handler handler) {
        super(handler);
    }
    @Override
    public void onChange(boolean selfChange) {
        this.onChange(selfChange, null);
    }
    @Override
    public void onChange(boolean selfChange, Uri uri) {
    }
}

