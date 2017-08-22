package com.shownew.home.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shownew.home.module.dao.ShopCarEntity;

import java.util.ArrayList;

/**
 * Created by WP on 2017/8/10.
 */

public class DatabaseUtils {

    public static void insert(Context context, ShopCarEntity shopCarEntity) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.shColor, shopCarEntity.getShColor());
        contentValues.put(DatabaseHelper.shKdprice, shopCarEntity.getShKdprice());
        contentValues.put(DatabaseHelper.shMpid, shopCarEntity.getShMpid());
        contentValues.put(DatabaseHelper.shNum, shopCarEntity.getShNum());
        contentValues.put(DatabaseHelper.shSimg, shopCarEntity.getShSimg());
        contentValues.put(DatabaseHelper.shDate, shopCarEntity.getShDate());
        contentValues.put(DatabaseHelper.shTitle, shopCarEntity.getShTitle());
        contentValues.put(DatabaseHelper.shPid, shopCarEntity.getShPid());
        contentValues.put(DatabaseHelper.shPrive, shopCarEntity.getShPrice());
        contentValues.put(DatabaseHelper.singlePrice, shopCarEntity.getSinglePrice());
        sqLiteDatabase.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();
    }

    public static void delete(Context context, ShopCarEntity shopCarEntity) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        sqLiteDatabase.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._id + "=?", new String[]{String.valueOf(shopCarEntity.get_id())});
        sqLiteDatabase.close();
    }

    public static void deleteAll(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        sqLiteDatabase.delete(DatabaseHelper.TABLE_NAME, null, null);
        sqLiteDatabase.close();
    }

    public static void updateSelectData(Context context, ArrayList<ShopCarEntity> shopCarEntities) {
        if (shopCarEntities == null)
            return;
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        for (ShopCarEntity shopCarEntity : shopCarEntities) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.shNum, shopCarEntity.getShNum());
            contentValues.put(DatabaseHelper.shPrive, shopCarEntity.getShPrice());
            sqLiteDatabase.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._id + "=?", new String[]{String.valueOf(shopCarEntity.get_id())});
        }
        sqLiteDatabase.close();
    }

    public static void updateData(Context context, ShopCarEntity shopCarEntities) {
        if (shopCarEntities == null)
            return;
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.shNum, shopCarEntities.getShNum());
        contentValues.put(DatabaseHelper.shPrive, shopCarEntities.getShPrice());
        sqLiteDatabase.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._id + "=?", new String[]{String.valueOf(shopCarEntities.get_id())});
        sqLiteDatabase.close();
    }

    public static ArrayList<ShopCarEntity> queryAllData(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        ArrayList<ShopCarEntity> shopCarEntities = new ArrayList<ShopCarEntity>();
        if (cursor == null) return shopCarEntities;


        while (cursor.moveToNext()) {
            int shNum = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.shNum));
            String shColor = cursor.getString(cursor.getColumnIndex(DatabaseHelper.shColor));
            Integer id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._id));
            Integer shPid = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.shPid));
            double shPrive = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.shPrive));
            String shSimg = cursor.getString(cursor.getColumnIndex(DatabaseHelper.shSimg));
            String shTitle = cursor.getString(cursor.getColumnIndex(DatabaseHelper.shTitle));
            Integer shMpid = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.shMpid));
            String shDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.shDate));
            double shKdprice = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.shKdprice));
            double singlePrice = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.singlePrice));

            ShopCarEntity shopCarEntity = new ShopCarEntity();
            shopCarEntity.setShNum(shNum);
            shopCarEntity.setShTitle(shTitle);
            shopCarEntity.set_id(id);
            shopCarEntity.setShColor(shColor);
            shopCarEntity.setShDate(shDate);
            shopCarEntity.setShKdprice(shKdprice);
            shopCarEntity.setShPrice(shPrive);
            shopCarEntity.setShPid(shPid);
            shopCarEntity.setShSimg(shSimg);
            shopCarEntity.setSinglePrice(singlePrice);
            shopCarEntity.setShMpid(shMpid);

            shopCarEntities.add(shopCarEntity);
        }
        cursor.close();
        sqLiteDatabase.close();
        return shopCarEntities;
    }

}
