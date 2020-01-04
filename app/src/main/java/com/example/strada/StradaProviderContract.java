package com.example.strada;

import android.net.Uri;

public class StradaProviderContract {//public Uris ans Strings to abstract the contract provider
    public static final String AUTHORITY =
            "com.example.strada.StradaProvider";
    public static final Uri ALL_URI =
            Uri.parse("content://" + AUTHORITY + "/");
    public static final Uri RUN_URI =
            Uri.parse("content://" + AUTHORITY + "/run");
    public static final Uri DIST_URI =
            Uri.parse("content://" + AUTHORITY + "/dist");

    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String DATE = "date";
    public static final String DISTANCE = "distance";
    public static final String DURATION = "duration";
    public static final String PACE = "pace";
    public static final String COMMENTS = "comments";

    public static final String SUM = "SUM(distance)";

}
