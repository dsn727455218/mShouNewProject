package com.shownew.home.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by WP on 2017/8/10.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String SQL_NAME = "shounew.db";
    private static final int VERSION = 1;
    public static final String TABLE_NAME = "shop";
    public static final String _id = "_Id";
    public static final String shPid = "shPid";
    public static final String shMpid = "shMpid";
    public static final String shTitle = "shTitle";
    public static final String shSimg = "shSimg";
    public static final String shColor = "shColor";
    public static final String shDate = "shDate";
    public static final String shPrive = "shPrive";
    public static final String shKdprice = "shKdprice";
    public static final String singlePrice = "singlePrice";
    public static final String shNum = "shNum";
    private static final String SQL = "create table " + TABLE_NAME + "(" + _id + " Integer primary key autoincrement,"
            + shPid + " varchar(20)," +
            shMpid + " varchar(20)," + shTitle + " text," + shSimg +
            " text," + shColor + " text," + shPrive + " Double," +
            shKdprice + " Double," + shNum + " Integer," + shDate + " text," + singlePrice + " Double)";

    public DatabaseHelper(Context context) {
        super(context, SQL_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("upgrade a database");
    }
}
